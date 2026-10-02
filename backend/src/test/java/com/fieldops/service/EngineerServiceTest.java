package com.fieldops.service;

import com.fieldops.dto.EngineerRequest;
import com.fieldops.dto.EngineerResponse;
import com.fieldops.entity.Engineer;
import com.fieldops.entity.User;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.repository.EngineerRepository;
import com.fieldops.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EngineerServiceTest {

    @Mock
    private EngineerRepository engineerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EngineerService engineerService;

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("John Engineer");
        user.setEmail("john@fieldops.com");
        user.setPasswordHash("hash");
        user.setRole("ENGINEER");
        user.setStatus("ACTIVE");
        return user;
    }

    private Engineer createEngineer() {
        User user = createUser();

        Engineer engineer = new Engineer();
        engineer.setId(10L);
        engineer.setUser(user);
        engineer.setSpecialization("Mechanical");
        engineer.setLocation("Kerala");
        engineer.setAvailability("AVAILABLE");

        return engineer;
    }

    private EngineerRequest createRequest() {
        EngineerRequest request = new EngineerRequest();
        request.setUserId(1L);
        request.setSpecialization("Mechanical");
        request.setLocation("Kerala");
        request.setAvailability("AVAILABLE");
        return request;
    }

    @Test
    void getAllEngineersReturnsEngineers() {
        Engineer engineer = createEngineer();

        when(engineerRepository.findAllWithUser())
                .thenReturn(List.of(engineer));

        List<EngineerResponse> result =
                engineerService.getAllEngineers();

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(1L, result.get(0).getUserId());
        assertEquals("John Engineer", result.get(0).getName());
        assertEquals("Mechanical", result.get(0).getSpecialization());
        assertEquals("Kerala", result.get(0).getLocation());
        assertEquals("AVAILABLE", result.get(0).getAvailability());

        verify(engineerRepository).findAllWithUser();
    }

    @Test
    void getAllEngineersReturnsEmptyListWhenNoEngineers() {
        when(engineerRepository.findAllWithUser())
                .thenReturn(List.of());

        List<EngineerResponse> result =
                engineerService.getAllEngineers();

        assertTrue(result.isEmpty());
        verify(engineerRepository).findAllWithUser();
    }

    @Test
    void getEngineerByIdReturnsEngineer() {
        Engineer engineer = createEngineer();

        when(engineerRepository.findByIdWithUser(10L))
                .thenReturn(Optional.of(engineer));

        EngineerResponse result =
                engineerService.getEngineerById(10L);

        assertEquals(10L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals("John Engineer", result.getName());
        assertEquals("john@fieldops.com", result.getEmail());
        assertEquals("Mechanical", result.getSpecialization());
        assertEquals("Kerala", result.getLocation());
        assertEquals("AVAILABLE", result.getAvailability());

        verify(engineerRepository).findByIdWithUser(10L);
    }

    @Test
    void getEngineerByIdThrowsExceptionWhenNotFound() {
        when(engineerRepository.findByIdWithUser(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> engineerService.getEngineerById(999L)
        );

        verify(engineerRepository).findByIdWithUser(999L);
    }

    @Test
    void createEngineerCreatesEngineer() {
        User user = createUser();
        Engineer savedEngineer = createEngineer();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(engineerRepository.save(any(Engineer.class)))
                .thenReturn(savedEngineer);

        when(engineerRepository.findByIdWithUser(10L))
                .thenReturn(Optional.of(savedEngineer));

        EngineerResponse result =
                engineerService.createEngineer(createRequest());

        assertEquals(10L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals("John Engineer", result.getName());
        assertEquals("Mechanical", result.getSpecialization());
        assertEquals("Kerala", result.getLocation());
        assertEquals("AVAILABLE", result.getAvailability());

        verify(userRepository).findById(1L);
        verify(engineerRepository).save(any(Engineer.class));
        verify(engineerRepository).findByIdWithUser(10L);
    }

    @Test
    void createEngineerThrowsExceptionWhenUserNotFound() {
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        EngineerRequest request = createRequest();
        request.setUserId(999L);

        assertThrows(
                ResourceNotFoundException.class,
                () -> engineerService.createEngineer(request)
        );

        verify(userRepository).findById(999L);
        verify(engineerRepository, never()).save(any());
    }

    @Test
    void createEngineerThrowsExceptionWhenSavedEngineerCannotBeReloaded() {
        User user = createUser();
        Engineer savedEngineer = createEngineer();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(engineerRepository.save(any(Engineer.class)))
                .thenReturn(savedEngineer);

        when(engineerRepository.findByIdWithUser(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> engineerService.createEngineer(createRequest())
        );

        verify(engineerRepository).save(any(Engineer.class));
        verify(engineerRepository).findByIdWithUser(10L);
    }

    @Test
    void updateEngineerUpdatesEngineer() {
        Engineer engineer = createEngineer();
        User user = createUser();

        when(engineerRepository.findByIdWithUser(10L))
                .thenReturn(Optional.of(engineer));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(engineerRepository.save(engineer))
                .thenReturn(engineer);

        when(engineerRepository.findByIdWithUser(10L))
                .thenReturn(Optional.of(engineer));

        EngineerRequest request = createRequest();
        request.setSpecialization("Electrical");
        request.setLocation("Tamil Nadu");
        request.setAvailability("BUSY");

        EngineerResponse result =
                engineerService.updateEngineer(10L, request);

        assertEquals("Electrical", engineer.getSpecialization());
        assertEquals("Tamil Nadu", engineer.getLocation());
        assertEquals("BUSY", engineer.getAvailability());
        assertEquals(10L, result.getId());

        verify(engineerRepository).save(engineer);
    }

    @Test
    void updateEngineerThrowsExceptionWhenEngineerNotFound() {
        when(engineerRepository.findByIdWithUser(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> engineerService.updateEngineer(
                        999L,
                        createRequest()
                )
        );

        verify(engineerRepository, never()).save(any());
    }

    @Test
    void updateEngineerThrowsExceptionWhenUserNotFound() {
        Engineer engineer = createEngineer();

        when(engineerRepository.findByIdWithUser(10L))
                .thenReturn(Optional.of(engineer));

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        EngineerRequest request = createRequest();
        request.setUserId(999L);

        assertThrows(
                ResourceNotFoundException.class,
                () -> engineerService.updateEngineer(10L, request)
        );

        verify(engineerRepository, never()).save(any());
    }

    @Test
    void getBySpecializationReturnsMatchingEngineers() {
        Engineer engineer = createEngineer();

        when(engineerRepository.findBySpecializationWithUser("Mechanical"))
                .thenReturn(List.of(engineer));

        List<EngineerResponse> result =
                engineerService.getBySpecialization("Mechanical");

        assertEquals(1, result.size());
        assertEquals("Mechanical", result.get(0).getSpecialization());

        verify(engineerRepository)
                .findBySpecializationWithUser("Mechanical");
    }

    @Test
    void getByLocationReturnsMatchingEngineers() {
        Engineer engineer = createEngineer();

        when(engineerRepository.findByLocationWithUser("Kerala"))
                .thenReturn(List.of(engineer));

        List<EngineerResponse> result =
                engineerService.getByLocation("Kerala");

        assertEquals(1, result.size());
        assertEquals("Kerala", result.get(0).getLocation());

        verify(engineerRepository)
                .findByLocationWithUser("Kerala");
    }

    @Test
    void getByAvailabilityReturnsMatchingEngineers() {
        Engineer engineer = createEngineer();

        when(engineerRepository.findByAvailabilityWithUser("AVAILABLE"))
                .thenReturn(List.of(engineer));

        List<EngineerResponse> result =
                engineerService.getByAvailability("AVAILABLE");

        assertEquals(1, result.size());
        assertEquals("AVAILABLE", result.get(0).getAvailability());

        verify(engineerRepository)
                .findByAvailabilityWithUser("AVAILABLE");
    }
}
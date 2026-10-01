package com.fieldops.service;

import com.fieldops.dto.EngineerRequest;
import com.fieldops.dto.EngineerResponse;
import com.fieldops.entity.Engineer;
import com.fieldops.entity.User;
import com.fieldops.exception.ResourceNotFoundException;
import com.fieldops.mapper.EngineerMapper;
import com.fieldops.repository.EngineerRepository;
import com.fieldops.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EngineerService {

    private final EngineerRepository engineerRepository;
    private final UserRepository userRepository;

    public EngineerService(
            EngineerRepository engineerRepository,
            UserRepository userRepository) {
        this.engineerRepository = engineerRepository;
        this.userRepository = userRepository;
    }

    public List<EngineerResponse> getAllEngineers() {
        return engineerRepository.findAllWithUser()
                .stream()
                .map(EngineerMapper::toResponse)
                .toList();
    }

    public EngineerResponse getEngineerById(Long id) {
        Engineer engineer = engineerRepository.findByIdWithUser(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Engineer not found with id: " + id));

        return EngineerMapper.toResponse(engineer);
    }

    public EngineerResponse createEngineer(EngineerRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + request.getUserId()));

        Engineer engineer = new Engineer();
        engineer.setUser(user);
        engineer.setSpecialization(request.getSpecialization());
        engineer.setLocation(request.getLocation());
        engineer.setAvailability(request.getAvailability());

        Engineer savedEngineer = engineerRepository.save(engineer);

        Engineer createdEngineer = engineerRepository.findByIdWithUser(savedEngineer.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Engineer not found with id: " + savedEngineer.getId()));

        return EngineerMapper.toResponse(createdEngineer);
    }

    public EngineerResponse updateEngineer(Long id, EngineerRequest request) {
        Engineer engineer = engineerRepository.findByIdWithUser(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Engineer not found with id: " + id));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + request.getUserId()));

        engineer.setUser(user);
        engineer.setSpecialization(request.getSpecialization());
        engineer.setLocation(request.getLocation());
        engineer.setAvailability(request.getAvailability());

        engineerRepository.save(engineer);

        Engineer updatedEngineer = engineerRepository.findByIdWithUser(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Engineer not found with id: " + id));

        return EngineerMapper.toResponse(updatedEngineer);
    }

    public List<EngineerResponse> getBySpecialization(String specialization) {
        return engineerRepository.findBySpecializationWithUser(specialization)
                .stream()
                .map(EngineerMapper::toResponse)
                .toList();
    }

    public List<EngineerResponse> getByLocation(String location) {
        return engineerRepository.findByLocationWithUser(location)
                .stream()
                .map(EngineerMapper::toResponse)
                .toList();
    }

    public List<EngineerResponse> getByAvailability(String availability) {
        return engineerRepository.findByAvailabilityWithUser(availability)
                .stream()
                .map(EngineerMapper::toResponse)
                .toList();
    }
}
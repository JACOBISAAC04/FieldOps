package com.fieldops.config;

import com.fieldops.entity.User;
import com.fieldops.repository.UserRepository;
import com.fieldops.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final FilterChain filterChain = mock(FilterChain.class);

    private final JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(jwtService, userRepository);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requestWithoutTokenContinuesChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userRepository);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    @Test
    void validTokenAuthenticatesUser() throws Exception {
        User user = createUser();

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("jacob@fieldops.com");

        when(userRepository.findByEmail("jacob@fieldops.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.isTokenValid("valid-token", user))
                .thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(user, authentication.getPrincipal());
        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ENGINEER"))
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void inactiveUserIsNotAuthenticated() throws Exception {
        User user = createUser();
        user.setStatus("INACTIVE");

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("jacob@fieldops.com");

        when(userRepository.findByEmail("jacob@fieldops.com"))
                .thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never())
                .isTokenValid("valid-token", user);
    }

    @Test
    void unknownUserIsNotAuthenticated() throws Exception {
        when(jwtService.extractEmail("valid-token"))
                .thenReturn("unknown@fieldops.com");

        when(userRepository.findByEmail("unknown@fieldops.com"))
                .thenReturn(Optional.empty());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void invalidTokenDoesNotAuthenticateUser() throws Exception {
        when(jwtService.extractEmail("invalid-token"))
                .thenThrow(new RuntimeException("Invalid token"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userRepository);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Jacob Isaac");
        user.setEmail("jacob@fieldops.com");
        user.setPasswordHash("hashed-password");
        user.setRole("ENGINEER");
        user.setStatus("ACTIVE");
        return user;
    }
}
package com.flix.flix.service.impl;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ERole;
import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Customer;
import com.flix.flix.model.request.LoginRequest;
import com.flix.flix.model.request.NewUserRequest;
import com.flix.flix.model.request.search.SearchAppUserRequest;
import com.flix.flix.model.response.AppUserResponse;
import com.flix.flix.model.response.SigninResponse;
import com.flix.flix.model.response.SignoutResponse;
import com.flix.flix.model.response.SignupResponse;
import com.flix.flix.repository.AppUserRepository;
import com.flix.flix.security.JwtAuthenticationFilter;
import com.flix.flix.security.JwtTokenProvider;
import com.flix.flix.specification.AppUserSpecification;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RedisTokenBlackListService redisTokenBlackListService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private HttpServletRequest httpServletRequest; // Mock for signout

    @InjectMocks
    private AppUserServiceImpl appUserService;

    private AppUser testUser;
    private NewUserRequest newUserRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = AppUser.builder()
                .id("test-user-id-123")
                .email("test@example.com")
                .username("testuser")
                .password("encodedPassword123")
                .roles(List.of(ERole.ROLE_ADMIN))
                .build();

        newUserRequest = NewUserRequest.builder()
                .email("newuser@example.com")
                .username("newuser")
                .password("rawPassword123")
                .role(List.of("ADMIN"))
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("rawPassword123")
                .build();
    }

    @Test
    void getAppUserById_shouldReturnAppUser_whenFound() {
        when(appUserRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        AppUser foundUser = appUserService.getAppUserById(testUser.getId());

        assertNotNull(foundUser);
        assertEquals(testUser.getId(), foundUser.getId());
        verify(appUserRepository, times(1)).findById(testUser.getId());
    }

    @Test
    void getAppUserById_shouldThrowRuntimeException_whenNotFound() {
        when(appUserRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                appUserService.getAppUserById("non-existent-id"));

        assertEquals(DbBash.USER_NOT_FOUND, thrown.getMessage());
        verify(appUserRepository, times(1)).findById(anyString());
    }

    @Test
    void signup_shouldReturnSignupResponse_whenSuccessful() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(appUserRepository.saveAndFlush(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId("new-generated-id"); // Simulate ID generation
            return user;
        });

        SignupResponse response = appUserService.signup(newUserRequest);

        assertNotNull(response);
        assertEquals("new-generated-id", response.getAccountId());
        assertEquals(newUserRequest.getEmail(), response.getEmail());
        assertEquals(List.of(ERole.findByDescription(newUserRequest.getRole().get(0))).toString(), response.getRole());

        verify(passwordEncoder, times(1)).encode(newUserRequest.getPassword());
        verify(appUserRepository, times(1)).saveAndFlush(any(AppUser.class));
    }

    @Test
    void signup_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doThrow(new RuntimeException("DB Error")).when(appUserRepository).saveAndFlush(any(AppUser.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                appUserService.signup(newUserRequest));

        assertTrue(thrown.getMessage().contains(ApiBash.SIGN_UP_FAILED));
        assertTrue(thrown.getMessage().contains("DB Error"));
        verify(passwordEncoder, times(1)).encode(newUserRequest.getPassword());
        verify(appUserRepository, times(1)).saveAndFlush(any(AppUser.class));
    }

    @Test
    void signup_shouldHandleInvalidRoleMapping_andThrowException() {
        NewUserRequest invalidRoleRequest = NewUserRequest.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password")
                .role(List.of("INVALID_ROLE")) // Role yang tidak ada di ERole
                .build();

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                appUserService.signup(invalidRoleRequest));

        assertTrue(thrown.getMessage().contains(ApiBash.SIGN_UP_FAILED));
        assertTrue(thrown.getMessage().contains("Invalid role description") ||
                    thrown.getMessage().contains("Role not found"));
        verify(appUserRepository, never()).saveAndFlush(any(AppUser.class));
    }


    @Test
    void getByEmail_shouldReturnAppUser_whenFound() {
        when(appUserRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        AppUser foundUser = appUserService.getByEmail(testUser.getEmail());

        assertNotNull(foundUser);
        assertEquals(testUser.getEmail(), foundUser.getEmail());
        verify(appUserRepository, times(1)).findByEmail(testUser.getEmail());
    }

    @Test
    void getByEmail_shouldThrowRuntimeException_whenNotFound() {
        when(appUserRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                appUserService.getByEmail("non-existent@example.com"));

        assertEquals("User not found", thrown.getMessage());
        verify(appUserRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void signin_shouldReturnSigninResponse_whenSuccessful() {
        Authentication authentication = mock(Authentication.class);
        User springSecurityUser = new User(testUser.getEmail(), testUser.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(ERole.ROLE_ADMIN.name())));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(springSecurityUser);
        when(jwtTokenProvider.generateToken(testUser.getEmail(), springSecurityUser.getAuthorities().toString()))
                .thenReturn("generatedJwtToken");
        when(appUserRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));


        SigninResponse response = appUserService.signin(loginRequest);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getAccountId());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals("generatedJwtToken", response.getToken());
        assertEquals(springSecurityUser.getAuthorities().toString(), response.getRole());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(testUser.getEmail(), springSecurityUser.getAuthorities().toString());
        verify(appUserRepository, times(1)).findByEmail(testUser.getEmail());
    }

    @Test
    void signin_shouldThrowRuntimeException_whenAuthenticationFails() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                appUserService.signin(loginRequest));

        assertTrue(thrown.getMessage().contains(ApiBash.SIGN_IN_FAILED));
        assertTrue(thrown.getMessage().contains("Invalid credentials"));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtTokenProvider); // Should not generate token
        verifyNoInteractions(appUserRepository); // Should not try to find user by email
    }

    @Test
    void signout_shouldReturnSignoutResponse_whenSuccessful() {
        String token = "valid.jwt.token";
        Long expirationTime = 3600000L; // 1 hour in ms

        when(jwtAuthenticationFilter.extractTokenFromRequest(httpServletRequest)).thenReturn(token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getExpirationTime(token)).thenReturn(expirationTime);
        doNothing().when(redisTokenBlackListService).blackListToken(token, expirationTime);

        SignoutResponse response = appUserService.signout(httpServletRequest);

        assertNotNull(response);
        assertEquals("Logout successful", response.getStatusMessage());
        assertEquals(token, response.getAccessToken());

        verify(jwtAuthenticationFilter, times(1)).extractTokenFromRequest(httpServletRequest);
        verify(jwtTokenProvider, times(1)).validateToken(token);
        verify(jwtTokenProvider, times(1)).getExpirationTime(token);
        verify(redisTokenBlackListService, times(1)).blackListToken(token, expirationTime);
    }

    @Test
    void signout_shouldThrowRuntimeException_whenTokenIsNull() {
        when(jwtAuthenticationFilter.extractTokenFromRequest(httpServletRequest)).thenReturn(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                appUserService.signout(httpServletRequest));

        assertEquals(ApiBash.SIGN_OUT_FAILED + ": Token is null", thrown.getMessage());

        verify(jwtAuthenticationFilter, times(1)).extractTokenFromRequest(httpServletRequest);
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(redisTokenBlackListService);
    }

    @Test
    void signout_shouldThrowRuntimeException_whenTokenIsInvalid() {
        String invalidToken = "invalid.jwt.token";
        when(jwtAuthenticationFilter.extractTokenFromRequest(httpServletRequest)).thenReturn(invalidToken);
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false); // Token is invalid

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                appUserService.signout(httpServletRequest));

        assertEquals(ApiBash.SIGN_OUT_FAILED + ": Token is null", thrown.getMessage());

        verify(jwtAuthenticationFilter, times(1)).extractTokenFromRequest(httpServletRequest);
        verify(jwtTokenProvider, times(1)).validateToken(invalidToken);
        verify(jwtTokenProvider, never()).getExpirationTime(anyString());
        verifyNoInteractions(redisTokenBlackListService);
    }


    @Test
    void signout_shouldThrowRuntimeException_whenTokenProviderThrowsException() {
        String token = "valid.jwt.token";
        when(jwtAuthenticationFilter.extractTokenFromRequest(httpServletRequest)).thenReturn(token);
        doThrow(new RuntimeException("Token processing error")).when(jwtTokenProvider).validateToken(token);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                appUserService.signout(httpServletRequest));

        assertTrue(thrown.getMessage().contains(ApiBash.SIGN_OUT_FAILED));
        assertTrue(thrown.getMessage().contains("Token processing error"));
        verify(jwtAuthenticationFilter, times(1)).extractTokenFromRequest(httpServletRequest);
        verify(jwtTokenProvider, times(1)).validateToken(token);
        verify(jwtTokenProvider, never()).getExpirationTime(anyString());
        verifyNoInteractions(redisTokenBlackListService);
    }

    @Test
    void getAll_shouldReturnPageOfAppUserResponses_withDefaultPagingAndSorting() {
        SearchAppUserRequest searchRequest = SearchAppUserRequest.builder()
                .page(0) // Will be converted to 1
                .size(0) // Will be converted to 10
                .sortBy("username")
                .direction("asc")
                .build();

        AppUser user1 = AppUser.builder().id("id1").username("user1").email("u1@ex.com").roles(List.of(ERole.ROLE_ADMIN)).build();
        AppUser user2 = AppUser.builder().id("id2").username("user2").email("u2@ex.com").roles(List.of(ERole.ROLE_ADMIN)).build();
        List<AppUser> appUserList = Arrays.asList(user1, user2);
        Page<AppUser> appUserPage = new PageImpl<>(appUserList, PageRequest.of(0, 10, Sort.by("username").ascending()), 2);

        try (MockedStatic<AppUserSpecification> mockedStatic = mockStatic(AppUserSpecification.class)) {
            mockedStatic.when(() -> AppUserSpecification.getSpecification(searchRequest)).thenReturn(mock(Specification.class));
            when(appUserRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(appUserPage);

            Page<AppUserResponse> result = appUserService.getAll(searchRequest);

            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            assertEquals("user1", result.getContent().get(0).getUsername());
            assertEquals("user2", result.getContent().get(1).getUsername());
            
            verify(appUserRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedStatic.verify(() -> AppUserSpecification.getSpecification(searchRequest), times(1));
        }
    }

    @Test
    void getAll_shouldReturnPageOfAppUserResponses_withProvidedPagingAndSorting() {
        SearchAppUserRequest searchRequest = SearchAppUserRequest.builder()
                .page(2)
                .size(5)
                .sortBy("email")
                .direction("desc")
                .build();

        AppUser user1 = AppUser.builder().id("id1").username("user1").email("u1@ex.com").roles(List.of(ERole.ROLE_ADMIN)).build();
        AppUser user2 = AppUser.builder().id("id2").username("user2").email("u2@ex.com").roles(List.of(ERole.ROLE_ADMIN)).build();
        List<AppUser> appUserList = Arrays.asList(user1, user2);
        Page<AppUser> appUserPage = new PageImpl<>(appUserList, PageRequest.of(1, 5, Sort.by("email").descending()), 12);

        try (MockedStatic<AppUserSpecification> mockedStatic = mockStatic(AppUserSpecification.class)) {
            mockedStatic.when(() -> AppUserSpecification.getSpecification(searchRequest)).thenReturn(mock(Specification.class));
            when(appUserRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(appUserPage);

            Page<AppUserResponse> result = appUserService.getAll(searchRequest);

            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(12, result.getTotalElements());
            
            verify(appUserRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedStatic.verify(() -> AppUserSpecification.getSpecification(searchRequest), times(1));
        }
    }

    @Test
    void getAll_shouldHandleNullCustomerInAppUserResponse() {
        SearchAppUserRequest searchRequest = SearchAppUserRequest.builder()
                .page(1)
                .size(10)
                .sortBy("username")
                .direction("asc")
                .build();

        AppUser userWithNullCustomer = AppUser.builder()
                .id("id3")
                .username("user3")
                .email("u3@ex.com")
                .roles(List.of(ERole.ROLE_ADMIN))
                .customer(null) // Customer is null
                .build();
        List<AppUser> appUserList = List.of(userWithNullCustomer);
        Page<AppUser> appUserPage = new PageImpl<>(appUserList);

        try (MockedStatic<AppUserSpecification> mockedStatic = mockStatic(AppUserSpecification.class)) {
            mockedStatic.when(() -> AppUserSpecification.getSpecification(searchRequest)).thenReturn(mock(Specification.class));
            when(appUserRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(appUserPage);

            Page<AppUserResponse> result = appUserService.getAll(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("", result.getContent().get(0).getCustomerFullname()); // Should be empty string
            verify(appUserRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedStatic.verify(() -> AppUserSpecification.getSpecification(searchRequest), times(1));
        }
    }

    @Test
    void getAll_shouldHandleNonNullCustomerInAppUserResponse() {
        SearchAppUserRequest searchRequest = SearchAppUserRequest.builder()
                .page(1)
                .size(10)
                .sortBy("username")
                .direction("asc")
                .build();

        Customer testCustomer = Customer.builder().id("cust1").fullname("Test Customer").build();
        AppUser userWithCustomer = AppUser.builder()
                .id("id4")
                .username("user4")
                .email("u4@ex.com")
                .roles(List.of(ERole.ROLE_ADMIN))
                .customer(testCustomer) // Customer is not null
                .build();
        List<AppUser> appUserList = List.of(userWithCustomer);
        Page<AppUser> appUserPage = new PageImpl<>(appUserList);

        try (MockedStatic<AppUserSpecification> mockedStatic = mockStatic(AppUserSpecification.class)) {
            mockedStatic.when(() -> AppUserSpecification.getSpecification(searchRequest)).thenReturn(mock(Specification.class));
            when(appUserRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(appUserPage);

            Page<AppUserResponse> result = appUserService.getAll(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Test Customer", result.getContent().get(0).getCustomerFullname()); // Should be fullname
            verify(appUserRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedStatic.verify(() -> AppUserSpecification.getSpecification(searchRequest), times(1));
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        SearchAppUserRequest searchRequest = SearchAppUserRequest.builder()
                .page(1)
                .size(10)
                .sortBy("username")
                .direction("asc")
                .build();

        try (MockedStatic<AppUserSpecification> mockedStatic = mockStatic(AppUserSpecification.class)) {
            mockedStatic.when(() -> AppUserSpecification.getSpecification(searchRequest)).thenReturn(mock(Specification.class));
            doThrow(new RuntimeException("DB access error")).when(appUserRepository).findAll(any(Specification.class), any(Pageable.class));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    appUserService.getAll(searchRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.GET_ALL_USER_FAILED));
            assertTrue(thrown.getMessage().contains("DB access error"));
            verify(appUserRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedStatic.verify(() -> AppUserSpecification.getSpecification(searchRequest), times(1));
        }
    }
}
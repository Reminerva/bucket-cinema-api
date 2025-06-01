package com.flix.flix.service.impl;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EGender;
import com.flix.flix.constant.custom_enum.ERole;
import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Employee;
import com.flix.flix.entity.Theater;
import com.flix.flix.entity.Transaction;
import com.flix.flix.model.request.NewAdminRequest;
import com.flix.flix.model.request.NewCashierRequest;
import com.flix.flix.model.request.NewEmployeeRequest;
import com.flix.flix.model.request.NewUserRequest;
import com.flix.flix.model.request.UpdateEmployeeRequest;
import com.flix.flix.model.request.search.SearchEmployeeRequest;
import com.flix.flix.model.response.EmployeeResponse;
import com.flix.flix.model.response.SignupResponse;
import com.flix.flix.repository.EmployeeRepository;
import com.flix.flix.service.AppUserService;
import com.flix.flix.service.TheaterService;
import com.flix.flix.specification.EmployeeSpecification;
import com.flix.flix.util.DateUtil;
import com.flix.flix.util.TokenUtil;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private TheaterService theaterService;
    @Mock
    private AppUserService appUserService;
    @Mock
    private TokenUtil tokenUtil;
    @Mock
    private HttpServletRequest httpServletRequest; // Untuk test getByCredentials, updateByCredentials

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee testEmployee;
    private AppUser testAppUser;
    private Theater testTheater;
    private NewEmployeeRequest newEmployeeRequest;
    private NewAdminRequest newAdminRequest;
    private NewCashierRequest newCashierRequest;
    private UpdateEmployeeRequest updateEmployeeRequest;
    private SignupResponse signupResponse;

    @BeforeEach
    void setUp() {
        testAppUser = AppUser.builder()
                .id("app-user-id-123")
                .email("employee@example.com")
                .username("employeetest")
                .password("encodedpassword")
                .roles(List.of(ERole.ROLE_EMPLOYEE))
                .build();

        testTheater = Theater.builder()
                .id("theater-id-abc")
                .name("Cineplex A")
                .build();

        testEmployee = Employee.builder()
                .id("emp-id-456")
                .fullname("Employee Test")
                .nikNumber("1234567890123456")
                .address("Jl. Raya No. 1")
                .phoneNumber("081234567890")
                .gender(EGender.GENDER_MALE)
                .city("Jakarta")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .dateOfAppliment(LocalDate.of(2020, 1, 1))
                .theater(testTheater)
                .isActive(true)
                .appUser(testAppUser)
                .transactions(Collections.singletonList(Transaction.builder().id("trx1").build())) // Sample transaction
                .build();

        testAppUser.setEmployee(testEmployee); // Link AppUser to Employee

        newEmployeeRequest = NewEmployeeRequest.builder()
                .email("newemp@example.com")
                .password("newempPass")
                .username("newemployee")
                .fullname("New Employee")
                .nikNumber("0987654321098765")
                .address("Jl. Baru No. 2")
                .phoneNumber("089876543210")
                .gender("MALE")
                .city("Surabaya")
                .dateOfBirth("1995-03-15")
                .dateOfAppliment("2021-07-20")
                .theaterId("theater-id-xyz")
                .build();

        newAdminRequest = NewAdminRequest.builder()
                .email("newadmin@example.com")
                .password("adminPass")
                .username("newadmin")
                .build();

        newCashierRequest = NewCashierRequest.builder()
                .email("newcashier@example.com")
                .password("cashierPass")
                .username("newcashier")
                .theaterId("theater-id-cashier")
                .build();

        updateEmployeeRequest = UpdateEmployeeRequest.builder()
                .fullname("Updated Employee Name")
                .nikNumber("1122334455667788")
                .address("Jl. Update No. 3")
                .phoneNumber("087654321098")
                .gender("FEMALE")
                .city("Bandung")
                .dateOfBirth("1988-11-25")
                .dateOfAppliment("2019-04-01")
                .theaterId("theater-id-updated")
                .build();

        signupResponse = SignupResponse.builder()
                .accountId("new-app-user-id")
                .email("newuser@example.com")
                .role(List.of("EMPLOYEE").toString())
                .build();
    }

    @Test
    void create_shouldReturnEmployeeResponse_whenSuccessful() {
        AppUser newAppUserForCreate = AppUser.builder()
                .id("new-app-user-id")
                .email(newEmployeeRequest.getEmail())
                .username(newEmployeeRequest.getUsername())
                .roles(List.of(ERole.ROLE_EMPLOYEE))
                .build();
        Theater newTheaterForCreate = Theater.builder().id(newEmployeeRequest.getTheaterId()).name("New Theater").build();

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<EGender> mockedEGender = mockStatic(EGender.class)) {

            when(appUserService.signup(any(NewUserRequest.class))).thenReturn(signupResponse);
            when(appUserService.getAppUserById(signupResponse.getAccountId())).thenReturn(newAppUserForCreate);
            when(theaterService.getTheaterById(newEmployeeRequest.getTheaterId())).thenReturn(newTheaterForCreate);

            mockedDateUtil.when(() -> DateUtil.parseDate(newEmployeeRequest.getDateOfBirth()))
                    .thenReturn(LocalDate.of(1995, 3, 15));
            mockedDateUtil.when(() -> DateUtil.parseDate(newEmployeeRequest.getDateOfAppliment()))
                    .thenReturn(LocalDate.of(2021, 7, 20));
            mockedEGender.when(() -> EGender.findByDescription(newEmployeeRequest.getGender()))
                    .thenReturn(EGender.GENDER_MALE);

            when(employeeRepository.saveAndFlush(any(Employee.class))).thenAnswer(invocation -> {
                Employee employee = invocation.getArgument(0);
                employee.setId("generated-employee-id");
                return employee;
            });

            EmployeeResponse response = employeeService.create(newEmployeeRequest);

            assertNotNull(response);
            assertEquals("generated-employee-id", response.getId());
            assertEquals(newEmployeeRequest.getFullname(), response.getFullname());
            assertEquals(newEmployeeRequest.getTheaterId(), response.getTheaterId());
            assertEquals(newEmployeeRequest.getEmail(), response.getAppUserEmail());
            assertEquals(newEmployeeRequest.getUsername(), response.getAppUserUsername());
            assertEquals("GENDER_MALE", response.getGender()); // Assert string form of enum
            assertEquals("1995-03-15", response.getDateOfBirth());
            assertEquals("2021-07-20", response.getDateOfAppliment());
            assertTrue(response.getIsActive());

            verify(appUserService, times(1)).signup(any(NewUserRequest.class));
            verify(appUserService, times(1)).getAppUserById(signupResponse.getAccountId());
            verify(theaterService, times(1)).getTheaterById(newEmployeeRequest.getTheaterId());
            verify(employeeRepository, times(1)).saveAndFlush(any(Employee.class));
            mockedDateUtil.verify(() -> DateUtil.parseDate(newEmployeeRequest.getDateOfBirth()), times(1));
            mockedDateUtil.verify(() -> DateUtil.parseDate(newEmployeeRequest.getDateOfAppliment()), times(1));
            mockedEGender.verify(() -> EGender.findByDescription(newEmployeeRequest.getGender()), times(1));
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenAppUserSignupFails() {
        when(appUserService.signup(any(NewUserRequest.class))).thenThrow(new RuntimeException("Signup failed"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.create(newEmployeeRequest));

        assertTrue(thrown.getMessage().contains(ApiBash.CREATE_EMPLOYEE_FAILED));
        assertTrue(thrown.getMessage().contains("Signup failed"));
        verifyNoInteractions(employeeRepository, theaterService); // No further calls
    }

    @Test
    void create_shouldThrowRuntimeException_whenInvalidGender() {
        newEmployeeRequest.setGender("INVALID_GENDER");
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<EGender> mockedEGender = mockStatic(EGender.class)) {
            when(appUserService.signup(any(NewUserRequest.class))).thenReturn(signupResponse);
            when(appUserService.getAppUserById(signupResponse.getAccountId())).thenReturn(testAppUser);

            mockedDateUtil.when(() -> DateUtil.parseDate(anyString())).thenReturn(LocalDate.now());
            mockedEGender.when(() -> EGender.findByDescription(newEmployeeRequest.getGender()))
                    .thenThrow(new RuntimeException("Gender not found"));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    employeeService.create(newEmployeeRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.CREATE_EMPLOYEE_FAILED));
            assertTrue(thrown.getMessage().contains("Gender not found"));
            verifyNoInteractions(employeeRepository, theaterService); // No further calls
        }
    }

    @Test
    void createAdmin_shouldReturnSignupResponse_whenSuccessful() {
        SignupResponse adminSignupResponse = SignupResponse.builder()
                .accountId("admin-account-id")
                .email(newAdminRequest.getEmail())
                .role(List.of("ADMIN").toString())
                .build();
        when(appUserService.signup(any(NewUserRequest.class))).thenReturn(adminSignupResponse);

        SignupResponse response = employeeService.createAdmin(newAdminRequest);

        assertNotNull(response);
        assertEquals(adminSignupResponse.getAccountId(), response.getAccountId());
        assertEquals(adminSignupResponse.getEmail(), response.getEmail());
        assertEquals(adminSignupResponse.getRole(), response.getRole());
        verify(appUserService, times(1)).signup(any(NewUserRequest.class));
        verifyNoInteractions(employeeRepository, theaterService); // Admin creation doesn't create employee entity
    }

    @Test
    void createAdmin_shouldThrowRuntimeException_whenSignupFails() {
        when(appUserService.signup(any(NewUserRequest.class))).thenThrow(new RuntimeException("Admin signup error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.createAdmin(newAdminRequest));

        assertTrue(thrown.getMessage().contains(ApiBash.CREATE_EMPLOYEE_FAILED)); // Note: Uses CREATE_EMPLOYEE_FAILED
        assertTrue(thrown.getMessage().contains("Admin signup error"));
        verify(appUserService, times(1)).signup(any(NewUserRequest.class));
    }


    @Test
    void createCashier_shouldReturnEmployeeResponse_whenSuccessful() {
        SignupResponse cashierSignupResponse = SignupResponse.builder()
                .accountId("cashier-account-id")
                .email(newCashierRequest.getEmail())
                .role(List.of("CASHIER").toString())
                .build();
        AppUser newAppUserForCashier = AppUser.builder()
                .id("cashier-account-id")
                .email(newCashierRequest.getEmail())
                .username(newCashierRequest.getUsername())
                .roles(List.of(ERole.ROLE_CASHIER))
                .build();
        Theater cashierTheater = Theater.builder().id(newCashierRequest.getTheaterId()).name("Cashier Theater").build();

        when(appUserService.signup(any(NewUserRequest.class))).thenReturn(cashierSignupResponse);
        when(appUserService.getAppUserById(cashierSignupResponse.getAccountId())).thenReturn(newAppUserForCashier);
        when(theaterService.getTheaterById(newCashierRequest.getTheaterId())).thenReturn(cashierTheater);

        when(employeeRepository.saveAndFlush(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId("generated-cashier-employee-id");
            return employee;
        });

        EmployeeResponse response = employeeService.createCashier(newCashierRequest);

        assertNotNull(response);
        assertEquals("generated-cashier-employee-id", response.getId());
        assertNull(response.getFullname()); // Cashier's employee fields are null
        assertEquals(newCashierRequest.getTheaterId(), response.getTheaterId());
        assertEquals(newCashierRequest.getEmail(), response.getAppUserEmail());
        assertEquals(newCashierRequest.getUsername(), response.getAppUserUsername());
        assertTrue(response.getIsActive());

        verify(appUserService, times(1)).signup(any(NewUserRequest.class));
        verify(appUserService, times(1)).getAppUserById(cashierSignupResponse.getAccountId());
        verify(theaterService, times(1)).getTheaterById(newCashierRequest.getTheaterId());
        verify(employeeRepository, times(1)).saveAndFlush(any(Employee.class));
    }

    @Test
    void createCashier_shouldThrowRuntimeException_whenTheaterNotFound() {
        SignupResponse cashierSignupResponse = SignupResponse.builder()
                .accountId("cashier-account-id")
                .email(newCashierRequest.getEmail())
                .role(List.of("CASHIER").toString())
                .build();
        AppUser newAppUserForCashier = AppUser.builder()
                .id("cashier-account-id")
                .email(newCashierRequest.getEmail())
                .username(newCashierRequest.getUsername())
                .roles(List.of(ERole.ROLE_CASHIER))
                .build();

        when(appUserService.signup(any(NewUserRequest.class))).thenReturn(cashierSignupResponse);
        when(appUserService.getAppUserById(cashierSignupResponse.getAccountId())).thenReturn(newAppUserForCashier);
        when(theaterService.getTheaterById(newCashierRequest.getTheaterId()))
                .thenThrow(new RuntimeException("Theater not found")); // Simulate theater not found

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.createCashier(newCashierRequest));

        assertTrue(thrown.getMessage().contains(ApiBash.CREATE_EMPLOYEE_FAILED));
        assertTrue(thrown.getMessage().contains("Theater not found"));
        verify(appUserService, times(1)).signup(any(NewUserRequest.class));
        verify(appUserService, times(1)).getAppUserById(cashierSignupResponse.getAccountId());
        verify(theaterService, times(1)).getTheaterById(newCashierRequest.getTheaterId());
        verifyNoInteractions(employeeRepository);
    }


    @Test
    void getEmployeeById_shouldReturnEmployee_whenFound() {
        when(employeeRepository.findById(testEmployee.getId())).thenReturn(Optional.of(testEmployee));

        Employee foundEmployee = employeeService.getEmployeeById(testEmployee.getId());

        assertNotNull(foundEmployee);
        assertEquals(testEmployee.getId(), foundEmployee.getId());
        verify(employeeRepository, times(1)).findById(testEmployee.getId());
    }

    @Test
    void getEmployeeById_shouldThrowRuntimeException_whenNotFound() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.getEmployeeById("non-existent-id"));

        assertEquals(DbBash.EMPLOYEE_NOT_FOUND, thrown.getMessage());
        verify(employeeRepository, times(1)).findById(anyString());
    }

    @Test
    void getById_shouldReturnEmployeeResponse_whenFound() {
        try (MockedStatic<EGender> mockedEGender = mockStatic(EGender.class)) {
            mockedEGender.when(() -> EGender.valueOf(anyString())).thenReturn(EGender.GENDER_MALE); // Or whatever conversion logic is needed
            when(employeeRepository.findById(testEmployee.getId())).thenReturn(Optional.of(testEmployee));

            EmployeeResponse response = employeeService.getById(testEmployee.getId());

            assertNotNull(response);
            assertEquals(testEmployee.getId(), response.getId());
            assertEquals(testEmployee.getFullname(), response.getFullname());
            assertEquals(testAppUser.getEmail(), response.getAppUserEmail());
            assertEquals(testTheater.getId(), response.getTheaterId());
            assertFalse(response.getTransactionsId().isEmpty());
            assertEquals("trx1", response.getTransactionsId().get(0));

            verify(employeeRepository, times(1)).findById(testEmployee.getId());
        }
    }

    @Test
    void getById_shouldThrowRuntimeException_whenEmployeeNotFound() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.getById("non-existent-id"));

        assertEquals(ApiBash.GET_EMPLOYEE_FAILED + ": " + DbBash.EMPLOYEE_NOT_FOUND, thrown.getMessage());
        verify(employeeRepository, times(1)).findById(anyString());
    }

    @Test
    void getAll_shouldReturnPageOfEmployeeResponses_withDefaultPagingAndSorting() {
        SearchEmployeeRequest searchRequest = SearchEmployeeRequest.builder()
                .page(0) // Will be converted to 1
                .size(0) // Will be converted to 10
                .sortBy("fullname")
                .direction("asc")
                .dateOfApplimentMax("2020-01-01")
                .dateOfApplimentMin("2019-01-01")
                .dateOfBirthMin("1970-01-01")
                .dateOfBirthMax("1980-01-01")
                .build();

        List<Employee> employees = Collections.singletonList(testEmployee);
        Page<Employee> employeePage = new PageImpl<>(employees, PageRequest.of(0, 10, Sort.by("fullname").ascending()), 1);

        try (MockedStatic<EmployeeSpecification> mockedStaticSpec = mockStatic(EmployeeSpecification.class);
                MockedStatic<EGender> mockedEGender = mockStatic(EGender.class)) { // Mock EGender for toEmployeeResponse

            mockedStaticSpec.when(() -> EmployeeSpecification.getSpecification(searchRequest)).thenReturn(mock(Specification.class));
            mockedEGender.when(() -> EGender.valueOf(anyString())).thenReturn(EGender.GENDER_MALE); // Example if EGender.toString() is used and converted back

            when(employeeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(employeePage);

            Page<EmployeeResponse> result = employeeService.getAll(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testEmployee.getFullname(), result.getContent().get(0).getFullname());
            
            verify(employeeRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedStaticSpec.verify(() -> EmployeeSpecification.getSpecification(searchRequest), times(1));
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenDateOfApplimentMinAfterMax() {
        SearchEmployeeRequest searchRequest = SearchEmployeeRequest.builder()
                .dateOfApplimentMin("2020-01-01")
                .dateOfApplimentMax("2019-01-01") // Min after Max
                .page(0) // Will be converted to 1
                .size(0) // Will be converted to 10
                .sortBy("username")
                .direction("asc")
                .build();

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(searchRequest.getDateOfApplimentMin())).thenReturn(LocalDate.of(2020, 1, 1));
            mockedDateUtil.when(() -> DateUtil.parseDate(searchRequest.getDateOfApplimentMax())).thenReturn(LocalDate.of(2019, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    employeeService.getAll(searchRequest));

            assertEquals(ApiBash.GET_ALL_EMPLOYEE_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
            verifyNoInteractions(employeeRepository);
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenDateOfBirthMinAfterMax() {
        SearchEmployeeRequest searchRequest = SearchEmployeeRequest.builder()
                .dateOfBirthMin("1990-01-01")
                .dateOfBirthMax("1980-01-01") // Min after Max
                .page(0) // Will be converted to 1
                .size(0) // Will be converted to 10
                .sortBy("username")
                .direction("asc")
                .build();

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(searchRequest.getDateOfBirthMin())).thenReturn(LocalDate.of(1990, 1, 1));
            mockedDateUtil.when(() -> DateUtil.parseDate(searchRequest.getDateOfBirthMax())).thenReturn(LocalDate.of(1980, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    employeeService.getAll(searchRequest));

            assertEquals(ApiBash.GET_ALL_EMPLOYEE_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
            verifyNoInteractions(employeeRepository);
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        SearchEmployeeRequest searchRequest = SearchEmployeeRequest.builder().page(1).size(10).sortBy("fullname").direction("asc").build();

        try (MockedStatic<EmployeeSpecification> mockedStaticSpec = mockStatic(EmployeeSpecification.class);
                MockedStatic<EGender> mockedEGender = mockStatic(EGender.class)) { // Mock EGender for toEmployeeResponse
            mockedStaticSpec.when(() -> EmployeeSpecification.getSpecification(searchRequest)).thenReturn(mock(Specification.class));
            mockedEGender.when(() -> EGender.valueOf(anyString())).thenReturn(EGender.GENDER_MALE); // Dummy mock if toEmployeeResponse is called

            doThrow(new RuntimeException("DB access error")).when(employeeRepository).findAll(any(Specification.class), any(Pageable.class));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    employeeService.getAll(searchRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.GET_ALL_EMPLOYEE_FAILED));
            assertTrue(thrown.getMessage().contains("DB access error"));
            verify(employeeRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedStaticSpec.verify(() -> EmployeeSpecification.getSpecification(searchRequest), times(1));
        }
    }

    @Test
    void update_shouldReturnEmployeeResponse_whenSuccessful() {
        Theater updatedTheater = Theater.builder().id(updateEmployeeRequest.getTheaterId()).name("Updated Theater").build();
        Employee existingEmployee = Employee.builder()
                .id(testEmployee.getId())
                .fullname("Old Name")
                .appUser(testAppUser)
                .build(); // Simplified existing employee

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<EGender> mockedEGender = mockStatic(EGender.class)) {

            when(employeeRepository.findById(testEmployee.getId())).thenReturn(Optional.of(existingEmployee));
            when(theaterService.getTheaterById(updateEmployeeRequest.getTheaterId())).thenReturn(updatedTheater);

            mockedDateUtil.when(() -> DateUtil.parseDate(updateEmployeeRequest.getDateOfBirth()))
                    .thenReturn(LocalDate.of(1988, 11, 25));
            mockedDateUtil.when(() -> DateUtil.parseDate(updateEmployeeRequest.getDateOfAppliment()))
                    .thenReturn(LocalDate.of(2019, 4, 1));
            mockedEGender.when(() -> EGender.findByDescription(updateEmployeeRequest.getGender()))
                    .thenReturn(EGender.GENDER_FEMALE);
            mockedEGender.when(() -> EGender.valueOf(anyString())).thenReturn(EGender.GENDER_FEMALE); // For toEmployeeResponse

            when(employeeRepository.saveAndFlush(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

            EmployeeResponse response = employeeService.update(testEmployee.getId(), updateEmployeeRequest);

            assertNotNull(response);
            assertEquals(testEmployee.getId(), response.getId());
            assertEquals(updateEmployeeRequest.getFullname(), response.getFullname());
            assertEquals(updateEmployeeRequest.getTheaterId(), response.getTheaterId());
            assertEquals("GENDER_FEMALE", response.getGender());

            verify(employeeRepository, times(1)).findById(testEmployee.getId());
            verify(theaterService, times(1)).getTheaterById(updateEmployeeRequest.getTheaterId());
            verify(employeeRepository, times(1)).saveAndFlush(any(Employee.class));
            mockedDateUtil.verify(() -> DateUtil.parseDate(updateEmployeeRequest.getDateOfBirth()), times(1));
            mockedDateUtil.verify(() -> DateUtil.parseDate(updateEmployeeRequest.getDateOfAppliment()), times(1));
            mockedEGender.verify(() -> EGender.findByDescription(updateEmployeeRequest.getGender()), times(1));
        }
    }

    @Test
    void update_shouldThrowRuntimeException_whenEmployeeNotFound() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.update("non-existent-id", updateEmployeeRequest));

        assertEquals(ApiBash.UPDATE_EMPLOYEE_FAILED + ": " + DbBash.EMPLOYEE_NOT_FOUND, thrown.getMessage());
        verify(employeeRepository, times(1)).findById(anyString());
        verify(employeeRepository, never()).saveAndFlush(any(Employee.class));
    }


    @Test
    void getByCredentials_shouldReturnEmployeeResponse_whenSuccessful() {
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenReturn(testAppUser);

        try (MockedStatic<EGender> mockedEGender = mockStatic(EGender.class)) {
            mockedEGender.when(() -> EGender.valueOf(anyString())).thenReturn(EGender.GENDER_MALE); // For toEmployeeResponse

            EmployeeResponse response = employeeService.getByCredentials(httpServletRequest);

            assertNotNull(response);
            assertEquals(testEmployee.getId(), response.getId());
            assertEquals(testEmployee.getFullname(), response.getFullname());
            verify(tokenUtil, times(1)).getAppUserByToken(httpServletRequest);
        }
    }

    @Test
    void getByCredentials_shouldThrowRuntimeException_whenAppUserNotFoundViaToken() {
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenThrow(new RuntimeException("Invalid token or user not found"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.getByCredentials(httpServletRequest));

        assertTrue(thrown.getMessage().contains(ApiBash.GET_EMPLOYEE_FAILED));
        assertTrue(thrown.getMessage().contains("Invalid token or user not found"));
        verify(tokenUtil, times(1)).getAppUserByToken(httpServletRequest);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void updateByCredentials_shouldReturnEmployeeResponse_whenSuccessfulAndAuthorized() {
        AppUser authorizedAppUser = AppUser.builder()
                .id("app-user-id-123")
                .email("employee@example.com")
                .username("employeetest")
                .roles(List.of(ERole.ROLE_EMPLOYEE))
                .employee(testEmployee)
                .build();

        Theater updatedTheater = Theater.builder().id(updateEmployeeRequest.getTheaterId()).name("Updated Theater").build();

        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenReturn(authorizedAppUser);
        when(employeeRepository.findById(testEmployee.getId())).thenReturn(Optional.of(testEmployee)); // For internal update call
        when(theaterService.getTheaterById(updateEmployeeRequest.getTheaterId())).thenReturn(updatedTheater); // For internal update call

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<EGender> mockedEGender = mockStatic(EGender.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(anyString())).thenReturn(LocalDate.now());
            mockedEGender.when(() -> EGender.findByDescription(anyString())).thenReturn(EGender.GENDER_FEMALE);
            mockedEGender.when(() -> EGender.valueOf(anyString())).thenReturn(EGender.GENDER_FEMALE); // For toEmployeeResponse

            when(employeeRepository.saveAndFlush(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

            EmployeeResponse response = employeeService.updateByCredentials(updateEmployeeRequest, httpServletRequest);

            assertNotNull(response);
            assertEquals(testEmployee.getId(), response.getId());
            assertEquals(updateEmployeeRequest.getFullname(), response.getFullname());

            verify(tokenUtil, times(1)).getAppUserByToken(httpServletRequest);
            verify(employeeRepository, times(1)).findById(testEmployee.getId()); // verify the internal update call
            verify(employeeRepository, times(1)).saveAndFlush(any(Employee.class));
        }
    }

    @Test
    void updateByCredentials_shouldThrowRuntimeException_whenUnauthorizedRole() {
        AppUser unauthorizedAppUser = AppUser.builder()
                .id("unauth-id")
                .roles(List.of(ERole.ROLE_ADMIN)) // Not EMPLOYEE
                .employee(testEmployee)
                .build();
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenReturn(unauthorizedAppUser);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.updateByCredentials(updateEmployeeRequest, httpServletRequest));

        assertEquals(DbBash.UNAUTHORIZED, thrown.getMessage());
        verify(tokenUtil, times(1)).getAppUserByToken(httpServletRequest);
        verifyNoInteractions(employeeRepository, theaterService);
    }

    @Test
    void updateByCredentials_shouldThrowRuntimeException_whenAppUserHasNoEmployee() {
        AppUser appUserNoEmployee = AppUser.builder()
                .id("unauth-id")
                .roles(List.of(ERole.ROLE_EMPLOYEE))
                .employee(null) // No employee linked
                .build();
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenReturn(appUserNoEmployee);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.updateByCredentials(updateEmployeeRequest, httpServletRequest));

        assertTrue(thrown.getMessage().contains("Cannot invoke \"com.flix.flix.entity.Employee.getId()\" because the return value of \"com.flix.flix.entity.AppUser.getEmployee()\" is null"));
        verify(tokenUtil, times(1)).getAppUserByToken(httpServletRequest);
        verifyNoInteractions(employeeRepository, theaterService);
    }

    @Test
    void softDelete_shouldSetIsActiveToFalse_whenFound() {
        Employee activeEmployee = Employee.builder()
                .id(testEmployee.getId())
                .fullname("Active Employee")
                .isActive(true)
                .build();
        when(employeeRepository.findById(testEmployee.getId())).thenReturn(Optional.of(activeEmployee));
        when(employeeRepository.saveAndFlush(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> employeeService.softDelete(testEmployee.getId()));

        assertFalse(activeEmployee.getIsActive()); // Verify that the object itself is updated
        verify(employeeRepository, times(1)).findById(testEmployee.getId());
        verify(employeeRepository, times(1)).saveAndFlush(activeEmployee);
    }

    @Test
    void softDelete_shouldThrowRuntimeException_whenNotFound() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.softDelete("non-existent-id"));

        assertEquals(ApiBash.DELETE_EMPLOYEE_FAILED + ": " + DbBash.EMPLOYEE_NOT_FOUND, thrown.getMessage());
        verify(employeeRepository, times(1)).findById(anyString());
        verify(employeeRepository, never()).saveAndFlush(any(Employee.class));
    }

    @Test
    void softDelete_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        Employee activeEmployee = Employee.builder()
                .id(testEmployee.getId())
                .fullname("Active Employee")
                .isActive(true)
                .build();
        when(employeeRepository.findById(testEmployee.getId())).thenReturn(Optional.of(activeEmployee));
        doThrow(new RuntimeException("DB save error")).when(employeeRepository).saveAndFlush(any(Employee.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                employeeService.softDelete(testEmployee.getId()));

        assertTrue(thrown.getMessage().contains(ApiBash.DELETE_EMPLOYEE_FAILED));
        assertTrue(thrown.getMessage().contains("DB save error"));
        verify(employeeRepository, times(1)).findById(testEmployee.getId());
        verify(employeeRepository, times(1)).saveAndFlush(any(Employee.class));
    }

    @Test
    void toEmployeeResponse_shouldHandleNullFields() {
        Employee employeeWithNulls = Employee.builder()
                .id("null-emp-id")
                .fullname("Null Field Employee")
                .nikNumber(null)
                .address(null)
                .phoneNumber(null)
                .gender(null)
                .city(null)
                .dateOfBirth(null)
                .dateOfAppliment(null)
                .theater(null)
                .isActive(false)
                .appUser(null)
                .transactions(null)
                .build();

        when(employeeRepository.findById(employeeWithNulls.getId())).thenReturn(Optional.of(employeeWithNulls));
        EmployeeResponse response = employeeService.getById(employeeWithNulls.getId());

        assertNotNull(response);
        assertEquals("Null Field Employee", response.getFullname());
        assertNull(response.getNikNumber());
        assertNull(response.getAddress());
        assertNull(response.getPhoneNumber());
        assertNull(response.getGender());
        assertNull(response.getCity());
        assertNull(response.getDateOfBirth());
        assertNull(response.getDateOfAppliment());
        assertNull(response.getTheaterId());
        assertNull(response.getAppUserId());
        assertNull(response.getAppUserEmail());
        assertNull(response.getAppUserUsername());
        assertNull(response.getTransactionsId()); // Changed from empty list to null based on code
        assertFalse(response.getIsActive());
    }

    @Test
    void toEmployeeResponse_shouldHandleNonNullTransactions() {
        Transaction trx1 = Transaction.builder().id("trx-001").build();
        Transaction trx2 = Transaction.builder().id("trx-002").build();
        Employee employeeWithTransactions = Employee.builder()
                .id("emp-trx")
                .fullname("Employee With Transactions")
                .transactions(List.of(trx1, trx2))
                .build();
        employeeWithTransactions.setAppUser(testAppUser); // Set minimal required for toEmployeeResponse

        when(employeeRepository.findById(employeeWithTransactions.getId())).thenReturn(Optional.of(employeeWithTransactions));
        EmployeeResponse response = employeeService.getById(employeeWithTransactions.getId());

        assertNotNull(response);
        assertNotNull(response.getTransactionsId());
        assertEquals(2, response.getTransactionsId().size());
        assertTrue(response.getTransactionsId().contains("trx-001"));
        assertTrue(response.getTransactionsId().contains("trx-002"));
    }
}
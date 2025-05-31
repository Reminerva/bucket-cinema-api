package com.flix.flix.specification;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import com.flix.flix.constant.custom_enum.EGender;
import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Employee;
import com.flix.flix.entity.Theater;
import com.flix.flix.model.request.search.SearchEmployeeRequest;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecificationTest {

    @Mock
    private Root<Employee> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;
    @Mock
    private Path<String> fullnamePath;
    @Mock
    private Path<String> nikNumberPath;
    @Mock
    private Path<String> addressPath;
    @Mock
    private Path<String> phoneNumberPath;
    @Mock
    private Path<EGender> genderPath;
    @Mock
    private Path<String> cityPath;
    @Mock
    private Path<Boolean> isActivePath;
    @Mock
    private Path<LocalDate> dateOfBirthPath;
    @Mock
    private Path<LocalDate> dateOfApplimentPath;
    @Mock
    private Join<Employee, AppUser> appUserJoin;
    @Mock
    private Join<Employee, Theater> theaterJoin;

    @Mock
    private Predicate predicate;

    private SearchEmployeeRequest searchEmployeeRequest = new SearchEmployeeRequest();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        searchEmployeeRequest.setFullname("John Doe");
        searchEmployeeRequest.setNikNumber("123456789");
        searchEmployeeRequest.setAddress("123 Main St");
        searchEmployeeRequest.setPhoneNumber("1234567890");
        searchEmployeeRequest.setGender("MALE");
        searchEmployeeRequest.setCity("New York");
        searchEmployeeRequest.setIsActive(true);
        searchEmployeeRequest.setDateOfBirthMin("1990-01-01");
        searchEmployeeRequest.setDateOfBirthMax("2000-01-01");
        searchEmployeeRequest.setDateOfApplimentMin("2020-01-01");
        searchEmployeeRequest.setDateOfApplimentMax("2022-01-01");
        searchEmployeeRequest.setAppUserUsername("johndoe");
        searchEmployeeRequest.setAppUserEmail("johndoe@com");
        searchEmployeeRequest.setTheaterName("Theater A");
        searchEmployeeRequest.setTheaterCity("New York");
    }

    @Test
    void testGetSpecification_notNull() {

        when(root.<String>get("fullname")).thenReturn(fullnamePath);
        when(root.<String>get("nikNumber")).thenReturn(nikNumberPath);
        when(root.<String>get("address")).thenReturn(addressPath);
        when(root.<String>get("phoneNumber")).thenReturn(phoneNumberPath);
        when(root.<EGender>get("gender")).thenReturn(genderPath);
        when(root.<String>get("city")).thenReturn(cityPath);
        when(root.<Boolean>get("isActive")).thenReturn(isActivePath);
        when(root.<LocalDate>get("dateOfBirth")).thenReturn(dateOfBirthPath);
        when(root.<LocalDate>get("dateOfAppliment")).thenReturn(dateOfApplimentPath);
        when(root.<Employee, AppUser>join("appUser")).thenReturn(appUserJoin);
        when(root.<Employee, Theater>join("theater")).thenReturn(theaterJoin);

        when(cb.like(cb.lower(fullnamePath), "%john%")).thenReturn(predicate);
        when(cb.like(nikNumberPath, "123456")).thenReturn(predicate);
        when(cb.like(cb.lower(addressPath), "%123%")).thenReturn(predicate);
        when(cb.like(phoneNumberPath, "123")).thenReturn(predicate);
        when(cb.equal(genderPath, "MALE")).thenReturn(predicate);
        when(cb.like(cb.lower(cityPath), "%new")).thenReturn(predicate);
        when(cb.equal(isActivePath, true)).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(dateOfBirthPath, LocalDate.parse("1990-01-01"))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(dateOfBirthPath, LocalDate.parse("2000-01-01"))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(dateOfApplimentPath, LocalDate.parse("2020-01-01"))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(dateOfApplimentPath, LocalDate.parse("2022-01-01"))).thenReturn(predicate);

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<Employee> specification = EmployeeSpecification.getSpecification(searchEmployeeRequest);
        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb, times(0)).or(any(Predicate.class), any(Predicate.class));
        verify(cb, times(1)).and(any(Predicate[].class));
    }

    @Test
    void testGetSpecification_allNull() {
        SearchEmployeeRequest searchEmployeeRequest = new SearchEmployeeRequest();
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        Specification<Employee> specification = EmployeeSpecification.getSpecification(searchEmployeeRequest);
        Predicate result = specification.toPredicate(root, query, cb);
        assertNotNull(result);
        verify(cb).and(any(Predicate[].class));
        verify(cb, never()).or(any(Predicate.class), any(Predicate.class));
    }

}
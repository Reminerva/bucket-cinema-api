package com.flix.flix.specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import com.flix.flix.constant.custom_enum.EGender;
import com.flix.flix.entity.AppUser;
import com.flix.flix.model.request.search.SearchAppUserRequest;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class AppUserSpecificationTest {

    @Mock
    private Root<AppUser> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;
    @Mock
    private Path<String> usernamePath;
    @Mock
    private Path<String> emailPath;
    @Mock
    private Path<String> customerFullnamePath;
    @Mock
    private Path<EGender> genderPath;
    @Mock
    private Path<LocalDate> dateOfBirthPath;
    @Mock
    private Path<LocalDate> dateOfApplimentPath;
    @Mock
    private Path<String> rolePath;
    @Mock
    private Join<AppUser, Object> roleJoin;

    @Mock
    private Predicate predicate;

    private SearchAppUserRequest searchAppUserRequest = new SearchAppUserRequest();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        searchAppUserRequest.setCustomerFullname("customerFullname");
        searchAppUserRequest.setEmail("email");
        searchAppUserRequest.setRole(List.of("Admin"));
        searchAppUserRequest.setUsername("username");

    }

    @Test
    void testGetSpecification_notNull() {

        when(root.<String>get("username")).thenReturn(usernamePath);
        when(root.<String>get("email")).thenReturn(emailPath);
        when(root.<String>get("customer")).thenReturn(customerFullnamePath);
        when(root.<EGender>get("gender")).thenReturn(genderPath);
        when(root.<AppUser, Object>join("roles")).thenReturn(roleJoin);

        when(cb.equal(usernamePath, "username")).thenReturn(predicate);
        when(cb.equal(emailPath, "email")).thenReturn(predicate);
        when(cb.equal(customerFullnamePath, "customerFullname")).thenReturn(predicate);
        when(cb.equal(genderPath, EGender.GENDER_MALE)).thenReturn(predicate);
        when(cb.equal(rolePath, "Admin")).thenReturn(predicate);

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<AppUser> specification = AppUserSpecification.getSpecification(searchAppUserRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
        verify(cb, times(2)).and(any(Predicate[].class));
        verify(cb, never()).or(any(Predicate.class), any(Predicate.class));
    }

    @Test
    void testGetSpecification_allNull() {
        SearchAppUserRequest searchAppUserRequest = new SearchAppUserRequest();

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<AppUser> specification = AppUserSpecification.getSpecification(searchAppUserRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
    }
}

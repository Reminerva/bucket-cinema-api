package com.flix.flix.specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import com.flix.flix.constant.custom_enum.EGender;
import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Customer;
import com.flix.flix.entity.FavGenre;
import com.flix.flix.entity.Product;
import com.flix.flix.model.request.search.SearchCustomerRequest;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecificationTest {

    @Mock
    private Root<Customer> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<Object> favGenrePath;

    @Mock
    private Path<AppUser> appUserPath;
    
    @Mock
    private Join<Customer, FavGenre> favGenreJoin;
    
    @Mock
    private Join<Customer, Product> likeProductJoin;
    
    @Mock
    private Join<Customer, Product> dislikeProductJoin;

    @Mock
    private Predicate predicate;

    private SearchCustomerRequest searchCustomerRequest = new SearchCustomerRequest();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        searchCustomerRequest.setBirthDateMax("2022-01-01");
        searchCustomerRequest.setBirthDateMin("2000-01-01");
        searchCustomerRequest.setEmail("email");
        searchCustomerRequest.setGender("MALE");
        searchCustomerRequest.setLastLoginMax("2022-01-01");
        searchCustomerRequest.setLastLoginMin("2000-01-01");
        searchCustomerRequest.setRegistrationDateMax("2022-01-01");
        searchCustomerRequest.setRegistrationDateMin("2000-01-01");
        searchCustomerRequest.setCity("city");
        searchCustomerRequest.setCountry("country");
        searchCustomerRequest.setFullname("fullname");
        searchCustomerRequest.setPhoneNumber("phoneNumber");
        searchCustomerRequest.setFavGenres(List.of("action", "horror"));
        searchCustomerRequest.setLikeProductTitle(List.of("title1", "title2"));
        searchCustomerRequest.setDislikeProductTitle(List.of("title1", "title2"));
        searchCustomerRequest.setPhoneNumber("phoneNumber");

    }

    @Test
    void testGetSpecification() {

        when(root.<AppUser>get("appUser")).thenReturn(appUserPath);
        when(root.<Customer, FavGenre>join("favGenre")).thenReturn(favGenreJoin);
        when(favGenreJoin.get("favGenre")).thenReturn(favGenrePath);
        when(root.<Customer, Product>join("likeProduct")).thenReturn(likeProductJoin);
        when(root.<Customer, Product>join("dislikeProduct")).thenReturn(dislikeProductJoin);

        when(cb.equal(root.get("birthDate"), LocalDate.parse("2022-01-01"))).thenReturn(predicate);
        when(cb.equal(root.get("email"), "email")).thenReturn(predicate);
        when(cb.equal(root.get("gender"), EGender.GENDER_MALE)).thenReturn(predicate);
        when(cb.equal(root.get("lastLogin"), LocalDate.parse("2022-01-01"))).thenReturn(predicate);
        when(cb.equal(root.get("registrationDate"), LocalDate.parse("2022-01-01"))).thenReturn(predicate);
        when(cb.equal(root.get("city"), "city")).thenReturn(predicate);
        when(cb.equal(root.get("country"), "country")).thenReturn(predicate);
        when(cb.equal(root.get("fullname"), "fullname")).thenReturn(predicate);
        when(cb.equal(root.get("phoneNumber"), "phoneNumber")).thenReturn(predicate);
        when(favGenreJoin.get("favGenre").in(List.of("action", "horror"))).thenReturn(predicate);
        when(cb.like(cb.lower(likeProductJoin.get("title")), "%title%")).thenReturn(predicate);
        when(cb.like(cb.lower(dislikeProductJoin.get("title")), "%title%")).thenReturn(predicate);

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<Customer> specification = CustomerSpecification.getSpecification(searchCustomerRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
        verify(cb, times(2)).and(any(Predicate[].class));
        verify(cb, never()).or(any(Predicate.class), any(Predicate.class));
    }

    @Test
    void testGetSpecification_allNull() {
        SearchCustomerRequest searchCustomerRequest = new SearchCustomerRequest();
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        Specification<Customer> specification = CustomerSpecification.getSpecification(searchCustomerRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
    }
}

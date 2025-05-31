package com.flix.flix.specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.flix.flix.entity.Employee;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.Theater;
import com.flix.flix.model.request.search.SearchTheaterRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class TheaterSpecificationTest {

    @Mock
    private Root<Theater> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Join<Theater, Employee> employeeJoin;

    @Mock
    private Path<Object> employeeNamePath;

    @Mock
    private Join<Theater, Product> productJoin;

    @Mock
    private Path<Object> productTitlePath;

    @Mock
    private Predicate predicate;

    private SearchTheaterRequest searchTheaterRequest = new SearchTheaterRequest();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        searchTheaterRequest.setOprationalStatus(true);
        searchTheaterRequest.setEmployeesName(List.of("John Doe"));
        searchTheaterRequest.setProductsTitle(List.of("Product 1"));
        searchTheaterRequest.setAddress("address");
        searchTheaterRequest.setCity("city");
        searchTheaterRequest.setContactEmail("email");
        searchTheaterRequest.setContactNumber("number");
        searchTheaterRequest.setCreatedAtMax("2022-01-01");
        searchTheaterRequest.setCreatedAtMin("2022-01-01");
        searchTheaterRequest.setUpdatedAtMax("2022-01-01");
        searchTheaterRequest.setUpdatedAtMin("2022-01-01");
        searchTheaterRequest.setName("name");
    }

    @Test
    void testGetSpecification() {

        when(root.<Theater, Employee>join("employees")).thenReturn(employeeJoin);
        when(employeeJoin.get("name")).thenReturn(employeeNamePath);
        when(root.<Theater, Product>join("products")).thenReturn(productJoin);
        when(productJoin.get("title")).thenReturn(productTitlePath);

        when(cb.equal(root.get("isOperational"), true)).thenReturn(predicate);
        when(cb.equal(employeeNamePath, "John Doe")).thenReturn(predicate);
        when(cb.equal(productTitlePath, "Product 1")).thenReturn(predicate);
        when(cb.like(cb.lower(root.get("address")), "%" + searchTheaterRequest.getAddress().toLowerCase() + "%")).thenReturn(predicate);
        when(cb.like(cb.lower(root.get("city")), "%" + searchTheaterRequest.getCity().toLowerCase() + "%")).thenReturn(predicate);
        when(cb.like(cb.lower(root.get("contactEmail")), "%" + searchTheaterRequest.getContactEmail().toLowerCase() + "%")).thenReturn(predicate);
        when(cb.like(root.get("contactNumber"), "%" + searchTheaterRequest.getContactNumber()+ "%")).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("createdAt"), DateUtil.parseDate(searchTheaterRequest.getCreatedAtMin()))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("createdAt"), DateUtil.parseDate(searchTheaterRequest.getCreatedAtMax()))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("updatedAt"), DateUtil.parseDate(searchTheaterRequest.getUpdatedAtMin()))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("updatedAt"), DateUtil.parseDate(searchTheaterRequest.getUpdatedAtMax()))).thenReturn(predicate);
        when(cb.like(cb.lower(root.get("name")), "%" + searchTheaterRequest.getName().toLowerCase() + "%")).thenReturn(predicate);

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<Theater> specification = TheaterSpecification.getSpecification(searchTheaterRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
    }

    @Test
    void testGetSpecification_allNull() {
        SearchTheaterRequest searchTheaterRequest = new SearchTheaterRequest();
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        Specification<Theater> specification = TheaterSpecification.getSpecification(searchTheaterRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
    }
}

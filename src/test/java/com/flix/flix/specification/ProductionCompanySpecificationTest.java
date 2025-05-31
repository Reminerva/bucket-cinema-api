package com.flix.flix.specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import com.flix.flix.entity.ProductionCompany;
import com.flix.flix.entity.Product;
import com.flix.flix.model.request.search.SearchProductionCompanyRequest;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class ProductionCompanySpecificationTest {

    @Mock
    private Root<ProductionCompany> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Join<ProductionCompany, Product> productJoin;

    @Mock
    private Predicate predicate;

    private SearchProductionCompanyRequest searchProductionCompanyRequest = new SearchProductionCompanyRequest();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        searchProductionCompanyRequest.setName("Test Production Company");
        searchProductionCompanyRequest.setOriginCountry("Japan");
        searchProductionCompanyRequest.setFoundedYearMin("2022-01-01");
        searchProductionCompanyRequest.setFoundedYearMax("2023-01-01");
        searchProductionCompanyRequest.setCeo("Test CEO");
        searchProductionCompanyRequest.setCreatedAtMin("2022-01-01");
        searchProductionCompanyRequest.setCreatedAtMax("2023-01-01");
        searchProductionCompanyRequest.setUpdatedAtMin("2022-01-01");
        searchProductionCompanyRequest.setUpdatedAtMax("2023-01-01");
        searchProductionCompanyRequest.setHeadquarters("Test Headquarters");
        searchProductionCompanyRequest.setHasProducts(List.of("product1", "product2"));

    }

    @Test
    void getSpecification_notNull() {

        when(root.<ProductionCompany, Product>join("hasProduct")).thenReturn(productJoin);

        when(cb.equal(root.get("name"), "Test Production Company")).thenReturn(predicate);
        when(cb.equal(root.get("originCountry"), "Japan")).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("foundedYear"), LocalDate.parse("2022-01-01"))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("foundedYear"), LocalDate.parse("2023-01-01"))).thenReturn(predicate);
        when(cb.equal(root.get("ceo"), "Test CEO")).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDate.parse("2022-01-01"))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDate.parse("2023-01-01"))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("updatedAt"), LocalDate.parse("2022-01-01"))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("updatedAt"), LocalDate.parse("2023-01-01"))).thenReturn(predicate);
        when(cb.like(cb.lower(root.get("headquarters")), "%test headquarters%")).thenReturn(predicate);

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<ProductionCompany> specification = ProductionCompanySpecification.getSpecification(searchProductionCompanyRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
    }

    @Test
    void getSpecification_allNull() {
        SearchProductionCompanyRequest searchProductionCompanyRequest = new SearchProductionCompanyRequest();
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        Specification<ProductionCompany> specification = ProductionCompanySpecification.getSpecification(searchProductionCompanyRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
    }

}

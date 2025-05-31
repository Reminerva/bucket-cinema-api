package com.flix.flix.specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.flix.flix.entity.ProductPricing;
import com.flix.flix.entity.Studio;
import com.flix.flix.model.request.search.SearchStudioRequest;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class StudioSpecificationTest {

    @Mock
    private Root<Studio> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<Object> theaterPath;

    @Mock
    private Join<Studio, ProductPricing> productPricingJoin;

    @Mock
    private Path<Object> productPricingPath;

    @Mock
    private Predicate predicate;

    private SearchStudioRequest searchStudioRequest = new SearchStudioRequest();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        searchStudioRequest.setName("Test Studio");
        searchStudioRequest.setSeatLayout(List.of("A1", "A2"));
        searchStudioRequest.setIsActive(true);
        searchStudioRequest.setProductId("productId");
        searchStudioRequest.setTheaterId("theaterId");
        searchStudioRequest.setStudioSize("Reguler Small");
        searchStudioRequest.setTheaterCity("theaterCity");
        searchStudioRequest.setTheaterName("theaterName");

    }

    @Test
    void testGetSpecification() {

        when(root.get("theater")).thenReturn(theaterPath);
        when(root.<Studio, ProductPricing>join("productPricing")).thenReturn(productPricingJoin);
        when(productPricingJoin.get("productIdPricing")).thenReturn(productPricingPath);

        when(cb.equal(root.get("name"), "Test Studio")).thenReturn(predicate);
        when(cb.equal(root.get("seatLayout"), "A1")).thenReturn(predicate);
        when(cb.equal(root.get("isActive"), true)).thenReturn(predicate);
        when(cb.equal(root.get("productId"), "productId")).thenReturn(predicate);
        when(cb.equal(root.get("theater").get("id"), "theaterId")).thenReturn(predicate);
        when(cb.equal(root.get("studioSize"), "Reguler Small")).thenReturn(predicate);
        when(cb.equal(root.get("theater").get("city"), "theaterCity")).thenReturn(predicate);
        when(cb.equal(root.get("theater").get("name"), "theaterName")).thenReturn(predicate);

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<Studio> specification = StudioSpecification.getSpecification(searchStudioRequest);
        specification.toPredicate(root, query, cb);
    }

    @Test
    void testGetSpecification_allNull() {
        SearchStudioRequest searchStudioRequest = new SearchStudioRequest();
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        Specification<Studio> specification = StudioSpecification.getSpecification(searchStudioRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
    }
}

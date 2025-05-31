package com.flix.flix.specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.flix.flix.entity.Transaction;
import com.flix.flix.model.request.search.SearchTransactionRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecificationTest {

    @Mock
    private Root<Transaction> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<Object> customerPath;

    @Mock
    private Path<Object> employeePath;

    @Mock
    private Path<Object> productPath;

    @Mock
    private Path<Object> theaterPath;

    @Mock
    private Path<Object> studioPath;

    @Mock
    private Path<Object> productPricingPath;

    @Mock
    private Path<Object> productSchedulingPath;

    @Mock
    private Predicate predicate;

    private SearchTransactionRequest searchTransactionRequest = new SearchTransactionRequest();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        searchTransactionRequest.setCreatedAtMax("2022-01-01");
        searchTransactionRequest.setCreatedAtMin("2022-01-01");
        searchTransactionRequest.setUpdatedAtMax("2022-01-01");
        searchTransactionRequest.setUpdatedAtMin("2022-01-01");
        searchTransactionRequest.setPaymentDateTimeMax("2022-01-01 00:00:00");
        searchTransactionRequest.setPaymentDateTimeMin("2022-01-01 00:00:00");
        searchTransactionRequest.setWatchDateMax("2022-01-01");
        searchTransactionRequest.setWatchDateMin("2022-01-01");
        searchTransactionRequest.setPaymentMethod("cash");
        searchTransactionRequest.setPaymentStatus("pending");
        searchTransactionRequest.setTransactionDateTimeMax("2022-01-01 00:00:00");
        searchTransactionRequest.setTransactionDateTimeMin("2022-01-01 00:00:00");
        searchTransactionRequest.setSeats(List.of("A1"));
        searchTransactionRequest.setProductPriceMin(1.0);
        searchTransactionRequest.setProductPriceMax(1.0);
        searchTransactionRequest.setProductSchedule("10:00");
        searchTransactionRequest.setCustomerId("1");
        searchTransactionRequest.setCustomerName("cname");
        searchTransactionRequest.setEmployeeId("1");
        searchTransactionRequest.setEmployeeName("ename");
        searchTransactionRequest.setExpirationDateMax("2022-01-01");
        searchTransactionRequest.setExpirationDateMin("2022-01-01");
        searchTransactionRequest.setProductTitle("title");
        searchTransactionRequest.setQtyMin(1);
        searchTransactionRequest.setQtyMax(1);
        searchTransactionRequest.setStudioName("studio");
        searchTransactionRequest.setTax(10);
        searchTransactionRequest.setTheaterName("theater");
    }

    @Test
    void testGetSpecification() {

        when(root.get("customer")).thenReturn(customerPath);
        when(root.get("employee")).thenReturn(employeePath);
        when(root.get("product")).thenReturn(productPath);
        when(root.get("theater")).thenReturn(theaterPath);
        when(root.get("studio")).thenReturn(studioPath);
        when(root.get("productPricing")).thenReturn(productPricingPath);
        when(root.get("productScheduling")).thenReturn(productSchedulingPath);

        when(cb.equal(root.get("paymentMethod"), "cash")).thenReturn(predicate);
        when(cb.equal(root.get("paymentStatus"), "pending")).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("transactionDateTime"), DateUtil.parseDateTime(searchTransactionRequest.getTransactionDateTimeMin()))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("transactionDateTime"), DateUtil.parseDateTime(searchTransactionRequest.getTransactionDateTimeMax()))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("paymentDateTime"), DateUtil.parseDateTime(searchTransactionRequest.getPaymentDateTimeMin()))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("paymentDateTime"), DateUtil.parseDateTime(searchTransactionRequest.getPaymentDateTimeMax()))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("createdAt"), DateUtil.parseDate(searchTransactionRequest.getCreatedAtMin()))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("createdAt"), DateUtil.parseDate(searchTransactionRequest.getCreatedAtMax()))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("updatedAt"), DateUtil.parseDate(searchTransactionRequest.getUpdatedAtMin()))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("updatedAt"), DateUtil.parseDate(searchTransactionRequest.getUpdatedAtMax()))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("watchDate"), DateUtil.parseDate(searchTransactionRequest.getWatchDateMin()))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("watchDate"), DateUtil.parseDate(searchTransactionRequest.getWatchDateMax()))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("expirationDate"), DateUtil.parseDate(searchTransactionRequest.getExpirationDateMin()))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("expirationDate"), DateUtil.parseDate(searchTransactionRequest.getExpirationDateMax()))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("qty"), searchTransactionRequest.getQtyMin())).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("qty"), searchTransactionRequest.getQtyMax())).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("tax"), searchTransactionRequest.getTax())).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("tax"), searchTransactionRequest.getTax())).thenReturn(predicate);

        when(cb.greaterThanOrEqualTo(root.get("productPricing").get("weekdayPrice"), searchTransactionRequest.getProductPriceMin())).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("productPricing").get("weekdayPrice"), searchTransactionRequest.getProductPriceMax())).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("productPricing").get("weekendPrice"), searchTransactionRequest.getProductPriceMin())).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("productPricing").get("weekendPrice"), searchTransactionRequest.getProductPriceMax())).thenReturn(predicate);
        when(cb.equal(root.get("productScheduling").get("schedule"), searchTransactionRequest.getProductSchedule())).thenReturn(predicate);
        when(cb.equal(root.get("customer").get("id"), searchTransactionRequest.getCustomerId())).thenReturn(predicate);
        when(cb.equal(root.get("customer").get("fullname"), searchTransactionRequest.getCustomerName())).thenReturn(predicate);
        when(cb.equal(root.get("employee").get("id"), searchTransactionRequest.getEmployeeId())).thenReturn(predicate);
        when(cb.like(root.get("employee").get("fullname"), searchTransactionRequest.getEmployeeName())).thenReturn(predicate);
        when(cb.equal(root.get("studio").get("name"), searchTransactionRequest.getStudioName())).thenReturn(predicate);
        when(cb.equal(root.get("product").get("title"), searchTransactionRequest.getProductTitle())).thenReturn(predicate);
        when(cb.like(cb.lower(root.get("theater").get("name")), searchTransactionRequest.getTheaterName())).thenReturn(predicate);

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<Transaction> specification = TransactionSpecification.getSpecification(searchTransactionRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
    }

    @Test
    void testGetSpecification_allNull() {
        SearchTransactionRequest searchTransactionRequest = new SearchTransactionRequest();
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        Specification<Transaction> specification = TransactionSpecification.getSpecification(searchTransactionRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
    }
}

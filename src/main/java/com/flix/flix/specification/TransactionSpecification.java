package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.EPaymentMethod;
import com.flix.flix.constant.custom_enum.EPaymentStatus;
import com.flix.flix.constant.custom_enum.ESchedule;
import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.constant.custom_enum.ETax;
import com.flix.flix.entity.Transaction;
import com.flix.flix.model.request.search.SearchTransactionRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Predicate;

public class TransactionSpecification {
    @SuppressWarnings("null")
    public static Specification<Transaction> getSpecification(SearchTransactionRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), request.getCustomerId()));
            }
            if (request.getCustomerName() != null) {
                predicates.add(cb.like(cb.lower(root.get("customer").get("fullname")), "%" + request.getCustomerName().toLowerCase() + "%"));
            }
            if (request.getEmployeeId() != null) {
                predicates.add(cb.equal(root.get("employee").get("id"), request.getEmployeeId()));
            }
            if (request.getEmployeeName() != null) {
                predicates.add(cb.like(cb.lower(root.get("employee").get("fullname")), "%" + request.getEmployeeName().toLowerCase() + "%"));
            }
            if (request.getTheaterName() != null) {
                predicates.add(cb.like(cb.lower(root.get("theater").get("name")), "%" + request.getTheaterName().toLowerCase() + "%"));
            }
            if (request.getStudioName() != null) {
                predicates.add(cb.like(cb.lower(root.get("studio").get("name")), "%" + request.getStudioName().toLowerCase() + "%"));
            }
            if (request.getProductTitle() != null) {
                predicates.add(cb.like(cb.lower(root.get("product").get("title")), "%" + request.getProductTitle().toLowerCase() + "%"));
            }
            if (request.getProductPriceMin() != null) {
                predicates.add(cb.or(
                    cb.greaterThanOrEqualTo(root.get("productPricing").get("weekdayPrice"), request.getProductPriceMin()),
                    cb.greaterThanOrEqualTo(root.get("productPricing").get("weekendPrice"), request.getProductPriceMin())
                ));
            }
            if (request.getProductPriceMax() != null) {
                predicates.add(cb.or(
                    cb.lessThanOrEqualTo(root.get("productPricing").get("weekdayPrice"), request.getProductPriceMax()),
                    cb.lessThanOrEqualTo(root.get("productPricing").get("weekendPrice"), request.getProductPriceMax())
                ));
            }
            if (request.getProductSchedule() != null) {
                predicates.add(cb.equal(root.get("productScheduling").get("schedule"), ESchedule.findByDescription(request.getProductSchedule())));
            }
            if (request.getQtyMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("qty"), request.getQtyMin()));
            }
            if (request.getQtyMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("qty"), request.getQtyMax()));
            }
            if (request.getTax() != null) {
                predicates.add(cb.equal(root.get("tax"), ETax.findByValue(request.getTax())));
            }
            if (request.getTransactionDateTimeMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDateTime"), DateUtil.parseDateTime(request.getTransactionDateTimeMin())));
            }
            if (request.getTransactionDateTimeMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDateTime"), DateUtil.parseDateTime(request.getTransactionDateTimeMax())));
            }
            if (request.getWatchDateMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("watchDate"), DateUtil.parseDate(request.getWatchDateMin())));
            }
            if (request.getWatchDateMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("watchDate"), DateUtil.parseDate(request.getWatchDateMax())));
            }
            if (request.getPaymentStatus() != null) {
                predicates.add(cb.equal(root.get("paymentStatus"), EPaymentStatus.findByDescription(request.getPaymentStatus())));
            }
            if (request.getPaymentDateTimeMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("paymentDateTime"), DateUtil.parseDateTime(request.getPaymentDateTimeMin())));
            }
            if (request.getPaymentDateTimeMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("paymentDateTime"), DateUtil.parseDateTime(request.getPaymentDateTimeMax())));
            }
            if (request.getPaymentMethod() != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), EPaymentMethod.findByDescription(request.getPaymentMethod())));
            }
            if (request.getPaymentDateTimeMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("paymentDateTime"), DateUtil.parseDateTime(request.getPaymentDateTimeMax())));
            }
            if (request.getSeats() != null) {
                for (String seat : request.getSeats()) {
                    predicates.add(cb.isMember(ESeat.findByDescription(seat), root.get("seats")));
                }
            }
            if (request.getCreatedAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), DateUtil.parseDate(request.getCreatedAtMin())));
            }
            if (request.getCreatedAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), DateUtil.parseDate(request.getCreatedAtMax())));
            }
            if (request.getUpdatedAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), DateUtil.parseDate(request.getUpdatedAtMin())));
            }
            if (request.getUpdatedAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), DateUtil.parseDate(request.getUpdatedAtMax())));
            }
            if (request.getExpirationDateMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expirationDate"), DateUtil.parseDate(request.getExpirationDateMin())));
            }
            if (request.getExpirationDateMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expirationDate"), DateUtil.parseDate(request.getExpirationDateMax())));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.entity.Employee;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.Theater;
import com.flix.flix.model.request.search.SearchTheaterRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class TheaterSpecification {

    private TheaterSpecification() {}

    @SuppressWarnings("null")
    public static Specification<Theater> getSpecification(SearchTheaterRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> havingPredicates = new ArrayList<>();

            if (request.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }
            if (request.getCity() != null) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + request.getCity().toLowerCase() + "%"));
            }
            if (request.getAddress() != null) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + request.getAddress().toLowerCase() + "%"));
            }
            if (request.getContactNumber() != null) {
                predicates.add(cb.like(root.get("contactNumber"), "%" + request.getContactNumber()+ "%"));
            }
            if (request.getContactEmail() != null) {
                predicates.add(cb.like(cb.lower(root.get("contactEmail")), "%" + request.getContactEmail().toLowerCase() + "%"));
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
            if (request.getOprationalStatus() != null) {
                predicates.add(cb.equal(root.get("oprationalStatus"), request.getOprationalStatus()));
            }
            if (request.getEmployeesName() != null && !request.getEmployeesName().isEmpty()) {
                Join<Theater, Employee> employeeJoin = root.join("employees");
                List<Predicate> hasEmployeePredicates = new ArrayList<>();
                for (String employeeName : request.getEmployeesName()) {
                    hasEmployeePredicates.add(cb.like(cb.lower(employeeJoin.get("fullname")), "%" + employeeName.toLowerCase() + "%"));
                }
                predicates.add(cb.or(hasEmployeePredicates.toArray(new Predicate[hasEmployeePredicates.size()])));
                havingPredicates.add(cb.equal(cb.countDistinct(employeeJoin), request.getEmployeesName().size()));
            }
            if (request.getProductsTitle() != null && !request.getProductsTitle().isEmpty()) {
                Join<Theater, Product> productJoin = root.join("products");
                List<Predicate> hasProductPredicates = new ArrayList<>();
                for (String product : request.getProductsTitle()) {
                    hasProductPredicates.add(cb.like(cb.lower(productJoin.get("title")), "%" + product.toLowerCase() + "%"));
                }
                predicates.add(cb.or(hasProductPredicates.toArray(new Predicate[hasProductPredicates.size()])));
                havingPredicates.add(cb.equal(cb.countDistinct(productJoin), request.getProductsTitle().size()));
            }

            cq.groupBy(root.get("id"));
            if (!havingPredicates.isEmpty()) {
                cq.having(cb.and(havingPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

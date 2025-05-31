package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.EGender;
import com.flix.flix.entity.Employee;
import com.flix.flix.model.request.search.SearchEmployeeRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Predicate;

public class EmployeeSpecification {

    private EmployeeSpecification() {}

    @SuppressWarnings("null")
    public static Specification<Employee> getSpecification(SearchEmployeeRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getFullname() != null) {
                predicates.add(cb.like(cb.lower(root.get("fullname")), "%" + request.getFullname().toLowerCase() + "%"));
            }
            if (request.getNikNumber() != null) {
                predicates.add(cb.like(root.get("nikNumber"), "%" + request.getNikNumber() + "%"));
            }
            if (request.getAddress() != null) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + request.getAddress().toLowerCase() + "%"));
            }
            if (request.getPhoneNumber() != null) {
                predicates.add(cb.like(root.get("phoneNumber"), "%" + request.getPhoneNumber() + "%"));
            }
            if (request.getGender() != null) {
                predicates.add(cb.equal(root.get("gender"), EGender.findByDescription(request.getGender())));
            }
            if (request.getCity() != null) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + request.getCity().toLowerCase() + "%"));
            }
            if (request.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.getIsActive()));
            }
            if (request.getDateOfBirthMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfBirth"), DateUtil.parseDate(request.getDateOfBirthMin())));
            }
            if (request.getDateOfBirthMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateOfBirth"), DateUtil.parseDate(request.getDateOfBirthMax())));
            }
            if (request.getDateOfApplimentMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfAppliment"), DateUtil.parseDate(request.getDateOfApplimentMin())));
            }
            if (request.getDateOfApplimentMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateOfAppliment"), DateUtil.parseDate(request.getDateOfApplimentMax())));
            }
            if (request.getAppUserUsername() != null) {
                predicates.add(cb.like(cb.lower(root.join("appUser").get("username")), "%" + request.getAppUserUsername().toLowerCase() + "%"));
            }
            if (request.getAppUserEmail() != null) {
                predicates.add(cb.like(cb.lower(root.join("appUser").get("email")), "%" + request.getAppUserEmail().toLowerCase() + "%"));
            }
            if (request.getTheaterName() != null) {
                predicates.add(cb.like(cb.lower(root.join("theater").get("name")), "%" + request.getTheaterName().toLowerCase() + "%"));
            }
            if (request.getTheaterCity() != null) {
                predicates.add(cb.like(cb.lower(root.join("theater").get("city")), "%" + request.getTheaterCity().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.ERole;
import com.flix.flix.entity.AppUser;
import com.flix.flix.model.request.search.SearchAppUserRequest;

import jakarta.persistence.criteria.Predicate;
public class AppUserSpecification {

    public static Specification<AppUser> getSpecification(SearchAppUserRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getUsername() != null) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + request.getUsername().toLowerCase() + "%"));
            }
            if (request.getEmail() != null) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + request.getEmail().toLowerCase() + "%"));
            }
            if (request.getRole() != null && request.getRole().size() > 0) {
                List<ERole> roles = request.getRole().stream().map(ERole::findByDescription).toList();
                predicates.add(root.get("role").in(roles));
            }
            if (request.getCustomerFullname() != null) {
                predicates.add(cb.like(cb.lower(root.get("customer").get("fullname")), "%" + request.getCustomerFullname().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

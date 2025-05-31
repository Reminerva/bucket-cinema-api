package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.ERole;
import com.flix.flix.entity.AppUser;
import com.flix.flix.model.request.search.SearchAppUserRequest;

import jakarta.persistence.criteria.Predicate;
public class AppUserSpecification {

    private AppUserSpecification() {}

    @SuppressWarnings("null")
    public static Specification<AppUser> getSpecification(SearchAppUserRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> havingPredicates = new ArrayList<>();

            if (request.getUsername() != null) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + request.getUsername().toLowerCase() + "%"));
            }
            if (request.getEmail() != null) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + request.getEmail().toLowerCase() + "%"));
            }
            if (request.getRole() != null && !request.getRole().isEmpty()) {
                Set<ERole> roles = request.getRole().stream()
                    .map(ERole::findByDescription)
                    .collect(Collectors.toSet());
                
                predicates.add(root.join("roles").in(roles));
                havingPredicates.add(cb.equal(cb.countDistinct(root.join("roles")), roles.size()));
            }
            if (request.getCustomerFullname() != null) {
                predicates.add(cb.like(cb.lower(root.get("customer").get("fullname")), "%" + request.getCustomerFullname().toLowerCase() + "%"));
            }

            cq.groupBy(root.get("id"));
            if (!havingPredicates.isEmpty()) {
                cq.having(cb.and(havingPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

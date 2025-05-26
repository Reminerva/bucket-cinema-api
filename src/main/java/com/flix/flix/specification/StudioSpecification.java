package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.constant.custom_enum.EStudioSize;
import com.flix.flix.entity.Studio;
import com.flix.flix.model.request.search.SearchStudioRequest;

import jakarta.persistence.criteria.Predicate;

public class StudioSpecification {

    @SuppressWarnings("null")
    public static Specification<Studio> getSpecification(SearchStudioRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }
            if (request.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.getIsActive()));
            }
            if (request.getStudioSize() != null) {
                predicates.add(cb.equal(root.get("studioSize"), EStudioSize.findByDescription(request.getStudioSize())));
            }
            if (request.getSeatLayout() != null) {
                for (String seatLayout : request.getSeatLayout()) {
                    predicates.add(cb.isMember(ESeat.findByDescription(seatLayout), root.get("seatLayout")));
                }
            }
            if (request.getProductId() != null) {
                System.out.println("ASDF1" + request.getProductId());
                predicates.add(cb.equal(root.join("productPricing").get("productIdPricing").get("id"), request.getProductId()));
            }
            if (request.getTheaterId() != null) {
                System.out.println("ASDF1" + request.getTheaterId());
                predicates.add(cb.equal(root.get("theater").get("id"), request.getTheaterId()));
            }
            if (request.getTheaterName() != null) {
                predicates.add(cb.like(cb.lower(root.get("theater").get("name")), "%" + request.getTheaterName().toLowerCase() + "%"));
            }
            if (request.getTheaterCity() != null) {
                predicates.add(cb.like(cb.lower(root.get("theater").get("city")), "%" + request.getTheaterCity().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

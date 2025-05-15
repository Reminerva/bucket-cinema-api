package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.ECountry;
import com.flix.flix.entity.ProductionCompany;
import com.flix.flix.model.request.search.SearchProductionCompanyRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Predicate;
public class ProductionCompanySpecification {

    public static Specification<ProductionCompany> getSpecification(SearchProductionCompanyRequest request) {
        return (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getName() != null) {
                        predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
                }
                if (request.getOriginCountry() != null) {
                        predicates.add(cb.equal(root.get("originCountry"), ECountry.findByDescription(request.getOriginCountry())));
                }
                if (request.getFoundedYearMin() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("foundedYear"), request.getFoundedYearMin()));
                }
                if (request.getFoundedYearMax() != null) {
                        predicates.add(cb.lessThanOrEqualTo(root.get("foundedYear"), request.getFoundedYearMax()));
                }
                if (request.getHeadquarters() != null) {
                        predicates.add(cb.like(cb.lower(root.get("headquarters")), "%" + request.getHeadquarters().toLowerCase() + "%"));
                }
                if (request.getCeo() != null) {
                        predicates.add(cb.like(cb.lower(root.get("ceo")), "%" + request.getCeo().toLowerCase() + "%"));
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

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}

package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.ECountry;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductionCompany;
import com.flix.flix.model.request.search.SearchProductionCompanyRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
public class ProductionCompanySpecification {

    @SuppressWarnings("null")
    public static Specification<ProductionCompany> getSpecification(SearchProductionCompanyRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> havingPredicates = new ArrayList<>();

            if (request.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }
            if (request.getOriginCountry() != null) {
                predicates.add(cb.equal(root.get("originCountry"), ECountry.findByDescription(request.getOriginCountry())));
            }
            if (request.getFoundedYearMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("foundedYear"), DateUtil.parseDate(request.getFoundedYearMin())));
            }
            if (request.getFoundedYearMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("foundedYear"), DateUtil.parseDate(request.getFoundedYearMax())));
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
            if (request.getHasProducts() != null && !request.getHasProducts().isEmpty()) {
                // unsolved way: (why it works?)
                // List<Predicate> hasProductPredicates = new ArrayList<>();

                // for (String hasProduct : request.getHasProducts()) {
                //     hasProductPredicates.add(cb.like(cb.lower(root.join("hasProduct").get("title")), "%" + hasProduct.toLowerCase() + "%"));
                // }
                // predicates.add(cb.and(hasProductPredicates.toArray(new Predicate[0])));

                Join<ProductionCompany, Product> hasProductJoin = root.join("hasProduct");
                List<Predicate> hasProductPredicates = new ArrayList<>();
                for (String hasProduct : request.getHasProducts()) {
                    hasProductPredicates.add(cb.like(cb.lower(hasProductJoin.get("title")), "%" + hasProduct.toLowerCase() + "%"));
                }
                predicates.add(cb.or(hasProductPredicates.toArray(new Predicate[0])));
                havingPredicates.add(cb.equal(cb.countDistinct(hasProductJoin.get("title")), request.getHasProducts().size()));
            }

            query.groupBy(root.get("id"));

            if (havingPredicates.size() > 0) {
                query.having(cb.and(havingPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}

package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.EArtistType;
import com.flix.flix.entity.Artist;
import com.flix.flix.entity.Product;
import com.flix.flix.model.request.search.SearchArtistRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class ArtistSpecification {

    @SuppressWarnings("null")
    public static Specification<Artist> getSpecification(SearchArtistRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> havingPredicates = new ArrayList<>();

            if (request.getName() != null) {
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("otherName")), "%" + request.getName().toLowerCase() + "%")
                ));
            }
            if (request.getPlaceOfBirth() != null) {
                predicates.add(cb.like(cb.lower(root.get("placeOfBirth")), "%" + request.getPlaceOfBirth().toLowerCase() + "%"));
            }
            if (request.getBirthDateMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("birthDate"), DateUtil.parseDate(request.getBirthDateMin())));
            }
            if (request.getBirthDateMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("birthDate"), DateUtil.parseDate(request.getBirthDateMax())));
            }
            if (request.getArtistType() != null && !request.getArtistType().isEmpty()) {
                List<EArtistType> artistTypes = request.getArtistType().stream()
                    .map(EArtistType::findByDescription)
                    .toList();

                for (EArtistType artistType : artistTypes) {
                    predicates.add(cb.isMember(artistType, root.get("artistTypes")));
                }
            }
            if (request.getInProductTitle() != null) {
                Join<Artist, Product> joinProduct = root.join("inProduct");
                List<Predicate> titlePredicates = new ArrayList<>();
                for (String title : request.getInProductTitle()) {
                    titlePredicates.add(cb.like(cb.lower(joinProduct.get("title")), "%" + title.toLowerCase() + "%"));
                }
                predicates.add(cb.or(titlePredicates.toArray(new Predicate[0])));
                havingPredicates.add(cb.equal(cb.countDistinct(joinProduct), request.getInProductTitle().size()));
            }

            cq.groupBy(root.get("id"));
            if (!havingPredicates.isEmpty()) {
                cq.having(cb.and(havingPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

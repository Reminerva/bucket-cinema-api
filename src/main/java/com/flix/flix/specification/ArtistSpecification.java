package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.EArtistType;
import com.flix.flix.entity.Artist;
import com.flix.flix.model.request.search.SearchArtistRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Predicate;

public class ArtistSpecification {

    public static Specification<Artist> getSpecification(SearchArtistRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

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
            if (request.getArtistType() != null) {
                predicates.add(cb.equal(root.get("artistType"), EArtistType.findByDescription(request.getArtistType())));
            }
            if (request.getInProductTitle() != null) {
                List<Predicate> likePredicates = request.getInProductTitle().stream()
                    .map(title -> cb.like(cb.lower(root.get("inProduct").get("title")), "%" + title.toLowerCase() + "%"))
                    .toList();
                predicates.add(cb.and(likePredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

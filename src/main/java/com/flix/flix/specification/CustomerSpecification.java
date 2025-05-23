package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.EGender;
import com.flix.flix.constant.custom_enum.EGenre;
import com.flix.flix.entity.Customer;
import com.flix.flix.entity.FavGenre;
import com.flix.flix.entity.Product;
import com.flix.flix.model.request.search.SearchCustomerRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class CustomerSpecification {

    @SuppressWarnings("null")
    public static Specification<Customer> getSpecification(SearchCustomerRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> havingPredicates = new ArrayList<>();

            if (request.getFullname() != null) {
                predicates.add(cb.like(cb.lower(root.get("fullname")), "%" + request.getFullname().toLowerCase() + "%"));
            }
            if (request.getCountry() != null) {
                predicates.add(cb.like(cb.lower(root.get("country")), "%" + request.getCountry().toLowerCase() + "%"));
            }
            if (request.getPhoneNumber() != null) {
                predicates.add(cb.like(root.get("phoneNumber"), "%" + request.getPhoneNumber() + "%"));
            }
            if (request.getCity() != null) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + request.getCity().toLowerCase() + "%"));
            }
            if (request.getGender() != null) {
                predicates.add(cb.equal(root.get("gender"), EGender.findByDescription(request.getGender().toLowerCase())));
            }
            if (request.getBirthDateMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("birthDate"), DateUtil.parseDate(request.getBirthDateMin())));
            }
            if (request.getBirthDateMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("birthDate"), DateUtil.parseDate(request.getBirthDateMax())));
            }
            if (request.getRegistrationDateMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("registrationDate"), DateUtil.parseDate(request.getRegistrationDateMin())));
            }
            if (request.getRegistrationDateMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("registrationDate"), DateUtil.parseDate(request.getRegistrationDateMax())));
            }
            if (request.getLastLoginMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastLogin"), DateUtil.parseDate(request.getLastLoginMin())));
            }
            if (request.getLastLoginMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastLogin"), DateUtil.parseDate(request.getLastLoginMax())));
            }
            if (request.getFavGenres() != null && !request.getFavGenres().isEmpty()) {
                Join<Customer, FavGenre> favGenreJoin = root.join("favGenre");
                Set<EGenre> requiredGenres = request.getFavGenres().stream()
                    .map(EGenre::findByDescription)
                    .collect(Collectors.toSet());
                predicates.add(favGenreJoin.get("favGenre").in(requiredGenres));
                havingPredicates.add(cb.equal(cb.countDistinct(root.get("id")), requiredGenres.size()));
            }
            if (request.getEmail() != null) {
                predicates.add(cb.like(cb.lower(root.get("appUser").get("email")), "%" + request.getEmail().toLowerCase() + "%"));
            }
            if (request.getLikeProductTitle() != null && !request.getLikeProductTitle().isEmpty()) {
                Join<Customer, Product> likeProductJoin = root.join("likeProduct");

                Set<String> requiredProducts = request.getLikeProductTitle().stream()
                    .map(title -> title.toLowerCase())
                    .collect(Collectors.toSet());

                List<Predicate> likePredicates = new ArrayList<>();
                for (String title : requiredProducts) {
                    likePredicates.add(cb.like(cb.lower(likeProductJoin.get("title")), "%" + title.toLowerCase() + "%"));
                }
                predicates.add(cb.or(likePredicates.toArray(new Predicate[0])));

                havingPredicates.add(cb.equal(cb.countDistinct(likeProductJoin.get("id")), request.getLikeProductTitle().size()));
            }
            if (request.getDislikeProductTitle() != null && !request.getDislikeProductTitle().isEmpty()) {
                Join<Customer, Product> dislikeProductJoin = root.join("dislikeProduct");

                Set<String> requiredProducts = request.getDislikeProductTitle().stream()
                    .map(title -> title.toLowerCase())
                    .collect(Collectors.toSet());

                List<Predicate> dislikePredicates = new ArrayList<>();
                for (String title : requiredProducts) {
                    dislikePredicates.add(cb.like(cb.lower(dislikeProductJoin.get("title")), "%" + title.toLowerCase() + "%"));
                }
                predicates.add(cb.or(dislikePredicates.toArray(new Predicate[0])));

                havingPredicates.add(cb.equal(cb.countDistinct(dislikeProductJoin.get("id")), request.getDislikeProductTitle().size()));
            }

            cq.groupBy(root.get("id"));
            if (!havingPredicates.isEmpty()) {
                cq.having(cb.and(havingPredicates.toArray(new Predicate[havingPredicates.size()])));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

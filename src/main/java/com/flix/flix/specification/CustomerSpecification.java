package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.EGender;
import com.flix.flix.constant.custom_enum.EGenre;
import com.flix.flix.entity.Customer;
import com.flix.flix.model.request.search.SearchCustomerRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Predicate;

public class CustomerSpecification {

    public static Specification<Customer> getSpecification(SearchCustomerRequest request) {
        return (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getFullname() != null) {
                    predicates.add(cb.like(cb.lower(root.get("fullname")), "%" + request.getFullname() + "%"));
                }
                if (request.getCountry() != null) {
                    predicates.add(cb.like(cb.lower(root.get("country")), "%" + request.getCountry() + "%"));
                }
                if (request.getPhoneNumber() != null) {
                    predicates.add(cb.like(cb.lower(root.get("phoneNumber")), "%" + request.getPhoneNumber() + "%"));
                }
                if (request.getCity() != null) {
                    predicates.add(cb.like(cb.lower(root.get("city")), "%" + request.getCity() + "%"));
                }
                if (request.getGender() != null) {
                    predicates.add(cb.equal(root.get("gender"), EGender.findByDescription(request.getGender())));
                }
                if (request.getRegistrationDate() != null) {
                    predicates.add(cb.equal(root.get("registrationDate"), DateUtil.parseDate(request.getRegistrationDate())));
                }
                if (request.getLastLogin() != null) {
                    predicates.add(cb.equal(root.get("lastLogin"), DateUtil.parseDate(request.getLastLogin())));
                }
                if (request.getFavGenres() != null) {
                    predicates.add(root.get("favGenre").in(request.getFavGenres().stream().map(EGenre::findByDescription).toList()));
                }
                if (request.getEmail() != null) {
                    predicates.add(cb.like(cb.lower(root.get("appUser").get("email")), "%" + request.getEmail() + "%"));
                }

                if (request.getDislikeProductTitle() != null && !request.getDislikeProductTitle().isEmpty()) {
                    List<Predicate> dislikePredicates = request.getDislikeProductTitle().stream()
                        .map(title -> cb.like(cb.lower(root.get("dislikeProduct").get("title")), "%" + title.toLowerCase() + "%"))
                        .toList();
                    predicates.add(cb.and(dislikePredicates.toArray(new Predicate[0])));
                }
                if (request.getLikeProductTitle() != null && !request.getLikeProductTitle().isEmpty()) {
                    List<Predicate> likePredicates = request.getLikeProductTitle().stream()
                        .map(title -> cb.like(cb.lower(root.get("likeProduct").get("title")), "%" + title.toLowerCase() + "%"))
                        .toList();
                    predicates.add(cb.and(likePredicates.toArray(new Predicate[0])));
                }
                if (request.getTheaterName() != null && !request.getTheaterName().isEmpty()) {
                    List<Predicate> theaterPredicates = request.getTheaterName().stream()
                        .map(name -> cb.like(cb.lower(root.get("theater").get("name")), "%" + name.toLowerCase() + "%"))
                        .toList();
                    predicates.add(cb.and(theaterPredicates.toArray(new Predicate[0])));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}

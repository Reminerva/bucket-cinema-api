package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.ECountry;
import com.flix.flix.constant.custom_enum.EGenre;
import com.flix.flix.constant.custom_enum.ELanguage;
import com.flix.flix.constant.custom_enum.ERated;
import com.flix.flix.entity.Artist;
import com.flix.flix.entity.MovieGenre;
import com.flix.flix.entity.Product;
import com.flix.flix.model.request.search.SearchProductRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
public class ProductSpecification {

    @SuppressWarnings("null")
    public static Specification<Product> getSpecification(SearchProductRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> havingPredicates = new ArrayList<>();

            if (request.getTitle() != null) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + request.getTitle().toLowerCase() + "%"));
            }
            if (request.getDurationMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("duration"), request.getDurationMin()));
            }
            if (request.getDurationMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("duration"), request.getDurationMax()));
            }
            if (request.getLanguage() != null) {
                predicates.add(cb.equal(root.get("language"), ELanguage.findByDescription(request.getLanguage())));
            }
            if (request.getCountry() != null) {
                predicates.add(cb.equal(root.get("country"), ECountry.findByDescription(request.getCountry())));
            }
            if (request.getReleaseDateMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("releaseDate"), DateUtil.parseDate(request.getReleaseDateMin())));
            }
            if (request.getReleaseDateMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("releaseDate"), DateUtil.parseDate(request.getReleaseDateMax())));
            }
            if (request.getRated() != null) {
                predicates.add(cb.equal(root.get("rated"), ERated.findByDescription(request.getRated())));
            }
            if (request.getBudgetMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("budget"), request.getBudgetMin()));
            }
            if (request.getBudgetMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("budget"), request.getBudgetMax()));
            }
            if (request.getImdbRatingMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("imdbRating"), request.getImdbRatingMin()));
            }
            if (request.getImdbRatingMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("imdbRating"), request.getImdbRatingMax()));
            }
            if (request.getRottenTomatoesRatingMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rottenTomatoesRating"), request.getRottenTomatoesRatingMin()));
            }
            if (request.getRottenTomatoesRatingMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rottenTomatoesRating"), request.getRottenTomatoesRatingMax()));
            }
            if (request.getMovieGenre() != null && !request.getMovieGenre().isEmpty()) {
                Join<Product, MovieGenre> movieGenreJoin = root.join("movieGenre");
                Set<EGenre> requiredGenres = request.getMovieGenre().stream()
                    .map(EGenre::findByDescription)
                    .collect(Collectors.toSet());

                predicates.add(movieGenreJoin.get("genre").in(requiredGenres));

                havingPredicates.add(cb.equal(cb.countDistinct(movieGenreJoin.get("genre")), requiredGenres.size()));
            }
            if (request.getProductPricingMin() != null) {
                predicates.add(cb.or(
                    cb.and(cb.greaterThanOrEqualTo(root.get("productPricing").get("weekdayPrice"), request.getProductPricingMin()),
                        cb.isTrue(root.get("productPricing").get("isPriceActive"))),
                    cb.and(cb.greaterThanOrEqualTo(root.get("productPricing").get("weekendPrice"), request.getProductPricingMin()),
                        cb.isTrue(root.get("productPricing").get("isPriceActive")))
                ));
            }
            if (request.getProductPricingMax() != null) {
                predicates.add(cb.or(
                    cb.and(cb.lessThanOrEqualTo(root.get("productPricing").get("weekdayPrice"), request.getProductPricingMax()),
                        cb.isTrue(root.get("productPricing").get("isPriceActive"))),
                    cb.and(cb.lessThanOrEqualTo(root.get("productPricing").get("weekendPrice"), request.getProductPricingMax()),
                        cb.isTrue(root.get("productPricing").get("isPriceActive")))
                ));
            }        
            if (request.getLastUpdatedMin() != null) {
                predicates.add(cb.or(
                    cb.greaterThanOrEqualTo(root.get("lastUpdated"), DateUtil.parseDate(request.getLastUpdatedMin())),
                    cb.greaterThanOrEqualTo(root.get("productPricing").get("priceDate"), DateUtil.parseDate(request.getLastUpdatedMin()))
                ));
            }
            if (request.getLastUpdatedMax() != null) {
                predicates.add(cb.or(
                    cb.lessThanOrEqualTo(root.get("lastUpdated"), DateUtil.parseDate(request.getLastUpdatedMax())),
                    cb.lessThanOrEqualTo(root.get("productPricing").get("priceDate"), DateUtil.parseDate(request.getLastUpdatedMax()))
                ));
            }
            if (request.getArtistsName() != null && !request.getArtistsName().isEmpty()) {
                Join<Product, Artist> nameJoin = root.join("artists");

                Set<String> artistNames = request.getArtistsName().stream()
                    .map(name -> name.toLowerCase())
                    .collect(Collectors.toSet());

                List<Predicate> predicatesArtist = new ArrayList<>();
                for (String name : artistNames) {
                    predicatesArtist.add(cb.like(cb.lower(nameJoin.get("name")), "%" + name + "%"));
                }
                predicates.add(cb.or(predicatesArtist.toArray(new Predicate[0])));

                havingPredicates.add(cb.equal(cb.countDistinct(nameJoin.get("name")), artistNames.size()));
            }
            if (request.getProductionCompany() != null) {
                predicates.add(cb.like(cb.lower(root.get("productionCompany").get("name")), "%" + request.getProductionCompany().toLowerCase() + "%"));
            }

            // apply groupBy once
            cq.groupBy(root.get("id"));

            // apply combined having condition
            if (!havingPredicates.isEmpty()) {
                cq.having(cb.and(havingPredicates.toArray(new Predicate[0])));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
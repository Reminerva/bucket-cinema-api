package com.flix.flix.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flix.flix.constant.custom_enum.ECountry;
import com.flix.flix.constant.custom_enum.EGenre;
import com.flix.flix.constant.custom_enum.ELanguage;
import com.flix.flix.constant.custom_enum.ERated;
import com.flix.flix.entity.Product;
import com.flix.flix.model.request.search.SearchProductRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.Predicate;
public class ProductSpecification {

  public static Specification<Product> getSpecification(SearchProductRequest request) {
      return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

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
            if (request.getDirectorName() != null) {
                predicates.add(cb.like(cb.lower(root.get("director")), "%" + request.getDirectorName().toLowerCase() + "%"));
            }
            if (request.getWriterName() != null) {
                predicates.add(cb.like(cb.lower(root.get("writer")), "%" + request.getWriterName().toLowerCase() + "%"));
            }
            if (request.getProducerName() != null) {
                predicates.add(cb.like(cb.lower(root.get("producer")), "%" + request.getProducerName().toLowerCase() + "%"));
            }
            if (request.getMovieGenre() != null && !request.getMovieGenre().isEmpty()) {
                List<EGenre> genres = request.getMovieGenre().stream().map(EGenre::findByDescription).toList();
                predicates.add(root.get("movieGenre").in(genres));
            }
            if (request.getProductPricingMin() != null) {
              predicates.add(cb.or(
                cb.and(cb.greaterThanOrEqualTo(root.get("productPricing").get("weekdayPrice"), request.getProductPricingMin()),
                    cb.isTrue(root.get("productPricing").get("weekdayPriceActive"))),
                cb.and(cb.greaterThanOrEqualTo(root.get("productPricing").get("weekendPrice"), request.getProductPricingMin()),
                    cb.isTrue(root.get("productPricing").get("weekendPriceActive")))
              ));
            }
            if (request.getProductPricingMax() != null) {
              predicates.add(cb.or(
                  cb.and(cb.lessThanOrEqualTo(root.get("productPricing").get("weekdayPrice"), request.getProductPricingMax()),
                      cb.isTrue(root.get("productPricing").get("weekdayPriceActive"))),
                  cb.and(cb.lessThanOrEqualTo(root.get("productPricing").get("weekendPrice"), request.getProductPricingMax()),
                      cb.isTrue(root.get("productPricing").get("weekendPriceActive")))
              ));
            }        
            if (request.getLastUpdatedMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastUpdated"), DateUtil.parseDate(request.getLastUpdatedMin())));
            }
            if (request.getLastUpdatedMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastUpdated"), DateUtil.parseDate(request.getLastUpdatedMax())));
            }
            if (request.getArtistsName() != null && !request.getArtistsName().isEmpty()) {
              List<Predicate> artistPredicates = new ArrayList<>();
              for (String name : request.getArtistsName()) {
                  artistPredicates.add(cb.like(cb.lower(root.get("artists").get("name")), "%" + name.toLowerCase() + "%"));
              }
              predicates.add(cb.or(artistPredicates.toArray(new Predicate[0])));
            }
            if (request.getProductionCompany() != null) {
                predicates.add(cb.like(cb.lower(root.get("productionCompany").get("name")), "%" + request.getProductionCompany().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
package com.flix.flix.specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import com.flix.flix.constant.custom_enum.ECountry;
import com.flix.flix.constant.custom_enum.ELanguage;
import com.flix.flix.entity.Artist;
import com.flix.flix.entity.Product;
import com.flix.flix.model.request.search.SearchProductRequest;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecificationTest {

    @Mock
    private Root<Product> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Join<Product, Object> movieGenreJoin;

    @Mock
    private Path<Object> movieGenrePath;

    @Mock
    private Path<Object> productPricingPath;

    @Mock
    private Path<Object> weekdayPricePath;

    @Mock
    private Path<Object> weekendPricePath;

    @Mock
    private Join<Product, Artist> artistJoin;

    @Mock
    private Path<Object> artistNamePath;

    @Mock
    private Path<Object> productionCompanyPath;

    @Mock
    private Path<Object> productionCompanyNamePath;

    @Mock
    private Predicate predicate;

    private SearchProductRequest searchProductRequest = new SearchProductRequest();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        searchProductRequest.setTitle("title");
        searchProductRequest.setDurationMin(60L);
        searchProductRequest.setDurationMax(120L);
        searchProductRequest.setLanguage("english");
        searchProductRequest.setCountry("japan");
        searchProductRequest.setBudgetMin(1000000L);
        searchProductRequest.setBudgetMax(2000000L);
        searchProductRequest.setImdbRatingMin(3.5);
        searchProductRequest.setImdbRatingMax(4.5);
        searchProductRequest.setRottenTomatoesRatingMin(3);
        searchProductRequest.setRottenTomatoesRatingMax(4);
        searchProductRequest.setReleaseDateMin("2022-01-01");
        searchProductRequest.setReleaseDateMax("2023-01-01");
        searchProductRequest.setLastUpdatedMin("2022-01-01");
        searchProductRequest.setLastUpdatedMax("2023-01-01");
        searchProductRequest.setMovieGenre(List.of("action", "horror"));
        searchProductRequest.setProductPricingMin(100000.0);
        searchProductRequest.setProductPricingMax(200000.0);
        searchProductRequest.setArtistsName(List.of("artist1", "artist2"));
        searchProductRequest.setProductionCompany("company1");
        searchProductRequest.setRated("PG-13");
    }

    @Test
    void testGetSpecification() {

        when(root.<Product, Object>join("movieGenre")).thenReturn(movieGenreJoin);
        when(movieGenreJoin.get("genre")).thenReturn(movieGenrePath);
        when(root.get("productPricing")).thenReturn(productPricingPath);
        when(productPricingPath.get("weekdayPrice")).thenReturn(weekdayPricePath);
        when(productPricingPath.get("weekendPrice")).thenReturn(weekendPricePath);
        when(root.<Product, Artist>join("artists")).thenReturn(artistJoin);
        when(artistJoin.get("name")).thenReturn(artistNamePath);
        when(root.get("productionCompany")).thenReturn(productionCompanyPath);
        when(productionCompanyPath.get("name")).thenReturn(productionCompanyNamePath);

        when(cb.equal(root.get("title"), "title")).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("duration"), 60L)).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("duration"), 120L)).thenReturn(predicate);
        when(cb.equal(root.get("language"), ELanguage.LANGUAGE_ENGLISH)).thenReturn(predicate);
        when(cb.equal(root.get("country"), ECountry.COUNTRY_JAPAN)).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("budget"), 1000000L)).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("budget"), 2000000L)).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("imdbRating"), 3.5)).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("imdbRating"), 4.5)).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("rottenTomatoesRating"), 3)).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("rottenTomatoesRating"), 4)).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("releaseDate"), LocalDate.parse("2022-01-01"))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("releaseDate"), LocalDate.parse("2023-01-01"))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("lastUpdated"), LocalDate.parse("2022-01-01"))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("lastUpdated"), LocalDate.parse("2023-01-01"))).thenReturn(predicate);
        when(cb.equal(root.get("rated"), "PG-13")).thenReturn(predicate);
        when(cb.equal(root.get("isDeleted"), false)).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("createdDate"), LocalDate.parse("2022-01-01"))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("createdDate"), LocalDate.parse("2023-01-01"))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("updatedDate"), LocalDate.parse("2022-01-01"))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("updatedDate"), LocalDate.parse("2023-01-01"))).thenReturn(predicate);
        when(cb.equal(root.get("createdBy"), "createdBy")).thenReturn(predicate);
        when(cb.equal(root.get("updatedBy"), "updatedBy")).thenReturn(predicate);

        when(movieGenreJoin.get("genre").in(searchProductRequest.getMovieGenre())).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("productPricing").get("weekdayPrice"), 100000.0)).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(root.get("productPricing").get("weekendPrice"), 100000.0)).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("productPricing").get("weekdayPrice"), 200000.0)).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(root.get("productPricing").get("weekendPrice"), 200000.0)).thenReturn(predicate);
        when(cb.like(cb.lower(artistJoin.get("name")), "%artist1%")).thenReturn(predicate);
        when(cb.like(cb.lower(productionCompanyPath.get("name")), "%company1%")).thenReturn(predicate);

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<Product> specification = ProductSpecification.getSpecification(searchProductRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
        // verify(cb, times(2)).and(any(Predicate[].class));
        // verify(cb, times(5)).or(any(Predicate.class), any(Predicate.class));
    }

    @Test
    void testGetSpecification_allNull() {
        SearchProductRequest searchProductRequest = new SearchProductRequest();
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        Specification<Product> specification = ProductSpecification.getSpecification(searchProductRequest);
        assertNotNull(specification.toPredicate(root, query, cb));
    }
}

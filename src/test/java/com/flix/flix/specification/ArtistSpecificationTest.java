package com.flix.flix.specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.flix.flix.constant.custom_enum.EArtistType;
import com.flix.flix.entity.Artist;
import com.flix.flix.entity.Product;
import com.flix.flix.model.request.search.SearchArtistRequest;
import com.flix.flix.util.DateUtil;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class ArtistSpecificationTest {

    @Mock
    private Root<Artist> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<String> namePath;

    @Mock
    private Path<String> otherNamePath;

    @Mock
    private Path<String> placeOfBirthPath;

    @Mock
    private Path<LocalDate> birthDatePath;

    @Mock
    private Path<Object> artistTypesPath;

    @Mock
    private Join<Artist, Product> productJoin;

    @Mock
    private Path<String> productTitlePath;

    @Mock
    private Predicate predicate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSpecification_withNameAndPlaceOfBirth() {
        SearchArtistRequest request = new SearchArtistRequest();
        request.setName("john");
        request.setPlaceOfBirth("usa");

        when(root.<String>get("name")).thenReturn(namePath);
        when(root.<String>get("otherName")).thenReturn(otherNamePath);
        when(root.<String>get("placeOfBirth")).thenReturn(placeOfBirthPath);


        when(cb.lower(namePath)).thenReturn(namePath);
        when(cb.lower(otherNamePath)).thenReturn(otherNamePath);
        when(cb.lower(placeOfBirthPath)).thenReturn(placeOfBirthPath);

        when(cb.like(eq(namePath), anyString())).thenReturn(predicate);
        when(cb.like(eq(otherNamePath), anyString())).thenReturn(predicate);
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<Artist> spec = ArtistSpecification.getSpecification(request);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).or(any(Predicate.class), any(Predicate.class));
        verify(cb).like(eq(placeOfBirthPath), anyString());
    }

    @Test
    void testGetSpecification_withNameAndPlaceOfBirthAndBirthDate() {
        SearchArtistRequest request = new SearchArtistRequest();
        request.setName("john");
        request.setPlaceOfBirth("usa");
        request.setBirthDateMin("1990-01-01");
        request.setBirthDateMax("1999-12-31");

        when(root.<String>get("name")).thenReturn(namePath);
        when(root.<String>get("otherName")).thenReturn(otherNamePath);
        when(root.<String>get("placeOfBirth")).thenReturn(placeOfBirthPath);
        when(root.<LocalDate>get("birthDate")).thenReturn(birthDatePath);

        when(cb.lower(namePath)).thenReturn(namePath);
        when(cb.lower(otherNamePath)).thenReturn(otherNamePath);
        when(cb.lower(placeOfBirthPath)).thenReturn(placeOfBirthPath);

        when(cb.like(eq(namePath), anyString())).thenReturn(predicate);
        when(cb.like(eq(otherNamePath), anyString())).thenReturn(predicate);
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(eq(birthDatePath), any(LocalDate.class))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(eq(birthDatePath), any(LocalDate.class))).thenReturn(predicate);

        Specification<Artist> spec = ArtistSpecification.getSpecification(request);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).or(any(Predicate.class), any(Predicate.class));
        verify(cb).like(eq(placeOfBirthPath), anyString());
        verify(cb).greaterThanOrEqualTo(eq(birthDatePath), any(LocalDate.class));
        verify(cb).lessThanOrEqualTo(eq(birthDatePath), any(LocalDate.class));
    }

    @Test
    void testGetSpecification_withNameAndPlaceOfBirthAndArtistType() {
        SearchArtistRequest request = new SearchArtistRequest();
        request.setName("john");
        request.setPlaceOfBirth("usa");
        request.setArtistType(List.of("Actor"));

        when(root.<String>get("name")).thenReturn(namePath);
        when(root.<String>get("otherName")).thenReturn(otherNamePath);
        when(root.<String>get("placeOfBirth")).thenReturn(placeOfBirthPath);
        when(root.<Object>get("artistTypes")).thenReturn(artistTypesPath);

        when(cb.lower(namePath)).thenReturn(namePath);
        when(cb.lower(otherNamePath)).thenReturn(otherNamePath);
        when(cb.lower(placeOfBirthPath)).thenReturn(placeOfBirthPath);

        when(cb.like(eq(namePath), anyString())).thenReturn(predicate);
        when(cb.like(eq(otherNamePath), anyString())).thenReturn(predicate);
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        when(cb.isMember(any(EArtistType.class), any(Path.class))).thenReturn(predicate);

        Specification<Artist> spec = ArtistSpecification.getSpecification(request);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).or(any(Predicate.class), any(Predicate.class));
        verify(cb).like(eq(placeOfBirthPath), anyString());
        verify(cb).isMember(any(EArtistType.class), any(Path.class));
    }

    @Test
    void testGetSpecification_withInProductTitle() {
        // Mock SearchArtistRequest
        SearchArtistRequest request = new SearchArtistRequest();
        request.setInProductTitle(List.of("movie", "series"));

        // Mock join
        Join<Object, Product> joinProduct = mock(Join.class);
        Path<String> titlePath = mock(Path.class);
        Predicate titlePredicate1 = mock(Predicate.class);
        Predicate titlePredicate2 = mock(Predicate.class);
        Predicate orPredicate = mock(Predicate.class);
        Predicate havingPredicate = mock(Predicate.class);

        // Mock root and cb behavior
        when(root.<Object, Product>join("inProduct")).thenReturn(joinProduct);
        when(joinProduct.<String>get("title")).thenReturn(titlePath);
        when(cb.lower(titlePath)).thenReturn(titlePath);
        when(cb.like(titlePath, "%movie%")).thenReturn(titlePredicate1);
        when(cb.like(titlePath, "%series%")).thenReturn(titlePredicate2);
        when(cb.or(titlePredicate1, titlePredicate2)).thenReturn(orPredicate);
        when(cb.countDistinct(joinProduct)).thenReturn(mock(Expression.class));
        when(cb.equal(any(), eq(2L))).thenReturn(havingPredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(mock(Predicate.class)); // For return

        // Run Specification
        Specification<Artist> spec = ArtistSpecification.getSpecification(request);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root).join("inProduct");
        verify(cb).like(any(), eq("%movie%"));
        verify(cb).like(any(), eq("%series%"));
    }

}

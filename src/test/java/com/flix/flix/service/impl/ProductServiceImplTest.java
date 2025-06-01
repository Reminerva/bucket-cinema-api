package com.flix.flix.service.impl;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.*;
import com.flix.flix.entity.Artist;
import com.flix.flix.entity.MovieGenre;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductArtist;
import com.flix.flix.entity.ProductionCompany;
import com.flix.flix.entity.Theater;
import com.flix.flix.model.request.NewProductArtistRequest;
import com.flix.flix.model.request.NewProductRequest;
import com.flix.flix.model.request.search.SearchProductRequest;
import com.flix.flix.model.response.ProductArtistResponse;
import com.flix.flix.model.response.ProductResponse;
import com.flix.flix.repository.ProductArtistRepository;
import com.flix.flix.repository.ProductRepository;
import com.flix.flix.repository.ProductionCompanyRepository;
import com.flix.flix.repository.TheaterRepository;
import com.flix.flix.service.ArtistService;
import com.flix.flix.service.MovieGenreService;
import com.flix.flix.service.ProductArtistService;
import com.flix.flix.service.ProductionCompanyService;
import com.flix.flix.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MovieGenreService movieGenreService;
    @Mock
    private ArtistService artistService;
    @Mock
    private ProductionCompanyService productionCompanyService;
    @Mock
    private ProductionCompanyRepository productionCompanyRepository;
    @Mock
    private TheaterRepository theaterRepository;
    @Mock
    private ProductArtistService productArtistService;
    @Mock
    private ProductArtistRepository productArtistRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private NewProductRequest newProductRequest;
    private Product product;
    private ProductionCompany productionCompany;
    private Artist artist1;
    private Artist artist2;
    private MovieGenre movieGenre1;
    private MovieGenre movieGenre2;
    private ProductArtist productArtist1;
    private ProductArtist productArtist2;
    private Theater theater1;
    private Theater theater2;
    private ProductArtistResponse productArtistResponse1;
    private ProductArtistResponse productArtistResponse2;


    @BeforeEach
    void setUp() {
        productionCompany = ProductionCompany.builder()
                .id("pc1")
                .name("Production A")
                .hasProduct(new ArrayList<>())
                .build();

        artist1 = Artist.builder().id("artist1").name("Actor One").build();
        artist2 = Artist.builder().id("artist2").name("Director Two").build();

        movieGenre1 = MovieGenre.builder().id("mg1").genre(EGenre.GENRE_ACTION).build();
        movieGenre2 = MovieGenre.builder().id("mg2").genre(EGenre.GENRE_COMEDY).build();

        productArtist1 = ProductArtist.builder()
                .id("pa1")
                .artist(artist1)
                .artistType(Arrays.asList(EArtistType.TYPE_ACTOR))
                .build();

        productArtist2 = ProductArtist.builder()
                .id("pa2")
                .artist(artist2)
                .artistType(Arrays.asList(EArtistType.TYPE_DIRECTOR))
                .build();

        productArtistResponse1 = ProductArtistResponse.builder()
                .id("pa1")
                .artistId("artist1")
                .artistName("Actor One")
                .artistType(Arrays.asList("ACTOR"))
                .build();

        productArtistResponse2 = ProductArtistResponse.builder()
                .id("pa2")
                .artistId("artist2")
                .artistName("Director Two")
                .artistType(Arrays.asList("DIRECTOR"))
                .build();


        product = Product.builder()
                .id("p1")
                .title("Test Movie")
                .duration(120L)
                .language(ELanguage.LANGUAGE_ENGLISH)
                .country(ECountry.COUNTRY_ENGLAND)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .posterUrl("poster.jpg")
                .trailerUrl("trailer.mp4")
                .rated(ERated.RATED_PG_13)
                .budget(100000000L)
                .synopsis("A great test movie.")
                .tagline("Test tagline")
                .imdbRating(8.5)
                .rottenTomatoesRating(90)
                .lastUpdated(LocalDate.now())
                .productionCompany(productionCompany)
                .movieGenre(new ArrayList<>(Arrays.asList(movieGenre1, movieGenre2)))
                .productArtists(new ArrayList<>(Arrays.asList(productArtist1, productArtist2)))
                .theaters(new ArrayList<>())
                .build();

        newProductRequest = NewProductRequest.builder()
                .title("New Test Movie")
                .duration(150L)
                .language("ENGLISH")
                .country("UNITED STATES")
                .releaseDate("2024-05-15")
                .posterUrl("new_poster.jpg")
                .trailerUrl("new_trailer.mp4")
                .rated("PG-13")
                .budget(150000000L)
                .synopsis("An even greater test movie.")
                .tagline("New tagline")
                .imdbRating(9.0)
                .rottenTomatoesRating(95)
                .productionCompanyId("pc1")
                .movieGenre(Arrays.asList("ACTION", "COMEDY"))
                .productArtistRequests(Arrays.asList(
                        NewProductArtistRequest.builder()
                                .artistId("artist1")
                                .artistTypes(Arrays.asList("ACTOR")).build(),
                        NewProductArtistRequest.builder()
                                .artistId("artist2")
                                .artistTypes(Arrays.asList("DIRECTOR")).build()
                ))
                .showingOnTheaters(Arrays.asList("t1", "t2"))
                .build();

        theater1 = Theater.builder().id("t1").name("Cinema A").city("City A").address("Addr A").contactNumber("111").contactEmail("a@a.com").products(new ArrayList<>()).build();
        theater2 = Theater.builder().id("t2").name("Cinema B").city("City B").address("Addr B").contactNumber("222").contactEmail("b@b.com").products(new ArrayList<>()).build();
    }

    @Test
    void create_Success() {
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(anyString())).thenReturn(LocalDate.of(2024, 5, 15));
            mockedDateUtil.when(() -> DateUtil.parseDateTime(anyString())).thenReturn(LocalDateTime.now()); // Not used directly in create but good to mock

            when(productionCompanyService.getProductionCompanyById(anyString())).thenReturn(productionCompany);
            when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> {
                Product savedProduct = invocation.getArgument(0);
                if (savedProduct.getId() == null) {
                    savedProduct.setId("new_p1"); // Simulate ID generation by DB
                }
                return savedProduct;
            });
            when(movieGenreService.create(any(MovieGenre.class))).thenAnswer(invocation -> {
                MovieGenre createdGenre = invocation.getArgument(0);
                createdGenre.setId("new_mg");
                return createdGenre;
            });
            when(artistService.getArtistById(artist1.getId())).thenReturn(artist1);
            when(artistService.getArtistById(artist2.getId())).thenReturn(artist2);
            when(productArtistService.create(any(ProductArtist.class))).thenAnswer(invocation -> {
                ProductArtist createdProductArtist = invocation.getArgument(0);
                createdProductArtist.setId("new_pa");
                return ProductArtistResponse.builder().id("new_pa").artistId(createdProductArtist.getArtist().getId()).build(); // Return a response for create
            });
            when(productArtistService.getProductArtistById(anyString())).thenAnswer(invocation -> {
                String id = invocation.getArgument(0);
                if (id.equals("new_pa")) return productArtist1; // Simulate retrieving the created ProductArtist
                return null;
            });

            ProductResponse response = productService.create(newProductRequest);

            assertNotNull(response);
            assertEquals("New Test Movie", response.getTitle());
            assertEquals("new_p1", response.getId());
            assertEquals(2, response.getMovieGenre().size());
            assertEquals(2, response.getProductArtists().size());

            verify(productionCompanyService, times(3)).getProductionCompanyById(anyString()); // Once for setting, once for adding to hasProduct
            verify(productRepository, times(2)).saveAndFlush(any(Product.class)); // First save, then final save
            verify(movieGenreService, times(2)).create(any(MovieGenre.class));
            verify(productArtistService, times(2)).create(any(ProductArtist.class));
            verify(productArtistService, times(2)).getProductArtistById(anyString());
        }
    }

    @Test
    void create_ThrowsException_OnDuplicateArtist() {
        newProductRequest.setProductArtistRequests(Arrays.asList(
                NewProductArtistRequest.builder().artistId("artist1").artistTypes(Arrays.asList("ACTOR")).build(),
                NewProductArtistRequest.builder().artistId("artist1").artistTypes(Arrays.asList("DIRECTOR")).build() // Duplicate artistId
        ));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.create(newProductRequest));
        assertTrue(thrown.getMessage().contains(DbBash.DUPLICATE_ARTIST_REQUEST));
        verify(productRepository, never()).saveAndFlush(any(Product.class));
    }

    @Test
    void create_ThrowsException_OnDuplicateArtistType() {
        newProductRequest.setProductArtistRequests(Arrays.asList(
                NewProductArtistRequest.builder().artistId("artist1").artistTypes(Arrays.asList("ACTOR", "ACTOR")).build() // Duplicate artist type
        ));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.create(newProductRequest));
        assertTrue(thrown.getMessage().contains(DbBash.DUPLICATE_ARTIST_TYPE_REQUEST));
        verify(productRepository, never()).saveAndFlush(any(Product.class));
    }

    @Test
    void create_ThrowsException_OnDuplicateTheater() {
        newProductRequest.setShowingOnTheaters(Arrays.asList("t1", "t1")); // Duplicate theater

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.create(newProductRequest));
        assertTrue(thrown.getMessage().contains(DbBash.DUPLICATE_THEATER_REQUEST));
        verify(productRepository, never()).saveAndFlush(any(Product.class));
    }

    @Test
    void create_ThrowsRuntimeException_WhenServiceFails() {
        when(productionCompanyService.getProductionCompanyById(anyString())).thenThrow(new RuntimeException("PC not found"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.create(newProductRequest));
        assertTrue(thrown.getMessage().contains(ApiBash.CREATE_PRODUCT_FAILED));
        assertTrue(thrown.getMessage().contains("PC not found"));
    }

    @Test
    void getAll_Success_WithCustomPaginationAndSorting() {
        SearchProductRequest searchRequest = new SearchProductRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("id");
        searchRequest.setReleaseDateMin("2023-01-01");
        searchRequest.setReleaseDateMax("2024-01-01");
        searchRequest.setBudgetMin(100L);
        searchRequest.setBudgetMax(200L);
        searchRequest.setDurationMin(100L);
        searchRequest.setDurationMax(200L);
        searchRequest.setImdbRatingMin(4.5);
        searchRequest.setImdbRatingMax(5.0);
        searchRequest.setRottenTomatoesRatingMin(50);
        searchRequest.setRottenTomatoesRatingMax(100);
        searchRequest.setLastUpdatedMin("2023-01-01");
        searchRequest.setLastUpdatedMax("2024-01-01");
        searchRequest.setProductPricingMin(100.0);
        searchRequest.setProductPricingMax(200.0);

        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);
        when(productArtistService.toProductArtistResponse(any(ProductArtist.class))).thenReturn(productArtistResponse1); // Mock toProductArtistResponse if called

        Page<ProductResponse> response = productService.getAll(searchRequest);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals("Test Movie", response.getContent().get(0).getTitle());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber()); // Page 1 becomes index 0
        assertEquals(10, capturedPageable.getPageSize());
        assertEquals("id: ASC", capturedPageable.getSort().toString()); // Default sort
    }

    @Test
    void getAll_ThrowsException_OnInvalidDateRange() {
        SearchProductRequest searchRequest = new SearchProductRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("id");
        searchRequest.setReleaseDateMin("2024-01-01");
        searchRequest.setReleaseDateMax("2023-01-01"); // Max before Min

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2024-01-01")).thenReturn(LocalDate.of(2024, 1, 1));
            mockedDateUtil.when(() -> DateUtil.parseDate("2023-01-01")).thenReturn(LocalDate.of(2023, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.getAll(searchRequest));
            assertTrue(thrown.getMessage().contains(DbBash.MIN_MAX_INVALID));
            verify(productRepository, never()).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    void getAll_ThrowsException_OnInvalidDurationRange() {
        SearchProductRequest searchRequest = new SearchProductRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("id");
        searchRequest.setDurationMin(200L);
        searchRequest.setDurationMax(100L); // Max less than Min

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.getAll(searchRequest));
        assertTrue(thrown.getMessage().contains(DbBash.MIN_MAX_INVALID));
    }

    @Test
    void getAll_ThrowsException_OnInvalidPricingRange() {
        SearchProductRequest searchRequest = new SearchProductRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("id");
        searchRequest.setProductPricingMin(200.0);
        searchRequest.setProductPricingMax(100.0); // Max less than Min

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.getAll(searchRequest));
        assertTrue(thrown.getMessage().contains(DbBash.MIN_MAX_INVALID));
    }

    @Test
    void getAll_ThrowsException_OnInvalidIMDBRatingRange() {
        SearchProductRequest searchRequest = new SearchProductRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("id");
        searchRequest.setImdbRatingMin(5.0);
        searchRequest.setImdbRatingMax(4.5); // Max less than Min

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.getAll(searchRequest));
        assertTrue(thrown.getMessage().contains(DbBash.MIN_MAX_INVALID));
    }

    @Test
    void getAll_ThrowsException_OnInvalidRTRatingRange() {
        SearchProductRequest searchRequest = new SearchProductRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("id");
        searchRequest.setRottenTomatoesRatingMin(100);
        searchRequest.setRottenTomatoesRatingMax(50); // Max less than Min

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.getAll(searchRequest));
        assertTrue(thrown.getMessage().contains(DbBash.MIN_MAX_INVALID));
    }

    @Test
    void getAll_ThrowsException_OnInvalidLastUpdatedRange() {
        SearchProductRequest searchRequest = new SearchProductRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("id");
        searchRequest.setLastUpdatedMin("2024-01-01");
        searchRequest.setLastUpdatedMax("2023-01-01"); // Max before Min

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2024-01-01")).thenReturn(LocalDate.of(2024, 1, 1));
            mockedDateUtil.when(() -> DateUtil.parseDate("2023-01-01")).thenReturn(LocalDate.of(2023, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.getAll(searchRequest));
            assertTrue(thrown.getMessage().contains(DbBash.MIN_MAX_INVALID));
            verify(productRepository, never()).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    void getAll_ThrowsException_OnInvalidBudgetRange() {
        SearchProductRequest searchRequest = new SearchProductRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("id");
        searchRequest.setBudgetMin(100L);
        searchRequest.setBudgetMax(50L); // Max less than Min

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.getAll(searchRequest));
        assertTrue(thrown.getMessage().contains(DbBash.MIN_MAX_INVALID));
    }

    @Test
    void getAll_ThrowsRuntimeException_WhenRepositoryFails() {
        SearchProductRequest searchRequest = new SearchProductRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("id");
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("DB Error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.getAll(searchRequest));
        assertTrue(thrown.getMessage().contains(ApiBash.GET_ALL_PRODUCT_FAILED));
        assertTrue(thrown.getMessage().contains("DB Error"));
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        Product result = productService.getProductById("p1");
        assertNotNull(result);
        assertEquals("p1", result.getId());
    }

    @Test
    void getProductById_NotFound() {
        when(productRepository.findById("nonExistentId")).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.getProductById("nonExistentId"));
        assertTrue(thrown.getMessage().contains(DbBash.PRODUCT_NOT_FOUND));
    }

    @Test
    void getById_Success() {
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(productArtistService.toProductArtistResponse(any(ProductArtist.class))).thenReturn(productArtistResponse1);

        ProductResponse response = productService.getById("p1");
        assertNotNull(response);
        assertEquals("p1", response.getId());
        assertEquals("Test Movie", response.getTitle());
    }

    @Test
    void getById_NotFound() {
        when(productRepository.findById("nonExistentId")).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.getById("nonExistentId"));
        assertTrue(thrown.getMessage().contains(ApiBash.GET_PRODUCT_FAILED));
        assertTrue(thrown.getMessage().contains(DbBash.PRODUCT_NOT_FOUND));
    }

    @Test
    void testUpdateProduct_Success() {
        String productId = "f4d713d0-7391-4aee-b9f5-4edb753f7bef";
        Product oldProduct = Product.builder()
            .id(productId)
            .title("Old Title")
            .productionCompany(ProductionCompany.builder().id("company-123").build())
            .movieGenre(new ArrayList<>())
            .build();

        NewProductRequest request = new NewProductRequest();
        request.setTitle("New Title");
        request.setDuration(90L);
        request.setLanguage("English");
        request.setCountry("United States");
        request.setReleaseDate("2025-06-01");
        request.setPosterUrl("poster.jpg");
        request.setTrailerUrl("trailer.mp4");
        request.setRated("PG-13");
        request.setBudget(10000000L);
        request.setSynopsis("New synopsis");
        request.setTagline("New tagline");
        request.setImdbRating(8.0);
        request.setRottenTomatoesRating(90);
        request.setProductionCompanyId("company-123");
        request.setMovieGenre(List.of("Action", "Comedy"));

        NewProductArtistRequest artistRequest = new NewProductArtistRequest();
        artistRequest.setProductId(productId);
        artistRequest.setArtistId("artist-1");
        artistRequest.setArtistTypes(List.of("ACTOR"));
        request.setProductArtistRequests(List.of(artistRequest));
        request.setShowingOnTheaters(List.of("theater-1"));

        when(productRepository.findById(productId)).thenReturn(Optional.of(oldProduct));

        ProductionCompany newCompany = ProductionCompany.builder().id("company-123").build();
        when(productionCompanyService.getProductionCompanyById("company-123")).thenReturn(newCompany);

        when(movieGenreService.create(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(artistService.getArtistById("artist-1")).thenReturn(Artist.builder().id("artist-1").build());

        when(productArtistService.getProductArtistByProductIdAndArtistId(any(), any())).thenReturn(null);
        when(productArtistService.create(any())).thenReturn(
            ProductArtistResponse.builder().id("pa-1").artistId("artist-1").build()
        );
        when(productArtistService.getProductArtistById("pa-1")).thenReturn(
            ProductArtist.builder().id("pa-1").build()
        );

        Theater theater = Theater.builder().id("theater-1").build();
        when(theaterRepository.findById("theater-1")).thenReturn(Optional.of(theater));

        when(productRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.update(productId, request);

        assertEquals("New Title", response.getTitle());
        verify(productRepository).saveAndFlush(any());
        verify(movieGenreService, times(2)).create(any());
        verify(productionCompanyRepository).save(newCompany);
        verify(theaterRepository).saveAllAndFlush(any());
    }

    @Test
    void testUpdateProduct_TheaterNotFound_ThrowsException() {
        String productId = "f4d713d0-7391-4aee-b9f5-4edb753f7bef";
        Product product = Product.builder()
            .id(productId)
            .title("Old Title")
            .movieGenre(new ArrayList<>())
            .build();

        NewProductRequest request = new NewProductRequest();
        request.setProductionCompanyId("company-123");
        request.setMovieGenre(new ArrayList<>());
        request.setProductArtistRequests(new ArrayList<>());
        request.setShowingOnTheaters(List.of("invalid-theater"));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productionCompanyService.getProductionCompanyById(any())).thenReturn(
            ProductionCompany.builder().id("company-123").build()
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.update(productId, request);
        });

        assertTrue(exception.getMessage().contains(ApiBash.UPDATE_PRODUCT_FAILED));
    }

    @Test
    void testUpdateProduct_ProductIdMismatch_ThrowsException() {
        String productId = "f4d713d0-7391-4aee-b9f5-4edb753f7bef";
        Product product = Product.builder()
            .id(productId)
            .title("Old Title")
            .movieGenre(new ArrayList<>())
            .build();

        NewProductRequest request = new NewProductRequest();
        request.setProductionCompanyId("company-123");
        request.setMovieGenre(new ArrayList<>());

        NewProductArtistRequest artistRequest = new NewProductArtistRequest();
        artistRequest.setProductId("mismatched-id"); // ID tidak cocok
        artistRequest.setArtistId("artist-1");
        artistRequest.setArtistTypes(List.of("ACTOR"));

        request.setProductArtistRequests(List.of(artistRequest));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productionCompanyService.getProductionCompanyById(any())).thenReturn(
            ProductionCompany.builder().id("company-123").build()
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.update(productId, request);
        });

        assertTrue(exception.getMessage().contains(ApiBash.UPDATE_PRODUCT_FAILED));
    }

    @Test
    void hardDelete_Success() {
        doNothing().when(productRepository).deleteById(anyString());
        when(productRepository.findById(anyString())).thenReturn(Optional.of(product));
        when(productArtistService.toProductArtistResponse(any(ProductArtist.class))).thenReturn(productArtistResponse1);

        assertDoesNotThrow(() -> productService.hardDelete("p1"));
        verify(productRepository, times(1)).deleteById("p1");
    }

    @Test
    void hardDelete_ThrowsException_WhenProductNotFound() {
        when(productRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.hardDelete("nonExistentId"));
        assertTrue(thrown.getMessage().contains(ApiBash.HARD_DELETE_PRODUCT_FAILED));
        assertTrue(thrown.getMessage().contains(DbBash.PRODUCT_NOT_FOUND));
    }

    @Test
    void softDelete_Success() {
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productArtistService.toProductArtistResponse(any(ProductArtist.class))).thenReturn(productArtistResponse1);


        assertDoesNotThrow(() -> productService.softDelete("p1"));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).saveAndFlush(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();
        assertEquals("p1", capturedProduct.getId());
        assertEquals(LocalDate.now(), capturedProduct.getLastUpdated());
    }

    @Test
    void softDelete_ThrowsException_WhenProductNotFound() {
        when(productRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.softDelete("nonExistentId"));
        assertTrue(thrown.getMessage().contains(ApiBash.SOFT_DELETE_PRODUCT_FAILED));
        assertTrue(thrown.getMessage().contains(DbBash.PRODUCT_NOT_FOUND));
    }
}
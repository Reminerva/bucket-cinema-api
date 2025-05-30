package com.flix.flix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.global_exception_handler.GlobalExceptionHandler;
import com.flix.flix.model.request.NewProductArtistRequest;
import com.flix.flix.model.request.NewProductRequest;
import com.flix.flix.model.request.search.SearchProductRequest;
import com.flix.flix.model.response.ProductResponse;
import com.flix.flix.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private NewProductRequest newProductRequest;
    private NewProductArtistRequest newProductArtistRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        newProductArtistRequest = new NewProductArtistRequest();
        newProductArtistRequest.setArtistId("1");
        newProductArtistRequest.setProductId("1");
        newProductArtistRequest.setArtistTypes(new ArrayList<>());

        newProductRequest = new NewProductRequest();
        newProductRequest.setTitle("Title");
        newProductRequest.setDuration(60L);
        newProductRequest.setLanguage("English");
        newProductRequest.setCountry("United States");
        newProductRequest.setReleaseDate("2022-01-01");
        newProductRequest.setPosterUrl("https://example.com/poster.jpg");
        newProductRequest.setTrailerUrl("https://example.com/trailer.mp4");
        newProductRequest.setRated("PG-13");
        newProductRequest.setBudget(1000000L);
        newProductRequest.setSynopsis("Synopsis");
        newProductRequest.setTagline("Tagline");
        newProductRequest.setImdbRating(0.0);
        newProductRequest.setRottenTomatoesRating(0);
        newProductRequest.setProductionCompanyId("1");
        newProductRequest.setMovieGenre(List.of("Action", "Adventure"));
        newProductRequest.setProductArtistRequests(List.of(newProductArtistRequest));
        newProductRequest.setShowingOnTheaters(List.of("Theater 1", "Theater 2"));

    }

    @Test
    void testCreate() throws Exception {

        when(productService.create(any(NewProductRequest.class))).thenReturn(new ProductResponse());

        mockMvc.perform(post(ApiBash.PRODUCT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProductRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.CREATE_PRODUCT_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(productService).create(any(NewProductRequest.class));
    }

    @Test
    void testGetAll() throws Exception {
        when(productService.getAll(any(SearchProductRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.PRODUCT))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_PRODUCT_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).getAll(any(SearchProductRequest.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(productService.update(any(String.class), any(NewProductRequest.class))).thenReturn(new ProductResponse());

        mockMvc.perform(put(ApiBash.PRODUCT + "/{id}", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProductRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.UPDATE_PRODUCT_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).update(any(String.class), any(NewProductRequest.class));
    }

    @Test
    void testSoftDelete() throws Exception {
        doNothing().when(productService).softDelete(any(String.class));

        mockMvc.perform(delete(ApiBash.PRODUCT + "/{id}" + ApiBash.SOFT_DELETE, "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.SOFT_DELETE_PRODUCT_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).softDelete(any(String.class));
    }

    @Test
    void testHardDelete() throws Exception {
        doNothing().when(productService).hardDelete(any(String.class));

        mockMvc.perform(delete(ApiBash.PRODUCT + "/{id}" + ApiBash.HARD_DELETE, "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.HARD_DELETE_PRODUCT_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).hardDelete(any(String.class));
    }

    @Test
    void testGetById() throws Exception {
        when(productService.getById(any(String.class))).thenReturn(new ProductResponse());

        mockMvc.perform(get(ApiBash.PRODUCT + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_PRODUCT_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).getById(any(String.class));
    }
}

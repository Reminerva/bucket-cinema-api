package com.flix.flix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.global_exception_handler.GlobalExceptionHandler;
import com.flix.flix.model.request.NewProductionCompanyRequest;
import com.flix.flix.model.request.search.SearchProductionCompanyRequest;
import com.flix.flix.model.response.ProductionCompanyResponse;
import com.flix.flix.service.ProductionCompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

class ProductionCompanyControllerTest {

    @Mock
    private ProductionCompanyService productionCompanyService;

    @InjectMocks
    private ProductionCompanyController productionCompanyController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private NewProductionCompanyRequest newProductionCompanyRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productionCompanyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        newProductionCompanyRequest = new NewProductionCompanyRequest();
        newProductionCompanyRequest.setName("name");
        newProductionCompanyRequest.setLogoUrl("logoUrl");
        newProductionCompanyRequest.setOriginCountry("originCountry");
        newProductionCompanyRequest.setWebsiteUrl("websiteUrl");
        newProductionCompanyRequest.setHeadquarters("headquarters");
        newProductionCompanyRequest.setCeo("ceo");
        newProductionCompanyRequest.setDescription("description");
        newProductionCompanyRequest.setContactEmail("contactEmail");
        newProductionCompanyRequest.setContactNumber("contactNumber");
        newProductionCompanyRequest.setFoundedYear("foundedYear");

    }

    @Test
    void testCreate() throws Exception {

        when(productionCompanyService.create(any(NewProductionCompanyRequest.class))).thenReturn(new ProductionCompanyResponse());

        mockMvc.perform(post(ApiBash.PRODUCTION_COMPANY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProductionCompanyRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.CREATE_PRODUCTION_COMPANY_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(productionCompanyService).create(any(NewProductionCompanyRequest.class));
    }

    @Test
    void testGetAll() throws Exception {
        when(productionCompanyService.getAll(any(SearchProductionCompanyRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.PRODUCTION_COMPANY))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_PRODUCTION_COMPANY_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productionCompanyService).getAll(any(SearchProductionCompanyRequest.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(productionCompanyService.update(any(String.class), any(NewProductionCompanyRequest.class))).thenReturn(new ProductionCompanyResponse());

        mockMvc.perform(put(ApiBash.PRODUCTION_COMPANY + "/{id}", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProductionCompanyRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.UPDATE_PRODUCTION_COMPANY_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productionCompanyService).update(any(String.class), any(NewProductionCompanyRequest.class));
    }

    @Test
    void testSoftDelete() throws Exception {
        doNothing().when(productionCompanyService).delete(any(String.class));

        mockMvc.perform(delete(ApiBash.PRODUCTION_COMPANY + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.DELETE_PRODUCTION_COMPANY_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productionCompanyService).delete(any(String.class));
    }

    @Test
    void testGetById() throws Exception {
        when(productionCompanyService.getById(any(String.class))).thenReturn(new ProductionCompanyResponse());

        mockMvc.perform(get(ApiBash.PRODUCTION_COMPANY + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_PRODUCTION_COMPANY_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productionCompanyService).getById(any(String.class));
    }
}

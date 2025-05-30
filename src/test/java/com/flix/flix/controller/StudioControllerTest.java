package com.flix.flix.controller;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.global_exception_handler.GlobalExceptionHandler;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.request.search.SearchSeatLayoutRequest;
import com.flix.flix.model.request.search.SearchStudioRequest;
import com.flix.flix.model.response.StudioResponse;
import com.flix.flix.service.StudioService;

import jakarta.servlet.http.HttpServletRequest;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudioControllerTest {

    @Mock
    private StudioService studioService;

    @InjectMocks
    private StudioController studioController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private NewStudioRequest newStudioRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        newStudioRequest = new NewStudioRequest();
        newStudioRequest.setName("name");
        newStudioRequest.setStudioSize("studioSize");
        newStudioRequest.setSeatLayout(List.of("seatLayout"));
        newStudioRequest.setTheaterId("theaterId");

    }

    @Test
    void testCreateStudio() throws Exception {
        when(studioService.create(any(NewStudioRequest.class))).thenReturn(new StudioResponse());

        mockMvc.perform(post(ApiBash.STUDIO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudioRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.CREATE_STUDIO_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(studioService).create(any(NewStudioRequest.class));
    }

    @Test
    void testDeleteStudio() throws Exception {
        doNothing().when(studioService).softDelete(any(String.class));

        mockMvc.perform(delete(ApiBash.STUDIO + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.DELETE_STUDIO_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(studioService).softDelete(any(String.class));
    }

    @Test
    void testGetAllStudio() throws Exception {
        when(studioService.getAll(any(SearchStudioRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.STUDIO))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_STUDIO_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(studioService).getAll(any(SearchStudioRequest.class));
    }

    @Test
    void testGetAllStudio_ifSuccess() throws Exception {
        SearchSeatLayoutRequest searchSeatLayoutRequest = new SearchSeatLayoutRequest();
        searchSeatLayoutRequest.setSeatLayout(List.of("seatLayout"));
        when(studioService.getAll(any(SearchStudioRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.STUDIO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SearchStudioRequest())))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_STUDIO_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(studioService).getAll(any(SearchStudioRequest.class));
    }

    @Test
    void testGetMyStudios() throws Exception {
        when(studioService.getByCredentials(any(HttpServletRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.STUDIO + "/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SearchStudioRequest())))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_STUDIO_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(studioService).getByCredentials(any(HttpServletRequest.class));
    }

    @Test
    void testGetStudioById() throws Exception {
        when(studioService.getById(any(String.class))).thenReturn(new StudioResponse());

        mockMvc.perform(get(ApiBash.STUDIO + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_STUDIO_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(studioService).getById(any(String.class));
    }

    @Test
    void testGetStudiosByProductIdAndTheaterId() throws Exception {
        when(studioService.getByProductIdAndTheaterId(any(String.class), any(String.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.STUDIO + "/{theaterId}/{productId}", "theaterId", "productId"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_STUDIO_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(studioService).getByProductIdAndTheaterId(any(String.class), any(String.class));
    }

    @Test
    void testUpdateStudio() throws Exception {
        when(studioService.update(any(String.class), any(NewStudioRequest.class))).thenReturn(new StudioResponse());

        mockMvc.perform(put(ApiBash.STUDIO + "/{id}", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudioRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.UPDATE_STUDIO_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(studioService).update(any(String.class), any(NewStudioRequest.class));
    }
}

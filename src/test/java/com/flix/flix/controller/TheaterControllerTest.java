package com.flix.flix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.global_exception_handler.GlobalExceptionHandler;
import com.flix.flix.model.request.NewTheaterRequest;
import com.flix.flix.model.request.search.SearchTheaterRequest;
import com.flix.flix.model.response.TheaterResponse;
import com.flix.flix.service.TheaterService;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TheaterControllerTest {

    @Mock
    private TheaterService theaterService;

    @InjectMocks
    private TheaterController theaterController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private NewTheaterRequest newTheaterRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(theaterController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        newTheaterRequest = new NewTheaterRequest();
        newTheaterRequest.setName("name");
        newTheaterRequest.setAddress("address");
        newTheaterRequest.setCity("city");
        newTheaterRequest.setContactEmail("contactEmail");
        newTheaterRequest.setContactNumber("contactNumber");

    }

    @Test
    void testCreate() throws Exception {

        when(theaterService.create(any(NewTheaterRequest.class))).thenReturn(new TheaterResponse());

        mockMvc.perform(post(ApiBash.THEATER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTheaterRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.CREATE_THEATER_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(theaterService).create(any(NewTheaterRequest.class));
    }

    @Test
    void testGetAll() throws Exception {
        when(theaterService.getAll(any(SearchTheaterRequest.class), any(HttpServletRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.THEATER))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_THEATER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(theaterService).getAll(any(SearchTheaterRequest.class), any(HttpServletRequest.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(theaterService.update(any(String.class), any(NewTheaterRequest.class))).thenReturn(new TheaterResponse());

        mockMvc.perform(put(ApiBash.THEATER + "/{id}", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTheaterRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.UPDATE_THEATER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(theaterService).update(any(String.class), any(NewTheaterRequest.class));
    }

    @Test
    void testSoftDelete() throws Exception {
        doNothing().when(theaterService).softDelete(any(String.class));

        mockMvc.perform(delete(ApiBash.THEATER + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.SOFT_DELETE_THEATER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(theaterService).softDelete(any(String.class));
    }

    @Test
    void testGetById() throws Exception {
        when(theaterService.getById(any(String.class))).thenReturn(new TheaterResponse());

        mockMvc.perform(get(ApiBash.THEATER + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_THEATER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(theaterService).getById(any(String.class));
    }

    @Test
    void testRefreshAllSeat() throws Exception {
        when(theaterService.refreshAllSeat(any(String.class))).thenReturn(new TheaterResponse());

        mockMvc.perform(put(ApiBash.THEATER + "/{id}" + ApiBash.REFRESH_ALL_SEAT, "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.REFRESH_ALL_SEAT_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(theaterService).refreshAllSeat(any(String.class));
    }
}

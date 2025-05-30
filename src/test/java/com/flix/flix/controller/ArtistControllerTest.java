package com.flix.flix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.global_exception_handler.GlobalExceptionHandler;
import com.flix.flix.model.request.NewArtistRequest;
import com.flix.flix.model.request.search.SearchArtistRequest;
import com.flix.flix.model.response.ArtistResponse;
import com.flix.flix.service.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

class ArtistControllerTest {

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private ArtistController artistController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private NewArtistRequest newArtistRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(artistController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        newArtistRequest = new NewArtistRequest();
        newArtistRequest.setName("name");
        newArtistRequest.setPlaceOfBirth("placeOfBirth");
        newArtistRequest.setBirthDate("birthDate");
        newArtistRequest.setOtherName("otherName");
        newArtistRequest.setBio("bio");
        newArtistRequest.setArtistTypes(List.of("actor"));
    }

    @Test
    void testCreate() throws Exception {

        when(artistService.create(any(NewArtistRequest.class))).thenReturn(new ArtistResponse());

        mockMvc.perform(post(ApiBash.ARTIST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newArtistRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.CREATE_ARTIST_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(artistService).create(any(NewArtistRequest.class));
    }

    @Test
    void testGetAll() throws Exception {
        when(artistService.getAll(any(SearchArtistRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.ARTIST))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_ARTIST_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(artistService).getAll(any(SearchArtistRequest.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(artistService.update(any(String.class), any(NewArtistRequest.class))).thenReturn(new ArtistResponse());

        mockMvc.perform(put(ApiBash.ARTIST + "/{id}", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newArtistRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.UPDATE_ARTIST_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(artistService).update(any(String.class), any(NewArtistRequest.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(artistService).delete(any(String.class));

        mockMvc.perform(delete(ApiBash.ARTIST + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.DELETE_ARTIST_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(artistService).delete(any(String.class));
    }

    @Test
    void testGetById() throws Exception {
        when(artistService.getById(any(String.class))).thenReturn(new ArtistResponse());

        mockMvc.perform(get(ApiBash.ARTIST + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ARTIST_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(artistService).getById(any(String.class));
    }
}

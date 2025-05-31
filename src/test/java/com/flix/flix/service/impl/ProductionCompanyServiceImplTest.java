package com.flix.flix.service.impl;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ECountry;
import com.flix.flix.entity.ProductionCompany;
import com.flix.flix.model.request.NewProductionCompanyRequest;
import com.flix.flix.model.request.search.SearchProductionCompanyRequest;
import com.flix.flix.model.response.ProductionCompanyResponse;
import com.flix.flix.repository.ProductionCompanyRepository;
import com.flix.flix.specification.ProductionCompanySpecification;
import com.flix.flix.util.DateUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
public class ProductionCompanyServiceImplTest {

    // --- Mocks ---
    @Mock // Creates a mock instance of ProductionCompanyRepository
    private ProductionCompanyRepository productionCompanyRepository;

    @InjectMocks // Injects the mocks into ProductionCompanyServiceImpl
    private ProductionCompanyServiceImpl productionCompanyService;

    // --- Test Data ---
    private ProductionCompany testProductionCompany;
    private NewProductionCompanyRequest newProductionCompanyRequest;
    private SearchProductionCompanyRequest searchProductionCompanyRequest;

    // --- Setup Method ---
    @BeforeEach // Runs before each test method
    void setUp() {
        testProductionCompany = ProductionCompany.builder()
                .id("pc-id-123")
                .name("Flix Studios")
                .logoUrl("http://example.com/logo.png")
                .originCountry(ECountry.COUNTRY_UNITED_STATES)
                .websiteUrl("http://flixstudios.com")
                .headquarters("Hollywood, CA")
                .ceo("Jane Doe")
                .description("A leading entertainment company.")
                .contactEmail("contact@flixstudios.com")
                .contactNumber("123-456-7890")
                .foundedYear(LocalDate.of(2000, 1, 1))
                .createdAt(LocalDate.of(2023, 1, 1))
                .updatedAt(LocalDate.of(2023, 1, 1))
                .build();

        newProductionCompanyRequest = NewProductionCompanyRequest.builder()
                .name("New Production Co")
                .logoUrl("http://newco.com/logo.png")
                .originCountry(ECountry.COUNTRY_CANADA.getDescription()) // Use description for request
                .websiteUrl("http://newco.com")
                .headquarters("Vancouver, BC")
                .ceo("John Smith")
                .description("New Canadian studio.")
                .contactEmail("info@newco.com")
                .contactNumber("987-654-3210")
                .foundedYear("2010-05-15") // String for request
                .build();

        searchProductionCompanyRequest = SearchProductionCompanyRequest.builder()
                .page(0)
                .size(0)
                .sortBy("name")
                .direction("asc")
                .build();
    }

    // --- Create Tests ---
    @Test
    void create_shouldReturnProductionCompanyResponse_whenSuccessful() {
        when(productionCompanyRepository.saveAndFlush(any(ProductionCompany.class)))
                .thenReturn(testProductionCompany); // Return the test company for success

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<ECountry> mockedECountry = mockStatic(ECountry.class)) {

            mockedDateUtil.when(() -> DateUtil.parseDate(anyString()))
                    .thenReturn(LocalDate.of(2010, 5, 15)); // Mock for newProductionCompanyRequest's date

            mockedECountry.when(() -> ECountry.findByDescription(anyString()))
                    .thenReturn(ECountry.COUNTRY_CANADA); // Mock for ECountry.findByDescription

            ProductionCompanyResponse response = productionCompanyService.create(newProductionCompanyRequest);

            assertNotNull(response);
            assertEquals(testProductionCompany.getId(), response.getId());
            assertEquals(testProductionCompany.getName(), response.getName());
            assertEquals(testProductionCompany.getLogoUrl(), response.getLogoUrl());
            assertEquals(testProductionCompany.getOriginCountry().getDescription(), response.getOriginCountry()); // Compare descriptions
            assertEquals(testProductionCompany.getFoundedYear().toString(), response.getFoundedYear());

            verify(productionCompanyRepository, times(1)).saveAndFlush(any(ProductionCompany.class));
            mockedDateUtil.verify(() -> DateUtil.parseDate(anyString()), times(1));
            mockedECountry.verify(() -> ECountry.findByDescription(anyString()), times(1));
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(productionCompanyRepository.saveAndFlush(any(ProductionCompany.class)))
                .thenThrow(new RuntimeException("Database error"));

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<ECountry> mockedECountry = mockStatic(ECountry.class)) {

            mockedDateUtil.when(() -> DateUtil.parseDate(anyString()))
                    .thenReturn(LocalDate.of(2010, 5, 15));

            mockedECountry.when(() -> ECountry.findByDescription(anyString()))
                    .thenReturn(ECountry.COUNTRY_CANADA);

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productionCompanyService.create(newProductionCompanyRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.CREATE_PRODUCTION_COMPANY_FAILED));
            assertTrue(thrown.getMessage().contains("Database error"));

            verify(productionCompanyRepository, times(1)).saveAndFlush(any(ProductionCompany.class));
            mockedDateUtil.verify(() -> DateUtil.parseDate(anyString()), times(1));
            mockedECountry.verify(() -> ECountry.findByDescription(anyString()), times(1));
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenDateParsingFails() {
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(anyString()))
                    .thenThrow(new IllegalArgumentException("Invalid date format"));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productionCompanyService.create(newProductionCompanyRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.CREATE_PRODUCTION_COMPANY_FAILED));
            assertTrue(thrown.getMessage().contains("Invalid date format"));

            mockedDateUtil.verify(() -> DateUtil.parseDate(anyString()), times(1));
            verifyNoInteractions(productionCompanyRepository); // No save should happen
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenOriginCountryNotFound() {
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<ECountry> mockedECountry = mockStatic(ECountry.class)) {

            mockedDateUtil.when(() -> DateUtil.parseDate(anyString()))
                    .thenReturn(LocalDate.of(2010, 5, 15));

            mockedECountry.when(() -> ECountry.findByDescription(anyString()))
                    .thenThrow(new IllegalArgumentException("Country not found"));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productionCompanyService.create(newProductionCompanyRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.CREATE_PRODUCTION_COMPANY_FAILED));
            assertTrue(thrown.getMessage().contains("Country not found"));

            mockedDateUtil.verify(() -> DateUtil.parseDate(anyString()), times(0));
            mockedECountry.verify(() -> ECountry.findByDescription(anyString()), times(1));
            verifyNoInteractions(productionCompanyRepository);
        }
    }

    // --- GetById Tests ---
    @Test
    void getById_shouldReturnProductionCompanyResponse_whenFound() {
        when(productionCompanyRepository.findById(testProductionCompany.getId()))
                .thenReturn(Optional.of(testProductionCompany));

        ProductionCompanyResponse response = productionCompanyService.getById(testProductionCompany.getId());

        assertNotNull(response);
        assertEquals(testProductionCompany.getId(), response.getId());
        assertEquals(testProductionCompany.getName(), response.getName());
        assertEquals(testProductionCompany.getOriginCountry().getDescription(), response.getOriginCountry());

        verify(productionCompanyRepository, times(1)).findById(testProductionCompany.getId());
    }

    @Test
    void getById_shouldThrowRuntimeException_whenNotFound() {
        when(productionCompanyRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productionCompanyService.getById("non-existent-id"));

        assertEquals(ApiBash.GET_PRODUCTION_COMPANY_FAILED + ": " + DbBash.PRODUCTION_COMPANY_NOT_FOUND, thrown.getMessage());

        verify(productionCompanyRepository, times(1)).findById(anyString());
    }

    // --- GetProductionCompanyById Tests (internal helper method) ---
    @Test
    void getProductionCompanyById_shouldReturnProductionCompany_whenFound() {
        when(productionCompanyRepository.findById(testProductionCompany.getId()))
                .thenReturn(Optional.of(testProductionCompany));

        ProductionCompany foundCompany = productionCompanyService.getProductionCompanyById(testProductionCompany.getId());

        assertNotNull(foundCompany);
        assertEquals(testProductionCompany.getId(), foundCompany.getId());

        verify(productionCompanyRepository, times(1)).findById(testProductionCompany.getId());
    }

    @Test
    void getProductionCompanyById_shouldThrowRuntimeException_whenNotFound() {
        when(productionCompanyRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productionCompanyService.getProductionCompanyById("non-existent-id"));

        assertEquals(DbBash.PRODUCTION_COMPANY_NOT_FOUND, thrown.getMessage());

        verify(productionCompanyRepository, times(1)).findById(anyString());
    }

    // --- Update Tests ---
    @Test
    void update_shouldReturnProductionCompanyResponse_whenSuccessful() {
        NewProductionCompanyRequest updateRequest = NewProductionCompanyRequest.builder()
                .name("Updated Name")
                .logoUrl("http://updated.com/logo.png")
                .originCountry(ECountry.COUNTRY_GERMANY.getDescription())
                .websiteUrl("http://updated.com")
                .headquarters("London, UK")
                .ceo("Updated CEO")
                .description("Updated description.")
                .contactEmail("updated@email.com")
                .contactNumber("098-765-4321")
                .foundedYear("2005-10-20")
                .build();

        when(productionCompanyRepository.findById(testProductionCompany.getId()))
                .thenReturn(Optional.of(testProductionCompany)); // Return the original company to be updated

        when(productionCompanyRepository.saveAndFlush(any(ProductionCompany.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Return the argument passed to save

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<ECountry> mockedECountry = mockStatic(ECountry.class)) {

            mockedDateUtil.when(() -> DateUtil.parseDate(updateRequest.getFoundedYear()))
                    .thenReturn(LocalDate.of(2005, 10, 20));

            mockedECountry.when(() -> ECountry.findByDescription(updateRequest.getOriginCountry()))
                    .thenReturn(ECountry.COUNTRY_GERMANY);

            ProductionCompanyResponse response = productionCompanyService.update(testProductionCompany.getId(), updateRequest);

            assertNotNull(response);
            assertEquals(testProductionCompany.getId(), response.getId()); // ID should remain the same
            assertEquals(updateRequest.getName(), response.getName());
            assertEquals(updateRequest.getLogoUrl(), response.getLogoUrl());
            assertEquals(updateRequest.getOriginCountry(), response.getOriginCountry());
            assertEquals(updateRequest.getFoundedYear(), response.getFoundedYear());

            assertEquals(updateRequest.getName(), testProductionCompany.getName());
            assertEquals(updateRequest.getLogoUrl(), testProductionCompany.getLogoUrl());
            assertEquals(ECountry.COUNTRY_GERMANY, testProductionCompany.getOriginCountry());
            assertEquals(LocalDate.of(2005, 10, 20), testProductionCompany.getFoundedYear());
            assertNotNull(testProductionCompany.getUpdatedAt()); // Should be updated to LocalDate.now() in service

            verify(productionCompanyRepository, times(1)).findById(testProductionCompany.getId());
            verify(productionCompanyRepository, times(1)).saveAndFlush(testProductionCompany); // Verify save was called with the modified object
            mockedDateUtil.verify(() -> DateUtil.parseDate(updateRequest.getFoundedYear()), times(1));
            mockedECountry.verify(() -> ECountry.findByDescription(updateRequest.getOriginCountry()), times(1));
        }
    }

    @Test
    void update_shouldThrowRuntimeException_whenCompanyNotFound() {
        when(productionCompanyRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productionCompanyService.update("non-existent-id", newProductionCompanyRequest));

        assertEquals(ApiBash.UPDATE_PRODUCTION_COMPANY_FAILED + ": " + DbBash.PRODUCTION_COMPANY_NOT_FOUND, thrown.getMessage());

        verify(productionCompanyRepository, times(1)).findById(anyString());
        verify(productionCompanyRepository, never()).saveAndFlush(any(ProductionCompany.class));
    }

    @Test
    void update_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(productionCompanyRepository.findById(testProductionCompany.getId()))
                .thenReturn(Optional.of(testProductionCompany));

        when(productionCompanyRepository.saveAndFlush(any(ProductionCompany.class)))
                .thenThrow(new RuntimeException("Update DB error"));

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<ECountry> mockedECountry = mockStatic(ECountry.class)) {

            mockedDateUtil.when(() -> DateUtil.parseDate(anyString()))
                    .thenReturn(LocalDate.now());
            mockedECountry.when(() -> ECountry.findByDescription(anyString()))
                    .thenReturn(ECountry.COUNTRY_UNITED_STATES);

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productionCompanyService.update(testProductionCompany.getId(), newProductionCompanyRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.UPDATE_PRODUCTION_COMPANY_FAILED));
            assertTrue(thrown.getMessage().contains("Update DB error"));

            verify(productionCompanyRepository, times(1)).findById(testProductionCompany.getId());
            verify(productionCompanyRepository, times(1)).saveAndFlush(any(ProductionCompany.class));
        }
    }

    // --- Delete Tests ---
    @Test
    void delete_shouldCompleteSuccessfully_whenFound() {
        when(productionCompanyRepository.findById(testProductionCompany.getId()))
                .thenReturn(Optional.of(testProductionCompany));

        doNothing().when(productionCompanyRepository).deleteById(testProductionCompany.getId());

        assertDoesNotThrow(() -> productionCompanyService.delete(testProductionCompany.getId()));

        verify(productionCompanyRepository, times(1)).findById(testProductionCompany.getId());
        verify(productionCompanyRepository, times(1)).deleteById(testProductionCompany.getId());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenNotFound() {
        when(productionCompanyRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productionCompanyService.delete("non-existent-id"));

        assertEquals(ApiBash.DELETE_PRODUCTION_COMPANY_FAILED + ": " + DbBash.PRODUCTION_COMPANY_NOT_FOUND, thrown.getMessage());

        verify(productionCompanyRepository, times(1)).findById(anyString());
        verify(productionCompanyRepository, never()).deleteById(anyString()); // Delete should not be called
    }

    @Test
    void delete_shouldThrowRuntimeException_whenRepositoryDeleteFails() {
        when(productionCompanyRepository.findById(testProductionCompany.getId()))
                .thenReturn(Optional.of(testProductionCompany));

        doThrow(new RuntimeException("Delete DB error")).when(productionCompanyRepository).deleteById(testProductionCompany.getId());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productionCompanyService.delete(testProductionCompany.getId()));

        assertTrue(thrown.getMessage().contains(ApiBash.DELETE_PRODUCTION_COMPANY_FAILED));
        assertTrue(thrown.getMessage().contains("Delete DB error"));

        verify(productionCompanyRepository, times(1)).findById(testProductionCompany.getId());
        verify(productionCompanyRepository, times(1)).deleteById(testProductionCompany.getId());
    }

    // --- GetAll Tests ---
    @Test
    void getAll_shouldReturnPageOfProductionCompanyResponses_whenFound() {
        searchProductionCompanyRequest.setPage(1);
        searchProductionCompanyRequest.setSize(10);
        searchProductionCompanyRequest.setCreatedAtMin("2022-01-01");
        searchProductionCompanyRequest.setCreatedAtMax("2023-01-01");
        searchProductionCompanyRequest.setUpdatedAtMin("2022-01-01");
        searchProductionCompanyRequest.setUpdatedAtMax("2023-01-01");
        searchProductionCompanyRequest.setFoundedYearMin("2022-01-01");
        searchProductionCompanyRequest.setFoundedYearMax("2023-01-01");

        List<ProductionCompany> productionCompanies = Arrays.asList(
                testProductionCompany,
                ProductionCompany.builder()
                        .id("pc-id-456")
                        .name("Another Studio")
                        .originCountry(ECountry.COUNTRY_JAPAN)
                        .foundedYear(LocalDate.of(1995, 3, 10))
                        .createdAt(LocalDate.of(2023, 1, 1))
                        .updatedAt(LocalDate.of(2023, 1, 1))
                        .build()
        );
        Page<ProductionCompany> productionCompanyPage = new PageImpl<>(productionCompanies,
                PageRequest.of(searchProductionCompanyRequest.getPage() - 1, searchProductionCompanyRequest.getSize(),
                        Sort.by(Sort.Direction.ASC, "name")),
                productionCompanies.size());

        when(productionCompanyRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(productionCompanyPage);

        try (MockedStatic<ProductionCompanySpecification> mockedSpecification = mockStatic(ProductionCompanySpecification.class)) {
            mockedSpecification.when(() -> ProductionCompanySpecification.getSpecification(any(SearchProductionCompanyRequest.class)))
                    .thenReturn(mock(Specification.class)); // Return a dummy Specification mock

            Page<ProductionCompanyResponse> responsePage = productionCompanyService.getAll(searchProductionCompanyRequest);

            assertNotNull(responsePage);
            assertEquals(2, responsePage.getTotalElements());
            assertEquals("Flix Studios", responsePage.getContent().get(0).getName());
            assertEquals("Another Studio", responsePage.getContent().get(1).getName());

            verify(productionCompanyRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedSpecification.verify(() -> ProductionCompanySpecification.getSpecification(any(SearchProductionCompanyRequest.class)), times(1));
        }
    }

    @Test
    void getAll_shouldReturnEmptyPage_whenNoCompaniesExist() {
        searchProductionCompanyRequest.setPage(1);
        searchProductionCompanyRequest.setSize(10);

        Page<ProductionCompany> emptyPage = new PageImpl<>(Collections.emptyList(),
                PageRequest.of(searchProductionCompanyRequest.getPage() - 1, searchProductionCompanyRequest.getSize(),
                        Sort.by(Sort.Direction.ASC, "name")), 0);
        when(productionCompanyRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        try (MockedStatic<ProductionCompanySpecification> mockedSpecification = mockStatic(ProductionCompanySpecification.class)) {
            mockedSpecification.when(() -> ProductionCompanySpecification.getSpecification(any(SearchProductionCompanyRequest.class)))
                    .thenReturn(mock(Specification.class));

            Page<ProductionCompanyResponse> responsePage = productionCompanyService.getAll(searchProductionCompanyRequest);

            assertNotNull(responsePage);
            assertTrue(responsePage.isEmpty());
            assertEquals(0, responsePage.getTotalElements());

            verify(productionCompanyRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedSpecification.verify(() -> ProductionCompanySpecification.getSpecification(any(SearchProductionCompanyRequest.class)), times(1));
        }
    }

    @Test
    void getAll_shouldHandleInvalidMinMaxFoundedYearAndThrowException() {
        searchProductionCompanyRequest.setFoundedYearMin("2020-01-01");
        searchProductionCompanyRequest.setFoundedYearMax("2019-01-01"); // Invalid

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2020-01-01")).thenReturn(LocalDate.of(2020, 1, 1));
            mockedDateUtil.when(() -> DateUtil.parseDate("2019-01-01")).thenReturn(LocalDate.of(2019, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productionCompanyService.getAll(searchProductionCompanyRequest));

            assertEquals(ApiBash.GET_ALL_PRODUCTION_COMPANY_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());

            mockedDateUtil.verify(() -> DateUtil.parseDate(anyString()), times(2));
            verifyNoInteractions(productionCompanyRepository);
        }
    }

    @Test
    void getAll_shouldHandleInvalidMinMaxCreatedAtAndThrowException() {
        searchProductionCompanyRequest.setCreatedAtMin("2020-01-01");
        searchProductionCompanyRequest.setCreatedAtMax("2019-01-01"); // Invalid

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2020-01-01")).thenReturn(LocalDate.of(2020, 1, 1));
            mockedDateUtil.when(() -> DateUtil.parseDate("2019-01-01")).thenReturn(LocalDate.of(2019, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productionCompanyService.getAll(searchProductionCompanyRequest));

            assertEquals(ApiBash.GET_ALL_PRODUCTION_COMPANY_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());

            mockedDateUtil.verify(() -> DateUtil.parseDate(anyString()), times(2));
            verifyNoInteractions(productionCompanyRepository);
        }
    }

    @Test
    void getAll_shouldHandleInvalidMinMaxUpdatedAtAndThrowException() {
        searchProductionCompanyRequest.setUpdatedAtMin("2020-01-01");
        searchProductionCompanyRequest.setUpdatedAtMax("2019-01-01"); // Invalid

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2020-01-01")).thenReturn(LocalDate.of(2020, 1, 1));
            mockedDateUtil.when(() -> DateUtil.parseDate("2019-01-01")).thenReturn(LocalDate.of(2019, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productionCompanyService.getAll(searchProductionCompanyRequest));

            assertEquals(ApiBash.GET_ALL_PRODUCTION_COMPANY_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());

            mockedDateUtil.verify(() -> DateUtil.parseDate(anyString()), times(2));
            verifyNoInteractions(productionCompanyRepository);
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        when(productionCompanyRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("DB findAll error"));

        try (MockedStatic<ProductionCompanySpecification> mockedSpecification = mockStatic(ProductionCompanySpecification.class)) {
            mockedSpecification.when(() -> ProductionCompanySpecification.getSpecification(any(SearchProductionCompanyRequest.class)))
                    .thenReturn(mock(Specification.class));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productionCompanyService.getAll(searchProductionCompanyRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.GET_ALL_PRODUCTION_COMPANY_FAILED));
            assertTrue(thrown.getMessage().contains("DB findAll error"));

            verify(productionCompanyRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedSpecification.verify(() -> ProductionCompanySpecification.getSpecification(any(SearchProductionCompanyRequest.class)), times(1));
        }
    }
}
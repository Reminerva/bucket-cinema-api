package com.flix.flix.service.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ECountry;
import com.flix.flix.entity.ProductionCompany;
import com.flix.flix.model.request.NewProductionCompanyRequest;
import com.flix.flix.model.request.search.SearchProductionCompanyRequest;
import com.flix.flix.model.response.ProductionCompanyResponse;
import com.flix.flix.repository.ProductionCompanyRepository;
import com.flix.flix.service.ProductionCompanyService;
import com.flix.flix.specification.ProductionCompanySpecification;
import com.flix.flix.util.DateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductionCompanyServiceImpl implements ProductionCompanyService {
    
    private final ProductionCompanyRepository productionCompanyRepository;
    
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductionCompanyResponse create(NewProductionCompanyRequest productionCompanyRequest) {
        try {
            ProductionCompany productionCompany = ProductionCompany.builder()
                .name(productionCompanyRequest.getName())
                .logoUrl(productionCompanyRequest.getLogoUrl())
                .originCountry(ECountry.findByDescription(productionCompanyRequest.getOriginCountry()))
                .websiteUrl(productionCompanyRequest.getWebsiteUrl())
                .headquarters(productionCompanyRequest.getHeadquarters())
                .ceo(productionCompanyRequest.getCeo())
                .description(productionCompanyRequest.getDescription())
                .contactEmail(productionCompanyRequest.getContactEmail())
                .contactNumber(productionCompanyRequest.getContactNumber())
                .foundedYear(DateUtil.parseDate(productionCompanyRequest.getFoundedYear()))
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

            return toProductionCompanyResponse(productionCompanyRepository.saveAndFlush(productionCompany));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<ProductionCompanyResponse> getAll(SearchProductionCompanyRequest searchProductionCompanyRequest) {
        try {
            if (searchProductionCompanyRequest.getPage() <= 0 || searchProductionCompanyRequest.getSize() <= 0) {
                searchProductionCompanyRequest.setPage(1);
                searchProductionCompanyRequest.setSize(10);
            }
            Sort sort = Sort.by(Sort.Direction.fromString(searchProductionCompanyRequest.getDirection()), searchProductionCompanyRequest.getSortBy());
            Pageable pageable = PageRequest.of(searchProductionCompanyRequest.getPage() - 1, searchProductionCompanyRequest.getSize(), sort);
            Specification<ProductionCompany> specification = ProductionCompanySpecification.getSpecification(searchProductionCompanyRequest);
            return productionCompanyRepository.findAll(specification, pageable).map(this::toProductionCompanyResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductionCompanyResponse getById(String id) {
        try {
            return toProductionCompanyResponse(getProductionCompanyById(id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductionCompany getProductionCompanyById(String id) {
        Optional<ProductionCompany> productionCompany = productionCompanyRepository.findById(id);
        if (productionCompany.isEmpty()) throw new RuntimeException(DbBash.PRODUCTION_COMPANY_NOT_FOUND);
        return productionCompany.get();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductionCompanyResponse update(String id, NewProductionCompanyRequest productionCompanyRequest) {
        try {
            ProductionCompany productionCompany = getProductionCompanyById(id);

            productionCompany.setName(productionCompanyRequest.getName());
            productionCompany.setLogoUrl(productionCompanyRequest.getLogoUrl());
            productionCompany.setOriginCountry(ECountry.findByDescription(productionCompanyRequest.getOriginCountry()));
            productionCompany.setWebsiteUrl(productionCompanyRequest.getWebsiteUrl());
            productionCompany.setHeadquarters(productionCompanyRequest.getHeadquarters());
            productionCompany.setCeo(productionCompanyRequest.getCeo());
            productionCompany.setDescription(productionCompanyRequest.getDescription());
            productionCompany.setContactEmail(productionCompanyRequest.getContactEmail());
            productionCompany.setContactNumber(productionCompanyRequest.getContactNumber());
            productionCompany.setFoundedYear(DateUtil.parseDate(productionCompanyRequest.getFoundedYear()));
            productionCompany.setUpdatedAt(LocalDate.now());

            return toProductionCompanyResponse(productionCompanyRepository.saveAndFlush(productionCompany));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        try {
            getProductionCompanyById(id);
            productionCompanyRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ProductionCompanyResponse toProductionCompanyResponse(ProductionCompany productionCompany) {
        return ProductionCompanyResponse.builder()
            .id(productionCompany.getId())
            .name(productionCompany.getName())
            .logoUrl(productionCompany.getLogoUrl())
            .originCountry(productionCompany.getOriginCountry().getDescription())
            .websiteUrl(productionCompany.getWebsiteUrl())
            .headquarters(productionCompany.getHeadquarters())
            .ceo(productionCompany.getCeo())
            .description(productionCompany.getDescription())
            .contactEmail(productionCompany.getContactEmail())
            .contactNumber(productionCompany.getContactNumber())
            .foundedYear(productionCompany.getFoundedYear().toString())
            .createdAt(productionCompany.getCreatedAt().toString())
            .updatedAt(productionCompany.getUpdatedAt().toString())
            .build();
    }

}

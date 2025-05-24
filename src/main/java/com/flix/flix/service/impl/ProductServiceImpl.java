package com.flix.flix.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EArtistType;
import com.flix.flix.constant.custom_enum.ECountry;
import com.flix.flix.constant.custom_enum.EGenre;
import com.flix.flix.constant.custom_enum.ELanguage;
import com.flix.flix.constant.custom_enum.ERated;
import com.flix.flix.entity.Artist;
import com.flix.flix.entity.MovieGenre;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductionCompany;
import com.flix.flix.entity.Theater;
import com.flix.flix.model.request.NewProductRequest;
import com.flix.flix.model.request.search.SearchProductRequest;
import com.flix.flix.model.response.ProductResponse;
import com.flix.flix.repository.ArtistRepository;
import com.flix.flix.repository.ProductRepository;
import com.flix.flix.repository.ProductionCompanyRepository;
import com.flix.flix.repository.TheaterRepository;
import com.flix.flix.service.ArtistService;
import com.flix.flix.service.MovieGenreService;
import com.flix.flix.service.ProductService;
import com.flix.flix.service.ProductionCompanyService;
import com.flix.flix.specification.ProductSpecification;
import com.flix.flix.util.DateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MovieGenreService movieGenreService;
    private final ArtistService artistService;
    private final ArtistRepository artistRepository;
    private final ProductionCompanyService productionCompanyService;
    private final ProductionCompanyRepository productionCompanyRepository;
    private final TheaterRepository theaterRepository;


    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductResponse create(NewProductRequest productRequest) {
        try {
            validateProductRequest(productRequest);
            Product product = Product.builder()
                .title(productRequest.getTitle())
                .duration(productRequest.getDuration())
                .language(ELanguage.findByDescription(productRequest.getLanguage()))
                .country(ECountry.findByDescription(productRequest.getCountry()))
                .releaseDate(DateUtil.parseDate(productRequest.getReleaseDate()))
                .posterUrl(productRequest.getPosterUrl())
                .trailerUrl(productRequest.getTrailerUrl())
                .rated(ERated.findByDescription(productRequest.getRated()))
                .budget(productRequest.getBudget())
                .synopsis(productRequest.getSynopsis())
                .tagline(productRequest.getTagline())
                .imdbRating(productRequest.getImdbRating())
                .rottenTomatoesRating(productRequest.getRottenTomatoesRating())
                .lastUpdated(LocalDate.now())
                .build();

            List<Product> hasProduct = new ArrayList<>();
            hasProduct.addAll(productionCompanyService.getProductionCompanyById(productRequest.getProductionCompanyId()).getHasProduct());
            hasProduct.add(product);
            productionCompanyService.getProductionCompanyById(productRequest.getProductionCompanyId()).setHasProduct(hasProduct);
            product.setProductionCompany(productionCompanyService.getProductionCompanyById(productRequest.getProductionCompanyId()));

            productRepository.saveAndFlush(product);

            List<MovieGenre> genres = productRequest.getMovieGenre().stream()
                .map(movieGenre -> movieGenreService.create(
                    MovieGenre.builder()
                        .product(product)
                        .genre(EGenre.findByDescription(movieGenre))
                        .build()))
                .toList();
            
            List<Artist> artists = productRequest.getArtistId().stream().map(artistId -> artistService.findById(artistId)).toList();
            product.getArtists().addAll(artists);

            product.getMovieGenre().addAll(genres);

            return toProductResponse(productRepository.saveAndFlush(product));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<ProductResponse> getAll(SearchProductRequest searchProductRequest) {
        try {
            if (searchProductRequest.getPage() <= 0 || searchProductRequest.getSize() <= 0) {
                searchProductRequest.setPage(1);
                searchProductRequest.setSize(10);
            }
            if (searchProductRequest.getReleaseDateMin() != null && searchProductRequest.getReleaseDateMax() != null) {
                if (DateUtil.parseDate(searchProductRequest.getReleaseDateMin()).isAfter(DateUtil.parseDate(searchProductRequest.getReleaseDateMax()))) {
                    throw new RuntimeException(DbBash.MIN_MAX_INVALID);
                }
            }
            if (searchProductRequest.getLastUpdatedMin() != null && searchProductRequest.getLastUpdatedMax() != null) {
                if (DateUtil.parseDate(searchProductRequest.getLastUpdatedMin()).isAfter(DateUtil.parseDate(searchProductRequest.getLastUpdatedMax()))) {
                    throw new RuntimeException(DbBash.MIN_MAX_INVALID);
                }
            }
            if (searchProductRequest.getBudgetMin() != null && searchProductRequest.getBudgetMax() != null) {
                if (searchProductRequest.getBudgetMin() > searchProductRequest.getBudgetMax()) {
                    throw new RuntimeException(DbBash.MIN_MAX_INVALID);
                }
            }
            if (searchProductRequest.getDurationMin() != null && searchProductRequest.getDurationMax() != null) {
                if (searchProductRequest.getDurationMin() > searchProductRequest.getDurationMax()) {
                    throw new RuntimeException(DbBash.MIN_MAX_INVALID);
                }
            }
            if (searchProductRequest.getImdbRatingMin() != null && searchProductRequest.getImdbRatingMax() != null) {
                if (searchProductRequest.getImdbRatingMin() > searchProductRequest.getImdbRatingMax()) {
                    throw new RuntimeException(DbBash.MIN_MAX_INVALID);
                }
            }
            if (searchProductRequest.getRottenTomatoesRatingMin() != null && searchProductRequest.getRottenTomatoesRatingMax() != null) {
                if (searchProductRequest.getRottenTomatoesRatingMin() > searchProductRequest.getRottenTomatoesRatingMax()) {
                    throw new RuntimeException(DbBash.MIN_MAX_INVALID);
                }
            }
            if (searchProductRequest.getProductPricingMin() != null && searchProductRequest.getProductPricingMax() != null) {
                if (searchProductRequest.getProductPricingMin() > searchProductRequest.getProductPricingMax()) {
                    throw new RuntimeException(DbBash.MIN_MAX_INVALID);
                }
            }
            Sort sort = Sort.by(Sort.Direction.fromString(searchProductRequest.getDirection()), searchProductRequest.getSortBy());
            Pageable pageable = PageRequest.of(searchProductRequest.getPage() - 1, searchProductRequest.getSize(), sort);
            Specification<Product> specification = ProductSpecification.getSpecification(searchProductRequest);
            return productRepository.findAll(specification, pageable).map(this::toProductResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Product getProductById(String id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) throw new RuntimeException(DbBash.PRODUCT_NOT_FOUND);
        return product.get();
    }
    @Override
    public ProductResponse getById(String id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) throw new RuntimeException(DbBash.PRODUCT_NOT_FOUND);
        return toProductResponse(product.get());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductResponse update(String id, NewProductRequest productRequest) {
        try {
            validateProductRequest(productRequest);
            // Pastikan produk ditemukan
            Product product_ = getProductById(id);

            // Cari ProductionCompany baru
            ProductionCompany newProductionCompany = productionCompanyService.getProductionCompanyById(productRequest.getProductionCompanyId());

            // Hapus produk dari ProductionCompany lama jika ada
            if (product_.getProductionCompany() != null) {
                product_.getProductionCompany().getHasProduct().remove(product_);
            }

            // Buat objek baru dengan data yang diperbarui
            Product updatedProduct = Product.builder()
                .id(id)
                .title(productRequest.getTitle())
                .duration(productRequest.getDuration())
                .language(ELanguage.findByDescription(productRequest.getLanguage()))
                .country(ECountry.findByDescription(productRequest.getCountry()))
                .releaseDate(DateUtil.parseDate(productRequest.getReleaseDate()))
                .posterUrl(productRequest.getPosterUrl())
                .trailerUrl(productRequest.getTrailerUrl())
                .rated(ERated.findByDescription(productRequest.getRated()))
                .budget(productRequest.getBudget())
                .synopsis(productRequest.getSynopsis())
                .tagline(productRequest.getTagline())
                .imdbRating(productRequest.getImdbRating())
                .rottenTomatoesRating(productRequest.getRottenTomatoesRating())
                .lastUpdated(LocalDate.now())
                .productionCompany(newProductionCompany)
                .build();

            product_.getMovieGenre().forEach(movieGenre -> movieGenreService.delete(movieGenre.getId()));
            
            updatedProduct.setMovieGenre(productRequest.getMovieGenre().stream()
                .map(movieGenre -> movieGenreService.create(MovieGenre.builder()
                    .product(updatedProduct)
                    .genre(EGenre.findByDescription(movieGenre))
                    .build()))
                .toList());

            List<Artist> artists = new ArrayList<>();
            if (updatedProduct.getArtists() == null) updatedProduct.setArtists(new ArrayList<>());
            if (productRequest.getArtistId() != null) {
                for (String artistId : productRequest.getArtistId()) {
                    Artist artist = artistService.findById(artistId);
                    if (!updatedProduct.containsArtist(artist)) {
                        artist.getInProduct().add(updatedProduct);
                        artists.add(artist);
                    }
                }
            }
            updatedProduct.setArtists(artists);
            artistRepository.saveAllAndFlush(artists);

            newProductionCompany.getHasProduct().add(updatedProduct);
            productionCompanyRepository.save(newProductionCompany);

            List<Theater> theaters = new ArrayList<>();
            if (updatedProduct.getTheaters() == null) updatedProduct.setTheaters(new ArrayList<>());
            if (productRequest.getShowingOnTheaters() != null) {
                for (String theaterId : productRequest.getShowingOnTheaters()) {
                    Optional<Theater> theaterOptional = theaterRepository.findById(theaterId);
                    if (theaterOptional.isEmpty()) throw new RuntimeException(DbBash.THEATER_NOT_FOUND);

                    Theater theater = theaterOptional.get();
                    if (!updatedProduct.getTheaters().contains(theater)) {
                        theater.getProducts().add(updatedProduct);
                        theaters.add(theater);
                    }
                }
            }
            updatedProduct.setTheaters(theaters);
            theaterRepository.saveAllAndFlush(theaters);

            return toProductResponse(productRepository.saveAndFlush(updatedProduct));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void hardDelete(String id) {
        try {
            getById(id);
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void softDelete(String id) {
        try {
            getById(id);
            Product product = productRepository.findById(id).get();
            product.setLastUpdated(LocalDate.now());
            productRepository.saveAndFlush(product);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ProductResponse toProductResponse(Product product) {
        try {
            List<ProductResponse.TheaterResponse> theaterResponseList = new ArrayList<>();
            if (product.getTheaters() != null && product.getTheaters().size() > 0) {
                for (Theater theater : product.getTheaters()) {
                    ProductResponse.TheaterResponse theaterResponse = ProductResponse.TheaterResponse.builder()
                        .id(theater.getId())
                        .name(theater.getName())
                        .city(theater.getCity())
                        .address(theater.getAddress())
                        .contactNumber(theater.getContactNumber())
                        .contactEmail(theater.getContactEmail())
                        .build();
                    theaterResponseList.add(theaterResponse);
                }
            }
            return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .duration(product.getDuration())
                .language(product.getLanguage().getDescription())
                .country(product.getCountry().getDescription())
                .releaseDate(product.getReleaseDate().toString())
                .posterUrl(product.getPosterUrl())
                .trailerUrl(product.getTrailerUrl())
                .rated(product.getRated().getDescription())
                .budget(product.getBudget())
                .synopsis(product.getSynopsis())
                .tagline(product.getTagline())
                .imdbRating(product.getImdbRating())
                .rottenTomatoesRating(product.getRottenTomatoesRating())
                .directorsId(getSecifiedsArtist(product, EArtistType.TYPE_DIRECTOR))
                .writersId(getSecifiedsArtist(product, EArtistType.TYPE_WRITER))
                .actorsId(getSecifiedsArtist(product, EArtistType.TYPE_ACTOR))
                .producersId(getSecifiedsArtist(product, EArtistType.TYPE_PRODUCER))
                .musicDirectorsId(getSecifiedsArtist(product, EArtistType.TYPE_MUSIC_DIRECTOR))
                .editorsId(getSecifiedsArtist(product, EArtistType.TYPE_EDITOR))
                .productionCompanyId(product.getProductionCompany() == null ? null : product.getProductionCompany().getId())
                .movieGenre(product.getMovieGenre().stream().map(movieGenre -> movieGenre.getGenre().getDescription()).toList())
                .lastUpdated(product.getLastUpdated().toString())
                .showingOnTheaters(theaterResponseList)
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validateProductRequest(NewProductRequest productRequest) {
        // cek duplikasi Artist
        if (productRequest.getArtistId().size() != new HashSet<>(productRequest.getArtistId()).size()) {
            throw new RuntimeException(DbBash.DUPLICATE_ARTIST_REQUEST);
        }

        // cek duplikasi Theater
        if (productRequest.getShowingOnTheaters().size() != new HashSet<>(productRequest.getShowingOnTheaters()).size()) {
            throw new RuntimeException(DbBash.DUPLICATE_THEATER_REQUEST);
        }
    }

    private List<String> getSecifiedsArtist(Product product, EArtistType artistType) {
        List<String> artistIds = new ArrayList<>();
        for (Artist artist : product.getArtists()) {
            if (artist.getArtistTypes().contains(artistType)) {
                artistIds.add(artist.getId());
            }
        }
        return artistIds;
    }
}

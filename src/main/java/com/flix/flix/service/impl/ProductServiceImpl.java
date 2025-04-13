package com.flix.flix.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.customEnum.ECountry;
import com.flix.flix.constant.customEnum.EGenre;
import com.flix.flix.constant.customEnum.ELanguage;
import com.flix.flix.constant.customEnum.ERated;
import com.flix.flix.entity.Artist;
import com.flix.flix.entity.MovieGenre;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductionCompany;
import com.flix.flix.model.request.NewProductRequest;
import com.flix.flix.model.response.ProductResponse;
import com.flix.flix.repository.ArtistRepository;
import com.flix.flix.repository.ProductRepository;
import com.flix.flix.repository.ProductionCompanyRepository;
import com.flix.flix.service.ArtistService;
import com.flix.flix.service.MovieGenreService;
import com.flix.flix.service.ProductService;
import com.flix.flix.service.ProductionCompanyService;
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

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductResponse create(NewProductRequest productRequest) {
        try {
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
                .director(productRequest.getDirector())
                .writer(productRequest.getWriter())
                .producer(productRequest.getProducer())
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
    public List<ProductResponse> getAll() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> toProductResponse(product)).toList();
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
                .director(productRequest.getDirector())
                .writer(productRequest.getWriter())
                .producer(productRequest.getProducer())
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

            return toProductResponse(productRepository.saveAndFlush(updatedProduct));
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
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
            .director(product.getDirector())
            .writer(product.getWriter())
            .producer(product.getProducer())
            .productionCompany(product.getProductionCompany().getId())
            .movieGenre(product.getMovieGenre().stream().map(movieGenre -> movieGenre.getGenre().getDescription()).toList())
            .lastUpdated(product.getLastUpdated().toString())
            .artistId(product.getArtists().stream().map(artist -> artist.getId()).toList())
            .build();
    }
}

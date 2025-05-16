package com.flix.flix.seeder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class FlixDataSeeder implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String ARTIST_CHECK_QUERY = "SELECT COUNT(*) FROM m_artist WHERE id = :id";
    private static final String PRODUCT_CHECK_QUERY = "SELECT COUNT(*) FROM m_product WHERE id = :id";
    private static final String PRODUCTION_COMPANY_CHECK_QUERY = "SELECT COUNT(*) FROM m_production_company WHERE id = :id";
    private static final String USER_CHECK_QUERY = "SELECT COUNT(*) FROM m_user WHERE id = :id";
    private static final String CUSTOMER_CHECK_QUERY = "SELECT COUNT(*) FROM m_customer WHERE id = :id";
    private static final String FAV_GENRE_CHECK_QUERY = "SELECT COUNT(*) FROM m_fav_genre WHERE id = :id";
    private static final String MOVIE_GENRE_CHECK_QUERY = "SELECT COUNT(*) FROM m_movie_genre WHERE id = :id";
    private static final String THEATER_CHECK_QUERY = "SELECT COUNT(*) FROM m_theater WHERE id = :id";
    private static final String STUDIO_CHECK_QUERY = "SELECT COUNT(*) FROM m_studio WHERE id = :id";
    private static final String AVAILABLE_SEAT_CHECK_QUERY = "SELECT COUNT(*) FROM studio_available_seat WHERE studio_id = :studio_id";
    private static final String PRODUCT_SCHEDULING_CHECK_QUERY = "SELECT COUNT(*) FROM m_product_scheduling WHERE id = :id";
    private static final String PRODUCT_PRICING_CHECK_QUERY = "SELECT COUNT(*) FROM m_product_pricing WHERE id = :id";
    private static final String PRODUCT_PRICING_SCHEDULING_CHECK_QUERY = "SELECT COUNT(*) FROM m_product_pricing_scheduling WHERE id = :id";

    private final PasswordEncoder passwordEncoder;

    public FlixDataSeeder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String defaultPassword = passwordEncoder.encode("password");

        // Insert ProductionCompany
        if (isDataAbsent(PRODUCTION_COMPANY_CHECK_QUERY, "a1b2c3d4-e5f6-7890-1234-567890abcdef")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_production_company (id, name, logo_url, origin_country, website_url, founded_year, contact_email, contact_number, headquarters, ceo, description, created_at, updated_at) VALUES " +
                "('a1b2c3d4-e5f6-7890-1234-567890abcdef', 'Marvel Studios', 'https://example.com/marvel_logo.png', 'COUNTRY_UNITED_STATES', 'https://www.marvel.com', '1993-09-08', 'contact@marvel.com', '+1-800-MARVEL', 'Burbank, California', 'Kevin Feige', 'American film and television production company.', NOW(), NOW())," +
                "('f9e8d7c6-b5a4-3210-fedc-ba9876543210', 'Walt Disney Pictures', 'https://example.com/disney_logo.png', 'COUNTRY_UNITED_STATES', 'https://www.disneystudios.com', '1923-10-16', 'contact@disney.com', '+1-800-DISNEY', 'Burbank, California', 'Bob Iger', 'American film production and distribution company.', NOW(), NOW())"
            ).executeUpdate();
        }

        // Insert Artist
        if (isDataAbsent(ARTIST_CHECK_QUERY, "11111111-2222-3333-4444-555555555555")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_artist (id, name, place_of_birth, birth_date, other_name, bio, artist_type) VALUES " +
                "('11111111-2222-3333-4444-555555555555', 'Robert Downey Jr.', 'New York City', '1965-04-04', NULL, 'American actor and producer.', 'TYPE_ACTOR')," +
                "('66666666-7777-8888-9999-000000000000', 'Scarlett Johansson', 'New York City', '1984-11-22', NULL, 'American actress.', 'TYPE_ACTOR')," +
                "('abcdef01-2345-6789-abcd-ef0123456789', 'Christopher Nolan', 'London', '1970-07-30', NULL, 'British-American film director, producer, and screenwriter.', 'TYPE_DIRECTOR')"
            ).executeUpdate();
        }

        // Insert Customer
        if (isDataAbsent(CUSTOMER_CHECK_QUERY, "cust-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_customer (id, fullname, country, phone_number, city, gender, registration_date, last_login) VALUES " +
                "('cust-001', 'Budi Santoso', 'Indonesia', '081234567890', 'Bandung', 'GENDER_MALE', NOW(), NOW())," +
                "('cust-002', 'Siti Aminah', 'Indonesia', '089876543210', 'Jakarta', 'GENDER_FEMALE', NOW(), NOW())," +
                "('cust-003', 'John Doe', 'USA', '+1-555-1234', 'New York', 'GENDER_MALE', NOW(), NOW())"
            ).executeUpdate();
        }

        // Insert AppUser
        if (isDataAbsent(USER_CHECK_QUERY, "user-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_user (id, username, email, password, role, customer_id) VALUES " +
                "('user-001', 'budi', 'budi@example.com', :password, ARRAY['ROLE_CUSTOMER'], 'cust-001')," +
                "('user-002', 'siti', 'siti@example.com', :password, ARRAY['ROLE_CUSTOMER'], 'cust-002')," +
                "('user-003', 'john', 'john@example.com', :password, ARRAY['ROLE_CUSTOMER'], 'cust-003')," +
                "('admin-001', 'admin', 'admin@flix.com', :password, ARRAY['ROLE_ADMIN'], NULL)"
            ).setParameter("password", defaultPassword).executeUpdate();
        }

        // Insert Product
        if (isDataAbsent(PRODUCT_CHECK_QUERY, "prod-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_product (id, title, duration, language, country, release_date, poster_url, trailer_url, rated, budget, synopsis, tagline, imdb_rating, rotten_tomatoes_rating, director, writer, producer, last_updated, production_company_id) VALUES " +
                "('prod-001', 'Avengers: Endgame', 181, 'LANGUAGE_ENGLISH', 'COUNTRY_UNITED_STATES', '2019-04-26', 'https://example.com/avengers_poster.png', 'https://example.com/avengers_trailer.mp4', 'RATED_PG_13', 356000000, 'The culmination of 22 interconnected films.', 'Part of the journey is the end.', 8.4, 94, 'Anthony Russo, Joe Russo', 'Christopher Markus, Stephen McFeely', 'Kevin Feige', NOW(), 'a1b2c3d4-e5f6-7890-1234-567890abcdef')," +
                "('prod-002', 'The Lion King', 118, 'LANGUAGE_ENGLISH', 'COUNTRY_UNITED_STATES', '1994-06-24', 'https://example.com/lionking_poster.png', 'https://example.com/lionking_trailer.mp4', 'RATED_G', 45000000, 'A young lion prince flees his kingdom only to learn the true meaning of responsibility and bravery.', 'Hakuna Matata.', 8.5, 93, 'Roger Allers, Rob Minkoff', 'Irene Mecchi, Jonathan Roberts, Linda Woolverton', 'Don Hahn', NOW(), 'f9e8d7c6-b5a4-3210-fedc-ba9876543210')," +
                "('prod-003', 'Inception', 148, 'LANGUAGE_ENGLISH', 'COUNTRY_UNITED_STATES', '2010-07-16', 'https://example.com/inception_poster.png', 'https://example.com/inception_trailer.mp4', 'RATED_PG_13', 160000000, 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 'Your mind is the scene of the crime.', 8.8, 87, 'Christopher Nolan', 'Christopher Nolan', 'Christopher Nolan, Emma Thomas', NOW(), NULL)"
            ).executeUpdate();
        }

        // Insert FavGenre
        if (isDataAbsent(FAV_GENRE_CHECK_QUERY, "fav-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_fav_genre (id, customer_id, fav_genre) VALUES " +
                "('fav-001', 'cust-001', 'GENRE_ACTION')," +
                "('fav-002', 'cust-001', 'GENRE_COMEDY')," +
                "('fav-003', 'cust-002', 'GENRE_DRAMA')," +
                "('fav-004', 'cust-003', 'GENRE_SCIENCE_FICTION')"
            ).executeUpdate();
        }

        // Insert MovieGenre
        if (isDataAbsent(MOVIE_GENRE_CHECK_QUERY, "mgenre-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_movie_genre (id, product_id, genre) VALUES " +
                "('mgenre-001', 'prod-001', 'GENRE_ACTION')," +
                "('mgenre-002', 'prod-001', 'GENRE_SCIENCE_FICTION')," +
                "('mgenre-003', 'prod-002', 'GENRE_ANIMATION')," +
                "('mgenre-004', 'prod-002', 'GENRE_ADVENTURE')," +
                "('mgenre-005', 'prod-003', 'GENRE_SCIENCE_FICTION')," +
                "('mgenre-006', 'prod-003', 'GENRE_THRILLER')"
            ).executeUpdate();
        }

        // Insert Theater
        if (isDataAbsent(THEATER_CHECK_QUERY, "theater-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_theater (id, name, city, address, contact_number, contact_email, created_at, updated_at, oprational_status) VALUES " +
                "('theater-001', 'CGV Bandung Electronic Center', 'Bandung', 'Jl. Purnawarman No.13-15', '022-82060901', 'bec@cgv.id', NOW(), NOW(), TRUE)," +
                "('theater-002', 'XXI Ciwalk', 'Bandung', 'Jl. Cihampelas Walk No.160', '022-2061021', 'ciwalk@xxi.co.id', NOW(), NOW(), TRUE)"
            ).executeUpdate();
        }

        // Insert Studio
        if (isDataAbsent(STUDIO_CHECK_QUERY, "studio-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_studio (id, name, studio_size, theater_id) VALUES " +
                "('studio-001', 'Studio 1', 'STUDIO_REGULER_SMALL', 'theater-001')," +
                "('studio-002', 'Studio 2', 'STUDIO_REGULER_MEDIUM', 'theater-001')," +
                "('studio-003', 'Studio 1', 'STUDIO_REGULER_SMALL', 'theater-002')"
            ).executeUpdate();
        }

        // Insert AvailableSeat for Studio
        if (isSeatDataAbsent(AVAILABLE_SEAT_CHECK_QUERY, "studio-001")) {
            entityManager.createNativeQuery("INSERT INTO studio_available_seat (studio_id, available_seat) VALUES " +
                "('studio-001', 'SEAT_A1'), ('studio-001', 'SEAT_A2'), ('studio-001', 'SEAT_B1'), ('studio-001', 'SEAT_B2')").executeUpdate();
        }
        if (isSeatDataAbsent(AVAILABLE_SEAT_CHECK_QUERY, "studio-002")) {
            entityManager.createNativeQuery("INSERT INTO studio_available_seat (studio_id, available_seat) VALUES " +
                "('studio-002', 'SEAT_C1'), ('studio-002', 'SEAT_C2'), ('studio-002', 'SEAT_D1')").executeUpdate();
        }
        if (isSeatDataAbsent(AVAILABLE_SEAT_CHECK_QUERY, "studio-003")) {
            entityManager.createNativeQuery("INSERT INTO studio_available_seat (studio_id, available_seat) VALUES " +
                "('studio-003', 'SEAT_E1'), ('studio-003', 'SEAT_E2')").executeUpdate();
        }

        // Insert ProductPricing
        if (isDataAbsent(PRODUCT_PRICING_CHECK_QUERY, "pprice-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_product_pricing (id, weekday_price, weekend_price, weekday_price_date, weekend_price_date, weekday_price_active, weekend_price_active, product_id) VALUES " +
                "('pprice-001', 50000.0, 75000.0, '2025-05-19', '2025-05-24', TRUE, TRUE, 'prod-001')," +
                "('pprice-002', 45000.0, 65000.0, '2025-05-19', '2025-05-24', TRUE, TRUE, 'prod-002')," +
                "('pprice-003', 55000.0, 80000.0, '2025-05-19', '2025-05-24', TRUE, TRUE, 'prod-003')"
            ).executeUpdate();
        }

        // Insert ProductScheduling
        if (isDataAbsent(PRODUCT_SCHEDULING_CHECK_QUERY, "psched-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_product_scheduling (id, schedule, product_id) VALUES " +
                "('psched-001', 'SCHEDULE_9_00', 'prod-001')," +
                "('psched-002', 'SCHEDULE_12_30', 'prod-001')," +
                "('psched-003', 'SCHEDULE_15_00', 'prod-002')," +
                "('psched-004', 'SCHEDULE_18_00', 'prod-003')"
            ).executeUpdate();
        }

        // Insert ProductPricingScheduling
        if (isDataAbsent(PRODUCT_PRICING_SCHEDULING_CHECK_QUERY, "ppsched-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO m_product_pricing_scheduling (id, product_pricing_id, product_id) VALUES " +
                "('ppsched-001', 'pprice-001', 'prod-001')," +
                "('ppsched-002', 'pprice-002', 'prod-002')," +
                "('ppsched-003', 'pprice-003', 'prod-003')"
            ).executeUpdate();
        }

        // Insert relations between ProductScheduling and ProductPricingScheduling
        if (isRelationAbsent("m_product_scheduling_product_pricing_scheduling", "product_scheduling_id", "psched-001", "product_pricing_scheduling_id", "ppsched-001")) {
            entityManager.createNativeQuery("INSERT INTO m_product_scheduling_product_pricing_scheduling (product_scheduling_id, product_pricing_scheduling_id) VALUES ('psched-001', 'ppsched-001')").executeUpdate();
        }
        if (isRelationAbsent("m_product_scheduling_product_pricing_scheduling", "product_scheduling_id", "psched-002", "product_pricing_scheduling_id", "ppsched-001")) {
            entityManager.createNativeQuery("INSERT INTO m_product_scheduling_product_pricing_scheduling (product_scheduling_id, product_pricing_scheduling_id) VALUES ('psched-002', 'ppsched-001')").executeUpdate();
        }
        if (isRelationAbsent("m_product_scheduling_product_pricing_scheduling", "product_scheduling_id", "psched-003", "product_pricing_scheduling_id", "ppsched-002")) {
            entityManager.createNativeQuery("INSERT INTO m_product_scheduling_product_pricing_scheduling (product_scheduling_id, product_pricing_scheduling_id) VALUES ('psched-003', 'ppsched-002')").executeUpdate();
        }
        if (isRelationAbsent("m_product_scheduling_product_pricing_scheduling", "product_scheduling_id", "psched-004", "product_pricing_scheduling_id", "ppsched-003")) {
            entityManager.createNativeQuery("INSERT INTO m_product_scheduling_product_pricing_scheduling (product_scheduling_id, product_pricing_scheduling_id) VALUES ('psched-004', 'ppsched-003')").executeUpdate();
        }

        // Insert relations between ProductPricingScheduling and Studio
        if (isRelationAbsent("m_studio_product_pricing_scheduling", "product_pricing_scheduling_id", "ppsched-001", "studios_id", "studio-001")) {
            entityManager.createNativeQuery("INSERT INTO m_studio_product_pricing_scheduling (product_pricing_scheduling_id, studios_id) VALUES ('ppsched-001', 'studio-001')").executeUpdate();
        }
        if (isRelationAbsent("m_studio_product_pricing_scheduling", "product_pricing_scheduling_id", "ppsched-001", "studios_id", "studio-002")) {
            entityManager.createNativeQuery("INSERT INTO m_studio_product_pricing_scheduling (product_pricing_scheduling_id, studios_id) VALUES ('ppsched-001', 'studio-002')").executeUpdate();
        }
        if (isRelationAbsent("m_studio_product_pricing_scheduling", "product_pricing_scheduling_id", "ppsched-002", "studios_id", "studio-001")) {
            entityManager.createNativeQuery("INSERT INTO m_studio_product_pricing_scheduling (product_pricing_scheduling_id, studios_id) VALUES ('ppsched-002', 'studio-001')").executeUpdate();
        }
        if (isRelationAbsent("m_studio_product_pricing_scheduling", "product_pricing_scheduling_id", "ppsched-003", "studios_id", "studio-003")) {
            entityManager.createNativeQuery("INSERT INTO m_studio_product_pricing_scheduling (product_pricing_scheduling_id, studios_id) VALUES ('ppsched-003', 'studio-003')").executeUpdate();
        }

        // Inserting relations between Product and Artist (example)
        if (isRelationAbsent("product_artist", "product_id", "prod-001", "artist_id", "11111111-2222-3333-4444-555555555555")) {
            entityManager.createNativeQuery("INSERT INTO product_artist (product_id, artist_id) VALUES ('prod-001', '11111111-2222-3333-4444-555555555555')").executeUpdate();
        }
        if (isRelationAbsent("product_artist", "product_id", "prod-001", "artist_id", "66666666-7777-8888-9999-000000000000")) {
            entityManager.createNativeQuery("INSERT INTO product_artist (product_id, artist_id) VALUES ('prod-001', '66666666-7777-8888-9999-000000000000')").executeUpdate();
        }
        if (isRelationAbsent("product_artist", "product_id", "prod-003", "artist_id", "abcdef01-2345-6789-abcd-ef0123456789")) {
            entityManager.createNativeQuery("INSERT INTO product_artist (product_id, artist_id) VALUES ('prod-003', 'abcdef01-2345-6789-abcd-ef0123456789')").executeUpdate();
        }
    }

    private boolean isDataAbsent(String query, String param) {
        Long count = (Long) entityManager.createNativeQuery(query)
            .setParameter("id", param)
            .getSingleResult();
        return count == 0;
    }

    private boolean isSeatDataAbsent(String query, String param) {
        Long count = (Long) entityManager.createNativeQuery(query)
            .setParameter("studio_id", param)
            .getSingleResult();
        return count == 0;
    }

    private boolean isRelationAbsent(String tableName, String column1Name, String column1Value, String column2Name, String column2Value) {
        Long count = (Long) entityManager.createNativeQuery(
            "SELECT COUNT(*) FROM " + tableName + " WHERE " + column1Name + " = :val1 AND " + column2Name + " = :val2"
        )
        .setParameter("val1", column1Value)
        .setParameter("val2", column2Value)
        .getSingleResult();
        return count == 0;
    }
}
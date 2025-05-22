package com.flix.flix.seeder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.flix.flix.constant.DbBash;

@Component
public class FlixDataSeeder implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String ARTIST_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.ARTIST_DB + " WHERE id = :id";
    private static final String ARTIST_TYPE_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.ARTIST_ARTIST_TYPE_DB + " WHERE artist_id = :artist_id";
    private static final String PRODUCT_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.PRODUCT_DB + " WHERE id = :id";
    private static final String PRODUCTION_COMPANY_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.PRODUCTION_COMPANY_DB + " WHERE id = :id";
    private static final String USER_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.USER_DB + " WHERE id = :id";
    private static final String ROLE_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.APP_USER_ROLE_DB + " WHERE app_user_id = :app_user_id";
    private static final String CUSTOMER_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.CUSTOMER_DB + " WHERE id = :id";
    private static final String EMPLOYEE_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.EMPLOYEE_DB + " WHERE id = :id";
    private static final String FAV_GENRE_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.FAV_GENRE_DB + " WHERE id = :id";
    private static final String MOVIE_GENRE_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.MOVIE_GENRE_DB + " WHERE id = :id";
    private static final String THEATER_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.THEATER_DB + " WHERE id = :id";
    private static final String STUDIO_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.STUDIO_DB + " WHERE id = :id";
    private static final String STUDIO_SEAT_SCHEDULE_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.STUDIO_SEAT_SCHEDULE_DB + " WHERE id = :id";
    private static final String AVAILABLE_SEAT_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.STUDIO_SEAT_SCHEDULE_AVAILABLE_SEAT_DB + " WHERE studio_seat_schedule_id = :studio_seat_schedule_id";
    private static final String SEAT_LAYOUT_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.STUDIO_SEAT_LAYOUT_DB + " WHERE studio_id = :studio_id";
    private static final String PRODUCT_SCHEDULING_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.PRODUCT_SCHEDULING_DB + " WHERE id = :id";
    private static final String PRODUCT_PRICING_CHECK_QUERY = "SELECT COUNT(*) FROM " + DbBash.PRODUCT_PRICING_DB + " WHERE id = :id";

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
                "INSERT INTO " + DbBash.PRODUCTION_COMPANY_DB + " (id, name, logo_url, origin_country, website_url, founded_year, contact_email, contact_number, headquarters, ceo, description, created_at, updated_at) VALUES " +
                "('a1b2c3d4-e5f6-7890-1234-567890abcdef', 'Marvel Studios', 'https://example.com/marvel_logo.png', 'COUNTRY_UNITED_STATES', 'https://www.marvel.com', '1993-09-08', 'contact@marvel.com', '+1-800-MARVEL', 'Burbank, California', 'Kevin Feige', 'American film and television production company.', NOW(), NOW())," +
                "('f9e8d7c6-b5a4-3210-fedc-ba9876543210', 'Walt Disney Pictures', 'https://example.com/disney_logo.png', 'COUNTRY_UNITED_STATES', 'https://www.disneystudios.com', '1923-10-16', 'contact@disney.com', '+1-800-DISNEY', 'Burbank, California', 'Bob Iger', 'American film production and distribution company.', NOW(), NOW())"
            ).executeUpdate();
        }

        // Insert Artist
        if (isDataAbsent(ARTIST_CHECK_QUERY, "11111111-2222-3333-4444-555555555555")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.ARTIST_DB + " (id, name, place_of_birth, birth_date, other_name, bio) VALUES " +
                "('11111111-2222-3333-4444-555555555555', 'Robert Downey Jr.', 'New York City', '1965-04-04', NULL, 'American actor and producer.')," +
                "('66666666-7777-8888-9999-000000000000', 'Scarlett Johansson', 'New York City', '1984-11-22', NULL, 'American actress.')," +
                "('abcdef01-2345-6789-abcd-ef0123456789', 'Christopher Nolan', 'London', '1970-07-30', NULL, 'British-American film director, producer, and screenwriter.')," +
                "('00000000-0000-0000-0000-000000000001', 'Chris Evans', 'Boston', '1981-06-13', NULL, 'American actor.')," +
                "('00000000-0000-0000-0000-000000000002', 'Mark Ruffalo', 'Kenosha', '1967-11-22', NULL, 'American actor and producer.')," +
                "('00000000-0000-0000-0000-000000000003', 'Russo Brothers', 'Cleveland', '1970-07-18', 'Anthony and Joe Russo', 'American film and television directors.')," + // Director for Endgame
                "('00000000-0000-0000-0000-000000000004', 'Jon Favreau', 'New York City', '1966-10-19', NULL, 'American actor, director, and producer.')," + // Director for Lion King
                "('00000000-0000-0000-0000-000000000005', 'Hans Zimmer', 'Frankfurt', '1957-09-12', NULL, 'German film score composer and record producer.')," + // Music Director for Lion King and Inception
                "('00000000-0000-0000-0000-000000000006', 'Pharrell Williams', 'Virginia Beach', '1973-04-05', NULL, 'American singer, rapper, songwriter, and record producer.')," + // Producer for Lion King
                "('00000000-0000-0000-0000-000000000007', 'Donald Glover', 'Edwards Air Force Base', '1983-09-25', 'Childish Gambino', 'American actor, singer, rapper, comedian, writer, and director.')," + // Actor for Lion King
                "('00000000-0000-0000-0000-000000000008', 'James Earl Jones', 'Arkabutla', '1931-01-17', NULL, 'American actor.')," + // Actor for Lion King
                "('00000000-0000-0000-0000-000000000009', 'Leonardo DiCaprio', 'Los Angeles', '1974-11-11', NULL, 'American actor and film producer.')," + // Actor for Inception
                "('00000000-0000-0000-0000-000000000010', 'Joseph Gordon-Levitt', 'Los Angeles', '1981-02-17', NULL, 'American actor and filmmaker.')," + // Actor for Inception
                "('00000000-0000-0000-0000-000000000011', 'Emma Thomas', 'London', '1971-12-09', NULL, 'English film producer, known for her collaborations with her husband, Christopher Nolan.')," + // Producer for Inception
                "('00000000-0000-0000-0000-000000000012', 'Lee Smith', 'Australia', '1960-03-22', NULL, 'Australian film editor.')" // Editor for Inception
            ).executeUpdate();
        }

        // Insert ArtistType for Artist
        if (isArtistTypeDataAbsent(ARTIST_TYPE_CHECK_QUERY, "11111111-2222-3333-4444-555555555555")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.ARTIST_ARTIST_TYPE_DB + " (artist_id, artist_type) VALUES " +
                "('11111111-2222-3333-4444-555555555555', 'TYPE_ACTOR')," + // Robert Downey Jr.
                "('11111111-2222-3333-4444-555555555555', 'TYPE_PRODUCER')," +
                "('66666666-7777-8888-9999-000000000000', 'TYPE_ACTOR')," + // Scarlett Johansson
                "('abcdef01-2345-6789-abcd-ef0123456789', 'TYPE_DIRECTOR')," + // Christopher Nolan
                "('abcdef01-2345-6789-abcd-ef0123456789', 'TYPE_WRITER')," +
                "('abcdef01-2345-6789-abcd-ef0123456789', 'TYPE_PRODUCER')," +
                "('00000000-0000-0000-0000-000000000001', 'TYPE_ACTOR')," + // Chris Evans
                "('00000000-0000-0000-0000-000000000002', 'TYPE_ACTOR')," + // Mark Ruffalo
                "('00000000-0000-0000-0000-000000000002', 'TYPE_PRODUCER')," +
                "('00000000-0000-0000-0000-000000000003', 'TYPE_DIRECTOR')," + // Russo Brothers
                "('00000000-0000-0000-0000-000000000004', 'TYPE_DIRECTOR')," + // Jon Favreau
                "('00000000-0000-0000-0000-000000000004', 'TYPE_ACTOR')," +
                "('00000000-0000-0000-0000-000000000004', 'TYPE_PRODUCER')," +
                "('00000000-0000-0000-0000-000000000005', 'TYPE_MUSIC_DIRECTOR')," + // Hans Zimmer
                "('00000000-0000-0000-0000-000000000006', 'TYPE_PRODUCER')," + // Pharrell Williams
                "('00000000-0000-0000-0000-000000000007', 'TYPE_ACTOR')," + // Donald Glover
                "('00000000-0000-0000-0000-000000000008', 'TYPE_ACTOR')," + // James Earl Jones
                "('00000000-0000-0000-0000-000000000009', 'TYPE_ACTOR')," + // Leonardo DiCaprio
                "('00000000-0000-0000-0000-000000000009', 'TYPE_PRODUCER')," +
                "('00000000-0000-0000-0000-000000000010', 'TYPE_ACTOR')," + // Joseph Gordon-Levitt
                "('00000000-0000-0000-0000-000000000011', 'TYPE_PRODUCER')," + // Emma Thomas
                "('00000000-0000-0000-0000-000000000012', 'TYPE_EDITOR')" // Lee Smith
            ).executeUpdate();
        }

        // Insert AppUser
        if (isDataAbsent(USER_CHECK_QUERY, "user-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.USER_DB + " (id, username, email, password) VALUES " +
                "('user-001', 'budi', 'budi@example.com', :password)," +
                "('user-002', 'siti', 'siti@example.com', :password)," +
                "('user-003', 'john', 'john@example.com', :password)" 
            ).setParameter("password", defaultPassword).executeUpdate();
        }

        // Insert Role for AppUser
        if (isRoleDataAbsent(ROLE_CHECK_QUERY, "user-001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.APP_USER_ROLE_DB + " (app_user_id, role) VALUES " +
                "('user-001', 'ROLE_CUSTOMER')," + 
                "('user-002', 'ROLE_CUSTOMER')," + 
                "('user-003', 'ROLE_CUSTOMER')").executeUpdate();
        }

        // Insert Customer
        if (isDataAbsent(CUSTOMER_CHECK_QUERY, "cust-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.CUSTOMER_DB + " (id, fullname, country, phone_number, city, gender, registration_date, last_login, app_user_id) VALUES " +
                "('cust-001', 'Budi Santoso', 'Indonesia', '081234567890', 'Bandung', 'GENDER_MALE', NOW(), NOW(), 'user-001')," +
                "('cust-002', 'Siti Aminah', 'Indonesia', '089876543210', 'Jakarta', 'GENDER_FEMALE', NOW(), NOW(), 'user-002')," +
                "('cust-003', 'John Doe', 'USA', '+1-555-1234', 'New York', 'GENDER_MALE', NOW(), NOW(), 'user-003')"
            ).executeUpdate();
        }

        // Insert Product
        if (isDataAbsent(PRODUCT_CHECK_QUERY, "prod-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.PRODUCT_DB + " (id, title, duration, language, country, release_date, poster_url, trailer_url, rated, budget, synopsis, tagline, imdb_rating, rotten_tomatoes_rating, last_updated, production_company_id) VALUES " +
                "('prod-001', 'Avengers: Endgame', 181, 'LANGUAGE_ENGLISH', 'COUNTRY_UNITED_STATES', '2019-04-26', 'https://example.com/avengers_poster.png', 'https://example.com/avengers_trailer.mp4', 'RATED_PG_13', 356000000, 'The culmination of 22 interconnected films.', 'Part of the journey is the end.', 8.4, 94, NOW(), 'a1b2c3d4-e5f6-7890-1234-567890abcdef')," +
                "('prod-002', 'The Lion King', 118, 'LANGUAGE_ENGLISH', 'COUNTRY_UNITED_STATES', '1994-06-24', 'https://example.com/lionking_poster.png', 'https://example.com/lionking_trailer.mp4', 'RATED_G', 45000000, 'A young lion prince flees his kingdom only to learn the true meaning of responsibility and bravery.', 'Hakuna Matata.', 8.5, 93, NOW(), 'f9e8d7c6-b5a4-3210-fedc-ba9876543210')," +
                "('prod-003', 'Inception', 148, 'LANGUAGE_ENGLISH', 'COUNTRY_UNITED_STATES', '2010-07-16', 'https://example.com/inception_poster.png', 'https://example.com/inception_trailer.mp4', 'RATED_PG_13', 160000000, 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 'Your mind is the scene of the crime.', 8.8, 87, NOW(), NULL)"
            ).executeUpdate();
        }

        // Insert FavGenre
        if (isDataAbsent(FAV_GENRE_CHECK_QUERY, "fav-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.FAV_GENRE_DB + " (id, customer_id, fav_genre) VALUES " +
                "('fav-001', 'cust-001', 'GENRE_ACTION')," +
                "('fav-002', 'cust-001', 'GENRE_COMEDY')," +
                "('fav-003', 'cust-002', 'GENRE_DRAMA')," +
                "('fav-004', 'cust-003', 'GENRE_SCIENCE_FICTION')"
            ).executeUpdate();
        }

        // Insert MovieGenre
        if (isDataAbsent(MOVIE_GENRE_CHECK_QUERY, "mgenre-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO "+ DbBash.MOVIE_GENRE_DB + " (id, product_id, genre) VALUES " +
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
                "INSERT INTO " + DbBash.THEATER_DB + " (id, name, city, address, contact_number, contact_email, created_at, updated_at, oprational_status) VALUES " +
                "('theater-001', 'CGV Bandung Electronic Center', 'Bandung', 'Jl. Purnawarman No.13-15', '022-82060901', 'bec@cgv.id', NOW(), NOW(), TRUE)," +
                "('theater-002', 'XXI Ciwalk', 'Bandung', 'Jl. Cihampelas Walk No.160', '022-2061021', 'ciwalk@xxi.co.id', NOW(), NOW(), TRUE)"
            ).executeUpdate();
        }

        // Insert AppUser Employee
        if (isDataAbsent(USER_CHECK_QUERY, "useremp-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.USER_DB + " (id, username, email, password) VALUES " +
                "('useremp-001', 'empName1', 'empName1@flix.com', :password)," +
                "('useremp-002', 'empName2', 'empName2@flix.com', :password)," +
                "('useremp-003', 'empName3', 'empName3@flix.com', :password)," +
                "('useremp-004', 'empName4', 'empName4@flix.com', :password)," +
                "('useremp-005', 'empName5', 'empName5@flix.com', :password)," +
                "('useremp-006', 'empName6', 'empName6@flix.com', :password)," +
                "('admin-001', 'admin', 'admin@flix.com', :password)"
            ).setParameter("password", defaultPassword).executeUpdate();
        }

        // Insert Role for AppUser
        if (isRoleDataAbsent(ROLE_CHECK_QUERY, "useremp-001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.APP_USER_ROLE_DB + " (app_user_id, role) VALUES " +
                "('useremp-001', 'ROLE_EMPLOYEE')," +
                "('useremp-002', 'ROLE_EMPLOYEE')," + 
                "('useremp-003', 'ROLE_EMPLOYEE')," +
                "('useremp-004', 'ROLE_EMPLOYEE')," +
                "('useremp-005', 'ROLE_CASHIER')," +
                "('useremp-006', 'ROLE_CASHIER')," +
                "('admin-001', 'ROLE_ADMIN')").executeUpdate();
        }

        // Insert Employee
        if (isDataAbsent(EMPLOYEE_CHECK_QUERY, "emp-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.EMPLOYEE_DB + " (id, fullname, address, phone_number, gender, city, date_of_birth, date_of_appliment, theater_id, nik_number, is_active, app_user_id) VALUES " +
                "('emp-001', 'Panda', 'Jl. Raya Bandung', '081234567890', 'GENDER_MALE', 'Bandung', '1990-01-01', NOW(), 'theater-001', '1111111111111111', TRUE, 'useremp-001')," +
                "('emp-002', 'Beruang', 'Jl. Raya Jakarta', '089876543210', 'GENDER_FEMALE', 'Jakarta', '1995-05-05', NOW(), 'theater-001', '2222222222222222', TRUE, 'useremp-002')," +
                "('emp-003', 'Kucing', 'Jl. Bandung Selatan', '089324823422', 'GENDER_FEMALE', 'Jakarta', '1997-12-15', NOW(), 'theater-002', '3333333333333333', TRUE, 'useremp-003')," +
                "('emp-004', 'John Doe', '123 Main Street', '+1-555-1234', 'GENDER_MALE', 'New York', '1980-10-10', NOW(), 'theater-002', '4444444444444444', TRUE, 'useremp-004')," +
                "('emp-005', 'Cashier', NULL, NULL, NULL, NULL, NULL, NOW(), 'theater-001', NULL, TRUE, 'useremp-005')," +
                "('emp-006', 'Cashier', NULL, NULL, NULL, NULL, NULL, NOW(), 'theater-002', NULL, TRUE, 'useremp-006')"
            ).executeUpdate();
        }

        // Insert Studio
        if (isDataAbsent(STUDIO_CHECK_QUERY, "studio-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.STUDIO_DB + " (id, name, studio_size, theater_id, is_active) VALUES " +
                "('studio-001', 'Studio 1', 'STUDIO_REGULER_SMALL', 'theater-001', TRUE)," +
                "('studio-002', 'Studio 2', 'STUDIO_REGULER_MEDIUM', 'theater-001', TRUE)," +
                "('studio-003', 'Studio 1', 'STUDIO_REGULER_SMALL', 'theater-002', TRUE)"
            ).executeUpdate();
        }

        // Insert ProductPricing
        if (isDataAbsent(PRODUCT_PRICING_CHECK_QUERY, "pprice-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.PRODUCT_PRICING_DB + " (id, weekday_price, weekend_price, price_date, is_price_active, product_id) VALUES " +
                "('pprice-001', 50000.0, 75000.0, '2025-05-24', TRUE, 'prod-001')," +
                "('pprice-002', 45000.0, 65000.0, '2025-05-24', TRUE, 'prod-002')," +
                "('pprice-003', 55000.0, 80000.0, '2025-05-24', TRUE, 'prod-003')"
            ).executeUpdate();
        }

        // Insert ProductScheduling
        if (isDataAbsent(PRODUCT_SCHEDULING_CHECK_QUERY, "psched-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.PRODUCT_SCHEDULING_DB + " (id, schedule, product_id) VALUES " +
                "('psched-001', 'SCHEDULE_9_00', 'prod-001')," +
                "('psched-002', 'SCHEDULE_12_30', 'prod-001')," +
                "('psched-003', 'SCHEDULE_15_00', 'prod-002')," +
                "('psched-004', 'SCHEDULE_18_00', 'prod-003')"
            ).executeUpdate();
        }

        // Insert StudioSeatSchedule
        if (isDataAbsent(STUDIO_SEAT_SCHEDULE_CHECK_QUERY, "studio_seat_schedule-001")) {
            entityManager.createNativeQuery(
                "INSERT INTO " + DbBash.STUDIO_SEAT_SCHEDULE_DB + " (id, studio_id, product_scheduling_id) VALUES " +
                "('studio_seat_schedule-001', 'studio-001', 'psched-001')," +
                "('studio_seat_schedule-002', 'studio-001', 'psched-002')," +
                "('studio_seat_schedule-003', 'studio-002', 'psched-003')," +
                "('studio_seat_schedule-004', 'studio-003', 'psched-004')"
            ).executeUpdate();
        }
        
        // Insert SeatLayout for Studio
        if (isStudioSeatLayoutDataAbsent(SEAT_LAYOUT_CHECK_QUERY, "studio-001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.STUDIO_SEAT_LAYOUT_DB + " (studio_id, seat_layout) VALUES " +
                "('studio-001', 'SEAT_A1'), ('studio-001', 'SEAT_A2'), ('studio-001', 'SEAT_B1'), ('studio-001', 'SEAT_B2')").executeUpdate();
        }
        if (isStudioSeatLayoutDataAbsent(SEAT_LAYOUT_CHECK_QUERY, "studio-002")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.STUDIO_SEAT_LAYOUT_DB + " (studio_id, seat_layout) VALUES " +
                "('studio-002', 'SEAT_A1'), ('studio-002', 'SEAT_A2'), ('studio-002', 'SEAT_B1'), ('studio-002', 'SEAT_B2')").executeUpdate();
        }
        if (isStudioSeatLayoutDataAbsent(SEAT_LAYOUT_CHECK_QUERY, "studio-003")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.STUDIO_SEAT_LAYOUT_DB + " (studio_id, seat_layout) VALUES " +
                "('studio-003', 'SEAT_C1'), ('studio-003', 'SEAT_C2'), ('studio-003', 'SEAT_D1')").executeUpdate();
        }

        // Insert AvailableSeat for StudioSeatSchedule
        if (isStudioSeatScheduleDataAbsent(AVAILABLE_SEAT_CHECK_QUERY, "studio_seat_schedule-001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.STUDIO_SEAT_SCHEDULE_AVAILABLE_SEAT_DB + " (studio_seat_schedule_id, available_seat) VALUES " +
                "('studio_seat_schedule-001', 'SEAT_A1'), ('studio_seat_schedule-001', 'SEAT_A2'), ('studio_seat_schedule-001', 'SEAT_B1'), ('studio_seat_schedule-001', 'SEAT_B2')").executeUpdate();
        }
        if (isStudioSeatScheduleDataAbsent(AVAILABLE_SEAT_CHECK_QUERY, "studio_seat_schedule-002")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.STUDIO_SEAT_SCHEDULE_AVAILABLE_SEAT_DB + " (studio_seat_schedule_id, available_seat) VALUES " +
                "('studio_seat_schedule-002', 'SEAT_A1'), ('studio_seat_schedule-002', 'SEAT_A2'), ('studio_seat_schedule-002', 'SEAT_B1'), ('studio_seat_schedule-002', 'SEAT_B2')").executeUpdate();
        }
        if (isStudioSeatScheduleDataAbsent(AVAILABLE_SEAT_CHECK_QUERY, "studio_seat_schedule-003")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.STUDIO_SEAT_SCHEDULE_AVAILABLE_SEAT_DB + " (studio_seat_schedule_id, available_seat) VALUES " +
                "('studio_seat_schedule-003', 'SEAT_C1'), ('studio_seat_schedule-003', 'SEAT_C2'), ('studio_seat_schedule-003', 'SEAT_D1')").executeUpdate();
        }
        if (isStudioSeatScheduleDataAbsent(AVAILABLE_SEAT_CHECK_QUERY, "studio_seat_schedule-004")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.STUDIO_SEAT_SCHEDULE_AVAILABLE_SEAT_DB + " (studio_seat_schedule_id, available_seat) VALUES " +
                "('studio_seat_schedule-004', 'SEAT_E1'), ('studio_seat_schedule-004', 'SEAT_E2')").executeUpdate();
        }

        // Insert relations between Product and Theater
        if (isRelationAbsent(DbBash.PRODUCT_THEATER_DB, "product_id", "prod-001", "theater_id", "theater-001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_THEATER_DB + " (product_id, theater_id) VALUES ('prod-001', 'theater-001')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_THEATER_DB, "product_id", "prod-002", "theater_id", "theater-001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_THEATER_DB + " (product_id, theater_id) VALUES ('prod-002', 'theater-001')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_THEATER_DB, "product_id", "prod-002", "theater_id", "theater-002")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_THEATER_DB + " (product_id, theater_id) VALUES ('prod-002', 'theater-002')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_THEATER_DB, "product_id", "prod-003", "theater_id", "theater-002")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_THEATER_DB + " (product_id, theater_id) VALUES ('prod-003', 'theater-002')").executeUpdate();
        }

        // Insert relations between ProductPricing and Studio
        if (isRelationAbsent(DbBash.PRODUCT_PRICING_STUDIO_DB, "product_pricing_id", "pprice-001", "studio_id", "studio-001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_PRICING_STUDIO_DB + " (product_pricing_id, studio_id) VALUES ('pprice-001', 'studio-001')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_PRICING_STUDIO_DB, "product_pricing_id", "pprice-002", "studio_id", "studio-001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_PRICING_STUDIO_DB + " (product_pricing_id, studio_id) VALUES ('pprice-002', 'studio-001')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_PRICING_STUDIO_DB, "product_pricing_id", "pprice-002", "studio_id", "studio-002")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_PRICING_STUDIO_DB + " (product_pricing_id, studio_id) VALUES ('pprice-002', 'studio-002')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_PRICING_STUDIO_DB, "product_pricing_id", "pprice-003", "studio_id", "studio-003")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_PRICING_STUDIO_DB + " (product_pricing_id, studio_id) VALUES ('pprice-003', 'studio-003')").executeUpdate();
        }

        // Insert relations between ProductScheduling and Studio
        if (isRelationAbsent(DbBash.PRODUCT_SCHEDULING_STUDIO_DB, "product_scheduling_id", "psched-001", "studio_id", "studio-001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_SCHEDULING_STUDIO_DB + " (product_scheduling_id, studio_id) VALUES ('psched-001', 'studio-001')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_SCHEDULING_STUDIO_DB, "product_scheduling_id", "psched-002", "studio_id", "studio-001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_SCHEDULING_STUDIO_DB + " (product_scheduling_id, studio_id) VALUES ('psched-002', 'studio-001')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_SCHEDULING_STUDIO_DB, "product_scheduling_id", "psched-003", "studio_id", "studio-002")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_SCHEDULING_STUDIO_DB + " (product_scheduling_id, studio_id) VALUES ('psched-003', 'studio-002')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_SCHEDULING_STUDIO_DB, "product_scheduling_id", "psched-004", "studio_id", "studio-003")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_SCHEDULING_STUDIO_DB + " (product_scheduling_id, studio_id) VALUES ('psched-004', 'studio-003')").executeUpdate();
        }

        // Inserting relations between Product and Artist
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-001", "artist_id", "11111111-2222-3333-4444-555555555555")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-001', '11111111-2222-3333-4444-555555555555')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-001", "artist_id", "66666666-7777-8888-9999-000000000000")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-001', '66666666-7777-8888-9999-000000000000')").executeUpdate();
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-003", "artist_id", "abcdef01-2345-6789-abcd-ef0123456789")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-003', 'abcdef01-2345-6789-abcd-ef0123456789')").executeUpdate();
        }
        // Additional artists for Avengers: Endgame
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-001", "artist_id", "00000000-0000-0000-0000-000000000001")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-001', '00000000-0000-0000-0000-000000000001')").executeUpdate(); // Chris Evans
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-001", "artist_id", "00000000-0000-0000-0000-000000000002")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-001', '00000000-0000-0000-0000-000000000002')").executeUpdate(); // Mark Ruffalo
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-001", "artist_id", "00000000-0000-0000-0000-000000000003")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-001', '00000000-0000-0000-0000-000000000003')").executeUpdate(); // Russo Brothers (Director)
        }

        // Artists for The Lion King
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-002", "artist_id", "00000000-0000-0000-0000-000000000004")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-002', '00000000-0000-0000-0000-000000000004')").executeUpdate(); // Jon Favreau (Director)
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-002", "artist_id", "00000000-0000-0000-0000-000000000005")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-002', '00000000-0000-0000-0000-000000000005')").executeUpdate(); // Hans Zimmer (Music Director)
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-002", "artist_id", "00000000-0000-0000-0000-000000000006")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-002', '00000000-0000-0000-0000-000000000006')").executeUpdate(); // Pharrell Williams (Producer)
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-002", "artist_id", "00000000-0000-0000-0000-000000000007")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-002', '00000000-0000-0000-0000-000000000007')").executeUpdate(); // Donald Glover (Actor)
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-002", "artist_id", "00000000-0000-0000-0000-000000000008")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-002', '00000000-0000-0000-0000-000000000008')").executeUpdate(); // James Earl Jones (Actor)
        }

        // Artists for Inception
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-003", "artist_id", "00000000-0000-0000-0000-000000000009")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-003', '00000000-0000-0000-0000-000000000009')").executeUpdate(); // Leonardo DiCaprio (Actor)
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-003", "artist_id", "00000000-0000-0000-0000-000000000010")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-003', '00000000-0000-0000-0000-000000000010')").executeUpdate(); // Joseph Gordon-Levitt (Actor)
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-003", "artist_id", "00000000-0000-0000-0000-000000000005")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-003', '00000000-0000-0000-0000-000000000005')").executeUpdate(); // Hans Zimmer (Music Director)
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-003", "artist_id", "00000000-0000-0000-0000-000000000011")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-003', '00000000-0000-0000-0000-000000000011')").executeUpdate(); // Emma Thomas (Producer)
        }
        if (isRelationAbsent(DbBash.PRODUCT_ARTIST_DB, "product_id", "prod-003", "artist_id", "00000000-0000-0000-0000-000000000012")) {
            entityManager.createNativeQuery("INSERT INTO " + DbBash.PRODUCT_ARTIST_DB + " (product_id, artist_id) VALUES ('prod-003', '00000000-0000-0000-0000-000000000012')").executeUpdate(); // Lee Smith (Editor)
        }
    }

    private boolean isDataAbsent(String query, String param) {
        Long count = (Long) entityManager.createNativeQuery(query)
            .setParameter("id", param)
            .getSingleResult();
        return count == 0;
    }

    private boolean isArtistTypeDataAbsent(String query, String param) {
        Long count = (Long) entityManager.createNativeQuery(query)
            .setParameter("artist_id", param)
            .getSingleResult();
        return count == 0;
    }

    private boolean isRoleDataAbsent(String query, String param) {
        Long count = (Long) entityManager.createNativeQuery(query)
            .setParameter("app_user_id", param)
            .getSingleResult();
        return count == 0;
    }

    private boolean isStudioSeatLayoutDataAbsent(String query, String param) {
        Long count = (Long) entityManager.createNativeQuery(query)
            .setParameter("studio_id", param)
            .getSingleResult();
        return count == 0;
    }

    private boolean isStudioSeatScheduleDataAbsent(String query, String param) {
        Long count = (Long) entityManager.createNativeQuery(query)
            .setParameter("studio_seat_schedule_id", param)
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
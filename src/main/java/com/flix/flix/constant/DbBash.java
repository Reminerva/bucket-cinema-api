package com.flix.flix.constant;

public class DbBash {
    public static final String USER_DB = "m_user";
    public static final String CUSTOMER_DB = "m_customer";
    public static final String EMPLOYEE_DB = "m_employee";
    public static final String FAV_GENRE_DB = "m_fav_genre";
    public static final String MOVIE_GENRE_DB = "m_movie_genre";
    public static final String PRODUCT_DB = "m_product";
    public static final String PRODUCTION_COMPANY_DB = "m_production_company";
    public static final String ARTIST_DB = "m_artist";
    public static final String THEATER_DB = "m_theater";
    public static final String STUDIO_DB = "m_studio";
    public static final String STUDIO_SEAT_SCHEDULE_DB = "m_studio_seat_schedule";
    public static final String PRODUCT_PRICING_DB = "m_product_pricing";
    public static final String PRODUCT_SCHEDULING_DB = "m_product_scheduling";
    public static final String PRODUCT_PRICING_SCHEDULING_DB = "m_product_pricing_scheduling";
    public static final String TRANSACTION_DB = "t_transaction";

    public static final String CUSTOMER_NOT_FOUND = "Customer not found";
    public static final String EMPLOYEE_NOT_FOUND = "Employee not found";
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String TRASACTION_NOT_FOUND = "Transaction not found";
    public static final String Data_Id_NOT_FOUND = "Data's id not found";
    public static final String GENRE_NOT_FOUND = "Genre not found";
    public static final String ARTIST_NOT_FOUND = "Artist not found";
    public static final String PRODUCTION_COMPANY_NOT_FOUND = "Production company not found";
    public static final String THEATER_NOT_FOUND = "Theater not found";
    public static final String STUDIO_NOT_FOUND = "Studio not found";
    public static final String PRODUCT_PRICING_NOT_FOUND = "Product pricing not found";
    public static final String PRODUCT_SCHEDULING_NOT_FOUND = "Product scheduling not found";
    public static final String PRODUCT_PRICING_SCHEDULING_NOT_FOUND = "Product pricing scheduling not found";
    public static final String STUDIO_SEAT_SCHEDULE_NOT_FOUND = "Studio seat schedule not found";
    public static final String PRODUCT_PRICING_SCHEDULING_ALREADY_EXISTS = "Product pricing scheduling already exists";
    public static final String TRANSACTION_NOT_FOUND = "Transaction not found";
    
    public static final String PRODUCT_ALREADY_PRICED = "The product has already been priced in the studio";
    public static final String PRODUCT_ALREADY_SCHEDULED = "The product has already been scheduled in exact time in the studio";

    public static final String THEATER_AND_STUDIO_NOT_MATCH = "Theater and studio not match";
    public static final String STUDIO_SEAT_NOT_MATCH = "Studio seat not match";
    public static final String PRODUCT_AND_THEATER_NOT_MATCH = "Product and theater not match";
    public static final String PRODUCT_AND_PRODUCT_PRICING_NOT_MATCH = "Product and product pricing not match";
    public static final String PRODUCT_AND_PRODUCT_SCHEDULING_NOT_MATCH = "Product and product scheduling not match";
    public static final String PRODUCT_PRICING_AND_STUDIO_NOT_MATCH = "Product pricing and studio not match";
    public static final String PRODUCT_SCHEDULING_AND_STUDIO_NOT_MATCH = "Product scheduling and studio not match";
    public static final String PRODUCT_SCHEDULING_NOT_MATCH = "Product scheduling not match";
    public static final String PRODUCT_PRICING_AND_PRODUCT_SCHEDULING_NOT_MATCH = "Product pricing and product scheduling not match";
    public static final String BOOKED_SEAT_AND_AVAILABLE_SEAT_NOT_MATCH = "Booked seat and available seat not match (If A1 is in availableSeat, A1 cannot be in bookedSeat, vice versa)";
    public static final String QTY_AND_SEAT_NOT_MATCH = "Qty and seat not match";

    public static final String SCHEDULE_CONFLICT = "Schedule conflict";

    public static final String INVALID_SEAT_LAYOUT = "Invalid seat layout";
    public static final String SEAT_ALREADY_BOOKED = "Seat already booked";

    public static final String UNAUTHORIZED = "Unauthorized";

    public static final String NIK_NUMBER_ALREADY_EXISTS_CONSTRAINT = "m_employee_nik_number_key";
    public static final String NIK_NUMBER_ALREADY_EXISTS = "NIK number already exists";

    public static final String EMAIL_ALREADY_EXISTS_CONSTRAINT = "m_user_email_key";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";

    public static final String USERNAME_ALREADY_EXISTS_CONSTRAINT = "m_user_username_key";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";

    public static final String ONLY_CASHIER_OR_CUSTOMER_CAN_CREATE_TRANSACTION = "Only cashier or customer can create transaction";
    public static final String ONLY_ADMIN_CAN_UPDATE_TRANSACTION = "Only admin can update transaction";

}

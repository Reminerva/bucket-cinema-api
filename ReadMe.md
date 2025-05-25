# Flix API Documentation

This document provides a comprehensive guide to using the Flix API/Bucket Cinema API, including available endpoints, request/response formats, and setup instructions.

## Table of Contents

1.  [Getting Started](#1-getting-started)
    * [Prerequisites](#prerequisites)
    * [Environment Setup](#environment-setup)
    * [Running the Application](#running-the-application)
        * [Option A: Running with Docker (Building from Source)](#option-a-running-with-docker-building-from-source)
        * [Option B: Running with Docker (Pre-built Image)](#option-b-running-with-docker-pre-built-image)
2.  [API Endpoints](#2-api-endpoints)
    * [Authentication](#authentication)
    * [User Management](#user-management)
    * [Customer Management](#customer-management)
    * [Employee Management](#employee-management)
    * [Artist Management](#artist-management)
    * [Production Company Management](#production-company-management)
    * [Product Management](#product-management)
    * [Theater Management](#theater-management)
    * [Studio Management](#studio-management)
    * [Transaction Management](#transaction-management)
3.  [Common Responses](#3-common-responses)
4.  [Roles and Authorization](#4-roles-and-authorization)

---

## 1. Getting Started

This section will guide you through setting up and running the Flix API. You have two primary ways to run this application: **building the Docker image from source** (which requires Java and Maven) or **pulling a pre-built Docker image** (which simplifies the setup by only requiring Docker).

---

### Prerequisites

Before you begin, ensure you have the following installed. **Note**: If you plan to only run the pre-built Docker image, you primarily only need **Docker Desktop** (or Docker Engine).

* **Java Development Kit (JDK) 17 or higher** (Required if building from source)
* **Maven** (Required if building from source)
* **Docker Desktop** (or Docker Engine if on Linux)
* **Git** (for cloning the repository, if building from source)

---

### Environment Setup

The API uses environment variables for configuration. You'll need to create a `.env` file in the root directory of the project, regardless of whether you're building from source or pulling a pre-built image. Below is an example of the variables you'll need to define.

* **Database Configuration**
    * **DATABASE_HOST**=localhost
    * **DATABASE_PORT**=5432
    * **DATABASE_NAME**=flix_db
    * **DATABASE_USERNAME**=your_db_user
    * **DATABASE_PASSWORD**=your_db_password

* **Redis Configuration**
    * **REDIS_HOST**=localhost
    * **REDIS_PASSWORD**=your_redis_password
    * **REDIS_PORT**=6379

* **JWT Configuration**
    * **SECRET_KEY**=your_super_secret_jwt_key_please_change_this_in_production
    * **EXPIRATION_TIME**=3600000 # 1 hour

* **Application Port**
    * **SERVER_PORT**=8081

**Note:** Replace `your_db_user`, `your_db_password`, and `your_super_secret_jwt_key_please_change_this_in_production` with your actual database credentials and a strong, unique JWT secret.

---

### Running the Application

You have two options to run the Flix API: **building from source with Docker** or **pulling a pre-built Docker image**.

---

#### Option A: Running with Docker (Building from Source)

Follow these steps to build and run the application using Docker:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Reminerva/bucket-cinema-api.git
    cd flix
    ```

2.  **Run the application with Docker Compose:**
    Ensure you are in the root directory of your project (where `docker-compose.yml` and `.env` are located).
    ```bash
    docker compose up -d
    ```
    * `-d`: Runs the containers in detached mode (in the background).
    * This command will build your `flix-api` image, and then start all defined services (`db`, `redis`, `app`) within a shared Docker network, allowing them to communicate.

    Your API should now be running and accessible at `http://localhost:8081` (or the port you specified in `SERVER_PORT`). To stop all services, run `docker compose down`.

---

#### Option B: Running with Docker Compose (Using Pre-built Image)

This is the recommended and simplest way to run the Flix API, as it leverages a pre-built image from Docker Hub and automatically sets up the database and Redis services. You don't need to clone the full repository or have Java/Maven installed for this option, only Docker.

1.  **Create a dedicated directory and your `.env` file:**
    Create a new empty directory for your project. Inside this directory, create your `.env` file with all the necessary environment variables as described in the **Environment Setup** section above.

2.  **Create your `docker-compose.yml` file:**
    In the same directory as your `.env` file, create a `docker-compose.yml` file with the following content. **This configuration specifically uses the pre-built `reksaalamsyah/flix-backend:1.0.0` image.**

    ```yaml

    services:
      app:
        image: reksaalamsyah/flix-backend:1.0.0 # Uses the pre-built image from Docker Hub
        container_name: flix_api_prebuilt
        ports:
          - "${SERVER_PORT}:${SERVER_PORT}" # Uses SERVER_PORT from .env
        env_file:
          - ./.env # Mounts your .env file
        depends_on:
          - db
          - redis
        restart: always

      db:
        image: postgres:13-alpine
        container_name: flix_postgres_db
        restart: always
        environment:
          POSTGRES_DB: ${DATABASE_NAME}
          POSTGRES_USER: ${DATABASE_USERNAME}
          POSTGRES_PASSWORD: ${DATABASE_PASSWORD}
        ports:
          - "5432:5432" # Optional: map for host access (e.g., for DBeaver)
        volumes:
          - db_data:/var/lib/postgresql/data # For data persistence

      redis:
        image: redis:latest # Or redis:6-alpine
        container_name: flix_redis
        restart: always
        environment:
          REDIS_PASSWORD: ${REDIS_PASSWORD} # If your Redis requires a password
        ports:
          - "6379:6379" # Optional: map for host access (e.g., for RedisInsight)

    volumes:
      db_data: # Define the named volume for database persistence
    ```

3.  **Run the application with Docker Compose:**
    Ensure you are in the directory containing `docker-compose.yml` and `.env`.
    ```bash
    docker compose up -d
    ```
    * `-d`: Runs the containers in detached mode (in the background).
    * This command will pull the `reksaalamsyah/flix-backend:1.0.0` image, and then start the PostgreSQL database, Redis, and your Flix API, all within a shared Docker network.

    Your API should now be running and accessible at `http://localhost:8081` (or the port you specified in `SERVER_PORT`). To stop all services, run `docker compose down`.


## 2. API Endpoints

This section details all the available API endpoints. All successful responses will follow the `CommonResponse` structure.

### Authentication

**Base Path:** `/api/v1/user/auth`

* **`POST /auth/signup` - Register a new user (Customer)**
    * **Description:** Allows a new customer to register an account.
    * **Roles:** Public
    * **Request Example (NewCustomerRequest):**
        ```json
        {
            "fullname": "John Doe",
            "country": "USA",
            "phoneNumber": "1234567890",
            "city": "New York",
            "gender": "MALE",
            "birthDate": "1990-01-15",
            "registrationDate": "2023-01-01",
            "lastLogin": "2023-01-01",
            "favGenre": ["Action", "Comedy"],
            "likeProductId": [],
            "dislikeProductId": [],
            "username": "johndoe123",
            "email": "john.doe@example.com",
            "password": "securepassword123"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later
        }
        ```
* **`POST /auth/signin` - User Login**
    * **Description:** Authenticates a user and returns a JWT token.
    * **Roles:** Public
    * **Request Example (LoginRequest):**
        ```json
        {
            "email": "john.doe@example.com",
            "password": "securepassword123"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later
        }
        ```
* **`POST /auth/signout` - User Logout**
    * **Description:** Invalidates the user's session/token.
    * **Roles:** Authenticated Users (Admin, Cashier, Customer)
    * **Request Example:** (No request body needed, typically uses JWT in header)
        ```json
        {}
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later
        }
        ```

### User Management

**Base Path:** `/api/v1/user`

* **`GET /api/v1/user` - Get All Users**
    * **Description:** Retrieves a paginated list of all registered users.
    * **Roles:** Admin
    * **Query Parameters:**
        * `page` (optional, default: 0): Page number.
        * `size` (optional, default: 10): Number of items per page.
        * `sortBy` (optional, default: `username`): Field to sort by.
        * `direction` (optional, default: `asc`): Sort direction (`asc` or `desc`).
        * `username` (optional): Filter by username.
        * `email` (optional): Filter by email.
        * `customerFullname` (optional): Filter by customer's full name.
        * `role` (optional, can be multiple): Filter by user role (e.g., `ADMIN`, `CUSTOMER`, `CASHIER`).
    * **Request Example:** (No request body)
        ```
        GET /api/v1/user?page=0&size=5&role=CUSTOMER
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later
        }
        ```

### Customer Management

**Base Path:** `/api/v1/customer`

* **`POST /api/v1/customer` - Create New Customer (Admin Only)**
    * **Description:** Allows an admin to create a new customer account.
    * **Roles:** Admin
    * **Request Example (NewCustomerRequest):**
        ```json
        {
            "fullname": "Jane Smith",
            "country": "Canada",
            "phoneNumber": "1987654321",
            "city": "Toronto",
            "gender": "FEMALE",
            "birthDate": "1995-05-20",
            "registrationDate": "2024-01-10",
            "lastLogin": "2024-01-10",
            "favGenre": ["Drama"],
            "likeProductId": [],
            "dislikeProductId": [],
            "username": "janesmith",
            "email": "jane.smith@example.com",
            "password": "anothersecurepassword"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(CustomerResponse)
        }
        ```
* **`GET /api/v1/customer` - Get All Customers (Admin Only)**
    * **Description:** Retrieves a paginated list of all customer accounts.
    * **Roles:** Admin
    * **Query Parameters:** (Similar to `User Management - Get All Users`, but specific to Customer fields)
        * `page` (optional, default: 0): Page number.
        * `size` (optional, default: 10): Number of items per page.
        * `sortBy` (optional, default: `fullname`): Field to sort by.
        * `direction` (optional, default: `asc`): Sort direction (`asc` or `desc`).
        * `fullname` (optional): Filter by customer's full name.
        * `country` (optional): Filter by customer's country.
        * `phoneNumber` (optional): Filter by customer's phone number.
        * `city` (optional): Filter by customer's city.
        * `gender` (optional): Filter by customer's gender.
        * `registrationDateMin` (optional): Filter by customer's registration date (get customers registered after this date).
        * `registrationDateMax` (optional): Filter by customer's registration date (get customers registered before this date).
        * `birthDateMin` (optional): Filter by customer's birth date (get customers born after this date).
        * `birthDateMax` (optional): Filter by customer's birth date (get customers born before this date).
        * `lastLoginMin` (optional): Filter by customer's last login date (get customers logged in after this date).
        * `lastLoginMax` (optional): Filter by customer's last login date (get customers logged in before this date).
        * `favGenres` (optional): Filter by customer's favorite genres (List of genre names, e.g., `["Action", "Comedy"]`).
        * `email` (optional): Filter by customer's email.
        * `likeProductTitle` (optional): Filter by products liked by the customer (List of product titles, e.g., `["The Shawshank Redemption", "The Godfather"]`).
        * `dislikeProductTitle` (optional): Filter by products disliked by the customer (List of product titles, e.g., `["The Shawshank Redemption", "The Godfather"]`).
    * **Request Example:**
        ```
        GET /api/v1/customer?customerFullname=John
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(List<CustomerResponse> with pagination)
        }
        ```
* **`GET /api/v1/customer/{id}` - Get Customer By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a customer's details by their ID.
    * **Roles:** Admin, Cashier, Customer (if `id` matches their own)
    * **Path Parameters:**
        * `id` (string, required): The ID of the customer.
    * **Request Example:**
        ```
        GET /api/v1/customer/some-customer-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(CustomerResponse)
        }
        ```
* **`PUT /api/v1/customer/{id}` - Update Customer By ID (Admin Only)**
    * **Description:** Updates a customer's details by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the customer to update.
    * **Request Example (UpdateCustomerRequest - Assuming it has similar fields to NewCustomerRequest but all optional):**
        ```json
        {
            "fullname": "Jane Smith",
            "country": "Canada",
            "phoneNumber": "1987654321",
            "city": "Toronto",
            "gender": "FEMALE",
            "birthDate": "1995-05-20",
            "registrationDate": "2024-01-10",
            "lastLogin": "2024-01-10",
            "favGenre": ["Drama"],
            "likeProductId": [],
            "dislikeProductId": []
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(CustomerResponse)
        }
        ```
* **`DELETE /api/v1/customer/{id}` - Delete Customer By ID (Admin Only)**
    * **Description:** Deletes a customer account by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the customer to delete.
    * **Request Example:**
        ```
        DELETE /api/v1/customer/some-customer-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(CustomerResponse)
        }
        ```
* **`GET /api/v1/customer/me` - Get Current Customer's Details (Customer Only)**
    * **Description:** Retrieves the details of the currently authenticated customer.
    * **Roles:** Customer
    * **Request Example:** (No request body, uses JWT from header)
        ```
        GET /api/v1/customer/me
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(CustomerResponse)
        }
        ```
* **`PUT /api/v1/customer/me` - Update Current Customer's Details (Customer Only)**
    * **Description:** Updates the details of the currently authenticated customer.
    * **Roles:** Customer
    * **Request Example (UpdateCustomerRequest):**
        ```json
        {
            "fullname": "Jane Smith",
            "country": "Canada",
            "phoneNumber": "1987654321",
            "city": "Toronto",
            "gender": "FEMALE",
            "birthDate": "1995-05-20",
            "registrationDate": "2024-01-10",
            "lastLogin": "2024-01-10",
            "favGenre": ["Drama"],
            "likeProductId": [],
            "dislikeProductId": []
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(CustomerResponse)
        }
        ```

### Employee Management

**Base Path:** `/api/v1/employee`

* **`POST /api/v1/employee` - Create New Employee (Employee Only)**
    * **Description:** Allows an admin to create a new Employee.
    * **Roles:** Admin
    * **Request Example (NewEmployeeRequest):**
        ```json
        {
            "fullname": "Jane Smith",
            "nikNumber": "1111111111111111",
            "address": "123 Main St",
            "phoneNumber": "1987654321",
            "gender": "FEMALE",
            "city": "Toronto",
            "dateOfBirth": "1995-05-20",
            "theaterId": "theater-001",
            "dateOfAppliment": "2024-01-10",
            "username": "janesmith",
            "email": "jane.smith@example.com",
            "password": "anothersecurepassword"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(EmployeeResponse)
        }
        ```
* **`POST /api/v1/employee/admin` - Create New Admin (Admin Only)**
    * **Description:** Allows an admin to create a new admin account.
    * **Roles:** Admin
    * **Request Example (NewAdminRequest):**
        ```json
        {
            "username": "adminUser",
            "email": "admin@example.com",
            "password": "adminpassword123"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(SignupResponse)
        }
        ```
* **`POST /api/v1/employee/cashier` - Create New Cashier (Admin Only)**
    * **Description:** Allows an admin to create a new cashier account.
    * **Roles:** Admin
    * **Request Example (NewCashierRequest):**
        ```json
        {
            "username": "cashierUser",
            "email": "cashier@example.com",
            "password": "cashierpassword123",
            "theaterId": "theater-001"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(EmployeeResponse)
        }
        ```
* **`GET /api/v1/employee` - Get All Employees (Admin Only)**
    * **Description:** Retrieves a paginated list of all employee accounts (Admins and Cashiers).
    * **Roles:** Admin
    * **Query Parameters:** (Similar to `User Management - Get All Users`, but specific to Employee fields)
        * `page` (optional, default: 0): Page number.
        * `size` (optional, default: 10): Number of items per page.
        * `sortBy` (optional, default: `fullname`): Field to sort by.
        * `direction` (optional, default: `asc`): Sort direction (`asc` or `desc`).
        * `fullname` (optional): Filter by employee's fullname.
        * `nikNumber` (optional): Filter by employee's nik number.
        * `address` (optional): Filter by employee's address.
        * `phoneNumber` (optional): Filter by employee's phone number.
        * `gender` (optional): Filter by employee's gender.
        * `city` (optional): Filter by employee's city.
        * `isActive` (optional): Filter by employee's active status.
        * `dateOfBirthMin` (optional): Filter by employee's date of birth (get employees who were born on or after this date).
        * `dateOfBirthMax` (optional): Filter by employee's date of birth (get employees who were born on or before this date).
        * `dateOfApplimentMin` (optional): Filter by employee's date of appliment (get employees who applied on or after this date).
        * `dateOfApplimentMax` (optional): Filter by employee's date of appliment (get employees who applied on or before this date).
        * `appUserUsername` (optional): Filter by employee's username.
        * `appUserEmail` (optional): Filter by employee's email.
        * `theaterName` (optional): Filter by employee's theater name.
        * `theaterCity` (optional): Filter by employee's theater city.
    * **Request Example:**
        ```
        GET /api/v1/employee?fullName=Jane
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(List<EmployeeResponse>) with paging
        }
        ```
* **`GET /api/v1/employee/{id}` - Get Employee By ID (Admin Only)**
    * **Description:** Retrieves an employee's details by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the employee.
    * **Request Example:**
        ```
        GET /api/v1/employee/some-employee-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(EmployeeResponse)
        }
        ```
* **`PUT /api/v1/employee/{id}` - Update Employee By ID (Admin Only)**
    * **Description:** Updates an employee's details by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the employee to update.
    * **Request Example (UpdateEmployeeRequest):**
        ```json
        {
            "fullname": "Updated Employee Name",
            "nikNumber": "1234567890123456",
            "address": "New Employee Address",
            "phoneNumber": "11122233344",
            "gender": "FEMALE",
            "city": "Bandung",
            "dateOfBirth": "1988-03-22",
            "theaterId": "another-theater-id-456",
            "dateOfAppliment": "2020-01-01",
            "username": "updated_employee",
            "email": "updated.employee@example.com",
            "password": "newsecurepassword"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(EmployeeResponse)
        }
        ```
* **`DELETE /api/v1/employee/{id}` - Soft Delete Employee By ID (Admin Only)**
    * **Description:** Soft deletes an employee account by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the employee to soft delete.
    * **Request Example:**
        ```
        DELETE /api/v1/employee/some-employee-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later
        }
        ```
* **`GET /api/v1/employee/me` - Get Current Employee's Details (Employee Only)**
    * **Description:** Retrieves the details of the currently authenticated employee (Admin or Cashier).
    * **Roles:** Admin, Cashier
    * **Request Example:** (No request body, uses JWT from header)
        ```
        GET /api/v1/employee/me
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(EmployeeResponse)
        }
        ```
* **`PUT /api/v1/employee/me` - Update Current Employee's Details (Employee Only)**
    * **Description:** Updates the details of the currently authenticated employee.
    * **Roles:** Admin, Cashier
    * **Request Example (UpdateEmployeeRequest):**
        ```json
        {
            "fullname": "Updated Employee Name",
            "nikNumber": "1234567890123456",
            "address": "New Employee Address",
            "phoneNumber": "11122233344",
            "gender": "FEMALE",
            "city": "Bandung",
            "dateOfBirth": "1988-03-22",
            "theaterId": "another-theater-id-456",
            "dateOfAppliment": "2020-01-01",
            "username": "updated_employee",
            "email": "updated.employee@example.com",
            "password": "newsecurepassword"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(EmployeeResponse)
        }
        ```

### Artist Management

**Base Path:** `/api/v1/artist`

* **`POST /api/v1/artist` - Create New Artist (Admin Only)**
    * **Description:** Creates a new artist record.
    * **Roles:** Admin
    * **Request Example (NewArtistRequest):**
        ```json
        {
            "name": "Robert John Downey Jr.",
            "placeOfBirth": "New York, New York, United States",
            "birthDate": "1965-04-04",
            "otherName": "Robert Downey Jr.",
            "bio": "Actor, producer, and philanthropist.",
            "artistTypes": ["Actor", "Producer"]
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ArtistResponse)
        }
        ```
* **`GET /api/v1/artist` - Get All Artists (Admin, Cashier, Customer)**
    * **Description:** Retrieves a paginated list of all artists.
    * **Roles:** Admin, Cashier, Customer
    * **Query Parameters:**
        * `page` (optional, default: 0): Page number.
        * `size` (optional, default: 10): Number of items per page.
        * `sortBy` (optional, default: `name`): Field to sort by.
        * `direction` (optional, default: `asc`): Sort direction (`asc` or `desc`).
        * `name` (optional): Filter by artist's name.
        * `placeOfBirth` (optional): Filter by artist's place of birth. 
        * `birthDateMin` (optional): Filter by artist's birth date (get artists born after this date).
        * `birthDateMax` (optional): Filter by artist's birth date (get artists born before this date).
        * `artistType` (optional): Filter by artist's type (List of artist types, e.g., `["Actor", "Producer"]`).
        * `inProductTitle` (optional): Filter by artist's appearance in products (List of product titles, e.g., `["The Shawshank Redemption", "The Godfather"]`).
    * **Request Example:**
        ```
        GET /api/v1/artist?name=Adele
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ArtistResponse)
        }
        ```
* **`GET /api/v1/artist/{id}` - Get Artist By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves an artist's details by their ID.
    * **Roles:** Admin, Cashier, Customer
    * **Path Parameters:**
        * `id` (string, required): The ID of the artist.
    * **Request Example:**
        ```
        GET /api/v1/artist/some-artist-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ArtistResponse)
        }
        ```
* **`PUT /api/v1/artist/{id}` - Update Artist By ID (Admin Only)**
    * **Description:** Updates an artist's details by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the artist to update.
    * **Request Example (NewArtistRequest):**
        ```json
        {
            "name": "Robert John Downey Jr.",
            "placeOfBirth": "New York, New York, United States",
            "birthDate": "1965-04-04",
            "otherName": "Robert Downey Jr.",
            "bio": "Actor, producer, and philanthropist.",
            "artistTypes": ["Actor", "Producer"]
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ArtistResponse)
        }
        ```
* **`DELETE /api/v1/artist/{id}` - Delete Artist By ID (Admin Only)**
    * **Description:** Deletes an artist record by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the artist to delete.
    * **Request Example:**
        ```
        DELETE /api/v1/artist/some-artist-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ArtistResponse)
        }
        ```

### Production Company Management

**Base Path:** `/api/v1/production-company`

* **`POST /api/v1/production-company` - Create New Production Company (Admin Only)**
    * **Description:** Creates a new production company record.
    * **Roles:** Admin
    * **Request Example (NewProductionCompanyRequest):**
        ```json
        {
            "name": "Warner Bros. Pictures",
            "logoUrl": "[http://example.com/warner_logo.png](http://example.com/warner_logo.png)",
            "originCountry": "USA",
            "websiteUrl": "[http://www.warnerbros.com](http://www.warnerbros.com)",
            "headquarters": "Burbank, California",
            "ceo": "Ann Sarnoff",
            "description": "An American film production and distribution company.",
            "contactEmail": "info@warnerbros.com",
            "contactNumber": "1-818-954-6000",
            "foundedYear": "1923"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductionCompanyResponse)
        }
        ```
* **`GET /api/v1/production-company` - Get All Production Companies (Admin, Cashier, Customer)**
    * **Description:** Retrieves a paginated list of all production companies.
    * **Roles:** Admin, Cashier, Customer
    * **Query Parameters:**
        * `page` (optional, default: 0): Page number.
        * `size` (optional, default: 10): Number of items per page.
        * `sortBy` (optional, default: `companyName`): Field to sort by.
        * `direction` (optional, default: `asc`): Sort direction (`asc` or `desc`).
        * `name` (optional): Filter by production company's name.
        * `originCountry` (optional): Filter by production company's origin country.
        * `foundedYearMin` (optional): Filter by production company's founded year (get companies founded after this year).
        * `foundedYearMax` (optional): Filter by production company's founded year (get companies founded before this year).
        * `headquarters` (optional): Filter by production company's headquarters.
        * `ceo` (optional): Filter by production company's ceo.
        * `createdAtMin` (optional): Filter by production company's added date (get companies added after this date, e.g., `YYYY-MM-DDTHH:MM:SS`).
        * `createdAtMax` (optional): Filter by production company's added date (get companies added before this date).
        * `updatedAtMin` (optional): Filter by production company's updated date (get companies updated after this date, e.g., `YYYY-MM-DDTHH:MM:SS`).
        * `updatedAtMax` (optional): Filter by production company's updated date (get companies updated before this date).
        * `hasProducts` (optional): Filter by production company's presence of products (List of product IDs, e.g., `["product-001", "product-002"]`).
    * **Request Example:**
        ```
        GET /api/v1/production-company?companyName=Warner
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(List<ProductionCompanyResponse> with paging)
        }
        ```
* **`GET /api/v1/production-company/{id}` - Get Production Company By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a production company's details by their ID.
    * **Roles:** Admin, Cashier, Customer
    * **Path Parameters:**
        * `id` (string, required): The ID of the production company.
    * **Request Example:**
        ```
        GET /api/v1/production-company/some-company-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductionCompanyResponse)
        }
        ```
* **`PUT /api/v1/production-company/{id}` - Update Production Company By ID (Admin Only)**
    * **Description:** Updates a production company's details by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the production company to update.
    * **Request Example (NewProductionCompanyRequest):**
        ```json
        {
            "name": "Warner Bros. Pictures",
            "logoUrl": "[http://example.com/warner_logo.png](http://example.com/warner_logo.png)",
            "originCountry": "USA",
            "websiteUrl": "[http://www.warnerbros.com](http://www.warnerbros.com)",
            "headquarters": "Burbank, California",
            "ceo": "Ann Sarnoff",
            "description": "An American film production and distribution company.",
            "contactEmail": "info@warnerbros.com",
            "contactNumber": "1-818-954-6000",
            "foundedYear": "1923"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductionCompanyResponse)
        }
        ```
* **`DELETE /api/v1/production-company/{id}` - Delete Production Company By ID (Admin Only)**
    * **Description:** Deletes a production company record by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the production company to delete.
    * **Request Example:**
        ```
        DELETE /api/v1/production-company/some-company-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductionCompanyResponse)
        }
        ```

### Product Management

**Base Path:** `/api/v1/product`

* **`POST /api/v1/product` - Create New Product (Admin Only)**
    * **Description:** Creates a new product (e.g., movie) record.
    * **Roles:** Admin
    * **Request Example (NewProductRequest):**
        ```json
        {
            "title": "The Matrix",
            "duration": 136,
            "language": "English",
            "country": "USA",
            "releaseDate": "1999-03-31",
            "posterUrl": "[http://example.com/matrix_poster.jpg](http://example.com/matrix_poster.jpg)",
            "trailerUrl": "[http://example.com/matrix_trailer.mp4](http://example.com/matrix_trailer.mp4)",
            "rated": "R",
            "budget": 63000000,
            "synopsis": "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
            "tagline": "Welcome to the Real World.",
            "imdbRating": 8.7,
            "rottenTomatoesRating": 88,
            "productionCompanyId": "some-production-company-id",
            "movieGenre": ["Action", "Sci-Fi"],
            "artistId": ["artist-001", "artist-002"]
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductResponse)
        }
        ```
* **`GET /api/v1/product` - Get All Products (Admin, Cashier, Customer)**
    * **Description:** Retrieves a paginated list of all products.
    * **Roles:** Admin, Cashier, Customer
    * **Query Parameters:**
        * `page` (optional, default: 0): Page number.
        * `size` (optional, default: 10): Number of items per page.
        * `sortBy` (optional, default: `title`): Field to sort by.
        * `direction` (optional, default: `asc`): Sort direction (`asc` or `desc`).
        * `title` (optional): Filter by product's title.
        * `durationMin` (optional): Filter by product's duration (in minutes).
        * `durationMax` (optional): Filter by product's duration (in minutes).
        * `language` (optional): Filter by product's language.
        * `country` (optional): Filter by product's country.
        * `releaseDateMin` (optional): Filter by product's release date (get products released after this date).
        * `releaseDateMax` (optional): Filter by product's release date (get products released before this date).
        * `rated` (optional): Filter by product's rating (List of ratings, e.g., `["G", "PG", "PG-13"]`).
        * `budgetMin` (optional): Filter by product's budget (in USD).
        * `budgetMax` (optional): Filter by product's budget (in USD).
        * `imdbRatingMin` (optional): Filter by product's imdb rating.
        * `imdbRatingMax` (optional): Filter by product's imdb rating.
        * `rottenTomatoesRatingMin` (optional): Filter by product's rotten tomatoes rating.
        * `rottenTomatoesRatingMax` (optional): Filter by product's rotten tomatoes rating.
        * `movieGenre` (optional): Filter by product's movie genre (List of genres, e.g., `["Action", "Drama"]`).
        * `productPricingMin` (optional): Filter by product's price (in USD).
        * `productPricingMax` (optional): Filter by product's price (in USD).
        * `lastUpdatedMin` (optional): Filter by product's last updated date (get products updated after this date).
        * `lastUpdatedMax` (optional): Filter by product's last updated date (get products updated before this date).
        * `artistsName` (optional): Filter by product's artists (List of artist names, e.g., `["John Doe", "Jane Smith"]`).
        * `productionCompany` (optional): Filter by product's production company.
    * **Request Example:**
        ```
        GET /api/v1/product?title=Avengers&genre=Action
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(List<ProductResponse> with paging)
        }
        ```
* **`GET /api/v1/product/{id}` - Get Product By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a product's details by its ID.
    * **Roles:** Admin, Cashier, Customer
    * **Path Parameters:**
        * `id` (string, required): The ID of the product.
    * **Request Example:**
        ```
        GET /api/v1/product/some-product-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductResponse)
        }
        ```
* **`PUT /api/v1/product/{id}` - Update Product By ID (Admin Only)**
    * **Description:** Updates a product's details by its ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the product to update.
    * **Request Example (NewProductRequest):**
        ```json
        {
            "title": "The Matrix",
            "duration": 136,
            "language": "English",
            "country": "USA",
            "releaseDate": "1999-03-31",
            "posterUrl": "[http://example.com/matrix_poster.jpg](http://example.com/matrix_poster.jpg)",
            "trailerUrl": "[http://example.com/matrix_trailer.mp4](http://example.com/matrix_trailer.mp4)",
            "rated": "R",
            "budget": 63000000,
            "synopsis": "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
            "tagline": "Welcome to the Real World.",
            "imdbRating": 8.7,
            "rottenTomatoesRating": 88,
            "productionCompanyId": "some-production-company-id",
            "movieGenre": ["Action", "Sci-Fi"],
            "artistId": ["artist-001", "artist-002"],
            "showingOnTheaters": ["theater-001", "theater-002"]
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductResponse)
        }
        ```
* **`DELETE /api/v1/product/{id}/hard-delete` - Hard Delete Product By ID (Admin Only)**
    * **Description:** Permanently deletes a product record by its ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the product to hard delete.
    * **Request Example:**
        ```
        DELETE /api/v1/product/some-product-id/hard-delete
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductResponse)
        }
        ```
* **`DELETE /api/v1/product/{id}/soft-delete` - Soft Delete Product By ID (Admin Only)**
    * **Description:** Soft deletes a product record by its ID (marks as inactive without permanent deletion).
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the product to soft delete.
    * **Request Example:**
        ```
        DELETE /api/v1/product/some-product-id/soft-delete
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductResponse)
        }
        ```

### Theater Management

**Base Path:** `/api/v1/theater`

* **`POST /api/v1/theater` - Create New Theater (Admin Only)**
    * **Description:** Creates a new theater record.
    * **Roles:** Admin
    * **Request Example (NewTheaterRequest):**
        ```json
        {
            "name": "Mega Cinema Plaza",
            "city": "123 Main Street, Cityville",
            "contactNumber": "1-555-123-4567",
            "contactEmail": "info@megacinema.com"
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(TheaterResponse)
        }
        ```
* **`GET /api/v1/theater` - Get All Theaters (Admin, Cashier, Customer)**
    * **Description:** Retrieves a paginated list of all theaters.
    * **Roles:** Admin, Cashier, Customer
    * **Query Parameters:**
        * `page` (optional, default: 0): Page number.
        * `size` (optional, default: 10): Number of items per page.
        * `sortBy` (optional, default: `theaterName`): Field to sort by.
        * `direction` (optional, default: `asc`): Sort direction (`asc` or `desc`).
        * `theaterName` (optional): Filter by theater name.
        * `address` (optional): Filter by address.
        * `contactPhone` (optional): Filter by contact phone.
        * `contactEmail` (optional): Filter by contact email.
        * `createdAtMin` (optional): Filter by minimum creation date (e.g., `YYYY-MM-DDTHH:MM:SS`).
        * `createdAtMax` (optional): Filter by maximum creation date.
        * `updatedAtMin` (optional): Filter by minimum update date (e.g., `YYYY-MM-DDTHH:MM:SS`).
        * `updatedAtMax` (optional): Filter by maximum update date.
        * `oprationalStatus` (optional): Filter by operational status (e.g., `True`, `False`).
        * `employeesName` (optional): Filter by employee name associated with the theater.
        * `productsTitle` (optional): Filter by product title shown in the theater.
    * **Request Example:**
        ```
        GET /api/v1/theater?theaterName=Cinema%20X&oprationalStatus=True
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(List<TheaterResponse> with paging)
        }
        ```
* **`GET /api/v1/theater/{id}` - Get Theater By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a theater's details by its ID.
    * **Roles:** Admin, Cashier, Customer
    * **Path Parameters:**
        * `id` (string, required): The ID of the theater.
    * **Request Example:**
        ```
        GET /api/v1/theater/theater-001
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(TheaterResponse)
        }
        ```
* **`PUT /api/v1/theater/{id}` - Update Theater By ID (Admin Only)**
    * **Description:** Updates a theater's details by its ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the theater to update.
    * **Request Example (NewTheaterRequest):**
        ```json
        {
            "name": "Mega Cinema Plaza",
            "city": "123 Main Street, Cityville",
            "contactNumber": "1-555-123-4567",
            "contactEmail": "info@megacinema.com",
            "operationalStatus": "true",
            "studiosId": ["studio1Id", "studio2Id"],
            "nowShowingId": ["product1Id", "product2Id"]
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(TheaterResponse)
        }
        ```
* **`DELETE /api/v1/theater/{id}` - Hard Delete Theater By ID (Admin Only)**
    * **Description:** Soft deletes a theater record by its ID (marks as inactive without permanent deletion).
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the theater to soft delete.
    * **Request Example:**
        ```
        DELETE /api/v1/theater/theater-001
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(TheaterResponse)
        }
        ```
* **`PUT /api/v1/theater/{id}/refresh-all-seat` - Refresh All Seats (Admin Only)**
    * **Description:** Refreshes all seats for a specific theater.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the theater.
    * **Request Example:** (No request body)
        ```
        PUT /api/v1/theater/theater-001/refresh-all-seat
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later
        }
        ```

### Studio Management

**Base Path:** `/api/v1/studio`

* **`POST /api/v1/studio` - Create New Studio (Admin Only)**
    * **Description:** Creates a new studio (screen/hall within a theater) record.
    * **Roles:** Admin
    * **Request Example (NewStudioRequest):**
        ```json
        {
            "name": "Studio 1 - Regular",
            "theaterId": "theater-001",
            "studioSize": "REGULAR MEDIUM",
            "seatLayout": ["A1", "A2", "B1", "B2"],
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(StudioResponse)
        }
        ```
* **`GET /api/v1/studio` - Get All Studios (Admin, Cashier, Customer)**
    * **Description:** Retrieves a paginated list of all studios. This endpoint needs request body.
    * **Roles:** Admin, Cashier, Customer
    * **Query Parameters:**
        * `page` (optional, default: 0): Page number.
        * `size` (optional, default: 10): Number of items per page.
        * `sortBy` (optional, default: `name`): Field to sort by.
        * `direction` (optional, default: `asc`): Sort direction (`asc` or `desc`).
        * `name` (optional): Filter by studio's name.
        * `isActive` (optional): Filter by studio's active status.
        * `studioSize` (optional): Filter by studio's size (e.g., `REGULAR MEDIUM`, `REGULAR LARGE`, `REGULAR SMALL`).
        * `theaterName` (optional): Filter by studio's theater name.
        * `theaterCity` (optional): Filter by studio's theater city.
    * **Request Example:**
        ```
        GET /api/v1/studio?name=Studio%201&theaterName=Cinema%20X
        ```
    * **Reqest body example:**
        ```json
        {
            "seatLayout": ["A1", "A2", "B1", "B2"],
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(List<StudioResponse> with paging)
        }
        ```
* **`GET /api/v1/studio/{id}` - Get Studio By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a studio's details by its ID.
    * **Roles:** Admin, Cashier, Customer
    * **Path Parameters:**
        * `id` (string, required): The ID of the studio.
    * **Request Example:**
        ```
        GET /api/v1/studio/some-studio-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(StudioResponse)
        }
        ```
* **`GET /api/v1/studio/{theaterId}/{productId}` - Get Studios by Product and Theater ID (Cashier, Customer)**
    * **Description:** Retrieves studios associated with a specific product and theater.
    * **Roles:** Cashier, Customer
    * **Path Parameters:**
        * `theaterId` (string, required): The ID of the theater.
        * `productId` (string, required): The ID of the product.
    * **Request Example:**
        ```
        GET /api/v1/studio/theater-001/some-product-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later
        }
        ```
* **`PUT /api/v1/studio/{id}` - Update Studio By ID (Admin Only)**
    * **Description:** Updates a studio's details by its ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the studio to update.
    * **Request Example (NewStudioRequest):**
        ```json
        {
            "name": "Studio 1 - Regular",
            "theaterId": "theater-001",
            "studioSize": "REGULAR MEDIUM",
            "seatLayout": ["A1", "A2", "B1", "B2"],
            "studioSeatScheduleRequests": [
                {
                    "studioId": "studio-001",
                    "productSchedulingId": "psched-001",
                    "bookedSeat": [],
                    "availableSeat": ["A1", "A2", "B1", "B2"],
                    "productSchedulingId": "psched-001"
                }
            ],
            "productPricingRequests": [
                {
                    "weekdayPrice": 45000.0,
                    "weekendPrice": 65000.0,
                    "isPriceActive": true,
                    "productId": "prod-001"
                }
            ],
            "productSchedulingRequests": [
                {
                    "schedule": "10.00",
                    "productId": "prod-001"
                }
            ]
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(StudioResponse)
        }
        ```
* **`DELETE /api/v1/studio/{id}` - Delete Studio By ID (Admin Only)**
    * **Description:** Deletes a studio record by its ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the studio to delete.
    * **Request Example:**
        ```
        DELETE /api/v1/studio/some-studio-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(StudioResponse)
        }
        ```
* **`GET /api/v1/studio/me` - Get Current Studio's Details (Cashier Only)**
    * **Description:** Retrieves the details of the current studio.
    * **Roles:** Cashier
    * **Request Example:**
        ```
        GET /api/v1/studio/me
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(StudioResponse)
        }
        ```


### Transaction Management

**Base Path:** `/api/v1/transaction`

* **`POST /api/v1/transaction` - Create New Transaction (Customer, Cashier)**
    * **Description:** Creates a new transaction record for a customer's purchase.
    * **Roles:** Customer, Cashier
    * **Request Example (NewTransactionRequest):**
        ```json
        {
            "theaterId" : "theater-001",
            "studioId" : "studio-001",
            "productId" : "prod-001",
            "productPricingId" : "ppricing-001",
            "productSchedulingId" : "psched-001",
            "qty" : 1,
            "tax" : 10,
            "transactionDateTime" : "2023-08-01T10:00:00",
            "watchDate" : "2023-08-01",
            "paymentDateTime" : "2023-08-01T10:00:00",
            "paymentMethod" : "CASH",
            "seats" : ["A1"],
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(TransactionResponse)
        }
        ```
* **`GET /api/v1/transaction` - Get All Transactions (Admin)**
    * **Description:** Retrieves a paginated list of all transactions.
    * **Roles:** Admin
    * **Query Parameters:**
        * `page` (optional, default: 0): Page number.
        * `size` (optional, default: 10): Number of items per page.
        * `sortBy` (optional, default: `transactionDateTime`): Field to sort by.
        * `direction` (optional, default: `desc`): Sort direction (`asc` or `desc`).
        * `customerId` (optional): Filter by transaction's customer id.
        * `customerName` (optional): Filter by transaction's customer name.
        * `employeeId` (optional): Filter by transaction's employee id.
        * `employeeName` (optional): Filter by transaction's employee name.
        * `theaterName` (optional): Filter by transaction's theater name.
        * `studioName` (optional): Filter by transaction's studio name.
        * `productTitle` (optional): Filter by transaction's product title.
        * `productPriceMin` (optional): Filter by transaction's product price (in USD).
        * `productPriceMax` (optional): Filter by transaction's product price (in USD).
        * `productSchedule` (optional): Filter by transaction's product schedule (e.g., "10.00").
        * `qtyMin` (optional): Filter by transaction's ticket quantity.
        * `qtyMax` (optional): Filter by transaction's ticket quantity.
        * `tax` (optional): Filter by transaction's tax amount.
        * `transactionDateTimeMin` (optional): Filter by transaction's transaction date (e.g., "2023-08-01T10:00:00").
        * `transactionDateTimeMax` (optional): Filter by transaction's transaction date.
        * `watchDateMin` (optional): Filter by transaction's watch date (e.g., "2023-08-01").
        * `watchDateMax` (optional): Filter by transaction's watch date.
        * `paymentStatus` (optional): Filter by transaction's payment status (e.g., "PAID", "PENDING", "CANCELLED").
        * `paymentDateTimeMin` (optional): Filter by transaction's payment date (e.g., "2023-08-01T10:00:00").
        * `paymentDateTimeMax` (optional): Filter by transaction's payment date.
        * `paymentMethod` (optional): Filter by transaction's payment method (e.g., "CASH", "CARD").
        * `seats` (optional): Filter by transaction's seats (List of seat numbers, e.g., `["A1", "A2"]`).
        * `createdAtMin` (optional): Filter by transaction's creation date (e.g., "2023-08-01T10:00:00").
        * `createdAtMax` (optional): Filter by transaction's creation date.
        * `updatedAtMin` (optional): Filter by transaction's update date (e.g., "2023-08-01T10:00:00").
        * `updatedAtMax` (optional): Filter by transaction's update date.
        * `expirationDateMin` (optional): Filter by transaction's expiration date (e.g., "2023-08-01T10:00:00").
        * `expirationDateMax` (optional): Filter by transaction's expiration date.
    * **Request Example:**
        ```
        GET /api/v1/transaction?paymentStatus=PAID
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later
        }
        ```
* **`GET /api/v1/transaction/me` - Get All Transactions (Customer, Cashier)**
    * **Description:** Retrieves a paginated list of all transactions.
    * **Roles:** Customer, Cashier
    * **Query Parameters:**
        * `page` (optional, default: 0): Page number.
        * `size` (optional, default: 10): Number of items per page.
        * `sortBy` (optional, default: `transactionDateTime`): Field to sort by.
        * `direction` (optional, default: `desc`): Sort direction (`asc` or `desc`).
        * `theaterName` (optional): Filter by transaction's theater name.
        * `studioName` (optional): Filter by transaction's studio name.
        * `productTitle` (optional): Filter by transaction's product title.
        * `productPriceMin` (optional): Filter by transaction's product price (in USD).
        * `productPriceMax` (optional): Filter by transaction's product price (in USD).
        * `productSchedule` (optional): Filter by transaction's product schedule (e.g., "10.00").
        * `qtyMin` (optional): Filter by transaction's ticket quantity.
        * `qtyMax` (optional): Filter by transaction's ticket quantity.
        * `tax` (optional): Filter by transaction's tax amount.
        * `transactionDateTimeMin` (optional): Filter by transaction's transaction date (e.g., "2023-08-01T10:00:00").
        * `transactionDateTimeMax` (optional): Filter by transaction's transaction date.
        * `watchDateMin` (optional): Filter by transaction's watch date (e.g., "2023-08-01").
        * `watchDateMax` (optional): Filter by transaction's watch date.
        * `paymentStatus` (optional): Filter by transaction's payment status (e.g., "PAID", "PENDING", "CANCELLED").
        * `paymentDateTimeMin` (optional): Filter by transaction's payment date (e.g., "2023-08-01T10:00:00").
        * `paymentDateTimeMax` (optional): Filter by transaction's payment date.
        * `paymentMethod` (optional): Filter by transaction's payment method (e.g., "CASH", "CARD").
        * `seats` (optional): Filter by transaction's seats (List of seat numbers, e.g., `["A1", "A2"]`).
        * `createdAtMin` (optional): Filter by transaction's creation date (e.g., "2023-08-01T10:00:00").
        * `createdAtMax` (optional): Filter by transaction's creation date.
        * `updatedAtMin` (optional): Filter by transaction's update date (e.g., "2023-08-01T10:00:00").
        * `updatedAtMax` (optional): Filter by transaction's update date.
        * `expirationDateMin` (optional): Filter by transaction's expiration date (e.g., "2023-08-01T10:00:00").
        * `expirationDateMax` (optional): Filter by transaction's expiration date.
    * **Request Example:**
        ```
        GET /api/v1/transaction?paymentStatus=PAID
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later
        }
        ```
* **`GET /api/v1/transaction/{id}` - Get Transaction By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a transaction's details by its ID.
    * **Roles:** Admin, Cashier, Customer (if `id` matches their own transaction)
    * **Path Parameters:**
        * `id` (string, required): The ID of the transaction.
    * **Request Example:**
        ```
        GET /api/v1/transaction/some-transaction-id
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later
        }
        ```
* **`PUT /api/v1/transaction/{id}` - Update Payment Status (Admin, Cashier)**
    * **Description:** Updates the payment status of a transaction by its ID.
    * **Roles:** Admin, Cashier
    * **Path Parameters:**
        * `id` (string, required): The ID of the transaction.
    * **Request Example:**
        ```json
        {
            "theaterId" : "theater-001",
            "studioId" : "studio-001",
            "productId" : "prod-001",
            "productPricingId" : "ppricing-001",
            "productSchedulingId" : "psched-001",
            "qty" : 1,
            "tax" : 10,
            "transactionDateTime" : "2023-08-01T10:00:00",
            "watchDate" : "2023-08-01",
            "paymentDateTime" : "2023-08-01T10:00:00",
            "paymentMethod" : "CASH",
            "paymentStatus" : "PAID",
            "seats" : ["A1"],
        }
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(TransactionResponse)
        }
        ```

## 3. Common Responses

All API responses are wrapped in a `CommonResponse` object with the following structure:

```json
{
    "code": 200, // HTTP status code
    "message": "Success message",
    "data": {}, // The actual response data (can be object or array)
    "paging": { // Only present for paginated responses
        "totalPages": 2,
        "totalElement": 12,
        "page": 1,
        "size": 10,
        "hasNext": true,
        "hasPrevious": false,
    }
}
```

Error responses will also follow this structure, but with a non-2xx code and an appropriate message.

## 4. Roles and Authorization
The API implements role-based access control using Spring Security's @PreAuthorize annotation. The following roles are defined:

**ADMIN:** Has full access to all API endpoints.
**CASHIER:** Has access to endpoints related to product viewing, studio viewing, and transaction viewing.
**CUSTOMER:** Has access to endpoints related to their own customer profile, product viewing, studio viewing, and creating/viewing their own transactions.

To access protected endpoints, users must include a valid JWT (JSON Web Token) in the Authorization header of their requests, prefixed with Bearer.

Example:
Authorization: Bearer <your_jwt_token>


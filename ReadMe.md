# Flix API Documentation

This document provides a comprehensive guide to using the Flix API/Bucket Cinema API, including available endpoints, request/response formats, and setup instructions.

## Table of Contents

1.  [Getting Started](#1-getting-started)
    * [API Documentation (Swagger UI)](#api-documentation-swagger-ui)
    * [Prerequisites](#prerequisites)
    * [Environment Setup](#environment-setup)
    * [Running the Application](#running-the-application)
        * [Option A: Running with Docker (Building from Source)](#option-a-running-with-docker-building-from-source)
        * [Option B: Running with Docker Compose (Using Pre-built Image)](#option-b-running-with-docker-compose-using-pre-built-image)
    * [Initial Data (Seeder)](#initial-data-seeder)
    * [Postman Collection](#postman-collection)
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

## API Documentation (Swagger UI)

You can explore the full API documentation, including all available endpoints, request parameters, and response models, directly through **Swagger UI**.

Once the backend application is running, open your web browser and navigate to:

`http://localhost:8081/swagger-ui/index.html`

This interactive documentation allows you to:
* View detailed information about each endpoint.
* Try out API calls directly from the browser (though you'll need to manually add authorization tokens for protected endpoints).
* Understand the expected request and response formats.

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

The API uses environment variables for configuration. You'll need to create a `.env` file in the root directory of the project, regardless of whether you're building from source or pulling a pre-built image. 

    ```env
    # Database Configuration (PostgreSQL)
    DATABASE_HOST=db
    DATABASE_USERNAME=your_db_user # Replace with your desired database username
    DATABASE_PASSWORD=your_db_password # Replace with your desired database password
    DATABASE_PORT=5432
    DATABASE_NAME=flix_db

    # Redis Configuration
    REDIS_HOST=redis
    REDIS_PORT=6379
    REDIS_PASSWORD=your_redis_password # Replace with your desired Redis password (can be empty if no password)

    # JWT Secret and Expiration
    SECRET_KEY=your_jwt_secret_key_here_a_long_random_string_is_recommended
    EXPIRATION_TIME=360000000 # Token expiration time in milliseconds (e.g., 1000 hours)

    # Server Port
    SERVER_PORT=8081

    # Frontend URL for CORS
    FRONTEND_URL=http://your_frontend_url
    ```
    **Note:** Replace placeholders like `your_db_user`, `your_db_password`, `your_redis_password`, `your_frontend_url` and `your_jwt_secret_key_here` with your actual desired values. For `SECRET_KEY`, generate a strong, random string.

---

### Running the Application

You have two options to run the Flix API: **building from source with Docker** or **pulling a pre-built Docker image**.

---

#### Option A: Running with Docker (Building from Source)

Follow these steps to build and run the application using Docker:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Reminerva/bucket-cinema-api.git
    cd bucket-cinema-api
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

### Initial Data (Seeder)

To facilitate testing and development, this application comes equipped with a **data seeder** that automatically populates the database with initial data when the application first starts (or when `spring.jpa.hibernate.ddl-auto=update` detects schema changes and performs initialization).

This means you **do not need to perform POST requests** to create basic data (such as users, theaters, etc.) if you only want to try GET requests on existing endpoints. Simply run the application, and the initial data will be available.

Here are some important initial data details: To get authenticated as an admin, customer, employee, or cashier user, you can use the following credentials:
* **Admin User**: `admin@flix.com` / `password` (to obtain admin token)
* **Customer User**: `budi@example.com` / `password` (to obtain customer token)
* **Employee User**: `empName1@flix.com` / `password` (to obtain employee token)
* **Cashier User**: `empName5@flix.com` / `password` (to obtain cashier token)

### Postman Collection

To facilitate testing and interaction with the API, you can import the provided Postman collection.

* **File Location:** `postman_collection/PostmanCollection.postman_collection.json`
* **How to Import:**
    1.  Open your Postman application.
    2.  Click the **"Import"** button in the top left corner.
    3.  Select the **"File"** tab and click **"Upload Files"**.
    4.  Navigate to your project directory and select the `PostmanCollection.postman_collection.json` file inside the `postman_collection` folder.
    5.  Follow the instructions to complete the import process.

This collection contains example requests for various API endpoints, including authentication endpoints, which will be very helpful in understanding how the API works and performing tests. Be sure to update Postman environment variables (such as `baseUrl` and tokens if necessary) to match your environment.

## 2. API Endpoints

This section details all the available API endpoints. All successful responses will follow the `CommonResponse` structure.

<details>
<summary><h2>Authentication</h2></summary>

**Auth Base Path:** `/api/v1/user/auth`

* **`POST {Auth Base Path}/signup` - Register a new user (Customer)**
    * **Description:** Allows a new customer to register an account.
    * **Roles:** Public
    * **Request Example (NewCustomerRequest):**
        ```json
        {
            "fullname": "John Doe",
            "country": "United States",
            "phoneNumber": "1234567890",
            "city": "New York",
            "gender": "MALE",
            "birthDate": "1990-01-15",
            "username": "johndoe123",
            "email": "john.doe@example.com",
            "password": "securepassword123"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Sign up success",
            "data": {
                "id": "1dd24fb0-9c9c-4dc5-b29a-adf6286e8865",
                "userAppId": "a3b3ad25-b89c-403e-85ae-e5522e7eab8b",
                "userAppUsername": "johndoe123",
                "userAppEmail": "john.doe@example.com",
                "fullname": "John Doe",
                "birthDate": "1990-01-15",
                "country": "United States",
                "phoneNumber": "1234567890",
                "city": "New York",
                "gender": "Male",
                "registrationDate": "2025-05-26",
                "lastLogin": "2025-05-26",
                "favGenre": [],
                "likeProductId": [],
                "dislikeProductId": []
            },
            "paging": null
        }
        ```
* **`POST {Auth Base Path}/signin` - User Login**
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
            "code": 202,
            "message": "Sign in success",
            "data": {
                "accountId": "admin-001",
                "email": "admin@flix.com",
                "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBmbGl4LmNvbSIsInJvbGUiOiJbUk9MRV9BRE1JTl0iLCJleHAiOjE3NDg2MjQ0MDB9.Jbex-GNi-JRUm3e6GLB__KA-kEcOWXzrl5YXs4vgTgxcKmag2HSl0Kr4bUEJE-8ECHazY4Qv6p_vy-y9ypvgHQ",
                "role": "[ROLE_ADMIN]"
            },
            "paging": null
        }
        ```
* **`POST {Auth Base Path}/signout` - User Logout**
    * **Description:** Invalidates the user's session/token.
    * **Roles:** Authenticated Users (Admin, Cashier, Customer)
    * **Request Example:** (No request body needed, typically uses JWT in header)
        ```json
        {}
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Sign out success",
            "data": {
                "statusMessage": "Logout successful",
                "accessToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBmbGl4LmNvbSIsInJvbGUiOiJbUk9MRV9BRE1JTl0iLCJleHAiOjE3NDg2MjQ0MDB9.Jbex-GNi-JRUm3e6GLB__KA-kEcOWXzrl5YXs4vgTgxcKmag2HSl0Kr4bUEJE-8ECHazY4Qv6p_vy-y9ypvgHQ"
            },
            "paging": null
        }
        ```
</details>

<details>
<summary><h2>User Management</h2></summary>

**User Base Path:** `/api/v1/user`

* **`GET {User Base Path}` - Get All Users**
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
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get all user success!",
            "data": [
                {
                    "id": "admin-001",
                    "username": "admin",
                    "email": "admin@flix.com",
                    "role": [
                        "ROLE_ADMIN"
                    ],
                    "customerFullname": ""
                }
            ],
            "paging": {
                "totalPages": 2,
                "totalElement": 11,
                "page": 1,
                "size": 10,
                "hasNext": true,
                "hasPrevious": false
            }
        }
        ```
</details>

<details>
<summary><h2>Customer Management</h2></summary>

**Customer Base Path:** `/api/v1/customer`

* **`POST {Customer Base Path}` - Create New Customer (Admin Only)**
    * **Description:** Allows an admin to create a new customer account.
    * **Roles:** Admin
    * **Request Example (NewCustomerRequest):**
        ```json
        {
            "fullname": "John Doe",
            "country": "United States",
            "phoneNumber": "1234567890",
            "city": "New York",
            "gender": "MALE",
            "birthDate": "1990-01-15",
            "username": "johndoe123",
            "email": "john.doe@example.com",
            "password": "securepassword123"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Customer created successfully!",
            "data": {
                "id": "71d8cd4b-94dc-40f0-9da6-7a05e60d4417",
                "userAppId": "8f7a59a3-a76a-456a-88ff-4c8ddb84520f",
                "userAppUsername": "johndoe123",
                "userAppEmail": "john.doe@example.com",
                "fullname": "John Doe",
                "birthDate": "1990-01-15",
                "country": "United States",
                "phoneNumber": "1234567890",
                "city": "New York",
                "gender": "Male",
                "registrationDate": "2025-05-26",
                "lastLogin": "2025-05-26",
                "favGenre": [],
                "likeProductId": [],
                "dislikeProductId": []
            },
            "paging": null
        }
        ```
* **`GET {Customer Base Path}` - Get All Customers (Admin Only)**
    * **Description:** Retrieves a paginated list of all customer accounts.
    * **Roles:** Admin
    * **Query Parameters:**
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
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get all customer success!",
            "data": [
                {
                    "id": "cust-001",
                    "userAppId": "user-001",
                    "userAppUsername": "budi",
                    "userAppEmail": "budi@example.com",
                    "fullname": "Budi Santoso",
                    "birthDate": "1990-01-01",
                    "country": "Indonesia",
                    "phoneNumber": "081234567890",
                    "city": "Bandung",
                    "gender": "Male",
                    "registrationDate": "2025-05-26",
                    "lastLogin": "2025-05-26",
                    "favGenre": [
                        "Action",
                        "Comedy"
                    ],
                    "likeProductId": [
                        "prod-001",
                        "prod-002",
                        "prod-003"
                    ],
                    "dislikeProductId": []
                }
            ],
            "paging": {
                "totalPages": 1,
                "totalElement": 4,
                "page": 1,
                "size": 10,
                "hasNext": false,
                "hasPrevious": false
            }
        }
        ```
* **`GET {Customer Base Path}/{id}` - Get Customer By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a customer's details by their ID.
    * **Roles:** Admin, Cashier, Customer (if `id` matches their own)
    * **Path Parameters:**
        * `id` (string, required): The ID of the customer.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get customer success!",
            "data": {
                "id": "cust-001",
                "userAppId": "user-001",
                "userAppUsername": "budi",
                "userAppEmail": "budi@example.com",
                "fullname": "Budi Santoso",
                "birthDate": "1990-01-01",
                "country": "Indonesia",
                "phoneNumber": "081234567890",
                "city": "Bandung",
                "gender": "Male",
                "registrationDate": "2025-05-26",
                "lastLogin": "2025-05-26",
                "favGenre": [
                    "Action",
                    "Comedy"
                ],
                "likeProductId": [
                    "prod-001",
                    "prod-002",
                    "prod-003"
                ],
                "dislikeProductId": []
            },
            "paging": null
        }
        ```
* **`PUT {Customer Base Path}/{id}` - Update Customer By ID (Admin Only)**
    * **Description:** Updates a customer's details by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the customer to update.
    * **Request Example (UpdateCustomerRequest):**
        ```json
        {
            "fullname": "Budi Santoso",
            "birthDate": "1990-01-01",
            "country": "Indonesia",
            "phoneNumber": "081234567890",
            "city": "Bandung",
            "gender": "Male",
            "registrationDate": "2025-05-26",
            "lastLogin": "2025-05-26",
            "favGenre": [
                "Action",
                "Comedy"
            ],
            "likeProductId": [
                "prod-001",
                "prod-002",
                "prod-003"
            ],
            "dislikeProductId": []
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Customer updated successfully!",
            "data": {
                "id": "cust-001",
                "userAppId": "user-001",
                "userAppUsername": "budi",
                "userAppEmail": "budi@example.com",
                "fullname": "Budi Santoso",
                "birthDate": "1990-01-01",
                "country": "Indonesia",
                "phoneNumber": "081234567890",
                "city": "Bandung",
                "gender": "Male",
                "registrationDate": "2025-05-26",
                "lastLogin": "2025-05-26",
                "favGenre": [
                    "Action",
                    "Comedy"
                ],
                "likeProductId": [
                    "prod-001",
                    "prod-002",
                    "prod-003"
                ],
                "dislikeProductId": []
            },
            "paging": null
        }
        ```
* **`DELETE {Customer Base Path}/{id}` - Delete Customer By ID (Admin Only)**
    * **Description:** Deletes a customer account by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the customer to delete.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Customer deleted successfully!",
            "data": null,
            "paging": null
        }
        ```
* **`GET {Customer Base Path}/me` - Get Current Customer's Details (Customer Only)**
    * **Description:** Retrieves the details of the currently authenticated customer.
    * **Roles:** Customer
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get customer success!",
            "data": {
                "id": "cust-001",
                "userAppId": "user-001",
                "userAppUsername": "budi",
                "userAppEmail": "budi@example.com",
                "fullname": "Budi Santoso",
                "birthDate": "1990-01-01",
                "country": "Indonesia",
                "phoneNumber": "081234567890",
                "city": "Bandung",
                "gender": "Male",
                "registrationDate": "2025-05-26",
                "lastLogin": "2025-05-26",
                "favGenre": [
                    "Action",
                    "Comedy"
                ],
                "likeProductId": [
                    "prod-001",
                    "prod-002",
                    "prod-003"
                ],
                "dislikeProductId": []
            },
            "paging": null
        }
        ```
* **`PUT {Customer Base Path}/me` - Update Current Customer's Details (Customer Only)**
    * **Description:** Updates the details of the currently authenticated customer.
    * **Roles:** Customer
    * **Request Example (UpdateCustomerRequest):**
        ```json
        {
            "fullname": "Budi Santoso",
            "birthDate": "1990-01-01",
            "country": "Indonesia",
            "phoneNumber": "081234567890",
            "city": "Bandung",
            "gender": "Male",
            "registrationDate": "2025-05-26",
            "lastLogin": "2025-05-26",
            "favGenre": [
                "Action",
                "Comedy"
            ],
            "likeProductId": [
                "prod-001",
                "prod-002",
                "prod-003"
            ],
            "dislikeProductId": []
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Customer updated successfully!",
            "data": {
                "id": "cust-001",
                "userAppId": "user-001",
                "userAppUsername": "budi",
                "userAppEmail": "budi@example.com",
                "fullname": "Budi Santoso",
                "birthDate": "1990-01-01",
                "country": "Indonesia",
                "phoneNumber": "081234567890",
                "city": "Bandung",
                "gender": "Male",
                "registrationDate": "2025-05-26",
                "lastLogin": "2025-05-26",
                "favGenre": [
                    "Action",
                    "Comedy"
                ],
                "likeProductId": [
                    "prod-001",
                    "prod-002",
                    "prod-003"
                ],
                "dislikeProductId": []
            },
            "paging": null
        }
        ```
</details>

<details>
<summary><h2>Employee Management</h2></summary>

**Employee Base Path:** `/api/v1/employee`

* **`POST {Employee Base Path}` - Create New Employee (Employee Only)**
    * **Description:** Allows an admin to create a new Employee.
    * **Roles:** Admin
    * **Request Example (NewEmployeeRequest):**
        ```json
        {
            "fullname": "Jane",
            "nikNumber": "8888888888888888",
            "address": "Jl. Raya Jakarta",
            "phoneNumber": "089876543210",
            "gender": "FEMALE",
            "city": "Jakarta",
            "dateOfBirth": "1995-05-05",
            "dateOfAppliment": "2025-05-19",
            "theaterId": "theater-002",
            "username": "Jane",
            "email": "jane@gmail.com",
            "password": "password"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Employee created successfully!",
            "data": {
                "id": "c3bca552-060d-4a28-bf3c-18e8f646d452",
                "fullname": "Jane",
                "nikNumber": "8888888888888888",
                "address": "Jl. Raya Jakarta",
                "phoneNumber": "089876543210",
                "gender": "GENDER_FEMALE",
                "city": "Jakarta",
                "dateOfBirth": "1995-05-05",
                "dateOfAppliment": "2025-05-19",
                "appUserId": "941acf5b-b157-4c4e-868e-ff557871a520",
                "appUserUsername": "Jane",
                "appUserEmail": "jane@gmail.com",
                "theaterId": "theater-002",
                "transactionsId": [],
                "isActive": true
            },
            "paging": null
        }
        ```
* **`POST {Employee Base Path}/admin` - Create New Admin (Admin Only)**
    * **Description:** Allows an admin to create a new admin account.
    * **Roles:** Admin
    * **Request Example (NewAdminRequest):**
        ```json
        {
            "username": "Jane",
            "email": "jane@gmail.com",
            "password": "password"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Admin created successfully!",
            "data": {
                "accountId": "3882dcda-9518-4c2d-a455-b60eea96cafd",
                "email": "jane@gmail.com",
                "role": "[ROLE_ADMIN]"
            },
            "paging": null
        }
        ```
* **`POST {Employee Base Path}/cashier` - Create New Cashier (Admin Only)**
    * **Description:** Allows an admin to create a new cashier account.
    * **Roles:** Admin
    * **Request Example (NewCashierRequest):**
        ```json
        {
            "theaterId": "theater-001",
            "username": "Jane",
            "email": "jane@gmail.com",
            "password": "password"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Cashier created successfully!",
            "data": {
                "id": "2dfa04c5-804f-4cfe-95f4-cd8665a00676",
                "fullname": null,
                "nikNumber": null,
                "address": null,
                "phoneNumber": null,
                "gender": null,
                "city": null,
                "dateOfBirth": null,
                "dateOfAppliment": null,
                "appUserId": "6006ab91-8034-45d8-9f56-615dfd55d4c0",
                "appUserUsername": "Jane",
                "appUserEmail": "jane@gmail.com",
                "theaterId": "theater-001",
                "transactionsId": [],
                "isActive": true
            },
            "paging": null
        }
        ```
* **`GET {Employee Base Path}` - Get All Employees (Admin Only)**
    * **Description:** Retrieves a paginated list of all employee accounts (Admins and Cashiers).
    * **Roles:** Admin
    * **Query Parameters:**
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
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get all employee successfully!",
            "data": [
                {
                    "id": "emp-002",
                    "fullname": "Beruang",
                    "nikNumber": "2222222222222222",
                    "address": "Jl. Raya Jakarta",
                    "phoneNumber": "089876543210",
                    "gender": "GENDER_FEMALE",
                    "city": "Jakarta",
                    "dateOfBirth": "1995-05-05",
                    "dateOfAppliment": "2025-05-26",
                    "appUserId": "useremp-002",
                    "appUserUsername": "empName2",
                    "appUserEmail": "empName2@flix.com",
                    "theaterId": "theater-001",
                    "transactionsId": [],
                    "isActive": true
                }
            ],
            "paging": {
                "totalPages": 1,
                "totalElement": 7,
                "page": 1,
                "size": 10,
                "hasNext": false,
                "hasPrevious": false
            }
        }
        ```
* **`GET {Employee Base Path}/{id}` - Get Employee By ID (Admin Only)**
    * **Description:** Retrieves an employee's details by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the employee.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get employee successfully!",
            "data": {
                "id": "emp-002",
                "fullname": "Beruang",
                "nikNumber": "2222222222222222",
                "address": "Jl. Raya Jakarta",
                "phoneNumber": "089876543210",
                "gender": "GENDER_FEMALE",
                "city": "Jakarta",
                "dateOfBirth": "1995-05-05",
                "dateOfAppliment": "2025-05-26",
                "appUserId": "useremp-002",
                "appUserUsername": "empName2",
                "appUserEmail": "empName2@flix.com",
                "theaterId": "theater-001",
                "transactionsId": [],
                "isActive": true
            },
            "paging": null
        }
        ```
* **`PUT {Employee Base Path}/{id}` - Update Employee By ID (Admin Only)**
    * **Description:** Updates an employee's details by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the employee to update.
    * **Request Example (UpdateEmployeeRequest):**
        ```json
        {
            "fullname": "Jane",
            "nikNumber": "8888888888888888",
            "address": "Jl. Raya Jakarta",
            "phoneNumber": "089876543210",
            "gender": "FEMALE",
            "city": "Jakarta",
            "dateOfBirth": "1995-05-05",
            "dateOfAppliment": "2025-05-19",
            "theaterId": "theater-002"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Employee updated successfully!",
            "data": {
                "id": "emp-002",
                "fullname": "Jane",
                "nikNumber": "8888888888888888",
                "address": "Jl. Raya Jakarta",
                "phoneNumber": "089876543210",
                "gender": "GENDER_FEMALE",
                "city": "Jakarta",
                "dateOfBirth": "1995-05-05",
                "dateOfAppliment": "2025-05-19",
                "appUserId": "useremp-002",
                "appUserUsername": "empName2",
                "appUserEmail": "empName2@flix.com",
                "theaterId": "theater-002",
                "transactionsId": [],
                "isActive": true
            },
            "paging": null
        }
        ```
* **`DELETE {Employee Base Path}/{id}` - Soft Delete Employee By ID (Admin Only)**
    * **Description:** Soft deletes an employee account by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the employee to soft delete.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Employee deleted successfully!",
            "data": null,
            "paging": null
        }
        ```
* **`GET {Employee Base Path}/me` - Get Current Employee's Details (Employee Only)**
    * **Description:** Retrieves the details of the currently authenticated employee.
    * **Roles:** Employee
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get employee successfully!",
            "data": {
                "id": "emp-001",
                "fullname": "Panda",
                "nikNumber": "1111111111111111",
                "address": "Jl. Raya Bandung",
                "phoneNumber": "081234567890",
                "gender": "GENDER_MALE",
                "city": "Bandung",
                "dateOfBirth": "1990-01-01",
                "dateOfAppliment": "2025-05-26",
                "appUserId": "useremp-001",
                "appUserUsername": "empName1",
                "appUserEmail": "empName1@flix.com",
                "theaterId": "theater-001",
                "transactionsId": [],
                "isActive": true
            },
            "paging": null
        }
        ```
* **`PUT {Employee Base Path}/me` - Update Current Employee's Details (Employee Only)**
    * **Description:** Updates the details of the currently authenticated employee.
    * **Roles:** Employee
    * **Request Example (UpdateEmployeeRequest):**
        ```json
        {
            "fullname": "Jane",
            "nikNumber": "8888888888888888",
            "address": "Jl. Raya Jakarta",
            "phoneNumber": "089876543210",
            "gender": "FEMALE",
            "city": "Jakarta",
            "dateOfBirth": "1995-05-05",
            "dateOfAppliment": "2025-05-19",
            "theaterId": "theater-002"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Employee updated successfully!",
            "data": {
                "id": "emp-001",
                "fullname": "Jane",
                "nikNumber": "8888888888888888",
                "address": "Jl. Raya Jakarta",
                "phoneNumber": "089876543210",
                "gender": "GENDER_FEMALE",
                "city": "Jakarta",
                "dateOfBirth": "1995-05-05",
                "dateOfAppliment": "2025-05-19",
                "appUserId": "useremp-001",
                "appUserUsername": "empName1",
                "appUserEmail": "empName1@flix.com",
                "theaterId": "theater-002",
                "transactionsId": [],
                "isActive": true
            },
            "paging": null
        }
        ```
</details>

<details>
<summary><h2>Artist Management</h2></summary>

**Artist Base Path:** `/api/v1/artist`

* **`POST {Artist Base Path}` - Create New Artist (Admin Only)**
    * **Description:** Creates a new artist record.
    * **Roles:** Admin
    * **Request Example (NewArtistRequest):**
        ```json
        {
            "name": "Robert John Downey Sr.",
            "placeOfBirth": "Garut, United States",
            "birthDate": "1944-04-04",
            "otherName": "Robert",
            "bio": "Actor, producer, and philanthropist.",
            "artistTypes": ["Actor", "Producer"]
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Artist created successfully!",
            "data": {
                "id": "9a9d6790-e453-498c-9690-591eff9adf96",
                "name": "Robert John Downey Sr.",
                "placeOfBirth": "Garut, United States",
                "birthDate": "1944-04-04",
                "otherName": "Robert",
                "bio": "Actor, producer, and philanthropist.",
                "artistTypes": [
                    "Actor",
                    "Producer"
                ],
                "productTitle": null
            },
            "paging": null
        }
        ```
* **`GET {Artist Base Path}` - Get All Artists (Admin, Cashier, Customer)**
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
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get all artist successfully!",
            "data": [
                {
                    "id": "00000000-0000-0000-0000-000000000001",
                    "name": "Chris Evans",
                    "placeOfBirth": "Boston",
                    "birthDate": "1981-06-13",
                    "otherName": null,
                    "bio": "American actor.",
                    "artistTypes": [
                        "Actor"
                    ],
                    "productTitle": [
                        "Avengers: Endgame"
                    ]
                }
            ],
            "paging": {
                "totalPages": 2,
                "totalElement": 17,
                "page": 1,
                "size": 10,
                "hasNext": true,
                "hasPrevious": false
            }
        }
        ```
* **`GET {Artist Base Path}/{id}` - Get Artist By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves an artist's details by their ID.
    * **Roles:** Admin, Cashier, Customer
    * **Path Parameters:**
        * `id` (string, required): The ID of the artist.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get artist successfully!",
            "data": {
                "id": "00000000-0000-0000-0000-000000000001",
                "name": "Chris Evans",
                "placeOfBirth": "Boston",
                "birthDate": "1981-06-13",
                "otherName": null,
                "bio": "American actor.",
                "artistTypes": [
                    "Actor"
                ],
                "productTitle": [
                    "Avengers: Endgame"
                ]
            },
            "paging": null
        }
        ```
* **`PUT {Artist Base Path}/{id}` - Update Artist By ID (Admin Only)**
    * **Description:** Updates an artist's details by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the artist to update.
    * **Request Example (NewArtistRequest):**
        ```json
        {
            "name": "Robert John Downey Sr.",
            "placeOfBirth": "Garut, United States",
            "birthDate": "1944-04-04",
            "otherName": "Robert",
            "bio": "Actor, producer, and philanthropist.",
            "artistTypes": ["Actor"]
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Artist updated successfully!",
            "data": {
                "id": "00000000-0000-0000-0000-000000000001",
                "name": "Robert John Downey Sr.",
                "placeOfBirth": "Garut, United States",
                "birthDate": "1944-04-04",
                "otherName": "Robert",
                "bio": "Actor, producer, and philanthropist.",
                "artistTypes": [
                    "Actor"
                ],
                "productTitle": [
                    "Avengers: Endgame"
                ]
            },
            "paging": null
        }
        ```
* **`DELETE {Artist Base Path}/{id}` - Delete Artist By ID (Admin Only)**
    * **Description:** Deletes an artist record by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the artist to delete.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Artist deleted successfully!",
            "data": null,
            "paging": null
        }
        ```
</details>

<details>
<summary><h2>Production Company Management</h2></summary>

**Production Company Base Path:** `/api/v1/production-company`

* **`POST {Production Company Base Path}` - Create New Production Company (Admin Only)**
    * **Description:** Creates a new production company record.
    * **Roles:** Admin
    * **Request Example (NewProductionCompanyRequest):**
        ```json
        {
            "name": "Warner Bros. Pictures",
            "logoUrl": "[http://example.com/warner_logo.png](http://example.com/warner_logo.png)",
            "originCountry": "United States",
            "websiteUrl": "[http://www.warnerbros.com](http://www.warnerbros.com)",
            "headquarters": "Burbank, California",
            "ceo": "Ann Sarnoff",
            "description": "An American film production and distribution company.",
            "contactEmail": "info@warnerbros.com",
            "contactNumber": "1-818-954-6000",
            "foundedYear": "1923-01-01"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Create production company success",
            "data": {
                "id": "91fac1aa-27ec-4266-8cdd-6115ef2f98f8",
                "name": "Warner Bros. Pictures",
                "logoUrl": "[http://example.com/warner_logo.png](http://example.com/warner_logo.png)",
                "originCountry": "United States",
                "websiteUrl": "[http://www.warnerbros.com](http://www.warnerbros.com)",
                "headquarters": "Burbank, California",
                "ceo": "Ann Sarnoff",
                "description": "An American film production and distribution company.",
                "contactEmail": "info@warnerbros.com",
                "contactNumber": "1-818-954-6000",
                "foundedYear": "1923-01-01",
                "createdAt": "2025-05-26",
                "updatedAt": "2025-05-26",
                "productTitle": []
            },
            "paging": null
        }
        ```
* **`GET {Production Company Base Path}` - Get All Production Companies (Admin, Cashier, Customer)**
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
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get all production company success",
            "data": [
                {
                    "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                    "name": "Marvel Studios",
                    "logoUrl": "https://example.com/marvel_logo.png",
                    "originCountry": "United States",
                    "websiteUrl": "https://www.marvel.com",
                    "headquarters": "Burbank, California",
                    "ceo": "Kevin Feige",
                    "description": "American film and television production company.",
                    "contactEmail": "contact@marvel.com",
                    "contactNumber": "1-800-MARVEL",
                    "foundedYear": "1993-09-08",
                    "createdAt": "2025-05-26",
                    "updatedAt": "2025-05-26",
                    "productTitle": [
                        "Avengers: Endgame"
                    ]
                }
            ],
            "paging": {
                "totalPages": 1,
                "totalElement": 3,
                "page": 1,
                "size": 10,
                "hasNext": false,
                "hasPrevious": false
            }
        }
        ```
* **`GET {Production Company Base Path}/{id}` - Get Production Company By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a production company's details by their ID.
    * **Roles:** Admin, Cashier, Customer
    * **Path Parameters:**
        * `id` (string, required): The ID of the production company.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get production company success",
            "data": {
                "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                "name": "Marvel Studios",
                "logoUrl": "https://example.com/marvel_logo.png",
                "originCountry": "United States",
                "websiteUrl": "https://www.marvel.com",
                "headquarters": "Burbank, California",
                "ceo": "Kevin Feige",
                "description": "American film and television production company.",
                "contactEmail": "contact@marvel.com",
                "contactNumber": "1-800-MARVEL",
                "foundedYear": "1993-09-08",
                "createdAt": "2025-05-26",
                "updatedAt": "2025-05-26",
                "productTitle": [
                    "Avengers: Endgame"
                ]
            },
            "paging": null
        }
        ```
* **`PUT {Production Company Base Path}/{id}` - Update Production Company By ID (Admin Only)**
    * **Description:** Updates a production company's details by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the production company to update.
    * **Request Example (NewProductionCompanyRequest):**
        ```json
        {
            "name": "Warner Bros Pict.",
            "logoUrl": "[http://example.com/warner_logo.png](http://example.com/warner_logo.png)",
            "originCountry": "United States",
            "websiteUrl": "[http://www.warnerbros.com](http://www.warnerbros.com)",
            "headquarters": "Burbank, California",
            "ceo": "Ann Sarnoff",
            "description": "An American film production and distribution company.",
            "contactEmail": "info@warnerbros.com",
            "contactNumber": "1-818-954-6000",
            "foundedYear": "1923-01-01"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Update production company success",
            "data": {
                "id": "91fac1aa-27ec-4266-8cdd-6115ef2f98f8",
                "name": "Warner Bro Pict.",
                "logoUrl": "[http://example.com/warner_logo.png](http://example.com/warner_logo.png)",
                "originCountry": "United States",
                "websiteUrl": "[http://www.warnerbros.com](http://www.warnerbros.com)",
                "headquarters": "Burbank, California",
                "ceo": "Ann Sarnoff",
                "description": "An American film production and distribution company.",
                "contactEmail": "info@warnerbros.com",
                "contactNumber": "1-818-954-6000",
                "foundedYear": "1923-01-01",
                "createdAt": "2025-05-26",
                "updatedAt": "2025-05-26",
                "productTitle": []
            },
            "paging": null
        }
        ```
* **`DELETE {Production Company Base Path}/{id}` - Delete Production Company By ID (Admin Only)**
    * **Description:** Deletes a production company record by their ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the production company to delete.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Production company deleted successfully!",
            "data": null,
            "paging": null
        }
        ```
</details>

<details>
<summary><h2>Product Management</h2></summary>

**Product Base Path:** `/api/v1/product`

* **`POST {Product Base Path}` - Create New Product (Admin Only)**
    * **Description:** Creates a new product (e.g., movie) record.
    * **Roles:** Admin
    * **Request Example (NewProductRequest):**
        ```json
        // Will be added later
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductResponse)
        }
        ```
* **`GET {Product Base Path}` - Get All Products (Admin, Cashier, Customer)**
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
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            // Will be added later(List<ProductResponse> with paging)
        }
        ```
* **`GET {Product Base Path}/{id}` - Get Product By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a product's details by its ID.
    * **Roles:** Admin, Cashier, Customer
    * **Path Parameters:**
        * `id` (string, required): The ID of the product.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductResponse)
        }
        ```
* **`PUT {Product Base Path}/{id}` - Update Product By ID (Admin Only)**
    * **Description:** Updates a product's details by its ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the product to update.
    * **Request Example (NewProductRequest):**
        ```json
        // Will be added later
        ```
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductResponse)
        }
        ```
* **`DELETE {Product Base Path}/{id}/hard-delete` - Hard Delete Product By ID (Admin Only)**
    * **Description:** Permanently deletes a product record by its ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the product to hard delete.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductResponse)
        }
        ```
* **`DELETE {Product Base Path}/{id}/soft-delete` - Soft Delete Product By ID (Admin Only)**
    * **Description:** Soft deletes a product record by its ID (marks as inactive without permanent deletion).
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the product to soft delete.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            // Will be added later(ProductResponse)
        }
        ```
</details>

<details>
<summary><h2>Theater Management</h2></summary>

**Theater Base Path:** `/api/v1/theater`

* **`POST {Theater Base Path}` - Create New Theater (Admin Only)**
    * **Description:** Creates a new theater record.
    * **Roles:** Admin
    * **Request Example (NewTheaterRequest):**
        ```json
        {
            "name": "CGV Bandung Electronic Center",
            "city": "Bandung",
            "address": "Jl. Purnawarman No.13-15",
            "contactNumber": "022-82060901",
            "contactEmail": "bec@cgv.id"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Create theater success",
            "data": {
                "id": "02b9fc24-7013-4e7b-a30f-fe4bde4c85ea",
                "name": "CGV Bandung Electronic Center",
                "city": "Bandung",
                "address": "Jl. Purnawarman No.13-15",
                "contactNumber": "022-82060901",
                "contactEmail": "bec@cgv.id",
                "createdAt": "2025-05-26",
                "updatedAt": "2025-05-26",
                "oprationalStatus": true,
                "studios": [],
                "nowShowing": [],
                "employees": []
            },
            "paging": null
        }
        ```
* **`GET {Theater Base Path}` - Get All Theaters (Admin, Cashier, Customer)**
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
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get all theater success",
            "data": [
                {
                    "id": "02b9fc24-7013-4e7b-a30f-fe4bde4c85ea",
                    "name": "CGV Bandung Electronic Center",
                    "city": "Bandung",
                    "address": "Jl. Purnawarman No.13-15",
                    "contactNumber": "022-82060901",
                    "contactEmail": "bec@cgv.id",
                    "createdAt": "2025-05-26",
                    "updatedAt": "2025-05-26",
                    "oprationalStatus": true,
                    "studios": [
                        {
                            "id": "studio-001",
                            "name": "Studio 1",
                            "studioSize": "Reguler Small",
                            "seatLayout": [
                                "A1",
                                "A2",
                                "B1",
                                "B2"
                            ],
                            "studioSeatSchedule": [
                                {
                                    "id": "studio_seat_schedule-001",
                                    "studioId": "studio-001",
                                    "productSchedulingId": "psched-001",
                                    "bookedSeat": [],
                                    "availableSeat": [
                                        "A1",
                                        "A2",
                                        "B1",
                                        "B2"
                                    ]
                                },
                                {
                                    "id": "studio_seat_schedule-002",
                                    "studioId": "studio-001",
                                    "productSchedulingId": "psched-002",
                                    "bookedSeat": [],
                                    "availableSeat": [
                                        "A1",
                                        "A2",
                                        "B1",
                                        "B2"
                                    ]
                                }
                            ],
                            "productPricing": [
                                {
                                    "id": "pprice-001",
                                    "weekdayPrice": 50000.0,
                                    "weekendPrice": 75000.0,
                                    "priceDate": "2025-05-24",
                                    "isPriceActive": true,
                                    "productId": "prod-001"
                                },
                                {
                                    "id": "pprice-002",
                                    "weekdayPrice": 45000.0,
                                    "weekendPrice": 65000.0,
                                    "priceDate": "2025-05-24",
                                    "isPriceActive": true,
                                    "productId": "prod-002"
                                }
                            ],
                            "productScheduling": [
                                {
                                    "id": "psched-001",
                                    "schedule": "9:00",
                                    "productId": "prod-001"
                                },
                                {
                                    "id": "psched-002",
                                    "schedule": "12:30",
                                    "productId": "prod-001"
                                }
                            ],
                            "isActive": true,
                            "theaterId": "02b9fc24-7013-4e7b-a30f-fe4bde4c85ea",
                            "theaterName": "CGV Bandung Electronic Center"
                        },
                        {
                            "id": "studio-002",
                            "name": "Studio 2",
                            "studioSize": "Reguler Medium",
                            "seatLayout": [
                                "A1",
                                "A2",
                                "B1",
                                "B2"
                            ],
                            "studioSeatSchedule": [
                                {
                                    "id": "studio_seat_schedule-003",
                                    "studioId": "studio-002",
                                    "productSchedulingId": "psched-003",
                                    "bookedSeat": [],
                                    "availableSeat": [
                                        "C1",
                                        "C2",
                                        "D1"
                                    ]
                                }
                            ],
                            "productPricing": [
                                {
                                    "id": "pprice-002",
                                    "weekdayPrice": 45000.0,
                                    "weekendPrice": 65000.0,
                                    "priceDate": "2025-05-24",
                                    "isPriceActive": true,
                                    "productId": "prod-002"
                                }
                            ],
                            "productScheduling": [
                                {
                                    "id": "psched-003",
                                    "schedule": "15:00",
                                    "productId": "prod-002"
                                }
                            ],
                            "isActive": true,
                            "theaterId": "02b9fc24-7013-4e7b-a30f-fe4bde4c85ea",
                            "theaterName": "CGV Bandung Electronic Center"
                        }
                    ],
                    "nowShowing": [
                        {
                            "id": "prod-001",
                            "title": "Avengers: Endgame",
                            "posterUrl": "https://example.com/avengers_poster.png"
                        },
                        {
                            "id": "prod-002",
                            "title": "The Lion King",
                            "posterUrl": "https://example.com/lionking_poster.png"
                        },
                        {
                            "id": "prod-003",
                            "title": "Inception",
                            "posterUrl": "https://example.com/inception_poster.png"
                        }
                    ],
                    "employees": []
                }
            ],
            "paging": {
                "totalPages": 1,
                "totalElement": 3,
                "page": 1,
                "size": 10,
                "hasNext": false,
                "hasPrevious": false
            }
        }
        ```
* **`GET {Theater Base Path}/{id}` - Get Theater By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a theater's details by its ID.
    * **Roles:** Admin, Cashier, Customer
    * **Path Parameters:**
        * `id` (string, required): The ID of the theater.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get theater success",
            "data": {
                "id": "02b9fc24-7013-4e7b-a30f-fe4bde4c85ea",
                "name": "CGV Bandung Electronic Center",
                "city": "Bandung",
                "address": "Jl. Purnawarman No.13-15",
                "contactNumber": "022-82060901",
                "contactEmail": "bec@cgv.id",
                "createdAt": "2025-05-26",
                "updatedAt": "2025-05-26",
                "oprationalStatus": true,
                "studios": [
                    {
                        "id": "studio-001",
                        "name": "Studio 1",
                        "studioSize": "Reguler Small",
                        "seatLayout": [
                            "A1",
                            "A2",
                            "B1",
                            "B2"
                        ],
                        "studioSeatSchedule": [
                            {
                                "id": "studio_seat_schedule-001",
                                "studioId": "studio-001",
                                "productSchedulingId": "psched-001",
                                "bookedSeat": [],
                                "availableSeat": [
                                    "A1",
                                    "A2",
                                    "B1",
                                    "B2"
                                ]
                            },
                            {
                                "id": "studio_seat_schedule-002",
                                "studioId": "studio-001",
                                "productSchedulingId": "psched-002",
                                "bookedSeat": [],
                                "availableSeat": [
                                    "A1",
                                    "A2",
                                    "B1",
                                    "B2"
                                ]
                            }
                        ],
                        "productPricing": [
                            {
                                "id": "pprice-001",
                                "weekdayPrice": 50000.0,
                                "weekendPrice": 75000.0,
                                "priceDate": "2025-05-24",
                                "isPriceActive": true,
                                "productId": "prod-001"
                            },
                            {
                                "id": "pprice-002",
                                "weekdayPrice": 45000.0,
                                "weekendPrice": 65000.0,
                                "priceDate": "2025-05-24",
                                "isPriceActive": true,
                                "productId": "prod-002"
                            }
                        ],
                        "productScheduling": [
                            {
                                "id": "psched-001",
                                "schedule": "9:00",
                                "productId": "prod-001"
                            },
                            {
                                "id": "psched-002",
                                "schedule": "12:30",
                                "productId": "prod-001"
                            }
                        ],
                        "isActive": true,
                        "theaterId": "02b9fc24-7013-4e7b-a30f-fe4bde4c85ea",
                        "theaterName": "CGV Bandung Electronic Center"
                    },
                    {
                        "id": "studio-002",
                        "name": "Studio 2",
                        "studioSize": "Reguler Medium",
                        "seatLayout": [
                            "A1",
                            "A2",
                            "B1",
                            "B2"
                        ],
                        "studioSeatSchedule": [
                            {
                                "id": "studio_seat_schedule-003",
                                "studioId": "studio-002",
                                "productSchedulingId": "psched-003",
                                "bookedSeat": [],
                                "availableSeat": [
                                    "C1",
                                    "C2",
                                    "D1"
                                ]
                            }
                        ],
                        "productPricing": [
                            {
                                "id": "pprice-002",
                                "weekdayPrice": 45000.0,
                                "weekendPrice": 65000.0,
                                "priceDate": "2025-05-24",
                                "isPriceActive": true,
                                "productId": "prod-002"
                            }
                        ],
                        "productScheduling": [
                            {
                                "id": "psched-003",
                                "schedule": "15:00",
                                "productId": "prod-002"
                            }
                        ],
                        "isActive": true,
                        "theaterId": "02b9fc24-7013-4e7b-a30f-fe4bde4c85ea",
                        "theaterName": "CGV Bandung Electronic Center"
                    }
                ],
                "nowShowing": [
                    {
                        "id": "prod-001",
                        "title": "Avengers: Endgame",
                        "posterUrl": "https://example.com/avengers_poster.png"
                    },
                    {
                        "id": "prod-002",
                        "title": "The Lion King",
                        "posterUrl": "https://example.com/lionking_poster.png"
                    },
                    {
                        "id": "prod-003",
                        "title": "Inception",
                        "posterUrl": "https://example.com/inception_poster.png"
                    }
                ],
                "employees": []
            },
            "paging": null
        }
        ```
* **`PUT {Theater Base Path}/{id}` - Update Theater By ID (Admin Only)**
    * **Description:** Updates a theater's details by its ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the theater to update.
    * **Request Example (NewTheaterRequest):**
        ```json
        {
            "name": "CGV Bandung Electronic Center",
            "city": "Bandung",
            "address": "Jl. Purnawarman No.13-15",
            "contactNumber": "022-82060901",
            "contactEmail": "bec@cgv.id",
            "oprationalStatus": true,
            "studiosId": [
                "studio-001",
                "studio-002"
            ],
            "nowShowingId": [
                "prod-001",
                "prod-002",
                "prod-003"
            ]
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Update theater success",
            "data": {
                "id": "02b9fc24-7013-4e7b-a30f-fe4bde4c85ea",
                "name": "CGV Bandung Electronic Center",
                "city": "Bandung",
                "address": "Jl. Purnawarman No.13-15",
                "contactNumber": "022-82060901",
                "contactEmail": "bec@cgv.id",
                "createdAt": "2025-05-26",
                "updatedAt": "2025-05-26",
                "oprationalStatus": true,
                "studios": [
                    {
                        "id": "studio-001",
                        "name": "Studio 1",
                        "studioSize": "Reguler Small",
                        "seatLayout": [
                            "A1",
                            "A2",
                            "B1",
                            "B2"
                        ],
                        "studioSeatSchedule": [
                            {
                                "id": "studio_seat_schedule-001",
                                "studioId": "studio-001",
                                "productSchedulingId": "psched-001",
                                "bookedSeat": [],
                                "availableSeat": [
                                    "A1",
                                    "A2",
                                    "B1",
                                    "B2"
                                ]
                            },
                            {
                                "id": "studio_seat_schedule-002",
                                "studioId": "studio-001",
                                "productSchedulingId": "psched-002",
                                "bookedSeat": [],
                                "availableSeat": [
                                    "A1",
                                    "A2",
                                    "B1",
                                    "B2"
                                ]
                            }
                        ],
                        "productPricing": [
                            {
                                "id": "pprice-001",
                                "weekdayPrice": 50000.0,
                                "weekendPrice": 75000.0,
                                "priceDate": "2025-05-24",
                                "isPriceActive": true,
                                "productId": "prod-001"
                            },
                            {
                                "id": "pprice-002",
                                "weekdayPrice": 45000.0,
                                "weekendPrice": 65000.0,
                                "priceDate": "2025-05-24",
                                "isPriceActive": true,
                                "productId": "prod-002"
                            }
                        ],
                        "productScheduling": [
                            {
                                "id": "psched-001",
                                "schedule": "9:00",
                                "productId": "prod-001"
                            },
                            {
                                "id": "psched-002",
                                "schedule": "12:30",
                                "productId": "prod-001"
                            }
                        ],
                        "isActive": true,
                        "theaterId": "02b9fc24-7013-4e7b-a30f-fe4bde4c85ea",
                        "theaterName": "CGV Bandung Electronic Center"
                    },
                    {
                        "id": "studio-002",
                        "name": "Studio 2",
                        "studioSize": "Reguler Medium",
                        "seatLayout": [
                            "A1",
                            "A2",
                            "B1",
                            "B2"
                        ],
                        "studioSeatSchedule": [
                            {
                                "id": "studio_seat_schedule-003",
                                "studioId": "studio-002",
                                "productSchedulingId": "psched-003",
                                "bookedSeat": [],
                                "availableSeat": [
                                    "C1",
                                    "C2",
                                    "D1"
                                ]
                            }
                        ],
                        "productPricing": [
                            {
                                "id": "pprice-002",
                                "weekdayPrice": 45000.0,
                                "weekendPrice": 65000.0,
                                "priceDate": "2025-05-24",
                                "isPriceActive": true,
                                "productId": "prod-002"
                            }
                        ],
                        "productScheduling": [
                            {
                                "id": "psched-003",
                                "schedule": "15:00",
                                "productId": "prod-002"
                            }
                        ],
                        "isActive": true,
                        "theaterId": "02b9fc24-7013-4e7b-a30f-fe4bde4c85ea",
                        "theaterName": "CGV Bandung Electronic Center"
                    }
                ],
                "nowShowing": [
                    {
                        "id": "prod-001",
                        "title": "Avengers: Endgame",
                        "posterUrl": "https://example.com/avengers_poster.png"
                    },
                    {
                        "id": "prod-002",
                        "title": "The Lion King",
                        "posterUrl": "https://example.com/lionking_poster.png"
                    },
                    {
                        "id": "prod-003",
                        "title": "Inception",
                        "posterUrl": "https://example.com/inception_poster.png"
                    }
                ],
                "employees": []
            },
            "paging": null
        }
        ```
* **`DELETE {Theater Base Path}/{id}` - Hard Delete Theater By ID (Admin Only)**
    * **Description:** Soft deletes a theater record by its ID (marks as inactive without permanent deletion).
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the theater to soft delete.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Soft delete theater success",
            "data": null,
            "paging": null
        }
        ```
* **`PUT {Theater Base Path}/{id}/refresh-all-seat` - Refresh All Seats (Admin Only)**
    * **Description:** Refreshes all seats for a specific theater.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the theater.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Refresh all seat success",
            "data": {
                "id": "theater-001",
                "name": "CGV Bandung Electronic Center",
                "city": "Bandung",
                "address": "Jl. Purnawarman No.13-15",
                "contactNumber": "02282060901",
                "contactEmail": "bec@cgv.id",
                "createdAt": "2025-05-26",
                "updatedAt": "2025-05-26",
                "oprationalStatus": true,
                "studios": [
                    {
                        "id": "studio-001",
                        "name": "Studio 1",
                        "studioSize": "Reguler Small",
                        "seatLayout": [
                            "A1",
                            "A2",
                            "B1",
                            "B2"
                        ],
                        "studioSeatSchedule": [
                            {
                                "id": "studio_seat_schedule-001",
                                "studioId": "studio-001",
                                "productSchedulingId": "psched-001",
                                "bookedSeat": [],
                                "availableSeat": [
                                    "A1",
                                    "A2",
                                    "B1",
                                    "B2"
                                ]
                            },
                            {
                                "id": "studio_seat_schedule-002",
                                "studioId": "studio-001",
                                "productSchedulingId": "psched-002",
                                "bookedSeat": [],
                                "availableSeat": [
                                    "A1",
                                    "A2",
                                    "B1",
                                    "B2"
                                ]
                            }
                        ],
                        "productPricing": [
                            {
                                "id": "pprice-001",
                                "weekdayPrice": 50000.0,
                                "weekendPrice": 75000.0,
                                "priceDate": "2025-05-24",
                                "isPriceActive": true,
                                "productId": "prod-001"
                            },
                            {
                                "id": "pprice-002",
                                "weekdayPrice": 45000.0,
                                "weekendPrice": 65000.0,
                                "priceDate": "2025-05-24",
                                "isPriceActive": true,
                                "productId": "prod-002"
                            }
                        ],
                        "productScheduling": [
                            {
                                "id": "psched-001",
                                "schedule": "9:00",
                                "productId": "prod-001"
                            },
                            {
                                "id": "psched-002",
                                "schedule": "12:30",
                                "productId": "prod-001"
                            }
                        ],
                        "isActive": true,
                        "theaterId": "theater-001",
                        "theaterName": "CGV Bandung Electronic Center"
                    },
                    {
                        "id": "studio-002",
                        "name": "Studio 2",
                        "studioSize": "Reguler Medium",
                        "seatLayout": [
                            "A1",
                            "A2",
                            "B1",
                            "B2"
                        ],
                        "studioSeatSchedule": [
                            {
                                "id": "studio_seat_schedule-003",
                                "studioId": "studio-002",
                                "productSchedulingId": "psched-003",
                                "bookedSeat": [],
                                "availableSeat": [
                                    "A1",
                                    "A2",
                                    "B1",
                                    "B2"
                                ]
                            }
                        ],
                        "productPricing": [
                            {
                                "id": "pprice-002",
                                "weekdayPrice": 45000.0,
                                "weekendPrice": 65000.0,
                                "priceDate": "2025-05-24",
                                "isPriceActive": true,
                                "productId": "prod-002"
                            }
                        ],
                        "productScheduling": [
                            {
                                "id": "psched-003",
                                "schedule": "15:00",
                                "productId": "prod-002"
                            }
                        ],
                        "isActive": true,
                        "theaterId": "theater-001",
                        "theaterName": "CGV Bandung Electronic Center"
                    }
                ],
                "nowShowing": [
                    {
                        "id": "prod-001",
                        "title": "Avengers: Endgame",
                        "posterUrl": "https://example.com/avengers_poster.png"
                    },
                    {
                        "id": "prod-002",
                        "title": "The Lion King",
                        "posterUrl": "https://example.com/lionking_poster.png"
                    }
                ],
                "employees": [
                    {
                        "id": "emp-001",
                        "fullname": "Panda"
                    },
                    {
                        "id": "emp-002",
                        "fullname": "Beruang"
                    },
                    {
                        "id": "emp-005",
                        "fullname": "Cashier"
                    }
                ]
            },
            "paging": null
        }
        ```
</details>

<details>
<summary><h2>Studio Management</h2></summary>

**Studio Base Path:** `/api/v1/studio`

* **`POST {Studio Base Path}` - Create New Studio (Admin Only)**
    * **Description:** Creates a new studio (screen/hall within a theater) record.
    * **Roles:** Admin
    * **Request Example (NewStudioRequest):**
        ```json
        {
            "name" : "Studio 1",
            "theaterId": "theater-001",
            "studioSize" : "REGULER SMALL",
            "seatLayout": [
                "A1",
                "A2",
                "B1",
                "B2"
            ]
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Create studio success",
            "data": {
                "id": "7a7d8384-4d8b-4653-a5c1-00e2137842a8",
                "name": "Studio 1",
                "studioSize": "Reguler Small",
                "seatLayout": [
                    "A1",
                    "A2",
                    "B1",
                    "B2"
                ],
                "studioSeatSchedule": [],
                "productPricing": [],
                "productScheduling": [],
                "isActive": true,
                "theaterId": "theater-001",
                "theaterName": "CGV Bandung Electronic Center"
            },
            "paging": null
        }
        ```
* **`GET {Studio Base Path}` - Get All Studios (Admin, Cashier, Customer)**
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
    * **Reqest body example:** 
        ```json
        {
            "seatLayout": ["A1", "A2", "B1", "B2"],
        }
        ```
    * **Reqest body example to get all studios:**
        ```json
        {}
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get all studio success",
            "data": [
                {
                    "id": "studio-001",
                    "name": "Studio 1",
                    "studioSize": "Reguler Small",
                    "seatLayout": [
                        "A1",
                        "A2",
                        "B1",
                        "B2"
                    ],
                    "studioSeatSchedule": [
                        {
                            "id": "studio_seat_schedule-001",
                            "studioId": "studio-001",
                            "productSchedulingId": "psched-001",
                            "bookedSeat": [],
                            "availableSeat": [
                                "A1",
                                "A2",
                                "B1",
                                "B2"
                            ]
                        },
                        {
                            "id": "studio_seat_schedule-002",
                            "studioId": "studio-001",
                            "productSchedulingId": "psched-002",
                            "bookedSeat": [],
                            "availableSeat": [
                                "A1",
                                "A2",
                                "B1",
                                "B2"
                            ]
                        }
                    ],
                    "productPricing": [
                        {
                            "id": "pprice-001",
                            "weekdayPrice": 50000.0,
                            "weekendPrice": 75000.0,
                            "priceDate": "2025-05-24",
                            "isPriceActive": true,
                            "productId": "prod-001"
                        },
                        {
                            "id": "pprice-002",
                            "weekdayPrice": 45000.0,
                            "weekendPrice": 65000.0,
                            "priceDate": "2025-05-24",
                            "isPriceActive": true,
                            "productId": "prod-002"
                        }
                    ],
                    "productScheduling": [
                        {
                            "id": "psched-001",
                            "schedule": "9:00",
                            "productId": "prod-001"
                        },
                        {
                            "id": "psched-002",
                            "schedule": "12:30",
                            "productId": "prod-001"
                        }
                    ],
                    "isActive": true,
                    "theaterId": "theater-001",
                    "theaterName": "CGV Bandung Electronic Center"
                }
            ],
            "paging": {
                "totalPages": 1,
                "totalElement": 3,
                "page": 1,
                "size": 10,
                "hasNext": false,
                "hasPrevious": false
            }
        }
        ```
* **`GET {Studio Base Path}/{id}` - Get Studio By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a studio's details by its ID.
    * **Roles:** Admin, Cashier, Customer
    * **Path Parameters:**
        * `id` (string, required): The ID of the studio.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get studio success",
            "data": {
                "id": "studio-001",
                "name": "Studio 1",
                "studioSize": "Reguler Small",
                "seatLayout": [
                    "A1",
                    "A2",
                    "B1",
                    "B2"
                ],
                "studioSeatSchedule": [
                    {
                        "id": "studio_seat_schedule-001",
                        "studioId": "studio-001",
                        "productSchedulingId": "psched-001",
                        "bookedSeat": [],
                        "availableSeat": [
                            "A1",
                            "A2",
                            "B1",
                            "B2"
                        ]
                    },
                    {
                        "id": "studio_seat_schedule-002",
                        "studioId": "studio-001",
                        "productSchedulingId": "psched-002",
                        "bookedSeat": [],
                        "availableSeat": [
                            "A1",
                            "A2",
                            "B1",
                            "B2"
                        ]
                    }
                ],
                "productPricing": [
                    {
                        "id": "pprice-001",
                        "weekdayPrice": 50000.0,
                        "weekendPrice": 75000.0,
                        "priceDate": "2025-05-24",
                        "isPriceActive": true,
                        "productId": "prod-001"
                    },
                    {
                        "id": "pprice-002",
                        "weekdayPrice": 45000.0,
                        "weekendPrice": 65000.0,
                        "priceDate": "2025-05-24",
                        "isPriceActive": true,
                        "productId": "prod-002"
                    }
                ],
                "productScheduling": [
                    {
                        "id": "psched-001",
                        "schedule": "9:00",
                        "productId": "prod-001"
                    },
                    {
                        "id": "psched-002",
                        "schedule": "12:30",
                        "productId": "prod-001"
                    }
                ],
                "isActive": true,
                "theaterId": "theater-001",
                "theaterName": "CGV Bandung Electronic Center"
            },
            "paging": null
        }
        ```
* **`GET {Studio Base Path}/{theaterId}/{productId}` - Get Studios by Product and Theater ID (Cashier, Customer)**
    * **Description:** Retrieves studios associated with a specific product and theater.
    * **Roles:** Cashier, Customer
    * **Path Parameters:**
        * `theaterId` (string, required): The ID of the theater.
        * `productId` (string, required): The ID of the product.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get studio success",
            "data": [
                {
                    "id": "studio-001",
                    "name": "Studio 1",
                    "studioSize": "Reguler Small",
                    "seatLayout": [
                        "A1",
                        "A2",
                        "B1",
                        "B2"
                    ],
                    "studioSeatSchedule": [
                        {
                            "id": "studio_seat_schedule-001",
                            "studioId": "studio-001",
                            "productScheduling": {
                                "id": "psched-001",
                                "schedule": "9:00",
                                "productId": "prod-001"
                            },
                            "bookedSeat": [],
                            "availableSeat": [
                                "A1",
                                "A2",
                                "B1",
                                "B2"
                            ]
                        },
                        {
                            "id": "studio_seat_schedule-002",
                            "studioId": "studio-001",
                            "productScheduling": {
                                "id": "psched-002",
                                "schedule": "12:30",
                                "productId": "prod-001"
                            },
                            "bookedSeat": [],
                            "availableSeat": [
                                "A1",
                                "A2",
                                "B1",
                                "B2"
                            ]
                        }
                    ],
                    "productPricing": [
                        {
                            "id": "pprice-001",
                            "weekdayPrice": 50000.0,
                            "weekendPrice": 75000.0,
                            "priceDate": "2025-05-24",
                            "isPriceActive": true,
                            "productId": "prod-001"
                        }
                    ],
                    "productScheduling": [
                        {
                            "id": "psched-001",
                            "schedule": "9:00",
                            "productId": "prod-001"
                        },
                        {
                            "id": "psched-002",
                            "schedule": "12:30",
                            "productId": "prod-001"
                        }
                    ],
                    "isActive": true,
                    "theaterId": "theater-001",
                    "theaterName": "CGV Bandung Electronic Center"
                }
            ],
            "paging": {
                "totalPages": 1,
                "totalElement": 1,
                "page": 1,
                "size": 1,
                "hasNext": false,
                "hasPrevious": false
            }
        }
        ```
* **`PUT {Studio Base Path}/{id}` - Update Studio By ID (Admin Only)**
    * **Description:** Updates a studio's details by its ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the studio to update.
    * **Request Example (NewStudioRequest):**
        ```json
        {
            "name" : "Studio 1",
            "theaterId": "theater-001",
            "studioSize" : "REGULER SMALL",
            "seatLayout": [
                "A1",
                "A2",
                "B1",
                "B2"
            ],
            "studioSeatScheduleRequests" : [
                {
                    "studioId": "7a7d8384-4d8b-4653-a5c1-00e2137842a8",
                    "productSchedulingId": "psched-001",
                    "bookedSeat": [],
                    "availableSeat": [
                        "A1",
                        "A2",
                        "B1",
                        "B2"
                    ]
                },
                {
                    "studioId": "7a7d8384-4d8b-4653-a5c1-00e2137842a8",
                    "productSchedulingId": "psched-002",
                    "bookedSeat": [],
                    "availableSeat": [
                        "A1",
                        "A2",
                        "B1",
                        "B2"
                    ]
                }
            ],
            "productPricingRequests": [
                {
                    "weekdayPrice": 45000.0,
                    "weekendPrice": 65000.0,
                    "isPriceActive": true,
                    "productId": "prod-002"
                }
            ],
            "productSchedulingRequests": [
                {
                    "schedule": "15:00",
                    "productId": "prod-002"
                },
                {
                    "schedule": "17:00",
                    "productId": "prod-002"
                }
            ]
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Update studio success",
            "data": {
                "id": "7a7d8384-4d8b-4653-a5c1-00e2137842a8",
                "name": "Studio 1",
                "studioSize": "Reguler Small",
                "seatLayout": [
                    "A1",
                    "A2",
                    "B1",
                    "B2"
                ],
                "studioSeatSchedule": [
                    {
                        "id": "f0fff93e-dfa5-4f8d-b174-af1b3b8719f5",
                        "studioId": "7a7d8384-4d8b-4653-a5c1-00e2137842a8",
                        "productSchedulingId": "psched-001",
                        "bookedSeat": [],
                        "availableSeat": [
                            "A1",
                            "A2",
                            "B1",
                            "B2"
                        ]
                    },
                    {
                        "id": "08134e29-0ec9-4980-809f-e33412671448",
                        "studioId": "7a7d8384-4d8b-4653-a5c1-00e2137842a8",
                        "productSchedulingId": "psched-002",
                        "bookedSeat": [],
                        "availableSeat": [
                            "A1",
                            "A2",
                            "B1",
                            "B2"
                        ]
                    }
                ],
                "productPricing": [
                    {
                        "id": "pprice-002",
                        "weekdayPrice": 45000.0,
                        "weekendPrice": 65000.0,
                        "priceDate": "2025-05-24",
                        "isPriceActive": true,
                        "productId": "prod-002"
                    }
                ],
                "productScheduling": [
                    {
                        "id": "psched-003",
                        "schedule": "15:00",
                        "productId": "prod-002"
                    },
                    {
                        "id": "67adfef4-54d9-41ca-9257-a8d4343a9422",
                        "schedule": "17:00",
                        "productId": "prod-002"
                    }
                ],
                "isActive": true,
                "theaterId": "theater-001",
                "theaterName": "CGV Bandung Electronic Center"
            },
            "paging": null
        }
        ```
* **`DELETE {Studio Base Path}/{id}` - Delete Studio By ID (Admin Only)**
    * **Description:** Deletes a studio record by its ID.
    * **Roles:** Admin
    * **Path Parameters:**
        * `id` (string, required): The ID of the studio to delete.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Delete studio success",
            "data": null,
            "paging": null
        }
        ```
* **`GET {Studio Base Path}/me` - Get Current Studio's Details (Cashier Only)**
    * **Description:** Retrieves the details of the current studio.
    * **Roles:** Cashier
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get studio success",
            "data": [
                {
                    "id": "studio-001",
                    "name": "Studio 1",
                    "studioSize": "Reguler Small",
                    "seatLayout": [
                        "A1",
                        "A2",
                        "B1",
                        "B2"
                    ],
                    "studioSeatSchedule": [
                        {
                            "id": "studio_seat_schedule-001",
                            "studioId": "studio-001",
                            "productScheduling": {
                                "id": "psched-001",
                                "schedule": "9:00",
                                "productId": "prod-001"
                            },
                            "bookedSeat": [],
                            "availableSeat": [
                                "A1",
                                "A2",
                                "B1",
                                "B2"
                            ]
                        },
                        {
                            "id": "studio_seat_schedule-002",
                            "studioId": "studio-001",
                            "productScheduling": {
                                "id": "psched-002",
                                "schedule": "12:30",
                                "productId": "prod-001"
                            },
                            "bookedSeat": [],
                            "availableSeat": [
                                "A1",
                                "A2",
                                "B1",
                                "B2"
                            ]
                        }
                    ],
                    "productPricing": [
                        {
                            "id": "pprice-001",
                            "weekdayPrice": 50000.0,
                            "weekendPrice": 75000.0,
                            "priceDate": "2025-05-24",
                            "isPriceActive": true,
                            "productId": "prod-001"
                        },
                        {
                            "id": "pprice-002",
                            "weekdayPrice": 45000.0,
                            "weekendPrice": 65000.0,
                            "priceDate": "2025-05-24",
                            "isPriceActive": true,
                            "productId": "prod-002"
                        }
                    ],
                    "productScheduling": [
                        {
                            "id": "psched-001",
                            "schedule": "9:00",
                            "productId": "prod-001"
                        },
                        {
                            "id": "psched-002",
                            "schedule": "12:30",
                            "productId": "prod-001"
                        }
                    ],
                    "isActive": true,
                    "theaterId": "theater-001",
                    "theaterName": "CGV Bandung Electronic Center"
                }
            ],
            "paging": {
                "totalPages": 1,
                "totalElement": 2,
                "page": 1,
                "size": 10,
                "hasNext": false,
                "hasPrevious": false
            }
        }
        ```

</details>

<details>
<summary><h2>Transaction Management</h2></summary>

**Transaction Base Path:** `/api/v1/transaction`

* **`POST {Transaction Base Path}` - Create New Transaction (Customer, Cashier)**
    * **Description:** Creates a new transaction record for a customer's purchase.
    * **Roles:** Customer, Cashier
    * **Request Example (NewTransactionRequest):**
        ```json
        {
            "theaterId": "theater-001",
            "studioId": "studio-001",
            "productId": "prod-001",
            "productPricingId": "pprice-001",
            "productSchedulingId": "psched-001",
            "qty": 1,
            "tax": 10,
            "transactionDateTime": "2025-12-12 12:10:12",
            "watchDate": "2025-12-12",
            "paymentDateTime": "2025-12-12 12:12:12",
            "paymentMethod": "CASH",
            "seats": [
                "A1"
            ]
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 201,
            "message": "Create transaction success",
            "data": {
                "id": "b2befd9a-2619-477d-aaf1-ecbd80999aa6",
                "customerId": null,
                "employeeCashierId": "emp-005",
                "theaterId": "theater-001",
                "studioId": "studio-001",
                "productId": "prod-001",
                "productPricingId": "pprice-001",
                "productSchedulingId": "psched-001",
                "qty": 1,
                "tax": 10,
                "transactionDateTime": "2025-12-12T12:10:12",
                "paymentStatus": "Pending",
                "paymentDateTime": "2025-12-12T12:12:12",
                "paymentMethod": "Cash",
                "seats": [
                    "A1"
                ],
                "createdAt": "2025-05-26T23:27:22.819025600",
                "updatedAt": "2025-05-26T23:27:22.819025600",
                "total": 55000.00,
                "watchDate": "2025-12-12"
            },
            "paging": null
        }
        ```
* **`GET {Transaction Base Path}` - Get All Transactions (Admin)**
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
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get all transaction success",
            "data": [
                {
                    "id": "b2befd9a-2619-477d-aaf1-ecbd80999aa6",
                    "customerId": null,
                    "employeeCashierId": "emp-005",
                    "theaterId": "theater-001",
                    "studioId": "studio-001",
                    "productId": "prod-001",
                    "productPricingId": "pprice-001",
                    "productSchedulingId": "psched-001",
                    "qty": 1,
                    "tax": 10,
                    "transactionDateTime": "2025-12-12T12:10:12",
                    "paymentStatus": "Pending",
                    "paymentDateTime": "2025-12-12T12:12:12",
                    "paymentMethod": "Cash",
                    "seats": [
                        "A1"
                    ],
                    "createdAt": "2025-05-26T23:27:22.819026",
                    "updatedAt": "2025-05-26T23:27:22.819026",
                    "total": 55000.00,
                    "watchDate": "2025-12-12"
                }
            ],
            "paging": {
                "totalPages": 1,
                "totalElement": 1,
                "page": 1,
                "size": 10,
                "hasNext": false,
                "hasPrevious": false
            }
        }
        ```
* **`GET {Transaction Base Path}/me` - Get All Transactions (Customer, Cashier)**
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
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get all transaction success",
            "data": [
                {
                    "id": "b2befd9a-2619-477d-aaf1-ecbd80999aa6",
                    "customerId": null,
                    "employeeCashierId": "emp-005",
                    "theaterId": "theater-001",
                    "studioId": "studio-001",
                    "productId": "prod-001",
                    "productPricingId": "pprice-001",
                    "productSchedulingId": "psched-001",
                    "qty": 1,
                    "tax": 10,
                    "transactionDateTime": "2025-12-12T12:10:12",
                    "paymentStatus": "Pending",
                    "paymentDateTime": "2025-12-12T12:12:12",
                    "paymentMethod": "Cash",
                    "seats": [
                        "A1"
                    ],
                    "createdAt": "2025-05-26T23:27:22.819026",
                    "updatedAt": "2025-05-26T23:27:22.819026",
                    "total": 55000.00,
                    "watchDate": "2025-12-12"
                }
            ],
            "paging": {
                "totalPages": 1,
                "totalElement": 1,
                "page": 1,
                "size": 10,
                "hasNext": false,
                "hasPrevious": false
            }
        }
        ```
* **`GET {Transaction Base Path}/{id}` - Get Transaction By ID (Admin, Cashier, Customer)**
    * **Description:** Retrieves a transaction's details by its ID.
    * **Roles:** Admin, Cashier, Customer (if `id` matches their own transaction)
    * **Path Parameters:**
        * `id` (string, required): The ID of the transaction.
    * **Request Example:** (No request body)
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Get transaction success",
            "data": {
                "id": "b2befd9a-2619-477d-aaf1-ecbd80999aa6",
                "customerId": null,
                "employeeCashierId": "emp-005",
                "theaterId": "theater-001",
                "studioId": "studio-001",
                "productId": "prod-001",
                "productPricingId": "pprice-001",
                "productSchedulingId": "psched-001",
                "qty": 1,
                "tax": 10,
                "transactionDateTime": "2025-12-12T12:10:12",
                "paymentStatus": "Success",
                "paymentDateTime": "2025-12-12T12:12:12",
                "paymentMethod": "Cash",
                "seats": [
                    "A1"
                ],
                "createdAt": "2025-05-26T23:27:22.819026",
                "updatedAt": "2025-05-26T23:27:22.819026",
                "total": 55000.00,
                "watchDate": "2025-12-12"
            },
            "paging": null
        }
        ```
* **`PUT {Transaction Base Path}/{id}` - Update Payment Status (Admin, Cashier)**
    * **Description:** Updates the payment status of a transaction by its ID.
    * **Roles:** Admin, Cashier
    * **Path Parameters:**
        * `id` (string, required): The ID of the transaction.
    * **Request Example:**
        ```json
        {
            "theaterId": "theater-001",
            "studioId": "studio-001",
            "productId": "prod-001",
            "productPricingId": "pprice-001",
            "productSchedulingId": "psched-001",
            "qty": 1,
            "tax": 10,
            "transactionDateTime": "2025-12-12 12:10:12",
            "watchDate": "2025-12-12",
            "paymentDateTime": "2025-12-12 12:12:12",
            "paymentMethod": "CASH",
            "seats": [
                "A1"
            ],
            "paymentStatus": "Success"
        }
        ```
    * **Response Example:**
        ```json
        {
            "code": 200,
            "message": "Update transaction success",
            "data": {
                "id": "b2befd9a-2619-477d-aaf1-ecbd80999aa6",
                "customerId": null,
                "employeeCashierId": "emp-005",
                "theaterId": "theater-001",
                "studioId": "studio-001",
                "productId": "prod-001",
                "productPricingId": "pprice-001",
                "productSchedulingId": "psched-001",
                "qty": 1,
                "tax": 10,
                "transactionDateTime": "2025-12-12T12:10:12",
                "paymentStatus": "Success",
                "paymentDateTime": "2025-12-12T12:12:12",
                "paymentMethod": "Cash",
                "seats": [
                    "A1"
                ],
                "createdAt": "2025-05-26T23:27:22.819026",
                "updatedAt": "2025-05-26T23:27:22.819026",
                "total": 55000.00,
                "watchDate": "2025-12-12"
            },
            "paging": null
        }
        ```
</details>

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
**EMPLOYEE:** Has access to endpoints related to their own employee profile.

To access protected endpoints, users must include a valid JWT (JSON Web Token) in the Authorization header of their requests, prefixed with Bearer.

Example:
Authorization: Bearer <your_jwt_token>


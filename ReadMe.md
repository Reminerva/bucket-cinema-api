# Bucket Cinema API Documentation

## Table of Contents
* [Introduction](#introduction)
* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Installation](#installation)
    * [Configuration](#configuration)
    * [Database Setup](#database-setup)
    * [Running the Application](#running-the-application)
* [Base URL](#base-url)
* [API Endpoints](#api-endpoints)
    * [Artist API](#artist-api)
    * [Customer API](#customer-api)
    * [Product API](#product-api)
    * [Production Company API](#production-company-api)
    * [Product Pricing and Scheduling API](#product-pricing-and-scheduling-api)
    * [Studio API](#studio-api)
    * [Theater API](#theater-api)
    * [Transaction API](#transaction-api)
    * [User API](#user-api)
* [Authentication and Authorization](#authentication-and-authorization)
* [Request and Response Format](#request-and-response-format)
* [Error Handling](#error-handling)
* [Date and Time Format](#date-and-time-format)

## Introduction
The Bucket Cinema API provides a set of RESTful endpoints for managing data related to a movie/entertainment platform.  It includes functionality for managing users, products, customers, transactions, and other related entities.

## Getting Started

### Prerequisites
* Java Development Kit (JDK) 8 or higher
* PostgreSQL Database
* Maven
* IntelliJ IDEA or other IDE
* Postman or similar API testing tool

### Installation
1.  Clone the repository:

### Configuration
1.  **Environment Configuration:**
    * Copy the `.env_example` file to `.env`:
    * Edit the `.env` file and provide the correct values for your environment, including database connection details, JWT secrets, and other settings.

### Database Setup
1.  **Create the database:**
    * Use a PostgreSQL client (e.g., `psql`) to create the database specified in your `.env` file.
    * Ensure that the database user specified in `.env` has the necessary permissions to access the database.

### Run the application!

## Base URL
The base URL for all API endpoints is:
http://localhost:{SERVER_PORT}/api/v1
## API Endpoints

### Artist API
* `POST /api/v1/artist`: Create a new artist.
* `GET /api/v1/artist/{id}`: Get artist by ID.
* `GET /api/v1/artist`: Get all artists.
* `PUT /api/v1/artist/{id}`: Update artist.
* `DELETE /api/v1/artist/{id}`: Delete artist.

### Customer API
* `POST /api/v1/customer`: Create a new customer.
* `GET /api/v1/customer/{id}`: Get customer by ID.
* `GET /api/v1/customer`: Get all customers (supports pagination and search).
* `PUT /api/v1/customer/{id}`: Update customer (for admin).
* `PUT /api/v1/customer/me`: Update customer (for user).
* `DELETE /api/v1/customer/{id}`: Delete customer.

### Product API
* `POST /api/v1/product`: Create a new product.
* `GET /api/v1/product/{id}`: Get product by ID.
* `GET /api/v1/product`: Get all products.
* `PUT /api/v1/product/{id}`: Update product.
* `DELETE /api/v1/product/{id}/soft-delete`: Soft delete product.

### Production Company API
* `POST /api/v1/production-company`: Create a new production company.
* `GET /api/v1/production-company/{id}`: Get production company by ID.
* `GET /api/v1/production-company`: Get all production companies.
* `PUT /api/v1/production-company/{id}`: Update production company.
* `DELETE /api/v1/production-company/{id}`: Delete production company.

### Product Pricing and Scheduling API
* `POST /api/v1/product-pricing-scheduling`: Create a new product pricing and scheduling entry.
* `GET /api/v1/product-pricing-scheduling/{id}`: Get product pricing and scheduling entry by ID.
* `GET /api/v1/product-pricing-scheduling`: Get all product pricing and scheduling entries.
* `PUT /api/v1/product-pricing-scheduling/{id}`: Update product pricing and scheduling entry.
* `DELETE /api/v1/product-pricing-scheduling/{id}`: Delete product pricing and scheduling entry.

### Studio API
* `POST /api/v1/studio`: Create a new studio.
* `GET /api/v1/studio/{id}`: Get studio by ID.
* `GET /api/v1/studio`: Get all studios.
* `PUT /api/v1/studio/{id}`: Update studio.
* `DELETE /api/v1/studio/{id}`: Delete studio.

### Theater API
* `POST /api/v1/theater`: Create a new theater.
* `GET /api/v1/theater/{id}`: Get theater by ID.
* `GET /api/v1/theater`: Get all theaters.
* `PUT /api/v1/theater/{id}`: Update theater.
* `DELETE /api/v1/theater/{id}`: Soft delete theater.

### Transaction API
* `POST /api/v1/transaction`: Create a new transaction.
* `GET /api/v1/transaction/{id}`: Get transaction by ID.
* `GET /api/v1/transaction`: Get all transactions.
* `PUT /api/v1/transaction/{id}`: Update transaction.

### User API
* `POST /api/v1/auth/user/signup`: Register a new user.
* `POST /api/v1/auth/admin/signup`: Register a new admin user.
* `POST /api/v1/auth/user/signin`: User login.
* `POST /api/v1/auth/user/signout`: User logout.

## Authentication and Authorization
* The API uses JWT (JSON Web Tokens) for authentication.
* Users must sign in to obtain a JWT token, which must be included in the `Authorization` header of subsequent requests.
* The API supports different user roles (e.g., ADMIN, CUSTOMER, CASHIER), and access to certain endpoints is restricted based on the user's role.  Use `@PreAuthorize` in the controllers to define the roles.

## Request and Response Format
* All API requests and responses are in JSON format.
* Requests should include a `Content-Type: application/json` header.
* Responses will typically include a `CommonResponse` wrapper with the following structure:
    ```json
    {
        "code": 200,
        "message": "Success message",
        "data": { ... }, // The actual response data
        "paging": { ... }  // Optional pagination information
    }
    ```

## Error Handling
* The API returns standard HTTP status codes to indicate the success or failure of a request.
* Error responses will typically include a `code` and a `message` in the `CommonResponse` wrapper to provide more information about the error.

## Date and Time Format
* Date and time parameters should be formatted as  `yyyy-MM-dd HH:mm:ss`.

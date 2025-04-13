package com.flix.flix.constant;

public class ResponseExampleSwaggerBash {        
    public final static String SIGN_UP_SUCCESS = """
                                                {
                                                    "code": 201,
                                                    "message": "Signup Success",
                                                    "data": {
                                                        "email": "admin@example.com",
                                                        "token": "12345678sdfasdf"
                                                    }
                                                }
                                                """;
    public final static String SIGN_UP_FAILED = """
                                                {
                                                    "code": 400,
                                                    "message": "Signup failed",
                                                    "data": null
                                                }
                                                """;
    public final static String SIGN_IN_SUCCESS = """
                                                {
                                                    "code": 202,
                                                    "message": "Login Success",
                                                    "data": {
                                                        "email": "admin@example.com",
                                                        "token": "12345678sdfasdf"
                                                    }
                                                }
                                                """;
    public final static String SIGN_IN_FAILED = """
                                                {
                                                    "code": 400,
                                                    "message": "Login failed",
                                                    "data": null
                                                }
                                                """;

    public final static String CREATE_PRODUCT_SUCCESS = """
                                                {
                                                    "code": 201,
                                                    "message": "Create product success",
                                                    "data": {
                                                        "id": "a1b2c3d4-e5f6-7890-gh12-ijkl34567890",
                                                        "productName": "Product",
                                                        "price": 10000,
                                                        "stock": 10,
                                                        "categoryProductId": "c507e3ca-e0e7-4b08-9bdd-e4ee67dc516e"
                                                    }
                                                }
                                            """;
    public final static String CREATE_PRODUCT_FAILED = """
                                                {
                                                    "code": 400,
                                                    "message": "Create product failed",
                                                    "data": null
                                                }
                                            """;
    public final static String GET_ALL_PRODUCT_SUCCESS = """
                                                        {
                                                            "code": 200,
                                                            "message": "Get all product success",
                                                            "data": [
                                                                {
                                                                    "id": "a1b2c3d4-e5f6-7890-gh12-ijkl34567890",
                                                                    "productName": "Product",
                                                                    "price": 10000,
                                                                    "stock": 10,
                                                                    "categoryProductId": "c507e3ca-e0e7-4b08-9bdd-e4ee67dc516e"
                                                                }
                                                            ]
                                                        }
                                                    """;
    public final static String GET_ALL_PRODUCT_FAILED = """
                                                        {
                                                            "code": 400,
                                                            "message": "Get all product failed",
                                                            "data": null
                                                        }
                                                    """;
    public final static String GET_PRODUCT_SUCCESS = """
                                                            {
                                                                "code": 200,
                                                                "message": "Get product by id success",
                                                                "data": {
                                                                    "id": "a1b2c3d4-e5f6-7890-gh12-ijkl34567890",
                                                                    "productName": "Product",
                                                                    "price": 10000,
                                                                    "stock": 10,
                                                                    "categoryProductId": "c507e3ca-e0e7-4b08-9bdd-e4ee67dc516e"
                                                                }
                                                            }
                                                        """;
    public final static String GET_PRODUCT_FAILED = """
                                                        {
                                                            "code": 400,
                                                            "message": "Get product failed",
                                                            "data": null
                                                        }
                                                    """;
    public final static String UPDATE_PRODUCT_SUCCESS = """
                                                        {
                                                            "code": 200,
                                                            "message": "Update product success",
                                                            "data": {
                                                                "id": "a1b2c3d4-e5f6-7890-gh12-ijkl34567890",
                                                                "productName": "Product",
                                                                "price": 10000,
                                                                "stock": 10,
                                                                "categoryProductId": "c507e3ca-e0e7-4b08-9bdd-e4ee67dc516e"
                                                            }
                                                        }
                                                    """;
    public final static String UPDATE_PRODUCT_FAILED = """
                                                            {
                                                                "code": 400,
                                                                "message": "Update product failed",
                                                                "data": null
                                                            }
                                                        """;
    public final static String HARD_DELETE_PRODUCT_SUCCESS = """
                                                            {
                                                                "code": 200,
                                                                "message": "Hard delete product success",
                                                                "data": null
                                                            }
                                                        """;
    public final static String HARD_DELETE_PRODUCT_FAILED =  """
                                                                {
                                                                    "code": 200,
                                                                    "message": "Soft delete product success",
                                                                    "data": null
                                                                }
                                                            """;
    public final static String SOFT_DELETE_PRODUCT_SUCCESS = """
                                                            {
                                                                "code": 200,
                                                                "message": "Soft delete product success",
                                                                "data": null
                                                            }
                                                        """;
    public final static String SOFT_DELETE_PRODUCT_FAILED = """
                                                        {
                                                            "code": 400,
                                                            "message": "Soft delete product failed",
                                                            "data": null
                                                        }
                                                    """;

    public final static String CREATE_CATEGORY_PRODUCT_SUCCESS = """
                                                        {
                                                            "code": 201,
                                                            "message": "Create category product success",
                                                            "data": {
                                                                "id": "a1b2c3d4-e5f6-7890-gh12-ijkl34567890",
                                                                "name": "Category Product",
                                                                "products": []
                                                            }
                                                        }
                                                    """;
    public final static String CREATE_CATEGORY_PRODUCT_FAILED = """
                                                        {
                                                            "code": 400,
                                                            "message": "Create category product failed",
                                                            "data": null
                                                        }
                                                    """;
    public final static String GET_ALL_CATEGORY_PRODUCT_SUCCESS = """
                                                                {
                                                                    "code": 200,
                                                                    "message": "Get all category product success",
                                                                    "data": [
                                                                        {
                                                                            "id": "a1b2c3d4-e5f6-7890-gh12-ijkl34567890",
                                                                            "name": "Furnitures",
                                                                            "products": [
                                                                                "e6ee6115-f5bd-4c93-9a18-09bbf3873d97",
                                                                                "6d91bc48-db4b-44fc-95e5-1964643a1ef9",
                                                                                "6e69e9dc-2182-4325-8511-247f6928c80b",
                                                                                "be1ca31f-27e7-42c4-bf20-418976537a12"
                                                                            ]
                                                                        },
                                                                        {
                                                                            "id": "d0239c29-34e0-4e50-a03b-3a915d3e0cf9",
                                                                            "name": "Elektronik",
                                                                            "products": []
                                                                        }
                                                                    ]
                                                                }
                                                            """;
    public final static String GET_ALL_CATEGORY_PRODUCT_FAILED = """
                                                                {
                                                                    "code": 400,
                                                                    "message": "Get all category product failed",
                                                                    "data": null
                                                                }
                                                            """;
    public final static String GET_CATEGORY_PRODUCT_SUCCESS = """
                                                                    {
                                                                        "code": 200,
                                                                        "message": "Get category product success",
                                                                        "data": {
                                                                            "id": "6a6c507f-7178-4371-b08b-4a1370b3e04d",
                                                                            "name": "Kendaraan",
                                                                            "products": [
                                                                                "72cfb973-4aa8-48c2-970f-6a65742775ce"
                                                                            ]
                                                                        }
                                                                    }
                                                                """;
    public final static String GET_CATEGORY_PRODUCT_FAILED = """
                                                                {
                                                                    "code": 400,
                                                                    "message": "Get category product failed",
                                                                    "data": null
                                                                }
                                                            """;
    public final static String UPDATE_CATEGORY_PRODUCT_SUCCESS = """
                                                                {
                                                                    "code": 200,
                                                                    "message": "Update category product success",
                                                                    "data": {
                                                                        "id": "c507e3ca-e0e7-4b08-9bdd-e4ee67dc516e",
                                                                        "name": "Vehicle",
                                                                        "products": [
                                                                            "e6ee6115-f5bd-4c93-9a18-09bbf3873d97",
                                                                            "6d91bc48-db4b-44fc-95e5-1964643a1ef9",
                                                                            "6e69e9dc-2182-4325-8511-247f6928c80b",
                                                                            "be1ca31f-27e7-42c4-bf20-418976537a12"
                                                                        ]
                                                                    }
                                                                }
                                                            """;
    public final static String UPDATE_CATEGORY_PRODUCT_FAILED = """
                                                                    {
                                                                        "code": 400,
                                                                        "message": "Update category product failed",
                                                                        "data": null
                                                                    }
                                                                """;
}

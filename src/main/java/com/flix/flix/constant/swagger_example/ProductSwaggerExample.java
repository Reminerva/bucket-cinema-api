package com.flix.flix.constant.swagger_example;

public class ProductSwaggerExample {

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

}

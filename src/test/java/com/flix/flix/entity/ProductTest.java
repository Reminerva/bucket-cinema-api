package com.flix.flix.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ProductTest {

    @Test
    void testContainsCustomerLike_shouldReturnTrue() {
        Product product = Product.builder().id("p1").build();
        Customer customer1 = Customer.builder().id("c1").build();
        product.getCustomerLike().add(customer1);
        assertTrue(product.containsCustomerLike(customer1));
    }

    @Test
    void testContainsCustomerLike_shouldReturnFalse() {
        Product product = Product.builder().id("p1").build();
        Customer customer1 = Customer.builder().id("c1").build();
        Customer customer2 = Customer.builder().id("c2").build();
        product.getCustomerLike().add(customer1);
        assertFalse(product.containsCustomerLike(customer2));
    }

    @Test
    void testContainsCustomerDislike_shouldReturnTrue() {
        Product product = Product.builder().id("p1").build();
        Customer customer1 = Customer.builder().id("c1").build();
        product.getCustomerDislike().add(customer1);
        assertTrue(product.containsCustomerDislike(customer1));
    }

    @Test
    void testContainsCustomerDislike_shouldReturnFalse() {
        Product product = Product.builder().id("p1").build();
        Customer customer1 = Customer.builder().id("c1").build();
        Customer customer2 = Customer.builder().id("c2").build();
        product.getCustomerDislike().add(customer1);
        assertFalse(product.containsCustomerDislike(customer2));
    }
}

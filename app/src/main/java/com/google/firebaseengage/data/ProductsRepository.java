package com.google.firebaseengage.data;

import com.google.firebaseengage.entities.Product;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sokol on 17.02.2017.
 */
public interface ProductsRepository {
    Timestamp getTimestamp();

    List<Product> getProducts();

    void update(List<Product> products, Timestamp timestamp);
}

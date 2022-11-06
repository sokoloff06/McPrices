package com.google.firebaseengage.api;

import com.google.firebaseengage.entities.Product;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sokol on 17.02.2017.
 */

public interface ProductsApi {
    Timestamp getLastUpdatedTimestamp();

    List<Product> getProducts();
}

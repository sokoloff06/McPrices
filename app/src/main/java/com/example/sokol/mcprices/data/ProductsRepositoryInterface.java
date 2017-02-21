package com.example.sokol.mcprices.data;

import com.example.sokol.mcprices.entities.Product;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sokol on 17.02.2017.
 */
public interface ProductsRepositoryInterface {
    Timestamp getTimestamp();

    List<Product> getProducts();

    void update(List<Product> products, Timestamp timestamp);
}

package com.example.sokol.mcprices.api;

import com.example.sokol.mcprices.entities.Product;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sokol on 17.02.2017.
 */

public interface McApiInterface {
    Timestamp getLastUpdatedTimestamp();
    List<Product> getProducts();
}

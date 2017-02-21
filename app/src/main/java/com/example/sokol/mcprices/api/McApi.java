package com.example.sokol.mcprices.api;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.sokol.mcprices.entities.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sokol on 17.02.2017.
 */

public class McApi implements McApiInterface {

    public static final String BASE_URL = "http://mc-prices-server.herokuapp.com";
    public static final String PRODUCTS_URL = BASE_URL + "/products";
    public static final String PRODUCTS_UPDATE_URL = PRODUCTS_URL + "/last-update";


    @Override
    public Timestamp getLastUpdatedTimestamp() {
        ObjectMapper mapper = new ObjectMapper();
        Timestamp lastUpdate = null;
        try {
            URL productsUpdateUrl = new URL(PRODUCTS_UPDATE_URL);
            lastUpdate = mapper.readValue(productsUpdateUrl, Timestamp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastUpdate;
    }

    @Override
    @Nullable
    public List<Product> getProducts() {
        List<Product> products = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            URL productsUrl = new URL(PRODUCTS_URL);
            Log.i("McApi", "Parsing URL doc...");
            products = mapper.readValue(productsUrl, new TypeReference<List<Product>>(){});
            Log.i("McApi", "doc is parsed");
        } catch (IOException e) {
            e.printStackTrace();
            return products;
        }
        return products;
    }
}

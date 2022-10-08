package com.google.firebaseengage.api;

import android.util.Log;

import com.google.firebaseengage.entities.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by sokol on 17.02.2017.
 */

public class McApiImpl implements McApi {

    private static final String BASE_URL = "https://mc-prices-server.herokuapp.com";
    private static final String PRODUCTS_URL = BASE_URL + "/products";
    private static final String PRODUCTS_UPDATE_URL = PRODUCTS_URL + "/last-update";
    private static final String TAG = "McApiImpl";


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
        List<Product> products;
        ObjectMapper mapper = new ObjectMapper();
        try {
            URL productsUrl = new URL(PRODUCTS_URL);
            Log.i(TAG, "Parsing URL doc...");
            products = mapper.readValue(productsUrl, new TypeReference<List<Product>>() {
            });
            Log.i(TAG, "doc is parsed");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return products;
    }
}

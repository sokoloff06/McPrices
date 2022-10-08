package com.google.firebaseengage.api;

import android.content.Context;

import com.google.firebaseengage.R;
import com.google.firebaseengage.entities.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;

public class McApiLocal implements McApi{
    private final Context context;
    Timestamp updateTimestamp;

    public McApiLocal(Context applicationContext) {
        this.context = applicationContext;
         updateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public Timestamp getLastUpdatedTimestamp() {
        return updateTimestamp;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> products = null;
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            InputStream is = context.getAssets().open("products.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            products = mapper.readValue(json, new TypeReference<List<Product>>() {
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return products;
    }
}

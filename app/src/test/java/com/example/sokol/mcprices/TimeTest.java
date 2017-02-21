package com.example.sokol.mcprices;

import com.example.sokol.mcprices.entities.Product;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sokol on 19.02.2017.
 */

public class TimeTest {

    @Test
    public void getProducts(){
        List<Product> products = new ArrayList<>();
        products.add(new Product("name", 0));
        System.out.println(products.get(0));
    }
}

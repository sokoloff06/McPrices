package com.example.sokol.mcprices.entities;

import java.util.Collection;
import java.util.Map;

/**
 * Created by sokol on 06.03.2017.
 */

public class Cart {

    private Map<Integer, ProductRecord> savedProducts;

    public Map<Integer, ProductRecord> getProducts() {
        return savedProducts;
    }

    public void setProducts(Map<Integer, ProductRecord> products) {
        this.savedProducts = products;
    }

    public void add(Product product) {
        Integer newProductId = product.getId();
        ProductRecord savedProduct = savedProducts.get(newProductId);
        if (savedProduct == null) {
            savedProducts.put(newProductId, new ProductRecord(1, product.getPrice(), product.getName(), product.getPic()));
        } else {
            savedProduct.add();
        }
    }

    public void remove(Product product) {
        Integer newProductId = product.getId();
        ProductRecord savedProduct = savedProducts.get(newProductId);
        if (savedProduct != null) {
            savedProduct.remove();
        }
    }

    public int getSum() {
        Collection<ProductRecord> cartProducts = savedProducts.values();
        int sum = 0;
        for (ProductRecord pr : cartProducts) {
            sum += pr.count * pr.price;
        }
        return sum;
    }

    private static class ProductRecord {

        private int count;
        private int price;
        private String name;
        private String pic;

        ProductRecord(int count, int price, String name, String pic) {
            this.count = count;
            this.price = price;
            this.name = name;
            this.pic = pic;
        }

        void add() {
            count++;
        }

        void remove() {
            if (count != 0) {
                count--;
            }
        }
    }
}

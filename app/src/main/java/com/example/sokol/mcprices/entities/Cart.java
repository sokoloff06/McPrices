package com.example.sokol.mcprices.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sokol on 06.03.2017.
 */

public class Cart {

    private Map<Integer, ProductRecord> savedProducts = new HashMap<>();

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
            savedProducts.put(newProductId, new ProductRecord(newProductId, 1, product.getPrice(), product.getName(), product.getPic()));
        } else {
            savedProduct.add();
        }
    }

    public void delete(ProductRecord pr) {
        savedProducts.remove(pr.getId());
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
        if (cartProducts.isEmpty()) {
            return sum;
        }
        for (ProductRecord pr : cartProducts) {
            sum += pr.count * pr.price;
        }
        return sum;
    }

    public void set(ProductRecord newProductRecord) {
        savedProducts.get(newProductRecord.getId()).setCount(newProductRecord.getCount());
    }

    public void add(ProductRecord productRecord) {
        Integer newProductId = productRecord.getId();
        ProductRecord savedProduct = savedProducts.get(newProductId);
        if (savedProduct == null) {
            savedProducts.put(newProductId, productRecord);
        } else {
            savedProduct.add();
        }
    }

    public void remove(int index) {
        savedProducts.remove(0);
    }

    public void remove(ProductRecord productRecord) {
        Integer newProductId = productRecord.getId();
        ProductRecord savedProduct = savedProducts.get(newProductId);
        if (savedProduct.getCount() == 1) {
            delete(savedProduct);
        }
        savedProduct.remove();
    }

    public static class ProductRecord {

        private int id;
        private int count;
        private int price;
        private String name;
        private String pic;

        public ProductRecord(int id, int count, int price, String name, String pic) {
            this.id = id;
            this.count = count;
            this.price = price;
            this.name = name;
            this.pic = pic;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        void add() {
            count++;
        }

        void remove() {
            if (count != 0) {
                count--;
            }
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
    }
}

package com.google.firebaseengage.entities;

/**
 * Created by sokol on 17.02.2017.
 */
public class Product {

    private int id;
    private String name;
    private int price;
    private String pic;

    public Product() {
    }

    public Product(int id, String name, int price, String pic) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.pic = pic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

}

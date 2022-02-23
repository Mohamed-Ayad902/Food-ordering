package com.example.e_commerce.models;

public class Category {

    private String category_image,category_name;

    public Category() {
    }

    public Category(String category_image, String category_name) {
        this.category_image = category_image;
        this.category_name = category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}

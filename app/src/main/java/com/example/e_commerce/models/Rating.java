package com.example.e_commerce.models;

public class Rating {

    private String comment, userId, productId;
    private double rating, count;

    public Rating() {
    }

    public Rating(String comment, String userId, String productId, double rating,double count) {
        this.comment = comment;
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.count = count;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}

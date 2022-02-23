package com.example.e_commerce.models;

import java.util.ArrayList;
import java.util.List;

public class Statics {

    public static final String COLLECTION_USERS = "users";
    public static final String CATEGORY = "category";
    public static final String COLLECTION_ALL_PRODUCTS = "AllProducts";
    public static final String PRODUCT_EXTRA = "product_extra";
    public static final String COLLECTION_REQUESTS = "Requests";
    public static final String EXTRA_CATEGORY = "categoryName";
    public static final String COLLECTION_ALL_CATEGORIES = "allCategories";

    private static Request instance = null;
    private static String tokenInstance = null;
    private static String requestId = null;
    private static List<Product> productList = new ArrayList<>();


    public static List<Product> getProductList() {
        return productList;
    }

    public static void setProductList(List<Product> productList) {
        Statics.productList = productList;
    }

    public static void setInstance(Request instance) {
        Statics.instance = instance;
    }

    public static Request getInstance() {
        return instance;
    }

    public static String statusTitle(int status) {
        switch (status) {
            case 0:
                return "Your order has been placed";
            case 1:
                return "Your order is being cooked now";
            case 2:
                return "Your order is coming to you";
            default:
                return "Your order has been shipped successfully";
        }
    }

    public static void setTokenInstance(String token) {
        tokenInstance = token;
    }

    public static String getTokenInstance() {
        return tokenInstance;
    }


    public static String getStatus(int status) {
        if (status == 0)
            return "placed";
        else if (status == 1)
            return "cooking";
        else if (status == 2)
            return "On my way";
        else
            return "Shipped";
    }

    public static void setRequestId(String requestId) {
        Statics.requestId = requestId;
    }

    public static String getRequestId() {
        return requestId;
    }
}

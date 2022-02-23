package com.example.e_commerce.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Entity(tableName = "product_table")
public class Product implements Parcelable {

    @PrimaryKey
    @NonNull
    @Exclude
    private String id;
    private String image, name, description, category, shopName, shopAddress, shopAddressDetailed;
    private boolean available;
    private List<Rating> ratings;
    private double price, rating, discount;

    public Product() {
        // empty constructor required for firebase
    }

    public Product(@NotNull String id, String image, String name, String description, double price, double rating, String category, boolean available) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.category = category;
        this.available = available;
    }

    public Product(@NotNull String id, String image, String name, String description, String category, String shopName, String shopAddress, String shopAddressDetailed, boolean available, double price, double rating, double discount) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
        this.category = category;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopAddressDetailed = shopAddressDetailed;
        this.available = available;
        this.price = price;
        this.rating = rating;
        this.discount = discount;
    }


    protected Product(Parcel in) {
        id = in.readString();
        image = in.readString();
        name = in.readString();
        description = in.readString();
        category = in.readString();
        shopName = in.readString();
        shopAddress = in.readString();
        shopAddressDetailed = in.readString();
        available = in.readByte() != 0;
        price = in.readDouble();
        rating = in.readDouble();
        discount = in.readDouble();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(shopName);
        dest.writeString(shopAddress);
        dest.writeString(shopAddressDetailed);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(available);
        }
        dest.writeDouble(price);
        dest.writeDouble(rating);
        dest.writeDouble(discount);
    }


    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopAddressDetailed() {
        return shopAddressDetailed;
    }

    public void setShopAddressDetailed(String shopAddressDetailed) {
        this.shopAddressDetailed = shopAddressDetailed;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}


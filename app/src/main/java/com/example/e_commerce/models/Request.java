package com.example.e_commerce.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.List;

public class Request implements Parcelable {

    private List<Order> orders;
    private String userId, address, phoneNumber, userName, comment;
    private double totalPrice;
    private int status;
    private Timestamp created;

    public Request() {
    }


    public Request(List<Order> orders, String userId, String address, String phoneNumber, double totalPrice, String userName, int status, String comment, Timestamp created) {
        this.orders = orders;
        this.userId = userId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.totalPrice = totalPrice;
        this.userName = userName;
        this.status = status;
        this.comment = comment;
        this.created = created;
    }

    protected Request(Parcel in) {
        userId = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        userName = in.readString();
        totalPrice = in.readDouble();
        status = in.readInt();
        created = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    public Request(List<Order> orders, String userId, String address, String phoneNumber, double totalPrice, String userName, int status, Timestamp created) {
        this.orders = orders;
        this.userId = userId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.totalPrice = totalPrice;
        this.userName = userName;
        this.status = status;
        this.created = created;

    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(userId);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeString(userName);
        dest.writeDouble(totalPrice);
        dest.writeInt(status);
        dest.writeParcelable(created, flags);
    }
}

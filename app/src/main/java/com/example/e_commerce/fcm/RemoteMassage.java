package com.example.e_commerce.fcm;

import com.google.gson.annotations.SerializedName;

public class RemoteMassage {

    @SerializedName("to")
    private String to;

    @SerializedName("notification")
    private Notification notification;

    public RemoteMassage(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getData() {
        return notification;
    }

    public void setData(Notification notification) {
        this.notification = notification;
    }
}

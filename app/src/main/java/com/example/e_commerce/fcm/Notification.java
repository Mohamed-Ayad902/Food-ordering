package com.example.e_commerce.fcm;

public class Notification {

    private String title,body;

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMassage() {
        return body;
    }

    public void setMassage(String massage) {
        this.body = massage;
    }
}

package com.example.aniscoreandroid.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("data")
    @Expose
    private User user;

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }
}

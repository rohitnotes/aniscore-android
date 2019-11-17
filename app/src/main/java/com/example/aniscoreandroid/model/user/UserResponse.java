package com.example.aniscoreandroid.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private UserData userData;

    public String getMessage() {
        return message;
    }

    public UserData getUserData() {
        return userData;
    }
}

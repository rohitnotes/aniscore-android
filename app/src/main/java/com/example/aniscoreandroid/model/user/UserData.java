package com.example.aniscoreandroid.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserData {
    @SerializedName("user")
    @Expose
    private User user;

    public User getUser() {
        return user;
    }
}

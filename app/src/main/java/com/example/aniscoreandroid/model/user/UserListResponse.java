package com.example.aniscoreandroid.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserListResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private UserListData userListData;

    public String getMessage() {
        return message;
    }

    public UserListData getUsersData() {
        return userListData;
    }
}

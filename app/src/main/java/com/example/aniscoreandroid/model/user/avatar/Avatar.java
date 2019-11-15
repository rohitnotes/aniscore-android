package com.example.aniscoreandroid.model.user.avatar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Avatar {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("data")
    @Expose
    private byte[] image;

    public String getType() {
        return type;
    }

    public byte[] getImage() {
        return image;
    }
}
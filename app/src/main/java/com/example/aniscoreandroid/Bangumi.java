package com.example.aniscoreandroid;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bangumi {
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("message")
    @Expose
    private String imageUrl;

    public String getTitle() {
        return this.title;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }
}

package com.example.aniscoreandroid.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private BangumiData data;

    public BangumiData getData() {
        return this.data;
    }
}

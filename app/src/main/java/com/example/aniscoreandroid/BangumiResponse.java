package com.example.aniscoreandroid;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return this.data;
    }
}

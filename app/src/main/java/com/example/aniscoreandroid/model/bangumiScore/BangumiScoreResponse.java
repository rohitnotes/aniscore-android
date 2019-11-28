package com.example.aniscoreandroid.model.bangumiScore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiScoreResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private BangumiScoreData data;

    public BangumiScoreData getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
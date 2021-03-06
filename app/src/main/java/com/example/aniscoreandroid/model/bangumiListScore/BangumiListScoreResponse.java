package com.example.aniscoreandroid.model.bangumiListScore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
 * user for both rank and bangumi search result view
 */
public class BangumiListScoreResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private BangumiListScoreData data;

    public String getMessage() {
        return message;
    }

    public BangumiListScoreData getData() {
        return this.data;
    }
}
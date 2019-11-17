package com.example.aniscoreandroid.model.bangumiListScore;

import com.example.aniscoreandroid.model.bangumiListScore.BangumiListScoreData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiListScoreResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private BangumiListScoreData data;

    public BangumiListScoreData getData() {
        return this.data;
    }
}
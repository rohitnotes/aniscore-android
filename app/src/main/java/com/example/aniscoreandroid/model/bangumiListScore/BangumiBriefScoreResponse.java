package com.example.aniscoreandroid.model.bangumiListScore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiBriefScoreResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private BangumiBriefScoreData data;

    public BangumiBriefScoreData getData() {
        return this.data;
    }
}

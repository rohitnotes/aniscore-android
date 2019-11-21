package com.example.aniscoreandroid.model.bangumiListScore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiBriefScoreData {
    @SerializedName("bangumi")
    @Expose
    BangumiBriefScore bangumi;

    public BangumiBriefScore getBangumi() {
        return this.bangumi;
    }
}

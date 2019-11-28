package com.example.aniscoreandroid.model.bangumiScore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiScoreData {
    @SerializedName("bangumiScore")
    @Expose
    BangumiScore bangumiScore;

    public BangumiScore getBangumiScore() {
        return bangumiScore;
    }
}

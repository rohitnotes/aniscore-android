package com.example.aniscoreandroid.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BangumiListScoreData {
    @SerializedName("bangumiList")
    @Expose
    List<BangumiBriefScore> bangumiList;


    public List<BangumiBriefScore> getBangumiList() {
        return this.bangumiList;
    }
}

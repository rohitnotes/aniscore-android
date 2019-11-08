package com.example.aniscoreandroid.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BangumiListData {
    @SerializedName("bangumiList")
    @Expose
    List<BangumiBrief> bangumiList;


    public List<BangumiBrief> getBangumiList() {
        return this.bangumiList;
    }
}

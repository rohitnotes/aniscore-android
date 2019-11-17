package com.example.aniscoreandroid.model.bangumiList;

import com.example.aniscoreandroid.model.bangumiList.BangumiListData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiListResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private BangumiListData data;

    public BangumiListData getData() {
        return this.data;
    }
}

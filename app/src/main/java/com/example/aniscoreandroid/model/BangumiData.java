package com.example.aniscoreandroid.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiData {
    @SerializedName("bangumi")
    @Expose
    Bangumi bangumi;


    public Bangumi getBangumi() {
        return this.bangumi;
    }
}

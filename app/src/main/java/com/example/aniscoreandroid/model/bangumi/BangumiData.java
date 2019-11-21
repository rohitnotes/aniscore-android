package com.example.aniscoreandroid.model.bangumi;

import com.example.aniscoreandroid.model.bangumi.Bangumi;
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

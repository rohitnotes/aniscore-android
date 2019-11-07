package com.example.aniscoreandroid;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("bangumi")
    @Expose
    Bangumi bangumi;


    public Bangumi getBangumi() {
        return this.bangumi;
    }
}

package com.example.aniscoreandroid.model.bangumiApi.name;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Name {
    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return this.name;
    }
}

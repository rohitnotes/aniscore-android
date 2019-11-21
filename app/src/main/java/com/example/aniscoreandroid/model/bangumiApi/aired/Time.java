package com.example.aniscoreandroid.model.bangumiApi.aired;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Time {
    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("month")
    @Expose
    private String month;

    @SerializedName("day")
    @Expose
    private String day;

    public String getYear() {
        return this.year;
    }

    public String getMonth() {
        return this.month;
    }

    public String getDay() {
        return this.day;
    }
}

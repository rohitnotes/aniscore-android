package com.example.aniscoreandroid.model.bangumiApi.aired;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Aired {
    @SerializedName("from")
    @Expose
    String fromDate;

    @SerializedName("to")
    @Expose
    String toDate;

    @SerializedName("prop")
    @Expose
    Prop prop;

    public String getFromDate() {
        return this.fromDate;
    }

    public String getToDate() {
        return this.toDate;
    }

    public Prop getProp() {
        return this.prop;
    }
}
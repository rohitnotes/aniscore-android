package com.example.aniscoreandroid.model.bangumiApi.aired;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Prop {
    @SerializedName("from")
    @Expose
    Time from;

    @SerializedName("to")
    @Expose
    Time to;

    public Time getFrom() {
        return this.from;
    }

    public Time getTo() {
        return this.to;
    }

    public String getFromYear() {
        if (this.from == null || this.from.getYear() == null) {
            return "";
        }
        return this.from.getYear();
    }

    public String getFromMonth() {
        if (this.from == null || this.from.getMonth() == null) {
            return "";
        }
        return this.from.getMonth();
    }

    public String getFromDay() {
        if (this.from == null || this.from.getDay() == null) {
            return "";
        }
        return this.from.getDay();
    }

    public String  getToYear() {
        if (this.to == null || this.to.getYear() == null) {
            return "";
        }
        return this.to.getYear();
    }

    public String getToMonth() {
        if (this.to == null || this.to.getMonth() == null) {
            return "";
        }
        return this.to.getMonth();
    }

    public String getToDay() {
        if (this.to == null || this.to.getDay() == null) {
            return "";
        }
        return this.to.getDay();
    }
}
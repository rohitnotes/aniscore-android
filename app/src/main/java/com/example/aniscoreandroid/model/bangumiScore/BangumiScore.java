package com.example.aniscoreandroid.model.bangumiScore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BangumiScore {
    @SerializedName("anime_id")
    @Expose()
    private String animeId;

    @SerializedName("1")
    @Expose
    private List<String> oneStarUsers;

    @SerializedName("2")
    @Expose
    private List<String> twoStarUsers;

    @SerializedName("3")
    @Expose
    private List<String> threeStarUsers;

    @SerializedName("4")
    @Expose
    private List<String> fourStarUsers;

    @SerializedName("5")
    @Expose
    private List<String> fiveStarUsers;

    @SerializedName("averageScore")
    @Expose
    private double averageScore;

    @SerializedName("userNumber")
    @Expose
    private int userNumber;

    @SerializedName("totalScore")
    @Expose
    private int totalScore;

    public double getAverageScore() {
        return averageScore;
    }

    public int getUserNumber() {
        return userNumber;
    }
}

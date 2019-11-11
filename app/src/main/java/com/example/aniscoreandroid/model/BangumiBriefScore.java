package com.example.aniscoreandroid.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
 * used for ranking
 */
public class BangumiBriefScore {
    @SerializedName("anime_id")
    @Expose
    private String animeId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("score")
    @Expose
    private double score;

    @SerializedName("userNumber")
    @Expose
    private int userNumber;

    @SerializedName("synopsis")
    @Expose
    private String synopsis;

    @SerializedName("totalScore")
    @Expose
    private double totalScore;

    public String getAnimeId() {
        return this.animeId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public double getScore() {
        return this.score;
    }

    public int getUserNumber() {
        return this.userNumber;
    }

    public double getTotalScore() {
        return this.totalScore;
    }

    public String getSynopsis() {
        return this.synopsis;
    }
}
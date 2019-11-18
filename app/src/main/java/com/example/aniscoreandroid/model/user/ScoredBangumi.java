package com.example.aniscoreandroid.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScoredBangumi {
    @SerializedName("anime_id")
    @Expose
    private String animeId;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("synopsis")
    @Expose
    private String synopsis;

    @SerializedName("score")
    @Expose
    private double score;

    public String getAnimeId() {
        return this.animeId;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getTitle(){
        return this.title;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public double getScore() {
        return this.score;
    }
}
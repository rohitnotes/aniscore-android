package com.example.aniscoreandroid.model.bangumi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bangumi {
    @SerializedName("anime_id")
    @Expose
    private String animeId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("synopsis")
    @Expose
    private String synopsis;

    @SerializedName("airing_start")
    @Expose
    private String airingStart;

    @SerializedName("genres")
    @Expose
    private String[] genres;

    @SerializedName("source")
    @Expose
    private String source;

    @SerializedName("producers")
    private String[] producers;

    @SerializedName("licensors")
    private String[] licensors;

    @SerializedName("continuing")
    private boolean continuing;

    public String getAnimeId() {
        return this.animeId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public String getAiringStart() {
        return this.airingStart;
    }

    public String[] getGenres() {
        return this.genres;
    }

    public String[] getLicensors() {
        return this.licensors;
    }

    public boolean getContinuing() {
        return this.continuing;
    }
}
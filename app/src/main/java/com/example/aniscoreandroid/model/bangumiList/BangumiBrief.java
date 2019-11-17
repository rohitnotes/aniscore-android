package com.example.aniscoreandroid.model.bangumiList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiBrief {
    @SerializedName("anime_id")
    @Expose
    private String animeId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    public String getAnimeId() {
        return this.animeId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }
}
package com.example.aniscoreandroid.model.bangumiApi;

import com.example.aniscoreandroid.model.bangumiApi.aired.Aired;
import com.example.aniscoreandroid.model.bangumiApi.name.Name;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BangumiDetail {
    @SerializedName("mal_id")
    @Expose
    private String animeId;

    @SerializedName("url")
    @Expose
    private String link;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("trailer_url")
    @Expose
    private String trailerLink;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("title_japanese")
    @Expose
    private String titleJapanese;

    @SerializedName("title_synonyms")
    @Expose
    private String[] titleSynonyms;

    @SerializedName("episodes")
    @Expose
    private int episodes;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("aired")
    @Expose
    private Aired aired;

    @SerializedName("synopsis")
    @Expose
    private String synopsis;

    @SerializedName("producers")
    @Expose
    private Name[] producers;

    @SerializedName("licensors")
    @Expose
    private Name[] licensors;

    @SerializedName("studios")
    @Expose
    private Name[] studios;

    @SerializedName("genres")
    @Expose
    private Name[] genres;

    public String getAnimeId() {
        return this.animeId;
    }

    public String getLink() {
        return this.link;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getTrailerLink() {
        return this.trailerLink;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTitleJapanese() {
        return this.titleJapanese;
    }

    public String[] getTitleSynonyms() {
        return this.titleSynonyms;
    }

    public int getEpisodes() {
        return this.episodes;
    }

    public String getStatus() {
        return this.status;
    }

    public Aired getAired() {
        return this.aired;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public Name[] getProducers() {
        return this.producers;
    }

    public Name[] getLicensors() {
        return this.licensors;
    }

    public Name[] getStudios() {
        return this.studios;
    }

    public Name[] getGenres() {
        return this.genres;
    }
}
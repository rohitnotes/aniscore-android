package com.example.aniscoreandroid.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("scoreAnime")
    @Expose
    private ScoredBangumi[] scoredBangumis;

    @SerializedName("following")
    @Expose
    private String[] following;             // array of user id

    @SerializedName("follower")
    @Expose
    private String[] follower;

    @SerializedName("id")
    @Expose
    private String userId;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("background")
    @Expose
    private String background;

    public String getAvatar() {
        return avatar;
    }

    public ScoredBangumi[] getScoredBangumis() {
        return scoredBangumis;
    }

    public String[] getFollowing() {
        return following;
    }

    public String[] getFollower() {
        return follower;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
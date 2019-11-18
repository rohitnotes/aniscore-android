package com.example.aniscoreandroid.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

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
    private LinkedList<String> following;             // array of user id

    @SerializedName("follower")
    @Expose
    private LinkedList<String> follower;

    @SerializedName("_id")
    @Expose
    private String userId;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("background")
    @Expose
    private String background;

    public ScoredBangumi[] getScoredBangumis() {
        return scoredBangumis;
    }

    public LinkedList<String> getFollowing() {
        return following;
    }

    public LinkedList<String> getFollower() {
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

    public String getBackground() {
        return background;
    }

    public String getAvatar() {
        return avatar;
    }

    /*
     * remove the userid from the following list
     */
    public void unFollow(String userId) {
        following.remove(userId);
    }

    /*
     * add the userId to the following list
     */
    public void follow(String userId) {
        following.add(userId);
    }

    /*
     * remove the userId from followers
     */
    public void removeFollower(String userId) {
        follower.remove(userId);
    }

    /*
     * add userId to followers
     */
    public void addFollower(String userId) {
        follower.add(userId);
    }
}
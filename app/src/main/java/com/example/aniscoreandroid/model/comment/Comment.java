package com.example.aniscoreandroid.model.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Comment {
    @SerializedName("_id")
    @Expose
    private String commentId;

    @SerializedName("anime_id")
    @Expose
    private String animeId;

    @SerializedName("parentComment_id")
    @Expose
    private String parentCommentId;

    @SerializedName("commentContent")
    @Expose
    private String commentContent;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("repliedComment_id")
    @Expose
    private String repliedCommentId;

    @SerializedName("repliedUsername")
    @Expose
    private String repliedUsername;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("like")
    @Expose
    private List<String> like;

    @SerializedName("dislike")
    @Expose
    private List<String> dislike;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    public String getCommentId() {
        return this.commentId;
    }

    public String getAnimeId() {
        return this.animeId;
    }

    public String getParentCommentId() {
        return this.parentCommentId;
    }

    public String getCommentContent() {
        return this.commentContent;
    }

    public String getUsername() {
        return this.username;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getRepliedCommentId() {
        return this.repliedCommentId;
    }

    public String getRepliedUsername() {
        return this.repliedUsername;
    }

    public String getDate() {
        return this.date;
    }

    public List<String> getLike() {
        return this.like;
    }

    public List<String> getDislike() {
        return this.dislike;
    }

    public String getAvatar() {
        return this.avatar;
    }
}
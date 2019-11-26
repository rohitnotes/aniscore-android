package com.example.aniscoreandroid.model.singleComment;

import com.example.aniscoreandroid.model.comment.Comment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleCommentData {
    @SerializedName("comment")
    @Expose
    Comment comment;

    public Comment getComment() {
        return comment;
    }
}
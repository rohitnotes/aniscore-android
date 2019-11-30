package com.example.aniscoreandroid.model.commentNumber;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentNumberData {
    @SerializedName("commentNumber")
    @Expose
    private int commentNumber;

    public int getCommentNumber() {
        return commentNumber;
    }
}
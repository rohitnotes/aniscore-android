package com.example.aniscoreandroid.model.commentNumber;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentNumberResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private CommentNumberData data;

    public CommentNumberData getData() {
        return data;
    }
}
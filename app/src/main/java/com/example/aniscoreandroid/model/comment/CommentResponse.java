package com.example.aniscoreandroid.model.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private CommentData data;

    public String getMessage() {
        return message;
    }

    public CommentData getData() {
        return data;
    }
}

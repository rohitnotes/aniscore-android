package com.example.aniscoreandroid.model.singleComment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleCommentResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private SingleCommentData data;

    public String getMessage() {
        return message;
    }

    public SingleCommentData getData() {
        return data;
    }
}
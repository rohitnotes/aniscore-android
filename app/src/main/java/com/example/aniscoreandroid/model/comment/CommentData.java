package com.example.aniscoreandroid.model.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class CommentData {
    @SerializedName("comments")
    @Expose
    LinkedList<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }
}

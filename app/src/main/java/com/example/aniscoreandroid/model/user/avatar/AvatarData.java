package com.example.aniscoreandroid.model.user.avatar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvatarData {
    @SerializedName("contentType")
    @Expose
    String contentType;

    @SerializedName("data")
    @Expose
    Avatar avatar;

    public String getContentType() {
        return this.contentType;
    }

    public Avatar getAvatar() {
        return this.avatar;
    }
}

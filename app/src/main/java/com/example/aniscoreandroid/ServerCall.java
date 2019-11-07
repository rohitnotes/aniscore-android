package com.example.aniscoreandroid;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServerCall {
    @GET("/api/bangumi/{bangumiId}")
    Call<BangumiResponse> test1(@Path("bangumiId") String bangumiId);
}

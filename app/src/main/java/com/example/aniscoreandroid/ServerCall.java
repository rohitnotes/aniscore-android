package com.example.aniscoreandroid;

import com.example.aniscoreandroid.model.BangumiListResponse;
import com.example.aniscoreandroid.model.BangumiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServerCall {
    @GET("/api/bangumi/{bangumiId}")
    Call<BangumiResponse> getBangumiDetailById(@Path("bangumiId") String bangumiId);

    @GET("/api/bangumiList/rank/{bangumiNumber}")
    Call<BangumiListResponse> getBangumiRank(@Path("bangumiNumber") int bangumiNumber);


}

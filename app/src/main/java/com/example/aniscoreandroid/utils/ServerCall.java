package com.example.aniscoreandroid.utils;

import com.example.aniscoreandroid.model.bangumiList.BangumiListResponse;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiListScoreResponse;
import com.example.aniscoreandroid.model.BangumiResponse;
import com.example.aniscoreandroid.model.user.AuthResponse;
import com.example.aniscoreandroid.model.user.UserResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServerCall {
    @GET("/api/bangumi/{bangumiId}")
    Call<BangumiResponse> getBangumiDetailById(@Path("bangumiId") String bangumiId);

    @GET("/api/bangumiList/rank/{bangumiNumber}")
    Call<BangumiListScoreResponse> getBangumiRank(@Path("bangumiNumber") int bangumiNumber);

    // get 10 bangumis of given year and season
    @GET("/api/bangumi/{year}/{season}/limit")
    Call<BangumiListResponse> getBangumiOfYearSeasonLimit(@Path("year") String year, @Path("season") String season);

    // get all bangumis of given year and season
    @GET("/api/bangumi/{year}/{season}")
    Call<BangumiListResponse> getBangumiOfYearSeason(@Path("year") String year, @Path("season") String season);

    @Headers("Content-Type: application/json")
    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body HashMap<String, String> input);

    @POST("/api/auth/logout")
    Call<AuthResponse> logout();

    @GET("/api/user/{userId}")
    Call<UserResponse> getUserById(@Path("userId") String userId);
}
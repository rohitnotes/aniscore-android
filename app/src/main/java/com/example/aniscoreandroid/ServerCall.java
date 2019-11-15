package com.example.aniscoreandroid;

import com.example.aniscoreandroid.model.BangumiListResponse;
import com.example.aniscoreandroid.model.BangumiListScoreResponse;
import com.example.aniscoreandroid.model.BangumiResponse;
import com.example.aniscoreandroid.model.user.LoginResponse;
import com.example.aniscoreandroid.model.user.UserResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
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
    Call<LoginResponse> login(@Body HashMap<String, String> input);

    @Headers({"Access-Control-Allow-Origin: *",
            "Access-Control-Allow-Headers: Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, authorization",
    "Content-Type: application/json; charset=utf-8"})
    @GET("/api/auth/currentUser")
    Call<UserResponse> getCurrentUser();
}

package com.example.aniscoreandroid.utils;

import com.example.aniscoreandroid.model.bangumiApi.BangumiDetail;
import com.example.aniscoreandroid.model.bangumiList.BangumiListResponse;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScoreResponse;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiListScoreResponse;
import com.example.aniscoreandroid.model.bangumi.BangumiResponse;
import com.example.aniscoreandroid.model.bangumiScore.BangumiScoreResponse;
import com.example.aniscoreandroid.model.comment.CommentResponse;
import com.example.aniscoreandroid.model.commentNumber.CommentNumberResponse;
import com.example.aniscoreandroid.model.singleComment.SingleCommentResponse;
import com.example.aniscoreandroid.model.user.AuthResponse;
import com.example.aniscoreandroid.model.user.UserListResponse;
import com.example.aniscoreandroid.model.user.UserResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ServerCall {
    // get bangumi detail by id
    @GET("/api/bangumi/{bangumiId}")
    Call<BangumiResponse> getBangumiDetailById(@Path("bangumiId") String bangumiId);

    // get top bangumiNumber of bangumi with highest total score
    @GET("/api/bangumiList/rank/{bangumiNumber}")
    Call<BangumiListScoreResponse> getBangumiRank(@Path("bangumiNumber") int bangumiNumber);

    // get bangumi brief information by id
    @GET("/api/bangumiList/{bangumiId}")
    Call<BangumiBriefScoreResponse> getBangumiBriefById(@Path("bangumiId") String bangumiId);

    // get 10 bangumis of given year and season
    @GET("/api/bangumi/{year}/{season}/limit")
    Call<BangumiListResponse> getBangumiOfYearSeasonLimit(@Path("year") String year, @Path("season") String season);

    // get all bangumis of given year and season
    @GET("/api/bangumi/{year}/{season}")
    Call<BangumiListResponse> getBangumiOfYearSeason(@Path("year") String year, @Path("season") String season);

    // login
    @Headers("Content-Type: application/json")
    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body HashMap<String, String> input);

    // logout
    @POST("/api/auth/logout")
    Call<AuthResponse> logout();

    // get user by id
    @GET("/api/user/{userId}")
    Call<UserResponse> getUserById(@Path("userId") String userId);

    // update current user avatar
    @Multipart
    @PUT("/api/avatar/{userId}")
    Call<UserResponse> updateAvatarById(@Path("userId") String userId, @Part MultipartBody.Part avatar);

    // update current background avatar
    @Multipart
    @PUT("/api/background/{userId}")
    Call<UserResponse> updateBackgroundById(@Path("userId") String userId, @Part MultipartBody.Part background);

    // user with userId follow the user with id given in body
    @Headers("Content-Type: application/json")
    @PUT("/api/user/follow/{userId}")
    Call<UserResponse> followUserById(@Path("userId") String userId, @Body HashMap<String, String> followId);

    // user with userId unfollow the user with id given in body
    @HTTP(method = "DELETE", path = "api/user/unfollow/{userId}", hasBody = true)
    Call<UserResponse> unFollowUserById(@Path("userId") String userId, @Body HashMap<String, String> unFollowId);

    // get bangumi detail by anime id
    @GET("anime/{bangumiId}")
    Call<BangumiDetail> getBangumiByIdApi(@Path("bangumiId") String bangumiId);

    // get single comment by id
    @GET("/api/comment/singlecomment/{commentId}")
    Call<CommentResponse> getCommentById(@Path("commentId") String commentId);

    // get all parent comments of an anime
    @GET("/api/comment/parentcomment/{bangumiId}")
    Call<CommentResponse> getParentCommentsByBangumiId(@Path("bangumiId") String bangumiId);

    @GET("/api/comment/parentcomment/{bangumiId}/{page}")
    Call<CommentResponse> getParentCommentsByBangumiIdWithPage(@Path("bangumiId") String bangumiId, @Path("page") int page);

    @GET("/api/comment/parentcomment/{bangumiId}/count")
    Call<CommentNumberResponse> getParentCommentCountByBangumiId(@Path("bangumiId") String bangumiId);

    // like or dislike a comment
    @PUT("/api/comment/{commentId}")
    Call<CommentResponse> updateStatusOfComment(@Path("commentId") String commentId, @Body HashMap<String, String> userInfo);

    // get all replies of a parent comment
    @GET("/api/comment/reply/{parentCommentId}")
    Call<CommentResponse> getRepliesOfComment(@Path("parentCommentId") String parentCommentId);

    // get replies of a parent comment by page
    @GET("/api/comment/reply/{parentCommentId}/{page}")
    Call<CommentResponse> getRepliesOfCommentWithPage(@Path("parentCommentId") String parentCommentId, @Path("page") int page);

    // submit a comment
    @POST("/api/comment")
    Call<SingleCommentResponse> submitComment(@Body HashMap<String, String> comment);

    // submit score for a bangumi
    @PUT("/api/bangumiScore/{bangumiId}")
    Call<BangumiScoreResponse> submitScore(@Path("bangumiId") String bangumiId, @Body HashMap<String, String> scoreDetail);

    // search for a bangumi by name
    @GET("/api/bangumiList/search/{query}")
    Call<BangumiListScoreResponse> searchBangumiByName(@Path("query") String query);

    // search for a user by username
    @GET("/api/user/search/{query}")
    Call<UserListResponse> searchUserByUsername(@Path("query") String query);
}
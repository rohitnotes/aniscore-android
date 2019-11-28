package com.example.aniscoreandroid.detailView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.adapter.CommentAdapter;
import com.example.aniscoreandroid.adapter.EndlessListLoad;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScore;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScoreData;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScoreResponse;
import com.example.aniscoreandroid.model.comment.Comment;
import com.example.aniscoreandroid.model.comment.CommentResponse;
import com.example.aniscoreandroid.utils.ServerCall;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentMain extends Fragment {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private static View view;
    private String bangumiId;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comment> comments;
    private boolean loading = true;
    private static double score;
    private static int userNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.comment_main_view, container, false);
        Bundle args = getArguments();
        bangumiId = args.getString("bangumiId");
        recyclerView = view.findViewById(R.id.comment_list);
        comments = new LinkedList<>();
        adapter = new CommentAdapter(comments, true);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.addOnScrollListener(new EndlessListLoad(linearLayoutManager, 2) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getParentComments(page+1);              // page starts at 1, not 0
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        // get first page of comments and fetch score using multi thread
        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.execute(new Runnable() {
            @Override
            public void run() {
                fetchScore(bangumiId);
            }
        });
        exec.execute(new Runnable() {
            @Override
            public void run() {
                getParentComments(1);
            }
        });
        exec.shutdown();
        Comments.setSubmitClickListener("none", "none", "none", comments, adapter);
        return view;
    }

    public static void setScore(double newScore) {
        score = newScore;
        TextView scoreView = view.findViewById(R.id.score);
        RatingBar bar = view.findViewById(R.id.rating_stars);
        bar.setRating((float)(score/2.0));
        scoreView.setText((score + ""));
    }

    public static void setUserNumber(int newUserNumber) {
        userNumber = newUserNumber;
        TextView userNumberView = view.findViewById(R.id.user_number);
        userNumberView.setText((userNumber + " users scored"));
    }

    /*
     * get score of the bangumi from database
     */
    private void fetchScore(String bangumiId) {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<BangumiBriefScoreResponse> briefScoreCall = service.getBangumiBriefById(bangumiId);
        briefScoreCall.enqueue(new Callback<BangumiBriefScoreResponse>() {
            @Override
            public void onResponse(Call<BangumiBriefScoreResponse> call, Response<BangumiBriefScoreResponse> response) {
                if (response.isSuccessful()) {
                    BangumiBriefScoreData data = response.body().getData();
                    setScoreSection(data);
                }
            }

            @Override
            public void onFailure(Call<BangumiBriefScoreResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    private void showRatingDialog() {
        RatingWindow window = new RatingWindow();
        window.show(((AppCompatActivity)view.getContext()).getSupportFragmentManager(), "RatingDialog");
    }

    private void setScoreSection(BangumiBriefScoreData data) {
        BangumiBriefScore bangumiBriefScore = data.getBangumi();
        score = bangumiBriefScore.getScore();
        userNumber = bangumiBriefScore.getUserNumber();
        ((TextView)view.findViewById(R.id.score)).setText((score + ""));
        ((TextView)view.findViewById(R.id.user_number)).setText((userNumber+" users scored"));
        RatingBar bar = view.findViewById(R.id.rating_stars);
        bar.setRating((float)(score/2.0));
        BangumiInfo.setScore(score);
        BangumiInfo.setUserNumber(userNumber);
        view.findViewById(R.id.score_section).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserId = Comments.getCurrentId();
                if (currentUserId == null || currentUserId.length() == 0) {
                    Comments.showDialog();
                } else {
                    showRatingDialog();
                }
            }
        });
    }

    private void getParentComments(int page) {
        if (!loading) {
            return;
        }
        ServerCall service = retrofit.create(ServerCall.class);
        Call<CommentResponse> parentCommentCall = service.getParentCommentsByBangumiIdWithPage(bangumiId, page);
        parentCommentCall.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful()) {
                    List<Comment> commentList = response.body().getData().getComments();
                    if (commentList.size() == 0) {
                        loading = false;
                    } else {
                        for (Comment comment : commentList) {
                            comments.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }
}
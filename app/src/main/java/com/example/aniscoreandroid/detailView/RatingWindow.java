package com.example.aniscoreandroid.detailView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.aniscoreandroid.DetailActivity;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.model.bangumiApi.BangumiDetail;
import com.example.aniscoreandroid.model.bangumiScore.BangumiScore;
import com.example.aniscoreandroid.model.bangumiScore.BangumiScoreResponse;
import com.example.aniscoreandroid.utils.ServerCall;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RatingWindow extends DialogFragment {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private double score;
    private View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.rating_window_view, null);
        RatingBar stars = view.findViewById(R.id.rating_stars_clickable);
        stars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                score = rating;
            }
        });

        builder.setView(view);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitScore();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private void submitScore() {
        BangumiDetail detail = DetailActivity.getBangumiDetail();
        String bangumiId = DetailActivity.getBangumiId();
        String currentUserId = Comments.getCurrentId();
        String imageUrl = detail.getImageUrl();
        String title = detail.getTitle();
        String synopsis = detail.getSynopsis();
        HashMap<String, String> body = new HashMap<>();
        body.put("score", (int)score+"");
        body.put("user_id", currentUserId);
        body.put("image_url", imageUrl);
        body.put("title", title);
        body.put("synopsis", synopsis);
        ServerCall service = retrofit.create(ServerCall.class);
        Call<BangumiScoreResponse> submitScoreCall =  service.submitScore(bangumiId, body);
        submitScoreCall.enqueue(new Callback<BangumiScoreResponse>() {
            @Override
            public void onResponse(Call<BangumiScoreResponse> call, Response<BangumiScoreResponse> response) {
                if (response.isSuccessful()) {
                    BangumiScore bangumiScore = response.body().getData().getBangumiScore();
                    double averageScore = bangumiScore.getAverageScore();
                    int userNumber = bangumiScore.getUserNumber();
                    BangumiInfo.setScore(averageScore);
                    BangumiInfo.setUserNumber(userNumber);
                    CommentMain.setScore(averageScore);
                    CommentMain.setUserNumber(userNumber);
                }
            }

            @Override
            public void onFailure(Call<BangumiScoreResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }
}

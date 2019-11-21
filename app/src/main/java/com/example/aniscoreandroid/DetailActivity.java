package com.example.aniscoreandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.aniscoreandroid.model.bangumiApi.BangumiDetail;
import com.example.aniscoreandroid.model.bangumiApi.aired.Aired;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScore;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScoreData;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScoreResponse;
import com.example.aniscoreandroid.utils.ServerCall;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {
    private String sourcePage;                          // the page direct to the detail page
    private String bangumiId;
    private Context context;
    private static BangumiDetail bangumiDetail;
    private Retrofit retrofitApi = new Retrofit.Builder().baseUrl("https://api.jikan.moe/v3/")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private Retrofit retrofitLocal = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        bangumiId = intent.getStringExtra("BANGUMI_ID");
        sourcePage = intent.getStringExtra("SOURCE");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        context = this;
        fetchBangumiDetail();
        fetchScore();
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        return getPreviousActivity();
    }

    /*
     * get parent activity
     */
    private Intent getPreviousActivity() {
        Intent intent = null;
        if (sourcePage.equals("MAIN")) {
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else if (sourcePage.equals("SEASON")) {
            intent = new Intent(this, SeasonBangumiActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else if (sourcePage.equals("USER")) {
            intent = new Intent(this, UserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        return intent;
    }

    private void fetchBangumiDetail() {
        ServerCall service = retrofitApi.create(ServerCall.class);
        Call<BangumiDetail> bangumiDetailCall = service.getBangumiByIdApi(bangumiId);
        bangumiDetailCall.enqueue(new Callback<BangumiDetail>() {
            @Override
            public void onResponse(Call<BangumiDetail> call, Response<BangumiDetail> response) {
                if (response.isSuccessful()) {
                    bangumiDetail = response.body();
                    setBangumi(response.body());
                    findViewById(R.id.detail_page).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<BangumiDetail> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    /*
     * set bangumi basic info
     */
    private void setBangumi(BangumiDetail detail) {
        ImageView image = findViewById(R.id.bangumi_image);
        // load image
        Glide.with(this).load(detail.getImageUrl()).into(image);
        // set title
        ((TextView)findViewById(R.id.bangumi_title)).setText(detail.getTitle());
        // set start date
        StringBuilder sb = new StringBuilder();
        Aired aired = detail.getAired();
        String fromYear = aired.getProp().getFromYear();
        String fromMonth = aired.getProp().getFromMonth();
        String fromDay = aired.getProp().getFromDay();
        if (fromYear.equals("")) {
            sb.append("Not known");
        } else {
            sb.append("Start on ");
            sb.append(fromYear);
            sb.append(".");
            sb.append(fromMonth);
            sb.append(".");
            sb.append(fromDay);
        }
        // set start airing date
        ((TextView)findViewById(R.id.start_date)).setText(sb.toString());
        // set current status
        ((TextView)findViewById(R.id.status)).setText(detail.getStatus());
        // set episode
        ((TextView)findViewById(R.id.episode)).setText(("Episodes:" + detail.getEpisodes()+""));
    }


    /*
     * get score of the bangumi from database
     */
    private void fetchScore() {
        ServerCall service = retrofitLocal.create(ServerCall.class);
        Call<BangumiBriefScoreResponse> briefScoreCall = service.getBangumiBriefById(bangumiId);
        briefScoreCall.enqueue(new Callback<BangumiBriefScoreResponse>() {
            @Override
            public void onResponse(Call<BangumiBriefScoreResponse> call, Response<BangumiBriefScoreResponse> response) {
                if (response.isSuccessful()) {
                    BangumiBriefScoreData data = response.body().getData();
                    BangumiBriefScore bangumiBriefScore = data.getBangumi();
                    double score = bangumiBriefScore.getScore();
                    int userNumber = bangumiBriefScore.getUserNumber();
                    TextView scoreView = findViewById(R.id.score);
                    RatingBar rateStar = findViewById(R.id.stars);
                    TextView userNumberView = findViewById(R.id.user_number);
                    scoreView.setGravity(Gravity.CENTER);
                    if (score == 0.0 && userNumber == 0) {                              // no user rates the bangumi
                        // set score to "No user rate"
                        scoreView.setText(("No user rate"));
                        scoreView.setTextColor(ContextCompat.getColor(context, R.color.viewMore_color));
                        scoreView.setTextSize(16);
                        RelativeLayout layout = findViewById(R.id.score_info);
                        // set layout margin right
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)layout.getLayoutParams();
                        params.rightMargin = 20;
                        layout.setLayoutParams(params);
                        // set rate star and user number invisible
                        rateStar.setVisibility(View.INVISIBLE);
                        userNumberView.setVisibility(View.INVISIBLE);
                        scoreView.setGravity(Gravity.CENTER);
                    } else {
                        scoreView.setText((score + ""));
                        // fill star indicator
                        rateStar.setRating((float)(score/2));
                        // center the score view relative to rating stars
                        int rateStarWidth = rateStar.getWidth();
                        scoreView.setMaxWidth(rateStarWidth);
                        scoreView.setMinWidth(rateStarWidth);
                        scoreView.setGravity(Gravity.CENTER);
                        // center the user number relative to rating stars
                        userNumberView.setText((userNumber + " users"));
                        userNumberView.setMaxWidth(rateStarWidth);
                        userNumberView.setMinWidth(rateStarWidth);
                        userNumberView.setGravity(Gravity.CENTER);
                    }
                }
            }

            @Override
            public void onFailure(Call<BangumiBriefScoreResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    private static BangumiDetail getBangumiDetail() {
        return bangumiDetail;
    }
}
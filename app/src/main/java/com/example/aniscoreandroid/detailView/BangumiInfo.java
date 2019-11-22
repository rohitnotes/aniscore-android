package com.example.aniscoreandroid.detailView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.aniscoreandroid.DetailActivity;
import com.example.aniscoreandroid.R;
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

public class BangumiInfo extends Fragment {
    private View view;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.bangumi_info_view, container, false);
        BangumiDetail bangumiDetail = DetailActivity.getBangumiDetail();
        setBangumi(bangumiDetail);
        fetchScore(bangumiDetail.getAnimeId());
        return view;
    }

    /*
     * set bangumi basic info
     */
    private void setBangumi(BangumiDetail detail) {
        ImageView image = view.findViewById(R.id.bangumi_image);
        // load image
        Glide.with(getContext()).load(detail.getImageUrl()).into(image);
        // set title
        ((TextView)view.findViewById(R.id.bangumi_title)).setText(detail.getTitle());
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
        ((TextView)view.findViewById(R.id.start_date)).setText(sb.toString());
        // set current status
        ((TextView)view.findViewById(R.id.status)).setText(detail.getStatus());
        // set episode
        ((TextView)view.findViewById(R.id.episode)).setText(("Episodes:" + detail.getEpisodes()+""));
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
                    setBangumiInfo(data);
                    view.findViewById(R.id.detail_page).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<BangumiBriefScoreResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    /*
     * set information of bangumi given data fetched from server
     */
    private void setBangumiInfo(BangumiBriefScoreData data) {
        BangumiBriefScore bangumiBriefScore = data.getBangumi();
        double score = bangumiBriefScore.getScore();
        int userNumber = bangumiBriefScore.getUserNumber();
        TextView scoreView = view.findViewById(R.id.score);
        RatingBar rateStar = view.findViewById(R.id.stars);
        TextView userNumberView = view.findViewById(R.id.user_number);
        scoreView.setGravity(Gravity.CENTER);
        if (score == 0.0 && userNumber == 0) {                              // no user rates the bangumi
            // set score to "No user rate"
            scoreView.setText(("No user rate"));
            scoreView.setTextColor(ContextCompat.getColor(getContext(), R.color.viewMore_color));
            scoreView.setTextSize(16);
            RelativeLayout layout = view.findViewById(R.id.score_info);
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
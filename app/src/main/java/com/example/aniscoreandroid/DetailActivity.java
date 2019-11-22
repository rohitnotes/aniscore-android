package com.example.aniscoreandroid;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.aniscoreandroid.detailView.BangumiInfo;
import com.example.aniscoreandroid.model.bangumiApi.BangumiDetail;
import com.example.aniscoreandroid.utils.ServerCall;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity{
    private String sourcePage;                          // the page direct to the detail page
    private String bangumiId;
    private String videoUrl;
    private String videoId;
    private static BangumiDetail bangumiDetail;
    private BottomNavigationView navigationView;
    private final String DEVELOPER_KEY = "AIzaSyCVBSekj5NusFaix11p_4k1P50XU4AjxSk";
    private Retrofit retrofitApi = new Retrofit.Builder().baseUrl("https://api.jikan.moe/v3/")
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
        fetchBangumiDetail();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectFragment(menuItem);
                return true;
            }
        });
        return true;
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

    private void selectFragment(MenuItem item) {
        Fragment fragment = null;
        switch(item.getItemId()) {
            case R.id.info:
                fragment = new BangumiInfo();
                break;
            case R.id.comment:
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
    }

    private void fetchBangumiDetail() {
        ServerCall service = retrofitApi.create(ServerCall.class);
        Call<BangumiDetail> bangumiDetailCall = service.getBangumiByIdApi(bangumiId);
        bangumiDetailCall.enqueue(new Callback<BangumiDetail>() {
            @Override
            public void onResponse(Call<BangumiDetail> call, Response<BangumiDetail> response) {
                if (response.isSuccessful()) {
                    bangumiDetail = response.body();
                    findViewById(R.id.detail_page).setVisibility(View.VISIBLE);
                    MenuItem defaultItem = navigationView.getMenu().getItem(0);
                    selectFragment(defaultItem);
                    videoUrl = response.body().getTrailerLink();
                    getVideoIdFromUrl(videoUrl);
                    setVideo();
                }
            }

            @Override
            public void onFailure(Call<BangumiDetail> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    private void setVideo() {
        if (videoId == null) {
            return;
        }
        YouTubePlayerSupportFragment video = (YouTubePlayerSupportFragment)getSupportFragmentManager().findFragmentById(R.id.video);
        video.initialize(DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
           @Override
           public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
               youTubePlayer.loadVideo(videoId);
           }

           @Override
           public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

           }
       });
    }

    private void getVideoIdFromUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.length() == 0) {
            videoId = null;
        }
        String baseUrl = "https://www.youtube.com/embed/";
        int start = baseUrl.length();
        int end = videoUrl.indexOf("?");
        if (end == -1) {
            end = videoUrl.length();
        }
        videoId = videoUrl.substring(start, end);
    }

    public static BangumiDetail getBangumiDetail() {
        return bangumiDetail;
    }
}
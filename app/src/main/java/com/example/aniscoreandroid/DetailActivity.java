package com.example.aniscoreandroid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.aniscoreandroid.detailView.BangumiInfo;
import com.example.aniscoreandroid.detailView.Comments;
import com.example.aniscoreandroid.model.bangumiApi.BangumiDetail;
import com.example.aniscoreandroid.utils.ServerCall;
import com.google.android.material.tabs.TabLayout;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {
    private String sourcePage;                          // the page direct to the detail page
    private static String bangumiId;
    private String videoUrl;
    private String videoId;
    private static BangumiDetail bangumiDetail;
    //private static BottomNavigationView navigationView;
    private static TabLayout tabNavigation;
    private Retrofit retrofitApi = new Retrofit.Builder().baseUrl("https://api.jikan.moe/v3/")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private YouTubePlayer currentYouTubePlayer;
    private ActionBar actionBar;
    private ViewPager viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        bangumiId = intent.getStringExtra("BANGUMI_ID");
        sourcePage = intent.getStringExtra("SOURCE");
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //actionBar.hide();
        }
        fetchBangumiDetail();
    }


    //added code view pager
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    /*@Override
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
    }*/

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
        } else if (sourcePage.equals("SEARCH")) {
            intent = new Intent(this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        return intent;
    }

    /*public static BottomNavigationView getNavigationView() {
        return navigationView;
    }*/

    public static TabLayout getNavigationView() {
        return tabNavigation;
    }

    public static BangumiDetail getBangumiDetail() {
        return bangumiDetail;
    }

    public static String getBangumiId() {
        return bangumiId;
    }

    /*private void selectFragment(MenuItem item) {
        Fragment fragment = null;
        switch(item.getItemId()) {
            case R.id.info:
                fragment = new BangumiInfo();
                break;
            case R.id.comment:
                fragment = new Comments();
                Bundle args = new Bundle();
                args.putString("bangumiId", bangumiId);
                fragment.setArguments(args);
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
    }*/

    /*
     * fetch bangumi detail from the server
     */
    private void fetchBangumiDetail() {
        ServerCall service = retrofitApi.create(ServerCall.class);
        Call<BangumiDetail> bangumiDetailCall = service.getBangumiByIdApi(bangumiId);
        bangumiDetailCall.enqueue(new Callback<BangumiDetail>() {
            @Override
            public void onResponse(Call<BangumiDetail> call, Response<BangumiDetail> response) {
                if (response.isSuccessful()) {
                    bangumiDetail = response.body();
                    findViewById(R.id.detail_page).setVisibility(View.VISIBLE);
                    //MenuItem defaultItem = navigationView.getMenu().getItem(0);
                    //selectFragment(defaultItem);
                    videoUrl = response.body().getTrailerLink();
                    getVideoIdFromUrl(videoUrl);
                    setVideo();
                    viewPager = findViewById(R.id.pager);
                    String[] titles = {"Info", "Comments"};
                    HashMap<String, String> map = new HashMap<>();
                    map.put("bangumiId", bangumiId);
                    Fragment[] fragments = {new BangumiInfo(), new Comments()};
                    pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments, map, titles, 2);
                    viewPager.setAdapter(pagerAdapter);
                    tabNavigation = findViewById(R.id.tab_layout);
                    tabNavigation.setupWithViewPager(viewPager);
                }
            }

            @Override
            public void onFailure(Call<BangumiDetail> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    private void setVideo() {
        // the bangumi has no youtube url
        if (videoId == null) {
            return;
        }
        final String DEVELOPER_KEY = "AIzaSyCVBSekj5NusFaix11p_4k1P50XU4AjxSk";
        YouTubePlayerSupportFragment video = (YouTubePlayerSupportFragment)getSupportFragmentManager().findFragmentById(R.id.video);
        video.initialize(DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
                youTubePlayer.loadVideo(videoId);
                currentYouTubePlayer = youTubePlayer;
                // set action bar state (hide or show) when video is playing or pause
                currentYouTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                    @Override
                    public void onPlaying() {
                        actionBar.hide();
                    }

                    @Override
                    public void onPaused() {
                        actionBar.show();
                    }

                    @Override
                    public void onStopped() {

                    }

                    @Override
                    public void onBuffering(boolean b) {

                    }

                    @Override
                    public void onSeekTo(int i) {

                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    /*
     * get the youtube video id from the url given
     */
    private void getVideoIdFromUrl(String videoUrl) {
        // the bangumi has no youtube url
        if (videoUrl == null || videoUrl.length() == 0) {
            videoId = null;
            return;
        }
        String baseUrl = "https://www.youtube.com/embed/";
        int start = baseUrl.length();
        int end = videoUrl.indexOf("?");
        if (end == -1) {
            end = videoUrl.length();
        }
        videoId = videoUrl.substring(start, end);
    }
}
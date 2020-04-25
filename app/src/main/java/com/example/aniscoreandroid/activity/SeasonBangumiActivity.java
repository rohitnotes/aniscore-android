package com.example.aniscoreandroid.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.activity.MainActivity;
import com.example.aniscoreandroid.adapter.BangumiBriefAdapter;
import com.example.aniscoreandroid.model.bangumiList.BangumiBrief;
import com.example.aniscoreandroid.model.bangumiList.BangumiListData;
import com.example.aniscoreandroid.model.bangumiList.BangumiListResponse;
import com.example.aniscoreandroid.utils.ServerCall;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeasonBangumiActivity extends AppCompatActivity {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000").
            addConverterFactory(GsonConverterFactory.create()).build();
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_season);
        recyclerView = findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        String year = intent.getStringExtra(MainActivity.SELECTED_YEAR);
        String season = intent.getStringExtra(MainActivity.SELECTED_SEASON);
        getBangumiOfGivenSeason(year, season);
    }

    private void getBangumiOfGivenSeason(String year, String season) {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<BangumiListResponse> seasonCall = service.getBangumiOfYearSeason(year, season);
        seasonCall.enqueue(new Callback<BangumiListResponse>() {
            @Override
            public void onResponse(Call<BangumiListResponse> call, Response<BangumiListResponse> response) {
                if (response.isSuccessful()) {
                    BangumiListData data = response.body().getData();
                    List<BangumiBrief> bangumiList = data.getBangumiList();
                    recyclerView.setAdapter(new BangumiBriefAdapter(bangumiList));
                    recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 3));
                }
            }

            @Override
            public void onFailure(Call<BangumiListResponse> call, Throwable t) {
                System.out.println("fail");
            }
        });
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        return getPreviousActivity();
    }

    /*
     * direct back to the main activity without reloading
     */
    private Intent getPreviousActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        return intent;
    }
}

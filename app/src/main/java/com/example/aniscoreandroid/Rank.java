package com.example.aniscoreandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.model.BangumiBriefScore;
import com.example.aniscoreandroid.model.BangumiListScoreData;
import com.example.aniscoreandroid.model.BangumiListScoreResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Rank extends Fragment {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private int bangumiNumber = 100;
    private View view;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.rank, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        getRank();
        return view;
    }

    private void getRank() {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<BangumiListScoreResponse> rankCall = service.getBangumiRank(bangumiNumber);
        rankCall.enqueue(new Callback<BangumiListScoreResponse>() {
            @Override
            public void onResponse(Call<BangumiListScoreResponse> call, Response<BangumiListScoreResponse> response) {
                if (response.isSuccessful()) {
                    BangumiListScoreData data = response.body().getData();
                    List<BangumiBriefScore> bangumiList = data.getBangumiList();
                    recyclerView.setAdapter(new BangumiRankAdapter(bangumiList));
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onFailure(Call<BangumiListScoreResponse> call, Throwable t) {
                ((TextView) view.findViewById(R.id.test)).setText(t.toString());
            }
        });
    }
}
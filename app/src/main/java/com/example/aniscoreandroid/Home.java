package com.example.aniscoreandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.model.BangumiBrief;
import com.example.aniscoreandroid.model.BangumiListData;
import com.example.aniscoreandroid.model.BangumiListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends Fragment {
    private RecyclerView rankView;
    Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000").
            addConverterFactory(GsonConverterFactory.create()).build();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.home_view, container, false);
        rankView = view.findViewById(R.id.rank_list);
        getRank();
        return view;
    }

    private void getRank() {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<BangumiListResponse> rankCall = service.getBangumiRank(3);
        rankCall.enqueue(new Callback<BangumiListResponse>() {
            @Override
            public void onResponse(Call<BangumiListResponse> call, Response<BangumiListResponse> response) {
                if (response.isSuccessful()) {
                    BangumiListData data = response.body().getData();
                    List<BangumiBrief> bangumiList = data.getBangumiList();
                    rankView.setAdapter(new BangumiBriefAdapter(bangumiList));
                    rankView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                }
            }

            @Override
            public void onFailure(Call<BangumiListResponse> call, Throwable t) {
                System.out.println("fail");
            }
        });
    }
}

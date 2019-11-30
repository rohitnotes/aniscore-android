package com.example.aniscoreandroid.searchResultView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.SearchActivity;
import com.example.aniscoreandroid.adapter.BangumiSearchAdapter;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScore;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiListScoreResponse;
import com.example.aniscoreandroid.utils.ServerCall;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BangumiResult extends Fragment {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private String query;
    private RecyclerView recyclerView;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.bangumi_search_result_view, container, false);
        Bundle args = getArguments();
        query = args.getString("query");
        recyclerView = view.findViewById(R.id.bangumi_search_results);
        getSearchResult();
        return view;
    }

    private void getSearchResult() {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<BangumiListScoreResponse> searchBangumiCall = service.searchBangumiByName(query);
        searchBangumiCall.enqueue(new Callback<BangumiListScoreResponse>() {
            @Override
            public void onResponse(Call<BangumiListScoreResponse> call, Response<BangumiListScoreResponse> response) {
                if (response.isSuccessful()) {
                    List<BangumiBriefScore> result = response.body().getData().getBangumiList();
                    if (result == null || result.size() == 0) {                             // no corresponding bangumi found
                        // set to 404 page
                        Fragment[] fragments = SearchActivity.getFragments();
                        PagerAdapter pagerAdapter = SearchActivity.getPagerAdapter();
                        fragments[0] = new NoResult();
                        pagerAdapter.notifyDataSetChanged();
                    } else {
                        // set result message
                        StringBuilder sb = new StringBuilder();
                        sb.append(result.size() + " results for").append(" \"" + query + "\"");
                        ((TextView)view.findViewById(R.id.bangumi_result_message)).setText(sb.toString());
                        // set bangumi list
                        recyclerView.setAdapter(new BangumiSearchAdapter(result));
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
            }

            @Override
            public void onFailure(Call<BangumiListScoreResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }
}
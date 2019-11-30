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
import com.example.aniscoreandroid.adapter.UserSearchAdapter;
import com.example.aniscoreandroid.model.user.User;
import com.example.aniscoreandroid.model.user.UserListResponse;
import com.example.aniscoreandroid.utils.ServerCall;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserResult extends Fragment {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private RecyclerView recyclerView;
    private String query;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.user_search_view, container,false);
        Bundle args = getArguments();
        query = args.getString("query");
        recyclerView = view.findViewById(R.id.user_search_results);
        getSearchResult();
        return view;
    }

    private void getSearchResult() {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<UserListResponse> searchUserCall = service.searchUserByUsername(query);
        searchUserCall.enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                if (response.isSuccessful()) {
                    List<User> result = response.body().getUsersData().getUsers();
                    if (result == null || result.size() == 0) {                 // no corresponding user found
                        // direct to 404 page
                        Fragment[] fragments = SearchActivity.getFragments();
                        PagerAdapter pagerAdapter = SearchActivity.getPagerAdapter();
                        fragments[1] = new NoResult();
                        pagerAdapter.notifyDataSetChanged();
                    } else {
                        // set result message
                        StringBuilder sb = new StringBuilder();
                        sb.append(result.size() + " results for").append(" \"" + query + "\"");
                        ((TextView)view.findViewById(R.id.user_result_message)).setText(sb.toString());
                        recyclerView.setAdapter(new UserSearchAdapter(response.body().getUsersData().getUsers()));
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }
}
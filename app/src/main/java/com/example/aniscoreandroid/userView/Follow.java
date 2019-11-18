package com.example.aniscoreandroid.userView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.UserActivity;
import com.example.aniscoreandroid.adapter.FollowAdapter;
import com.example.aniscoreandroid.model.user.User;
import com.example.aniscoreandroid.model.user.UserResponse;
import com.example.aniscoreandroid.utils.ServerCall;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * for both follower and following
 */
public class Follow extends Fragment {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private String FIND_USER_SUCCESS = "Succesfully find the user";
    private List<User> follows;
    private FollowAdapter adapter;
    private String mode;                // "following" or "follower"

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.follow_layout, container, false);
        follows = new ArrayList<>();
        adapter = new FollowAdapter(follows, R.layout.detail_follow_view);
        List<String> followList;
        RecyclerView follow_view = view.findViewById(R.id.follow_list);
        if (mode.equals("following")) {
            followList = UserActivity.getUserInfo().getFollowing();
        } else {
            followList = UserActivity.getUserInfo().getFollower();
        }
        if (followList != null && followList.size() > 0) {
            getAllUsersById(followList);
            follow_view.setAdapter(adapter);
            follow_view.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            // the user has no followers/followings
            follow_view.setVisibility(View.GONE);
            TextView no_follow_view = view.findViewById(R.id.no_follow);
            no_follow_view.setVisibility(View.VISIBLE);
            no_follow_view.setText(("No " + mode));
        }
        return view;
    }

    public Follow(String pageMode) {
        mode = pageMode;
    }

    private void getAllUsersById(final List<String> followIds) {
        ExecutorService exec = Executors.newFixedThreadPool(followIds.size());
        for (int i = 0; i < followIds.size(); i++) {
            final int idx = i;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    getUserById(followIds.get(idx));
                }
            });
        }
    }

    /*
     * get single user by id
     */
    private void getUserById(String userId) {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<UserResponse> getUserCall = service.getUserById(userId);
        getUserCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equals(FIND_USER_SUCCESS)) {
                        follows.add(response.body().getUserData().getUser());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }
}
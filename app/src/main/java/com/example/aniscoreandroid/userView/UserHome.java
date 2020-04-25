package com.example.aniscoreandroid.userView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.activity.UserActivity;
import com.example.aniscoreandroid.adapter.BangumiBriefScoreAdapter;
import com.example.aniscoreandroid.adapter.FollowAdapter;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScore;
import com.example.aniscoreandroid.model.user.ScoredBangumi;
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

public class UserHome extends Fragment {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private View view;
    private User user;
    private List<User> followings;             // followings
    private List<User> followers;              // followers
    private String FIND_USER_SUCCESS = "Succesfully find the user";
    private FollowAdapter followingAdapter;
    private FollowAdapter followerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.user_home_view, container, false);
        user = UserActivity.getUserInfo();
        followings = new ArrayList<>();
        followingAdapter = new FollowAdapter(followings, R.layout.home_follow_layout);
        followers = new ArrayList<>();
        followerAdapter =  new FollowAdapter(followers, R.layout.home_follow_layout);
        setScoreBangumi();
        setFollowing();
        setFollowers();
        TextView viewMoreBangumi = view.findViewById(R.id.view_more_1);
        viewMoreBangumi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toScoredBangumi();
            }
        });
        TextView viewMoreFollowing = view.findViewById(R.id.view_more_2);
        viewMoreFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFollow("following");
            }
        });
        TextView viewMoreFollower = view.findViewById(R.id.view_more_3);
        viewMoreFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFollow("follower");
            }
        });
        return view;
    }

    /*
     * click view more to switch to scored bangumi section
     */
    private void toScoredBangumi() {
        /*UserActivity.getNavigationView().setSelectedItemId(R.id.scored_bangumi);
        Fragment fragment = new ScoredBangumiView();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();*/
        UserActivity.getViewPager().setCurrentItem(1);
    }

    /*
     * click view more to switch to following or follower section,
     * @param mode following or follower
     */
    private void toFollow(String mode) {
        if(mode.equals("following")) {
            //UserActivity.getNavigationView().setSelectedItemId(R.id.following);
            // slide to the following section with tab updated
            UserActivity.getViewPager().setCurrentItem(2);
        } else {
            //UserActivity.getNavigationView().setSelectedItemId(R.id.follower);
            // slide to the follower section with tab updated
            UserActivity.getViewPager().setCurrentItem(3);
        }
        /*Fragment fragment = new Follow(mode);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();*/
    }

    /*
     * set scored bangumi section
     */
    private void setScoreBangumi() {
        RecyclerView scoredBangumi = view.findViewById(R.id.scored_bangumi_list);
        ScoredBangumi[] scoredBangumis = user.getScoredBangumis();
        List<BangumiBriefScore> bangumiBriefScoreList = new ArrayList<>();
        int limit = Math.min(scoredBangumis.length, 3);
        if (limit == 0) {
            scoredBangumi.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.no_bangumi).setVisibility(View.VISIBLE);
            return;
        }
        for (int i = 0; i < limit; i++) {
            bangumiBriefScoreList.add(new BangumiBriefScore(scoredBangumis[i]));
        }
        scoredBangumi.setAdapter(new BangumiBriefScoreAdapter(bangumiBriefScoreList));
        scoredBangumi.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    /*
     * set following section
     */
    private void setFollowing() {
        RecyclerView followingView;
        List<String> followingIds;
        followingView = view.findViewById(R.id.following_list);
        followingIds = user.getFollowing();
        if (followingIds.size() == 0) {
            followingView.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.no_following).setVisibility(View.VISIBLE);
            return;
        }
        getAllUsersByIds(followingIds, "following");
        followingView.setAdapter(followingAdapter);
        followingView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    /*
     * set follower section
     */
    private void setFollowers() {
        RecyclerView followerView;
        List<String> followerIds;
        followerView = view.findViewById(R.id.follower_list);
        followerView.setAdapter(followerAdapter);
        followerIds = user.getFollower();
        if (followerIds.size() == 0) {
            followerView.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.no_follower).setVisibility(View.VISIBLE);
            return;
        }
        getAllUsersByIds(followerIds, "follower");
        followerView.setAdapter(followerAdapter);
        followerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    /*
     * get all followings or followers
     */
    private void getAllUsersByIds(final List<String> followIds, final String mode) {
        int threadNum = Math.min(followIds.size(), 3);
        ExecutorService exec = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < followIds.size(); i++) {
            final int idx = i;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    getUserById(followIds.get(idx), mode);
                }
            });
        }
        exec.shutdown();
    }

    /*
     * get single user by id
     */
    private void getUserById(String userId, final String mode) {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<UserResponse> getUserCall = service.getUserById(userId);
        getUserCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equals(FIND_USER_SUCCESS)) {
                        if (mode.equals("following")) {
                            followings.add(response.body().getUserData().getUser());
                            followingAdapter.notifyDataSetChanged();
                        } else {
                            followers.add(response.body().getUserData().getUser());
                            followerAdapter.notifyDataSetChanged();
                        }
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
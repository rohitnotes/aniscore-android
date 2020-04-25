package com.example.aniscoreandroid.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.activity.UserActivity;
import com.example.aniscoreandroid.model.user.User;
import com.example.aniscoreandroid.model.user.UserResponse;
import com.example.aniscoreandroid.utils.ServerCall;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder>{
    private List<User> users;
    private Context context;
    private final String baseUrl = "http://10.0.2.2:4000/";
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private SharedPreferences preference;
    private String currentUserId;
    private User currentUser;
    private int layout;

    public class FollowViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username;
        MaterialButton follow;

        public FollowViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.avatar);
            username = view.findViewById(R.id.username);
            follow = view.findViewById(R.id.follow);
        }
    }

    public FollowAdapter(List<User> userList, int layoutView) {
        users = userList;
        layout = layoutView;
    }

    @NonNull
    @Override
    public FollowAdapter.FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        context = parent.getContext();
        preference = context.getSharedPreferences("Current user", Context.MODE_PRIVATE);
        if (preference != null) {
            currentUserId = preference.getString("userId", null);
        }
        currentUser = UserActivity.getCurrentUser();
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FollowViewHolder holder, int position) {
        final User current = users.get(position);
        // set username
        holder.username.setText(current.getUsername());
        // set avatar
        setAvatar(current, holder);
        // set follow/following button visibility
        if (currentUser == null || currentUserId == null || current.getUserId().equals(currentUserId)) {
            holder.follow.setVisibility(View.GONE);
        } else {
            // judge whether current user is following the user
            if (currentUser.getFollowing().contains(current.getUserId())) {
                holder.follow.setText(("Unfollow"));
            } else {
                holder.follow.setText(("Follow"));
            }
            // set follow or unfollow handler
            holder.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateStatus(holder.follow, current.getUserId());
                    if (holder.follow.getText().equals("Unfollow")) {
                        holder.follow.setText(("Follow"));
                    } else {
                        holder.follow.setText(("Unfollow"));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /*
     * set avatar view and click listener
     */
    private void setAvatar(final User current, final FollowViewHolder holder) {
        // determine whether the user has his own avatar
        if (current.getAvatar()!=null && current.getAvatar().length() > 0) {
            Glide.with(context).load(baseUrl + current.getAvatar()).asBitmap().centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .override(150, 150).into(new BitmapImageViewTarget(holder.avatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.avatar.setImageDrawable(circularBitmapDrawable);
                    }
                });
        } else {
            Glide.with(context).load(R.drawable.default_avatar).asBitmap().centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .override(150, 150).into(new BitmapImageViewTarget(holder.avatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.avatar.setImageDrawable(circularBitmapDrawable);
                    }
                });
        }
        // click avatar to other user pages
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ((Activity)v.getContext()).getIntent();
                intent.putExtra("USER_ID", current.getUserId());
                ((Activity)(v.getContext())).overridePendingTransition(0, 0);
                ((Activity)(v.getContext())).finish();
                ((Activity)(v.getContext())).overridePendingTransition(0, 0);
                (v.getContext()).startActivity(intent);
            }
        });
    }

    /*
     * follow or unfollow the user with userId based on the button
     */
    private void updateStatus(final MaterialButton button, final String userId) {
        // no user has logged in
        if (preference == null) {
            return;
        }
        // judge whether there is user logged in or the two userIds are both current user id
        if (currentUserId == null || currentUserId.equals(userId)) {
            return;
        }
        ServerCall service = retrofit.create(ServerCall.class);
        Call<UserResponse> updateStatusCall;
        HashMap<String, String> body = new HashMap<>();
        final String currentStatus = button.getText().toString();
        // determine current status
        if (currentStatus.equals("Unfollow")) {               // unfollow the user
            body.put("unfollow_id", userId);
            updateStatusCall = service.unFollowUserById(currentUserId, body);
        } else {
            body.put("following_id", userId);
            updateStatusCall = service.followUserById(currentUserId, body);
        }
        // follow/unfollow user of userId
        updateStatusCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    // update current user in login activity
                    if (currentStatus.equals("Unfollow")) {
                        currentUser.unFollow(userId);
                        User visitedUser = UserActivity.getUserInfo();
                        // user page current user visited is his own
                        if (visitedUser!=null && visitedUser.getUserId().equals(currentUserId)) {
                            visitedUser.unFollow(userId);
                        }
                    } else {
                        currentUser.follow(userId);
                        User visitedUser = UserActivity.getUserInfo();
                        // user page current user visited is his own
                        if (visitedUser!=null && visitedUser.getUserId().equals(currentUserId)) {
                            visitedUser.follow(userId);
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
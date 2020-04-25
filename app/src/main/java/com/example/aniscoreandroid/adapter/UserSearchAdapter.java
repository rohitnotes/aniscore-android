package com.example.aniscoreandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.util.LinkedList;
import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserSearchViewHolder> {
    private List<User> searchResults;
    private Context context;
    private final String baseUrl = "http://10.0.2.2:4000/";

    public class UserSearchViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username;
        TextView followingNum;
        TextView followerNum;
        RelativeLayout section;

        public UserSearchViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.avatar);
            username = view.findViewById(R.id.username);
            followingNum = view.findViewById(R.id.following_num);
            followerNum = view.findViewById(R.id.follower_num);
            section = view.findViewById(R.id.user_section);
        }
    }

    public UserSearchAdapter(List<User> results) {
        searchResults = results;
    }

    @NonNull
    @Override
    public UserSearchAdapter.UserSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_search_result_layout, parent, false);
        context = view.getContext();
        return new UserSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserSearchViewHolder holder, int position) {
        final User current = searchResults.get(position);
        // set avatar
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
        // set username
        holder.username.setText(current.getUsername());
        // set following number
        LinkedList<String> following = current.getFollowing();
        if (following == null || following.size() == 0) {
            holder.followingNum.setText(("No following"));
        } else {
            holder.followingNum.setText((following.size() + " followings"));
        }
        // set follower number
        LinkedList<String> follower = current.getFollower();
        if (follower == null || follower.size() == 0) {
            holder.followerNum.setText(("No follower"));
        } else {
            holder.followerNum.setText((follower.size() + " followers"));
        }
        // set click listener for the whole section, clicking the section will direct to user page
        holder.section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("USER_ID", current.getUserId());
                intent.putExtra("SOURCE", "SEARCH");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }
}
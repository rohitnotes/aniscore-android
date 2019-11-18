package com.example.aniscoreandroid.homeView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.aniscoreandroid.LoginActivity;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.UserActivity;

public class User extends Fragment {
    private View view;
    private String baseUrl = "http://10.0.2.2:4000/";
    private SharedPreferences preference;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.user_view, container, false);
        preference = getActivity().getSharedPreferences("Current user", Context.MODE_PRIVATE);
        // currently there is user logged in
        if (preference != null && preference.getString("username", null) != null) {
            view.findViewById(R.id.layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.login_layout).setVisibility(View.GONE);
            // set username and email
            ((TextView)view.findViewById(R.id.username)).setText(preference.getString("username", ""));
            ((TextView)view.findViewById(R.id.email)).setText(preference.getString("email", ""));
            setAvatar(view);
        } else {                // no user logged in
            view.findViewById(R.id.layout).setVisibility(View.GONE);
            view.findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
            // set log in click listener
            view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    /*
     * set avatar
     */
    private void setAvatar(View view) {
        final ImageView avatar = view.findViewById(R.id.avatar);
        final Context context = getContext();
        // set avatar
        String avatarPath = preference.getString("avatar", "");
        // current user has selected an avatar, avoid cache in order to update avatar
        if(avatarPath.length() > 0) {
            Glide.with(context).load(baseUrl + preference.getString("avatar", ""))
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .override(200, 200).into(avatar);
            // set click listener to direct to the user page
            view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    intent.putExtra("USER_ID", preference.getString("userId", ""));
                    startActivity(intent);
                }
            });
        } else {                // the user has not selected an avatar
            Glide.with(context).load(R.drawable.default_avatar).asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .override(200, 200).into(avatar);
        }
    }
}
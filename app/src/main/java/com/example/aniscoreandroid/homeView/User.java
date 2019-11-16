package com.example.aniscoreandroid.homeView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.aniscoreandroid.R;

public class User extends Fragment {
    private View view;
    private String baseUrl = "http://10.0.2.2:4000/";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.user_view, container, false);
        SharedPreferences preference = getActivity().getSharedPreferences("Current user", Context.MODE_PRIVATE);
        if (preference != null) {
            ((TextView)view.findViewById(R.id.username)).setText(preference.getString("username", ""));
            final ImageView avatar = view.findViewById(R.id.avatar);
            final Context context = getContext();
            Glide.with(context).load(baseUrl + preference.getString("avatar", ""))
                   .into(avatar);
        }
        return view;
    }
}

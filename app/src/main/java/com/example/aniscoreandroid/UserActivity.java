package com.example.aniscoreandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.aniscoreandroid.model.user.User;
import com.example.aniscoreandroid.model.user.UserResponse;
import com.example.aniscoreandroid.utils.ServerCall;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserActivity extends AppCompatActivity {
    private String userId;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private final String baseUrl = "http://10.0.2.2:4000/";
    private User user;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_user);
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        getUser();
    }

    private void getUser() {
        ServerCall service = retrofit.create(ServerCall.class);
        final Call<UserResponse> userCall = service.getUserById(userId);
        userCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equals("Succesfully find the user")) {
                        user = response.body().getUserData().getUser();
                        setView(user);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                System.out.println("fail");
            }
        });
    }

    private void setView(User user) {
        // set background
        final ImageView background = findViewById(R.id.background);
        String backgroundPath = user.getBackground();
        if (backgroundPath != null && backgroundPath.length() > 0) {
            // avoid caching in order to update background
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Glide.with(this).load(baseUrl + backgroundPath).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(background);
        }
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("background");
            }
        });
        // set avatar
        final ImageView avatar = findViewById(R.id.avatar);
        String avatarPath = user.getAvatar();
        if (avatarPath != null && avatarPath.length() > 0) {
            // avoid caching in order to update background
            Glide.with(this).load(baseUrl + user.getAvatar()).asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).centerCrop()
                .into(new BitmapImageViewTarget(avatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        avatar.setImageDrawable(circularBitmapDrawable);
                    }
                });
        }
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("avatar");
            }
        });
    }

    private void selectImage(String mode) {
        Intent intent = new Intent(this, SelectImageActivity.class);
        intent.putExtra("MODE", mode);
        intent.putExtra("USER_ID", userId);
        if (mode.equals("avatar")) {
            intent.putExtra("CURRENT_IMAGE", user.getAvatar());
        } else {
            intent.putExtra("CURRENT_IMAGE", user.getBackground());
        }
        startActivity(intent);
    }
}
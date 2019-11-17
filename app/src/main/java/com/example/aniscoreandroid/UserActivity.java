package com.example.aniscoreandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
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
                        //((TextView)findViewById(R.id.test)).setText(user.getBackground());
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
            Glide.with(this).load(baseUrl + backgroundPath).into(background);
        }
        // set avatar
        final ImageView avatar = findViewById(R.id.avatar);
        String avatarPath = user.getAvatar();
        if (avatarPath != null && avatarPath.length() > 0) {
            Glide.with(this).load(baseUrl + user.getAvatar()).asBitmap().centerCrop()
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
    }
}
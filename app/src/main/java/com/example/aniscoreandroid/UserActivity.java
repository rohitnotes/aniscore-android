package com.example.aniscoreandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.aniscoreandroid.model.user.ScoredBangumi;
import com.example.aniscoreandroid.model.user.User;
import com.example.aniscoreandroid.model.user.UserResponse;
import com.example.aniscoreandroid.userView.Follow;
import com.example.aniscoreandroid.userView.ScoredBangumiView;
import com.example.aniscoreandroid.userView.UserHome;
import com.example.aniscoreandroid.utils.ServerCall;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private SharedPreferences preference;
    private static BottomNavigationView navigationView;
    private static User user;                           // the user page current user visited
    private static User currentUser;                    // current user

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_user);
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        preference = getApplicationContext().getSharedPreferences("Current user", Context.MODE_PRIVATE);
        navigationView = findViewById(R.id.user_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectFragment(menuItem);
                return true;
            }
        });
        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.execute(new Runnable() {
            @Override
            public void run() {
                retrieveCurrentUser();
            }
        });
        exec.execute(new Runnable() {
            @Override
            public void run() {
                getUser();
            }
        });
        exec.shutdown();
    }

    /*
     * get current user by stored userId
     */
    private void retrieveCurrentUser() {
        if (preference == null) {
            return;
        }
        String currentUserId = preference.getString("userId", null);
        if (currentUserId == null) {
            return;
        }
        ServerCall service = retrofit.create(ServerCall.class);
        Call<UserResponse> currentUserCall = service.getUserById(currentUserId);
        currentUserCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equals("Succesfully find the user")) {
                        currentUser = response.body().getUserData().getUser();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                System.out.println("fail");
            }
        });
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static BottomNavigationView getNavigationView() {
        return navigationView;
    }

    /*
     * navigation bar listener
     */
    private void selectFragment(MenuItem menuItem) {
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.home:
                fragment = new UserHome();
                break;
            case R.id.scored_bangumi:
                fragment = new ScoredBangumiView();
                break;
            case R.id.following:
                fragment = new Follow("following");
                break;
            case R.id.follower:
                fragment = new Follow("follower");
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
    }

    /*
     * get current visited user, used in fragments of user activity
     */
    public static User getUserInfo() {
        return user;
    }

    /*
     * get user info from server
     */
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
                        // set default fragment
                        MenuItem item = navigationView.getMenu().getItem(0);
                        selectFragment(item);
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
        //set username
        TextView username = findViewById(R.id.username);
        username.setText(user.getUsername());
        // set following
        TextView following = findViewById(R.id.following);
        following.setText(((user.getFollowing().size()) + " followings"));
        // set follower
        TextView follower = findViewById(R.id.follower);
        follower.setText(((user.getFollower().size()) + " followers"));
        // set background
        final ImageView background = findViewById(R.id.background);
        setBackground(background);
        // set avatar
        final ImageView avatar = findViewById(R.id.avatar);
        setAvatar(avatar);
        // check whether the user is current user.
        if (userId.equals(preference.getString("userId", ""))) {
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage("avatar");
                }
            });
            background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage("background");
                }
            });
        }
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

    /*
     * set background image view
     */
    private void setBackground(ImageView background) {
        String backgroundPath = user.getBackground();
        if (backgroundPath != null && backgroundPath.length() > 0) {
            // avoid caching in order to update background
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Glide.with(this).load(baseUrl + backgroundPath).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(background);
        }
    }

    /*
     * set avatar image view
     */
    private void setAvatar(final ImageView avatar) {
        String avatarPath = user.getAvatar();
        if (avatarPath != null && avatarPath.length() > 0) {
            // avoid caching in order to update avatar
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
        } else {
            Glide.with(this).load(R.drawable.default_avatar).asBitmap()
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
    }
}
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.aniscoreandroid.model.user.User;
import com.example.aniscoreandroid.model.user.UserResponse;
import com.example.aniscoreandroid.userView.Follow;
import com.example.aniscoreandroid.userView.ScoredBangumiView;
import com.example.aniscoreandroid.userView.UserHome;
import com.example.aniscoreandroid.utils.ServerCall;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserActivity extends AppCompatActivity {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private final String baseUrl = "http://10.0.2.2:4000/";
    private SharedPreferences preference;
    private static BottomNavigationView navigationView;
    private String userId;                              // the userId of the user current user visited
    private static User user;                           // the user page current user visited
    private static User currentUser;                    // current user
    private String bangumiId;                           // bangumi id, for condition switching from comment section of detail page to user page
    private String sourcePage;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_user);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        sourcePage = intent.getStringExtra("SOURCE");
        bangumiId = intent.getStringExtra("bangumiId");
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


    @Override
    public Intent getSupportParentActivityIntent() {
        return getPreviousActivity();
    }

    /*
     * direct back to the main activity without reloading
     */
    private Intent getPreviousActivity() {
        Intent intent = null;
        if (sourcePage.equals("MAIN")) {
            intent = new Intent(this, MainActivity.class);
        } else if (sourcePage.equals("DETAIL")){
            intent = new Intent(this, DetailActivity.class);
            intent.putExtra("BANGUMI_ID", bangumiId);
        }
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        startActivity(intent);
        return intent;
    }

    /*
     * get current user
     */
    private void retrieveCurrentUser() {
        if (preference == null) {
            findViewById(R.id.follow_status).setVisibility(View.GONE);         // set status button to invisible
            return;
        }
        String currentUserId = preference.getString("userId", null);
        if (currentUserId == null) {
            findViewById(R.id.follow_status).setVisibility(View.GONE);      // set status button to invisible
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
                        setButton();
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
        // avoid caching in order to update background
        if (backgroundPath != null && backgroundPath.length() > 0) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Glide.with(this).load(baseUrl + backgroundPath).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(background);
        } else {
            Glide.with(this).load(R.drawable.default_background).centerCrop()
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

    /*
     * set the button
     */
    private void setButton() {
        final MaterialButton button = findViewById(R.id.follow_status);
        if (currentUser.getFollowing().contains(userId)) {
            button.setText(("Following"));
        } else {
            button.setText(("Follow"));
        }
        if(currentUser.getUserId().equals(userId)){
            button.setVisibility(View.GONE);
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currentStatus = button.getText().toString();
                    updateStatus(button);
                    if (currentStatus.equals("Following")) {
                        button.setText(("Follow"));
                    } else {
                        button.setText(("Following"));
                    }
                }
            });
        }
    }


    /*
     * follow or unfollow the user with userId based on the button
     */
    private void updateStatus(final MaterialButton button) {
        // no user has logged in
        if (preference == null) {
            return;
        }
        // judge whether there is user logged in or the two userIds are both current user id
        if (currentUser == null || currentUser.getUserId().equals(userId)) {
            return;
        }
        final String currentUserId = currentUser.getUserId();
        ServerCall service = retrofit.create(ServerCall.class);
        Call<UserResponse> updateStatusCall;
        HashMap<String, String> body = new HashMap<>();
        final String currentStatus = button.getText().toString();
        // determine current status
        if (currentStatus.equals("Following")) {               // unfollow the user
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
                    } else {
                        currentUser.follow(userId);
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
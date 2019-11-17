package com.example.aniscoreandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.aniscoreandroid.homeView.Home;
import com.example.aniscoreandroid.homeView.Rank;
import com.example.aniscoreandroid.homeView.Season;
import com.example.aniscoreandroid.homeView.User;
import com.example.aniscoreandroid.model.user.AuthResponse;
import com.example.aniscoreandroid.utils.ServerCall;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000").
            addConverterFactory(GsonConverterFactory.create()).build();
    private BottomNavigationView navigationView;
    private int selectId = 0;
    public final static String SELECTED_YEAR = "BANGUMI_YEAR";
    public final static String SELECTED_SEASON = "BANGUMI_SEASON";
    private final String baseUrl = "http://10.0.2.2:4000/";
    private static SharedPreferences preference;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() != selectId) {
                    selectFragment(menuItem);
                }
                return true;
            }
        });
        MenuItem home = navigationView.getMenu().getItem(0);
        selectFragment(home);
        selectId = R.id.home;
        // first time initiate the app
        if (preference == null) {
            preference = getApplicationContext().getSharedPreferences("Current user", Context.MODE_PRIVATE);
            editor = preference.edit();
        } else {            // the shared preference has been initialized
            Intent intent = getIntent();
            String username = intent.getStringExtra(LoginActivity.CURRENT_USER_NAME);
            // currently there is user logged in
            if (username != null && username.length() > 0) {
                String email = intent.getStringExtra(LoginActivity.CURRENT_USER_EMAIL);
                String userId = intent.getStringExtra(LoginActivity.CURRENT_USER_ID);
                String avatar = intent.getStringExtra(LoginActivity.CURRENT_USER_AVATAR);
                editor.putString("username", username);
                editor.putString("email", email);
                editor.putString("userId", userId);
                editor.putString("avatar", avatar);
                editor.apply();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem authItem= menu.findItem(R.id.login);
        // currently there is a user logged in
        if (preference != null && preference.getString("username", null) != null) {
            authItem.setTitle("Log out");
            authItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    logout();
                    return true;
                }
            });
        } else {            // currently there is no user logged in
            authItem.setTitle("Log in");
            authItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    toLogin();
                    return true;
                }
            });
        }
        // set avatar
        final ImageView avatar = new ImageView(this);
        avatar.setMaxWidth(120);
        avatar.setMinimumWidth(120);
        avatar.setMaxHeight(120);
        avatar.setMinimumHeight(120);
        final Context context = avatar.getContext();
        if (preference != null && preference.getString("avatar", "").length() > 0) {
            Glide.with(this).load(baseUrl + preference.getString("avatar", null)).asBitmap().centerCrop().
                    into(new BitmapImageViewTarget(avatar) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            avatar.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
        menu.findItem(R.id.avatar).setActionView(avatar);
        return true;
    }

    /*
     * click the login on the navigation bar to direct to login page
     */
    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /*
     * click the logout on the navibar to logout
     */
    private void logout() {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<AuthResponse> logoutCall = service.logout();
        logoutCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equals("Log Out Successfully")) {
                        Intent intent = getIntent();
                        editor.clear();
                        editor.commit();
                        intent.putExtra(LoginActivity.CURRENT_USER_NAME, "");
                        intent.putExtra(LoginActivity.CURRENT_USER_ID, "");
                        intent.putExtra(LoginActivity.CURRENT_USER_AVATAR, "");
                        intent.putExtra(LoginActivity.CURRENT_USER_EMAIL, "");
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                System.out.println("fail");
            }
        });
    }

    /*
    * listener for selecting menu items of the bottom navigation bar
    */
    private void selectFragment(MenuItem menuItem) {
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.home:
                fragment = new Home();
                break;
            case R.id.season:
                fragment = new Season();
                break;
            case R.id.rank:
                fragment = new Rank();
                break;
            case R.id.user:
                fragment = new User();
                break;
        }
        if (fragment != null) {
            selectId = menuItem.getItemId();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
    }
}
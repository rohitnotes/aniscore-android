package com.example.aniscoreandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.aniscoreandroid.homeView.Home;
import com.example.aniscoreandroid.homeView.Rank;
import com.example.aniscoreandroid.homeView.Season;
import com.example.aniscoreandroid.model.user.UserResponse;
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
    private String username;
    private Menu optionsMenu;
    private static final String CURRENT_USER_NAME = "CURRENT_USER_NAME_MAIN";

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
        Intent intent = getIntent();
        username = intent.getStringExtra(LoginActivity.CURRENT_USER_NAME);
        if (username == null) {
            username = "NO USER";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        optionsMenu = menu;
        inflater.inflate(R.menu.options_menu, menu);
        menu.findItem(R.id.login).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                toLogin();
                return true;
            }
        });
        menu.findItem(R.id.user).setTitle(username);
        return true;
    }

    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

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
        }
        if (fragment != null) {
            selectId = menuItem.getItemId();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
    }
}
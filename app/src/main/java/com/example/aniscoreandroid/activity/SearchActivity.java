package com.example.aniscoreandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.searchResultView.BangumiResult;
import com.example.aniscoreandroid.searchResultView.UserResult;
import com.example.aniscoreandroid.adapter.ScreenSlidePagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    private String query;
    private ViewPager viewPager;
    private static PagerAdapter pagerAdapter;
    private static Fragment[] fragments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        query = intent.getStringExtra("SEARCH_QUERY");
        viewPager = findViewById(R.id.search_pager);
        fragments = new Fragment[2];
        fragments[0] = new BangumiResult();
        fragments[1] = new UserResult();
        HashMap<String, String> args = new HashMap<>();
        args.put("query", query);
        String[] titles = {"Bangumi", "User"};
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments, args, titles, 2, PagerAdapter.POSITION_NONE);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabNavigation = findViewById(R.id.tab_layout);
        tabNavigation.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        // set search bar
        SearchView searchView = (SearchView)menu.findItem(R.id.search_bar).getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String newQuery) {
                        query = newQuery;
                        reload();
                        return true;
                    }
                }
        );
        return true;
    }

    //added code view pager
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    public static Fragment[] getFragments() {
        return fragments;
    }

    public static PagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    private void reload() {
        Intent intent = getIntent();
        intent.putExtra("SEARCH_QUERY", query);
        finish();
        startActivity(intent);
    }
}
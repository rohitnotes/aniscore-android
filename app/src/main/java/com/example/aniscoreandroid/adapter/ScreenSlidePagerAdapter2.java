package com.example.aniscoreandroid.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.aniscoreandroid.userView.Follow;
import com.example.aniscoreandroid.userView.ScoredBangumiView;
import com.example.aniscoreandroid.userView.UserHome;

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter2 extends FragmentStateAdapter {
    public ScreenSlidePagerAdapter2(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment =  new UserHome();
                break;
            case 1:
                fragment = new ScoredBangumiView();
                break;
            case 2:
                fragment = new Follow("following");
                break;
            case 3:
                fragment = new Follow("follower");
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
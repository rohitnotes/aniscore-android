package com.example.aniscoreandroid.utils;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.aniscoreandroid.userView.UserHome;

import java.util.HashMap;
import java.util.Map;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private int pageNum;
    private Bundle args;
    private String[] titles;
    private Fragment[] fragments;
    private int positionMode = POSITION_UNCHANGED;

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ScreenSlidePagerAdapter(FragmentManager fm, Fragment[] fragmentList, HashMap<String, String> arguments, String[] titleList, int pageNumber) {
        super(fm);
        if (arguments != null) {
            args = new Bundle();
            for (Map.Entry<String, String> arg : arguments.entrySet()) {
                args.putString(arg.getKey(), arg.getValue());
            }
        }
        fragments = fragmentList;
        titles = titleList;
        pageNum = pageNumber;
    }

    public ScreenSlidePagerAdapter(FragmentManager fm, Fragment[] fragmentList, HashMap<String, String> arguments, String[] titleList, int pageNumber, int mode) {
        super(fm);
        if (arguments != null) {
            args = new Bundle();
            for (Map.Entry<String, String> arg : arguments.entrySet()) {
                args.putString(arg.getKey(), arg.getValue());
            }
        }
        fragments = fragmentList;
        titles = titleList;
        pageNum = pageNumber;
        positionMode = mode;
    }

    @Override
    public Fragment getItem(int position) {
        if (args != null) {
            fragments[position].setArguments(args);
        }
        return fragments[position];
    }

    @Override
    public int getCount() {
        return pageNum;
    }

    @Override
    public int getItemPosition(Object object) {
        return positionMode;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
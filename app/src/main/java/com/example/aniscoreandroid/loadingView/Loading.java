package com.example.aniscoreandroid.loadingView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.aniscoreandroid.R;

public class Loading extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.loading, container, false);
        return view;
    }
}
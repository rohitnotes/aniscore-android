package com.example.aniscoreandroid.userView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.UserActivity;
import com.example.aniscoreandroid.adapter.BangumiBriefScoreAdapter;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScore;
import com.example.aniscoreandroid.model.user.ScoredBangumi;

import java.util.ArrayList;
import java.util.List;

public class ScoredBangumiView extends Fragment {
    List<BangumiBriefScore> scoredBangumis;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.scored_bangumi_view, container, false);
        ScoredBangumi[] scoredBangumiList = UserActivity.getUserInfo().getScoredBangumis();
        RecyclerView recyclerView = view.findViewById(R.id.scored_bangumi_list);
        // the user has not scored any bangumis
        if (scoredBangumiList.length == 0) {
            TextView noBangumiView = view.findViewById(R.id.no_bangumi);
            noBangumiView.setVisibility(View.VISIBLE);
            noBangumiView.setText(R.string.no_score_bangumi);
        } else {
            scoredBangumis = new ArrayList<>();
            for (int i = 0; i < scoredBangumiList.length; i++) {
                scoredBangumis.add(new BangumiBriefScore(scoredBangumiList[i]));
            }
            recyclerView.setAdapter(new BangumiBriefScoreAdapter(scoredBangumis));
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
        return view;
    }
}
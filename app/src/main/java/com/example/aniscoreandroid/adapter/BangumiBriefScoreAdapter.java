package com.example.aniscoreandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScore;

import java.util.List;

public class BangumiBriefScoreAdapter extends RecyclerView.Adapter<BangumiBriefScoreAdapter.BangumiBriefScoreViewHolder> {
    private List<BangumiBriefScore> bangumiList;
    private Context context;

    public class BangumiBriefScoreViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        TextView score;

        public BangumiBriefScoreViewHolder(View bangumiView) {
            super(bangumiView);
            image = bangumiView.findViewById(R.id.bangumi_image);
            title = bangumiView.findViewById(R.id.bangumi_title);
            score = bangumiView.findViewById(R.id.score);
        }
    }

    public BangumiBriefScoreAdapter(List<BangumiBriefScore> bangumis) {
        bangumiList = bangumis;
    }

    @NonNull
    @Override
    public BangumiBriefScoreAdapter.BangumiBriefScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bangumibriefscore_layout, parent, false);
        context = view.getContext();
        return new BangumiBriefScoreAdapter.BangumiBriefScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BangumiBriefScoreViewHolder holder, int position) {
        BangumiBriefScore currentBangumi = bangumiList.get(position);
        Glide.with(context).load(currentBangumi.getImageUrl()).into(holder.image);
        String title = currentBangumi.getTitle();
        if(title.length() > 25) {
            holder.title.setText((title.substring(0, 25) + "..."));
        } else {
            holder.title.setText(title);
        }
        holder.image.setColorFilter(Color.rgb(150, 150, 150), PorterDuff.Mode.MULTIPLY);
        holder.score.setText((currentBangumi.getScore() + ""));
    }

    @Override
    public int getItemCount() {
        return bangumiList.size();
    }
}
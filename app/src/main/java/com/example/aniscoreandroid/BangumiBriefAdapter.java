package com.example.aniscoreandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.model.BangumiBrief;

import java.util.List;

public class BangumiBriefAdapter extends RecyclerView.Adapter<BangumiBriefAdapter.BangumiBriefViewHolder> {
    private List<BangumiBrief> bangumiList;

    public class BangumiBriefViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;

        public BangumiBriefViewHolder(View bangumiView) {
            super(bangumiView);
            image = bangumiView.findViewById(R.id.bangumi_image);
            title = bangumiView.findViewById(R.id.bangumi_title);
        }
    }

    public BangumiBriefAdapter(List<BangumiBrief> bangumis) {
        bangumiList = bangumis;
    }

    @NonNull
    @Override
    public BangumiBriefAdapter.BangumiBriefViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bangumibrief_layout, parent, false);
        return new BangumiBriefAdapter.BangumiBriefViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(BangumiBriefViewHolder holder, int position) {
        BangumiBrief currentBangumi = bangumiList.get(position);
        new DownloadImageTask(holder.image).execute(currentBangumi.getImageUrl());
        String title = currentBangumi.getTitle();
        if(title.length() > 30) {
            holder.title.setText((title.substring(0, 30) + "..."));
        } else {
            holder.title.setText(title);
        }
    }

    @NonNull
    @Override
    public int getItemCount() {
        return bangumiList.size();
    }
}

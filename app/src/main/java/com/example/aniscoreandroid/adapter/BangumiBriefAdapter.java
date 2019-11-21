package com.example.aniscoreandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aniscoreandroid.DetailActivity;
import com.example.aniscoreandroid.MainActivity;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.SeasonBangumiActivity;
import com.example.aniscoreandroid.model.bangumiList.BangumiBrief;

import java.util.List;

public class BangumiBriefAdapter extends RecyclerView.Adapter<BangumiBriefAdapter.BangumiBriefViewHolder> {
    private List<BangumiBrief> bangumiList;
    private Context context;

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
        context = view.getContext();
        return new BangumiBriefAdapter.BangumiBriefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BangumiBriefViewHolder holder, int position) {
        final BangumiBrief currentBangumi = bangumiList.get(position);
        Glide.with(context).load(currentBangumi.getImageUrl()).into(holder.image);
        String title = currentBangumi.getTitle();
        if(title.length() > 25) {
            holder.title.setText((title.substring(0, 25) + "..."));
        } else {
            holder.title.setText(title);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                if (context.getClass().equals(MainActivity.class)) {
                    intent.putExtra("SOURCE", "MAIN");
                } else if (context.getClass().equals(SeasonBangumiActivity.class)) {
                    intent.putExtra("SOURCE", "SEASON");
                }
                intent.putExtra("BANGUMI_ID", currentBangumi.getAnimeId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bangumiList.size();
    }
}
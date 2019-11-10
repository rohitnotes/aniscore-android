package com.example.aniscoreandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.model.BangumiBrief;

import java.util.List;

public class BangumiSeasonAdapter extends RecyclerView.Adapter<BangumiSeasonAdapter.BangumiSeasonViewHolder> {
    private List<List<BangumiBrief>> seasons;
    private Context context;

    public class BangumiSeasonViewHolder extends  RecyclerView.ViewHolder{
        RecyclerView recyclerView;

        public BangumiSeasonViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.single_recycler_view);
        }
    }

    public BangumiSeasonAdapter(List<List<BangumiBrief>> seasonList) {
        seasons = seasonList;
    }

    @NonNull
    @Override
    public BangumiSeasonAdapter.BangumiSeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_season_view, parent, false);
        context = parent.getContext();
        return new BangumiSeasonViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(BangumiSeasonViewHolder holder, int position) {
        List<BangumiBrief> current = seasons.get(position);
        holder.recyclerView.setAdapter(new BangumiBriefAdapter(current));
        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
    }

    @NonNull
    @Override
    public int getItemCount() {
        return seasons.size();
    }
}
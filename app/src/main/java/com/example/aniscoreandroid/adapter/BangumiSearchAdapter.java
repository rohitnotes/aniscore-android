package com.example.aniscoreandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aniscoreandroid.activity.DetailActivity;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScore;

import java.util.List;

public class BangumiSearchAdapter extends RecyclerView.Adapter<BangumiSearchAdapter.BangumiSearchViewHolder> {
    private List<BangumiBriefScore> searchResults;
    private Context context;

    public class BangumiSearchViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView synopsis;
        TextView score;
        TextView userNumber;
        RelativeLayout section;

        public BangumiSearchViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.bangumi_image);
            title = view.findViewById(R.id.bangumi_title);
            synopsis = view.findViewById(R.id.synopsis);
            score = view.findViewById(R.id.score);
            userNumber = view.findViewById(R.id.user_number);
            section = view.findViewById(R.id.bangumi_section);
        }
    }

    public BangumiSearchAdapter(List<BangumiBriefScore> results) {
        searchResults = results;
    }

    @NonNull
    @Override
    public BangumiSearchAdapter.BangumiSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bangumi_search_result_layout, parent, false);
        context = view.getContext();
        return new BangumiSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BangumiSearchViewHolder holder, int position) {
        final BangumiBriefScore currentBangumi = searchResults.get(position);
        // set bangumi image
        Glide.with(context).load(currentBangumi.getImageUrl()).into(holder.image);
        // set bangumi title
        holder.title.setText(currentBangumi.getTitle());
        // set bangumi synopsis, if synopsis has length > 80, get substring
        String synopsis = currentBangumi.getSynopsis();
        if (synopsis.length() > 80) {
            synopsis = synopsis.substring(0, 80) + "...";
        } else if (synopsis.length() == 0) {
            synopsis = "No synopsis";
        }
        holder.synopsis.setText(synopsis);
        // set bangumi score and user number, if no user scored, then set score and user number invisible
        if (currentBangumi.getUserNumber() == 0) {
            holder.score.setVisibility(View.INVISIBLE);
            holder.userNumber.setVisibility(View.INVISIBLE);
        } else {
            holder.score.setVisibility(View.VISIBLE);
            holder.userNumber.setVisibility(View.VISIBLE);
            holder.score.setText((currentBangumi.getScore() + ""));
            holder.userNumber.setText((currentBangumi.getUserNumber() + " users"));
        }
        // set the whole section clickable, clicking the section will direct user to the detail activity
        holder.section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("BANGUMI_ID", currentBangumi.getAnimeId());
                intent.putExtra("SOURCE", "SEARCH");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }
}
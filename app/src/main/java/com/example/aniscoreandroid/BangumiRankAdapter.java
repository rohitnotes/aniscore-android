package com.example.aniscoreandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aniscoreandroid.model.BangumiBriefScore;

import java.util.List;

public class BangumiRankAdapter extends RecyclerView.Adapter<BangumiRankAdapter.BangumiRankViewHolder> {
    private List<BangumiBriefScore> rankList;
    private Context context;

    public class BangumiRankViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView synopsis;
        TextView rankNumber;
        TextView score;
        TextView totalScore;
        TextView userNumber;

        public BangumiRankViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.bangumi_image);
            title = view.findViewById(R.id.bangumi_title);
            synopsis = view.findViewById(R.id.synopsis);
            rankNumber = view.findViewById(R.id.rank_number);
            score = view.findViewById(R.id.score);
            totalScore = view.findViewById(R.id.total_score);
            userNumber = view.findViewById(R.id.user_number);
        }
    }

    public BangumiRankAdapter(List<BangumiBriefScore> list) {
        rankList = list;
    }

    @NonNull
    @Override
    public BangumiRankAdapter.BangumiRankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bangumirank_layout, parent, false);
        context = view.getContext();
        return new BangumiRankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BangumiRankViewHolder holder, int position) {
        BangumiBriefScore currentBangumi = rankList.get(position);
        Glide.with(context).load(currentBangumi.getImageUrl()).into(holder.image);
        holder.title.setText(currentBangumi.getTitle());
        String synopsis = currentBangumi.getSynopsis();
        if (synopsis.length() > 70) {
            holder.synopsis.setText((synopsis.substring(0, 70) + "..."));
        } else {
            holder.synopsis.setText(synopsis);
        }
        holder.rankNumber.setText((position+1+""));
        holder.score.setText((currentBangumi.getScore() + ""));
        holder.totalScore.setText(("Total Score  " + currentBangumi.getTotalScore() + ""));
        holder.userNumber.setText((currentBangumi.getUserNumber() + " scored"));
    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }
}
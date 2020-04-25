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
import com.example.aniscoreandroid.activity.DetailActivity;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.model.bangumiListScore.BangumiBriefScore;

import java.util.List;

/*
 * used for rank fragment in main activity, the rank is based on total score
 */
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
        final BangumiBriefScore currentBangumi = rankList.get(position);
        // set bangumi image
        Glide.with(context).load(currentBangumi.getImageUrl()).into(holder.image);
        // set title
        holder.title.setText(currentBangumi.getTitle());
        // set synopsis, if synopsis has more than 70 characters, take only first 70 characters
        String synopsis = currentBangumi.getSynopsis();
        if (synopsis.length() > 70) {
            holder.synopsis.setText((synopsis.substring(0, 70) + "..."));
        } else {
            holder.synopsis.setText(synopsis);
        }
        // set rank number
        holder.rankNumber.setText((position+1+""));
        // set average score
        holder.score.setText((currentBangumi.getScore() + ""));
        // set total score
        holder.totalScore.setText(("Total Score  " + currentBangumi.getTotalScore() + ""));
        // set number of user scored
        holder.userNumber.setText((currentBangumi.getUserNumber() + " scored"));
        // set click listener for bangumi image, clicking the image will direct user to detail activity
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("BANGUMI_ID", currentBangumi.getAnimeId());
                intent.putExtra("SOURCE", "MAIN");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }
}
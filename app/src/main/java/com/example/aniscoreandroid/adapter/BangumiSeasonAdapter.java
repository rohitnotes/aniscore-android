package com.example.aniscoreandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.activity.MainActivity;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.activity.SeasonBangumiActivity;
import com.example.aniscoreandroid.model.bangumiList.BangumiBrief;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * the adapter is only used for season section in home page, for listing first 3 bangumi of each season
 */
public class BangumiSeasonAdapter extends RecyclerView.Adapter<BangumiSeasonAdapter.BangumiSeasonViewHolder> {
    private List<List<BangumiBrief>> seasons;
    private Context context;

    public class BangumiSeasonViewHolder extends RecyclerView.ViewHolder{
        RecyclerView recyclerView;
        TextView title;
        TextView viewMore;

        public BangumiSeasonViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.single_recycler_view);
            title = view.findViewById(R.id.season_title);
            viewMore = view.findViewById(R.id.viewMore);
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
        int[] currentDate = getCurrentYearMonth();
        List<BangumiBrief> current = seasons.get(position);
        int currentMonth = currentDate[1];
        int month = 0;
        // turn on current month into current season month,  current season month can only be 1, 4, 7, 10
        if (currentMonth >= 1 && currentMonth < 4) {
            currentMonth = 1;
        } else if (currentMonth >= 4 && currentMonth < 7) {
            currentMonth = 4;
        } else if (currentMonth >= 7 && currentMonth < 10) {
            currentMonth = 7;
        } else {
            currentMonth = 10;
        }
        int year = currentDate[0];
        // previous years bangumi
        if (position > currentMonth/4) {
            year--;                         // pass to previous year
            year -= (position-currentMonth/4-1) / 4;             // get year based on index
            int remain = (position - currentMonth / 4 - 1) % 4;
            // get the month of bangumi based on current season month and the position
            month = 10 - (remain * 3);
        } else {
            month = currentMonth;
        }
        final String yearStr = year + "";
        // get season corresponding to month
        final String season = getSeasonGivenMonth(month);
        holder.title.setText((year + "年" + month + "月番"));
        // set click listener for view more, clicking view more will direct user to season fragment in main activity
        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SeasonBangumiActivity.class);
                intent.putExtra(MainActivity.SELECTED_YEAR, yearStr);
                intent.putExtra(MainActivity.SELECTED_SEASON, season);
                context.startActivity(intent);
            }
        });
        holder.recyclerView.setAdapter(new BangumiBriefAdapter(current));
        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
    }

    @NonNull
    @Override
    public int getItemCount() {
        return seasons.size();
    }

    /*
     * get the season depend on current date
     */
    private int[] getCurrentYearMonth() {
        Date date = new Date();
        int[] time = new int[2];                    // time[0] is year, time[1] is season
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        time[0] = year;
        time[1] = month;
        return time;
    }

    /*
     * get season given month
     */
    private String getSeasonGivenMonth(int month) {
        if (month == 1) {
            return "winter";
        } else if (month == 4) {
            return "spring";
        } else if (month == 7) {
            return "summer";
        } else {
            return "fall";
        }
    }
}
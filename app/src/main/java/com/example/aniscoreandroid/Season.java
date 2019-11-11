package com.example.aniscoreandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.model.BangumiBrief;
import com.example.aniscoreandroid.model.BangumiListData;
import com.example.aniscoreandroid.model.BangumiListResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Season extends Fragment {
    private RecyclerView recyclerView;
    CountDownLatch latch;
    private List<List<BangumiBrief>> seasonBangumiList;
    private View view;
    private int seasonNum;
    private int currentYear;
    private int startYear = 2005;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000").
            addConverterFactory(GsonConverterFactory.create()).build();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.season_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        seasonNum = getSeasonNumber();
        seasonBangumiList = new ArrayList<>();
        for (int i = 0; i < seasonNum; i++) {
            seasonBangumiList.add(new ArrayList<BangumiBrief>());
        }
        latch = new CountDownLatch(seasonNum);
        loadBangumi();
        return view;
    }

    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(new BangumiSeasonAdapter(seasonBangumiList));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /*
     * get 3 bangumi from each season concurrently
     */
    public void loadBangumi() {
        ExecutorService exec = Executors.newCachedThreadPool();
        String[] currentTime = getCurrentSeason();
        int year = Integer.parseInt(currentTime[0]);
        int month = Integer.parseInt(currentTime[2]);
        for (int i = month; i >= 1; i-=3) {                             //get all bangumi of current year
            final String season = getSeasonGivenMonth(i);
            final String yearStr = year + "";
            final int mon = i;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    getBangumiOfYearSeason(yearStr, season, mon);
                }
            });
        }
        // get previous year bangumi
        for (int i = year-1; i >= startYear; i--) {
            for (int j = 10; j >= 1; j-=3) {
                final String season = getSeasonGivenMonth(j);
                final String yearStr = i + "";
                final int mon = j;
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        getBangumiOfYearSeason(yearStr, season, mon);
                    }
                });
            }
        }
        exec.shutdown();
        try {
            latch.await();
        } catch(Exception e) {
        } finally{
            recyclerView.setAdapter(new BangumiSeasonAdapter(seasonBangumiList));
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private void getBangumiOfYearSeason(final String year, String season, final int month) {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<BangumiListResponse> getBangumiCall = service.getBangumiOfYearSeasonLimit(year, season);
        BangumiListData data = null;
        try {
            data = getBangumiCall.execute().body().getData();
        } catch(Exception e) {
            ((TextView) view.findViewById(R.id.test)).setText(e.toString());
        }
        if (data != null) {
            List<BangumiBrief> bangumiList = data.getBangumiList();
            int idx = (currentYear - Integer.parseInt(year)) * 4 + (10-month)/3;
            if (bangumiList.size() > 3) {
                List<BangumiBrief> firstThreeBangumi = new ArrayList<>();
                firstThreeBangumi.add(bangumiList.get(0));
                firstThreeBangumi.add(bangumiList.get(1));
                firstThreeBangumi.add(bangumiList.get(2));
                seasonBangumiList.set(idx, firstThreeBangumi);
            } else {
                seasonBangumiList.set(idx, bangumiList);
            }
        }
        latch.countDown();


        /*getBangumiCall.enqueue(new Callback<BangumiListResponse>() {
            @Override
            public void onResponse(Call<BangumiListResponse> call, Response<BangumiListResponse> response) {
                BangumiListData data = response.body().getData();
                List<BangumiBrief> bangumiList = data.getBangumiList();
                int idx = (currentYear - Integer.parseInt(year)) * 4 + (10-month)/3;
                if (bangumiList.size() > 3) {
                    List<BangumiBrief> firstThreeBangumi = new ArrayList<>();
                    firstThreeBangumi.add(bangumiList.get(0));
                    firstThreeBangumi.add(bangumiList.get(1));
                    firstThreeBangumi.add(bangumiList.get(2));
                    seasonBangumiList.set(idx, firstThreeBangumi);
                } else {
                    seasonBangumiList.set(idx, bangumiList);
                }
                //latch.countDown();
            }

            @Override
            public void onFailure(Call<BangumiListResponse> call, Throwable t) {

            }
        });*/
    }


    private int getSeasonNumber() {
        String[] current = getCurrentSeason();
        int month = Integer.parseInt(current[2]);
        int year = Integer.parseInt(current[0]);
        return (year-startYear) * 4 + (month-1)/3 + 1;
    }


    private String getSeasonGivenMonth(int month) {
        String season = "";
        if (month == 1) {
            season = "winter";
        } else if (month == 4) {
            season = "spring";
        } else if (month == 7) {
            season = "summer";
        } else {
            season = "fall";
        }
        return season;
    }


    private String[] getCurrentSeason() {
        Date date = new Date();
        String[] time = new String[3];      // time[0] is year, time[1] is season
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        time[0] = year + "";
        if (month >= 1 && month <= 4) {
            time[1] = "winter";
            time[2] = "1";
        } else if (month >= 4 && month <= 7) {
            time[1] = "spring";
            time[2] = "4";
        } else if (month >= 7 && month <= 10) {
            time[1] = "summer";
            time[2] = "7";
        } else {
            time[1] = "fall";
            time[2] = "10";
        }
        return time;
    }
}
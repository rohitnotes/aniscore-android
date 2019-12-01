package com.example.aniscoreandroid.homeView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.utils.EndlessListLoad;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.utils.ServerCall;
import com.example.aniscoreandroid.adapter.BangumiSeasonAdapter;
import com.example.aniscoreandroid.model.bangumiList.BangumiBrief;
import com.example.aniscoreandroid.model.bangumiList.BangumiListData;
import com.example.aniscoreandroid.model.bangumiList.BangumiListResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Season extends Fragment {
    private RecyclerView recyclerView;
    private List<List<BangumiBrief>> seasonBangumiList;
    private View view;
    Hashtable<Integer, int[]> map = new Hashtable<>();
    private int currentYear;
    private int currentMonth;
    private int startYear = 2005;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000").
            addConverterFactory(GsonConverterFactory.create()).build();
    BangumiSeasonAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.season_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH)+1;
        seasonBangumiList = new LinkedList<>();
        adapter = new BangumiSeasonAdapter(seasonBangumiList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        loadBangumi(0);
        recyclerView.addOnScrollListener(new EndlessListLoad(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadBangumi(page);
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    public void loadBangumi(int idx) {
        ExecutorService exec = Executors.newFixedThreadPool(4);
        final int year = currentYear-idx;
        if (year < startYear) {
            return;
        }
        final String yearStr = year + "";
        for (int j = 10; j >= 1; j -= 3) {
            if (j > currentMonth && year == currentYear) {
                continue;
            }
            final String season = getSeasonGivenMonth(j);
            final int month = j;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    getBangumiOfYearSeason(yearStr, season, month);
                }
            });
        }
        exec.shutdown();
    }

    private void getBangumiOfYearSeason(final String year, String season, final int month) {
        if (Integer.parseInt(year) < startYear) {
            return;
        }
        final int yearNum = Integer.parseInt(year);
        ServerCall service = retrofit.create(ServerCall.class);
        Call<BangumiListResponse> getBangumiCall = service.getBangumiOfYearSeasonLimit(year, season);
        getBangumiCall.enqueue(new Callback<BangumiListResponse>() {
            @Override
            public void onResponse(Call<BangumiListResponse> call, Response<BangumiListResponse> response) {
                BangumiListData data = response.body().getData();
                List<BangumiBrief> bangumiList = data.getBangumiList();
                if (bangumiList.size() > 3) {
                    List<BangumiBrief> firstThreeBangumi = new ArrayList<>();
                    firstThreeBangumi.add(bangumiList.get(0));
                    firstThreeBangumi.add(bangumiList.get(1));
                    firstThreeBangumi.add(bangumiList.get(2));
                    addToList(yearNum, month, firstThreeBangumi);
                } else {
                    addToList(yearNum, month, bangumiList);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<BangumiListResponse> call, Throwable t) {
                System.out.println("fail");
            }
        });
    }

    private String getSeasonGivenMonth(int month) {
        String season = "";
        if (month == 1) {
            season = "winter";
        } else if (month == 4) {
            season = "spring";
        } else if (month == 7) {
            season = "summer";
        } else if (month == 10) {
            season = "fall";
        }
        return season;
    }

    /*
     * sort the new added season by date
     */
    private synchronized void addToList(int year, int month, List<BangumiBrief> list) {
        if (seasonBangumiList.size() == 0) {
            int[] temp = new int[2];
            temp[0] = year;
            temp[1] = month;
            map.put(0, temp);
            seasonBangumiList.add(list);
            return;
        }
        int current = seasonBangumiList.size()-1;
        if (year < map.get(current)[0]) {
            seasonBangumiList.add(list);
            map.put(current+1, new int[]{year, month});
            return;
        }
        while (current >= 0 && map.get(current)[0] <= year) {
            if (month < map.get(current)[1] && year == map.get(current)[0]) {
                break;
            }
            current--;
        }
        if (current == seasonBangumiList.size()-1) {
            seasonBangumiList.add(list);
            map.put(seasonBangumiList.size()-1, new int[]{year, month});
            return;
        } else {
            seasonBangumiList.add(current+1, list);
        }
        int[] origin = map.get(current+1);
        map.put(current+1,new int[] {year, month});
        for (int i = current+2; i < seasonBangumiList.size()-1; i++) {
            int[] temp = map.get(i);
            map.put(i, origin);
            origin = temp;
        }
        map.put(seasonBangumiList.size()-1, origin);
    }
}
package com.example.aniscoreandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.model.bangumiApi.name.Name;

import java.util.ArrayList;
import java.util.List;

// the class is used in detail view for displaying bangumi type
public class BangumiTypeAdapter extends RecyclerView.Adapter<BangumiTypeAdapter.BangumiTypeViewHolder> {
    private List<String> bangumiTypes;
    public class BangumiTypeViewHolder extends RecyclerView.ViewHolder {
        private TextView typeView;

        public BangumiTypeViewHolder(View view) {
            super(view);
            typeView = view.findViewById(R.id.type);
        }
    }

    public BangumiTypeAdapter(Name[] bangumiTypeList) {
        bangumiTypes = new ArrayList<>();
        int minLen = Math.min(bangumiTypeList.length, 4);
        for (int i = 0; i < minLen; i++) {
            bangumiTypes.add(bangumiTypeList[i].getName());
        }
    }

    @NonNull
    @Override
    public BangumiTypeAdapter.BangumiTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.type_view, parent, false);
        return new BangumiTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BangumiTypeViewHolder holder, int position) {
        holder.typeView.setText(bangumiTypes.get(position));
    }

    @Override
    public int getItemCount() {
        return bangumiTypes.size();
    }
}
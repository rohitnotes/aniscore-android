package com.example.aniscoreandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.model.comment.Comment;

import java.util.ArrayList;
import java.util.List;

/*
 * the adapter is only used at the main comment page, displaying first 3 reply of a parent comment
 */
public class ReplyBriefAdapter extends RecyclerView.Adapter<ReplyBriefAdapter.ReplyViewHolder> {
    List<Comment> replies;

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView replyUsername;
        TextView replyContent;

        public ReplyViewHolder(View view) {
            super(view);
            replyUsername = view.findViewById(R.id.replied_username);
            replyContent = view.findViewById(R.id.replied_content);
        }
    }

    public ReplyBriefAdapter(List<Comment> replyList) {
        replies = new ArrayList<>();
        int len = Math.min(replyList.size(), 3);
        for (int i = 0; i < len; i++) {
            replies.add(replyList.get(i));
        }
    }

    @NonNull
    @Override
    public ReplyBriefAdapter.ReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reply_layout, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReplyViewHolder holder, int position) {
        Comment current = replies.get(position);
        holder.replyUsername.setText(current.getUsername());
        holder.replyContent.setText(current.getCommentContent());
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }
}
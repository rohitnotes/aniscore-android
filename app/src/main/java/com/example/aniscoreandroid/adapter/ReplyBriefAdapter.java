package com.example.aniscoreandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.activity.UserActivity;
import com.example.aniscoreandroid.detailView.CommentDetail;
import com.example.aniscoreandroid.detailView.Comments;
import com.example.aniscoreandroid.model.comment.Comment;

import java.util.List;

/*
 * the adapter is only used at the main comment page, displaying first 3 reply of a parent comment
 */
public class ReplyBriefAdapter extends RecyclerView.Adapter<ReplyBriefAdapter.ReplyViewHolder> {
    private List<Comment> replies;
    private View view;

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView replyUsername;
        TextView replyContent;
        LinearLayout replyLayout;

        public ReplyViewHolder(View view) {
            super(view);
            replyUsername = view.findViewById(R.id.replied_username);
            replyContent = view.findViewById(R.id.replied_content);
            replyLayout = view.findViewById(R.id.reply_layout);
        }
    }

    public ReplyBriefAdapter(List<Comment> replyList) {
        replies = replyList;
    }

    @NonNull
    @Override
    public ReplyBriefAdapter.ReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reply_layout, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReplyViewHolder holder, int position) {
        final Comment current = replies.get(position);
        holder.replyUsername.setText((current.getUsername() + ": "));
        // set listener for username, clicking username to direct to user page of the user
        holder.replyUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = view.getContext();
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("USER_ID", current.getUserId());
                intent.putExtra("SOURCE", "DETAIL");            // set source page for directing back
                context.startActivity(intent);
            }
        });
        holder.replyContent.setText(current.getCommentContent());
        holder.replyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CommentDetail();
                Bundle args = Comments.getArgs();
                args.putString("commentId", current.getParentCommentId());
                fragment.setArguments(args);
                FragmentTransaction ft = ((AppCompatActivity)view.getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.comment_section, fragment);
                ft.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }
}
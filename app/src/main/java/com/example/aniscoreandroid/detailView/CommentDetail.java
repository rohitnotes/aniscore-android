package com.example.aniscoreandroid.detailView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.DetailActivity;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.adapter.CommentAdapter;
import com.example.aniscoreandroid.utils.EndlessListLoad;
import com.example.aniscoreandroid.model.comment.Comment;
import com.example.aniscoreandroid.model.comment.CommentResponse;
import com.example.aniscoreandroid.utils.ServerCall;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentDetail extends Fragment {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private RecyclerView recyclerView;
    private List<Comment> replies;
    private String bangumId;
    private String parentCommentId;
    private CommentAdapter adapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.comment_detail_view, container, false);
        final Bundle args = getArguments();
        bangumId = args.getString("bangumiId");
        parentCommentId = args.getString("commentId");
        recyclerView = view.findViewById(R.id.reply_list);
        replies = new LinkedList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new CommentAdapter(replies, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new EndlessListLoad(linearLayoutManager, 2) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getReplies(page+1);              // page starts at 1, not 0
            }
        });
        DetailActivity.getNavigationView().setVisibility(View.GONE);
        // set click listener for the cross, click cross will direct user back to comment main
        view.findViewById(R.id.cross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.getNavigationView().setVisibility(View.VISIBLE);
                //getActivity().overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
                args.remove("commentId");
                Fragment fragment = new CommentMain();
                fragment.setArguments(args);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.comment_section, fragment);
                ft.commit();
            }
        });
        getParentComment();
        getReplies(1);
        return view;
    }

    /*
     * get parent comment by its id
     */
    private void getParentComment() {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<CommentResponse> getCommentCall = service.getCommentById(parentCommentId);
        getCommentCall.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                // getComments() always has length 1
                Comment parentComment =  response.body().getData().getComments().get(0);
                if (replies.size() > 0) {
                    replies.add(0, parentComment);
                } else {
                    replies.add(parentComment);
                }
                adapter.notifyDataSetChanged();
                Comments.setSubmitClickListener(parentComment.getCommentId(), parentComment.getCommentId(),
                        parentComment.getUsername(), replies, adapter);
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                ((TextView)view.findViewById(R.id.test)).setText(t.toString());
                System.out.println(t.toString());
            }
        });
    }

    /*
     * get a page of the parent comment's replies
     */
    private void getReplies(int page) {
        ServerCall service = retrofit.create(ServerCall.class);
        Call<CommentResponse> replyCall = service.getRepliesOfCommentWithPage(parentCommentId, page);
        replyCall.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful()) {
                    List<Comment> replyList = response.body().getData().getComments();
                    for (Comment comment : replyList) {
                        replies.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }
}
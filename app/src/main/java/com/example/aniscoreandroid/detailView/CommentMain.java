package com.example.aniscoreandroid.detailView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.adapter.CommentAdapter;
import com.example.aniscoreandroid.adapter.EndlessListLoad;
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

public class CommentMain extends Fragment {
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private View view;
    private String bangumiId;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comment> comments;
    private boolean loading = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.comment_main_view, container, false);
        Bundle args = getArguments();
        bangumiId = args.getString("bangumiId");
        recyclerView = view.findViewById(R.id.comment_list);
        comments = new LinkedList<>();
        adapter = new CommentAdapter(comments, true);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.addOnScrollListener(new EndlessListLoad(linearLayoutManager, 2) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getParentComments(page+1);              // page starts at 1, not 0
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        // get first page of comments
        getParentComments(1);
        Comments.setSubmitClickListener("none", "none", "none", comments, adapter);
        return view;
    }

    private void getParentComments(int page) {
        if (!loading) {
            return;
        }
        ServerCall service = retrofit.create(ServerCall.class);
        Call<CommentResponse> parentCommentCall = service.getParentCommentsByBangumiIdWithPage(bangumiId, page);
        parentCommentCall.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful()) {
                    List<Comment> commentList = response.body().getData().getComments();
                    if (commentList.size() == 0) {
                        loading = false;
                    } else {
                        for (Comment comment : commentList) {
                            comments.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }
}
package com.example.aniscoreandroid.detailView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.model.comment.CommentResponse;
import com.example.aniscoreandroid.utils.ServerCall;
import com.example.aniscoreandroid.utils.TextChangeListener;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Comments extends Fragment {
    private static Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private View view;
    private static String bangumId;
    private static EditText commentBox;
    private static TextView submit;
    private static TextChangeListener commentListener;
    private static String currentUsername;
    private static String avatar;
    private static FragmentTransaction ft;                          // fragment transaction for holding fragment
    private static Bundle args;                                     // args holding the bangumiId

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.comment_view, container, false);
        args = getArguments();
        bangumId = args.getString("bangumiId");
        // set text listener for comment boc
        commentBox = view.findViewById(R.id.comment_box);
        commentListener = new TextChangeListener();
        commentBox.addTextChangedListener(commentListener);
        // set click listener for user submit own comment
        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment("none", "none", "none");
            }
        });
        // get current username
        SharedPreferences preferences = getContext().getSharedPreferences("Current user", Context.MODE_PRIVATE);
        currentUsername = preferences.getString("username", null);
        avatar = preferences.getString("avatar", null);
        // set default fragment
        setDefaultPage();
        return view;
    }

    private void setDefaultPage() {
        Fragment fragment = new CommentMain();
        ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.setArguments(args);
        ft.replace(R.id.comment_section, fragment);
        ft.commit();
    }

    private static void refresh() {
        Fragment fragment = null;
        if (fragment.getClass().getSimpleName().equals("CommentMain")) {
            fragment = new CommentMain();
        }
        fragment.setArguments(args);
        ft.detach(fragment);
        ft.attach(fragment);
        ft.commit();
    }

    public static EditText getCommentBox() {
        return commentBox;
    }

    public static void setReplyClickListener(final String parentCommentId, final String repliedCommentId, final String repliedUsername) {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment(parentCommentId, repliedCommentId, repliedUsername);
            }
        });
    }

    /*
     * listener for the comment box
     * @param parentCommentId the parent comment Id of the comment where user reply, "none" if the
     * user create a new parent comment
     * @param repliedCommentId the id of the comment under the parent comment where the user reply
     * @param repliedUsername the username of the user current user replied
     */
    private static void submitComment(String parentCommentId, String repliedCommentId, String repliedUsername) {
        ServerCall service = retrofit.create(ServerCall.class);
        HashMap<String, String> body = new HashMap<>();
        body.put("anime_id",bangumId);
        body.put("parentComment_id", parentCommentId);
        body.put("commentContent", commentListener.getQuery());
        body.put("username", currentUsername);
        body.put("avatar", avatar);
        body.put("repliedComment_id", repliedCommentId);
        body.put("repliedUsername", repliedUsername);
        Call<CommentResponse> submitCall = service.submitComment(body);
        submitCall.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                refresh();
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }
}
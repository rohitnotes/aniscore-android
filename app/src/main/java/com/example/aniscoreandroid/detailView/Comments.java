package com.example.aniscoreandroid.detailView;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.model.comment.Comment;
import com.example.aniscoreandroid.model.singleComment.SingleCommentResponse;
import com.example.aniscoreandroid.utils.ServerCall;
import com.example.aniscoreandroid.utils.TextChangeListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Comments extends Fragment {
    private static Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private static View view;
    private static String bangumId;
    private static EditText commentBox;
    private static TextView submit;
    private static TextChangeListener commentListener;
    private static String currentUsername;
    private static String currentUserId;
    private static String avatar;
    private static FragmentTransaction ft;                          // fragment transaction for holding fragment
    private static Bundle args;                                     // args holding the bangumiId

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.comment_view, container, false);
        args = getArguments();
        bangumId = args.getString("bangumiId");
        // set submit own comment
        submit = view.findViewById(R.id.submit);
        // set text listener for comment boc
        commentBox = view.findViewById(R.id.comment_box);
        commentListener = new TextChangeListener(submit);
        commentBox.addTextChangedListener(commentListener);
        // get current username
        SharedPreferences preferences = getContext().getSharedPreferences("Current user", Context.MODE_PRIVATE);
        currentUsername = preferences.getString("username", null);
        currentUserId = preferences.getString("userId", null);
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

    public static EditText getCommentBox() {
        return commentBox;
    }

    public static void setSubmitClickListener(final String parentCommentId, final String repliedCommentId, final String repliedUsername, final List<Comment> comments, final RecyclerView.Adapter adapter) {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment(parentCommentId, repliedCommentId, repliedUsername, comments, adapter);
            }
        });
    }

    /*
     * listener for the comment box
     * @param parentCommentId the parent comment Id of the comment where user reply, "none" if the
     * user create a new parent comment
     * @param repliedCommentId the id of the comment under the parent comment where the user reply
     * @param repliedUsername the username of the user current user replied
     * @param comments the comment list the submit comment add to
     * @param adapter the adapter
     */
    private static void submitComment(final String parentCommentId, String repliedCommentId, String repliedUsername, final List<Comment> comments, final RecyclerView.Adapter adapter) {
        if (currentUsername == null || currentUserId == null || commentListener.getQuery() == null || commentListener.getQuery().length() == 0) {
            return;
        }
        ServerCall service = retrofit.create(ServerCall.class);
        HashMap<String, String> body = new HashMap<>();
        body.put("anime_id",bangumId);
        body.put("parentComment_id", parentCommentId);
        body.put("commentContent", commentListener.getQuery());
        body.put("user_id",currentUserId);
        body.put("username", currentUsername);
        body.put("avatar", avatar);
        body.put("repliedComment_id", repliedCommentId);
        body.put("repliedUsername", repliedUsername);
        // set date
        Date now = new Date();
        String nowDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH).format(now);
        body.put("date", nowDate);
        // server call
        Call<SingleCommentResponse> submitCall = service.submitComment(body);
        submitCall.enqueue(new Callback<SingleCommentResponse>() {
            @Override
            public void onResponse(Call<SingleCommentResponse> call, Response<SingleCommentResponse> response) {
                // the list returned list has only one comment
                Comment newComment = response.body().getData().getComment();
                if (comments.size() == 0) {
                    comments.add(newComment);
                } else {
                    if (parentCommentId.equals("none")) {           // for parent comment, newer comment will be at top
                        comments.add(0, newComment);
                    } else {                                        // for replies, older comment will be at top
                        comments.add(newComment);
                    }
                }
                adapter.notifyDataSetChanged();
                commentBox.setText("");
                // hide keyboard
                view.requestFocus();
                InputMethodManager manager = (InputMethodManager)view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                if (manager != null) {
                    manager.hideSoftInputFromWindow(commentBox.getWindowToken(), 0);
                }
            }

            @Override
            public void onFailure(Call<SingleCommentResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }
}
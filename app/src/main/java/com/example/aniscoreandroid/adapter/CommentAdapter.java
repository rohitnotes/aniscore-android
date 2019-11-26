package com.example.aniscoreandroid.adapter;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.detailView.Comments;
import com.example.aniscoreandroid.model.comment.Comment;
import com.example.aniscoreandroid.model.comment.CommentResponse;
import com.example.aniscoreandroid.utils.ServerCall;


import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * this is the adapter user for view of all comments of one bangumi and all replies of one comment
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;
    private boolean hasReplies;
    private Context context;
    private final String baseUrl = "http://10.0.2.2:4000/";
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create()).build();
    private SharedPreferences preference;
    private String currentUserId;
    private String defaultHint = "Add some comments";               // hint for comment box
    private View view;

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView avatar;
        TextView username;
        TextView date;
        TextView content;
        ImageView likeIcon;
        TextView likeNum;
        ImageView dislikeIcon;
        TextView dislikeNum;
        RecyclerView replies;           // only parent comment has the replies
        RelativeLayout commentLayout;

        public CommentViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.avatar);
            username = view.findViewById(R.id.username);
            date = view.findViewById(R.id.date);
            content = view.findViewById(R.id.content);
            likeIcon = view.findViewById(R.id.like);
            likeNum = view.findViewById(R.id.like_number);
            dislikeIcon = view.findViewById(R.id.dislike);
            dislikeNum = view.findViewById(R.id.dislike_number);
            replies = view.findViewById(R.id.replies);
            commentLayout = view.findViewById(R.id.comment_layout);
        }
    }

    public CommentAdapter(List<Comment> commentList, boolean hasReply) {
        comments = commentList;
        hasReplies = hasReply;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        context = parent.getContext();
        preference = context.getSharedPreferences("Current user", Context.MODE_PRIVATE);
        if (preference != null) {
            currentUserId = preference.getString("userId", null);
        }
        return new CommentViewHolder(view);
    }

    @Override public void onBindViewHolder(final CommentViewHolder holder, int position) {
        final Comment current = comments.get(position);
        // set rounded avatar
        Glide.with(context).load(baseUrl + current.getAvatar()).asBitmap().centerCrop()
                .override(100, 100).into(new BitmapImageViewTarget(holder.avatar) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.avatar.setImageDrawable(circularBitmapDrawable);
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append(current.getUsername());
        // set username
        if (!(current.getRepliedUsername().equals("none"))) {
            sb.append("@");
            sb.append(current.getRepliedUsername());
        }
        holder.username.setText(sb.toString());
        // set date
        String givenDate = current.getDate();
        int index = givenDate.indexOf('T');
        holder.date.setText(givenDate.substring(0, index));
        // set comment content
        holder.content.setText(current.getCommentContent());
        // set like
        setLike(holder, current);
        // set dislike icon
        setDislike(holder, current);
        // set replies
        setReplies(holder, current);
        // set reply listener for whole layout
        setKeyboard(holder, current);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    /*
     * set like icon, like number and click listener
     */
    private void setLike(final CommentViewHolder holder, final Comment current) {
        // set like icon
        final List<String> likes = current.getLike();
        final List<String> dislikes = current.getDislike();
        if (currentUserId != null && likes != null && likes.contains(currentUserId)) {          // current user has liked the comment
            holder.likeIcon.setImageResource(R.drawable.like_filled);
        } else {
            holder.likeIcon.setImageResource(R.drawable.like_unfilled);
        }
        // set like number
        if (likes != null && likes.size() > 0) {
            holder.likeNum.setText((likes.size() + ""));
        }
        // set click listener for like icon and like number
        final View.OnClickListener likeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserId != null) {                // has a user logged in
                    // change icon and number
                    if (dislikes != null && dislikes.contains(currentUserId)) {
                        // switch from dislike to like
                        // reduce dislike number by 1
                        if (dislikes.size() > 1) {
                            holder.dislikeNum.setText(((dislikes.size() - 1) + ""));
                        } else {
                            holder.dislikeNum.setText("");
                        }
                        // increase like number by 1
                        if (likes == null) {
                            holder.likeNum.setText("1");
                        } else {
                            holder.likeNum.setText(((likes.size() + 1) + ""));
                        }
                        // remove current user id from dislikes and add to likes
                        dislikes.remove(currentUserId);
                        likes.add(currentUserId);
                        // unfill dislike icon
                        holder.dislikeIcon.setImageResource(R.drawable.unlike_unfilled);
                        // fill like icon
                        holder.likeIcon.setImageResource(R.drawable.like_filled);
                    } else if (likes != null && likes.contains(currentUserId)) {
                        // cancel the like of a comment
                        // reduce like number by 1
                        if (likes.size() > 1) {
                            holder.likeNum.setText(((likes.size() - 1) + ""));
                        } else {
                            holder.likeNum.setText("");
                        }
                        // remove current user id from likes
                        likes.remove(currentUserId);
                        // unfill the like icon
                        holder.likeIcon.setImageResource(R.drawable.like_unfilled);
                    } else {
                        // like a comment
                        // increase the like number by 1
                        if (likes == null) {
                            holder.likeNum.setText("1");
                        } else {
                            holder.likeNum.setText(((likes.size() + 1) + ""));
                        }
                        // add current user id to likes
                        likes.add(currentUserId);
                        // fill the like icon
                        holder.likeIcon.setImageResource(R.drawable.like_filled);
                    }
                    // update status in backend
                    updateStatusOfComment(current.getCommentId(), "like");
                }
            }
        };
        // add click listener to like icon and like number
        holder.likeIcon.setOnClickListener(likeListener);
        holder.likeNum.setOnClickListener(likeListener);
    }

    private void setDislike(final CommentViewHolder holder, final Comment current) {
        // set dislike icon
        final List<String> likes = current.getLike();
        final List<String> dislikes = current.getDislike();
        if (currentUserId != null && dislikes != null && dislikes.contains(currentUserId)) {   // current user has disliked the comment
            holder.dislikeIcon.setImageResource(R.drawable.unlike_filled);
        } else {
            holder.dislikeIcon.setImageResource(R.drawable.unlike_unfilled);
        }
        // set dislike number
        if (dislikes != null && dislikes.size() > 0) {
            holder.dislikeNum.setText((dislikes.size()  + ""));
        }
        // set click listener for dislike icon and dislike number
        View.OnClickListener dislikeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserId != null) {            // has a user logged in
                    // change icon and number
                    if (likes != null && likes.contains(currentUserId)) {
                        // switch from like to dislike
                        // reduce like number by one
                        if (likes.size() > 1) {
                            holder.likeNum.setText(((likes.size() - 1) + ""));
                        } else{
                            holder.likeNum.setText("");
                        }
                        // increase the dislike number by one
                        if (dislikes == null) {
                            holder.dislikeNum.setText("1");
                        } else {
                            holder.dislikeNum.setText(((dislikes.size() + 1) + ""));
                        }
                        // remove current user id from likes and add to dislikes
                        likes.remove(currentUserId);
                        dislikes.add(currentUserId);
                        // unfill like icon
                        holder.likeIcon.setImageResource(R.drawable.like_unfilled);
                        // fill dislike icon
                        holder.dislikeIcon.setImageResource(R.drawable.unlike_filled);
                    } else if (dislikes != null && dislikes.contains(currentUserId)) {
                        // cancel dislike of a comment
                        // reduce dislike number by one
                        if (dislikes.size() > 1) {
                            holder.dislikeNum.setText(((dislikes.size() - 1) + ""));
                        } else {
                            holder.dislikeNum.setText("");
                        }
                        // remove current user id from dislikes
                        dislikes.remove(currentUserId);
                        // unfill the dislike icon
                        holder.dislikeIcon.setImageResource(R.drawable.unlike_unfilled);
                    } else {
                        // dislike a comment
                        // increase the dislike number by one
                        if (dislikes == null) {
                            holder.dislikeNum.setText("1");
                        } else {
                            holder.dislikeNum.setText(((dislikes.size() + 1) + ""));
                        }
                        // add current user id to dislikes
                        dislikes.add(currentUserId);
                        // fill the dislike icon
                        holder.dislikeIcon.setImageResource(R.drawable.unlike_filled);
                    }
                    // update status in backend
                    updateStatusOfComment(current.getCommentId(), "dislike");
                }
            }
        };
        // add listener to dislike icon and dislike number
        holder.dislikeIcon.setOnClickListener(dislikeListener);
        holder.dislikeNum.setOnClickListener(dislikeListener);
    }

    /*
     * like or dislike the comment written by user with given userId, must check currentUserId before
     * calling this function
     * @param attitude: the icon user clicks, "like" or "dislike"
     */
    private void updateStatusOfComment(String commentId, String attitude) {
        ServerCall service = retrofit.create(ServerCall.class);
        HashMap<String, String> body = new HashMap<>();
        body.put("attitude", attitude);
        body.put("user_id", currentUserId);
        Call<CommentResponse> updateCall = service.updateStatusOfComment(commentId, body);
        updateCall.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {

            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
    }

    /*
     * set reply section of a comment, reply section only occurs in main comment page
     * this function is only requires backend call when parentComment is the parentComment
     * otherwise, the reply section is set to invisible
     */
    private void setReplies(final CommentViewHolder holder, Comment parentComment) {
        if (hasReplies) {
            ServerCall service = retrofit.create(ServerCall.class);
            Call<CommentResponse> replyCall = service.getRepliesOfComment(parentComment.getCommentId());
            replyCall.enqueue(new Callback<CommentResponse>() {
                @Override
                public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                    if (response.isSuccessful()) {
                        List<Comment> replyList = response.body().getData().getComments();
                        if (replyList == null || replyList.size() == 0) {
                            holder.replies.setVisibility(View.GONE);
                        } else {
                            holder.replies.setVisibility(View.VISIBLE);
                            holder.replies.setAdapter(new ReplyBriefAdapter(replyList));
                            holder.replies.setLayoutManager(new LinearLayoutManager(context));
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

    /*
     * set keyboard listener when clicking a comment
     */
    private void setKeyboard(CommentViewHolder holder, final Comment current) {
        final InputMethodManager manager = (InputMethodManager)view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText commentBox= Comments.getCommentBox();
                String hint = commentBox.getHint().toString();          // get hint of comment box
                // set focus on comment box, letting keyboard appear in one click
                commentBox.requestFocus();
                if (hint.equals(defaultHint)) {
                    String parentCommentId = current.getParentCommentId();
                    if (parentCommentId.equals("none")) {           // the comment replied is the parent comment
                        parentCommentId = current.getCommentId();
                    }
                    commentBox.setHint("Reply " + current.getUsername());
                    Comments.setReplyClickListener(parentCommentId, current.getCommentId(), current.getUsername());
                    // show keyboard
                    if (manager != null) {
                        manager.showSoftInput(commentBox, 0);
                    }
                } else {                         // cancel reply
                    commentBox.setHint(defaultHint);
                    // set focus on comment box, letting keyboard hide in one click
                    commentBox.requestFocus();
                    Comments.setReplyClickListener("none", "none", "none");
                    // hide keyboard
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(commentBox.getWindowToken(), 0);
                    }
                }
            }
        });
    }
}
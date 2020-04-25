package com.example.aniscoreandroid.adapter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.aniscoreandroid.R;
import com.example.aniscoreandroid.activity.UserActivity;
import com.example.aniscoreandroid.detailView.CommentDetail;
import com.example.aniscoreandroid.detailView.Comments;
import com.example.aniscoreandroid.model.comment.Comment;
import com.example.aniscoreandroid.model.comment.CommentResponse;
import com.example.aniscoreandroid.utils.ServerCall;


import java.util.HashMap;
import java.util.LinkedList;
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
    private boolean isInMain;                       // judge whether the fragment is commentMain or commentDetail
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
        CardView cardView;

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
            cardView = view.findViewById(R.id.card);
        }
    }

    public CommentAdapter(List<Comment> commentList, boolean inMain) {
        comments = commentList;
        isInMain = inMain;
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
        // click avatar to go to the user page
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), UserActivity.class);
                intent.putExtra("USER_ID", current.getUserId());
                intent.putExtra("SOURCE", "DETAIL");
                view.getContext().startActivity(intent);
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append(current.getUsername());
        // set username
        if (!isInMain && !(current.getRepliedUsername().equals("none"))
                && !(current.getRepliedCommentId().equals(current.getParentCommentId()))) {
            sb.append("@").append(current.getRepliedUsername());
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
        // set listener for in detail page
        final RecyclerView.Adapter adapter = this;
        // set keyboard listener
        // if current fragment is commentMain, the keyboard listener is set after async call for replies finished, which is in setReplies
        // if current fragment is commentDetail, then the adapter and list is the comment adapter and comment list
        if (!isInMain) {
            setKeyboard(holder, current, adapter, comments);
        }
        // currently user is in commentMain page, reply section exists displaying first 3 replies
        if (isInMain) {
            // click reply section to comment detail fragment
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new CommentDetail();
                    Bundle args = Comments.getArgs();
                    args.putString("commentId", current.getCommentId());
                    fragment.setArguments(args);
                    FragmentTransaction ft = ((AppCompatActivity)view.getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.comment_section, fragment);
                    ft.commit();
                }
            });
        }
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
                setAttitudeListener(holder, current, "like");
            }
        };
        // add click listener to like icon and like number
        holder.likeIcon.setOnClickListener(likeListener);
        holder.likeNum.setOnClickListener(likeListener);
    }

    /*
     * set dislike icon, dislike number and click listener
     */
    private void setDislike(final CommentViewHolder holder, final Comment current) {
        // set dislike icon
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
                setAttitudeListener(holder, current, "dislike");
            }
        };
        // add listener to dislike icon and dislike number
        holder.dislikeIcon.setOnClickListener(dislikeListener);
        holder.dislikeNum.setOnClickListener(dislikeListener);
    }

    /*
     * set click listener for user clicking a like or dislike icon
     * mode can only be like or dislike
     */
    private void setAttitudeListener(final CommentViewHolder holder, final Comment current, String mode) {
        List<String> attitude;                      // attitude user has toward comment, like or dislike
        List<String> oppAttitude;                   // opposite attitude toward user's attitude toward comment
        TextView attitudeNum;
        TextView oppAttitudeNum;
        ImageView attitudeIcon;
        ImageView oppAttitudeIcon;
        int attitudeFilled;
        int attitudeUnfilled;
        int oppAttitudeUnfilled;
        if (mode.equals("like")) {
            attitude = current.getLike();
            oppAttitude = current.getDislike();
            attitudeNum = holder.likeNum;
            oppAttitudeNum = holder.dislikeNum;
            attitudeIcon = holder.likeIcon;
            oppAttitudeIcon = holder.dislikeIcon;
            attitudeFilled = R.drawable.like_filled;
            attitudeUnfilled = R.drawable.like_unfilled;
            oppAttitudeUnfilled = R.drawable.unlike_unfilled;
        } else {
            attitude = current.getDislike();
            oppAttitude = current.getLike();
            attitudeNum = holder.dislikeNum;
            oppAttitudeNum = holder.likeNum;
            attitudeIcon = holder.dislikeIcon;
            oppAttitudeIcon = holder.likeIcon;
            attitudeFilled = R.drawable.unlike_filled;
            attitudeUnfilled = R.drawable.unlike_unfilled;
            oppAttitudeUnfilled = R.drawable.like_unfilled;
        }
        if (currentUserId != null) {                // has a user logged in
            // change icon and number
            if (oppAttitude != null && oppAttitude.contains(currentUserId)) {
                // switch from dislike to like
                // reduce dislike number by 1
                if (oppAttitude.size() > 1) {
                    oppAttitudeNum.setText(((oppAttitude.size() - 1) + ""));
                } else {
                    oppAttitudeNum.setText("");
                }
                // increase the attitude user clicked number by 1
                if (attitude == null) {
                    attitudeNum.setText("1");
                } else {
                    attitudeNum.setText(((attitude.size() + 1) + ""));
                }
                // remove current user id from dislikes and add to likes
                oppAttitude.remove(currentUserId);
                attitude.add(currentUserId);
                // unfill opp attitude icon
                oppAttitudeIcon.setImageResource(oppAttitudeUnfilled);
                // fill attitude icon
                attitudeIcon.setImageResource(attitudeFilled);
            } else if (attitude != null && attitude.contains(currentUserId)) {
                // cancel the like of a comment
                // reduce like number by 1
                if (attitude.size() > 1) {
                    attitudeNum.setText(((attitude.size() - 1) + ""));
                } else {
                    attitudeNum.setText("");
                }
                // remove current user id from attitude
                attitude.remove(currentUserId);
                // unfill the attitude icon
                attitudeIcon.setImageResource(attitudeUnfilled);
            } else {
                // like a comment
                // increase the like number by 1
                if (attitude == null) {
                    attitudeNum.setText("1");
                } else {
                    attitudeNum.setText(((attitude.size() + 1) + ""));
                }
                // add current user id to likes
                attitude.add(currentUserId);
                // fill the like icon
                attitudeIcon.setImageResource(attitudeFilled);
            }
            // update status in backend
            updateStatusOfComment(current.getCommentId(), mode);
        }
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
    private void setReplies(final CommentViewHolder holder, final Comment parentComment) {
        if (isInMain) {
            ServerCall service = retrofit.create(ServerCall.class);
            Call<CommentResponse> replyCall = service.getRepliesOfCommentWithPage(parentComment.getCommentId(), 1);
            replyCall.enqueue(new Callback<CommentResponse>() {
                @Override
                public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                    if (response.isSuccessful()) {
                        List<Comment> replyList = response.body().getData().getComments();
                        if (replyList == null) {
                            replyList = new LinkedList<>();
                        }
                        if (replyList.size() == 0) {
                            holder.cardView.setVisibility(View.GONE);
                        } else {
                            if (replyList.size() > 3) {
                                replyList = replyList.subList(0, 3);
                            }
                            holder.cardView.setVisibility(View.VISIBLE);
                        }
                        ReplyBriefAdapter replyAdapter = new ReplyBriefAdapter(replyList);
                        holder.replies.setAdapter(replyAdapter);
                        holder.replies.setLayoutManager(new LinearLayoutManager(context));
                        // set reply listener for whole layout except reply section
                        setKeyboard(holder, parentComment, replyAdapter, replyList);
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
     * set keyboard listener when clicking a comment to reply
     * @param holder the view holder
     * @param current the comment user clicks
     * @param adapter the adapter of the recycler view to notify data change,
     * allowing new comment to show on screen immediately after user click submit
     * @param commentList the comment list where the new comment is added to
     */
    private void setKeyboard(final CommentViewHolder holder, final Comment current, final RecyclerView.Adapter adapter, final List<Comment> commentList) {
        final InputMethodManager manager = (InputMethodManager)view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reply section only exists in comment main
                if (isInMain) {
                    holder.cardView.setVisibility(View.VISIBLE);
                }
                EditText commentBox= Comments.getCommentBox();
                String hint = commentBox.getHint().toString();                          // get hint of comment box
                // set focus on comment box, letting keyboard appear in one click
                commentBox.requestFocus();
                if (hint.equals(defaultHint)) {
                    String parentCommentId = current.getParentCommentId();
                    if (parentCommentId.equals("none")) {                               // the comment replied is the parent comment
                        parentCommentId = current.getCommentId();
                    }
                    commentBox.setHint("Reply " + current.getUsername());
                    // set submit reply listener
                    Comments.setSubmitClickListener(parentCommentId, current.getCommentId(), current.getUsername(), commentList, adapter);
                    // show keyboard
                    if (manager != null) {
                        manager.showSoftInput(commentBox, 0);
                    }
                } else {                                                                // cancel reply
                    commentBox.setHint(defaultHint);
                    // shift back to submit default listener
                    if (isInMain) {
                        // in comment main page, submit default listener is to add a new parent comment
                        Comments.setSubmitClickListener("none", "none", "none", commentList, adapter);
                    } else {
                        // in comment detail page, submit default listener is to reply parent comment
                        // get first comment in the comments, for comment detail page, comments is passed to the function as commentList
                        Comment parentComment = commentList.get(0);
                        Comments.setSubmitClickListener(parentComment.getCommentId(), parentComment.getCommentId(),
                                parentComment.getUsername(), commentList, adapter);
                    }
                    // hide keyboard
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(commentBox.getWindowToken(), 0);
                    }
                }
            }
        });
    }
}
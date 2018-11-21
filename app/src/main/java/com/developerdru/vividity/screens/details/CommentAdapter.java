package com.developerdru.vividity.screens.details;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.developerdru.vividity.R;
import com.developerdru.vividity.data.entities.PhotoComment;
import com.developerdru.vividity.utils.GlideApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentVH> {

    private static final String DATE_FORMAT = "MMM-dd, yyyy";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT,
            Locale.US);

    private List<PhotoComment> commentList;

    private Listener listener;
    private String myId;

    CommentAdapter(@NonNull String myId, @NonNull Listener listener) {
        this.myId = myId;
        this.listener = listener;
        this.commentList = new ArrayList<>();
    }

    void addComments(List<PhotoComment> comments) {
        this.commentList.clear();
        this.commentList.addAll(comments);
        notifyDataSetChanged();
    }

    public void appendComments(List<PhotoComment> comments) {
        this.commentList.addAll(comments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View contentView = inflater.inflate(R.layout.item_photo_comment, viewGroup, false);
        return new CommentVH(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentVH commentVH, int index) {
        commentVH.bind(index);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentVH extends RecyclerView.ViewHolder {
        ImageView imgCommenter, imgDelete;
        TextView tvCommenterName, tvCommentText, tvCommentDate;

        CommentVH(@NonNull View itemView) {
            super(itemView);
            imgCommenter = itemView.findViewById(R.id.imgCommenter);
            imgDelete = itemView.findViewById(R.id.imgCommentDelete);
            tvCommenterName = itemView.findViewById(R.id.tvCommenterName);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvCommentDate = itemView.findViewById(R.id.tvCommentDate);
        }

        void bind(int index) {
            PhotoComment comment = commentList.get(index);
            GlideApp.with(itemView.getContext())
                    .load(comment.getCommenterPic())
                    .placeholder(R.drawable.ic_logo_baby)
                    .error(R.drawable.ic_baby_mono)
                    .into(imgCommenter);

            imgCommenter.setOnClickListener(v -> listener.onCommenterImageTapped(comment,
                    imgCommenter));

            // Show delete icon only for current user's comments
            imgDelete.setVisibility(myId.equalsIgnoreCase(comment.getCommenterId()) ? View
                    .VISIBLE : View.GONE);
            imgDelete.setOnClickListener(v -> listener.onDeleteTapped(comment));

            tvCommenterName.setText(comment.getCommenterName());
            tvCommentDate.setText(dateFormatter.format(comment.getTimestamp()));
            tvCommentText.setText(comment.getText());
        }
    }

    interface Listener {
        void onDeleteTapped(@NonNull PhotoComment comment);

        void onCommenterImageTapped(@NonNull PhotoComment comment, View sharedElement);
    }
}

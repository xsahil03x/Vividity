package com.developerdru.vividity.screens.home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoVH> {

    PhotoAdapter () {

    }

    @NonNull
    @Override
    public PhotoVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoVH photoVH, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class PhotoVH extends RecyclerView.ViewHolder {

        ImageView imgMain, imgUploader;
        TextView tvCaption, tvUploadDate, tvUpVoteCount, tvCommentCount, tvUploaderName;

        public PhotoVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}

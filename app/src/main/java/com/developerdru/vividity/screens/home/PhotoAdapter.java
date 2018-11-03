package com.developerdru.vividity.screens.home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.developerdru.vividity.R;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.utils.GlideApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoVH> {

    private List<Photo> photos;

    private static final String FORMAT_DATE = "MMM-dd, yyyy";
    private SimpleDateFormat dateFormat;

    PhotoAdapter() {
        photos = new ArrayList<>();
        dateFormat = new SimpleDateFormat(FORMAT_DATE, Locale.getDefault());
    }

    void appendPhotos(Collection<Photo> newPhotos) {
        photos.addAll(newPhotos);
        notifyDataSetChanged();
    }

    void resetPhotos(Collection<Photo> newPhotos) {
        photos.clear();
        photos.removeAll(newPhotos);
        photos.addAll(newPhotos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View photoView = inflater.inflate(R.layout.item_photo, viewGroup, false);
        return new PhotoVH(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoVH photoVH, int i) {
        photoVH.bind(i);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoVH extends RecyclerView.ViewHolder {

        ImageView imgMain, imgUploader;
        TextView tvCaption, tvUploadDate, tvUpVoteCount, tvCommentCount, tvUploaderName;

        PhotoVH(@NonNull View itemView) {
            super(itemView);
            imgMain = itemView.findViewById(R.id.img_photo_main);
            imgUploader = itemView.findViewById(R.id.imgUploader);
            tvCaption = itemView.findViewById(R.id.tvPhotoCaption);
            tvUploadDate = itemView.findViewById(R.id.tvUploadDate);
            tvUpVoteCount = itemView.findViewById(R.id.tvUpvoteCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            tvUploaderName = itemView.findViewById(R.id.tvUploaderName);
        }

        void bind(int position) {
            Photo photo = photos.get(position);
            GlideApp.with(itemView.getContext()).load(photo.getUploaderPic()).into(imgUploader);
            GlideApp.with(itemView.getContext()).load(photo.getDownloadURL()).into(imgMain);
            tvCaption.setText(photo.getCaption());
            tvUploaderName.setText(photo.getUploader());
            tvCommentCount.setText(String.valueOf(photo.getCommentsCount()));
            tvUpVoteCount.setText(String.valueOf(photo.getUpvoteCount()));
            tvUploadDate.setText(dateFormat.format(photo.getTimestamp()));
        }
    }
}

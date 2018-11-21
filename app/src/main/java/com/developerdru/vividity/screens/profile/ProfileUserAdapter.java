package com.developerdru.vividity.screens.profile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.developerdru.vividity.R;
import com.developerdru.vividity.data.entities.FollowUser;
import com.developerdru.vividity.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

class ProfileUserAdapter extends RecyclerView.Adapter<ProfileUserAdapter.FollowUserVH> {

    private List<FollowUser> users;
    private boolean isMyProfile;
    private InteractionListener listener;

    ProfileUserAdapter(boolean isMyProfile, @NonNull InteractionListener listener) {
        users = new ArrayList<>();
        this.isMyProfile = isMyProfile;
        this.listener = listener;
    }

    void reset(List<FollowUser> newUsers) {
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    void unfollowUser(FollowUser user) {
        users.remove(user);
        notifyDataSetChanged();
    }

    void append(List<FollowUser> newUsers) {
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FollowUserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_follow_user, parent, false);
        return new FollowUserVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowUserVH followUserVH, int position) {
        followUserVH.bind(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class FollowUserVH extends RecyclerView.ViewHolder {

        ImageView imgUser, imgDelete;
        TextView name;
        Switch notification;

        FollowUserVH(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgMyFollow);
            imgDelete = itemView.findViewById(R.id.imgFollowDelete);
            name = itemView.findViewById(R.id.tvMyFollow);
            notification = itemView.findViewById(R.id.switchFollowNotification);
        }

        void bind(int position) {
            FollowUser user = users.get(position);
            GlideApp.with(itemView.getContext()).load(user.getProfilePicURL())
                    .placeholder(R.drawable.vividity_logo)
                    .error(R.drawable.ic_baby_mono)
                    .into(imgUser);
            name.setText(user.getDisplayName());
            notification.setChecked(user.isNotification());

            imgDelete.setVisibility(isMyProfile ? View.VISIBLE : View.GONE);
            imgDelete.setOnClickListener(v -> listener.onDeleteTapped(user));
            imgUser.setOnClickListener(v -> listener.onProfilePicTapped(user, imgUser));

            notification.setVisibility(isMyProfile ? View.VISIBLE : View.GONE);
            notification.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onNotificationStatusChange(user, isChecked);
            });
        }
    }

    interface InteractionListener {
        void onNotificationStatusChange(FollowUser followUser, boolean getNotified);

        void onDeleteTapped(FollowUser followUser);

        void onProfilePicTapped(FollowUser followUser, View sharedElement);
    }

}

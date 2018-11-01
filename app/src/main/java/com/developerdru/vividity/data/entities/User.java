package com.developerdru.vividity.data.entities;

import java.util.Map;

public class User {

    private String userId;
    private boolean uploadPermission = true;
    private String profilePicURL;
    private String signInService; // google or twitter
    private String signInServiceIdentifier;
    private String displayName;
    private String fcmToken;
    private Map<String, FollowUser> followers;
    private Map<String, FollowUser> follows;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isUploadPermission() {
        return uploadPermission;
    }

    public void setUploadPermission(boolean uploadPermission) {
        this.uploadPermission = uploadPermission;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getSignInService() {
        return signInService;
    }

    public void setSignInService(String signInService) {
        this.signInService = signInService;
    }

    public String getSignInServiceIdentifier() {
        return signInServiceIdentifier;
    }

    public void setSignInServiceIdentifier(String signInServiceIdentifier) {
        this.signInServiceIdentifier = signInServiceIdentifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Map<String, FollowUser> getFollowers() {
        return followers;
    }

    public void setFollowers(Map<String, FollowUser> followers) {
        this.followers = followers;
    }

    public Map<String, FollowUser> getFollows() {
        return follows;
    }

    public void setFollows(Map<String, FollowUser> follows) {
        this.follows = follows;
    }
}

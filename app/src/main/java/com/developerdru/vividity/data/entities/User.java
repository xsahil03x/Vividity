package com.developerdru.vividity.data.entities;

public class User {

    private String userId;
    private boolean uploadPermission = true;
    private String profilePicURL;
    private String profilePicName;
    private String signInService; // google or twitter
    private String signInServiceIdentifier;
    private String signInServiceDisplayName;
    private String displayName;
    private String fcmToken;

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

    public String getProfilePicName() {
        return profilePicName;
    }

    public void setProfilePicName(String profilePicName) {
        this.profilePicName = profilePicName;
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

    public String getSignInServiceDisplayName() {
        return signInServiceDisplayName;
    }

    public void setSignInServiceDisplayName(String signInServiceDisplayName) {
        this.signInServiceDisplayName = signInServiceDisplayName;
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
}

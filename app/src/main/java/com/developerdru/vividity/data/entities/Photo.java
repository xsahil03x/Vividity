package com.developerdru.vividity.data.entities;

import java.util.Objects;

public class Photo {

    private String picIdentifier;
    private String picName;
    private String caption;
    private String uploader;
    private String uploaderId;
    private long commentsCount;
    private long upvoteCount;
    private String downloadURL;
    private long timestamp;
    private String uploaderPic;

    public String getPicIdentifier() {
        return picIdentifier;
    }

    public void setPicIdentifier(String picIdentifier) {
        this.picIdentifier = picIdentifier;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public long getUpvoteCount() {
        return upvoteCount;
    }

    public void setUpvoteCount(long upvoteCount) {
        this.upvoteCount = upvoteCount;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUploaderPic() {
        return uploaderPic;
    }

    public void setUploaderPic(String uploaderPic) {
        this.uploaderPic = uploaderPic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(picIdentifier, photo.picIdentifier) &&
                Objects.equals(downloadURL, photo.downloadURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(picIdentifier, downloadURL);
    }
}

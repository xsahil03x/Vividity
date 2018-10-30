package com.developerdru.vividity.data.entities;

public class PhotoComment {

    private String commentIdentifier;
    private String artifactId;
    private String commenterId;
    private String commenterName;
    private String commenterPic;
    private String text;
    private long timestamp;

    public String getCommentIdentifier() {
        return commentIdentifier;
    }

    public void setCommentIdentifier(String commentIdentifier) {
        this.commentIdentifier = commentIdentifier;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getCommenterPic() {
        return commenterPic;
    }

    public void setCommenterPic(String commenterPic) {
        this.commenterPic = commenterPic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

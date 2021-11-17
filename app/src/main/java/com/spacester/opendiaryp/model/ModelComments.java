package com.spacester.opendiaryp.model;

@SuppressWarnings({"ALL", "unused"})
public class ModelComments {
    String cId, comment,dp,id,mane,timestamp,pLikes,pId,type;

    public ModelComments() {
    }

    public ModelComments(String cId, String comment, String dp, String id, String mane, String timestamp, String pLikes, String pId, String type) {
        this.cId = cId;
        this.comment = comment;
        this.dp = dp;
        this.id = id;
        this.mane = mane;
        this.timestamp = timestamp;
        this.pLikes = pLikes;
        this.pId = pId;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMane() {
        return mane;
    }

    public void setMane(String mane) {
        this.mane = mane;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }
}

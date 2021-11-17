package com.spacester.opendiaryp.model;

@SuppressWarnings({"ALL", "unused"})
public class ModelPost {
    String dp,id,meme,name,pLikes,pId,pTime,text,vine,type,pComments,pViews;

    public ModelPost() {
    }

    public ModelPost(String dp, String id, String meme, String name, String pLikes, String pId, String pTime, String text, String vine, String type, String pComments, String pViews) {
        this.dp = dp;
        this.id = id;
        this.meme = meme;
        this.name = name;
        this.pLikes = pLikes;
        this.pId = pId;
        this.pTime = pTime;
        this.text = text;
        this.vine = vine;
        this.type = type;
        this.pComments = pComments;
        this.pViews = pViews;
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

    public String getMeme() {
        return meme;
    }

    public void setMeme(String meme) {
        this.meme = meme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVine() {
        return vine;
    }

    public void setVine(String vine) {
        this.vine = vine;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

    public String getpViews() {
        return pViews;
    }

    public void setpViews(String pViews) {
        this.pViews = pViews;
    }
}

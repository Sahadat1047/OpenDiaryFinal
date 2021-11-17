package com.spacester.opendiaryp.model;

@SuppressWarnings({"ALL", "unused"})
public class ModelChat  {

    private String sender;
    private String receiver;
    private String msg;
    private String type;
    private boolean isSeen;

    public ModelChat(String sender, String receiver, String msg, String type, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.type = type;
        this.isSeen = isSeen;
    }

    public ModelChat() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }
}

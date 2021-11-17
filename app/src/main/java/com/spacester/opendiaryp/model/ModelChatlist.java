package com.spacester.opendiaryp.model;

@SuppressWarnings({"ALL", "unused"})
public class ModelChatlist {
    String id;

    public ModelChatlist(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public ModelChatlist() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

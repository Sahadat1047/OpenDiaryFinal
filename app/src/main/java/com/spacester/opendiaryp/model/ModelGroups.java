package com.spacester.opendiaryp.model;

@SuppressWarnings({"ALL", "unused"})
public class ModelGroups {
    String groupId,gName,gUsername,gBio,gLink,gIcon,timestamp,createdBy;

    public ModelGroups() {
    }

    public ModelGroups(String groupId, String gName, String gUsername, String gBio, String gLink, String gIcon, String timestamp, String createdBy) {
        this.groupId = groupId;
        this.gName = gName;
        this.gUsername = gUsername;
        this.gBio = gBio;
        this.gLink = gLink;
        this.gIcon = gIcon;
        this.timestamp = timestamp;
        this.createdBy = createdBy;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getgUsername() {
        return gUsername;
    }

    public void setgUsername(String gUsername) {
        this.gUsername = gUsername;
    }

    public String getgBio() {
        return gBio;
    }

    public void setgBio(String gBio) {
        this.gBio = gBio;
    }

    public String getgLink() {
        return gLink;
    }

    public void setgLink(String gLink) {
        this.gLink = gLink;
    }

    public String getgIcon() {
        return gIcon;
    }

    public void setgIcon(String gIcon) {
        this.gIcon = gIcon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}

package com.spacester.opendiaryp.model;

@SuppressWarnings({"ALL", "unused"})
public class ModelUser {
    String name, email, username, bio, location, link, photo, phone, id,status,typingTo;
    boolean isBlocked = false;
    public ModelUser() {
    }

    public ModelUser(String name, String email, String username, String bio, String location, String link, String photo, String phone, String id, String status, String typingTo, boolean isBlocked) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.location = location;
        this.link = link;
        this.photo = photo;
        this.phone = phone;
        this.id = id;
        this.status = status;
        this.typingTo = typingTo;
        this.isBlocked = isBlocked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}

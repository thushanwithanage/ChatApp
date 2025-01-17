package com.thushan.app2.Model;

public class Users
{
    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String lastseen;

    public Users()
    {
    }

    public Users(String id, String username, String imageURL, String status, String lastseen)
    {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.lastseen = lastseen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getLastseen() { return lastseen; }

    public void setLastseen(String lastseen) { this.lastseen = lastseen; }
}

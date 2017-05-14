package com.example.aleksey.githubdemo.data.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aleksey on 11.05.17.
 */

public class SearchRecord {

    @Expose
    private String login;

    @Expose
    private long id;

    @SerializedName("avatar_url")
    @Expose
    private String avatarUrl;

    @Expose
    private String url;

    @Expose
    private String type;

    public String getLogin() {
        return login;
    }

    public long getId() {
        return id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }
}

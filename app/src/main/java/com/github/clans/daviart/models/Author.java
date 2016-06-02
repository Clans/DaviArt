
package com.github.clans.daviart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Author {

    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("usericon")
    @Expose
    private String usericon;
    @SerializedName("type")
    @Expose
    private String type;

    public String getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }

    public String getUsericon() {
        return usericon;
    }

    public String getType() {
        return type;
    }
}

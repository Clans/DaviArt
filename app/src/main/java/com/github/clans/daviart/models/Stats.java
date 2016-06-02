
package com.github.clans.daviart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("comments")
    @Expose
    private int comments;
    @SerializedName("favourites")
    @Expose
    private int favourites;

    public int getComments() {
        return comments;
    }

    public int getFavourites() {
        return favourites;
    }
}

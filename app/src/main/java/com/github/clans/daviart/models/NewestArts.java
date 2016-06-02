
package com.github.clans.daviart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NewestArts {

    @SerializedName("has_more")
    @Expose
    private boolean hasMore;
    @SerializedName("next_offset")
    @Expose
    private int nextOffset;
    @SerializedName("results")
    @Expose
    private List<Art> arts = new ArrayList<Art>();

    public boolean hasMore() {
        return hasMore;
    }

    public int getNextOffset() {
        return nextOffset;
    }

    public List<Art> getArts() {
        return arts;
    }
}

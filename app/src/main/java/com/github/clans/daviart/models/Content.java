
package com.github.clans.daviart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("src")
    @Expose
    private String src;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("transparency")
    @Expose
    private boolean transparency;
    @SerializedName("filesize")
    @Expose
    private int filesize;

    public String getSrc() {
        return src;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean isTransparency() {
        return transparency;
    }

    public int getFilesize() {
        return filesize;
    }
}

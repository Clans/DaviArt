
package com.github.clans.daviart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Preview {

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
}

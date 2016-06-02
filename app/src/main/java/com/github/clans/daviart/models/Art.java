
package com.github.clans.daviart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Art {

    @SerializedName("deviationid")
    @Expose
    private String deviationid;
    @SerializedName("printid")
    @Expose
    private String printid;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("category_path")
    @Expose
    private String categoryPath;
    @SerializedName("is_favourited")
    @Expose
    private boolean isFavourited;
    @SerializedName("is_deleted")
    @Expose
    private boolean isDeleted;
    @SerializedName("author")
    @Expose
    private Author author;
    @SerializedName("stats")
    @Expose
    private Stats stats;
    @SerializedName("published_time")
    @Expose
    private int publishedTime;
    @SerializedName("allows_comments")
    @Expose
    private boolean allowsComments;
    @SerializedName("content")
    @Expose
    private Content content;
    @SerializedName("thumbs")
    @Expose
    private List<Thumb> thumbs = new ArrayList<Thumb>();
    @SerializedName("is_mature")
    @Expose
    private boolean isMature;
    @SerializedName("is_downloadable")
    @Expose
    private boolean isDownloadable;
    @SerializedName("preview")
    @Expose
    private Preview preview;
    @SerializedName("download_filesize")
    @Expose
    private int downloadFilesize;

    public String getDeviationid() {
        return deviationid;
    }

    public String getPrintid() {
        return printid;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public boolean isFavourited() {
        return isFavourited;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Author getAuthor() {
        return author;
    }

    public Stats getStats() {
        return stats;
    }

    public int getPublishedTime() {
        return publishedTime;
    }

    public boolean isAllowsComments() {
        return allowsComments;
    }

    public Content getContent() {
        return content;
    }

    public List<Thumb> getThumbs() {
        return thumbs;
    }

    public boolean isMature() {
        return isMature;
    }

    public boolean isDownloadable() {
        return isDownloadable;
    }

    public Preview getPreview() {
        return preview;
    }

    public int getDownloadFilesize() {
        return downloadFilesize;
    }
}

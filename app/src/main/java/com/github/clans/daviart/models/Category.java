
package com.github.clans.daviart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {

    @SerializedName("catpath")
    @Expose
    private String catpath;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("has_subcategory")
    @Expose
    private boolean hasSubcategory;
    @SerializedName("parent_catpath")
    @Expose
    private String parentCatpath;
    private boolean expanded;
    private List<Category> subCategories;
    private int indention;

    /**
     * @return The catpath
     */
    public String getCatpath() {
        return catpath;
    }

    /**
     * @param catpath The catpath
     */
    public void setCatpath(String catpath) {
        this.catpath = catpath;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The hasSubcategory
     */
    public boolean hasSubcategory() {
        return hasSubcategory;
    }

    /**
     * @param hasSubcategory The has_subcategory
     */
    public void setHasSubcategory(boolean hasSubcategory) {
        this.hasSubcategory = hasSubcategory;
    }

    /**
     * @return The parentCatpath
     */
    public String getParentCatpath() {
        return parentCatpath;
    }

    /**
     * @param parentCatpath The parent_catpath
     */
    public void setParentCatpath(String parentCatpath) {
        this.parentCatpath = parentCatpath;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public int getIndention() {
        return indention;
    }

    public void setIndention(int indention) {
        this.indention = indention;
    }
}

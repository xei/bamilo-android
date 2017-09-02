package com.mobile.utils.search;

/**
 * Created by shahrooz on 1/17/17.
 */

public class SearchModel {
    private int icon;
    private String title;

    private boolean isGroupHeader = false;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }




    public boolean isGroupHeader() {
        return isGroupHeader;
    }

    public void setGroupHeader(boolean groupHeader) {
        isGroupHeader = groupHeader;
    }

    public SearchModel(String title) {
        this(-1,title);
        isGroupHeader = true;
    }
    public SearchModel(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;

    }
}

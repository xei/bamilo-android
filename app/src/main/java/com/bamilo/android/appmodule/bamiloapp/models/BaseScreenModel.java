package com.bamilo.android.appmodule.bamiloapp.models;

/**
 * Created by mohsen on 1/9/18.
 */

public class BaseScreenModel {
    public String screenName;
    public String category;
    public String label;
    public long loadBegin;

    public BaseScreenModel(String screenName, String category, String label, long loadBegin) {
        this.screenName = screenName;
        this.category = category;
        this.label = label;
        this.loadBegin = loadBegin;
    }
}

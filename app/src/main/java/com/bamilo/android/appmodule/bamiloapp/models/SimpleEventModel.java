package com.bamilo.android.appmodule.bamiloapp.models;

/**
 * Created by mohsen on 1/14/18.
 */

public class SimpleEventModel extends BaseEventModel {
    public static final long NO_VALUE = -1;
    public String category;
    public String action;
    public String label;
    public long value;

    public SimpleEventModel() {
    }

    public SimpleEventModel(String category, String action, String label, long value) {
        this.category = category;
        this.action = action;
        this.label = label;
        this.value = value;
    }
}

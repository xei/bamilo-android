package com.mobile.factories;

import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map.Entry;

public class ViewGroupFactory {
    // TODO validate if it's better wih SparceArray
    private HashMap<Integer, View> views;

    public ViewGroupFactory(ViewGroup viewGroup) {
        views = new HashMap<>();
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                views.put(child.getId(), child);
            }
        }
    }

    public ViewGroupFactory(View... args) {
        views = new HashMap<>();
        for (View child : args) {
            if (child != null) {
                views.put(child.getId(), child);
            }
        }
    }

    public View setViewVisible(View view){
        if(view != null){
            return setViewVisible(view.getId());
        }
        return null;
    }

    public View setViewVisible(int id) {
        View child = null;
        for (Entry<Integer, View> entry : views.entrySet()) {
            int key = entry.getKey();
            View value = entry.getValue();
            if (key == id) {
                value.setVisibility(View.VISIBLE);
                child = value;
            } else {
                value.setVisibility(View.GONE);
            }
        }
        return child;
    }

    public View getVisibleView() {
        for (Entry<Integer, View> entry : views.entrySet()) {
            View value = entry.getValue();
            if (value.getVisibility() == View.VISIBLE) {
                return value;
            }
        }
        return null;
    }

    public void hideAllViews(){
        for (Entry<Integer, View> entry : views.entrySet()) {
            View value = entry.getValue();
            value.setVisibility(View.GONE);
        }
    }

}

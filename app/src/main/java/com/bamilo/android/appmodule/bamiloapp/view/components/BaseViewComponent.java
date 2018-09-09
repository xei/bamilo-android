package com.bamilo.android.appmodule.bamiloapp.view.components;

import android.content.Context;
import android.view.View;

import com.bamilo.android.framework.service.objects.home.model.BaseComponent;
import com.bamilo.android.framework.service.pojo.RestConstants;

public abstract class BaseViewComponent<T> {

    String mPage;
    int mInstanceIndex;

    public abstract View getView(Context context);

    public abstract void setContent(T content);

    public void enableTracking(String page, int instanceIndex) {
        this.mPage = page;
        this.mInstanceIndex = instanceIndex;
    }

    public enum ComponentType {
        Slider(RestConstants.MAIN_TEASER),
        Tile(RestConstants.TILE_TEASER),
        DailyDeal(RestConstants.DAILY_DEALS),
        Carousel(RestConstants.FEATURED_STORES);
        private final String name;

        ComponentType(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }
}

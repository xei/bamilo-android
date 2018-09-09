package com.bamilo.android.core.service.model.data.home;

import com.bamilo.android.core.service.model.JsonConstants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 12/19/2017.
 */

public abstract class BaseComponent {
    public static final Map<String, Class> componentsMap;
    static {
        componentsMap = new HashMap<>();
        componentsMap.put(JsonConstants.VALUE_TYPE_SLIDER, SliderComponent.class);
        componentsMap.put(JsonConstants.VALUE_TYPE_CAROUSEL, CarouselComponent.class);
        componentsMap.put(JsonConstants.VALUE_TYPE_DEAL_BOX, DealComponent.class);
        componentsMap.put(JsonConstants.VALUE_TYPE_TILE, TileComponent.class);
    }
    @SerializedName(JsonConstants.RestConstants.TYPE)
    @Expose
    private String type;

    protected int teaserId = 0;

    @SerializedName(JsonConstants.RestConstants.HAS_DATA)
    @Expose
    private boolean hasData;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public int getTeaserId() {
        return teaserId;
    }
    public void setTeaserId(int teaserId) {
        this.teaserId = teaserId;
    }
}

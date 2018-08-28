package com.bamilo.android.framework.service.objects.home.model;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 10/21/2017.
 */

public abstract class BaseComponent implements IJSONSerializable {
    private static Map<ComponentType, Class> componentsMap;

    static {
        componentsMap = new HashMap<>();
        componentsMap.put(ComponentType.Slider, SliderComponent.class);
        componentsMap.put(ComponentType.Carousel, CarouselComponent.class);
        componentsMap.put(ComponentType.DailyDeal, DailyDealComponent.class);
        componentsMap.put(ComponentType.Tile, TileComponent.class);
    }

    private String type;
    private boolean hasData;


    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        type = jsonObject.optString(RestConstants.TYPE);
        hasData = jsonObject.optBoolean(RestConstants.HAS_DATA, true);
        return false;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.COMPLETE_JSON;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean hasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public static BaseComponent createFromJson(JSONObject jsonObject) throws JSONException {
        BaseComponent result = null;
        String type = jsonObject.optString(RestConstants.TYPE);
        ComponentType componentType = ComponentType.fromString(type);
        Class clazz = componentsMap.get(componentType);
        if (clazz != null) {
            try {
                BaseComponent component = (BaseComponent) Class.forName(clazz.getName()).newInstance();
                component.initialize(jsonObject);
                result = component;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
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

        public static ComponentType fromString(String text) {
            for (ComponentType b : ComponentType.values()) {
                if (b.name.equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }
}

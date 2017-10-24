package com.mobile.view.components;

import android.content.Context;
import android.view.View;

import com.mobile.service.objects.home.model.BaseComponent;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseViewComponent<T> {
    public static Map<BaseComponent.ComponentType, Class> componentTypesMap;

    static {
        componentTypesMap = new HashMap<>();
        componentTypesMap.put(BaseComponent.ComponentType.Slider, SliderViewComponent.class);
        componentTypesMap.put(BaseComponent.ComponentType.Tile, TileViewComponent.class);
        componentTypesMap.put(BaseComponent.ComponentType.DailyDeal, DailyDealViewComponent.class);
        componentTypesMap.put(BaseComponent.ComponentType.Carousel, CategoriesCarouselViewComponent.class);
    }

    protected String mPage;
    protected int mInstanceIndex;

    public abstract View getView(Context context);

    public abstract void setContent(T content);

    public abstract void setComponent(BaseComponent component);

    public void enableTracking(String page, int instanceIndex) {
        this.mPage = page;
        this.mInstanceIndex = instanceIndex;
    }

    public static BaseViewComponent createFromBaseComponent(BaseComponent baseComponent) {
        BaseComponent.ComponentType componentType = BaseComponent.ComponentType.fromString(baseComponent.getType());
        Class clazz = componentTypesMap.get(componentType);
        if (clazz != null) {
            try {
                BaseViewComponent baseViewComponent = (BaseViewComponent) Class.forName(clazz.getName()).newInstance();
                baseViewComponent.setComponent(baseComponent);
                return baseViewComponent;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

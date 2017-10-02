package com.mobile.view.components;

import android.content.Context;
import android.view.View;

import com.mobile.components.infiniteviewpager.InfiniteCirclePageIndicator;
import com.mobile.components.viewpager.PreviewViewPager;
import com.mobile.view.R;

import java.util.List;

public class SliderComponent implements BaseComponent<List<SliderComponent.Item>> {
    private List<Item> items;

    @Override
    public View getView(Context context) {
        View rootView = View.inflate(context, R.layout.component_slider, null);

        PreviewViewPager vpSlider = (PreviewViewPager) rootView.findViewById(R.id.vpSlider);
        InfiniteCirclePageIndicator indicatorSlider = (InfiniteCirclePageIndicator) rootView.findViewById(R.id.indicatorSlider);

        return rootView;
    }

    @Override
    public void setContent(List<Item> content) {
        this.items = content;
    }

    public static class Item {
        public String imageUrl;
        public String targetLink;
    }
}

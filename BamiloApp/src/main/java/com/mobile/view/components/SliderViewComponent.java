package com.mobile.view.components;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mobile.components.infiniteviewpager.InfiniteCirclePageIndicator;
import com.mobile.components.infiniteviewpager.InfinitePagerAdapter;
import com.mobile.components.viewpager.PreviewViewPager;
import com.mobile.service.objects.home.model.BaseComponent;
import com.mobile.service.objects.home.model.SliderComponent;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SliderViewComponent extends BaseViewComponent<List<SliderViewComponent.Item>> {
    private static final float SLIDE_WIDTH_RATIO = 2.1375F;
    private List<Item> items;
    private OnSlideClickListener onSlideClickListener;

    public SliderViewComponent() {
    }

    @Override
    public View getView(Context context) {
        View rootView = View.inflate(context, R.layout.component_slider, null);

        final PreviewViewPager vpSlider = (PreviewViewPager) rootView.findViewById(R.id.vpSlider);
        final InfiniteCirclePageIndicator indicatorSlider = (InfiniteCirclePageIndicator) rootView.findViewById(R.id.indicatorSlider);

        // reversing the list to make the user feel RTL
        Collections.reverse(items);

        final SliderAdapter adapter = new SliderAdapter(context, items);
        adapter.onSlideClickListener = new OnSlideClickListener() {
            @Override
            public void onSlideClicked(View v, int position, Item item) {
                if (onSlideClickListener != null) {
                    onSlideClickListener.onSlideClicked(v, position, item);
                }
            }
        };
        final SliderInfiniteAdapter infinitePagerAdapter = new SliderInfiniteAdapter(adapter);
        infinitePagerAdapter.enableInfinitePages(adapter.getCount() > 1);
        vpSlider.setAdapter(infinitePagerAdapter);
        vpSlider.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                4, context.getResources().getDisplayMetrics()));

        // to force the view pager behave like RTL ;)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            vpSlider.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        indicatorSlider.setViewPager(vpSlider, items.size() - 1);
        infinitePagerAdapter.setOneItemMode();
        int slideWidth = calculatePageWidth(context, vpSlider);
        // 5 times : 1 for paddingBottom of constraintLayout, 2 for cardView compatPadding, 2 for cardView elevation
        int slideContainerPadding = (int) context.getResources().getDimension(R.dimen.slider_slide_padding) * 5;
        int slideHeight = (int) (slideWidth / SLIDE_WIDTH_RATIO) + slideContainerPadding;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) vpSlider.getLayoutParams();
        params.height = slideHeight;
        vpSlider.setLayoutParams(params);
        return rootView;
    }

    private int calculatePageWidth(Context context, ViewPager vpSlider) {
        int pagerSidePadding = vpSlider.getPaddingRight() + vpSlider.getPaddingRight();
        int pageMargins = vpSlider.getPageMargin() * 2;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        return screenWidth - (pageMargins + pagerSidePadding);
    }

    @Override
    public void setContent(List<Item> content) {
        this.items = content;
    }

    @Override
    public void setComponent(BaseComponent component) {
        if (!(component instanceof SliderComponent)) {
            return;
        }
        List<Item> sliderItems = new ArrayList<>();
        SliderComponent sliderComponent = (SliderComponent) component;

        for (SliderComponent.Slide slide : sliderComponent.getSlides()) {
            Item tempItem = new Item(slide.getPortraitImage(), slide.getTarget());
            sliderItems.add(tempItem);
        }

        setContent(sliderItems);
    }

    public OnSlideClickListener getOnSlideClickListener() {
        return onSlideClickListener;
    }

    public void setOnSlideClickListener(OnSlideClickListener onSlideClickListener) {
        this.onSlideClickListener = onSlideClickListener;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        public Item(String imageUrl, String targetLink) {
            this.imageUrl = imageUrl;
            this.targetLink = targetLink;
        }

        public String imageUrl;
        public String targetLink;
    }

    private static class SliderInfiniteAdapter extends InfinitePagerAdapter {
        SliderAdapter sliderAdapter;

        public SliderInfiniteAdapter(PagerAdapter adapter) {
            super(adapter);
            this.sliderAdapter = (SliderAdapter) adapter;
        }

        @Override
        public float getPageWidth(int position) {
//            return .948F;
            return 1;
        }
    }

    private static class SliderAdapter extends PagerAdapter {
        private Context mContext;
        private List<Item> items;
        private OnSlideClickListener onSlideClickListener;

        public SliderAdapter(Context context, List<Item> items) {
            this.mContext = context;
            this.items = items;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View rootView;
            rootView = LayoutInflater.from(mContext).inflate(R.layout.component_slider_slide, container, false);
            ImageView imgSlide = (ImageView) rootView.findViewById(R.id.imgSlide);
            setImageToLoad(items.get(position).imageUrl, imgSlide);
            if (onSlideClickListener != null) {
                imgSlide.setClickable(true);
                imgSlide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSlideClickListener.onSlideClicked(v, position, items.get(position));
                    }
                });
            }
            rootView.setTag(String.valueOf(position));
            container.addView(rootView);
            return rootView;
        }

        private void setImageToLoad(String imageUrl, ImageView imageView) {
            ImageManager.getInstance().loadImage(imageUrl, imageView, null, R.drawable.no_image_slider, false);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            container.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            if (items == null) {
                return 0;
            }
            return items.size();
        }
    }

    public interface OnSlideClickListener {
        void onSlideClicked(View v, int position, Item item);
    }
}

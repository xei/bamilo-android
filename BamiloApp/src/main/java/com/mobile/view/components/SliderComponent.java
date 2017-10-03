package com.mobile.view.components;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.infiniteviewpager.InfiniteCirclePageIndicator;
import com.mobile.components.infiniteviewpager.InfinitePagerAdapter;
import com.mobile.components.viewpager.PreviewViewPager;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.Collections;
import java.util.List;

public class SliderComponent implements BaseComponent<List<SliderComponent.Item>> {
    private List<Item> items;
    private OnSlideClickListener onSlideClickListener;

    public SliderComponent(List<Item> items) {
        this.items = items;
    }

    @Override
    public View getView(Context context) {
        View rootView = View.inflate(context, R.layout.component_slider, null);

        final PreviewViewPager vpSlider = (PreviewViewPager) rootView.findViewById(R.id.vpSlider);
        InfiniteCirclePageIndicator indicatorSlider = (InfiniteCirclePageIndicator) rootView.findViewById(R.id.indicatorSlider);

        // reversing the list to make the user feel RTL
        Collections.reverse(items);

        final SliderAdapter adapter = new SliderAdapter(context, items, items.size() - 1);
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
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int realPosition = infinitePagerAdapter.getVirtualPosition(position);
                adapter.setSelectedPagePosition(realPosition);
                for (int i = 0 ; i < vpSlider.getChildCount() ; i++) {
                    vpSlider.getChildAt(i).animate().alpha(0.6F).start();
                }
                View view = vpSlider.findViewWithTag(String.valueOf(realPosition));
                if (view != null) {
                    view.animate().alpha(1F).start();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        vpSlider.addOnPageChangeListener(onPageChangeListener);
        vpSlider.setAdapter(infinitePagerAdapter);
        vpSlider.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                4, context.getResources().getDisplayMetrics()));

        // to force the view pager behave like RTL ;)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            vpSlider.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        indicatorSlider.setViewPager(vpSlider, items.size() - 1);
        onPageChangeListener.onPageSelected(items.size() - 1);
        return rootView;
    }

    @Override
    public void setContent(List<Item> content) {
        this.items = content;
    }

    public OnSlideClickListener getOnSlideClickListener() {
        return onSlideClickListener;
    }

    public void setOnSlideClickListener(OnSlideClickListener onSlideClickListener) {
        this.onSlideClickListener = onSlideClickListener;
    }

    public void setItems(List<Item> items) {
        this.items = items;
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
        private int selectedPagePosition;

        public SliderAdapter(Context context, List<Item> items, int initialPosition) {
            this.mContext = context;
            this.items = items;
            this.selectedPagePosition = initialPosition;
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
            if (position == selectedPagePosition) {
                rootView.setAlpha(1F);
            } else {
                rootView.setAlpha(.6F);
            }
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

        public int getSelectedPagePosition() {
            return selectedPagePosition;
        }

        public void setSelectedPagePosition(int selectedPagePosition) {
            this.selectedPagePosition = selectedPagePosition;
        }
    }

    public interface OnSlideClickListener {
        void onSlideClicked(View v, int position, Item item);
    }
}

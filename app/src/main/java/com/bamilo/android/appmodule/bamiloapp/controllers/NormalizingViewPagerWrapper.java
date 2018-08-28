package com.bamilo.android.appmodule.bamiloapp.controllers;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bamilo.android.framework.components.viewpagerindicator.IconPagerAdapter;
import com.bamilo.android.framework.service.utils.output.Print;

public class NormalizingViewPagerWrapper extends ViewPager implements IconPagerAdapter {
    private final static String TAG = NormalizingViewPagerWrapper.class.getSimpleName();
    public static final int MAX_REAL_ITEMS = 100;
    private static int OFFSCREEN_PAGE_LIMIT = 1;

    private ViewPager mViewPager;
    private NormalizingPagerAdapter mNormPagerAdapter;

    public NormalizingViewPagerWrapper(Context context) {
        super(context);
    }

    public NormalizingViewPagerWrapper(Context context, ViewPager viewPager, IPagerAdapter adapter,
            OnPageChangeListener pageListener) {
        super(context);
        mViewPager = viewPager;
        mViewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
        if ( mViewPager.getCurrentItem() == 0) {
            int position = calcStartPosition(adapter);
            mViewPager.setCurrentItem(position);
        }
        mNormPagerAdapter = new NormalizingPagerAdapter(adapter);
        setOnPageChangeListener(pageListener);
    }

    private int calcStartPosition(IPagerAdapter adapter) {
        int moveForEven = (adapter.getRealCount() % 2 == 0) ? 2 : 0;
        return adapter.getCount() / 2 + moveForEven;
    }

    @Override
    public PagerAdapter getAdapter() {
        return mNormPagerAdapter;
    }

    @Override
    public boolean isFakeDragging() {
        return mViewPager.isFakeDragging();
    }

    @Override
    public boolean beginFakeDrag() {
        return mViewPager.beginFakeDrag();
    }

    @Override
    public void endFakeDrag() {
        mViewPager.endFakeDrag();
    }

    @Override
    public void fakeDragBy(float xOffset) {
        mViewPager.fakeDragBy(xOffset);
    }

    @Override
    public void setCurrentItem(int item) {
        mViewPager.setCurrentItem(calcNextItem(item));
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        mViewPager.setCurrentItem(calcNextItem(item), smoothScroll);
    }

    private int calcNextItem(int item) {
        // Log.d(TAG, "calcNextItem item = " + item);
        int currentItem = mViewPager.getCurrentItem();
        // Log.d(TAG, "currentItem = " + currentItem);

        int aux = 0;
        if( mNormPagerAdapter != null &&  mNormPagerAdapter.getCount() > 0 ){
            aux = currentItem % mNormPagerAdapter.getCount();
        }
            
        int delta = item - aux;
        int nextItem = currentItem + delta;

        // Log.d(TAG, "calcNextItem: nextItem = " + nextItem);
        return nextItem;
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mViewPager.setOnPageChangeListener(new NormalizingOnPageChangeListener(listener));
    }
    


    @Override
    public int getIconResId(int index) {
        return mNormPagerAdapter.getIconResId( index );
    }

    @Override
    public int getCount() {
        return mNormPagerAdapter.getCount();
    }

    private static class NormalizingPagerAdapter extends PagerAdapter implements IconPagerAdapter {
        private IPagerAdapter mAdapter;

        NormalizingPagerAdapter(IPagerAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public int getCount() {
            return mAdapter.getRealCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return mAdapter.isViewFromObject(view, object);
        }
        
        @Override
        public int getIconResId( int index) {
            if ( mAdapter instanceof IconPagerAdapter )
                return ((IconPagerAdapter)mAdapter).getIconResId(index);
            else
                return 0;
        }
    }

    public interface IPagerAdapter {
        public int getRealCount();

        public int MAX_REAL_ITEMS = NormalizingViewPagerWrapper.MAX_REAL_ITEMS;

        public int getCount();

        public int getStartVirtualPosition();

        public boolean isViewFromObject(View view, Object object);
    }

    private class NormalizingOnPageChangeListener implements OnPageChangeListener {
        private OnPageChangeListener mListener;

        public NormalizingOnPageChangeListener(OnPageChangeListener pageListener) {
            mListener = pageListener;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mListener != null)
                mListener.onPageScrollStateChanged(state);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int virtualPosition = calcVirtualPosition(position);
            if (mListener != null)
                mListener.onPageScrolled(virtualPosition, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            int virtualPosition = calcVirtualPosition(position);
            Print.d(TAG, "onPageSelected: position = " + position + " virtualPosition = "
                    + virtualPosition);
            if (mListener != null)
                mListener.onPageSelected(virtualPosition);
        }

        private int calcVirtualPosition(int position) {
            return position % mNormPagerAdapter.getCount();
        }
    }
}

package com.mobile.utils.home;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

public class HomeMainTeaserAdapter extends PagerAdapter {

    public static final String TAG = LogTagHelper.create(HomeMainTeaserAdapter.class);

    private final View.OnClickListener mOnClickListener;

    private ArrayList<BaseTeaserObject> mTeasers;

    private LayoutInflater mInflater;

    public HomeMainTeaserAdapter(Context context, ArrayList<BaseTeaserObject> teasers, View.OnClickListener listener) {
        mTeasers = teasers;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = listener;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return CollectionUtils.isNotEmpty(mTeasers) ? mTeasers.size() : 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view .View,
     * java.lang.Object)
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }


    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view .View, int)
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return instantiateVirtualItem(container, position);
    }

    public Object instantiateVirtualItem(ViewGroup container, int position) {
        View view = null;
        try {
            view = mInflater.inflate(R.layout._def_home_teaser_main_item, container, false);
            BaseTeaserObject teaser = mTeasers.get(position);
            String imageUrl = teaser.getImagePhone();
            setClickableView(view, teaser);
            setImageToLoad(imageUrl, view);
            container.addView(view);
        } catch (InflateException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void setClickableView(View view, BaseTeaserObject teaser) {
        if(mOnClickListener != null) {
            view.setTag(R.id.target_title, teaser.getTitle());
            view.setTag(R.id.target_type, teaser.getTargetType());
            view.setTag(R.id.target_url, teaser.getUrl());
            view.setOnClickListener(mOnClickListener);
        }
    }

    private void setImageToLoad(String imageUrl, View imageTeaserView) {
        View progressBar = imageTeaserView.findViewById(R.id.home_teaser_item_progress);
        ImageView imageView = (ImageView) imageTeaserView.findViewById(R.id.home_teaser_item_image);
        RocketImageLoader.instance.loadImage(imageUrl, imageView, progressBar, R.drawable.no_image_large);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup , int,
     * java.lang.Object)
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
        //return mWidth;
        //return 0.7f;
    }
}

package com.mobile.utils.home.holder;

import android.content.Context;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.service.objects.home.object.BaseTeaserObject;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.ArrayList;

public class HomeMainTeaserAdapter extends PagerAdapter {

    public static final String TAG = HomeMainTeaserAdapter.class.getSimpleName();

    private final View.OnClickListener mOnClickListener;

    private final boolean isTablet;

    private ArrayList<BaseTeaserObject> mTeasers;

    private LayoutInflater mInflater;

    /**
     * Constructor
     */
    public HomeMainTeaserAdapter(Context context, ArrayList<BaseTeaserObject> teasers, View.OnClickListener listener, boolean tablet) {
        mTeasers = teasers;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = listener;
        isTablet = tablet;
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
        View view = mInflater.inflate(R.layout.home_teaser_main_item, container, false);
        BaseTeaserObject teaser = mTeasers.get(position);
        setImageToLoad(teaser.getImage(isTablet), view);
        TeaserViewFactory.setClickableView(view, teaser, mOnClickListener, position);
        container.addView(view);
        return view;
    }

    private void setImageToLoad(String imageUrl, View imageTeaserView) {
        View progressBar = imageTeaserView.findViewById(R.id.home_teaser_item_progress);
        final ImageView imageView = (ImageView) imageTeaserView.findViewById(R.id.home_teaser_item_image);
        //RocketImageLoader.instance.loadImage(imageUrl, imageView, progressBar, R.drawable.no_image_large);
        ImageManager.getInstance().loadImage(imageUrl, imageView, progressBar, R.drawable.no_image_slider, false);
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
        //return mWidth;
        //return 0.7f;
        return super.getPageWidth(position);
    }
}

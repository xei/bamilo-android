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

    private ArrayList<BaseTeaserObject> mTeasers;

    private LayoutInflater mInflater;

    public HomeMainTeaserAdapter(Context context, ArrayList<BaseTeaserObject> teasers) {
        mTeasers = teasers;
        mInflater = LayoutInflater.from(context);
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
        int virtualPosition = position;
        /*
        if (getRealCount() > 0) {
            virtualPosition = position % getRealCount();
        }
        */
        return instantiateVirtualItem(container, virtualPosition);
    }

    public Object instantiateVirtualItem(ViewGroup container, int position) {
        View view = null;
        try {
            view = mInflater.inflate(R.layout.image_loadable, container, false);
            String imageUrl = mTeasers.get(position).getImagePhone();
            setImageToLoad(imageUrl, view);
            container.addView(view);
        } catch (InflateException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void setImageToLoad(String imageUrl, View imageTeaserView) {
        View progressBar = imageTeaserView.findViewById(R.id.image_loading_progress);
        ImageView imageView = (ImageView) imageTeaserView.findViewById(R.id.image_view);
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

}

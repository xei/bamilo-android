/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import pt.rocket.controllers.NormalizingViewPagerWrapper;
import pt.rocket.controllers.NormalizingViewPagerWrapper.IPagerAdapter;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.ITargeting;
import pt.rocket.framework.objects.ImageTeaserGroup.TeaserImage;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.HomeFragmentActivity;
import pt.rocket.view.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */

public class MainOneSlideFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create( MainOneSlideFragment.class );

    private HomeFragmentActivity parentActivity;
    
    private ArrayList<TeaserImage> teaserImageArrayList;
    
    private OnClickListener onTeaserClickListener;


    /**
     * 
     * @param dynamicForm
     * @return
     */
	public static MainOneSlideFragment getInstance() {
        MainOneSlideFragment mainOneSlideFragment = new MainOneSlideFragment();
        return mainOneSlideFragment;
    }
    
    /**
     * Empty constructor
     * @param arrayList 
     */
    public MainOneSlideFragment() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class));
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values){
        this.teaserImageArrayList = (ArrayList<TeaserImage>) values;
    }
    
    @Override
    public void sendListener(int identifier, OnClickListener onTeaserClickListener){
        this.onTeaserClickListener = onTeaserClickListener;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
        parentActivity = (HomeFragmentActivity) activity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        View view = inflater.inflate(R.layout.teaser_swipe_banners_group, viewGroup, false);
             
        
        ViewPager pager = (ViewPager) view.findViewById(R.id.viewpager );
        if (pager.getAdapter() == null) {
            ImagePagerAdapter adapter = new ImagePagerAdapter(teaserImageArrayList, inflater);
            pager.setAdapter(adapter);
            PageIndicator indicator = (PageIndicator) view
                    .findViewById(R.id.indicator);
            NormalizingViewPagerWrapper pagerWrapper = new NormalizingViewPagerWrapper(
                    getActivity(), pager, adapter, indicator);
            indicator.setViewPager(pagerWrapper);
        }
        
        Log.i(TAG, "code1 yesees");
        
        return view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
//        FlurryTracker.get().begin();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        //
//        AnalyticsGoogle.get().trackPage(R.string.glogin);
        //
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
//        FlurryTracker.get().end();
    }

    @Override
    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
        return true;
    }
        
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        return false;
    }
    
    
    private View createImageTeaserView(TeaserImage teaserImage, ViewGroup vg, LayoutInflater mInflater) {
        View imageTeaserView = mInflater.inflate(R.layout.image_loadable, vg,
                false);
        setImageToLoad(teaserImage.getImageUrl(), imageTeaserView);
        attachTeaserListener(teaserImage, imageTeaserView);
        return imageTeaserView;
    }
    
    private void attachTeaserListener(ITargeting targeting, View view) {
        view.setTag(R.id.target_url, targeting.getTargetUrl());
        view.setTag(R.id.target_type, targeting.getTargetType());
        view.setTag(R.id.target_title, targeting.getTargetTitle());
        view.setOnClickListener(onTeaserClickListener);
    }
    
    private void setImageToLoad(String imageUrl, View imageTeaserView) {
        final ImageView imageView = (ImageView) imageTeaserView
                .findViewById(R.id.image_view);
        final View progressBar = imageTeaserView
                .findViewById(R.id.image_loading_progress);
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageLoader.getInstance().displayImage(imageUrl, imageView,
                    new SimpleImageLoadingListener() {

                        /*
                         * (non-Javadoc)
                         * 
                         * @see
                         * com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener
                         * #onLoadingComplete(java.lang.String, android.view.View,
                         * android.graphics.Bitmap)
                         */
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            progressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }
                    });
        }

    }

    
    private class ImagePagerAdapter extends PagerAdapter implements IPagerAdapter, IconPagerAdapter {

        private final List<TeaserImage> teaserImages;
        private LayoutInflater mInflater;
        /**
         * @param inflater 
         * 
         */
        public ImagePagerAdapter(List<TeaserImage> teaserImages, LayoutInflater inflater) {
            this.teaserImages = teaserImages;
            this.mInflater = inflater;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return MAX_REAL_ITEMS;
        }
        
        public int getRealCount() {
            return teaserImages.size();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.support.v4.view.PagerAdapter#isViewFromObject(android.view
         * .View, java.lang.Object)
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.support.v4.view.PagerAdapter#instantiateItem(android.view
         * .View, int)
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {  
            int virtualPosition = position % getRealCount();
            return instantiateVirtualItem(container, virtualPosition);
        }
        
        public Object instantiateVirtualItem(ViewGroup container, int position) {
            TeaserImage teaserImage = teaserImages.get(position);
            View view = createImageTeaserView(teaserImage, container, mInflater);
            attachTeaserListener(teaserImage, view);
            container.addView(view);
            return view;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup
         * , int, java.lang.Object)
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        
        @Override
        public int getStartVirtualPosition() {
            return 0;
        }

        @Override
        public int getIconResId(int index) {
            return R.drawable.shape_pageindicator;            
        }

    }

}

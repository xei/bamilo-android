package com.bamilo.android.appmodule.bamiloapp.controllers;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bamilo.android.appmodule.bamiloapp.controllers.NormalizingViewPagerWrapper.IPagerAdapter;
import com.bamilo.android.framework.service.objects.product.ImageUrls;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.photoview.PhotoView;
import com.bamilo.android.appmodule.bamiloapp.utils.photoview.PhotoViewAttacher;
import com.bamilo.android.R;

import java.util.ArrayList;

public class GalleryPagerAdapter extends PagerAdapter implements IPagerAdapter {

    private static final String TAG = GalleryPagerAdapter.class.getSimpleName();
    
    private final static int MIN_NUM_OF_IMAGES = 3;

    private ArrayList<ImageUrls> mImageUrls;
    private final LayoutInflater mInflater;
    private boolean isZoomAvailable = false;
    private View primaryView;
    private Context mContext;

    public GalleryPagerAdapter(Context context, ArrayList<ImageUrls> imageUrls, boolean zoomAvailable){
        mImageUrls = imageUrls;
        mInflater = LayoutInflater.from(context);
        isZoomAvailable = zoomAvailable;
        mContext = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        // return MAX_REAL_ITEMS;
        return mImageUrls.size();
    }

    public int getRealCount() {
        return mImageUrls.size();
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


    public void replaceAll(ArrayList<ImageUrls> images) {
        this.mImageUrls = images;
        notifyDataSetChanged();
        Print.d(TAG, "replaceAll: done - notfied");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view .View, int)
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int virtualPosition = position;
        if (getRealCount() > 0) {
            virtualPosition = position % getRealCount();
        }

        return instantiateVirtualItem(container, virtualPosition);
    }

    public Object instantiateVirtualItem(ViewGroup container, int position) {
        View view = null;
        try {
            if (this.isZoomAvailable) {
                Print.i(TAG, " full_screen_gallery: " + position);
                view = mInflater.inflate(R.layout._def_pdv_gallery_item, container, false);
            } else {
                view = mInflater.inflate(R.layout._def_gen_image_loadable, container, false);
            }

            ImageUrls imageUrls =  mImageUrls.get(position);
            if(imageUrls.hasZoom() && this.isZoomAvailable)
                setImageToLoad(imageUrls.getZoom(), view);
            else
                setImageToLoad(imageUrls.getUrl(), view);
            container.addView(view);
        } catch (InflateException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void setImageToLoad(String imageUrl, View imageTeaserView) {
        final View progressBar = imageTeaserView.findViewById(R.id.image_loading_progress);
        final ImageView imageView = (this.isZoomAvailable) ? (PhotoView) imageTeaserView.findViewById(R.id.image_view) : (ImageView) imageTeaserView.findViewById(R.id.image_view);
        ImageManager.getInstance().loadImage(imageUrl, imageView, progressBar, R.drawable.no_image_large, false);
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
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#setPrimaryItem(android.view.ViewGroup, int, java.lang.Object)
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        // Check the primary view
        resetPrimaryView(object);
    }
        
    /**
     * Force the reset of the primary image view for a product that only has one image 
     * Fix the ticket MOBILE-3733
     * @author sergiopereira
     * @see <a href="https://github.com/chrisbanes/PhotoView">Github: PhotoView</a>
     */
    private void resetPrimaryView(Object object){
        //Log.d(TAG, "SET PRIMARY ITEM: " + position + " ITEMS: " + getRealCount() );
        // Validate zoom support
        if (this.isZoomAvailable && getRealCount() == MIN_NUM_OF_IMAGES) {
            // Get the primary view and reset view
            try {
                primaryView = (View) object;
                ImageView mCurrentPhotoView = (ImageView) primaryView.findViewById(R.id.image_view);
                PhotoViewAttacher mAttacher = new PhotoViewAttacher(mCurrentPhotoView);
                mAttacher.setScaleType(ScaleType.FIT_CENTER);
            } catch (IllegalStateException | NullPointerException e) {
                Print.w(TAG, "RESETING IMAGE VIEW: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get the primary view on the view pager
     * @return View
     * @author sergiopereira
     */
    public View getPrimaryView(){
        return primaryView;
    }
    
    @Override
    public int getStartVirtualPosition() {
        return 1;
    }

}

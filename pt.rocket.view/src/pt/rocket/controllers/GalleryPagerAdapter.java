package pt.rocket.controllers;

import java.util.ArrayList;

import pt.rocket.controllers.NormalizingViewPagerWrapper.IPagerAdapter;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.R;
import pt.rocket.utils.photoview.PhotoView;
import pt.rocket.utils.photoview.PhotoViewAttacher;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import de.akquinet.android.androlog.Log;

public class GalleryPagerAdapter extends PagerAdapter implements IPagerAdapter {

    private static final String TAG = LogTagHelper.create(GalleryPagerAdapter.class);
    
    private final static int MIN_NUM_OF_IMAGES = 3;
    
    private ArrayList<String> mImageUrls;
    private LayoutInflater mInflater;
    private boolean isZoomAvailable = false;
    //private Context mContext;
    private View primaryView;

    public GalleryPagerAdapter(Context context, ArrayList<String> imageUrls, boolean isZoomAvailable) {
        mImageUrls = imageUrls;
        mInflater = LayoutInflater.from(context);
        //this.mContext = context;
        this.isZoomAvailable = isZoomAvailable;
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

    public void replaceAll(ArrayList<String> images) {
        this.mImageUrls = images;
        notifyDataSetChanged();
        Log.d(TAG, "replaceAll: done - notfied");
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
                Log.i(TAG, " full_screen_gallery: " + position);
                view = mInflater.inflate(R.layout.full_screen_gallery, container, false);
            } else {
                view = mInflater.inflate(R.layout.image_loadable, container, false);
            }

            String imageUrl = mImageUrls.get(position);
            setImageToLoad(imageUrl, view);
            container.addView(view);
        } catch (InflateException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void setImageToLoad(String imageUrl, View imageTeaserView) {
        final View progressBar = imageTeaserView.findViewById(R.id.image_loading_progress);
        if (this.isZoomAvailable) {
            final PhotoView imageView = (PhotoView) imageTeaserView.findViewById(R.id.image_view);
            imageView.setImageResource(R.drawable.no_image_small);
            Log.i(TAG, "LOAD PHOTO: " + imageView.getId() + " " + imageUrl);
            if (!TextUtils.isEmpty(imageUrl)) {
                RocketImageLoader.instance.loadImage(imageUrl, imageView, progressBar, R.drawable.no_image_large);
            }
        } else {
            final ImageView imageView = (ImageView) imageTeaserView.findViewById(R.id.image_view);
            imageView.setImageResource(R.drawable.no_image_small);
             
            if (!TextUtils.isEmpty(imageUrl)) {
                RocketImageLoader.instance.loadImage(imageUrl, imageView, progressBar, R.drawable.no_image_large);
            }
        }

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
        resetPrimaryView(container, position, object);
    }
        
    /**
     * Force the reset of the primary image view for a product that only has one image 
     * Fix the ticket MOBILE-3733
     * @author sergiopereira
     * @see <a href="https://github.com/chrisbanes/PhotoView">Github: PhotoView</a>
     */
    private void resetPrimaryView(ViewGroup container, int position, Object object){
        //Log.d(TAG, "SET PRIMARY ITEM: " + position + " ITEMS: " + getRealCount() );
        // Validate zoom support
        if (this.isZoomAvailable && getRealCount() == MIN_NUM_OF_IMAGES) {
            // Get the primary view and reset view
            try {
                primaryView = (View) object;
                ImageView mCurrentPhotoView = (ImageView) primaryView.findViewById(R.id.image_view);
                PhotoViewAttacher mAttacher = new PhotoViewAttacher(mCurrentPhotoView);
                mAttacher.setScaleType(ScaleType.FIT_CENTER);
                mAttacher = null;
            } catch (IllegalStateException e) {
                Log.w(TAG, "RESETING IMAGE VIEW: " + e.getMessage());
            } catch (NullPointerException e) {
                Log.w(TAG, "RESETING IMAGE VIEW: " + e.getMessage());
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

package com.bamilo.android.appmodule.bamiloapp.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.controllers.NormalizingViewPagerWrapper.IPagerAdapter;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.photoview.PhotoView;
import com.bamilo.android.appmodule.bamiloapp.utils.photoview.PhotoViewAttacher;
import com.bamilo.android.framework.service.objects.product.ImageUrls;
import java.util.ArrayList;

public class GalleryPagerAdapter extends PagerAdapter implements IPagerAdapter {

    private static final String TAG = GalleryPagerAdapter.class.getSimpleName();

    private final static int MIN_NUM_OF_IMAGES = 3;

    private ArrayList<ImageUrls> mImageUrls;
    private final LayoutInflater mInflater;
    private boolean isZoomAvailable = false;
    private View primaryView;

    public GalleryPagerAdapter(Context context, ArrayList<ImageUrls> imageUrls,
            boolean zoomAvailable) {
        mImageUrls = imageUrls;
        mInflater = LayoutInflater.from(context);
        isZoomAvailable = zoomAvailable;
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    public int getRealCount() {
        return mImageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
        return arg0 == arg1;
    }

    public void replaceAll(ArrayList<ImageUrls> images) {
        this.mImageUrls = images;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
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
                view = mInflater.inflate(R.layout._def_pdv_gallery_item, container, false);
            } else {
                view = mInflater.inflate(R.layout._def_gen_image_loadable, container, false);
            }

            ImageUrls imageUrls = mImageUrls.get(position);
            if (imageUrls.hasZoom() && this.isZoomAvailable) {
                setImageToLoad(imageUrls.getZoom(), view);
            } else {
                setImageToLoad(imageUrls.getUrl(), view);
            }
            container.addView(view);
        } catch (InflateException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void setImageToLoad(String imageUrl, View imageTeaserView) {
        final View progressBar = imageTeaserView.findViewById(R.id.image_loading_progress);
        final ImageView imageView =
                (this.isZoomAvailable) ? (PhotoView) imageTeaserView.findViewById(R.id.image_view)
                        : (ImageView) imageTeaserView.findViewById(R.id.image_view);
        ImageManager.getInstance()
                .loadImage(imageUrl, imageView, progressBar, R.drawable.no_image_large, false);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        resetPrimaryView(object);
    }

    /**
     * Force the reset of the primary image view for a product that only has one image Fix the
     * ticket MOBILE-3733
     *
     * @author sergiopereira
     * @see <a href="https://github.com/chrisbanes/PhotoView">Github: PhotoView</a>
     */
    private void resetPrimaryView(Object object) {
        // Validate zoom support
        if (this.isZoomAvailable && getRealCount() == MIN_NUM_OF_IMAGES) {
            // Get the primary view and reset view
            try {
                primaryView = (View) object;
                ImageView mCurrentPhotoView = primaryView.findViewById(R.id.image_view);
                PhotoViewAttacher mAttacher = new PhotoViewAttacher(mCurrentPhotoView);
                mAttacher.setScaleType(ScaleType.FIT_CENTER);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Get the primary view on the view pager
     *
     * @return View
     * @author sergiopereira
     */
    public View getPrimaryView() {
        return primaryView;
    }

    @Override
    public int getStartVirtualPosition() {
        return 1;
    }
}

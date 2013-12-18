package pt.rocket.controllers;

import java.util.ArrayList;

import pt.rocket.app.ImageLoaderComponent;
import pt.rocket.controllers.NormalizingViewPagerWrapper.IPagerAdapter;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.view.R;
import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import de.akquinet.android.androlog.Log;

public class GalleryPagerAdapter extends PagerAdapter implements IPagerAdapter {

    private static final String TAG = LogTagHelper.create(GalleryPagerAdapter.class);
    private ArrayList<String> mImageUrls;
    private LayoutInflater mInflater;
    private boolean isZoomAvailable = false;
    private Context mContext;

    public GalleryPagerAdapter(Context context, ArrayList<String> imageUrls, boolean isZoomAvailable) {
        mImageUrls = imageUrls;
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
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
                Log.i(TAG, " full_screen_gallery ");
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
            
            if (!TextUtils.isEmpty(imageUrl)) {
                AQuery aq = new AQuery(mContext);
                aq.id(imageView).image(imageUrl, true, true, 0, 0, new BitmapAjaxCallback() {

                            @Override
                            public void callback(String url, ImageView iv, Bitmap bm,
                                    AjaxStatus status) {

                                iv.setImageBitmap(bm);
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                // ImageLoader
                // .getInstance()
                // .displayImage(
                // imageUrl,
                // imageView,
                // JumiaApplication.COMPONENTS.
                // get(ImageLoaderComponent.class).largeLoaderOptions,
                // new SimpleImageLoadingListener() {
                //
                // /*
                // * (non-Javadoc)
                // *
                // * @see com.nostra13.universalimageloader.core.assist.
                // * SimpleImageLoadingListener#onLoadingComplete(java.lang.String,
                // * android.view.View, android.graphics.Bitmap)
                // */
                // @Override
                // public void onLoadingComplete(String imageUri, View view,
                // Bitmap loadedImage) {
                // progressBar.setVisibility(View.GONE);
                // imageView.setVisibility(View.VISIBLE);
                // }
                //
                // });
            }
        } else {
            final ImageView imageView = (ImageView) imageTeaserView.findViewById(R.id.image_view);
            imageView.setImageResource(R.drawable.no_image_small);
             
            if (!TextUtils.isEmpty(imageUrl)) {
                AQuery aq = new AQuery(mContext);
                aq.id(imageView).image(imageUrl, true, true, 0, 0, new BitmapAjaxCallback() {

                            @Override
                            public void callback(String url, ImageView iv, Bitmap bm,
                                    AjaxStatus status) {

                                iv.setImageBitmap(bm);
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                // ImageLoader
                // .getInstance()
                // .displayImage(
                // imageUrl,
                // imageView,
                // JumiaApplication.COMPONENTS.
                // get(ImageLoaderComponent.class).largeLoaderOptions,
                // new SimpleImageLoadingListener() {
                //
                // /*
                // * (non-Javadoc)
                // *
                // * @see com.nostra13.universalimageloader.core.assist.
                // * SimpleImageLoadingListener#onLoadingComplete(java.lang.String,
                // * android.view.View, android.graphics.Bitmap)
                // */
                // @Override
                // public void onLoadingComplete(String imageUri, View view,
                // Bitmap loadedImage) {
                // progressBar.setVisibility(View.GONE);
                // imageView.setVisibility(View.VISIBLE);
                // }
                //
                // });
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

    @Override
    public int getStartVirtualPosition() {
        return 1;
    }

}

package pt.rocket.controllers;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import pt.rocket.app.ImageLoaderComponent;
import pt.rocket.controllers.NormalizingViewPagerWrapper.IPagerAdapter;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.view.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GalleryPagerAdapter extends PagerAdapter implements IPagerAdapter {
    
	private static final String TAG = LogTagHelper.create( GalleryPagerAdapter.class );
    private final List<String> mImageUrls;
	private LayoutInflater mInflater;

	public GalleryPagerAdapter(Context context, List<String> imageUrls) {
		mImageUrls = imageUrls;
		mInflater = LayoutInflater.from(context);
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
        return mImageUrls.size();
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view
	 * .View, java.lang.Object)
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view
	 * .View, int)
	 */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int virtualPosition = position % getRealCount();
        return instantiateVirtualItem(container, virtualPosition);
    }
    
	public Object instantiateVirtualItem(ViewGroup container, int position) {
		View view = mInflater.inflate(R.layout.image_loadable, container, false);
		String imageUrl = mImageUrls.get(position);
		setImageToLoad(imageUrl, view);
		container.addView( view );
 
		return view;
	}

	private void setImageToLoad(String imageUrl, View imageTeaserView) {
		final ImageView imageView = (ImageView) imageTeaserView.findViewById(R.id.image_view);
		imageView.setImageResource(R.drawable.no_image_small);
		final View progressBar = imageTeaserView.findViewById(R.id.image_loading_progress);
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageLoader
                    .getInstance()
                    .displayImage(
                            imageUrl,
                            imageView,
                            JumiaApplication.COMPONENTS.
                                get(ImageLoaderComponent.class).largeLoaderOptions,
                            new SimpleImageLoadingListener() {

                                /*
                                 * (non-Javadoc)
                                 * 
                                 * @see com.nostra13.universalimageloader.core.assist.
                                 * SimpleImageLoadingListener#onLoadingComplete(java.lang.String,
                                 * android.view.View, android.graphics.Bitmap)
                                 */
                                @Override
                                public void onLoadingComplete(String imageUri, View view,
                                        Bitmap loadedImage) {
                                    progressBar.setVisibility(View.GONE);
                                    imageView.setVisibility(View.VISIBLE);
                                }

                            });
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup ,
	 * int, java.lang.Object)
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

    @Override
    public int getStartVirtualPosition() {
        return 0;
    }

}

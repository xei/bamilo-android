/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.ITargeting;
import pt.rocket.framework.objects.ImageTeaserGroup.TeaserImage;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.WindowHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class StaticBannerFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(StaticBannerFragment.class);

    private static final int MAX_IMAGES_ON_SCREEN = 2;

    private static ArrayList<TeaserImage> teaserImageArrayList;

    private OnClickListener onTeaserClickListener;

    private LayoutInflater inflater;

    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static StaticBannerFragment getInstance() {
        StaticBannerFragment staticBannerFragment = new StaticBannerFragment();
        return staticBannerFragment;
    }

    /**
     * Empty constructor
     * 
     * @param arrayList
     */
    public StaticBannerFragment() {
        super(IS_NESTED_FRAGMENT);
        this.setRetainInstance(true);
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
        this.teaserImageArrayList = (ArrayList<TeaserImage>) values;
    }

    @Override
    public void sendListener(int identifier, OnClickListener onTeaserClickListener) {
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
    public View onCreateView(LayoutInflater mInflater, ViewGroup viewGroup,
            Bundle savedInstanceState) {
        super.onCreateView(mInflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");

        View view = mInflater.inflate(R.layout.teaser_banners_group, null, false);
        inflater = mInflater;
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
        Log.i(TAG, "ON START");
        ViewGroup container = (ViewGroup) getView()
                .findViewById(R.id.teaser_group_container);

        if (teaserImageArrayList != null) {
            int maxItems = MAX_IMAGES_ON_SCREEN;
            if (teaserImageArrayList.size() < MAX_IMAGES_ON_SCREEN) {
                maxItems = teaserImageArrayList.size();
            }

            int i;
            for (i = 0; i < maxItems; i++) {
                TeaserImage image = teaserImageArrayList.get(i);
                if (i > 0)
                    inflater.inflate(R.layout.vertical_divider, container);
                container.addView(createImageTeaserView(image, container, inflater));
            }

        }

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
        // FlurryTracker.get().end();
    }

    @Override
    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
        return true;
    }

    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        return false;
    }

    private View createImageTeaserView(TeaserImage teaserImage, ViewGroup vg,
            LayoutInflater mInflater) {
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
        
        int mainContentWidth = (int) (WindowHelper.getWidth(getActivity()) * getResources().getFraction(R.dimen.navigation_menu_offset,1,1));
        imageTeaserView.getLayoutParams().width = mainContentWidth / teaserImageArrayList.size();
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
                            imageView.setScaleType(ScaleType.FIT_XY);
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            // TODO Auto-generated method stub
                            super.onLoadingStarted(imageUri, view);
                            imageView.setScaleType(ScaleType.FIT_CENTER);
                        }

                    });
        }

    }

    @Override
    public void notifyFragment(Bundle bundle) {
        // TODO Auto-generated method stub
        
    }
}

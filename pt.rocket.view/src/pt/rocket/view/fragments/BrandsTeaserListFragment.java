/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.BrandsTeaserGroup;
import pt.rocket.framework.objects.BrandsTeaserGroup.TeaserBrand;
import pt.rocket.framework.objects.ITargeting;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import pt.rocket.view.TeaserFragmentActivity;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class BrandsTeaserListFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(BrandsTeaserListFragment.class);

    private TeaserFragmentActivity parentActivity;

    private BrandsTeaserGroup brandsTeaserGroup;

    private OnClickListener onTeaserClickListener;

    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static BrandsTeaserListFragment getInstance() {
        BrandsTeaserListFragment producTeaserListFragment = new BrandsTeaserListFragment();
        return producTeaserListFragment;
    }

    /**
     * Empty constructor
     * 
     * @param arrayList
     */
    public BrandsTeaserListFragment() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class));
        
    }

    
    @Override
    public void sendValuesToFragment(int identifier, Object values){
        this.brandsTeaserGroup = (BrandsTeaserGroup) values;
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
        parentActivity = (TeaserFragmentActivity) activity;
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
     * setImageToLoad
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup viewGroup,
            Bundle savedInstanceState) {
        super.onCreateView(mInflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");

        View view = mInflater.inflate(R.layout.teaser_brands_group, viewGroup, false);

        ViewGroup container = (ViewGroup) view
                .findViewById(R.id.teaser_group_container);

        ((TextView) view.findViewById(R.id.teaser_group_title))
                .setText(brandsTeaserGroup.getTitle());
        for (TeaserBrand brand : brandsTeaserGroup.getTeasers()) {
            container.addView(createBrandTeaserView(brand, container, mInflater));
        }
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
//        AnalyticsGoogle.get().trackPage(R.string.gteaserprod_prefix);
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

    private View createBrandTeaserView(TeaserBrand brand, ViewGroup vg, LayoutInflater mInflater) {
        View brandTeaserView = mInflater.inflate(R.layout.brand_item_small,
                vg, false);
        if (brand.getImage() != null) {
            setImageToLoad(brand.getImage().getUrl(),
                    brandTeaserView);
        }
        attachTeaserListener(brand, brandTeaserView);
        return brandTeaserView;
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
 }

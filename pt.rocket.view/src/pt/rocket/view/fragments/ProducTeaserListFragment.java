/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.ITargeting;
import pt.rocket.framework.objects.ProductTeaserGroup;
import pt.rocket.framework.objects.ProductTeaserGroup.TeaserProduct;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.HomeFragmentActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
public class ProducTeaserListFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ProducTeaserListFragment.class);

    private HomeFragmentActivity parentActivity;

    private static ProductTeaserGroup productTeaserGroup;

    private OnClickListener onTeaserClickListener;

    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProducTeaserListFragment getInstance() {
        ProducTeaserListFragment producTeaserListFragment = new ProducTeaserListFragment();
        return producTeaserListFragment;
    }

    /**
     * Empty constructor
     * 
     * @param arrayList
     */
    public ProducTeaserListFragment() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class));
        
    }

    
    @Override
    public void sendValuesToFragment(int identifier, Object values){
        this.productTeaserGroup = (ProductTeaserGroup) values;
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
    public View onCreateView(LayoutInflater mInflater, ViewGroup viewGroup,
            Bundle savedInstanceState) {
        super.onCreateView(mInflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");

        View view = mInflater.inflate(R.layout.teaser_products_group, viewGroup, false);

        ViewGroup container = (ViewGroup) view
                .findViewById(R.id.teaser_group_container);

        ((TextView) view.findViewById(R.id.teaser_group_title))
                .setText(productTeaserGroup.getTitle());
        for (TeaserProduct product : productTeaserGroup.getTeasers()) {
            container.addView(createProductTeaserView(product, container, mInflater));
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

    private View createProductTeaserView(TeaserProduct product, ViewGroup vg, LayoutInflater mInflater) {
        View productTeaserView = mInflater.inflate(R.layout.product_item_small,
                vg, false);
        if (product.getImages().size() > 0) {
            setImageToLoad(product.getImages().get(0).getUrl(),
                    productTeaserView);
        }
        ((TextView) productTeaserView.findViewById(R.id.item_title))
                .setText(product.getName());
        String price;
        if (!TextUtils.isEmpty(product.getSpecialPrice())) {
            price = product.getSpecialPrice();
        } else {
            price = product.getPrice();
        }
        ((TextView) productTeaserView.findViewById(R.id.item_price))
                .setText(price);
        attachTeaserListener(product, productTeaserView);
        return productTeaserView;
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
        
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        int width;
        int height;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            display.getSize(size);
            width = size.x;
            height = size.y;
       } else {
           width = display.getWidth();
           height = display.getHeight();
       }
        
        imageTeaserView.getLayoutParams().width = width/productTeaserGroup.getTeasers().size();
        
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

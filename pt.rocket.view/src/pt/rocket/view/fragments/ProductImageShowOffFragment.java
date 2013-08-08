/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.OnFragmentActivityInteraction;
//import pt.rocket.view.ImageLoaderComponent;
import pt.rocket.app.ImageLoaderComponent;
import pt.rocket.view.ProductDetailsActivityFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class ProductImageShowOffFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(ProductImageShowOffFragment.class);

    private ProductDetailsActivityFragment parentActivity;

    private ViewGroup mProductImageShowOffContainer;

    private ImageView mProductImage;

    private ProgressBar mProductImageLoading;
    
    private ViewGroup mProductDiscountContainer;

    private TextView mProductDiscountPercentage;
    private CompleteProduct mCompleteProduct;
    
    private int CURRENT_IMAGE_INDEX = 0;
    private View mainView;
    
    private OnFragmentActivityInteraction mCallback;
    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductImageShowOffFragment getInstance() {
        ProductImageShowOffFragment productImageShowOffFragment = new ProductImageShowOffFragment();
        return productImageShowOffFragment;
    }

    /**
     * Empty constructor
     * 
     * @param arrayList
     */
    public ProductImageShowOffFragment() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class));
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
        this.mCompleteProduct= (CompleteProduct) values;
        if(identifier==1){
            setContentInformation();
        }
    }

    @Override
    public void sendPositionToFragment(int position){

        /**
         * if still loading the product info, show image loading.
         */
        if(position < 0){
            showImageLoading();
        }
    }
    
    @Override
    public void sendListener(int identifier, OnClickListener onTeaserClickListener) {

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
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentActivityInteraction) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnActivityFragmentInteraction");
        }
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

        mainView = mInflater.inflate(R.layout.product_showoff_container_frame, viewGroup, false);

        mProductImageShowOffContainer = (ViewGroup) mainView.findViewById(R.id.product_showoff_fragment);
        mProductImageShowOffContainer.setOnClickListener(this);
        mProductImage = (ImageView) mProductImageShowOffContainer.findViewById(R.id.product_image);
        mProductImageLoading = (ProgressBar) mProductImageShowOffContainer
                .findViewById(R.id.progressBar2);

        mProductDiscountContainer = (ViewGroup) mProductImageShowOffContainer
                .findViewById(R.id.product_discount_container);

        mProductDiscountPercentage = (TextView) mProductDiscountContainer
                .findViewById(R.id.product_discount_percentage);
        setContentInformation();
        return mainView;
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
        // FlurryTracker.get().begin();
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
        // AnalyticsGoogle.get().trackPage(R.string.gteaser_prefix);
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
 
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        mCallback.onFragmentSelected(FragmentType.PRODUCT_SHOWOFF);
    }
    
    private void setContentInformation() {
        if(mCompleteProduct == null){
            getActivity().finish();
        }
        // displayVariations();
        loadProductImage(CURRENT_IMAGE_INDEX);
        setPercentageValue();
    }
    
    private void setPercentageValue(){
        int discountPercentage = mCompleteProduct.getMaxSavingPercentage().intValue();
        String unitPrice = mCompleteProduct.getPrice();
        String specialPrice = mCompleteProduct.getMaxSpecialPrice();
        if (specialPrice == null || specialPrice.equals(unitPrice)) {
            // display only the normal price
            mProductDiscountContainer.setVisibility(View.GONE);
        } else {
            // display reduced and special price
            mProductDiscountPercentage.setText("-" + String.valueOf(discountPercentage) + "%");
            mProductDiscountContainer.setVisibility(View.VISIBLE);
        }
    }
    
    private void showImageLoading(){
        mProductImage.setImageResource(R.drawable.no_image_large);
        mProductImageLoading.setVisibility(View.VISIBLE);
    }
    
    private void loadProductImage(int indexInImageList) {
        if (indexInImageList > mCompleteProduct.getImageList().size() - 1) {
            Log.w(TAG,
                    "loadProductImage: index for image is out of range of imagelist. Cant display anything");
            return;
        }
        showImageLoading();
        Log.d(TAG, "loadProductImage: loading product image");
        
        String imageURL = mCompleteProduct.getImageList().get(indexInImageList);
        ImageLoader.getInstance().displayImage(imageURL, mProductImage,
                JumiaApplication.COMPONENTS.get(ImageLoaderComponent.class).largeLoaderOptions,
                new SimpleImageLoadingListener() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#
                     * onLoadingComplete(java.lang.String, android.view.View,
                     * android.graphics.Bitmap)
                     */
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        mProductImage.setVisibility(View.VISIBLE);
                        mProductImageLoading.setVisibility(View.GONE);
                        Log.d(TAG, "loadProductImage: onLoadingComplete");
                    }

                });
    }
    
}

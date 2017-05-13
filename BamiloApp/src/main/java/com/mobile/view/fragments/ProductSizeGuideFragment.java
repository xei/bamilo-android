/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.mobile.components._unused_.dialogs.WizardPreferences;
import com.mobile.components._unused_.dialogs.WizardPreferences.WizardType;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.service.utils.NetworkConnectivity;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.imageloader.RocketImageLoader.RocketImageLoaderListener;
import com.mobile.utils.photoview.PhotoView;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Class used to shoe the size guide.
 * @author sergiopereira
 */
public class ProductSizeGuideFragment extends BaseFragment {

    private static final String TAG = ProductSizeGuideFragment.class.getSimpleName();
    
    private String mSizeGuideUrl;

    private View mWizard;

    private PhotoView mImageView;

    /**
     * Empty constructor
     * @author sergiopereira
     */
    public ProductSizeGuideFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.PRODUCT,
                R.layout.product_size_guide_main,
                R.string.size_guide_label,
                NO_ADJUST_CONTENT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get size guide URL from arguments
        mSizeGuideUrl = getArguments().getString(ConstantsIntentExtra.SIZE_GUIDE_URL);
        // Get from saved instance
        if(savedInstanceState != null) mSizeGuideUrl = savedInstanceState.getString(ConstantsIntentExtra.SIZE_GUIDE_URL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get views
        mImageView = (PhotoView) view.findViewById(R.id.product_size_guide_image);
        mWizard = view.findViewById(R.id.product_size_wizard_stub);
        // Validate URL
        if(!TextUtils.isEmpty(mSizeGuideUrl)) {
            showSizeGuide(mImageView, mSizeGuideUrl);
            showWizard(mWizard);
        }
        // Show empty view
        else showContinueShopping();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON RESUME");
        outState.putString(ConstantsIntentExtra.SIZE_GUIDE_URL, mSizeGuideUrl);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
        mWizard = null;
        mImageView = null;
    }
    
    /*
     * ############# LAYOUT #############
     */
    
    /**
     * 
     * @param mImageView 
     * @param url
     */
    private void showSizeGuide(PhotoView mImageView, String url) {
        Print.i(TAG, "ON SHOW SIZE GUIDE");
     // Load image        
        RocketImageLoader.getInstance().loadImage(url, mImageView, null, R.drawable.no_image_large, new RocketImageLoaderListener() {
            
            @Override
            public void onLoadedSuccess(String url, Bitmap bitmap) { 
                showFragmentContentContainer();
            }
            
            @Override
            public void onLoadedError() {
                showRetryLayout();
            }
            
            @Override
            public void onLoadedCancel() { }
        });
    }
    
    private void showRetryLayout() {
        if(NetworkConnectivity.isConnected(getBaseActivity())) showFragmentErrorRetry();
        else showFragmentNoNetworkRetry();
    }
    
    /**
     * Method used to validate if is necessary show the wizard.
     * @param mWizard
     * @author sergiopereira
     */
    private void showWizard(View mWizard) {
        // Get flag
        boolean firstTime = WizardPreferences.isFirstTime(getBaseActivity(), WizardType.SIZE_GUIDE);
        // Validate
        if(!firstTime) return;
        // Inflate stub view
        mWizard.setVisibility(View.VISIBLE);
        // Set view
        try {
            getView().findViewById(R.id.wizard_product_size_button).setOnClickListener(this);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING NPE ON SHOW RETRY LAYOUT");
        }   
    }
    
    /*
     * ############# CLICK LISTENER #############
     */
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Case wizard
        if (id == R.id.wizard_product_size_button) onClickWizardButton();
        // Case unknown
        else Print.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
    }

    /**
     * Process the click on wizard
     * @author sergiopereira
     */
    private void onClickWizardButton() {
        try {
            // Set flag
            WizardPreferences.changeState(getBaseActivity(), WizardType.SIZE_GUIDE);
            // Hide wizard
            mWizard.setVisibility(View.GONE);   
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON HIDE WIZARD", e);
        }
    }


    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onClickContinueButton();
    }

}

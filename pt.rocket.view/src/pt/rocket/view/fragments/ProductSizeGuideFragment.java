/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.WizardPreferences;
import pt.rocket.utils.dialogfragments.WizardPreferences.WizardType;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.utils.imageloader.RocketImageLoader.RocketImageLoaderListener;
import pt.rocket.utils.photoview.PhotoView;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import de.akquinet.android.androlog.Log;

/**
 * Class used to shoe the size guide.
 * @author sergiopereira
 */
public class ProductSizeGuideFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(ProductSizeGuideFragment.class);
    
    private String mSizeGuideUrl;

    private View mWizard;

    private PhotoView mImageView;
    
    /**
     * Get a instance of ProductSizeGuideFragment
     * @return ProductSizeGuideFragment
     * @author sergiopereira
     */
    public static ProductSizeGuideFragment newInstance(Bundle bundle) {
        ProductSizeGuideFragment fragment = new ProductSizeGuideFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     * @author sergiopereira
     */
    public ProductSizeGuideFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.product_size_guide_main,
                R.string.size_guide_label,
                KeyboardState.NO_ADJUST_CONTENT,
                ConstantsCheckout.NO_CHECKOUT);
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
        // Get size guide URL from arguments
        mSizeGuideUrl = getArguments().getString(ConstantsIntentExtra.SIZE_GUIDE_URL);
        // Get from saved instance
        if(savedInstanceState != null) mSizeGuideUrl = savedInstanceState.getString(ConstantsIntentExtra.SIZE_GUIDE_URL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
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
     * @see pt.rocket.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
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
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON RESUME");
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
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
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
        Log.i(TAG, "ON SHOW SIZE GUIDE");
        // Load image        
        RocketImageLoader.getInstance().loadImage(url, mImageView, null, R.drawable.no_image_large, new RocketImageLoaderListener() {
            
            @Override
            public void onLoadedSuccess(Bitmap bitmap) { }
            
            @Override
            public void onLoadedError() {
                // Show continue shopping
                showContinueShopping();
            }
            
            @Override
            public void onLoadedCancel(String imageUrl) { }
        });
    }
    
    /**
     * Method used to validate if is necessary show the wizard.
     * @param mWizard
     * @author sergiopereira
     */
    private void showWizard(View mWizard) {
        // Get flag
        boolean isFirsttTime = WizardPreferences.isFirstTime(getBaseActivity(), WizardType.SIZE_GUIDE);
        // Validate
        if(!isFirsttTime) return;
        // Inflate stub view
        mWizard.setVisibility(View.VISIBLE);
        // Set view
        try {
            getView().findViewById(R.id.wizard_product_size_button).setOnClickListener(this);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW RETRY LAYOUT");
        }   
    }

    /**
     * Show continue
     * @author sergiopereira
     */
    private void showContinueShopping() {
        Log.i(TAG, "ON SHOW RETRY LAYOUT");
        showFragmentEmpty(R.string.server_error, android.R.color.transparent, R.string.continue_shopping, this);
    }
    
    /*
     * ############# CLICK LISTENER #############
     */
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // Get view id
        int id = v.getId();
        // Case retry
        if(id == R.id.fragment_root_empty_button) onClickContinueButton();
        // Case wizard
        else if (id == R.id.wizard_product_size_button) onClickWizardButton();
        // Case unknown
        else Log.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
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
            Log.w(TAG, "WARNING: NPE ON HIDE WIZARD", e);
        }
    }

}

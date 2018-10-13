/**
 *
 */
package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bamilo.android.framework.components._unused_.dialogs.WizardPreferences;
import com.bamilo.android.framework.components._unused_.dialogs.WizardPreferences.WizardType;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.framework.service.utils.NetworkConnectivity;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.photoview.PhotoView;
import com.bamilo.android.R;

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get size guide URL from arguments
        mSizeGuideUrl = getArguments().getString(ConstantsIntentExtra.SIZE_GUIDE_URL);
        // Get from saved instance
        if(savedInstanceState != null) mSizeGuideUrl = savedInstanceState.getString(ConstantsIntentExtra.SIZE_GUIDE_URL);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get views
        mImageView = view.findViewById(R.id.product_size_guide_image);
        mWizard = view.findViewById(R.id.product_size_wizard_stub);
        // Validate URL
        if(!TextUtils.isEmpty(mSizeGuideUrl)) {
            showSizeGuide(mImageView, mSizeGuideUrl);
            showWizard(mWizard);
        }
        // Show empty view
        else showContinueShopping();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ConstantsIntentExtra.SIZE_GUIDE_URL, mSizeGuideUrl);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWizard = null;
        mImageView = null;
    }

    /**
     *
     * @param mImageView
     * @param url
     */
    private void showSizeGuide(PhotoView mImageView, String url) {
        ImageManager.getInstance().loadImage(url, mImageView, null, R.drawable.no_image_large, false, new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                    Target<Drawable> target,
                    boolean isFirstResource) {
                showFragmentContentContainer();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                    DataSource dataSource, boolean isFirstResource) {
                showRetryLayout();
                return false;
            }
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
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Case wizard
        if (id == R.id.wizard_product_size_button) onClickWizardButton();
        // Case unknown
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
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onClickContinueButton();
    }
}

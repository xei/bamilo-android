package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.helpers.products.GetProductHelper;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.objects.product.pojo.ProductComplete;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.rest.errors.ErrorCode;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.R;

/**
 * Class that represents the fragment that shows the product information, related to description and key features.
 *
 * @author Paulo Carvalho
 * 
 */
public class ProductDetailsSummaryFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = ProductDetailsSummaryFragment.class.getSimpleName();

    private TextView mProductFeaturesText;
    private TextView mProductDescriptionText;
    private ProductComplete mCompleteProduct;
    private View mainView;
    private String mCompleteProductSku;

    /**
     * Get instance
     */
    public static ProductDetailsSummaryFragment getInstance(Bundle bundle) {
        ProductDetailsSummaryFragment fragment = new ProductDetailsSummaryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public ProductDetailsSummaryFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.product_summary_fragment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        Bundle arguments = getArguments();
        if(arguments != null) {
            Parcelable parcelableProduct = arguments.getParcelable(ConstantsIntentExtra.PRODUCT);
            if(parcelableProduct instanceof ProductComplete){
                mCompleteProduct = (ProductComplete) parcelableProduct;
                if(mCompleteProduct != null){
                    mCompleteProductSku = mCompleteProduct.getSku();
                }
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Validate saved instance
        if(savedInstanceState != null){
            mCompleteProductSku = savedInstanceState.getString(GetProductHelper.SKU_TAG);
        }
        // Load views
        mainView = view;
        getViews();
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

        /**
         * Validate product
         * If null is assumed that the system clean some data
         */
        if (mCompleteProduct != null && mainView != null) {
            getViews();
            displayProductInformation();
        } else if (!TextUtils.isEmpty(mCompleteProductSku)) {
            ContentValues values = new ContentValues();
            values.put(GetProductHelper.SKU_TAG, mCompleteProductSku);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
            triggerContentEvent(new GetProductHelper(), bundle, this);
        } else {
            showFragmentErrorRetry();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(GetProductHelper.SKU_TAG, mCompleteProductSku);
        super.onSaveInstanceState(outState);

    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mainView = null;
        mCompleteProduct = null;
    }
    
    private void getViews(){
        mProductDescriptionText = (TextView) mainView.findViewById(R.id.product_description_text);
        mProductFeaturesText = (TextView) mainView.findViewById(R.id.product_features_text);
    }
    
    private void displayProductInformation() {
        displayFeatures();
        displayDescription();
    }


    /**
     * Displays the features section
     */
    private void displayFeatures() {
        String shortDescription = mCompleteProduct.getShortDescription();
        if (TextUtils.isEmpty(shortDescription)) {
            if(mProductFeaturesText != null){
                mProductFeaturesText.setVisibility(View.GONE);
            }
        } else {
            mProductFeaturesText.setText(shortDescription);

        }
    }

    /**
     * Displays the description section
     */
    private void displayDescription() {
        String longDescription = mCompleteProduct.getDescription();
        //added apires: set long description
        if (TextUtils.isEmpty(longDescription)) {
            if(mProductDescriptionText != null){
                mProductDescriptionText.setVisibility(View.GONE);
            }
        } else {
            //TODO validate if it's to remove or not
            mProductDescriptionText.setText(longDescription);
        }
    }


    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }



    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }

        if (getBaseActivity() == null)
            return;

        super.handleSuccessEvent(baseResponse);
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_PRODUCT_DETAIL:
                if (((ProductComplete) baseResponse.getMetadata().getData()).getName() == null) {
                    getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.product_could_not_retrieved));
                    getActivity().onBackPressed();
                    return;
                } else {
                    mCompleteProduct = (ProductComplete) baseResponse.getContentData();
                    getViews();
                    displayProductInformation();
                    // Waiting for the fragment comunication
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showFragmentContentContainer();
                        }
                    }, 300);
                }

                break;
            default:
                break;
        }
    }




    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }

        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        switch (eventType) {
            case GET_PRODUCT_DETAIL:
                if (!ErrorCode.isNetworkError(errorCode)) {
                    getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.product_could_not_retrieved));
                    showFragmentContentContainer();
                    try {
                        getBaseActivity().onBackPressed();
                    } catch (IllegalStateException e) {
                        getBaseActivity().popBackStackUntilTag(FragmentType.HOME.toString());
                    }
                    return;
                }
            default:
                break;
        }
    }
}

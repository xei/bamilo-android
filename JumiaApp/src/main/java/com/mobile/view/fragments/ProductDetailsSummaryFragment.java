package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.Toast;
import com.mobile.view.R;

/**
 * Class that represents the fragment that shows the product information, related to description and key features.
 *
 * @author Paulo Carvalho
 * 
 */
public class ProductDetailsSummaryFragment extends BaseFragment {

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
        Print.i(TAG, "ON VIEW CREATED");
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
            triggerContentEvent(new GetProductHelper(), bundle, responseCallback);
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
        Print.i(TAG, "ON PAUSE");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.i(TAG, "ON SAVE INSTANCE STATE");
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
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
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
        mainView = null;
        mCompleteProduct = null;
        System.gc();
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
            Print.i(TAG, "shortDescription : empty");
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
            Print.i(TAG, "longDescription : empty");
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
        Print.d(TAG, "RETRY");
        onResume();        
    }
    
    
    
    IResponseCallback responseCallback = new IResponseCallback() {

        @Override
        public void onRequestError(BaseResponse baseResponse) {
            onErrorEvent(baseResponse);
        }

        @Override
        public void onRequestComplete(BaseResponse baseResponse) {
            onSuccessEvent(baseResponse);
        }
    };

    public void onSuccessEvent(BaseResponse baseResponse) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (getBaseActivity() == null)
            return;

        super.handleSuccessEvent(baseResponse);
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "onSuccessEvent: type = " + eventType);
        switch (eventType) {
        case GET_PRODUCT_DETAIL:
            if (((ProductComplete) baseResponse.getMetadata().getData()).getName() == null) {
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return;
            } else {
                mCompleteProduct = (ProductComplete) baseResponse.getMetadata().getData();
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

    public void onErrorEvent(BaseResponse baseResponse) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        EventType eventType = baseResponse.getEventType();
        ErrorCode errorCode = baseResponse.getError().getErrorCode();
        Print.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {

        case GET_PRODUCT_DETAIL:
            if (!errorCode.isNetworkError()) {
                Toast.makeText(getBaseActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();

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

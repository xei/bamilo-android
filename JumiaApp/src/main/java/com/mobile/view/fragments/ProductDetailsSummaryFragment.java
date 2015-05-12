package com.mobile.view.fragments;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.view.R;

import java.util.EnumSet;

import de.akquinet.android.androlog.Log;

/**
 * Class that represents the fragment that shows the product information, related to description and key features.
 *
 * @author Paulo Carvalho
 * 
 */
public class ProductDetailsSummaryFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ProductDetailsSummaryFragment.class);

    private TextView mProductName;
    private TextView mProductPriceSpecial;
    private TextView mProductPriceNormal;
    private RelativeLayout mProductFeaturesContainer;
    private RelativeLayout mProductDescriptionContainer;
    private TextView mProductFeaturesText;
    private TextView mProductDescriptionText;
    private CompleteProduct mCompleteProduct;
    private View mainView;
    private String mCompleteProductUrl;

    /**
     * Get instance
     * 
     * @return
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
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Product,
                R.layout.product_summary_fragment,
                NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
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
        // Retain this fragment across configuration changes.
        Bundle arguments = getArguments();
        if(arguments != null) {
            String url = arguments.getString(ConstantsIntentExtra.CONTENT_URL);
            mCompleteProductUrl = TextUtils.isEmpty(url) ? "" : url;
            Parcelable parcelableProduct = arguments.getParcelable(ConstantsIntentExtra.PRODUCT);
            if(parcelableProduct instanceof CompleteProduct){
                mCompleteProduct = (CompleteProduct) parcelableProduct;
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
        Log.i(TAG, "ON VIEW CREATED");
        // Validate saved instance
        if(savedInstanceState != null){
            mCompleteProductUrl = savedInstanceState.getString(GetProductHelper.PRODUCT_URL);
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

        /**
         * Validate product
         * If null is assumed that the system clean some data
         */
        if(mCompleteProduct != null && mainView != null) {
            getViews();
            displayProductInformation();
        }else{
            if (JumiaApplication.mIsBound && !TextUtils.isEmpty(mCompleteProductUrl)) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
                triggerContentEvent(new GetProductHelper(), bundle, responseCallback);
            } else {
                showFragmentErrorRetry();
            }
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
        Log.i(TAG, "ON PAUSE");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "ON SAVE INSTANCE STATE");
        if(outState != null)
            outState.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
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
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        mainView = null;
        mCompleteProduct = null;
        System.gc();
    }
    
    private void getViews(){
        mProductName = (TextView) mainView.findViewById(R.id.product_detail_name);
        mProductPriceSpecial = (TextView) mainView.findViewById(R.id.product_price_special);
        mProductPriceNormal = (TextView) mainView.findViewById(R.id.product_price_normal);
        mProductFeaturesContainer = (RelativeLayout) mainView.findViewById(R.id.features_container);
        mProductFeaturesText = (TextView) mainView.findViewById(R.id.product_features_text);
        mProductDescriptionContainer = (RelativeLayout) mainView.findViewById(R.id.description_container);
        mProductDescriptionText = (TextView) mainView.findViewById(R.id.product_description_text);
    }
    
    private void displayProductInformation() {
        mProductName.setText(mCompleteProduct.getBrand() + " " + mCompleteProduct.getName());
        displayPriceInformation();
        displayFeatures();
        displayDescription();
        showAtLeastOne();
    }

    /**
     * displays information related to the product price
     */
    private void displayPriceInformation() {
        String unitPrice = mCompleteProduct.getPrice();
        /*--if (unitPrice == null) unitPrice = mCompleteProduct.getMaxPrice();*/
        String specialPrice = mCompleteProduct.getSpecialPrice();
        /*--if (specialPrice == null) specialPrice = mCompleteProduct.getMaxSpecialPrice();*/

        displayPriceInfo(unitPrice, specialPrice);
    }
    
    private void displayPriceInfo(String unitPrice, String specialPrice) {
        /*-if (specialPrice == null && unitPrice == null) {
            mProductPriceNormal.setVisibility(View.GONE);
            mProductPriceSpecial.setVisibility(View.GONE);
        } else*/
        if (specialPrice == null || (unitPrice.equals(specialPrice))) {
            // display only the special price
            mProductPriceSpecial.setText(unitPrice);
            mProductPriceNormal.setVisibility(View.GONE);
        } else {
            // display special and normal price
            mProductPriceSpecial.setText(specialPrice);
            mProductPriceNormal.setText(unitPrice);
            mProductPriceNormal.setVisibility(View.VISIBLE);
            mProductPriceNormal.setPaintFlags(mProductPriceNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    /**
     * Displays the features section
     */
    private void displayFeatures() {
        String shortDescription = mCompleteProduct.getShortDescription();
        // Don't show the features box if there is no content for it
        if (TextUtils.isEmpty(shortDescription)) {
            Log.i(TAG, "shortDescription : empty");
            if(mProductFeaturesContainer != null){
                mProductFeaturesContainer.setVisibility(View.GONE);
            }
        } else {
            mProductFeaturesContainer.setVisibility(View.VISIBLE);

            //TODO validate if it's to remove or not
//        String translatedDescription = shortDescription.replace("\r", "<br>");
//            Log.d(TAG, "displayFeatures: *" + translatedDescription + "*");
//
//        Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
//        // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with bold tags
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//            Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);
//            MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(), MetricAffectingSpan.class);
//            for (MetricAffectingSpan span : spans) {
//                htmlText.removeSpan(span);
//            }
//        }
//        mProductFeaturesText.setText(htmlText);
            mProductFeaturesText.setText(shortDescription);
        }
    }

    /**
     * Displays the description section
     */
    private void displayDescription() {
        String longDescription = mCompleteProduct.getDescription();


        if (TextUtils.isEmpty(longDescription)) {
            Log.i(TAG, "longDescription : empty");
            if(mProductDescriptionContainer != null){
                mProductDescriptionContainer.setVisibility(View.GONE);
            }
        } else {
            //TODO validate if it's to remove or not
//            String translatedDescription = longDescription.replace("\r", "<br>");
//            Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
//            // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with bold tags
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);
//                MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(), MetricAffectingSpan.class);
//                for (MetricAffectingSpan span: spans) {
//                    htmlText.removeSpan(span);
//                }
//            }
//            mProductDescriptionText.setText(htmlText);
            mProductDescriptionText.setText(longDescription);
        }
    }

    /**
     * show at least a empty box if both fields don't come from the API
     */
    private void showAtLeastOne(){
        if(mProductDescriptionContainer!=null && !mProductDescriptionContainer.isShown() && mProductFeaturesContainer!=null && !mProductFeaturesContainer.isShown()){
            mProductDescriptionContainer.setVisibility(View.VISIBLE);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Log.d(TAG,"RETRY");
        onResume();        
    };
    
    
    
    IResponseCallback responseCallback = new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };

    public void onSuccessEvent(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (getBaseActivity() == null)
            return;

        super.handleSuccessEvent(bundle);
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.d(TAG, "onSuccessEvent: type = " + eventType);
        switch (eventType) {
        case GET_PRODUCT_EVENT:
            if (((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return;
            } else {
                mCompleteProduct = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
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

    public void onErrorEvent(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (super.handleErrorEvent(bundle)) {
            return;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {

        case GET_PRODUCT_EVENT:
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
/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.customfontviews.TextView;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.products.GetProductHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.Toast;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class ProductDetailsDescriptionFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(ProductDetailsDescriptionFragment.class);

    private static ProductDetailsDescriptionFragment sProductDetailsDescriptionFragment;

    private TextView mProductName;
    private TextView mProductPriceSpecial;
    private TextView mProductPriceNormal;
    private RelativeLayout mProductFeaturesContainer;
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
    public static ProductDetailsDescriptionFragment getInstance(Bundle bundle) {
        sProductDetailsDescriptionFragment = new ProductDetailsDescriptionFragment();
        String contentUrl = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
        sProductDetailsDescriptionFragment.mCompleteProductUrl = contentUrl != null ? contentUrl : "";
        return sProductDetailsDescriptionFragment;
    }

    /**
     * Empty constructor
     */
    public ProductDetailsDescriptionFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.product_description_fragment,
                0,
                KeyboardState.NO_ADJUST_CONTENT);
        // super(IS_NESTED_FRAGMENT, R.layout.product_description_fragment);
        // R.string.product_details_title
        this.mCompleteProduct = JumiaApplication.INSTANCE.getCurrentProduct();
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
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        mainView = view;
        
        getViews();
        if(savedInstanceState != null){
            mCompleteProductUrl = savedInstanceState.getString(GetProductHelper.PRODUCT_URL);
        }
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
        mCompleteProduct = JumiaApplication.INSTANCE.getCurrentProduct();
        /**
         * Validate product
         * If null is assumed that the system clean some data
         */
        if(mCompleteProduct != null && mainView != null) {
            getViews();
            displayProductInformation(mainView);
        }else{
            if (JumiaApplication.mIsBound && !TextUtils.isEmpty(mCompleteProductUrl)) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
                triggerContentEvent(new GetProductHelper(), bundle, responseCallback);
            } else {
                showFragmentRetry(this);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(outState != null)
            outState.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
        super.onSaveInstanceState(outState);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
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
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
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
        mProductDescriptionText = (TextView) mainView.findViewById(R.id.product_description_text);
    }
    
    private void displayProductInformation(View view ) {
        mProductName.setText( mCompleteProduct.getBrand() + " " + mCompleteProduct.getName());
        displayPriceInformation();
        displaySpecification();
        displayDescription();
    }
    
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
    
    private void displaySpecification() {
        String shortDescription = mCompleteProduct.getShortDescription();
        // Don't show the features box if there is no content for it
        if (TextUtils.isEmpty(shortDescription)) {
            Log.i(TAG, "shortDescription : empty");
            if(mProductFeaturesContainer!=null){
                mProductFeaturesContainer.setVisibility(View.GONE);
            }
            return;
        } else {
            mProductFeaturesContainer.setVisibility(View.VISIBLE);
        
        String translatedDescription = shortDescription.replace("\r", "<br>");
        Log.d(TAG, "displaySpecification: *" + translatedDescription + "*");
        
        Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
        // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with bold tags
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);
            MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(), MetricAffectingSpan.class);
            for (MetricAffectingSpan span : spans) {
                htmlText.removeSpan(span);
            }
        }
        mProductFeaturesText.setText(htmlText);
        
//        mProductFeaturesText.setText(Html.fromHtml(translatedDescription));
        }
    }
    
    private void displayDescription() {
        String longDescription = mCompleteProduct.getDescription();
        String translatedDescription = longDescription.replace("\r", "<br>");
        Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
        // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with bold tags
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);                
            MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(), MetricAffectingSpan.class);
            for (MetricAffectingSpan span: spans) {
                htmlText.removeSpan(span);                
            }
        }
        mProductDescriptionText.setText(htmlText);
        
//        mProductDescriptionText.setText( Html.fromHtml(translatedDescription));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.fragment_root_retry_button){
            Log.d(TAG,"RETRY");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DESCRIPTION, getArguments(), FragmentController.ADD_TO_BACK_STACK);
        }
    }
    
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

        getBaseActivity().handleSuccessEvent(bundle);
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.d(TAG, "onSuccessEvent: type = " + eventType);
        switch (eventType) {
        case GET_PRODUCT_EVENT:
            if (((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return;
            } else {
                mCompleteProduct = (CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                getViews();
                displayProductInformation(mainView);   
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

        if (getBaseActivity().handleErrorEvent(bundle)) {
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

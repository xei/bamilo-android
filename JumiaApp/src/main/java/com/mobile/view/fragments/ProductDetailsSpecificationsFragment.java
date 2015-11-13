package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.objects.product.pojo.ProductSpecification;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.Toast;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * Class that represents the fragment that shows the product specifications.
 *
 * @author Paulo Carvalho
 * 
 */
public class ProductDetailsSpecificationsFragment extends BaseFragment {

    private static final String TAG = ProductDetailsSpecificationsFragment.class.getSimpleName();

    private LinearLayout mProductSpecsContainer;
    private ProductComplete mCompleteProduct;
    private View mainView;
    private String mCompleteProductSku;
    private ArrayList<ProductSpecification> mProductSpecifications;
    private LayoutInflater inflater;

    /**
     * Get instance
     */
    public static ProductDetailsSpecificationsFragment getInstance(Bundle bundle) {
        ProductDetailsSpecificationsFragment fragment = new ProductDetailsSpecificationsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public ProductDetailsSpecificationsFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.product_specs_fragment);
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
        inflater = LayoutInflater.from(getBaseActivity());
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
            displaySpecification();
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
    }
    
    private void getViews(){
        mProductSpecsContainer = (LinearLayout) mainView.findViewById(R.id.specs_main_container);
    }

    /**
     * Display the product specifications
     */
    private void displaySpecification() {

        if(CollectionUtils.isEmpty(mProductSpecifications)){
            mProductSpecifications = mCompleteProduct.getProductSpecifications();
        }

        if(!CollectionUtils.isEmpty(mProductSpecifications)){
            if(mProductSpecsContainer != null){
                mProductSpecsContainer.removeAllViews();
            }
            for (ProductSpecification productSpecification : mProductSpecifications) {
                addSpecTable(productSpecification);
            }
        }
    }

    /**
     * Add specification table to the specification list
     */
    private void addSpecTable(ProductSpecification productSpecification){

        View theInflatedView = inflater.inflate(R.layout.product_specs_container, mProductSpecsContainer, false);
        TextView specHeader = (TextView) theInflatedView.findViewById(R.id.HeaderSpecs);
        LinearLayout specsList = (LinearLayout) theInflatedView.findViewById(R.id.specs_container_list);

        HashMap<String,String> specsMap = productSpecification.getSpecifications();

        if(specsMap != null && specsMap.size() > 0){
            specHeader.setText(productSpecification.getTitle().toUpperCase());
            try {
                Iterator it = specsMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    addSpecTableRow(pair, specsList);
                }

                mProductSpecsContainer.addView(theInflatedView);
            } catch (ConcurrentModificationException exception){
                theInflatedView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Add a key/value row to the specification table
     * @param pair, key/value of the table
     * @param parent
     */
    private void addSpecTableRow(Map.Entry pair, final LinearLayout parent){
        View theInflatedView = inflater.inflate(R.layout._def_product_specs_container_item, parent, false);
        TextView specKey = (TextView) theInflatedView.findViewById(R.id.specs_item_key);
        TextView specValue = (TextView) theInflatedView.findViewById(R.id.specs_item_value);

        specKey.setText(pair.getKey().toString());
        specValue.setText(pair.getValue().toString());
            View separator = createSeparator();
            parent.addView(theInflatedView);
            parent.addView(separator);
    }


    /**
     * Create a separator line
     */
    private View createSeparator()
    {
        View view = new View(getBaseActivity().getApplicationContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2);
        view.setLayoutParams(params);
        view.setBackgroundColor(getResources().getColor(R.color.black_400));
        return view;

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
                displaySpecification();
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

/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.OffersListAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.Errors;
import com.mobile.framework.objects.Offer;
import com.mobile.framework.objects.ProductOffers;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.cart.GetShoppingCartAddItemHelper;
import com.mobile.helpers.products.GetProductOffersHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * Class used to show the product offers
 * @author Paulo Carvalho
 */
public class ProductOffersFragment extends BaseFragment implements OnClickListener, OffersListAdapter.IOffersAdapterService, android.widget.AdapterView.OnItemClickListener {

    private static final String TAG = LogTagHelper.create(ProductOffersFragment.class);
    
    private String mCompleteProductUrl;

    private String mCompleteProductName;
    
    private ProductOffers productOffers;
    
    private static final String PRODUCT_OFFERS = "product_offers";
    
    private TextView mProductName;
    
    private TextView mOffersCount;
    
    private TextView mOffersMinPrice;
    
    private GridView mOffersList;
    
    private DialogFragment mDialogAddedToCart;
    

    public static ProductOffersFragment newInstance(Bundle bundle) {
        ProductOffersFragment fragment = new ProductOffersFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    public ProductOffersFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Offers,
                R.layout.product_offers_main,
                0,
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
        // Get product URL from arguments
        mCompleteProductUrl = getArguments().getString(ConstantsIntentExtra.CONTENT_URL);
        // Get product name from arguments
        mCompleteProductName = getArguments().getString(ConstantsIntentExtra.PRODUCT_NAME);
        // Get from saved instance
        if(savedInstanceState != null){
            mCompleteProductUrl = savedInstanceState.getString(ConstantsIntentExtra.CONTENT_URL);
            mCompleteProductName = savedInstanceState.getString(ConstantsIntentExtra.PRODUCT_NAME);
            productOffers = savedInstanceState.getParcelable(PRODUCT_OFFERS);
        }
            
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
        Log.i(TAG, "ON VIEW CREATED");
        // Get views
        mProductName = (TextView) view.findViewById(R.id.offer_product_name);
        mOffersCount = (TextView) view.findViewById(R.id.offer_product_count);
        mOffersMinPrice = (TextView) view.findViewById(R.id.offer_product_min_price);
        mOffersList = (GridView) view.findViewById(R.id.offers_list);
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
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
        Bundle arg = new Bundle();
        if(productOffers == null){
            arg.putString(GetProductOffersHelper.PRODUCT_URL, mCompleteProductUrl);
            triggerContentEvent(new GetProductOffersHelper(), arg, responseCallback);
        } else {
            setAppContent();
        }

    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProductUrl);
        outState.putString(ConstantsIntentExtra.PRODUCT_NAME, mCompleteProductName);
        if(productOffers != null && productOffers.getOffers().size() > 0){
            outState.putParcelable(PRODUCT_OFFERS, productOffers);
        }
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
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
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
    }
    
    /*
     * ############# LAYOUT #############
     */
    
    private void setAppContent(){
        
        mProductName.setText(mCompleteProductName);
        mOffersCount.setText(""+productOffers.getTotalOffers());
        mOffersMinPrice.setText(""+productOffers.getMinPriceOffer());
        // set the number of grid columns depending on the screen size    
        int numColumns = getBaseActivity().getResources().getInteger(R.integer.catalog_list_num_columns);
        mOffersList.setNumColumns(numColumns);
        
        OffersListAdapter offersAdapter = new OffersListAdapter(getActivity().getApplicationContext(), productOffers.getOffers(), this);
        
        mOffersList.setAdapter(offersAdapter);
        mOffersList.setOnItemClickListener(this);
    }

    private void orderOffersByLowerPrice(ProductOffers productOffersArray){
        if(productOffersArray != null){
            ArrayList<Offer> offers = productOffersArray.getOffers();
            if(offers != null && offers.size() > 0){
                Collections.sort(offers, new CustomComparator());
                productOffers.setOffers(offers); 
          }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "ON ITEM CLICK");
        Offer offer = productOffers.getOffers().get(position);
        if(offer.getSeller() != null){
            Bundle bundle = new Bundle();
            String targetUrl = offer.getSeller().getUrl();
            String targetTitle = offer.getSeller().getName();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetTitle);
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
            //bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaser_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, targetUrl);
            getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, true);
        }

    }

    public class CustomComparator implements Comparator<Offer> {
        @Override
        public int compare(Offer o1, Offer o2) {
            return ((Double)o1.getPriceOfferDouble()).compareTo((Double)o2.getPriceOfferDouble());
        }
    }
    
    
    /*
     * ############# LISTENERS #############
     */
    
    
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
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        if (getBaseActivity() == null) return;
        
        super.handleSuccessEvent(bundle);
        
        switch (eventType) {
        case GET_PRODUCT_OFFERS:
            productOffers = (ProductOffers) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            orderOffersByLowerPrice(productOffers);
            setAppContent();
            showFragmentContentContainer();
            hideActivityProgress();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            getBaseActivity().updateCartInfo();
            hideActivityProgress();
            showFragmentContentContainer();
//            mAddToCartButton.setEnabled(true);
            executeAddToShoppingCartCompleted();
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
        hideActivityProgress();
        
        if (super.handleErrorEvent(bundle)) {
            return;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
        case GET_PRODUCT_OFFERS:
            hideActivityProgress();
            showFragmentContentContainer();
            showFragmentNoNetworkRetry(EventType.GET_PRODUCT_OFFERS);
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
//            mBundleButton.setEnabled(true);
//            isAddingProductToCart = false;
            hideActivityProgress();
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle
                        .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);

                if (errorMessages != null) {
                    int titleRes = R.string.error_add_to_cart_failed;
                    int msgRes = -1;

                    String message = null;
                    if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_SOLD_OUT)) {
                        msgRes = R.string.product_outof_stock;
                    } else if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_PRODUCT_ADD_OVERQUANTITY)) {
                        msgRes = R.string.error_add_to_shopping_cart_quantity;
                    } else if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_ERROR_ADDING)) {
                        List<String> validateMessages = errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
                        if (validateMessages != null && validateMessages.size() > 0) {
                            message = validateMessages.get(0);
                        } else {
                            msgRes = R.string.error_add_to_cart_failed;
                        }
                    }

                    if (msgRes != -1) {
                        message = getString(msgRes);
                    } else if (message == null) {
                        return;
                    }

                    FragmentManager fm = getFragmentManager();
                    dialog = DialogGenericFragment.newInstance(true, true, false,
                            getString(titleRes),
                            message,
                            getString(R.string.ok_label), "", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    int id = v.getId();
                                    if (id == R.id.button1) {
                                        dismissDialogFragment();
                                    }
                                }
                            });
                    dialog.show(fm, null);
                    return;
                }
            }
            if (!errorCode.isNetworkError()) {
                addToShoppingCartFailed();
                return;
            }
            break;
        default:
            break;
        }
    }
    
    
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
        // Case unknown
        else Log.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
    }

  

    
    @Override
    protected void onRetryRequest(EventType eventType) {
        Log.i(TAG, "ON RETRY REQUEST");
    }

    @Override
    public void onAddOfferToCart(Offer offer) {
        // Add one unity to cart 
        triggerAddItemToCart(offer.getSku(), offer.getSimpleSku(),offer.getPriceForTracking());
        
    }
    
    private void executeAddToShoppingCartCompleted() {

        String msgText = "1 " + getResources().getString(R.string.added_to_shop_cart_dialog_text);

        mDialogAddedToCart = DialogGenericFragment.newInstance(
                false,
                false,
                true,
                getString(R.string.your_cart),
                msgText,
                getString(R.string.go_to_cart), getString(R.string.continue_shopping),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            if (getBaseActivity() != null) {
                                getBaseActivity().onSwitchFragment(
                                        FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE,
                                        FragmentController.ADD_TO_BACK_STACK);
                            }
                            if (mDialogAddedToCart != null) {
                                mDialogAddedToCart.dismiss();
                            }

                        } else if (id == R.id.button2) {
                            showFragmentContentContainer();
                            mDialogAddedToCart.dismiss();
                        }
                    }
                });

        mDialogAddedToCart.show(getFragmentManager(), null);
    }
    
    private void addToShoppingCartFailed() {
        mDialogAddedToCart = DialogGenericFragment.newInstance(false, false, true, null,
                getResources().getString(R.string.error_add_to_shopping_cart), getResources()
                        .getString(R.string.ok_label), "", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            mDialogAddedToCart.dismiss();
                        } else if (id == R.id.button2) {

                        }
                    }
                });

        mDialogAddedToCart.show(getFragmentManager(), null);
    }
    
   
    
    private void triggerAddItemToCart(String sku, String simpleSKU, double price) {
        ContentValues values = new ContentValues();
        values.put("p", sku);
        values.put("sku", simpleSKU);
        values.put("quantity", "1");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);
        triggerContentEventProgress(new GetShoppingCartAddItemHelper(), bundle, responseCallback);
        
        // GA OFFER TRACKING              
        Log.d("TRACK","SIMLPE SKU:"+simpleSKU+" PRICE:"+price);
        TrackerDelegator.trackAddOfferToCart(simpleSKU,price);
        
    }
}

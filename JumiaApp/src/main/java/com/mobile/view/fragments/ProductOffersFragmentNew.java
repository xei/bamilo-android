package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.OffersListAdapterNew;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.products.GetProductOffersHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.product.OfferList;
import com.mobile.newFramework.objects.product.pojo.ProductOffer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.ErrorConstants;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.Map;

/**
 * Class used to show the product offers
 * @author Paulo Carvalho
 * @modified sergiopereira
 */
public class ProductOffersFragmentNew extends BaseFragment implements OffersListAdapterNew.IOffersAdapterService, AdapterView.OnItemClickListener, IResponseCallback, DialogSimpleListFragment.OnDialogListListener {

    private static final String TAG = ProductOffersFragmentNew.class.getSimpleName();

    private String mCompleteProductSku;

    private String mCompleteProductName;

    private String mCompleteBrand;

    private OfferList productOffers;

    private static final String PRODUCT_OFFERS = "product_offers";

    private TextView mProductName;

    private TextView mProductBrand;

    private GridView mOffersList;

    private ProductOffer offerAddToCart;

    /**
     * Get a new instance of {@link #ProductOffersFragmentNew}.
     * @param bundle The arguments
     * @return ProductOffersFragment
     */
    public static ProductOffersFragmentNew newInstance(Bundle bundle) {
        ProductOffersFragmentNew fragment = new ProductOffersFragmentNew();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public ProductOffersFragmentNew() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.OFFERS,
                R.layout.product_offers_main_new,
                R.string.other_sellers,
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

        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        // Get data
        mCompleteProductSku = arguments.getString(ConstantsIntentExtra.CONTENT_ID);
        mCompleteProductName = arguments.getString(ConstantsIntentExtra.PRODUCT_NAME);
        mCompleteBrand = arguments.getString(ConstantsIntentExtra.PRODUCT_BRAND);

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
        mProductName = (TextView) view.findViewById(R.id.offer_product_name);
        mProductBrand = (TextView) view.findViewById(R.id.offer_product_brand);
        mOffersList = (GridView) view.findViewById(R.id.offers_list);
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
        if (productOffers == null && mCompleteProductSku != null) {
            triggerGetProductOffers(mCompleteProductSku);
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
        outState.putString(ConstantsIntentExtra.CONTENT_ID, mCompleteProductSku);
        outState.putString(ConstantsIntentExtra.PRODUCT_NAME, mCompleteProductName);
        if(productOffers != null && CollectionUtils.isEmpty(productOffers.getOffers())){
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
    }
    
    /*
     * ############# LAYOUT #############
     */

    /**
     * Show content
     */
    private void setAppContent(){
        mProductName.setText(mCompleteProductName);
        mProductBrand.setText(mCompleteBrand);
        OffersListAdapterNew offersAdapter = new OffersListAdapterNew(getActivity().getApplicationContext(),productOffers.getOffers(), this);
        mOffersList.setAdapter(offersAdapter);
        mOffersList.setOnItemClickListener(this);
    }



    /*
     * ############# TRIGGERS #############
     */

    private void triggerGetProductOffers(String sku) {

        triggerContentEvent(new GetProductOffersHelper(), GetProductOffersHelper.createBundle(sku), this);
    }

    /**
     * Trigger to add an item to cart
     * @param sku The sku
     * @param simpleSKU The simple sku
     * @param price The price
     */
    private void triggerAddItemToCart(String sku, String simpleSKU, double price) {
        // GA OFFER TRACKING
        Print.d(TAG, "SIMLPE SKU:" + simpleSKU + " PRICE:" + price);
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), ShoppingCartAddItemHelper.createBundle(sku, simpleSKU), this);
        TrackerDelegator.trackAddOfferToCart(simpleSKU, price);
    }
    
    /*
     * ############# RESPONSE #############
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        if (getBaseActivity() == null) return;
        
        super.handleSuccessEvent(baseResponse);
        
        switch (eventType) {
        case GET_PRODUCT_OFFERS:
            productOffers = (OfferList)baseResponse.getMetadata().getData();
            setAppContent();
            showFragmentContentContainer();
            hideActivityProgress();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            getBaseActivity().updateCartInfo();
            hideActivityProgress();
            showFragmentContentContainer();
            showAddToCartCompleteMessage(baseResponse);
            break;
        default:
            break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        hideActivityProgress();
        
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
        case GET_PRODUCT_OFFERS:
            hideActivityProgress();
            showFragmentContentContainer();
            showFragmentErrorRetry();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            hideActivityProgress();
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                Map errorMessages = baseResponse.getErrorMessages();
                if (errorMessages != null) {
                    String message = null;
                    if (errorMessages.containsKey(ErrorConstants.ORDER_PRODUCT_SOLD_OUT)) {
                        message = getString(R.string.product_outof_stock);
                    } else if (errorMessages.containsKey(ErrorConstants.PRODUCT_ADD_OVER_QUANTITY)) {
                        message = getString(R.string.error_add_to_shopping_cart_quantity);
                    } else if (errorMessages.containsKey(ErrorConstants.ORDER_PRODUCT_ERROR_ADDING)) {
                        message = getString(R.string.error_add_to_cart_failed);
                    }

                    if (message == null) {
                        return;
                    }

                    FragmentManager fm = getFragmentManager();
                    dialog = DialogGenericFragment.newInstance(true, false,
                            getString(R.string.error_add_to_cart_failed),
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
            if (!ErrorCode.isNetworkError(errorCode)) {
                addToShoppingCartFailed();
                return;
            }
            break;
        default:
            break;
        }
    }

    /*
     * ############# LISTENERS #############
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Print.d(TAG, "ON ITEM CLICK");
        ProductOffer offer = productOffers.getOffers().get(position);
        if(offer.getSeller() != null){
            Bundle bundle = new Bundle();
            String targetUrl = offer.getSeller().getUrl();
            String targetTitle = offer.getSeller().getName();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetTitle);
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, targetUrl);
            getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, true);
        }
    }


    @Override
    public void onAddOfferToCart(ProductOffer offer) {
        // Add one unity to cart
        if(!offer.hasSelectedSimpleVariation()){
            offerAddToCart = offer;
            onClickVariation(offer);

        } else {
            triggerAddItemToCart(offer.getSku(), offer.getSelectedSimple().getSku(), offer.getFinalPrice());
        }
    }

    @Override
    public void onClickVariation(ProductOffer offer) {
        Print.i(TAG, "ON CLICK TO SHOW VARIATION LIST");
        try {
            DialogSimpleListFragment dialog = DialogSimpleListFragment.newInstance(
                    getBaseActivity(),
                    getString(R.string.product_variance_choose),
                    offer,
                    this);
            dialog.show(getFragmentManager(), null);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON SHOW VARIATIONS DIALOG");
        }
    }

    private void executeAddToShoppingCartCompleted() {
        super.showInfoAddToShoppingCartCompleted();

    }
    
    private void addToShoppingCartFailed() {
        super.showInfoAddToShoppingCartFailed() ;

    }

    @Override
    public void onDialogListItemSelect(int position) {
        if(offerAddToCart != null){
            onAddOfferToCart(offerAddToCart);
            offerAddToCart = null;
        }
    }

    @Override
    public void onDialogListClickView(View view) {

    }

    @Override
    public void onDialogListDismiss() {
        ListAdapter listAdapter = mOffersList.getAdapter();
        if(listAdapter instanceof OffersListAdapterNew){
            ((OffersListAdapterNew) listAdapter).notifyDataSetChanged();
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        onResume();
    }
}

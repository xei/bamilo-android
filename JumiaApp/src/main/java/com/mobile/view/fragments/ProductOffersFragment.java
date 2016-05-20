package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.DividerItemDecoration;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.OffersListAdapter;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.products.GetProductOffersHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.campaign.CampaignItem;
import com.mobile.newFramework.objects.product.OfferList;
import com.mobile.newFramework.objects.product.pojo.ProductOffer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.catalog.HeaderFooterGridView;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Class used to show the product offers
 * @author Paulo Carvalho
 * @modified sergiopereira
 */
public class ProductOffersFragment extends BaseFragment implements OffersListAdapter.IOffersAdapterService, AdapterView.OnItemClickListener, IResponseCallback, DialogSimpleListFragment.OnDialogListListener {

    private static final String TAG = ProductOffersFragment.class.getSimpleName();

    private String mCompleteProductSku;

    private String mCompleteProductName;

    private String mCompleteBrand;

    private OfferList productOffers;

    private static final String PRODUCT_OFFERS = "product_offers";

    private TextView mProductName;

    private TextView mProductBrand;

    private HeaderFooterGridView mOffersList;

    private ProductOffer offerAddToCart;

    /**
     * Empty constructor
     */
    public ProductOffersFragment() {
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
        mOffersList = (HeaderFooterGridView) view.findViewById(R.id.offers_list);
        mOffersList.setNestedScrollingEnabled(false);
        mOffersList.setHasFixedSize(true);
        mOffersList.setGridLayoutManager(getResources().getInteger(R.integer.favourite_num_columns));
        mOffersList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mOffersList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));
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
        OffersListAdapter offersAdapter = new OffersListAdapter(getActivity().getApplicationContext(),productOffers.getOffers(), this);
        mOffersList.setAdapter(offersAdapter);
        mOffersList.setOnClickListener(this);
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
     * @param price The price
     */
    private void triggerAddItemToCart(String sku, double price) {
        // GA OFFER TRACKING
        Print.d(TAG, "SIMLPE SKU:" + sku + " PRICE:" + price);
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), ShoppingCartAddItemHelper.createBundle(sku), this);
        TrackerDelegator.trackAddOfferToCart(sku, price);
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
            productOffers = (OfferList)baseResponse.getContentData();
            setAppContent();
            showFragmentContentContainer();
            hideActivityProgress();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            getBaseActivity().updateCartInfo();
            hideActivityProgress();
            showFragmentContentContainer();
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
        // Hide progress
        hideActivityProgress();
        // Super
        if (super.handleErrorEvent(baseResponse)) return;
        // Validate type
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
        case GET_PRODUCT_OFFERS:
            showFragmentContentContainer();
            showFragmentErrorRetry();
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
            String targetUrl = offer.getSeller().getTarget();
            String targetTitle = offer.getSeller().getName();
            bundle.putString(ConstantsIntentExtra.DATA, targetUrl);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetTitle);
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, targetUrl);
            getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, true);
        }
    }


    @Override
    public void onAddOfferToCart(ProductOffer offer) {
        // Validate simple
        if(!offer.hasSelectedSimpleVariation()){
            offerAddToCart = offer;
            onClickVariation(offer);
        }
        // Add simple
        else if(offer.getSelectedSimple() != null) {
            triggerAddItemToCart(offer.getSelectedSimple().getSku(), offer.getFinalPrice());
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
    public void onDialogSizeListClickView(int position, CampaignItem item) {

    }

    @Override
    public void onDialogListDismiss() {
       mOffersList.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onClickRetryButton(View view) {
        onResume();
    }
}

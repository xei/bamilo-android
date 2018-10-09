package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.TextView;
import com.bamilo.android.framework.components.recycler.DividerItemDecoration;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.OffersListAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ShoppingCartAddItemHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.products.GetProductOffersHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.objects.campaign.CampaignItem;
import com.bamilo.android.framework.service.objects.product.OfferList;
import com.bamilo.android.framework.service.objects.product.pojo.ProductOffer;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.HeaderFooterGridView;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogSimpleListFragment;
import com.bamilo.android.R;

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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        // Get views
        mProductName = view.findViewById(R.id.offer_product_name);
        mProductBrand = view.findViewById(R.id.offer_product_brand);
        mOffersList = view.findViewById(R.id.offers_list);
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
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
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), ShoppingCartAddItemHelper.createBundle(sku), this);
//        TrackerDelegator.trackAddOfferToCart(sku, price);
    }
    
    /*
     * ############# RESPONSE #############
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();

        // Validate fragment visibility
        if (isOnStoppingProcess) {
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
            return;
        }
        // Hide progress
        hideActivityProgress();
        // Super
        if (super.handleErrorEvent(baseResponse)) return;
        // Validate type
        EventType eventType = baseResponse.getEventType();
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
        try {
            DialogSimpleListFragment dialog = DialogSimpleListFragment.newInstance(
                    getBaseActivity(),
                    getString(R.string.product_variance_choose),
                    offer,
                    this);
            dialog.show(getFragmentManager(), null);
        } catch (NullPointerException e) {
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

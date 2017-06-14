package com.mobile.view.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emarsys.predict.RecommendedItem;
import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.HorizontalListView;
import com.mobile.components.recycler.VerticalSpaceItemDecoration;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.EventConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.EventFactory;
import com.mobile.helpers.cart.CartHelper;
import com.mobile.helpers.cart.ClearShoppingCartHelper;
import com.mobile.helpers.teasers.GetRichRelevanceHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.extlibraries.emarsys.predict.recommended.Item;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendListCompletionHandler;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendManager;
import com.mobile.managers.TrackerManager;
import com.mobile.service.objects.cart.PurchaseCartItem;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.objects.product.RichRelevance;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.emarsys.EmarsysTracker;
import com.mobile.utils.home.holder.HomeRecommendationsGridTeaserHolder;
import com.mobile.utils.home.holder.RecommendationsCartHolder;
import com.mobile.utils.home.holder.RecommendationsHolder;
import com.mobile.utils.home.holder.RichRelevanceAdapter;
import com.mobile.utils.pushwoosh.PushWooshTracker;
import com.mobile.utils.pushwoosh.PushwooshCounter;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;
import com.pushwoosh.PushManager;
import com.pushwoosh.SendPushTagsCallBack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sergiopereira
 *
 */
public class CheckoutThanksFragment extends BaseFragment implements IResponseCallback, TargetLink.OnAppendDataListener {

    private static final String TAG = CheckoutThanksFragment.class.getSimpleName();

    private String mOrderShipping;

    private String mOrderTax;

    private String mPaymentMethod;

    private double mGrandTotalValue;

    private String mOrderNumber;

    private RichRelevance mRichRelevance;

    private ViewGroup mRelatedProductsView;

    private String mRelatedRichRelevanceHash;

    private static final int ITEMS_MARGIN = 6;
    RecommendManager recommendManager;
    RecommendationsCartHolder recommendationsHolder;
    private String itemsId="";

    private PurchaseEntity oldCart = null;
    /**
     * Empty constructor
     */
    public CheckoutThanksFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.CHECKOUT,
                R.layout.checkout_thanks,
                R.string.checkout_label,
                NO_ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_THANKS);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get values
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if(arguments != null){
            mOrderNumber = getArguments().getString(RestConstants.ORDER_NUMBER);
            mOrderShipping = getArguments().getString(RestConstants.TRANSACTION_SHIPPING);
            mOrderTax = getArguments().getString(RestConstants.TRANSACTION_TAX);
            mPaymentMethod = getArguments().getString(RestConstants.PAYMENT_METHOD);
            mGrandTotalValue = getArguments().getDouble(RestConstants.ORDER_GRAND_TOTAL);
            if(getArguments().containsKey(RestConstants.RECOMMENDED_PRODUCTS)) {
                mRichRelevance = getArguments().getParcelable(RestConstants.RECOMMENDED_PRODUCTS);
            }
        }
        recommendManager = new RecommendManager();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        prepareLayout(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        TrackerDelegator.trackPage(TrackingPage.CHECKOUT_THANKS, getLoadTime(), false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RestConstants.ORDER_NUMBER, mOrderNumber);
        outState.putString(RestConstants.TRANSACTION_SHIPPING, mOrderShipping);
        outState.putString(RestConstants.TRANSACTION_TAX, mOrderTax);
        outState.putString(RestConstants.PAYMENT_METHOD, mPaymentMethod);
        outState.putDouble(RestConstants.ORDER_GRAND_TOTAL, mGrandTotalValue);
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    /**
     * Show content
     */
    private void prepareLayout(View view) {
        // Track purchase
        PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
        if (cart == null) {
            if (oldCart == null) {
                cart = new PurchaseEntity();
            } else {
                cart = oldCart;
            }
        }
        TrackerDelegator.trackPurchaseInCheckoutThanks(cart, mOrderNumber, mGrandTotalValue, mOrderShipping, mOrderTax, mPaymentMethod);
        ArrayList<PurchaseCartItem> carts=  cart.getCartItems();
        String categories = "";
        if (carts != null) {
            for (PurchaseCartItem cat : carts) {
                categories += cat.getCategories();
                itemsId += cat.getSku() + ",";
            }
        }
        TrackerManager.postEvent(getBaseActivity(), EventConstants.Purchase, EventFactory.purchase(categories, (long)cart.getTotal(), true));

        //sendRecommend();

        // Related Products
        mRelatedProductsView = (ViewGroup) view.findViewById(R.id.pdv_related_container);
        ImageView imageSuccess = (ImageView) view.findViewById(R.id.success_image);
        /**
         * Mirror image to avoid having extra assets for each resolution for RTL.
         */
        UIUtils.mirrorViewForRTL(imageSuccess);
        // Show
        //setRelatedItems();
        sendRecommend(cart);
        oldCart = cart;
        // Clean cart
        triggerClearCart();

        recommendManager.sendPurchaseRecommend();
        BamiloApplication.INSTANCE.setCart(null);

        // Update cart info
        getBaseActivity().updateCartInfo();
        // Continue button
        view.findViewById(R.id.btn_checkout_continue).setOnClickListener(this);
        // Add a link to order status
        setOrderStatusLink(view, mOrderNumber);
        PushwooshCounter.setPurchaseCount();
        HashMap<String, Object> open_count = new HashMap<>();
        open_count.put("PurchaseCount",PushwooshCounter.getPurchaseCount());
        SendPushTagsCallBack callBack = new SendPushTagsCallBack() {
            @Override
            public void taskStarted() {

            }

            @Override
            public void onSentTagsSuccess(Map<String, String> map) {
                Print.d(TAG, "PurchaseCount app callback is"+map);
            }

            @Override
            public void onSentTagsError(Exception e) {

            }
        };
        PushManager.sendTags(getBaseActivity(),open_count,callBack);

    }

    /**
     * Hide related items.
     */
    private void hideRelatedItems() {

        //UIUtils.setVisibility(mRelatedProductsView, false);
    }

    /**
     * Create Rich Relevance or Related Products View
     */
    private void setRelatedItems() {
        // Verify if there's Rich Relevance request to make
        if(mRichRelevance != null && !mRichRelevance.isHasData()){
            triggerRichRelevance(mRichRelevance.getTarget());
            hideRelatedItems();
            return;
        }

        if (mRichRelevance != null && CollectionUtils.isNotEmpty(mRichRelevance.getRichRelevanceProducts())) {
            if(mRichRelevance != null && TextUtils.isNotEmpty(mRichRelevance.getTitle())){
                ((TextView) mRelatedProductsView.findViewById(R.id.pdv_related_title)).setText(mRichRelevance.getTitle());
            }
            HorizontalListView relatedGridView = (HorizontalListView) mRelatedProductsView.findViewById(R.id.rich_relevance_listview);
            relatedGridView.enableRtlSupport(ShopSelector.isRtl());
            relatedGridView.addItemDecoration(new VerticalSpaceItemDecoration(ITEMS_MARGIN));
            relatedGridView.setAdapter(new RichRelevanceAdapter(mRichRelevance.getRichRelevanceProducts(), this, false));
            mRelatedProductsView.setVisibility(View.VISIBLE);
        } else {
            hideRelatedItems();
        }
    }

    private void triggerRichRelevance(String target) {
        triggerContentEvent(new GetRichRelevanceHelper(), GetRichRelevanceHelper.createBundle(TargetLink.getIdFromTargetLink(target)), this);
    }

    /**
     * Set the link into a string to order status
     *
     * @author sergiopereira
     * @see <href=http://www.chrisumbel.com/article/android_textview_rich_text_spannablestring>
     *      SpannableString</href>
     */
    private void setOrderStatusLink(View view, String orderNumber) {
        // Set text for order status
        Button button = (Button) view.findViewById(R.id.order_status_text);
        button.setTag(orderNumber);
        // Make ClickableSpans and URLSpans work
        button.setOnClickListener(this);
    }

    private void triggerClearCart() {
        Print.i(TAG, "TRIGGER: CLEAR CART FINISH");
        triggerContentEventNoLoading(new ClearShoppingCartHelper(), null, this);
    }



    private void goToProduct(final View view){
        // Remove all checkout process entries
        getBaseActivity().removeAllNativeCheckoutFromBackStack();
//        getBaseActivity().popBackStackEntriesUntilTag(FragmentType.HOME.toString());
        @TargetLink.Type String target = (String) view.getTag(R.id.target_sku);
        mRelatedRichRelevanceHash = (String) view.getTag(R.id.target_rr_hash);
        new TargetLink(getWeakBaseActivity(), target)
                .addAppendListener(this)
                .run();
    }

    @Override
    public void onAppendData(FragmentType next, String title, String id, Bundle data) {
        if(com.mobile.service.utils.TextUtils.isNotEmpty(mRelatedRichRelevanceHash))
            data.putString(ConstantsIntentExtra.RICH_RELEVANCE_HASH, mRelatedRichRelevanceHash );
    }

    /**
     * Process the click on the spannable string
     */
    private void onClickSpannableString(View view) {
        // Remove all checkout process entries
        getBaseActivity().removeAllNativeCheckoutFromBackStack();
        // Switch to track order
        Bundle bundle = new Bundle();
        // Validate orderNumber from tag
        String orderNumber = view.getTag() == null ? null : view.getTag().toString();
        if (!TextUtils.isEmpty(orderNumber)) {
            bundle.putString(ConstantsIntentExtra.ARG_1, view.getTag().toString());
            getBaseActivity().onSwitchFragment(FragmentType.ORDER_STATUS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        Print.d(TAG, "VIEW ID: " + view.getId());
        // CASE continue
        if (view.getId() == R.id.btn_checkout_continue) onClickContinue();
        // CASE order status
        else if (view.getId() == R.id.order_status_text) {
            Print.d(TAG, "ON CLICK SPAN: " + view.getId());
            onClickSpannableString(view);
        }
        // CASE default
        else {
            goToProduct(view);
        }

    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onClickRetryButton();
    }

    /**
     * Process the click on retry button.
     * @author paulo
     */
    private void onClickRetryButton() {
        getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on continue
     * @author sergiopereira
     */
    private void onClickContinue(){
        // Get user id
        String userId = "";
        if (BamiloApplication.CUSTOMER != null && BamiloApplication.CUSTOMER.getIdAsString() != null) userId = BamiloApplication.CUSTOMER.getIdAsString();
        // Tracking
        TrackerDelegator.trackCheckoutContinueShopping(userId);
        // Goto home
        getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#allowBackPressed()
     */
    @Override
    public boolean allowBackPressed() {
        getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        return true;
    }

    /**
     * Process the success event
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Hide dialog progress
        hideActivityProgress();
        // Validate event
        super.handleSuccessEvent(baseResponse);
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        switch (eventType){
            case GET_RICH_RELEVANCE_EVENT:
                mRichRelevance = (RichRelevance) baseResponse.getContentData();
                sendRecommend(BamiloApplication.INSTANCE.getCart());
                //setRelatedItems();
                showFragmentContentContainer();
                break;
            default:
                break;
        }

    }

    /**
     * Process the error event
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON ERROR EVENT: " + eventType);
        switch (eventType) {
            case GET_RICH_RELEVANCE_EVENT:
                hideRelatedItems();
                showFragmentContentContainer();
                break;
            default:
                break;
        }

    }

    private void sendRecommend(PurchaseEntity cart) {

        RecommendListCompletionHandler handler = new RecommendListCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(String category, List<RecommendedItem> data) {

                if (data == null || data.size() == 0) {
                    //mRelatedProductsView.removeView(recommendationsHolder.itemView);
                   // recommendations.setVisibility(View.GONE);
                    return;
                }
                LayoutInflater inflater = LayoutInflater.from(getBaseActivity());


                if (recommendationsHolder == null ) {
                    recommendationsHolder = new RecommendationsCartHolder(getBaseActivity(), inflater.inflate(R.layout.recommendation_cart, mRelatedProductsView, false), null);
                }
                if (recommendationsHolder != null ) {
                    try {


                        // Set view
                        mRelatedProductsView.removeView(recommendationsHolder.itemView);
                        recommendationsHolder = new RecommendationsCartHolder(getBaseActivity(), inflater.inflate(R.layout.recommendation_cart, mRelatedProductsView, false), null);

                        recommendationsHolder.onBind(data);
                        // Add to container

                        mRelatedProductsView.addView(recommendationsHolder.itemView, mRelatedProductsView.getChildCount()-1);
                    }
                    catch (Exception ex) {

                    }


                }
            }


        };

        if (cart == null) return;

        if (cart.getCartCount() == 1) {
            recommendManager.sendAlsoBoughtRecommend(null, cart.getCartItems().get(0).getSku(), handler);
        } else {
            recommendManager.sendPersonalRecommend(handler);
        }


    }

}

package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ClearShoppingCartHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.teasers.GetRichRelevanceHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.RichRelevanceAdapter;
import com.bamilo.android.appmodule.bamiloapp.view.MainFragmentActivity;
import com.bamilo.android.appmodule.modernbamilo.customview.BamiloActionButton;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.appmodule.modernbamilo.tracking.TrackingEvents;
import com.bamilo.android.appmodule.modernbamilo.userreview.UserReviewActivity;
import com.bamilo.android.appmodule.modernbamilo.util.storage.SharedPreferencesHelperKt;
import com.bamilo.android.framework.components.customfontviews.Button;
import com.bamilo.android.framework.components.recycler.HorizontalListView;
import com.bamilo.android.framework.components.recycler.VerticalSpaceItemDecoration;
import com.bamilo.android.framework.service.objects.cart.PurchaseCartItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.product.RichRelevance;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author sergiopereira
 */
public class CheckoutThanksFragment extends BaseFragment implements TargetLink.OnAppendDataListener,
        IResponseCallback {

    private static final String TAG = CheckoutThanksFragment.class.getSimpleName();

    private String orderShipping;

    private String orderTax;

    private String paymentMethod;

    private double grandTotalValue;

    private String orderNumber;

    private RichRelevance richRelevance;

    private ViewGroup relatedProductsView;

    private String relatedRichRelevanceHash;

    private static final int ITEMS_MARGIN = 6;

//    RecommendManager recommendManager;
//    RecommendationsCartHolder recommendationsHolder;

    private PurchaseEntity oldCart = null;
    private BamiloActionButton btnContinueShopping;

    private Boolean notOpenYet = true;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OldProductDetailsFragment.clearSelectedRegionCityId();

        // Get values
        getBundleValues(savedInstanceState);

//        recommendManager = new RecommendManager();

        TrackEvent();
        trackHomePageItemPurchaseEvent();

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CHECKOUT_FINISH.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    private void TrackEvent() {
        PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
        if (cart != null && cart.getCartItems() != null) {
            EventTracker.INSTANCE.purchase(
                    (long) cart.getTotal(),
                    cart.getCartCount(),
                    cart.getCouponCode(),
                    orderNumber,
                    (paymentMethod.equals("COD")) ? TrackingEvents.PaymentMethod.COD : TrackingEvents.PaymentMethod.IPG, ""
            );
        }
    }

    private void trackHomePageItemPurchaseEvent() {
        for (String sku : BamiloApplication.INSTANCE.getHomepageTrackingItemsSkus()) {
            SimpleEventModel sem = getSimpleEventModel();
            sem.category = SharedPreferencesHelperKt.
                    getHomePageItemsPurchaseTrackCategory(getContext());
            sem.action = EventConstants.Purchased;
            sem.label = SharedPreferencesHelperKt.
                    getHomePageItemsPurchaseTrackLabel(getContext());

            try {
                EventTracker.INSTANCE.purchase(
                        (long) BamiloApplication.INSTANCE.getCart().getTotal(),
                        BamiloApplication.INSTANCE.getCart().getCartCount(),
                        BamiloApplication.INSTANCE.getCart().getCouponCode(),
                        orderNumber,
                        (paymentMethod.equals("COD")) ? TrackingEvents.PaymentMethod.COD : TrackingEvents.PaymentMethod.IPG, ""
                );
            } catch (Exception ignored) {}

        }

        BamiloApplication.INSTANCE.clearHomepageTrackingItemsSkus();
    }

    private SimpleEventModel getSimpleEventModel() {
        SimpleEventModel sem = new SimpleEventModel();
        sem.category = CategoryConstants.CHECKOUT;
        sem.action = EventActionKeys.CHECKOUT_FINISH;
        sem.label = null;
        sem.value = SimpleEventModel.NO_VALUE;
        return sem;
    }

    private void getBundleValues(Bundle savedInstanceState) {
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if (arguments != null) {
            orderNumber = arguments.getString(RestConstants.ORDER_NUMBER);
            orderShipping = arguments.getString(RestConstants.TRANSACTION_SHIPPING);
            orderTax = arguments.getString(RestConstants.TRANSACTION_TAX);
            paymentMethod = arguments.getString(RestConstants.PAYMENT_METHOD);
            grandTotalValue = arguments.getDouble(RestConstants.ORDER_GRAND_TOTAL);
            if (arguments.containsKey(RestConstants.RECOMMENDED_PRODUCTS)) {
                richRelevance = arguments.getParcelable(RestConstants.RECOMMENDED_PRODUCTS);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareLayout(view);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RestConstants.ORDER_NUMBER, orderNumber);
        outState.putString(RestConstants.TRANSACTION_SHIPPING, orderShipping);
        outState.putString(RestConstants.TRANSACTION_TAX, orderTax);
        outState.putString(RestConstants.PAYMENT_METHOD, paymentMethod);
        outState.putDouble(RestConstants.ORDER_GRAND_TOTAL, grandTotalValue);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Show content
     */
    private void prepareLayout(View view) {
        // Track purchase
        PurchaseEntity cart = getCart();

        TrackerDelegator
                .trackPurchaseInCheckoutThanks(cart, orderNumber, grandTotalValue, orderShipping,
                        orderTax, paymentMethod);

        ArrayList<PurchaseCartItem> carts = cart.getCartItems();
        StringBuilder categories = new StringBuilder();
        if (carts != null) {
            for (PurchaseCartItem cat : carts) {
                categories.append(cat.getCategories());
            }
        }

        // Track Purchase
        MainEventModel purchaseEventModel = new MainEventModel(null, null, null,
                SimpleEventModel.NO_VALUE,
                MainEventModel.createPurchaseEventModelAttributes(categories.toString(),
                        (long) cart.getTotal(), true));

        if (cart != null && cart.getCartItems() != null) {
            EventTracker.INSTANCE.purchase(
                    (long) cart.getTotal(),
                    cart.getCartCount(),
                    cart.getCouponCode(),
                    orderNumber,
                    (paymentMethod.equals("COD")) ? TrackingEvents.PaymentMethod.COD : TrackingEvents.PaymentMethod.IPG, ""
            );
        }


        // Related Products
        relatedProductsView = view.findViewById(R.id.related_container);

        // Show
        //sendRecommend(cart);
        oldCart = cart;

        // Clean cart
        triggerClearCart();

//        recommendManager.sendPurchaseRecommend();
        BamiloApplication.INSTANCE.setCart(null);

        //Show Order Number
        showOrderNumber(view);

        // Update cart info
        getBaseActivity().updateCartInfo();

        // Continue button
        btnContinueShopping = view.findViewById(R.id.btn_checkout_continue);
        btnContinueShopping.setOnClickListener(this);

        // Add a link to order status
        setOrderStatusLink(view, orderNumber);
    }

    private PurchaseEntity getCart() {

        PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
        if (cart == null) {
            if (oldCart == null) {
                cart = new PurchaseEntity();
            } else {
                cart = oldCart;
            }
        }
        return cart;
    }

    private void showOrderNumber(View view) {
        TextView tvOrderNumber = view.findViewById(R.id.order_number_text);
        if (TextUtils.isEmpty(orderNumber)) {
            tvOrderNumber.setVisibility(View.GONE);
        } else {
            if (getContext() != null) {
                tvOrderNumber.setText(
                        getContext().getResources().getString(R.string.order_number, orderNumber));
            }
        }
    }

    /**
     * Hide related items.
     */
    private void hideRelatedItems() {
        /*UIUtils.setVisibility(relatedProductsView, false);
        UIUtils.setVisibility(btnContinueShopping, true);*/
    }

    /**
     * Create Rich Relevance or Related Products View
     */
    private void setRelatedItems() {
        // Verify if there's Rich Relevance request to make
        if (richRelevance != null && !richRelevance.isHasData()) {
            triggerRichRelevance(richRelevance.getTarget());
            hideRelatedItems();
            return;
        }

        if (richRelevance != null && CollectionUtils
                .isNotEmpty(richRelevance.getRichRelevanceProducts())) {
            if (richRelevance != null && TextUtils.isNotEmpty(richRelevance.getTitle())) {
                ((TextView) relatedProductsView.findViewById(R.id.pdv_related_title))
                        .setText(richRelevance.getTitle());
            }
            HorizontalListView relatedGridView = relatedProductsView
                    .findViewById(R.id.rich_relevance_listview);
            relatedGridView.enableRtlSupport(ShopSelector.isRtl());
            relatedGridView.addItemDecoration(new VerticalSpaceItemDecoration(ITEMS_MARGIN));
            relatedGridView
                    .setAdapter(new RichRelevanceAdapter(richRelevance.getRichRelevanceProducts(),
                            this,
                            false));
            relatedProductsView.setVisibility(View.VISIBLE);
        } else {
            hideRelatedItems();
        }
    }

    private void triggerRichRelevance(String target) {
        triggerContentEvent(new GetRichRelevanceHelper(),
                GetRichRelevanceHelper.createBundle(TargetLink.getIdFromTargetLink(target)),
                this);
    }

    /**
     * Set the link into a string to order status
     *
     * @author sergiopereira
     * @see <href=http://www.chrisumbel.com/article/android_textview_rich_text_spannablestring>
     * SpannableString</href>
     */
    private void setOrderStatusLink(View view, String orderNumber) {
        // Set text for order status
        Button button = view.findViewById(R.id.order_status_text);
        button.setTag(orderNumber);
        // Make ClickableSpans and URLSpans work
        button.setOnClickListener(this);
    }

    private void triggerClearCart() {
        triggerContentEventNoLoading(new ClearShoppingCartHelper(), null, this);
    }


    private void goToProduct(final View view) {
        // Remove all checkout process entries
        getBaseActivity().removeAllNativeCheckoutFromBackStack();
        // getBaseActivity().popBackStackEntriesUntilTag(FragmentType.HOME.toString());
        @TargetLink.Type String target = (String) view.getTag(R.id.target_sku);
        relatedRichRelevanceHash = (String) view.getTag(R.id.target_rr_hash);
        new TargetLink(getWeakBaseActivity(), target)
                .addAppendListener(this)
                .run();
    }

    @Override
    public void onAppendData(FragmentType next, String title, String id, Bundle data) {
        if (TextUtils.isNotEmpty(relatedRichRelevanceHash)) {
            data.putString(ConstantsIntentExtra.RICH_RELEVANCE_HASH, relatedRichRelevanceHash);
        }
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
            bundle.putString(ConstantsIntentExtra.ORDER_NUMBER, orderNumber);
            getBaseActivity().onSwitchFragment(FragmentType.ORDER_STATUS, bundle,
                    FragmentController.ADD_TO_BACK_STACK);
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

        switch (view.getId()) {
            case R.id.btn_checkout_continue:
                onClickContinue();
                break;
            case R.id.order_status_text:
                onClickSpannableString(view);
                break;
            default:
                goToProduct(view);
                break;
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
     *
     * @author paulo
     */
    private void onClickRetryButton() {
        getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE,
                FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on continue
     *
     * @author sergiopereira
     */
    private void onClickContinue() {
        // Goto home

        Intent intent = new Intent(getContext(), MainFragmentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getBaseActivity().finish();

//        if (BamiloApplication.CUSTOMER != null
//                && BamiloApplication.CUSTOMER.getIdAsString() != null) {
//            getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE,
//                    FragmentController.ADD_TO_BACK_STACK);
//        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#allowBackPressed()
     */

    @Override
    public boolean allowBackPressed() {
        FragmentController.getInstance().gotoPdv(getBaseActivity());
//        getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE,
//                FragmentController.ADD_TO_BACK_STACK);
        return false;
    }

    /**
     * Process the success event
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            return;
        }
        // Hide dialog progress
        hideActivityProgress();
        // Validate event
        super.handleSuccessEvent(baseResponse);
        switch (eventType) {
            case GET_RICH_RELEVANCE_EVENT:
                richRelevance = (RichRelevance) baseResponse.getContentData();
//                sendRecommend(BamiloApplication.INSTANCE.getCart());
                //setRelatedItems();
                showFragmentContentContainer();
                break;
            default:
                break;
        }

        if (getContext() != null && notOpenYet) {
            final String userId = BamiloApplication.CUSTOMER.getIdAsString();
            UserReviewActivity
                    .start(getContext(), UserReviewActivity.getTYPE_USER_REVIEW_AFTER_PURCHASE(),
                            userId, orderNumber);
            notOpenYet = false;
        }

    }

    /**
     * Process the error event
     */

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            return;
        }

        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_RICH_RELEVANCE_EVENT:
                hideRelatedItems();
                showFragmentContentContainer();
                break;
            default:
                break;
        }
    }

//    private void sendRecommend(PurchaseEntity cart) {
//
//        RecommendListCompletionHandler handler = (category, data) -> {
//            if (!isAdded()) {
//                return;
//            }
//
//            if (data == null || data.size() == 0) {
//                //relatedProductsView.removeView(recommendationsHolder.itemView);
//                // recommendations.setVisibility(View.GONE);
//                relatedProductsView.setVisibility(View.GONE);
//                btnContinueShopping.setVisibility(View.VISIBLE);
//                return;
//            }
//            btnContinueShopping.setVisibility(View.GONE);
//            relatedProductsView.setVisibility(View.VISIBLE);
//            LayoutInflater inflater = LayoutInflater.from(getBaseActivity());
//
//            if (recommendationsHolder == null) {
//                recommendationsHolder = new RecommendationsCartHolder(getBaseActivity(),
//                        inflater.inflate(R.layout.recommendation_cart,
//                                relatedProductsView,
//                                false),
//                        null);
//            }
//            try {
//                // Set view
//                relatedProductsView.removeView(recommendationsHolder.itemView);
//                recommendationsHolder = new RecommendationsCartHolder(getBaseActivity(),
//                        inflater.inflate(R.layout.recommendation_cart, relatedProductsView,
//                                false), null);
//
//                recommendationsHolder.onBind(data);
//                // Add to container
//
//                relatedProductsView.addView(recommendationsHolder.itemView,
//                        relatedProductsView.getChildCount() - 1);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        };
//
//        if (cart == null) {
//            return;
//        }
//
//        if (cart.getCartCount() == 1) {
//            recommendManager
//                    .sendAlsoBoughtRecommend(null, cart.getCartItems().get(0).getSku(), 6, handler);
//        } else {
//            recommendManager.sendPersonalRecommend(6, handler);
//        }
//    }
}

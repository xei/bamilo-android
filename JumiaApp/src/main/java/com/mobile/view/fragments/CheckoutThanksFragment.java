package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView.BufferType;

import com.mobile.app.JumiaApplication;
import com.mobile.components.ExpandedGridViewComponent;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.DividerItemDecoration;
import com.mobile.components.recycler.HorizontalListView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ClearShoppingCartHelper;
import com.mobile.helpers.teasers.GetRichRelevanceHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.product.RichRelevance;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.home.holder.CheckoutRRAdapter;
import com.mobile.utils.pdv.RelatedProductsAdapter;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

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
    /**
     * Get instance
     */
    public static CheckoutThanksFragment getInstance(Bundle bundle) {
        CheckoutThanksFragment fragment = new CheckoutThanksFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

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
        trackPurchase();

        // Related Products
        mRelatedProductsView = (ViewGroup) view.findViewById(R.id.related_container);
        ImageView imageSuccess = (ImageView) view.findViewById(R.id.success_image);

        UIUtils.setProgressForRTLPreJellyMr2(imageSuccess);
        setRelatedItems();
        // Clean cart
        triggerClearCart();
        JumiaApplication.INSTANCE.setCart(null);
        // Update cart info
        getBaseActivity().updateCartInfo();
        // Continue button
        view.findViewById(R.id.btn_checkout_continue).setOnClickListener(this);
        // Add a link to order status
        setOrderStatusLink(mOrderNumber);
    }

    /**
     * Create Rich Relevance or Related Products View
     */
    private void setRelatedItems() {
        // Verify if there's Rich Relevance request to make
        if(mRichRelevance != null && !mRichRelevance.isHasData()){
            triggerRichRelevance(mRichRelevance.getTarget());
            mRelatedProductsView.setVisibility(View.GONE);
            return;
        }

        if (mRichRelevance != null && CollectionUtils.isNotEmpty(mRichRelevance.getRichRelevanceProducts())) {

            if(mRichRelevance != null && com.mobile.newFramework.utils.TextUtils.isNotEmpty(mRichRelevance.getTitle())){
                ((TextView) mRelatedProductsView.findViewById(R.id.pdv_related_title)).setText(mRichRelevance.getTitle());

            }

            HorizontalListView relatedGridView = (HorizontalListView) mRelatedProductsView.findViewById(R.id.rich_relevance_listview);
            relatedGridView.enableRtlSupport(ShopSelector.isRtl());
            relatedGridView.addItemDecoration(new VerticalSpaceItemDecoration(10));
            relatedGridView.setAdapter(new CheckoutRRAdapter(mRichRelevance.getRichRelevanceProducts(), this));
            mRelatedProductsView.setVisibility(View.VISIBLE);
        } else {
            mRelatedProductsView.setVisibility(View.GONE);
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
    private void setOrderStatusLink(String orderNumber) {

        // Set text for order status
        Button button = (Button) getView().findViewById(R.id.order_status_text);
        button.setTag(orderNumber);
        // Make ClickableSpans and URLSpans work
        button.setOnClickListener(this);
    }

    private void triggerClearCart() {
        Print.i(TAG, "TRIGGER: CLEAR CART FINISH");
        triggerContentEventNoLoading(new ClearShoppingCartHelper(), null, this);
    }

    // TODO: move this method for TrackerDelegator and try unify with TrackerDelegator.trackPurchaseNativeCheckout
    private void trackPurchase() {
        if (JumiaApplication.INSTANCE.getCart() != null && CollectionUtils.isNotEmpty(JumiaApplication.INSTANCE.getCart().getCartItems())) {
            Bundle params = new Bundle();
            params.putString(TrackerDelegator.ORDER_NUMBER_KEY, mOrderNumber);
            params.putDouble(TrackerDelegator.VALUE_KEY, JumiaApplication.INSTANCE.getCart().getPriceForTracking());
            params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
            params.putParcelable(TrackerDelegator.CUSTOMER_KEY, JumiaApplication.CUSTOMER);
            params.putString(TrackerDelegator.COUPON_KEY, String.valueOf(JumiaApplication.INSTANCE.getCart().getCouponDiscount()));
            params.putInt(TrackerDelegator.CART_COUNT, JumiaApplication.INSTANCE.getCart().getCartCount());
            params.putDouble(TrackerDelegator.GRAND_TOTAL, mGrandTotalValue);

            if(!TextUtils.isEmpty(mOrderShipping) && !TextUtils.isEmpty(mOrderTax) && !TextUtils.isEmpty(mPaymentMethod)){
                params.putString(TrackerDelegator.SHIPPING_KEY, mOrderShipping);
                params.putString(TrackerDelegator.TAX_KEY, mOrderTax);
                params.putString(TrackerDelegator.PAYMENT_METHOD_KEY, mPaymentMethod);
            }
            TrackerDelegator.trackPurchaseNativeCheckout(params, JumiaApplication.INSTANCE.getCart().getCartItems(), JumiaApplication.INSTANCE.getCart().getAttributeSetIdList());
        }
    }

    private void goToProduct(final View view){
        @TargetLink.Type String target = (String) view.getTag(R.id.target_sku);
        // Get title
        String hash = (String) view.getTag(R.id.target_rr_hash);

        mRelatedRichRelevanceHash = hash;

        new TargetLink(getWeakBaseActivity(), target)
                .addAppendListener(this)
                .retainBackStackEntries()
                .run();
    }

    @Override
    public void onAppendData(FragmentType next, String title, String id, Bundle data) {
        if(com.mobile.newFramework.utils.TextUtils.isNotEmpty(mRelatedRichRelevanceHash))
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
     * Process the click on order number
     */
    @SuppressWarnings("deprecation")
    private void onClickOrderNumber(View v){
        ClipboardManager ClipMan = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipMan.setPrimaryClip(ClipData.newPlainText("simple text", ((TextView) v).getText()));
        showWarningSuccessMessage(getString(R.string.copied_to_clipboard));
    }
    
    /**
     * Process the click on continue
     * @author sergiopereira
     */
    private void onClickContinue(){
        // Get user id
        String userId = "";
        if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) userId = JumiaApplication.CUSTOMER.getIdAsString();
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

        switch (eventType){
            case GET_RICH_RELEVANCE_EVENT:
                mRichRelevance = (RichRelevance) baseResponse.getContentData();
                setRelatedItems();
                showFragmentContentContainer();
                break;
            default:
                break;
        }
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
    }

    /**
     * Process the error event
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        switch (eventType) {
            case GET_RICH_RELEVANCE_EVENT:
                setRelatedItems();
                showFragmentContentContainer();
                break;
            default:
                break;
        }
        Print.i(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceWidth;

        public VerticalSpaceItemDecoration(int verticalSpaceWidth) {
            this.mVerticalSpaceWidth = verticalSpaceWidth;
        }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.right = mVerticalSpaceWidth;
        }
    }


}

/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.TextView.BufferType;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ClearShoppingCartHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutThanksFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = CheckoutThanksFragment.class.getSimpleName();

    private static String order_number;
    
    private String orderShipping;
    
    private String orderTax;
    
    private String paymentMethod;

    private double mGrandTotalValue;
    
    /**
     * Get instance
     * 
     * @return
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
                NavigationAction.Checkout,
                R.layout.checkout_thanks,
                R.string.checkout_label,
                KeyboardState.NO_ADJUST_CONTENT,
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
        if(arguments != null &&
                getArguments().containsKey(ConstantsCheckout.CHECKOUT_THANKS_ORDER_SHIPPING) &&
                getArguments().containsKey(ConstantsCheckout.CHECKOUT_THANKS_ORDER_TAX) &&
                getArguments().containsKey(ConstantsCheckout.CHECKOUT_THANKS_PAYMENT_METHOD) &&
                getArguments().containsKey(ConstantsCheckout.CHECKOUT_THANKS_ORDER_TOTAL)){
            
            orderShipping = getArguments().getString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_SHIPPING);
            orderTax = getArguments().getString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_TAX);
            paymentMethod = getArguments().getString(ConstantsCheckout.CHECKOUT_THANKS_PAYMENT_METHOD);
            mGrandTotalValue = getArguments().getDouble(ConstantsCheckout.CHECKOUT_THANKS_ORDER_TOTAL);
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
        outState.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_SHIPPING, orderShipping);
        outState.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_TAX, orderTax);
        outState.putString(ConstantsCheckout.CHECKOUT_THANKS_PAYMENT_METHOD, paymentMethod);
        outState.putDouble(ConstantsCheckout.CHECKOUT_THANKS_ORDER_TOTAL, mGrandTotalValue);
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
        
        // String order_number = args.getString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR);
        if (JumiaApplication.INSTANCE.getPaymentMethodForm() != null && JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber() != null) {
            order_number = JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber();
            // Track purchase
            if(!JumiaApplication.INSTANCE.getPaymentMethodForm().isCameFromWebCheckout()) trackPurchase();
        }
        
        // Clean cart and payment
        JumiaApplication.INSTANCE.setCart(new PurchaseEntity());
        JumiaApplication.INSTANCE.setPaymentMethodForm(null);
        // Update cart info
        getBaseActivity().updateCartInfo();
        // Order number
        TextView tV = (TextView) view.findViewById(R.id.order_number_id);
        tV.setText(order_number);
        tV.setOnClickListener(this);
        // Continue button
        view.findViewById(R.id.btn_checkout_continue).setOnClickListener(this);
        // Add a link to order status
        setOrderStatusLink(order_number);
    }

    /**
     * Set the link into a string to order status
     * 
     * @author sergiopereira
     * @see <href=http://www.chrisumbel.com/article/android_textview_rich_text_spannablestring>
     *      SpannableString</href>
     */
    private void setOrderStatusLink(String orderNumber) {

        // Get strings
        String mainText = getString(R.string.order_track_success);
        String text = getString(R.string.order_track_link);
        int index = mainText.indexOf(text);
        if (index == -1) {
            index = 0;
            text = mainText;
        }
        // Create link
        SpannableString link = new SpannableString(mainText);
        link.setSpan(new TouchableSpan(R.color.yellow_dark, R.color.grey_middle), index, index + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Set text for order status
        TextView textView = (TextView) getView().findViewById(R.id.order_status_text);
        textView.setTag(orderNumber);
        // Make ClickableSpans and URLSpans work
        textView.setMovementMethod(new LinkTouchMovementMethod());
        // Set text with span style
        textView.setText(link, BufferType.SPANNABLE);
    }

    private void triggerClearCart() {
        Print.i(TAG, "TRIGGER: CHECKOUT FINISH");
        triggerContentEventNoLoading(new ClearShoppingCartHelper(), null, this);
    }

    private void trackPurchase() {
        if (JumiaApplication.INSTANCE.getCart() != null) {
            Bundle params = new Bundle();
            params.putString(TrackerDelegator.ORDER_NUMBER_KEY, order_number);
            params.putDouble(TrackerDelegator.VALUE_KEY, JumiaApplication.INSTANCE.getCart().getPriceForTracking());
            params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
            params.putParcelable(TrackerDelegator.CUSTOMER_KEY, JumiaApplication.CUSTOMER);
            params.putString(TrackerDelegator.COUPON_KEY, String.valueOf(JumiaApplication.INSTANCE.getCart().getCouponDiscount()));
            params.putInt(TrackerDelegator.CART_COUNT, JumiaApplication.INSTANCE.getCart().getCartCount());
            params.putDouble(TrackerDelegator.GRAND_TOTAL, mGrandTotalValue);
                        
            if(!TextUtils.isEmpty(orderShipping) && !TextUtils.isEmpty(orderTax) && !TextUtils.isEmpty(paymentMethod)){
                params.putString(TrackerDelegator.SHIPPING_KEY, orderShipping);
                params.putString(TrackerDelegator.TAX_KEY, orderTax);
                params.putString(TrackerDelegator.PAYMENT_METHOD_KEY, paymentMethod);
            }
            TrackerDelegator.trackPurchaseNativeCheckout(params, JumiaApplication.INSTANCE.getCart().getCartItems(), JumiaApplication.INSTANCE.getCart().getAttributeSetIdList());
        }

        triggerClearCart();
        JumiaApplication.INSTANCE.setCart(null);
    }

    /*--
     * http://stackoverflow.com/questions/20856105/change-the-text-color-of-a-clickablespan-when-pressed/20905824#20905824
     */
    private class LinkTouchMovementMethod extends LinkMovementMethod {
        private TouchableSpan mPressedSpan;

        @Override
        public boolean onTouchEvent(android.widget.TextView textView, Spannable spannable, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mPressedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan), spannable.getSpanEnd(mPressedSpan));
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                TouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan.setPressed(false);
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
            } else {
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(false);
                    super.onTouchEvent(textView, spannable, event);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
            return true;
        }

        private TouchableSpan getPressedSpan(android.widget.TextView textView, Spannable spannable, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
            TouchableSpan touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;
        }
    }

    class TouchableSpan extends ClickableSpan {
        private boolean mIsPressed;
        private int mNormalTextColor;
        private int mPressedTextColor;

        public TouchableSpan(int normalTextColorRes, int pressedTextColorRes) {
            mNormalTextColor = getResources().getColor(normalTextColorRes);
            mPressedTextColor = getResources().getColor(pressedTextColorRes);
        }

        public void setPressed(boolean isSelected) {
            mIsPressed = isSelected;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
            ds.setUnderlineText(true);
        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.order_status_text) {
                Print.d(TAG, "ON CLICK SPAN: " + view.getId());
                onClickSpannableString(view);
            }
        }
    }

    /**
     * Process the click on the spannable string
     * 
     * @param view
     * @author sergiopereira
     */
    private void onClickSpannableString(View view) {
        // Remove all checkout process entries
        getBaseActivity().removeAllNativeCheckoutFromBackStack();
        // Switch to track order
        Bundle bundle = new Bundle();
        // Validate orderNumber from tag
        String orderNumber = view.getTag() == null ? null : view.getTag().toString();
        if (!TextUtils.isEmpty(orderNumber)) {
            bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR, view.getTag().toString());
        }
        
        getBaseActivity().onSwitchFragment(FragmentType.MY_ORDERS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        Print.d(TAG, "VIEW ID: " + view.getId() + " " + R.id.order_status_text);
        // CASE continue
        if (view.getId() == R.id.btn_checkout_continue) onClickContinue();
        // CASE order number
        else if(view.getId() == R.id.order_number_id) onClickOrderNumber(view);   
        // CASE default
        else getBaseActivity().onSwitchFragment(FragmentType.MY_ORDERS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        
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
     * @param v
     * @author sergiopereira
     */
    @SuppressWarnings("deprecation")
    private void onClickOrderNumber(View v){
        ClipboardManager ClipMan = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipMan.setPrimaryClip(ClipData.newPlainText("simple text",((TextView) v).getText()));

        Toast.makeText(getActivity(), getString(R.string.copied_to_clipboard),Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        onSuccessEvent(baseResponse);

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        onErrorEvent(baseResponse);

    }

    /**
     * Process the success event
     * 
     * @param baseResponse
     * @return
     */
    protected boolean onSuccessEvent(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        return true;
    }

    /**
     * Process the error event
     * 
     * @param baseResponse
     * @return
     */
    protected boolean onErrorEvent(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        ErrorCode errorCode = baseResponse.getError().getErrorCode();
        Print.i(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        return false;
    }

}

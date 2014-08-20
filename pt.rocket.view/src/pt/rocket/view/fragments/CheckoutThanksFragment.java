/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.cart.ClearShoppingCartHelper;
import pt.rocket.helpers.voucher.SetVoucherHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.R;
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
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutThanksFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutThanksFragment.class);

    private static CheckoutThanksFragment checkoutStep5Fragment;

    private static String order_number;

    Customer mCustomer;

    /**
     * Get instance
     * 
     * @return
     */
    public static CheckoutThanksFragment getInstance() {
        if (checkoutStep5Fragment == null)
            checkoutStep5Fragment = new CheckoutThanksFragment();
        return checkoutStep5Fragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutThanksFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                R.layout.checkout_thanks,
                ConstantsCheckout.CHECKOUT_THANKS,
                KeyboardState.NO_ADJUST_CONTENT);
        this.setRetainInstance(true);
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
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareLayout();
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
        TrackerDelegator.trackPage(TrackingPage.CHECKOUT_THANKS);
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

    /**
     * Show content
     */
    private void prepareLayout() {
        
        // String order_number = args.getString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR);
        if (JumiaApplication.INSTANCE.getPaymentMethodForm() != null && JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber() != null) {
            order_number = JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber();
            // Track purchase
            if(!JumiaApplication.INSTANCE.getPaymentMethodForm().isCameFromWebCheckout()) trackPurchase();
        }
        
        // Clean cart and payment
        JumiaApplication.INSTANCE.setCart(new ShoppingCart(JumiaApplication.INSTANCE.getItemSimpleDataRegistry()));
        JumiaApplication.INSTANCE.setPaymentMethodForm(null);
        // Update cart info
        getBaseActivity().updateCartInfo();
        // Order number
        TextView tV = (TextView) getView().findViewById(R.id.order_number_id);
        tV.setText(order_number);
        tV.setOnClickListener(this);
        // Continue button
        getView().findViewById(R.id.btn_checkout_continue).setOnClickListener(this);
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
        android.widget.TextView textView = (android.widget.TextView) getView().findViewById(R.id.order_status_text);
        textView.setTag(orderNumber);
        // Make ClickableSpans and URLSpans work
        textView.setMovementMethod(new LinkTouchMovementMethod());
        // Set text with span style
        textView.setText(link, BufferType.SPANNABLE);
    }

    private void triggerClearCart() {
        Log.i(TAG, "TRIGGER: CHECKOUT FINISH");
        triggerContentEventWithNoLoading(new ClearShoppingCartHelper(), null, this);
        triggerContentEventWithNoLoading(new SetVoucherHelper(), null, this);
    }

    private void trackPurchase() {
        if (JumiaApplication.INSTANCE.getCart() != null) {
            Bundle params = new Bundle();
            params.putString(TrackerDelegator.ORDER_NUMBER_KEY, order_number);
            params.putString(TrackerDelegator.VALUE_KEY, JumiaApplication.INSTANCE.getCart().getCartValue());
            params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
            params.putParcelable(TrackerDelegator.CUSTOMER_KEY, JumiaApplication.CUSTOMER);
            params.putString(TrackerDelegator.COUPON_KEY, JumiaApplication.INSTANCE.getCart().getCouponDiscount());
                        
            TrackerDelegator.trackPurchaseNativeCheckout(params, JumiaApplication.INSTANCE.getCart().getCartItems());
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
                Log.d(TAG, "ON CLICK SPAN: " + view.getId());
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
        super.removeNativeCheckoutFromBackStack();
        // Switch to track order
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR, view.getTag().toString());
        getBaseActivity().onSwitchFragment(FragmentType.TRACK_ORDER, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG, "VIEW ID: " + v.getId() + " " + R.id.order_status_text);
        // CASE continue
        if (v.getId() == R.id.btn_checkout_continue) onClickContinue();
        // CASE order number
        else if(v.getId() == R.id.order_number_id) onClickOrderNumber(v);
        // CASE default
        else getBaseActivity().onSwitchFragment(FragmentType.TRACK_ORDER, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        
    }
    
    /**
     * Process the click on order number
     * @param v
     * @author sergiopereira
     */
    @SuppressWarnings("deprecation")
    private void onClickOrderNumber(View v){
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            android.text.ClipboardManager ClipMan = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipMan.setText(((TextView) v).getText());
        } else {
            ClipboardManager ClipMan = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipMan.setPrimaryClip(ClipData.newPlainText("simple text",((TextView) v).getText()));
        }
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
     * @see pt.rocket.view.fragments.BaseFragment#allowBackPressed()
     */
    @Override
    public boolean allowBackPressed() {
        getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        return true;
    }

    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);

    }

    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);

    }

    /**
     * Process the success event
     * 
     * @param bundle
     * @return
     */
    protected boolean onSuccessEvent(Bundle bundle) {

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
        case GET_CUSTOMER:
            trackPurchase();
            break;
        default:
            break;
        }

        return true;
    }

    /**
     * Process the error event
     * 
     * @param bundle
     * @return
     */
    protected boolean onErrorEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
        case GET_CUSTOMER:
            
            break;
        default:
            break;
        }

        return false;
    }

}

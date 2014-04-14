/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import org.holoeverywhere.widget.TextView;
import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.ClearShoppingCartHelper;
import pt.rocket.helpers.GetCustomerHelper;
import pt.rocket.helpers.SetVoucherHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.MainFragmentActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutThanksFragment extends BaseFragment implements OnClickListener,
        IResponseCallback {

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
        super(EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                ConstantsCheckout.CHECKOUT_THANKS);
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
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        View view = inflater.inflate(R.layout.checkout_thanks, container, false);
        return view;
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
        AnalyticsGoogle.get().trackPage(R.string.gcheckoutfinal);
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
        prepareLayout();
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
        Log.i(TAG, "ON DESTROY");
    }

    /**
     * Show content
     */
    private void prepareLayout() {
        // String order_number = args.getString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR);
        if (JumiaApplication.INSTANCE.getPaymentMethodForm() != null
                && JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber() != null) {
            order_number = JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber();
            if(!JumiaApplication.INSTANCE.getPaymentMethodForm().isCameFromWebCheckout()){
                trackPurchase();
            }
        }
        
        TextView tV = (TextView) getView().findViewById(R.id.order_number_id);
        tV.setText(order_number);
        tV.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
                    android.text.ClipboardManager ClipMan = (android.text.ClipboardManager) getActivity()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipMan.setText(((TextView) v).getText());
                } else {
                    ClipboardManager ClipMan = (ClipboardManager) getActivity().getSystemService(
                            Context.CLIPBOARD_SERVICE);
                    ClipMan.setPrimaryClip(ClipData.newPlainText("simple text",
                            ((TextView) v).getText()));
                }

                Toast.makeText(getActivity(), getString(R.string.copied_to_clipboard),
                        Toast.LENGTH_SHORT).show();
            }
        });

        getView().findViewById(R.id.btn_checkout_continue).setOnClickListener(this);
        // Add a link to order status
        setOrderStatusLink(order_number);
        // Show the container
        getBaseActivity().showContentContainer();
        JumiaApplication.INSTANCE.setPaymentMethodForm(null);
        JumiaApplication.INSTANCE.setCart(null);
        getBaseActivity().updateCartInfo();
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
        link.setSpan(clickableSpan, index, index + text.length(), 0);
        link.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow_dark)), index,
                index + text.length(), 0);
        // Set text for order status
        TextView textView = (TextView) getView().findViewById(R.id.order_status_text);
        textView.setTag(orderNumber);
        // Make ClickableSpans and URLSpans work
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        // Set text with span style
        textView.setText(link, BufferType.SPANNABLE);
    }
    
    private void triggerClearCart() {
        Log.i(TAG, "TRIGGER: CHECKOUT FINISH");
        triggerContentEventWithNoLoading(new ClearShoppingCartHelper(), null, this);
        triggerContentEventWithNoLoading(new SetVoucherHelper(), null, this);
    }

    private void trackPurchase() {
        if (JumiaApplication.INSTANCE.getCart() != null)
            TrackerDelegator.trackPurchaseNativeCheckout(getActivity().getApplicationContext(), order_number, JumiaApplication.INSTANCE.getCart().getCartValue(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), JumiaApplication.INSTANCE.getCart().getCartItems(), JumiaApplication.INSTANCE.CUSTOMER);
            
        triggerClearCart();
        JumiaApplication.INSTANCE.setCart(null);
    }

    /**
     * Click span listener
     * 
     * @author sergiopereira
     */
    ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.order_status_text) {
                Log.d(TAG, "ON CLICK SPAN: " + view.getId());
                onClickSpannableString(view);
            }
        }
    };

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
        getBaseActivity().onSwitchFragment(FragmentType.TRACK_ORDER, bundle,
                FragmentController.ADD_TO_BACK_STACK);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG, "VIEW ID: " + v.getId() + " " + R.id.order_status_text);
        if (v.getId() == R.id.btn_checkout_continue){
            String user_id = "";
            if(JumiaApplication.INSTANCE.CUSTOMER != null && JumiaApplication.INSTANCE.CUSTOMER.getIdAsString() != null){
                user_id = JumiaApplication.INSTANCE.CUSTOMER.getIdAsString();
            }
            AnalyticsGoogle.get().trackCheckoutContinueShopping(getBaseActivity(), user_id);
            ActivitiesWorkFlow.homePageActivity(getActivity());
        }
            
        // getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE,
        // FragmentController.ADD_TO_BACK_STACK);
        else {
            getBaseActivity().onSwitchFragment(FragmentType.TRACK_ORDER,
                    FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#allowBackPressed()
     */
    @Override
    public boolean allowBackPressed() {
        ((MainFragmentActivity) getBaseActivity()).popBackStack(FragmentType.HOME.toString());
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
        Log.i(TAG, "ON SUCCESS EVENT");

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

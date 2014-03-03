package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.forms.PaymentMethodForm;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Address;
import pt.rocket.framework.objects.Order;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.objects.PaymentMethods;
import pt.rocket.framework.objects.ShippingMethods;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.address.GetDefaultBillingAddressHelper;
import pt.rocket.helpers.address.GetDefaultShippingAddressHelper;
import pt.rocket.helpers.checkout.CheckoutFinishHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutMyOrderFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutMyOrderFragment.class);

    private static CheckoutMyOrderFragment mMyOrderFragment;

    private ViewGroup mProductsContainer;

    private TextView mTotalValue;

    private ShoppingCart cart;

    private TextView mShipFeeValue;

    private ViewGroup mShipFeeView;

    private ViewGroup mShippingAddressContainer;

    private TextView mShippingMethodName;

    private ViewGroup totalView;

    private ViewGroup mVoucherView;

    private TextView mVoucherValue;

    private ViewGroup mBillingAddressContainer;

    private View mBillingAddressIsSame;

    private TextView mPaymentName;

    private Address shippingAddress;

    private Address billingAddress;

    private ContentValues shippingMethod;

    private ContentValues paymentOptions;

    private TextView mProductsNum;

    private TextView mProductsValue;

    private TextView mCoupon;

    private ViewGroup mPaymentFormContainer;

    /**
     * 
     * @return
     */
    public static CheckoutMyOrderFragment getInstance(Bundle bundle) {
        if (mMyOrderFragment == null)
            mMyOrderFragment = new CheckoutMyOrderFragment();
        return mMyOrderFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutMyOrderFragment() {
        super(EnumSet.of(EventType.CHECKOUT_FINISH_EVENT, EventType.GET_MY_ORDER_EVENT),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Unknown,
                0);
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
        setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.checkout_my_order_main, viewGroup, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get containers
        
        // Get product items
        mProductsContainer = (ViewGroup) view.findViewById(R.id.checkout_my_order_products_list);
                
        // Get sub total
        mProductsNum = (TextView) view.findViewById(R.id.checkout_my_order_products_text_n_items);
        mProductsValue = (TextView) view.findViewById(R.id.checkout_my_order_products_text_total_items);
        mShipFeeView = (ViewGroup) view.findViewById(R.id.checkout_my_order_products_shippingfee_container);
        mShipFeeValue = (TextView) view.findViewById(R.id.checkout_my_order_products_text_shippingfee);
        mVoucherView = (ViewGroup) view.findViewById(R.id.checkout_my_order_products_voucher_container);
        mVoucherValue = (TextView) view.findViewById(R.id.checkout_my_order_products_text_voucher);
        mTotalValue = (TextView) view.findViewById(R.id.checkout_my_order_products_text_total);
        
        // Get shipping address
        view.findViewById(R.id.checkout_my_order_shipping_address_btn_edit).setOnClickListener((OnClickListener) this);
        mShippingAddressContainer = (ViewGroup) view.findViewById(R.id.checkout_my_order_shipping_address_list);
        
        // Get billing address
        view.findViewById(R.id.checkout_my_order_billing_address_btn_edit).setOnClickListener((OnClickListener) this);
        mBillingAddressContainer = (ViewGroup) view.findViewById(R.id.checkout_my_order_billing_address_list);
        mBillingAddressIsSame = view.findViewById(R.id.checkout_my_order_billing_address_is_same);
        
        // Get shipping method
        view.findViewById(R.id.checkout_my_order_shipping_method_btn_edit).setOnClickListener((OnClickListener) this);
        mShippingMethodName = (TextView) view.findViewById(R.id.checkout_my_order_shipping_method_name);
        
        // Get payment options
        view.findViewById(R.id.checkout_my_order_payment_options_btn_edit).setOnClickListener((OnClickListener) this);
        mPaymentName = (TextView) view.findViewById(R.id.checkout_my_order_payment_name);
        mCoupon = (TextView) view.findViewById(R.id.checkout_my_order_payment_coupon);
        
        // XXX
        mPaymentFormContainer = (ViewGroup) view.findViewById(R.id.checkout_payment_form_container);
        
        
        // Get the next step button
        view.findViewById(R.id.checkout_my_order_button_enter).setOnClickListener((OnClickListener) this);
                
        // Get my Order
        triggerGetMyOrder();

    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onStart()
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
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }

    /**
     * 
     */
    private void showMyOrder() {
        // Get cart
        cart = JumiaApplication.INSTANCE.getCart();
        if(cart != null) showProducts();
        // Get shipping address
        shippingAddress = JumiaApplication.INSTANCE.getShippingAddress();
        if(shippingAddress != null) showShippingAddress();
        // Get billing address
        billingAddress = JumiaApplication.INSTANCE.getBillingAddress();
        if(billingAddress != null) showBillingAddress();
        // Get shipping method
        shippingMethod = JumiaApplication.INSTANCE.getShippingMethod();
        if(shippingMethod != null) showShippingMethod();
        // Get payment options
        paymentOptions = JumiaApplication.INSTANCE.getPaymentMethod();
        if(paymentOptions != null) showPaymentOptions();
        
        getBaseActivity().showContentContainer(false);
    }
    
    /**
     * 
     */
    private void showProducts(){
        // Show products
        for (ShoppingCartItem item : cart.getCartItems().values()) {
            View prodInflateView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_my_order_product_item, mProductsContainer, false);
            // Image
            ImageView imageView = (ImageView) prodInflateView.findViewById(R.id.my_order_item_image);
            AQuery aq = new AQuery(getBaseActivity());
            aq.id(imageView).image(item.getImageUrl());
            // Name
            ((TextView) prodInflateView.findViewById(R.id.my_order_item_name)).setText(item.getName());
            // Quantity
            ((TextView) prodInflateView.findViewById(R.id.my_order_item_quantity)).setText("Qty: " + item.getQuantity());
            // Price
            ((TextView) prodInflateView.findViewById(R.id.my_order_item_price)).setText(item.getPrice());
            // Add item view
            mProductsContainer.addView(prodInflateView);
        }
        // Show sub total
        showSubTotal();
    }
    
    /**
     * 
     */
    private void showSubTotal() {
        int size = cart.getCartItems().size();
        String itemsLabel = (size > 1) ? getString(R.string.my_order_items_label) : getString(R.string.my_order_item_label);
        mProductsNum.setText(size + " " + itemsLabel);
        mProductsValue.setText(cart.getCartValue()); 
        mShipFeeValue.setText(cart.getShippingValue());
        mVoucherView.setVisibility(View.GONE);
        mTotalValue.setText(cart.getCartValue());
    }
    
    /**
     * 
     */
    private void showShippingAddress() {
        Log.d(TAG, "SHOW SHIPPING ADDRESS: " + shippingAddress.getAddress());
        View shippingAddressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, mShippingAddressContainer, false);
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_name)).setText(shippingAddress.getFirstName() + " " + shippingAddress.getLastName());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_street)).setText(shippingAddress.getAddress());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_region)).setText(shippingAddress.getCity());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(shippingAddress.getPostcode());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_phone)).setText(""+shippingAddress.getPhone());
        shippingAddressView.findViewById(R.id.checkout_address_item_btn_container).setVisibility(View.GONE);
        mShippingAddressContainer.addView(shippingAddressView);
    }
    
    /**
     * 
     */
    private void showBillingAddress() {
        // Validate address
        if(shippingAddress.getId() != billingAddress.getId()){
            // Hide text is the same
            mBillingAddressIsSame.setVisibility(View.GONE);
            // Show address
            View billingAddressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, mBillingAddressContainer, false);
            ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_name)).setText(billingAddress.getFirstName() + " " + billingAddress.getLastName());
            ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_street)).setText(billingAddress.getAddress());
            ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_region)).setText(billingAddress.getCity());
            ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(billingAddress.getPostcode());
            ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_phone)).setText(""+billingAddress.getPhone());
            billingAddressView.findViewById(R.id.checkout_address_item_btn_container).setVisibility(View.GONE);
            mBillingAddressContainer.addView(billingAddressView);
        } else {
            mBillingAddressContainer.setVisibility(View.GONE);
        }
    }
    
    /**
     * 
     */
    private void showShippingMethod() {
        Log.d(TAG, "code1shipping : " + shippingMethod.toString());
        Set<Entry<String, Object>> ships = shippingMethod.valueSet();
        for (Entry<String, Object> entry : ships) {
            String curKey = entry.getKey();
            String curValue = (String)  entry.getValue();
            Log.d(TAG, "SHIPPING METHOD: " + curKey + " " + curValue);
            if(curKey.equals("name")) mShippingMethodName.setText(curValue);
        }
    }
    
    /**
     * 
     */
    private void showPaymentOptions() {
        Set<Entry<String, Object>> ships = paymentOptions.valueSet();
        for (Entry<String, Object> entry : ships) {
            String curKey = entry.getKey();
            String curValue = (String) entry.getValue();
            Log.d(TAG, "PAYMENT OPTION: " + curKey + " " + curValue);
            if(curKey.equals("name")) mPaymentName.setText(curValue);
        }
        
    }

    /**
     * ############# CLICK LISTENER #############
     */
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Shipping Address Edit
        if (id == R.id.checkout_my_order_shipping_address_btn_edit) onClickEditAddressesButton();
        // Billing Address Edit
        else if (id == R.id.checkout_my_order_billing_address_btn_edit) onClickEditAddressesButton();
        // Shipping method
        else if (id == R.id.checkout_my_order_shipping_method_btn_edit) onClickEditShippingMethodButton();
        // Payment options
        else if (id == R.id.checkout_my_order_payment_options_btn_edit) onClickEditPaymentOptionsButton();
        // Next step button
        else if (id == R.id.checkout_my_order_button_enter) onClickNextStepButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }

    /**
     * 
     */
    private void onClickNextStepButton() {
        Log.i(TAG, "ON CLICK: NextStep");
        triggerCheckoutFinish();
    }    
    
    private void onClickEditAddressesButton() {
        Log.i(TAG, "ON CLICK: EditAddresses");
        getBaseActivity().onSwitchFragment(FragmentType.MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * 
     */
    private void onClickEditShippingMethodButton() {
        Log.i(TAG, "ON CLICK: EditShippingMethod");
        getBaseActivity().onSwitchFragment(FragmentType.SHIPPING_METHODS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * 
     */
    private void onClickEditPaymentOptionsButton() {
        Log.i(TAG, "ON CLICK: EditPaymentOptions");
        getBaseActivity().onSwitchFragment(FragmentType.PAYMENT_METHODS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * ############# REQUESTS #############
     */

    /**
     * 
     */
    private void triggerGetMyOrder() {
        Log.i(TAG, "TRIGGER: GET MY ORDER");
        // TODO: Get order summary
        // triggerContentEvent(new GetMyOrderHelper(), null, this);
        triggerContentEvent(new GetDefaultShippingAddressHelper(), null, this);
    }
    
    /**
     * 
     */
    private void triggerCheckoutFinish() {
        Log.i(TAG, "TRIGGER: CHECKOUT FINISH");
        triggerContentEvent(new CheckoutFinishHelper(), null, this);
    }

    /**
     * ############# RESPONSE #############
     */
    /**
     * 
     * @param bundle
     * @return
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS EVENT");
        
        // Validate fragment visibility
        if(!isVisible()){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        if(getBaseActivity() != null){
            Log.d(TAG, "BASE ACTIVITY HANDLE SUCCESS EVENT");
            getBaseActivity().handleSuccessEvent(bundle);
        } else {
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
        case GET_MY_ORDER_EVENT:
            Log.d(TAG, "RECEIVED GET_MY_ORDER_EVENT");
            // OrderSummary order = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            break;
        case CHECKOUT_FINISH_EVENT:
            Log.d(TAG, "RECEIVED CHECKOUT_FINISH_EVENT");
            if(JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_SUBMIT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_AUTO_SUBMIT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_AUTO_REDIRECT_EXTERNAL){
                getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_EXTERNAL_PAYMENT, null, FragmentController.ADD_TO_BACK_STACK);
            } else {
                bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR, JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber());
                getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle, FragmentController.ADD_TO_BACK_STACK); 
            }

            //Order order = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            //bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR, order.getOrderNumber());
            //getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle, FragmentController.ADD_TO_BACK_STACK);
            break;
            
        // TODO: Remove this filters    
        case GET_DEFAULT_SHIPPING_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED GET_DEFAULT_SHIPPING_ADDRESS_EVENT");
            Address shipAddress = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.INSTANCE.setShippingAddress(shipAddress);
            // Get billing address
            sendRequest(new GetDefaultBillingAddressHelper(), null, this);
            break;
        case GET_DEFAULT_BILLING_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED GET_DEFAULT_BILLING_ADDRESS_EVENT");
            Address bilAddress = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.INSTANCE.setBillingAddress(bilAddress);
            // Show order 
            showMyOrder();
            break;
            
        default:
            break;
        }

        return true;
    }

    /**
     * 
     * @param bundle
     * @return
     */
    protected boolean onErrorEvent(Bundle bundle) {
        if (!isVisible()) {
            return true;
        }
        if (getBaseActivity().handleErrorEvent(bundle)) {
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
        case CHECKOUT_FINISH_EVENT:
            Log.d(TAG, "RECEIVED CHECKOUT_FINISH_EVENT");
            
            //{"success":false,
            //"messages":{
            //"error":["BILLING_ERROR, PAYMENT_ERROR"]},
            //"session":{"id":"jfs5u9iiurce0psjn1rjp6si71","expire":null,"YII_CSRF_TOKEN":"f1e67b7fcfbed7e96e4d6d69bba26a0f06c72c51"}}
            
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY); 
                showErrorDialog(errors);
                getBaseActivity().showContentContainer(false);
            }
            break;
        case GET_DEFAULT_SHIPPING_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED CHECKOUT_FINISH_EVENT");
            break;
        case GET_DEFAULT_BILLING_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED GET_DEFAULT_BILLING_ADDRESS_EVENT");
            break;
        default:
            break;
        }

        return false;
    }

    /**
     * ########### RESPONSE LISTENER ###########
     */
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }
    
    /**
     * ########### DIALOGS ###########  
     */    
    /**
     * Dialog used to show an error
     * @param errors
     */
    private void showErrorDialog(HashMap<String, List<String>> errors){
        Log.d(TAG, "SHOW LOGIN ERROR DIALOG");
        List<String> errorMessages = (List<String>) errors.get(RestConstants.JSON_VALIDATE_TAG);

        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
            
            if(getBaseActivity() != null) getBaseActivity().showContentContainer(false);
            
            dialog = DialogGenericFragment.newInstance(true, true, false,
                    getString(R.string.error_login_title),
                    errorMessages.get(0),
                    getString(R.string.ok_label), "", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dialog.dismiss();
                            }

                        }

                    });
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
        } else {
            
            /**
             * TODO: THE ERROR MUST RETURN THE MESSAGE
             */
            Toast.makeText(getBaseActivity(), "ERROR ON FINISH CHECKOUT!", Toast.LENGTH_SHORT).show();
        }
    }

}

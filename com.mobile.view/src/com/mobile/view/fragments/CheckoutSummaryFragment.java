/**
 * 
 */
package com.mobile.view.fragments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.Address;
import com.mobile.framework.objects.OrderSummary;
import com.mobile.framework.objects.ShoppingCart;
import com.mobile.framework.objects.ShoppingCartItem;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
import com.mobile.helpers.cart.GetShoppingCartRemoveItemHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show the order summary in the checkout process
 * @author sergiopereira
 * 
 */
public class CheckoutSummaryFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutSummaryFragment.class);

    private static CheckoutSummaryFragment sOrderSummaryFragment;

    private ViewGroup mProductList;

    private TextView mSubTotal;
    
    private TextView mExtraCosts;
    
    private LinearLayout mExtraCostsContainer;

    private ShoppingCart mCart;

    private TextView mShippingFeeValue;

    private ViewGroup mShippingFeeView;

    private ViewGroup mShippingAddressView;

    private ViewGroup mShippingMethodView;

    private ViewGroup mTotalView;

    private OrderSummary mOrderSummary;

    private ViewGroup mShippingAddressList;

    private TextView mShippingMethodText;

    private TextView mTotal;

    private int mCheckoutStep;

    private ViewGroup mVoucherView;

    private TextView mVoucherValue;

    private TextView mVatValue;

    private View mVatView;

    /**
     * Get instance
     * @return CheckoutSummaryFragment
     */
    public static CheckoutSummaryFragment getInstance(int checkoutStep, OrderSummary orderSummary) {
        //if (mOrderSummaryFragment == null) 
        sOrderSummaryFragment = new CheckoutSummaryFragment();
        // Save order summary
        sOrderSummaryFragment.mCheckoutStep = checkoutStep;
        // Save order summary
        sOrderSummaryFragment.mOrderSummary = orderSummary;
        // return instance
        return sOrderSummaryFragment;
    }

    /**
     * Empty constructor for nested fragment
     */
    public CheckoutSummaryFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.checkout_summary_main);
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
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        
        // Products
        mProductList = (ViewGroup) view.findViewById(R.id.checkout_summary_products_list);
        view.findViewById(R.id.checkout_summary_products_btn_edit).setOnClickListener(this);
        mVatView = view.findViewById(R.id.checkout_summary_products_vat_container);
        mVatValue = (TextView) view.findViewById(R.id.checkout_summary_products_text_vat_value);
        mShippingFeeView = (ViewGroup) view.findViewById(R.id.checkout_summary_products_shippingfee_container);
        mShippingFeeValue = (TextView) view.findViewById(R.id.checkout_summary_products_text_shippingfee);
        mVoucherView = (ViewGroup) view.findViewById(R.id.checkout_summary_products_voucher_container);
        mVoucherValue = (TextView) view.findViewById(R.id.checkout_summary_products_text_voucher);
        mSubTotal = (TextView) view.findViewById(R.id.checkout_summary_products_text_subtotal);
        mExtraCosts = (TextView) view.findViewById(R.id.checkout_summary_extra_costs_value);
        mExtraCostsContainer = (LinearLayout) view.findViewById(R.id.checkout_summary_extra_costs_container);
        // Shipping Address
        mShippingAddressView = (ViewGroup) view.findViewById(R.id.checkout_summary_include_shipping_address);
        mShippingAddressList = (ViewGroup) view.findViewById(R.id.checkout_summary_shipping_address_list);
        view.findViewById(R.id.checkout_summary_shipping_address_btn_edit).setOnClickListener(this);
        // Shipping Method
        mShippingMethodView = (ViewGroup) view.findViewById(R.id.checkout_summary_include_shipping_method);
        mShippingMethodText = (TextView) view.findViewById(R.id.checkout_summary_shipping_method_text);
        view.findViewById(R.id.checkout_summary_shipping_method_btn_edit).setOnClickListener(this);
        // Total
        mTotalView = (ViewGroup) view.findViewById(R.id.checkout_summary_include_total);
        mTotal = (TextView) view.findViewById(R.id.checkout_summary_total_text);
        //Validate is service is available
        if(JumiaApplication.mIsBound){
            // Get cart
            mCart = JumiaApplication.INSTANCE.getCart();
            if (mCart == null) triggerGetShoppingCart();
            else showOrderSummary();
        } else {
            showFragmentRetry(this);
        }

    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onStart()
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
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }

    /**
     * Show the order summary
     * @author sergiopereira
     */
    private void showOrderSummary() {
        
        // Validate current cart
        if(mCart != null && mCart.getCartItems().size() == 0) {
            showNoItems();
            return;
        }

        // Validate order summary
        if(mOrderSummary == null)  Log.w(TAG, "ORDER SUMMARY IS NULL");
        else Log.d(TAG, "ORDER SUMMARY: " + mOrderSummary.toString());

        // Validate the current checkout step
        switch (mCheckoutStep) {
        case ConstantsCheckout.CHECKOUT_PAYMENT:
            // Validate shipping method
            if(mOrderSummary != null && mOrderSummary.hasShippingMethod()) showShippingMethod(mOrderSummary.getShippingMethod());
            // continue
        case ConstantsCheckout.CHECKOUT_SHIPPING:
            // Shipping fees
            if(mOrderSummary != null) showShippingFees();
            // Validate shipping address
            if(mOrderSummary != null && mOrderSummary.hasShippingAddress()) showShippingAddress(mOrderSummary.getShippingAddress());
            // Validate total
            if(mOrderSummary != null) showTotal(mOrderSummary.getTotal());
            // continue
        case ConstantsCheckout.CHECKOUT_BILLING:
            // VAT
            if(mOrderSummary != null) showVat();
            // Voucher
            if(mOrderSummary != null) showVoucher();
            
        case ConstantsCheckout.CHECKOUT_ABOUT_YOU:
        default:
            // Show cart
            showCart();
            if(mCart != null) showPriceRules();
            break;
        }
        
    }
    
    /**
     * Show the current cart
     * @author sergiopereira
     */
    private void showCart() {
        // Show all items
        Map<String, ShoppingCartItem> mShopMapItems = mCart.getCartItems();
        ArrayList<ShoppingCartItem> mShopList = new ArrayList<ShoppingCartItem>(mShopMapItems.values());
        mProductList.removeAllViews();
        for (ShoppingCartItem item : mShopList) {
            View cartItemView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_summary_list_item, mProductList, false);
            // Name
            ((TextView) cartItemView.findViewById(R.id.order_summary_item_name)).setText(item.getName());            
            // Price
            String price = item.getPrice();
            if (!item.getPrice().equals(item.getSpecialPrice())) price = item.getSpecialPrice();  
            ((TextView) cartItemView.findViewById(R.id.order_summary_item_quantity)).setText(item.getQuantity() + " x  " + price);
            // Variation
            String variation = item.getVariation();
            if (variation != null &&
                    variation.length() > 0 &&
                    !variation.equalsIgnoreCase(",") &&
                    !variation.equalsIgnoreCase("...") &&
                    !variation.equalsIgnoreCase(".")) {
                ((TextView) cartItemView.findViewById(R.id.order_summary_item_variation)).setText(variation);
                ((TextView) cartItemView.findViewById(R.id.order_summary_item_variation)).setVisibility(View.VISIBLE);
            } 
            // Buttons
            View deleteButton = cartItemView.findViewById(R.id.order_summary_item_btn_remove);
            // deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener((OnClickListener)this);
            deleteButton.setTag(item.getConfigSimpleSKU());
            // Add view
            mProductList.addView(cartItemView);
        }
        // Sub total
        mSubTotal.setText(CurrencyFormatter.formatCurrency(mCart.getCartValue()));
        
        if(!mCart.isSumCosts()){
            // Fix NAFAMZ-7848
            mExtraCosts.setText(CurrencyFormatter.formatCurrency(new BigDecimal(mCart.getExtraCosts()).toString()));
            mExtraCostsContainer.setVisibility(View.VISIBLE);
        } else {
            mExtraCostsContainer.setVisibility(View.GONE);
        }
    }
    
    /**
     * Show the current shipping address
     * @author sergiopereira
     */
    private void showShippingAddress(Address shippingAddress) {
        Log.d(TAG, "SHOW SHIPPING ADDRESS: " + shippingAddress.getAddress());
        mShippingAddressList.removeAllViews();
        View shippingAddressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, mShippingAddressList, false);
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_name)).setText(shippingAddress.getFirstName() + " " + shippingAddress.getLastName());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_street)).setText(shippingAddress.getAddress());

        // Only use region if is available
        StringBuilder regionString = new StringBuilder();
        if(!TextUtils.isEmpty(shippingAddress.getRegion())) {
            regionString.append(shippingAddress.getRegion()).append(" ");
        }
        regionString.append(shippingAddress.getCity());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_region)).setText(regionString.toString());

        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(shippingAddress.getPostcode());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_phone)).setText(""+shippingAddress.getPhone());
        shippingAddressView.findViewById(R.id.checkout_address_item_btn_container).setVisibility(View.GONE);
        mShippingAddressList.addView(shippingAddressView);
        mShippingAddressView.setVisibility(View.VISIBLE);
    }
    
    /**
     * Show the current shipping method
     * @author sergiopereira
     */
    private void showShippingMethod(String method) {
        mShippingMethodText.setText(method);
        mShippingMethodView.setVisibility(View.VISIBLE);
    }
    
    /**
     * Show the shipping fee
     * @author sergiopereira
     */
    private void showShippingFees() {
        if(!mCart.isSumCosts()){
            mShippingFeeValue.setText(CurrencyFormatter.formatCurrency(mOrderSummary.getShippingAmount()));
        } else {
            mShippingFeeValue.setText(CurrencyFormatter.formatCurrency(mCart.getSumCostsValue()));
        }
        
        mShippingFeeView.setVisibility(View.VISIBLE);
    }
    
    /**
     * Show voucher
     * @author sergiopereira
     */
    private void showVoucher() {
        Log.d(TAG, "ORDER VOUCHER: " + mOrderSummary.getDiscountCouponValue());
        if(mOrderSummary.hasCouponDiscount()) {
            mVoucherValue.setText("- " + CurrencyFormatter.formatCurrency(mOrderSummary.getDiscountCouponValue()));
            mVoucherView.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Show Cart Price Rules
     */
    private void showPriceRules(){
        if(mCart.getPriceRules() != null && mCart.getPriceRules().size() > 0){
            Log.i(TAG, "code1rules : pass");
            LinearLayout priceRulesContainer = (LinearLayout) getView().findViewById(R.id.checkout_summary_price_rules_container);
            priceRulesContainer.setVisibility(View.VISIBLE);
            LayoutInflater mLayoutInflater = LayoutInflater.from(getBaseActivity());
            Set<String> priceRulesKeys = mCart.getPriceRules().keySet();
            for (String key : priceRulesKeys) {
                View priceRuleElement = mLayoutInflater.inflate(R.layout.price_rules_summary_element, null);
                ((TextView) priceRuleElement.findViewById(R.id.price_rules_label)).setText(key);
                ((TextView) priceRuleElement.findViewById(R.id.price_rules_value)).setText("-"+CurrencyFormatter.formatCurrency(mCart.getPriceRules().get(key)));
                priceRulesContainer.addView(priceRuleElement);
            }
            
        }
    }
    
    /**
     * Show VAT
     * @author sergiopereira
     */
    private void showVat() {
        Log.d(TAG, "ORDER VAT: " + mOrderSummary.getTaxAmount());
        mVatValue.setText(CurrencyFormatter.formatCurrency(mOrderSummary.getTaxAmount()));
        mVatView.setVisibility(View.VISIBLE);
    }
    
    /**
     * Show the current total
     * @author sergiopereira
     */
    private void showTotal(String total) {
        mTotal.setText(CurrencyFormatter.formatCurrency(total));
        mTotalView.setVisibility(View.VISIBLE);
    }
    
    /**
     * Show dialog to exit from checkout process
     * @author sergiopereira
     */
    public void showNoItems() {
        // Show dialog
        dialog = DialogGenericFragment.newInstance(true, true, false,
                getString(R.string.order_summary_label),
                getString(R.string.order_no_items),
                getString(R.string.ok_label),
                "",
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            dismissDialogFragement();
                            getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        }
                    }
                });
        // Fixed back bug
        dialog.setCancelable(false);
        dialog.show(getBaseActivity().getSupportFragmentManager(), null);
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
        // Prod Edit
        if (id == R.id.checkout_summary_products_btn_edit) onClickEditProdButton();
        // Ship Address Edit
        else if (id == R.id.checkout_summary_shipping_address_btn_edit) onClickEditAddessButton();
        // Ship Address Edit
        else if (id == R.id.checkout_summary_shipping_method_btn_edit) onClickEditMethodButton();
        // Remove
        else if (id == R.id.order_summary_item_btn_remove) onClickRemoveItemButton(view);
        // Retry button
        if(id == R.id.fragment_root_retry_button) onClickRetryButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on retry button.
     * @author paulo
     */
    private void onClickRetryButton() {
        Bundle bundle = new Bundle();
        if(null != JumiaApplication.CUSTOMER){
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
            
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
//            restartAllFragments();
        }
    }

    /**
     * Process the click on edit cart
     * @author sergiopereira
     */
    private void onClickEditProdButton() {
        Log.i(TAG, "ON CLICK: EDIT PROD");
        if(FragmentController.getInstance().hasEntry(FragmentType.SHOPPING_CART.toString()))
            FragmentController.getInstance().popAllEntriesUntil(getBaseActivity(), FragmentType.SHOPPING_CART.toString());
        else
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * Process the click on edit address
     * @author sergiopereira
     */
    private void onClickEditAddessButton() {
        Log.i(TAG, "ON CLICK: EDIT ADDRESS");
        if(FragmentController.getInstance().hasEntry(FragmentType.MY_ADDRESSES.toString()))
            FragmentController.getInstance().popAllEntriesUntil(getBaseActivity(), FragmentType.MY_ADDRESSES.toString());
        else
            getBaseActivity().onSwitchFragment(FragmentType.MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * Process the click on edit method
     */
    private void onClickEditMethodButton() {
        Log.i(TAG, "ON CLICK: EDIT METHOD");
        if(FragmentController.getInstance().hasEntry(FragmentType.SHIPPING_METHODS.toString()))
            FragmentController.getInstance().popAllEntriesUntil(getBaseActivity(), FragmentType.SHIPPING_METHODS.toString());
        else
            getBaseActivity().onSwitchFragment(FragmentType.SHIPPING_METHODS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on delete item on cart
     * @author sergiopereira
     */
    private void onClickRemoveItemButton(View view) {
        try {
            // Get sku from tag
            String sku = view.getTag().toString();
            Log.i(TAG, "ON CLICK: REMOVE ITEM: " + sku);
            // Remove clicked item
            triggerRemoveItem(sku);
        } catch (NullPointerException e) {
            Log.w(TAG, "ON DELETE CLICK", e);
        }
    }

    /**
     * ############# REQUESTS #############
     */

    /**
     * Trigger to get the shopping cart
     * @author sergiopereira
     */
    private void triggerGetShoppingCart() {
        Log.i(TAG, "TRIGGER: GET SHOPPING CART");
        triggerContentEvent(new GetShoppingCartItemsHelper(), null, this);
    }
    
    /**
     * Trigger to remove an item from the shopping cart
     * @author sergiopereira
     * @param sku
     */
    private void triggerRemoveItem(String sku){
        ContentValues values = new ContentValues();
        values.put("sku", sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartRemoveItemHelper.ITEM, values);
        triggerContentEventProgress(new GetShoppingCartRemoveItemHelper(), bundle, (IResponseCallback) this);
    }
    
    /**
     * ############# RESPONSE #############
     */

    /**
     * Process the success response
     * @param bundle
     * @return
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
        case GET_SHOPPING_CART_ITEMS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHOPPING_CART_ITEMS_EVENT");
            mCart = (ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            showOrderSummary();
            showFragmentContentContainer();
            break;
        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
            Log.d(TAG, "RECEIVED REMOVE_ITEM_FROM_SHOPPING_CART_EVENT");
            mCart = (ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            showOrderSummary();
            getBaseActivity().updateNavigationMenu();
            hideActivityProgress();
            showFragmentContentContainer();
            break;
        default:
            Log.d(TAG, "RECEIVED UNKNOWN EVENT");
            break;
        }

        return true;
    }

    /**
     * Process the error response
     * @param bundle
     * @return
     */
    protected boolean onErrorEvent(Bundle bundle) {
        
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        // Generic error
        if (super.handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
        case GET_SHOPPING_CART_ITEMS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHOPPING_CART_ITEMS_EVENT");
            break;
        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
            hideActivityProgress();
            Log.d(TAG, "RECEIVED REMOVE_ITEM_FROM_SHOPPING_CART_EVENT");
            break;
        default:
            Log.d(TAG, "RECEIVED UNKNOWN EVENT");
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
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }

}

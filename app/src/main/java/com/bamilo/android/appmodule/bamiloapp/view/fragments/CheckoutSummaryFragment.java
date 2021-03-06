package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.GetShoppingCartItemsHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ShoppingCartRemoveItemHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.objects.addresses.Address;
import com.bamilo.android.framework.service.objects.cart.PurchaseCartItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;
import com.bamilo.android.appmodule.bamiloapp.utils.cart.UICartUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogGenericFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.product.UIProductUtils;
import com.bamilo.android.R;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Class used to show the order summary in the checkout process
 *
 * @author sergiopereira
 */
public class CheckoutSummaryFragment extends BaseFragment implements IResponseCallback {

    public static final String TAG = CheckoutSummaryFragment.class.getSimpleName();

    private ViewGroup mProductList;

    private TextView mSubTotal;

    private TextView mExtraCosts;

    private LinearLayout mExtraCostsContainer;

    private TextView mShippingFeeValue;

    private ViewGroup mShippingFeeView;

    private ViewGroup mShippingAddressView;

    private ViewGroup mShippingMethodView;

    private PurchaseEntity mOrderSummary;

    private ViewGroup mShippingAddressList;

    private TextView mShippingMethodText;

    private int mCheckoutStep;

    private ViewGroup mVoucherView;

    private TextView mVoucherValue;

    private LinearLayout mPriceRules;

    /**
     * Get instance
     *
     * @return CheckoutSummaryFragment
     */
    public static CheckoutSummaryFragment getInstance(int checkoutStep, PurchaseEntity orderSummary) {
        CheckoutSummaryFragment sOrderSummaryFragment = new CheckoutSummaryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsIntentExtra.ARG_1, checkoutStep);
        bundle.putParcelable(ConstantsIntentExtra.DATA, orderSummary);
        sOrderSummaryFragment.setArguments(bundle);
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Arguments
        Bundle savedState = savedInstanceState != null ? savedInstanceState : getArguments();
        if(savedState != null) {
            mCheckoutStep = savedState.getInt(ConstantsIntentExtra.ARG_1);
            mOrderSummary = savedState.getParcelable(ConstantsIntentExtra.DATA);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Products
        mProductList = (ViewGroup) view.findViewById(R.id.checkout_summary_products_list);
        view.findViewById(R.id.checkout_summary_products_btn_edit).setOnClickListener(this);
        mShippingFeeView = (ViewGroup) view.findViewById(R.id.checkout_summary_products_shippingfee_container);
        mShippingFeeValue = (TextView) view.findViewById(R.id.checkout_summary_products_text_shippingfee);
        mVoucherView = (ViewGroup) view.findViewById(R.id.checkout_summary_products_voucher_container);
        mVoucherValue = (TextView) view.findViewById(R.id.checkout_summary_products_text_voucher);
        mSubTotal = (TextView) view.findViewById(R.id.checkout_summary_products_text_subtotal);
        mExtraCosts = (TextView) view.findViewById(R.id.checkout_summary_extra_costs_value);
        mExtraCostsContainer = (LinearLayout) view.findViewById(R.id.checkout_summary_extra_costs_container);
        // Price rules
        mPriceRules = (LinearLayout) view.findViewById(R.id.checkout_summary_price_rules_container);
        // Shipping Address
        mShippingAddressView = (ViewGroup) view.findViewById(R.id.checkout_summary_include_shipping_address);
        mShippingAddressList = (ViewGroup) view.findViewById(R.id.checkout_summary_shipping_address_list);
        view.findViewById(R.id.checkout_summary_shipping_address_btn_edit).setOnClickListener(this);
        // Shipping Method
        mShippingMethodView = (ViewGroup) view.findViewById(R.id.checkout_summary_include_shipping_method);
        mShippingMethodText = (TextView) view.findViewById(R.id.checkout_summary_shipping_method_text);
        view.findViewById(R.id.checkout_summary_shipping_method_btn_edit).setOnClickListener(this);
        // Get saved order summary
        if (mOrderSummary != null) {
            showOrderSummary();
        } else {
            triggerGetShoppingCart();
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mOrderSummary != null) {
            outState.putInt(ConstantsIntentExtra.ARG_1, mCheckoutStep);
            outState.putParcelable(ConstantsIntentExtra.DATA, mOrderSummary);
        }
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
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
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
    }

    /**
     * Method used to update the checkout summary case is not in stopping process.
     */
    public void onUpdate(int checkoutStep, PurchaseEntity orderSummary) {
        // Validate fragment visibility
        if (isOnStoppingProcess && getBaseActivity() != null) {
            return;
        }
        mCheckoutStep = checkoutStep;
        mOrderSummary = orderSummary;
        showOrderSummary();
    }

    /**
     * Show the order summary
     *
     * @author sergiopereira
     */
    private void showOrderSummary() {
        // Validate current cart
        if (mOrderSummary == null || mOrderSummary.getCartItems().size() == 0) {
            showNoItems();
            return;
        }


        // Validate the current checkout step
        switch (mCheckoutStep) {
            case ConstantsCheckout.CHECKOUT_PAYMENT:
                // Validate shipping method
                if (mOrderSummary.hasShippingMethod()) {
                    showShippingMethod(mOrderSummary.getShippingMethod());
                }
                // Shipping fees
                UICartUtils.setShippingRule(mOrderSummary, mShippingFeeView, mShippingFeeValue, mExtraCostsContainer, mExtraCosts);
                // continue
            case ConstantsCheckout.CHECKOUT_SHIPPING:
                // Validate shipping address
                if (mOrderSummary.hasShippingAddress()) {
                    showShippingAddress(mOrderSummary.getShippingAddress());
                }
                // continue
            case ConstantsCheckout.CHECKOUT_BILLING:
                // Voucher
                showVoucher();
                // continue
            case ConstantsCheckout.CHECKOUT_ABOUT_YOU:
            default:
                // Show cart
                showCart();
                CheckoutStepManager.showPriceRules(getBaseActivity(), mPriceRules, mOrderSummary.getPriceRules());
                break;
        }

    }

    /**
     * Show the current cart
     *
     * @author sergiopereira
     */
    private void showCart() {
        // Show all items
        ArrayList<PurchaseCartItem> mShopList = new ArrayList<>(mOrderSummary.getCartItems());
        mProductList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getBaseActivity());
        for (PurchaseCartItem item : mShopList) {
            // Add view
            View cartItemView = inflater.inflate(R.layout.checkout_summary_list_item, mProductList, false);
            ImageView mImageView = (ImageView) cartItemView.findViewById(R.id.image_view);
            View pBar = cartItemView.findViewById(R.id.image_loading_progress);
            ImageView shopFirstImageView = (ImageView) cartItemView.findViewById(R.id.item_shop_first);
            TextView variationName = (TextView) cartItemView.findViewById(R.id.item_text_variation);
            TextView variationValue = (TextView) cartItemView.findViewById(R.id.item_text_variation_value);
            View deleteButton = cartItemView.findViewById(R.id.button_delete);
            // Name
            ((TextView) cartItemView.findViewById(R.id.item_name)).setText(item.getName());
            // Shop First
            UIProductUtils.setShopFirst(item, shopFirstImageView);
            UIProductUtils.showShopFirstOverlayMessage(this, item, shopFirstImageView);
            // Image
            ImageManager.getInstance().loadImage(item.getImageUrl(), mImageView, pBar, R.drawable.no_image_large, false);
            // Price
            double price = item.hasDiscount() ? item.getSpecialPrice() : item.getPrice();
            String text = getString(R.string.first_x_second_placeholder, item.getQuantity(), CurrencyFormatter.formatCurrency(price));
            ((TextView) cartItemView.findViewById(R.id.item_regprice)).setText(text);
            // Variation
            UICartUtils.setVariation(item, variationName, variationValue);
            // Buttons
            deleteButton.setOnClickListener(this);
            deleteButton.setTag(item.getConfigSimpleSKU());
            // Add view
            mProductList.addView(cartItemView);
        }
        // Sub total
        mSubTotal.setText(CurrencyFormatter.formatCurrency(mOrderSummary.getTotal()));

        if (!mOrderSummary.hasSumCosts() && mOrderSummary.getExtraCosts() != 0d) {
            // Fix NAFAMZ-7848
            mExtraCosts.setText(CurrencyFormatter.formatCurrency(new BigDecimal(mOrderSummary.getExtraCosts()).toString()));
            mExtraCostsContainer.setVisibility(View.VISIBLE);
        } else {
            mExtraCostsContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Show the current shipping address
     *
     * @author sergiopereira
     */
    private void showShippingAddress(Address shippingAddress) {
        mShippingAddressList.removeAllViews();
        View shippingAddressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, mShippingAddressList, false);
        String name = getString(R.string.first_space_second_placeholder, shippingAddress.getFirstName(), shippingAddress.getLastName());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_name)).setText(name);
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_street)).setText(shippingAddress.getAddress());
        shippingAddressView.findViewById(R.id.checkout_address_item_btn_edit).setVisibility(View.GONE);

        // Only use region if is available
        StringBuilder regionString = new StringBuilder();
        if (!TextUtils.isEmpty(shippingAddress.getRegion())) {
            regionString.append(shippingAddress.getRegion()).append(" ");
        }
        regionString.append(shippingAddress.getCity());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_region)).setText(regionString.toString());

        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(shippingAddress.getPostcode());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_phone)).setText(shippingAddress.getPhone());
        mShippingAddressList.addView(shippingAddressView);
        mShippingAddressView.setVisibility(View.VISIBLE);
    }

    /**
     * Show the current shipping method
     *
     * @author sergiopereira
     */
    private void showShippingMethod(String method) {
        mShippingMethodText.setText(method);
        mShippingMethodView.setVisibility(View.VISIBLE);
    }

    /**
     * Show voucher
     *
     * @author sergiopereira
     */
    private void showVoucher() {
        if (mOrderSummary.hasCouponDiscount()) {
            mVoucherValue.setText(getString(R.string.placeholder_discount, CurrencyFormatter.formatCurrency(mOrderSummary.getCouponDiscount())));
            mVoucherView.setVisibility(View.VISIBLE);
        } else {
            mVoucherView.setVisibility(View.GONE);
        }
    }

    /**
     * Show dialog to exit from checkout process
     *
     * @author sergiopereira
     */
    public void showNoItems() {
        // Show dialog
        dialog = DialogGenericFragment.newInstance(true, false,
                getString(R.string.order_summary_label),
                getString(R.string.order_no_items),
                getString(R.string.ok_label),
                "",
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            dismissDialogFragment();
                            BamiloApplication.INSTANCE.setCart(null);
                            getBaseActivity().updateCartInfo();
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
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Prod Edit
        if (id == R.id.checkout_summary_products_btn_edit) onClickEditProdButton();
        // Ship Address Edit
        else if (id == R.id.checkout_summary_shipping_address_btn_edit) onClickEditAddessButton();
        // Ship Address Edit
        else if (id == R.id.checkout_summary_shipping_method_btn_edit) onClickEditMethodButton();
        // Remove
        else if (id == R.id.button_delete) {
            onClickRemoveItemButton(view);
        }
        // Unknown view
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
        Bundle bundle = new Bundle();
        if (null != BamiloApplication.CUSTOMER) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on edit cart
     *
     * @author sergiopereira
     */
    private void onClickEditProdButton() {
        if (!getBaseActivity().popBackStackUntilTag(FragmentType.SHOPPING_CART.toString())) {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on edit address
     *
     * @author sergiopereira
     */
    private void onClickEditAddessButton() {
        if (!getBaseActivity().popBackStackUntilTag(FragmentType.CHECKOUT_MY_ADDRESSES.toString())) {
            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on edit method
     */
    private void onClickEditMethodButton() {
        if (!getBaseActivity().popBackStackUntilTag(FragmentType.CHECKOUT_SHIPPING.toString())) {
            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_SHIPPING, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on delete item on cart
     *
     * @author sergiopereira
     */
    private void onClickRemoveItemButton(View view) {
        try {
            // Get sku from tag
            String sku = view.getTag().toString();
            // Remove clicked item
            triggerRemoveItem(sku);
        } catch (NullPointerException e) {
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
        triggerContentEvent(new GetShoppingCartItemsHelper(), null, this);
    }

    /**
     * Trigger to remove an item from the shopping cart
     */
    private void triggerRemoveItem(String sku) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.SKU, sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEventProgress(new ShoppingCartRemoveItemHelper(), bundle, this);
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
    public void onRequestError(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        switch (eventType) {
            case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
                hideActivityProgress();
                break;
            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            return;
        }

        switch (eventType) {
            case GET_SHOPPING_CART_ITEMS_EVENT:
                mOrderSummary = (PurchaseEntity) baseResponse.getContentData();
                showOrderSummary();
                showFragmentContentContainer();
                break;
            case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
                mOrderSummary = (PurchaseEntity) baseResponse.getContentData();
                showOrderSummary();
                hideActivityProgress();
                showFragmentContentContainer();
                break;
            default:
                break;
        }
    }

}

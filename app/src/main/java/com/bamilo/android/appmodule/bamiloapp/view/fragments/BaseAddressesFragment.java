package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.objects.addresses.Address;
import com.bamilo.android.framework.service.objects.addresses.Addresses;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * The base class used to show the customer addresses for Account and Checkout.
 * @author sergio pereira
 */
public abstract class BaseAddressesFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = BaseAddressesFragment.class.getSimpleName();
    private ViewGroup mShippingView;
    private ViewGroup mBillingView;
    private ViewGroup mOthersView;
    private View mButtonBottom;
    protected Addresses mAddresses;

    /**
     * Constructor
     */
    public BaseAddressesFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @StringRes int titleResId, @ConstantsCheckout.CheckoutType int titleCheckout) {
        super(enabledMenuItems, action, R.layout.checkout_addresses_main, titleResId, ADJUST_CONTENT, titleCheckout);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get sections
        mShippingView = view.findViewById(R.id.checkout_addresses_section_shipping);
        mBillingView = view.findViewById(R.id.checkout_addresses_section_billing);
        mOthersView = view.findViewById(R.id.checkout_addresses_section_other);
        // Set buttons
        view.findViewById(R.id.checkout_addresses_button_add_top).setOnClickListener(this);
        mButtonBottom = view.findViewById(R.id.checkout_addresses_button_add_bottom);
        mButtonBottom.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get addresses
        if(mAddresses == null || onlyHasTitleChild(mShippingView)) {
            triggerGetForm();
        } else {
            showFragmentContentContainer();
        }
    }

    protected boolean onlyHasTitleChild(ViewGroup group) {
        return group.getChildCount() == 1;
    }

    protected void showAddresses(Addresses addresses) {
        // Save addresses
        mAddresses = addresses;
        // Same address
        boolean isSameAddress = addresses.hasDefaultShippingAndBillingAddress();
        // Set shipping container
        TextView shippingTitle = mShippingView.findViewById(R.id.checkout_address_title);
        shippingTitle.setText(getString(isSameAddress ? R.string.address_shipping_billing_label : R.string.address_shipping_label));
        addAddress(mShippingView, addresses.getShippingAddress(), false);
        // Set billing container
        if (isSameAddress) {
            mBillingView.setVisibility(View.GONE);
        } else {
            TextView billingTitle = mBillingView.findViewById(R.id.checkout_address_title);
            billingTitle.setText(getString(R.string.address_billing_label));
            addAddress(mBillingView, addresses.getBillingAddress(), false);
        }
        // Show other container
        if (CollectionUtils.isNotEmpty(addresses.getAddresses())) {
            TextView othersTitle = mOthersView.findViewById(R.id.checkout_address_title);
            othersTitle.setText(getString(R.string.address_others_label));
            addAddresses(mOthersView, addresses.getAddresses());
        } else {
            mOthersView.setVisibility(View.GONE);
            mButtonBottom.setVisibility(View.GONE);
        }
        // Show container
        showFragmentContentContainer();
    }


    /**
     * Add addresses to the radio group
     *
     * @author sergiopereira
     */
    private void addAddresses(ViewGroup container, HashMap<String, Address> addresses) {
        for (LinkedHashMap.Entry<String, Address> item : addresses.entrySet()) {
            Address otherAddress = item.getValue();
            addAddress(container, otherAddress, true);
        }
    }

    /**
     * Add the current address to the radio group
     *
     * @author sergiopereira
     */
    private void addAddress(ViewGroup container, Address address, boolean isOther) {
        View addressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, container, false);
        setAddressView(addressView, address, address.getId(), isOther);
        container.addView(addressView);
    }

    /**
     * Set the address view
     *
     * @param parent  the main view
     * @param address the current address
     * @param tag     the tag to associate
     * @param isOther the flag that indicates whether is an Other address to change error message
     * @author sergiopereira
     */
    private void setAddressView(View parent, Address address, int tag, boolean isOther) {
        // Text
        ((TextView) parent.findViewById(R.id.checkout_address_item_name)).setText(String.format(getString(R.string.first_space_second_placeholder), address.getFirstName(), address.getLastName()));
        ((TextView) parent.findViewById(R.id.checkout_address_item_street)).setText(address.getAddress());
        // Only use region if is available
        String regionString = address.getCity();
        if (TextUtils.isNotEmpty(address.getRegion())) {
            regionString = String.format(getString(R.string.first_space_second_placeholder), address.getRegion(), address.getCity());
        }
        ((TextView) parent.findViewById(R.id.checkout_address_item_region)).setText(regionString);
        ((TextView) parent.findViewById(R.id.checkout_address_item_postcode)).setText(address.getPostcode());
        ((TextView) parent.findViewById(R.id.checkout_address_item_phone)).setText(address.getPhone());

        if(!address.isValid()){
            TextView checkoutInvalid = parent.findViewById(R.id.checkout_address_invalid);
            if(isOther){
                checkoutInvalid.setText(getString(R.string.invalid_address_other));
            }
            checkoutInvalid.findViewById(R.id.checkout_address_invalid).setVisibility(View.VISIBLE);
        }


        // Buttons
        View editBtn = parent.findViewById(R.id.checkout_address_item_btn_edit);
        editBtn.setTag(tag);
        editBtn.setOnClickListener(this);
    }

    /*
     * ############# REQUESTS #############
     */

    protected void triggerGetForm() {
        triggerGetAddresses();
    }

    protected abstract void triggerGetAddresses();

    /*
     * ############# CLICK LISTENER #############
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Add new
        if (id == R.id.checkout_addresses_button_add_top || id == R.id.checkout_addresses_button_add_bottom) onClickCreateAddressButton();
        // Edit button
        else if (id == R.id.checkout_address_item_btn_edit) onClickEditAddressButton(view);
        // Unknown view
        else super.onClick(view);
    }

    /**
     * Process the click on edit button.</br>
     * Gets the address id from view tag.
     */
    protected abstract void onClickEditAddressButton(View view);

    /**
     * Process the click on add button.
     */
    protected abstract void onClickCreateAddressButton();


}

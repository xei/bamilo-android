package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.HashMap;
import java.util.Map;
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
        super(enabledMenuItems, action, R.layout.checkout_addresses, titleResId, ADJUST_CONTENT, titleCheckout);
    }

    /*
     * ############# LIFE CYCLE #############
     */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get sections
        mShippingView = (ViewGroup) view.findViewById(R.id.checkout_addresses_section_shipping);
        mBillingView = (ViewGroup) view.findViewById(R.id.checkout_addresses_section_billing);
        mOthersView = (ViewGroup) view.findViewById(R.id.checkout_addresses_section_other);
        // Set buttons
        view.findViewById(R.id.checkout_addresses_button_add_top).setOnClickListener(this);
        mButtonBottom = view.findViewById(R.id.checkout_addresses_button_add_bottom);
        mButtonBottom.setOnClickListener(this);
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
        // Get addresses
        if(mAddresses == null || onlyHasTitleChild(mShippingView)) {
            triggerGetForm();
        } else {
            showFragmentContentContainer();
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /*
     * ############# LAYOUT #############
     */

    protected boolean onlyHasTitleChild(ViewGroup group) {
        return group.getChildCount() == 1;
    }

    protected void showAddresses(Addresses addresses) {
        // Save addresses
        mAddresses = addresses;
        // Same address
        boolean isSameAddress = addresses.hasDefaultShippingAndBillingAddress();
        // Set shipping container
        TextView shippingTitle = (TextView) mShippingView.findViewById(R.id.checkout_address_title);
        shippingTitle.setText(getString(isSameAddress ? R.string.address_shipping_billing_label : R.string.address_shipping_label));
        addAddress(mShippingView, addresses.getShippingAddress());
        // Set billing container
        if (isSameAddress) {
            mBillingView.setVisibility(View.GONE);
        } else {
            TextView billingTitle = (TextView) mBillingView.findViewById(R.id.checkout_address_title);
            billingTitle.setText(getString(R.string.address_billing_label));
            addAddress(mBillingView, addresses.getBillingAddress());
        }
        // Show other container
        if (CollectionUtils.isNotEmpty(addresses.getAddresses())) {
            TextView othersTitle = (TextView) mOthersView.findViewById(R.id.checkout_address_title);
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
        for (Map.Entry<String, Address> item : addresses.entrySet()) {
            Address otherAddress = item.getValue();
            addAddress(container, otherAddress);
        }
    }

    /**
     * Add the current address to the radio group
     *
     * @author sergiopereira
     */
    private void addAddress(ViewGroup container, Address address) {
        Print.d(TAG, "ADD ADDRESS: " + address.getId());
        View addressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, container, false);
        setAddressView(addressView, address, address.getId());
        container.addView(addressView);
    }

    /**
     * Set the address view
     *
     * @param parent  the main view
     * @param address the current address
     * @param tag     the tag to associate
     * @author sergiopereira
     */
    private void setAddressView(View parent, Address address, int tag) {
        // Text
        ((TextView) parent.findViewById(R.id.checkout_address_item_name)).setText(String.format(getString(R.string.first_space_second_placeholders), address.getFirstName(), address.getLastName()));
        ((TextView) parent.findViewById(R.id.checkout_address_item_street)).setText(address.getAddress());
        // Only use region if is available
        String regionString = address.getCity();
        if (TextUtils.isNotEmpty(address.getRegion())) {
            regionString = String.format(getString(R.string.first_space_second_placeholders), address.getRegion(), address.getCity());
        }
        ((TextView) parent.findViewById(R.id.checkout_address_item_region)).setText(regionString);
        ((TextView) parent.findViewById(R.id.checkout_address_item_postcode)).setText(address.getPostcode());
        ((TextView) parent.findViewById(R.id.checkout_address_item_phone)).setText(address.getPhone());
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

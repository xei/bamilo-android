package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.objects.Address;
import com.mobile.framework.output.Print;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.Addresses;
import com.mobile.utils.GenericRadioGroup;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/02/24
 */
public abstract class MyAddressesFragment extends BaseFragment implements IResponseCallback, RadioGroup.OnCheckedChangeListener{

    private static final String TAG = LogTagHelper.create(MyAddressesFragment.class);

    private static final String IS_SAME_ADDRESS = "1";

    private static final String ISNT_SAME_ADDRESS = "0";

    protected static final String BILLING_ID_TAG = "billingAddressId";

    protected static final String SHIPPING_ID_TAG = "shippingAddressId";

    protected static final String IS_SAME_TAG = "shippingAddressDifferent";

    protected GenericRadioGroup mTopRadioGroup;

    protected GenericRadioGroup mBottomRadioGroup;

    protected Addresses addresses;

    protected CheckBox mIsSameCheckBox;

    protected TextView mTopTitle;

    protected TextView mBottomTitle;

    protected View mTopAddContainer;

    protected ScrollView mMainScrollView;

    protected Form hiddenForm;

    protected static String sameAddress = "";

    protected MyAddressesFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId, KeyboardState adjust_state) {
        super(enabledMenuItems, action, R.layout.checkout_my_addresses, titleResId, adjust_state);
    }

    protected MyAddressesFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId, KeyboardState adjust_state, int titleCheckout) {
        super(enabledMenuItems, action, R.layout.checkout_my_addresses, titleResId, adjust_state, titleCheckout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get the main scroll view that can be null
        mMainScrollView = (ScrollView) view.findViewById(R.id.checkout_addresses_one_scroll);
        // Get containers
        mTopTitle = (TextView) view.findViewById(R.id.checkout_addresses_default_title);
        mTopRadioGroup = (GenericRadioGroup) view.findViewById(R.id.checkout_addresses_default_container);
        mTopRadioGroup.setOnCheckedChangeListener(this);
        mBottomTitle = (TextView) view.findViewById(R.id.checkout_addresses_other_title);
        mBottomRadioGroup = (GenericRadioGroup) view.findViewById(R.id.checkout_addresses_other_container);
        mBottomRadioGroup.setOnCheckedChangeListener(this);
        mIsSameCheckBox = (CheckBox) view.findViewById(R.id.checkout_address_billing_checkbox);
        mIsSameCheckBox.setOnClickListener(this);
        // Buttons
        mTopAddContainer = view.findViewById(R.id.checkout_addresses_top_button_container);
        view.findViewById(R.id.checkout_addresses_default_add).setOnClickListener(this);
        view.findViewById(R.id.checkout_addresses_other_add).setOnClickListener(this);
        view.findViewById(R.id.checkout_addresses_button_enter).setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Flag
        sameAddress = "";
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
        // Get and show addresses
        triggerGetForm();
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
        // Submit
        if(id == R.id.checkout_addresses_button_enter) onClickSubmitAddressesButton();
            // Add new
        else if(id == R.id.checkout_addresses_default_add) onClickCreateAddressButton();
            // Add new
        else if(id == R.id.checkout_addresses_other_add) onClickCreateAddressButton();
            // Edit button
        else if(id == R.id.checkout_address_item_btn_edit) onClickEditAddressButton(view);
            // Delete button
        else if(id == R.id.checkout_address_item_btn_delete) onClickDeleteAddressButton(view);
            // Check box
        else if(id == R.id.checkout_address_billing_checkbox) onClickCheckBox((CheckBox) view);
            // Unknown view
        else Print.i(TAG, "ON CLICK: UNKNOWN VIEW " + view.getTag());
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
    protected abstract void onClickRetryButton() ;

    /**
     * Process the click on add button.
     * @author sergiopereira
     */
    protected abstract void onClickCreateAddressButton() ;

    /**
     * Process the click on check box.
     * @param view
     * @author sergiopereira
     */
    protected void onClickCheckBox(final CheckBox view){
        Print.d(TAG, "SAME ADDRESS CLICK: " + view.isChecked());
        // Show loading
        showFragmentLoading();
        // Validate the current selection
        validateCurrentShippingSelection();
        // Clean containers
        cleanContainers();
        // Show loading for new redesign
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isVisible()){
                    // Show address
                    showAddresses(view.isChecked());
                }
            }
        }, 750);
    }



    /**
     * Validate the shipping selection and if not zero switch the addresses
     * @author sergiopereira
     */
    private void validateCurrentShippingSelection(){
        // Validate if is not the current shipping address
        int position = mTopRadioGroup.getCheckedPosition();
        if(position > 0) addresses.switchShippingAddress(position - 1);
    }


    /**
     * Process the click on delete button.
     * @param view
     * @author sergiopereira
     */
    private void onClickDeleteAddressButton(View view){
        String addressId = view.getTag().toString();
        Print.i(TAG, "ON CLICK: DELETE ADDRESS " + addressId);
    }

    /**
     * Process the click on edit button.</br>
     * Gets the address id from view tag.
     * @param view
     * @author sergiopereira
     */
    protected abstract void onClickEditAddressButton(View view);

    /**
     * Process the click on submit button.</br>
     */
    private void onClickSubmitAddressesButton() {
        Print.i(TAG, "ON CLICK: SUBMIT");
        // Validate the is same check box
        if (mIsSameCheckBox.isChecked()) submitSameAddresses();
        else submitDifferentAddresses();
    }


    /**
     * Submit the same address for shipping and billing
     * @author sergiopereira
     */
    private void submitSameAddresses(){
        // Validate radio group
        if (mTopRadioGroup.isValidateGroup()){
            // Get addresses id and submit
            String addressId = mTopRadioGroup.getCheckedTag().toString();
            submitForm(addressId, addressId, IS_SAME_ADDRESS);
        } else if(mBottomRadioGroup.isValidateGroup()){
            // Get addresses id and submit
            String addressId = mBottomRadioGroup.getCheckedTag().toString();
            submitForm(addressId, addressId, IS_SAME_ADDRESS);
        } else {
            Toast.makeText(getBaseActivity(), getString(R.string.billing_choose_address), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Submit the different addresses for shipping and billing
     * @author sergiopereira
     */
    private void submitDifferentAddresses() {
        // Validate radio groups
        if (mTopRadioGroup.isValidateGroup() && mBottomRadioGroup.isValidateGroup()) {
            // Get addresses id
            String shippingAddressId = mTopRadioGroup.getCheckedTag().toString();
            String billingAddressId = mBottomRadioGroup.getCheckedTag().toString();
            String isSameAddress = ISNT_SAME_ADDRESS;
            if (shippingAddressId != null) {
                isSameAddress = (shippingAddressId.equals(billingAddressId)) ? IS_SAME_ADDRESS : ISNT_SAME_ADDRESS;
            }
            // Submit values
            submitForm(shippingAddressId, billingAddressId, isSameAddress);
        } else {
            Toast.makeText(getBaseActivity(), getString(R.string.billing_choose_address), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Submit the current values using the form
     * @param shippingAddressId
     * @param billingAddressId
     * @param isDifferent
     */
    protected abstract void submitForm(String shippingAddressId, String billingAddressId, String isDifferent);

    /**
     * ########## ON CHECK CHANGE LISTENER ##########
     */

    /*
     * (non-Javadoc)
     * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedPos) {
        Print.d(TAG, "CHECKED RADIO TAG: " + ((GenericRadioGroup) group).getCheckedTag());
        // Is address is same for all
        if(mIsSameCheckBox.isChecked()) switchBetweenRadioGroups((GenericRadioGroup) group);
    }

    /**
     * Switch focus between radio group
     * @param parent
     * @author sergiopereira
     */
    private void switchBetweenRadioGroups(GenericRadioGroup parent){
        // Validate radio group
        int radioGroupId = parent.getId();
        // Checked on top group uncheck bottom group
        if(radioGroupId == mBottomRadioGroup.getId()) mTopRadioGroup.clearCheckGroup();
            // Checked on bottom group uncheck top group
        else if (radioGroupId == mTopRadioGroup.getId()) mBottomRadioGroup.clearCheckGroup();
    }

    /**
     * ########## FILL CONTENT ##########
     */

    /**
     * Show my addresses
     * @param isSameAddress
     * @author sergiopereira
     */
    protected void showAddresses(boolean isSameAddress) {
        Print.d(TAG, "SHOW ADDRESSES: " + isSameAddress);
        // Clear containers before adding to them
        if(mBottomRadioGroup != null){
            mBottomRadioGroup.removeAllViews();
        }
        if(mTopRadioGroup != null){
            mTopRadioGroup.removeAllViews();
        }
        // Set flag
        sameAddress = "" + isSameAddress;
        // Validate current address
        if(isSameAddress){
            // Set top container
            mTopTitle.setText(getString(R.string.billing_def_shipping_label));
            mTopTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            // Set Shipping Address (checked)
            mTopRadioGroup.setCheckedItem(0);
            addAddress(mTopRadioGroup, addresses.getShippingAddress());
            // Set the check box checked
            mIsSameCheckBox.setChecked(isSameAddress);
            // Hide add button
            mTopAddContainer.setVisibility(View.GONE);
            // Set bottom container
            mBottomTitle.setText(getString(R.string.billing_others_label));
            // Add billing address if different
            if(!addresses.hasDefaultShippingAndBillingAddress()) addAddress(mBottomRadioGroup, addresses.getBillingAddress());
            // Set the other addresses
            addAddresses(mBottomRadioGroup, addresses.getAddresses());
        }else{

            Address shippingAddress = addresses.getShippingAddress();
            Address billingAddress = addresses.getBillingAddress();

            // Set top container
            mTopTitle.setText(getString(R.string.billing_shipping_label));
            // mTopTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_addaddress_orange, 0);
            // Set Shipping Address (checked) and others
            mTopRadioGroup.setCheckedItem(0);
            addAddress(mTopRadioGroup, shippingAddress);
            if(!addresses.hasDefaultShippingAndBillingAddress()){
                addAddress(mTopRadioGroup, billingAddress);
            }

            addAddresses(mTopRadioGroup, addresses.getAddresses());
            // Set the check box checked
            mIsSameCheckBox.setChecked(isSameAddress);
            // Show add button
            mTopAddContainer.setVisibility(View.VISIBLE);
            // Set bottom container
            mBottomTitle.setText(getString(R.string.billing_billing_label));
            // Set Billing address (checked) and others
            mBottomRadioGroup.setCheckedItem(0);
            addAddress(mBottomRadioGroup, billingAddress);
            if(!addresses.hasDefaultShippingAndBillingAddress()){
                addAddress(mBottomRadioGroup, shippingAddress);
            }

            addAddresses(mBottomRadioGroup, addresses.getAddresses());
        }
        // Show content
        showFragmentContentContainer();
    }

    /**
     * Clean the view
     * @author sergiopereira
     */
    protected void cleanContainers(){
        if(mMainScrollView != null) mMainScrollView.fullScroll(ScrollView.FOCUS_UP);
        if(mTopRadioGroup != null) mTopRadioGroup.removeAllViews();
        if(mBottomRadioGroup != null) mBottomRadioGroup.removeAllViews();
    }

    /**
     * Add addresses to the radio group
     * @param container the radio group
     * @param addresses
     * @author sergiopereira
     */
    private void addAddresses(GenericRadioGroup container, HashMap<String, Address> addresses) {
        for (Map.Entry<String, Address> item : addresses.entrySet()) {
            Address otherAddress = item.getValue();
            addAddress(container, otherAddress);
        }
    }

    /**
     * Add the current address to the radio group
     * @param container
     * @param address
     * @author sergiopereira
     */
    private void addAddress(GenericRadioGroup container, Address address){
        Print.d(TAG, "ADD ADDRESS: " + address.getId());
        View addressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, container, false);
        setAddressView(addressView, address, "" + address.getId());
        container.addView(addressView, "" + address.getId());
    }

    /**
     * Set the address view
     * @param parent the main view
     * @param address the current address
     * @param tag the tag to associate
     * @author sergiopereira
     */
    private void setAddressView(View parent, Address address, String tag) {
        // Text
        ((TextView) parent.findViewById(R.id.checkout_address_item_name)).setText(address.getFirstName() + " " + address.getLastName());
        ((TextView) parent.findViewById(R.id.checkout_address_item_street)).setText(address.getAddress());

        // Only use region if is available
        StringBuilder regionString = new StringBuilder();
        if (!TextUtils.isEmpty(address.getRegion())) {
            regionString.append(address.getRegion()).append(" ");
        }
        regionString.append(address.getCity());
        ((TextView) parent.findViewById(R.id.checkout_address_item_region)).setText(regionString.toString());

        ((TextView) parent.findViewById(R.id.checkout_address_item_postcode)).setText(address.getPostcode());
        ((TextView) parent.findViewById(R.id.checkout_address_item_phone)).setText("" + address.getPhone());
        parent.findViewById(R.id.checkout_address_item_divider).setVisibility(View.VISIBLE);
        // Buttons
        View editBtn = parent.findViewById(R.id.checkout_address_item_btn_edit);
        View deleteBtn = parent.findViewById(R.id.checkout_address_item_btn_delete);
        // deleteBtn.setVisibility(View.VISIBLE);
        editBtn.setTag(tag);
        deleteBtn.setTag(tag);
        editBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    /**
     * Method used to validate if the user has shipping and billing address
     * @return true or false
     * @author sergiopereira
     */
    protected boolean isValidateResponse(){
        // Validate addresses at this point the user should have addresses
        try {
            if(addresses.hasBillingAddress() && addresses.hasShippingAddress()){ return true; }
            else { Print.w(TAG, "WARNING: CUSTOMER SHIPPING OR BILLING ADDRESS IS NULL"); return false; }
        } catch (NullPointerException e) { Print.w(TAG, "WARNING: ADDRESSES IS NULL"); return false; }
    }

    /**
     * ############# REQUESTS #############
     */

    protected abstract void triggerGetForm();

    /**
     * ########### RESPONSE LISTENER ###########
     */
    /*
     * (non-Javadoc)
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

    /**
     * ############# RESPONSE #############
     */

    protected abstract boolean onErrorEvent(Bundle bundle);

    protected abstract boolean onSuccessEvent(Bundle bundle);

    /**
     * ########### DIALOGS ###########
     */

    /**
     * Dialog used to show an error
     * @param errors
     */
    protected void showErrorDialog(HashMap<String, List<String>> errors, int titleId) {
        Print.d(TAG, "SHOW LOGIN ERROR DIALOG");
        List<String> errorMessages = null;
        if (errors != null) {
            errorMessages = errors.get(RestConstants.JSON_VALIDATE_TAG);
        }
        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
            showFragmentContentContainer();
            dialog = DialogGenericFragment.newInstance(true, false,
                    getString(titleId),
                    errorMessages.get(0),
                    getString(R.string.ok_label),
                    "",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dismissDialogFragment();
                            }
                        }
                    });
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
        }
    }

}

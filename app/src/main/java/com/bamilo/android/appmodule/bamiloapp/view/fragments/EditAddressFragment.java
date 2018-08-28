package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.framework.components.customfontviews.Button;
import com.bamilo.android.framework.components.customfontviews.EditText;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.helpers.address.EditAddressHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.address.GetAddressHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.address.GetCitiesHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.address.GetPostalCodeHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.address.GetRegionsHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.address.SetDefaultShippingAddressHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.objects.addresses.Address;
import com.bamilo.android.framework.service.objects.addresses.AddressCity;
import com.bamilo.android.framework.service.objects.addresses.AddressPostalCode;
import com.bamilo.android.framework.service.objects.addresses.AddressPostalCodes;
import com.bamilo.android.framework.service.objects.addresses.AddressRegion;
import com.bamilo.android.framework.service.objects.addresses.AddressRegions;
import com.bamilo.android.framework.service.objects.addresses.FormListItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.ApiConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.R;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/02/25
 */
public abstract class EditAddressFragment extends BaseFragment implements IResponseCallback,
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = EditAddressFragment.class.getSimpleName();
    public static final String SELECTED_ADDRESS = "selected_address";
    public static final int INVALID_ADDRESS_ID = -1;
    private static final int UNKNOWN_POSTAL_CODE = -1;
    private Address mAddress;
    protected ArrayList<AddressRegion> mRegions;
    protected int mAddressId;
    protected PurchaseEntity orderSummary;
    Spinner address_spinner, city_spinner, postal_spinner;
    TextView name_error, family_error, cellphone_error, address_error;
    EditText name;
    EditText family;
    EditText address;
    EditText postal_code;
    EditText cellphone;
    private Button add;
    String action = ApiConstants.EDIT_ADDRESS_API_PATH;
    String id, nameVal, familyVal, address1, address2, phone, shipping, billing;
    int region, city, postcode;
    private TextView address_postal_code_error;
    private EventType errorType;


    /**
     * Constructor
     */
    public EditAddressFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, int titleResId, @KeyboardState int adjust_state, @ConstantsCheckout.CheckoutType int titleCheckout) {
        super(enabledMenuItems, action, R.layout.checkout_edit_address_shipping2, titleResId, adjust_state, titleCheckout);
    }
    /*public EditAddressFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, int titleResId, @KeyboardState int adjust_state, @ConstantsCheckout.CheckoutType int titleCheckout) {
        super(enabledMenuItems, action, R.layout.checkout_edit_address_shipping2, titleResId, adjust_state, titleCheckout);
    }*/

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TrackerDelegator.trackPage(TrackingPage.EDIT_ADDRESS, getLoadTime(), false);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = getArguments() != null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            mAddressId = arguments.getInt(ConstantsIntentExtra.ARG_1, INVALID_ADDRESS_ID);
        }

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.EDIT_ADDRESS.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        name = (EditText) view.findViewById(R.id.address_name);
        family = (EditText) view.findViewById(R.id.address_family);
        cellphone = (EditText) view.findViewById(R.id.address_cell);
        address = (EditText) view.findViewById(R.id.address_direction);
        address_spinner = (Spinner) view.findViewById(R.id.address_state);
        city_spinner = (Spinner) view.findViewById(R.id.address_city);
        postal_spinner = (Spinner) view.findViewById(R.id.address_postal_region);
        postal_code = (EditText) view.findViewById(R.id.address_postal_code);
        add = (Button) view.findViewById(R.id.edit_address_btn);
        add.setOnClickListener(this);

        name_error = (TextView) view.findViewById(R.id.address_name_error);
        family_error = (TextView) view.findViewById(R.id.address_last_name_error);
        cellphone_error = (TextView) view.findViewById(R.id.address_cellphone_error);
        address_error = (TextView) view.findViewById(R.id.address_text_error);
        address_postal_code_error = (TextView) view.findViewById(R.id.address_postal_code_error);
        triggerGetAddress(mAddressId);
    }

    private void onSuccessGetAddress() {
        id = String.valueOf(mAddress.getId());
        nameVal = mAddress.getFirstName();
        familyVal = mAddress.getLastName();
        phone = mAddress.getPhone();
        address1 = mAddress.getAddress();
        address2 = mAddress.getAddress2();

        initialdataform();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.d(TAG, "ON SAVE SATE");
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Next button
        if (id == R.id.edit_address_btn) {
            onClickCreateAddressButton();
        }
        // Unknown view
        else {
            Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
        }
    }

    private boolean formValidated() {
        boolean flag = true;
        name_error.setVisibility(View.GONE);
        family_error.setVisibility(View.GONE);
        address_error.setVisibility(View.GONE);
        cellphone_error.setVisibility(View.GONE);
        address_postal_code_error.setVisibility(View.GONE);

        if (name.getText().length() >= 0) {
          /*  */
            if (name.getText().length() == 0) {
                name_error.setVisibility(View.VISIBLE);
                name_error.setText(R.string.error_isrequired);
                flag = false;
            }
            if (name.getText().length() > 0 && name.getText().length() < 2) {
                name_error.setVisibility(View.VISIBLE);
                name_error.setText(R.string.address_at_least_two_chars_needed);
                flag = false;
            }

        }
        if (family.getText().length() >= 0) {
          /*  */
            if (family.getText().length() == 0) {
                family_error.setVisibility(View.VISIBLE);
                family_error.setText(R.string.error_isrequired);
                flag = false;
            }
            if (family.getText().length() > 0 && family.getText().length() < 2) {
                family_error.setVisibility(View.VISIBLE);
                family_error.setText(R.string.address_at_least_two_chars_needed);
                flag = false;
            }

        }
        if (address.getText().length() >= 0) {
          /*  */
            if (address.getText().length() == 0) {
                address_error.setVisibility(View.VISIBLE);
                address_error.setText(R.string.error_isrequired);
                flag = false;
            }
            if (address.getText().length() > 0 && address.getText().length() < 2) {
                address_error.setVisibility(View.VISIBLE);
                address_error.setText(R.string.address_at_least_two_chars_needed);
                flag = false;
            }
        }


        if (cellphone.getText().length() >= 0) {
          /*  */
            Pattern pattern = Pattern.compile(getString(R.string.cellphone_regex), Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(cellphone.getText());
            boolean result = matcher.matches();
            if (!result) {
                cellphone.setVisibility(View.VISIBLE);
                cellphone_error.setVisibility(View.VISIBLE);
                cellphone_error.setText(R.string.address_phone_number_invalidity_error);
                flag = false;
            }
            if (cellphone.getText().length() == 0) {
                cellphone.setVisibility(View.VISIBLE);
                cellphone_error.setVisibility(View.VISIBLE);
                cellphone_error.setText(R.string.error_isrequired);
                flag = false;
            }

        }

        if (postal_code.getText().length() != 0 && postal_code.getText().length() != 10) {
            address_postal_code_error.setVisibility(View.VISIBLE);
            flag = false;

        }


        return flag;
    }

    private void onClickCreateAddressButton() {
        Print.i(TAG, "ON CLICK: CREATE");
        // Validate
        if (!formValidated()) {
            Print.i(TAG, "SAME FORM: INVALID");
        } else {

            ContentValues values = new ContentValues();
            values.put("address_form[id]", id);
            //values.put("address_form[national_id]", national_id.getText().toString());
            values.put("address_form[first_name]", name.getText().toString());
            values.put("address_form[last_name]", family.getText().toString());
            values.put("address_form[address1]", address.getText().toString());
            values.put("address_form[address2]", postal_code.getText().toString());
            values.put("address_form[region]", region);
            values.put("address_form[city]", city);
            if (postcode != UNKNOWN_POSTAL_CODE) {
                values.put("address_form[postcode]", postcode);
            } else {
                values.put("address_form[postcode]", "");
            }
            values.put("address_form[phone]", cellphone.getText().toString());
            values.put("address_form[is_default_shipping]", 1);
            values.put("address_form[is_default_billing]", 1);
            values.put("address_form[gender]", BamiloApplication.CUSTOMER.getGender());

            triggerEditAddress(action, values);
            triggerDefaultAddressForm(Integer.parseInt(id));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");

    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Print.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
        mRegions = null;
    }

    private void initialdataform() {
        name.setText(nameVal);
        family.setText(familyVal);
        cellphone.setText(phone);
        address.setText(address1);
        postal_code.setText(address2);
    }

    /**
     * Method used to set the regions on the respective form
     */
    private void setRegions(ArrayList<AddressRegion> regions) {
        Print.d(TAG, "SET REGIONS REGIONS: ");

        // Create adapter
        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, regions);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        address_spinner.setAdapter(adapter);
        address_spinner.setSelection(getDefaultPosition(mAddress.getRegion(), regions));
        address_spinner.setOnItemSelectedListener(this);
        hideActivityProgress();
    }

    /**
     * Method used to set the cities on the respective form
     */
    private void setCities(ArrayList<AddressCity> cities) {
        address_spinner = (Spinner) View.inflate(getBaseActivity(), R.layout._def_gen_form_spinner, null);
        // Create adapter
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, cities);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        city_spinner.setAdapter(adapter);
        city_spinner.setSelection(getDefaultPosition(mAddress.getCity(), cities));
        city_spinner.setOnItemSelectedListener(this);
        hideActivityProgress();
    }

    /**
     * Method used to set the postalCodes on the respective form
     */
    private void setPostalCodes(final ArrayList<AddressPostalCode> postalCodes) {
        // Create adapter
        ArrayAdapter<AddressPostalCode> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, postalCodes);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        postal_spinner.setAdapter(adapter);
        if (postalCodes.size() > 1) {
            postal_spinner.setSelection(getDefaultPosition(mAddress.getPostcode(), postalCodes));
            postal_spinner.setVisibility(View.VISIBLE);
        } else {
            if (!CollectionUtils.isEmpty(postalCodes)) {
                postcode = postalCodes.get(0).getValue();
            } else {
                postcode = UNKNOWN_POSTAL_CODE;
            }
            postal_spinner.setVisibility(View.GONE);
        }
        postal_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                postcode = postalCodes.get(position).getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Show invisible content to trigger spinner listeners
        // showGhostFragmentContentContainer();
    }

    /**
     * Get the position of the address city
     *
     * @return int the position
     */
    private int getDefaultPosition(String label, ArrayList<? extends FormListItem> data) {
        try {

            for (int i = 0; i < data.size(); i++) {
                FormListItem formListItem = data.get(i);
                if (formListItem.getLabel().equals(label)) {
                    return i;
                }
            }
        } catch (NullPointerException | NumberFormatException e) {
            //...
        }
        return 0;
    }

    @Override
    protected void onClickContinueButton() {
        retry();
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        retry();
    }

    private void retry() {
        switch (errorType) {
            case GET_REGIONS_EVENT:
                triggerGetRegions();
                break;
            case GET_CITIES_EVENT: {
                if (region != 0) {
                    triggerGetCities(region);
                } else {
                    city_spinner.setSelection(0);
                    errorType = EventType.GET_REGIONS_EVENT;
                    retry();
                }
                break;
            }
            case GET_POSTAL_CODE_EVENT: {
                if (city != 0) {
                    triggerGetPostalCodes(city);
                } else {
                    postal_spinner.setSelection(0);
                    errorType = EventType.GET_CITIES_EVENT;
                    retry();
                }
                break;
            }
            case CREATE_ADDRESS_SIGNUP_EVENT:
                onClickRetryButton();
                break;
            case CREATE_ADDRESS_EVENT:
                onClickCreateAddressButton();
                break;
            default:
                getBaseActivity().onBackPressed();
                break;
        }
    }

    /**
     * Process the click on retry button.
     *
     * @author paulo
     */
    protected abstract void onClickRetryButton();

    /**
     * ########### ON ITEM SELECTED LISTENER ###########
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener#onNothingSelected(com.mobile.components.absspinner.IcsAdapterView)
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener#onItemSelected(com.mobile.components.absspinner.IcsAdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Print.d(TAG, "ON ITEM SELECTED");
        Object object = parent.getItemAtPosition(position);
        if (object instanceof AddressRegion) {
            region = ((AddressRegion) object).getValue();
            // Get cities
            triggerGetCities(region);
        } else if (object instanceof AddressCity) {
            int oldCity = city;
            city = ((AddressCity) object).getValue();
            // Get postal codes
            if (city != oldCity) {
                postcode = 0;
            }
            triggerGetPostalCodes(city);
        }
    }

    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to edit an address
     */
    private void triggerEditAddress(String action, ContentValues values) {
        Print.i(TAG, "TRIGGER: EDIT ADDRESS");
        triggerContentEvent(new EditAddressHelper(), EditAddressHelper.createBundle(action, values), this);
        // Hide the keyboard
        getBaseActivity().hideKeyboard();
    }

    private void triggerGetAddress(int mAddressId) {
        showFragmentContentContainer();
        triggerContentEvent(new GetAddressHelper(), GetAddressHelper.createBundle(String.valueOf(mAddressId)), this);
    }

    /**
     * Trigger to get regions
     */
    private void triggerGetRegions() {
        showFragmentContentContainer();
        triggerContentEvent(new GetRegionsHelper(), GetRegionsHelper.createBundle(ApiConstants.GET_REGIONS_API_PATH), this);
    }

    /**
     * Trigger to get cities
     */
    private void triggerGetCities(int region) {
        showFragmentContentContainer();
        triggerContentEvent(new GetCitiesHelper(), GetCitiesHelper.createBundle(ApiConstants.GET_CITIES_API_PATH, region, String.valueOf(region)), this);
    }

    /**
     * Trigger to get postal codes
     */
    private void triggerGetPostalCodes(int city) {
        showFragmentContentContainer();
        triggerContentEvent(new GetPostalCodeHelper(), GetPostalCodeHelper.createBundle(ApiConstants.GET_ADDRESS_POST_CODES_API_PATH, city, String.valueOf(city)), this);
    }

    protected void triggerDefaultAddressForm(int mAddressId) {
        triggerContentEvent(new SetDefaultShippingAddressHelper(), SetDefaultShippingAddressHelper.createBundle(mAddressId), this);
    }

    /**
     * ############# RESPONSE #############
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        switch (eventType) {
            case GET_EVENT_EVENT:
                mAddress = (Address) baseResponse.getContentData();
                onSuccessGetAddress();
                triggerGetRegions();
                break;
            case GET_REGIONS_EVENT:
                Print.d(TAG, "RECEIVED GET_REGIONS_EVENT");
                mRegions = (AddressRegions) baseResponse.getContentData();
                if (CollectionUtils.isNotEmpty(mRegions)) {
                    setRegions(mRegions);
                } else {
                    Print.w(TAG, "GET REGIONS EVENT: IS EMPTY");
                    super.showFragmentErrorRetry();

                }
                //hideActivityProgress();
                break;
            case GET_CITIES_EVENT:
                Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
                ArrayList<AddressCity> cities = (GetCitiesHelper.AddressCitiesStruct)baseResponse.getContentData();
                setCities(cities);
                break;
            case GET_POSTAL_CODE_EVENT:
                Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
                ArrayList<AddressPostalCode> postalCodes = (AddressPostalCodes)baseResponse.getContentData();
                setPostalCodes(postalCodes);
                // Show
                showFragmentContentContainer();
                break;
            case EDIT_ADDRESS_EVENT:
                Print.d(TAG, "RECEIVED EDIT_ADDRESS_EVENT");
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.edit_address_success));
                getBaseActivity().onBackPressed();
                break;
            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Get type
        errorType = baseResponse.getEventType();
        // Validate
        if (isOnStoppingProcess || errorType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.i(TAG, "SUPER HANDLE ERROR EVENT");
            return;
        }
        // Validate
        int errorCode = baseResponse.getError().getCode();
        Print.i(TAG, "ON ERROR EVENT: " + errorType + " " + errorCode);
        switch (errorType) {
            case GET_EDIT_ADDRESS_FORM_EVENT:
                onGetEditAddressFormErrorEvent(baseResponse);
                break;
            case GET_REGIONS_EVENT:
                onGetRegionsErrorEvent(baseResponse);
                break;
            case GET_CITIES_EVENT:
                onGetCitiesErrorEvent(baseResponse);
                break;
            case GET_POSTAL_CODE_EVENT:
                onGetPostalCodesErrorEvent();
                break;
            case EDIT_ADDRESS_EVENT:
                onEditAddressErrorEvent(baseResponse);
                break;
            default:
                break;
        }
    }

    protected void onGetEditAddressFormErrorEvent(BaseResponse baseResponse) {
        Print.w(TAG, "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
    }

    protected void onGetRegionsErrorEvent(BaseResponse baseResponse) {
        Print.w(TAG, "RECEIVED GET_REGIONS_EVENT");
    }

    protected void onGetCitiesErrorEvent(BaseResponse baseResponse) {
        Print.w(TAG, "RECEIVED GET_CITIES_EVENT");
    }

    protected void onGetPostalCodesErrorEvent() {
        Print.w(TAG, "RECEIVED GET_POSTAL_CODES_EVENT");
    }

    protected void onEditAddressErrorEvent(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED EDIT_ADDRESS_EVENT");
    }

}


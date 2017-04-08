package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.absspinner.PromptSpinnerAdapter;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.address.EditAddressHelper;
import com.mobile.helpers.address.GetCitiesHelper;
import com.mobile.helpers.address.GetFormEditAddressHelper;
import com.mobile.helpers.address.GetPostalCodeHelper;
import com.mobile.helpers.address.GetRegionsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.AddressForms;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.objects.addresses.AddressCity;
import com.mobile.newFramework.objects.addresses.AddressPostalCode;
import com.mobile.newFramework.objects.addresses.AddressPostalCodes;
import com.mobile.newFramework.objects.addresses.AddressRegion;
import com.mobile.newFramework.objects.addresses.AddressRegions;
import com.mobile.newFramework.objects.addresses.FormListItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/02/25
 */
public abstract class EditAddressFragment extends BaseFragment implements IResponseCallback, IcsAdapterView.OnItemSelectedListener {

    private static final String TAG = EditAddressFragment.class.getSimpleName();
    public static final String SELECTED_ADDRESS = "selected_address";
    public static final int INVALID_ADDRESS_ID = -1;
    protected ViewGroup mEditFormContainer;
    protected DynamicForm mEditFormGenerator;
    protected Form mFormResponse;
    protected ArrayList<AddressRegion> mRegions;
    protected int mAddressId;
    protected PurchaseEntity orderSummary;
    protected boolean isCityIdAnEditText = false;
    private Bundle mFormSavedState;
    IcsSpinner address_spinner ,city_spinner,postal_spinner,gender_spinner;
    TextView name_error , family_error , national_error,postal_error,cellphone_error ,address_error;
    EditText name;
    EditText family;
    EditText address;
    EditText national_id;
    EditText postal_code;
    EditText cellphone;
    private Button add;
    String action;
    String id , nameVal , familyVal , address1 ,address2,region , city, postcode,phone ,shipping ,billing;

    /**
     * Constructor
     */
    public EditAddressFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, int titleResId, @KeyboardState int adjust_state, @ConstantsCheckout.CheckoutType int titleCheckout) {
        super(enabledMenuItems, action, R.layout.checkout_edit_address_main, titleResId, adjust_state, titleCheckout);
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
        Print.i(TAG, "ON CREATE");
        // Saved form state
        mFormSavedState = savedInstanceState;
        // Get arguments
        Bundle arguments = getArguments() != null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            mAddressId = arguments.getInt(ConstantsIntentExtra.ARG_1, INVALID_ADDRESS_ID);
        }
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_EDIT_ADDRESS);
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
        national_id = (EditText) view.findViewById(R.id.address_national_id);
        gender_spinner = (IcsSpinner) view.findViewById(R.id.address_gender);
        cellphone = (EditText) view.findViewById(R.id.address_cell);
        address = (EditText) view.findViewById(R.id.address_direction);
        address_spinner = (IcsSpinner) view.findViewById(R.id.address_state);
        city_spinner = (IcsSpinner) view.findViewById(R.id.address_city);
        postal_spinner = (IcsSpinner) view.findViewById(R.id.address_postal_region);
        postal_code = (EditText) view.findViewById(R.id.address_postal_code);
        add = (Button) view.findViewById(R.id.add_address_btn);

        name_error = (TextView) view.findViewById(R.id.address_name_error);
        family_error = (TextView) view.findViewById(R.id.address_last_name_error);
        national_error = (TextView) view.findViewById(R.id.address_national_id_error);
        cellphone_error = (TextView) view.findViewById(R.id.address_cellphone_error);
        postal_error = (TextView) view.findViewById(R.id.address_postal_error);
        address_error = (TextView) view.findViewById(R.id.address_text_error);
       /* // Spinner Drop down elements
        ArrayList<AddressCity> city = new ArrayList<AddressCity>();
        city.add(new AddressCity(0,"شهر"));
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.spinner_item,city);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt("شهر");
        city_spinner.setAdapter(promptAdapter);*/
        // Container
       /* mEditFormContainer = (ViewGroup) view.findViewById(R.id.checkout_edit_form_container);
        // Next button
        view.findViewById(R.id.checkout_edit_button_enter).setOnClickListener(this);
        //Validate current address
        if (mAddressId == INVALID_ADDRESS_ID) {
            showFragmentErrorRetry();
        }*/
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
        outState.putInt(EditAddressFragment.SELECTED_ADDRESS, mAddressId);
        if (mEditFormGenerator != null) {
            mEditFormGenerator.saveFormState(outState);
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
        // Case goes to back stack save the state
        Bundle bundle = new Bundle();
        if(mEditFormGenerator != null) {
            mEditFormGenerator.saveFormState(bundle);
        }
        mFormSavedState = bundle;

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

    /**
     * Load the dynamic form
     */
    protected void loadEditAddressForm(Form form) {
        Print.i(TAG, "LOAD EDIT ADDRESS FORM");
        // Edit form
       String action = form.getAction();
        int x = form.getFields().size();
        List<FormField> fields = form.getFields();
        int i =0;

        for (FormField field : fields)
        {
            if(field.getKey().equals("id")) {
               id = form.getFields().get(i).getValue();
            }
            if(field.getKey().equals("first_name")) {
                nameVal = form.getFields().get(i).getValue();
            }
            if(field.getKey().equals("last_name")) {
                familyVal = form.getFields().get(i).getValue();
            }
            if(field.getKey().equals("address1")) {
                address1= form.getFields().get(i).getValue();
            }
            if(field.getKey().equals("address2")) {
                address2 = form.getFields().get(i).getValue();
            }
            if(field.getKey().equals("region")) {
                region = form.getFields().get(i).getValue();
            }
            if(field.getKey().equals("city")) {
                city = form.getFields().get(i).getValue();
            }
            if(field.getKey().equals("postcode")) {
                postcode = form.getFields().get(i).getValue();
            }
            if(field.getKey().equals("phone")) {
                phone= form.getFields().get(i).getValue();
            }
            if(field.getKey().equals("is_default_shipping")) {
                shipping= form.getFields().get(i).getValue();
            }
            if(field.getKey().equals("is_default_billing")) {
                billing = form.getFields().get(i).getValue();
            }


        i++;
        }

        /*List<FormField> fields = ((AddressForms) form.getContentData()).getShippingForm() .getFields();
*/
       /* mEditFormGenerator = FormFactory.create(FormConstants.ADDRESS_FORM, getBaseActivity(), form);
        mEditFormContainer.removeAllViews();
        mEditFormGenerator.loadSaveFormState(mFormSavedState);
        mEditFormContainer.addView(mEditFormGenerator.getContainer());
        mEditFormContainer.refreshDrawableState();*/
        // Validate Regions
        if(mRegions == null) {
            FormField field = form.getFieldKeyMap().get(RestConstants.REGION);
            triggerGetRegions(field.getApiCall());
        } else {
            Print.d(TAG, "REGIONS ISN'T NULL");
            setRegions(mEditFormGenerator, mRegions);
        }
        // Define if CITY is a List or Text
       /* DynamicFormItem item = mEditFormGenerator.getItemByKey(RestConstants.CITY);
        isCityIdAnEditText = item != null && item.getDataControl() instanceof EditText;
        // Show selected address content
        mEditFormContainer.refreshDrawableState();*/
    }

    /**
     * Method used to set the regions on the respective form
     */
    private void setRegions(DynamicForm dynamicForm, ArrayList<AddressRegion> regions){
        Print.d(TAG, "SET REGIONS REGIONS: ");
        // Get region item
     /*   DynamicFormItem formItem = dynamicForm.getItemByKey(RestConstants.REGION);
        // Clean group
        ViewGroup group = formItem.getControl();
        group.removeAllViews();*/
        // Add a spinner
         address_spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout._def_gen_form_spinner, null);
        // Create adapter
        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<>( getBaseActivity(), R.layout.form_spinner_item, regions);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        address_spinner.setAdapter(adapter);
        if (mFormSavedState != null && mFormSavedState.getInt(RestConstants.REGION) <= address_spinner.getCount()) {
            address_spinner.setSelection(mFormSavedState.getInt(RestConstants.REGION));
        } else {
            address_spinner.setSelection(Integer.parseInt(city));
        }
        address_spinner.setOnItemSelectedListener(this);
hideActivityProgress();
        // Show invisible content to trigger spinner listeners
        showGhostFragmentContentContainer();
    }

    /**
     * Method used to set the cities on the respective form
     */
    private void setCities(DynamicForm dynamicForm, ArrayList<AddressCity> cities){
        address_spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout._def_gen_form_spinner, null);
        // Create adapter
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<>( getBaseActivity(), R.layout.form_spinner_item, cities);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        city_spinner.setAdapter(adapter);
        if (mFormSavedState != null && mFormSavedState.getInt(RestConstants.REGION) <= city_spinner.getCount()) {
            city_spinner.setSelection(mFormSavedState.getInt(RestConstants.REGION));
        } else {
            city_spinner.setSelection(Integer.parseInt(city));
        }
        city_spinner.setOnItemSelectedListener(this);
        hideActivityProgress();
        // Show invisible content to trigger spinner listeners
        showGhostFragmentContentContainer();
    }

    /**
     * Method used to set the postalCodes on the respective form
     */
    private void setPostalCodes(DynamicForm dynamicForm, ArrayList<AddressPostalCode> postalCodes){
        postal_spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout._def_gen_form_spinner, null);
        // Create adapter
        ArrayAdapter<AddressPostalCode> adapter = new ArrayAdapter<>( getBaseActivity(), R.layout.form_spinner_item, postalCodes);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        postal_spinner.setAdapter(adapter);
        if (mFormSavedState != null && mFormSavedState.getInt(RestConstants.REGION) <= city_spinner.getCount()) {
            postal_spinner.setSelection(mFormSavedState.getInt(RestConstants.REGION));
        } else {
            postal_spinner.setSelection(Integer.parseInt(city));
        }
        postal_spinner.setOnItemSelectedListener(this);
        hideActivityProgress();
        // Show invisible content to trigger spinner listeners
        showGhostFragmentContentContainer();
    }

    /**
     * Get the position of the address city
     * @return int the position
     */
    private int getDefaultPosition(DynamicFormItem formItem, ArrayList<? extends FormListItem> regions){
        try {
            int position = Integer.valueOf(formItem.getEntry().getValue());
            for(int i = 0; i < regions.size(); i++){
                FormListItem formListItem = regions.get(i);
                if(formListItem.getValue() == position){
                    return i;
                }
            }
        } catch (NullPointerException | NumberFormatException e) {
            //...
        }
        return 0;
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
        // Next button
        if (id == R.id.checkout_edit_button_enter) {
            onClickEditAddressButton();
        }
        // Unknown view
        else {
            Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
        }
    }

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
     * Process the click on the next step button
     */
    private void onClickEditAddressButton() {
        Print.i(TAG, "ON CLICK: EDIT");
        if (mEditFormGenerator.validate()) {
            triggerEditAddress(mEditFormGenerator.getForm().getAction(), mEditFormGenerator.save());
        } else {
            Print.i(TAG, "INVALID FORM");
        }
    }

    /**
     * ########### ON ITEM SELECTED LISTENER ###########
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener#onNothingSelected(com.mobile.components.absspinner.IcsAdapterView)
     */
    @Override
    public void onNothingSelected(IcsAdapterView<?> parent) {
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener#onItemSelected(com.mobile.components.absspinner.IcsAdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
        Print.d(TAG, "ON ITEM SELECTED");
        Object object = parent.getItemAtPosition(position);
        if (object instanceof AddressRegion) {
            // Get city field
            FormField field = mFormResponse.getFieldKeyMap().get(RestConstants.CITY);
            // Case list
            if (FormInputType.list == field.getInputType()) {
                // Request the cities for this region id
                int regionId = ((AddressRegion) object).getValue();
                // Get cities
                triggerGetCities(field.getApiCall(), regionId);
            }
            // Case text or other
            else {
                showFragmentContentContainer();
            }
        } else if (object instanceof AddressCity){
            // Get city field
            FormField field = mFormResponse.getFieldKeyMap().get(RestConstants.POSTCODE);
            // Case list
            if (field != null && FormInputType.list == field.getInputType()) {
                // Request the postal codes for this city id
                int cityId = ((AddressCity) object).getValue();
                // Get postal codes
                triggerGetPostalCodes(field.getApiCall(), cityId);

            }
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

    /**
     * Trigger to get the address form
     */
    protected void triggerEditAddressForm(){
        Print.i(TAG, "TRIGGER: EDIT FORM");
        triggerContentEvent(new GetFormEditAddressHelper(), GetFormEditAddressHelper.createBundle(mAddressId), this);
    }

    /**
     * Trigger to get regions
     */
    private void triggerGetRegions(String apiCall){
        Print.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        triggerContentEventNoLoading(new GetRegionsHelper(), GetRegionsHelper.createBundle(apiCall), this);
    }

    /**
     * Trigger to get cities
     */
    private void triggerGetCities(String apiCall, int region){
        Print.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        triggerContentEvent(new GetCitiesHelper(), GetCitiesHelper.createBundle(apiCall, region, null), this);
    }

    /**
     * Trigger to get postal codes
     */
    private void triggerGetPostalCodes(String apiCall, int city){
        Print.i(TAG, "TRIGGER: GET POSTAL CODES: " + apiCall);
        triggerContentEvent(new GetPostalCodeHelper(), GetPostalCodeHelper.createBundle(apiCall, city, null), this);
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

        if(isOnStoppingProcess || eventType == null){
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        switch (eventType) {
            case GET_EDIT_ADDRESS_FORM_EVENT:
                Print.d(TAG, "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
                // Get order summary
                orderSummary = JumiaApplication.INSTANCE.getCart();
                // Form
                Form form = (Form)baseResponse.getContentData();
                mFormResponse = form;
                // Load form, get regions
                loadEditAddressForm(form);
                break;
            case GET_REGIONS_EVENT:
                Print.d(TAG, "RECEIVED GET_REGIONS_EVENT");
                mRegions = (AddressRegions) baseResponse.getContentData();
                if (CollectionUtils.isNotEmpty(mRegions)) {
                    setRegions(mEditFormGenerator, mRegions);
                    hideActivityProgress();
                } else {
                    Print.w(TAG, "GET REGIONS EVENT: IS EMPTY");
                    super.showFragmentErrorRetry();
                    hideActivityProgress();
                }
                hideActivityProgress();
                break;
            case GET_CITIES_EVENT:
                Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
                ArrayList<AddressCity> cities = (GetCitiesHelper.AddressCitiesStruct)baseResponse.getContentData();
                setCities(mEditFormGenerator, cities);
                // Show
                showFragmentContentContainer();
                break;
            case GET_POSTAL_CODE_EVENT:
                Print.d(TAG, "RECEIVED GET_CITIES_EVENT");
                ArrayList<AddressPostalCode> postalCodes = (AddressPostalCodes)baseResponse.getContentData();
                setPostalCodes(mEditFormGenerator, postalCodes);
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
        EventType eventType = baseResponse.getEventType();
        // Validate
        if(isOnStoppingProcess || eventType == null){
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
        Print.i(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
        switch (eventType) {
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

    protected void onGetEditAddressFormErrorEvent(BaseResponse baseResponse){
        Print.w(TAG, "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
    }

    protected void onGetRegionsErrorEvent(BaseResponse baseResponse){
        Print.w(TAG, "RECEIVED GET_REGIONS_EVENT");
    }

    protected void onGetCitiesErrorEvent(BaseResponse baseResponse){
        Print.w(TAG, "RECEIVED GET_CITIES_EVENT");
    }

    protected void onGetPostalCodesErrorEvent() {
        Print.w(TAG, "RECEIVED GET_POSTAL_CODES_EVENT");
    }

    protected void onEditAddressErrorEvent(BaseResponse baseResponse){
        Print.d(TAG, "RECEIVED EDIT_ADDRESS_EVENT");
    }

}


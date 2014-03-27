/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.FormConstants;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.forms.FormField;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Address;
import pt.rocket.framework.objects.AddressCity;
import pt.rocket.framework.objects.AddressRegion;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetInitFormHelper;
import pt.rocket.helpers.address.GetCitiesHelper;
import pt.rocket.helpers.address.GetFormEditAddressHelper;
import pt.rocket.helpers.address.GetRegionsHelper;
import pt.rocket.helpers.address.SetEditedAddressHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsSpinner;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutEditAddressFragment extends BaseFragment implements OnClickListener, IResponseCallback, OnItemSelectedListener {

    private static final String TAG = LogTagHelper.create(CheckoutEditAddressFragment.class);
    
    public static final String SELECTED_ADDRESS = "selected_address";
    
    private static CheckoutEditAddressFragment mEditAddressFragment;
    
    private ViewGroup mEditFormContainer;

    private DynamicForm mEditFormGenerator;

    private Form mFormResponse;

    private ArrayList<AddressRegion> mRegions;
    
    private Address mCurrentAddress;

    private OrderSummary orderSummary;

    private View mMsgRequired;
    
    
    /**
     * 
     * @return
     */
    public static CheckoutEditAddressFragment getInstance(Bundle bundle) {
        mEditAddressFragment = new CheckoutEditAddressFragment();
        mEditAddressFragment.mCurrentAddress = bundle.getParcelable(SELECTED_ADDRESS);
        return mEditAddressFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutEditAddressFragment() {
        super(EnumSet.of(EventType.GET_EDIT_ADDRESS_FORM_EVENT, EventType.EDIT_ADDRESS_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH), 
                NavigationAction.Checkout,
                ConstantsCheckout.CHECKOUT_BILLING);
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
        return inflater.inflate(R.layout.checkout_edit_address_main, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        
        // Validate current address, if null goto back
        if(mCurrentAddress == null) onClickCancelAddressButton();
        
        // Create address form
        mEditFormContainer = (ViewGroup) view.findViewById(R.id.checkout_edit_form_container);
        // Message
        mMsgRequired = view.findViewById(R.id.checkout_edit_address_required_text);
        // Next button
        view.findViewById(R.id.checkout_edit_button_enter).setOnClickListener((OnClickListener) this);
        // Cancel button
        view.findViewById(R.id.checkout_edit_button_cancel).setOnClickListener((OnClickListener) this);
        
        // Get and show form
        if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
            triggerInitForm();
        } else if(mFormResponse != null && orderSummary != null){
            loadEditAddressForm(mFormResponse);
        } else {
            triggerEditAddressForm();
        }
    }
    
    
    /*
     * (non-Javadoc)
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
        TrackerDelegator.trackCheckoutStep(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), R.string.gcheckoutEditAddress, R.string.xcheckouteditaddress, R.string.mixprop_checkout_edit_address);
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
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        mRegions = null;
    }
    
    
    /**
     * Load the dynamic form
     * @param form
     */
    private void loadEditAddressForm(Form form) {
        Log.i(TAG, "LOAD EDIT ADDRESS FORM");
        // Edit form
        mEditFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getBaseActivity(), form);
        mEditFormContainer.removeAllViews();
        mEditFormContainer.addView(mEditFormGenerator.getContainer());                
        mEditFormContainer.refreshDrawableState();
        // Validate Regions
        if(mRegions == null) {
            FormField field = form.getFieldKeyMap().get(RestConstants.JSON_REGION_ID_TAG);
            String url = field.getDataCalls().get(RestConstants.JSON_API_CALL_TAG);
            triggerGetRegions(url);
        } else {
            Log.d(TAG, "REGIONS ISN'T NULL");
            setRegions(mEditFormGenerator, mRegions, mCurrentAddress);
        }
        // Hide check boxes
        hideSomeFields(mEditFormGenerator);
        // Show selected address content
        showSelectedAddress(mEditFormGenerator, mCurrentAddress);
        // Show
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, orderSummary);
        // Show
        getBaseActivity().showContentContainer();
        
        mEditFormContainer.refreshDrawableState();
    }
    
    /**
     * 
     * @param dynamicForm
     * @param selectedAddress
     */
    private void showSelectedAddress(DynamicForm dynamicForm, Address selectedAddress){
        // First name       
        ((EditText) dynamicForm.getItemByKey(RestConstants.JSON_FIRST_NAME_TAG).getEditControl()).setText(selectedAddress.getFirstName());
        // Last name        
        ((EditText) dynamicForm.getItemByKey(RestConstants.JSON_LAST_NAME_TAG).getEditControl()).setText(selectedAddress.getLastName());
        // Address 1        
        ((EditText) dynamicForm.getItemByKey(RestConstants.JSON_ADDRESS1_TAG).getEditControl()).setText(selectedAddress.getAddress());
        // Address 2        
        ((EditText) dynamicForm.getItemByKey(RestConstants.JSON_ADDRESS2_TAG).getEditControl()).setText(selectedAddress.getAddress2());
        // Phone            
        ((EditText) dynamicForm.getItemByKey(RestConstants.JSON_PHONE_TAG).getEditControl()).setText(selectedAddress.getPhone());
    }
    
    /**
     * Hide the default check boxes
     * @param dynamicForm
     */
    private void hideSomeFields(DynamicForm dynamicForm){
        DynamicFormItem item = dynamicForm.getItemByKey(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG);
        item.getEditControl().setVisibility(View.GONE);
        item = dynamicForm.getItemByKey(RestConstants.JSON_IS_DEFAULT_BILLING_TAG);
        item.getEditControl().setVisibility(View.GONE);
        item = dynamicForm.getItemByKey(RestConstants.JSON_CITY_TAG);
        item.getControl().setVisibility(View.GONE);
    }
    
    /**
     * Method used to set the regions on the respective form
     * @param dynamicForm
     * @param regions
     * @param tag
     */
    private void setRegions(DynamicForm dynamicForm, ArrayList<AddressRegion> regions, Address selecedAddress){
        Log.d(TAG, "SET REGIONS REGIONS: ");
        // Get region item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.JSON_REGION_ID_TAG);
        // Clean group
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        // Create adapter
        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<AddressRegion>( getBaseActivity(), R.layout.form_spinner_item, regions);
        spinner.setAdapter(adapter);
        spinner.setSelection(getRegionPosition(regions, selecedAddress));
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }
    
    /**
     * Get the position of the address region
     * @param regions
     * @param selecedAddress
     * @return int the position
     */
    private int getRegionPosition(ArrayList<AddressRegion> regions, Address selecedAddress){
        for (int i = 0; i < regions.size(); i++) {
            if(regions.get(i).getId() == selecedAddress.getFkCustomerAddressRegion()) return i;
        }
        return 0;
    }
        
    /**
     * Validate the current region selection and update the cities
     * @param requestedRegionAndFields
     * @param cities
     * @param selectedAddress 
     */
    private void setCitiesOnSelectedRegion(ArrayList<AddressCity> cities, Address selectedAddress){
        setCities(mEditFormGenerator, cities, selectedAddress);
    }
    
    /**
     * Method used to set the cities on the respective form
     * @param dynamicForm
     * @param cities
     * @param selectedAddress 
     * @param tag
     */
    private void setCities(DynamicForm dynamicForm, ArrayList<AddressCity> cities, Address selectedAddress){
        // Get city item
        DynamicFormItem v = dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG);
        // Clean group
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();        
        // Add a spinner
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        // Create adapter
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<AddressCity>( getBaseActivity(), R.layout.form_spinner_item, cities);
        spinner.setAdapter(adapter);
        spinner.setSelection(getCityPosition(cities, selectedAddress));
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }
    
    /**
     * Get the position of the address city
     * @param cities
     * @param selecedAddress
     * @return int the position
     */
    private int getCityPosition(ArrayList<AddressCity> cities, Address selecedAddress){
        for (int i = 0; i < cities.size(); i++) {
            if(cities.get(i).getId() == selecedAddress.getFkCustomerAddressCity()) return i;
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
        // Get view id
        int id = view.getId();
        // Next button
        if(id == R.id.checkout_edit_button_enter) onClickEditAddressButton();
        // Next button
        else if(id == R.id.checkout_edit_button_cancel) onClickCancelAddressButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on the next step button
     */
    private void onClickEditAddressButton() {
        Log.i(TAG, "ON CLICK: EDIT");
        
        // Hide error message
        if(mMsgRequired!=null) mMsgRequired.setVisibility(View.GONE);
        
        // Validate spinner
        ViewGroup mRegionGroup = (ViewGroup) mEditFormGenerator.getItemByKey(RestConstants.JSON_REGION_ID_TAG).getControl();
        // Validate if region group is filled
        if(!(mRegionGroup.getChildAt(0) instanceof IcsSpinner)) { 
            Log.w(TAG, "REGION SPINNER NOT FILL YET");
            // Show error message
            if(mMsgRequired != null) mMsgRequired.setVisibility(View.VISIBLE);
            return; 
        };
        
        triggerEditAddress(createContentValues(mEditFormGenerator));
    }
    
    
    /**
     * Process the click on the cancel button
     */
    private void onClickCancelAddressButton() {
        Log.i(TAG, "ON CLICK: CANCEL");
        getBaseActivity().onBackPressed();
    }
    
    /**
     * Method used to create the content values
     * @param dynamicForm
     * @param isDefaultShipping
     * @param isDefaultBilling
     * @return new content values
     */
    private ContentValues createContentValues(DynamicForm dynamicForm){
        // Save content values
        ContentValues mContentValues = dynamicForm.save();
        // Get the region
        ViewGroup mRegionGroup = (ViewGroup) dynamicForm.getItemByKey(RestConstants.JSON_REGION_ID_TAG).getControl();
        IcsSpinner mRegionSpinner = (IcsSpinner) mRegionGroup.getChildAt(0);
        AddressRegion mSelectedRegion = (AddressRegion) mRegionSpinner.getSelectedItem(); 
        Log.d(TAG, "SELECTED REGION: " + mSelectedRegion.getName() + " " + mSelectedRegion.getId());
        
        // Save city
        String mCityId = "";
        String mCityName = "";
        // Get from spinner
        View mCityView = dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG).getControl();
        if(((ViewGroup) mCityView).getChildAt(0) instanceof IcsSpinner) {
            // Get the city
            IcsSpinner mCitySpinner = (IcsSpinner) ((ViewGroup) mCityView).getChildAt(0);
            AddressCity mSelectedCity = (AddressCity) mCitySpinner.getSelectedItem(); 
            Log.d(TAG, "SELECTED CITY: " + mSelectedCity.getValue() + " " + mSelectedCity.getId() );
            mCityId = "" + mSelectedCity.getId();
            mCityName = mSelectedCity.getValue();
        }
        // Get from edit text
        else if(mCityView instanceof EditText) {
            EditText mCityEdit = (EditText) dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG).getEditControl();
            mCityName = mCityEdit.getText().toString();
            Log.d(TAG, "SELECTED CITY: " + mCityName );
        }
        
        // Get some values
        int mAddressId = mCurrentAddress.getId();
        int mRegionId = mSelectedRegion.getId();
        String isDefaultBilling = (mCurrentAddress.isDefaultBilling()) ? "1" : "0";
        String isDefaultShipping = (mCurrentAddress.isDefaultShipping()) ? "1" : "0";
        // Put values
        for (String key : mContentValues.keySet()) {
            if(key.contains(RestConstants.JSON_ADDRESS_ID_TAG)) mContentValues.put(key, mAddressId);
            else if(key.contains(RestConstants.JSON_IS_DEFAULT_BILLING_TAG)) mContentValues.put(key, isDefaultBilling);
            else if(key.contains(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG)) mContentValues.put(key, isDefaultShipping);
            else if(key.contains(RestConstants.JSON_REGION_ID_TAG)) mContentValues.put(key, mRegionId);
            else if(key.contains(RestConstants.JSON_CITY_ID_TAG)) mContentValues.put(key, mCityId);
            else if(key.contains(RestConstants.JSON_CITY_TAG)) mContentValues.put(key, mCityName);
        }
        Log.d(TAG, "CURRENT CONTENT VALUES: " + mContentValues.toString());
        // return the new content values
        return mContentValues;
    }
    
    /**
     * ########### ON ITEM SELECTED LISTENER ###########  
     */
    /*
     * (non-Javadoc)
     * @see com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener#onNothingSelected(com.actionbarsherlock.internal.widget.IcsAdapterView)
     */
    @Override
    public void onNothingSelected(IcsAdapterView<?> parent) { }
    
    /*
     * (non-Javadoc)
     * @see com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener#onItemSelected(com.actionbarsherlock.internal.widget.IcsAdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "CURRENT TAG: " + parent.getTag());
        Object object = parent.getItemAtPosition(position);
        if(object instanceof AddressRegion) {
            FormField field = mFormResponse.getFieldKeyMap().get(RestConstants.JSON_CITY_ID_TAG);
            String url = field.getDataCalls().get(RestConstants.JSON_API_CALL_TAG);
            Log.d(TAG, "API CALL: " + url);
            // Request the cities for this region id 
            int regionId = ((AddressRegion) object).getId();
            // Get cities
            triggerGetCities(url, regionId);
        }
    }
    
    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to edit an address
     * @param values
     */
    private void triggerEditAddress(ContentValues values) {
        Log.i(TAG, "TRIGGER: EDIT ADDRESS");
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetEditedAddressHelper.FORM_CONTENT_VALUES, values);
        triggerContentEvent(new SetEditedAddressHelper(), bundle, this);
    }
    
    /**
     * Trigger to get the address form
     */
    private void triggerEditAddressForm(){
        Log.i(TAG, "TRIGGER: LOGIN FORM");
        triggerContentEvent(new GetFormEditAddressHelper(), null, this);
    }
    
    /**
     * Trigger to initialize forms
     */
    private void triggerInitForm(){
        Log.i(TAG, "TRIGGER: INIT FORMS");
        triggerContentEvent(new GetInitFormHelper(), null, this);
    }
    
    /**
     * Trigger to get regions
     * @param apiCall
     */
    private void triggerGetRegions(String apiCall){
        Log.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, apiCall);
        triggerContentEventWithNoLoading(new GetRegionsHelper(), bundle, this);
    }
    
    /**
     * Trigger to get cities
     * @param apiCall
     * @param region id
     * @param selectedTag 
     */
    private void triggerGetCities(String apiCall, int region){
        Log.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, apiCall);
        bundle.putInt(GetCitiesHelper.REGION_ID_TAG, region);
        triggerContentEventWithNoLoading(new GetCitiesHelper(), bundle, this);
    }
    
    
   
    /**
     * ############# RESPONSE #############
     */
    /**
     * Filter the success response
     * @param bundle
     * @return boolean
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS EVENT");
        
        if(isOnStoppingProcess){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case INIT_FORMS:
            Log.d(TAG, "RECEIVED INIT_FORMS");
            triggerEditAddressForm();
            break;
        case GET_EDIT_ADDRESS_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
            // Get order summary
            orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
            // Form
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            mFormResponse = form;
            loadEditAddressForm(form);
            break;
        case GET_REGIONS_EVENT:
            Log.d(TAG, "RECEIVED GET_REGIONS_EVENT");
            mRegions = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            if (super.hasContent(mRegions)){
                setRegions(mEditFormGenerator, mRegions, mCurrentAddress);
            } else {
                Log.w(TAG, "GET REGIONS EVENT: IS EMPTY");
                super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "GET REGIONS EVENT: IS EMPTY");
            }
            break;
        case GET_CITIES_EVENT:
            Log.d(TAG, "RECEIVED GET_CITIES_EVENT");
            ArrayList<AddressCity> cities = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            setCitiesOnSelectedRegion(cities, mCurrentAddress);
            break;
        case EDIT_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED EDIT_ADDRESS_EVENT");
            Toast.makeText(getBaseActivity(), getString(R.string.edit_address_success), Toast.LENGTH_SHORT).show();
            getBaseActivity().onBackPressed();
            break;
        default:
            break;
        }
        
        return true;
    }
    
    /**
     * Filter the error response
     * @param bundle
     * @return boolean
     */
    protected boolean onErrorEvent(Bundle bundle) {
        
        if(isOnStoppingProcess){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
    	
        // Generic error
        if (getBaseActivity() != null && getBaseActivity().handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        switch (eventType) {
        case INIT_FORMS:
            Log.d(TAG, "RECEIVED INIT_FORMS");
            break;
        case GET_EDIT_ADDRESS_FORM_EVENT:
            Log.w(TAG, "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
            break;
        case GET_REGIONS_EVENT:
            Log.w(TAG, "RECEIVED GET_REGIONS_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_REGIONS_EVENT");
            break;
        case GET_CITIES_EVENT:
            Log.w(TAG, "RECEIVED GET_CITIES_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_CITIES_EVENT");
            break;
        case EDIT_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED EDIT_ADDRESS_EVENT");
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                @SuppressWarnings("unchecked")
                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY); 
                showErrorDialog(errors);
                getBaseActivity().showContentContainer();
            } else {
                Log.w(TAG, "RECEIVED GET_CITIES_EVENT: " + errorCode.name());
                super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_CITIES_EVENT: " + errorCode.name());
            }
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
            
            if(getBaseActivity() != null) getBaseActivity().showContentContainer();
            
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
            if(mMsgRequired!=null) mMsgRequired.setVisibility(View.VISIBLE);
            else Toast.makeText(getBaseActivity(), getString(R.string.register_required_text), Toast.LENGTH_SHORT).show();
        }
    }


}
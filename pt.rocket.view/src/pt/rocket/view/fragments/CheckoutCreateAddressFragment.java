/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.holoeverywhere.widget.CheckBox;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.forms.FormField;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.AddressCity;
import pt.rocket.framework.objects.AddressRegion;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetInitFormHelper;
import pt.rocket.helpers.address.GetCitiesHelper;
import pt.rocket.helpers.address.GetFormAddAddressHelper;
import pt.rocket.helpers.address.GetRegionsHelper;
import pt.rocket.helpers.address.SetNewAddressHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
public class CheckoutCreateAddressFragment extends BaseFragment implements OnClickListener, IResponseCallback, OnItemSelectedListener, OnCheckedChangeListener {

    private static final String TAG = LogTagHelper.create(CheckoutCreateAddressFragment.class);
    
    private static CheckoutCreateAddressFragment createAddressFragment;
    
    private static final String SHIPPING_FORM_TAG = "shipping";
    
    private static final String BILLING_FORM_TAG = "billing";
    
    private static final int IS_DEFAULT_SHIPPING_ADDRESS = 1;
    
    private static final int IS_DEFAULT_BILLING_ADDRESS = 1;
    
    private static final int ISNT_DEFAULT_SHIPPING_ADDRESS = 0;
    
    private static final int ISNT_DEFAULT_BILLING_ADDRESS = 0;
    
    private ViewGroup shippingFormContainer;

    private DynamicForm shippingFormGenerator;

    private Form formResponse;

    private ViewGroup billingContainer;

    private ViewGroup billingFormContainer;

    private DynamicForm billingFormGenerator;

    private ArrayList<AddressRegion> regions;
    
    private String selectedRegionOnShipping = "";
    
    private String selectedRegionOnBilling = "";

    private CheckBox billingCheckBox;

    private boolean wasCreatedTwoAddresses = false;
    
    
    /**
     * Fragment used to create an address
     * @return CheckoutCreateAddressFragment
     * @author sergiopereira
     */
    public static CheckoutCreateAddressFragment getInstance(Bundle bundle) {
        createAddressFragment = new CheckoutCreateAddressFragment();
        return createAddressFragment;
    }

    /**
     * Empty constructor
     * @author sergiopereira
     */
    public CheckoutCreateAddressFragment() {
        super(EnumSet.of(EventType.GET_CREATE_ADDRESS_FORM_EVENT, EventType.CREATE_ADDRESS_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.MyAccount, 
                BaseActivity.CHECKOUT_STEP_2);
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
        return inflater.inflate(R.layout.checkout_create_address_main, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        
        // Shipping form
        shippingFormContainer = (ViewGroup) view.findViewById(R.id.checkout_address_form_shipping_container);
        // Billing container
        billingContainer = (ViewGroup) view.findViewById(R.id.checkout_address_form_include_billing);
        // Billing form
        billingFormContainer = (ViewGroup) view.findViewById(R.id.checkout_address_form_billing_container);
        // Billing check box
        billingCheckBox = (CheckBox) view.findViewById(R.id.checkout_address_billing_checkbox); 
        billingCheckBox.setOnCheckedChangeListener((OnCheckedChangeListener) this);
        billingCheckBox.setChecked(true);
        // Next button
        view.findViewById(R.id.checkout_address_button_enter).setOnClickListener((OnClickListener) this);
        
        // Get and show form
        if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
            triggerInitForm();
        } else if(formResponse != null){
            loadCreateAddressForm(formResponse);
        } else {
            triggerCreateAddressForm();
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
        regions = null;
    }
    
    
    /**
     * Load the dynamic form
     * @param form
     * @author sergiopereira
     */
    private void loadCreateAddressForm(Form form) {
        Log.i(TAG, "LOAD CREATE ADDRESS FORM: " + form.name);

        // Shipping form
        shippingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), form);
        shippingFormContainer.removeAllViews();
        shippingFormContainer.addView(shippingFormGenerator.getContainer());                
        shippingFormContainer.refreshDrawableState();
        
        // Billing form
        billingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), form);
        billingFormContainer.removeAllViews();
        billingFormContainer.addView(billingFormGenerator.getContainer());
        billingFormContainer.refreshDrawableState();
        
        // Hide default check box
        hideDefaultCheckBoxes(shippingFormGenerator);
        hideDefaultCheckBoxes(billingFormGenerator);
        
        // Validate Regions
        if(regions == null) {
            FormField field = form.theRealFieldMapping.get("fk_customer_address_region");
            String url = field.getDataCalls().get("api_call");
            Log.d(TAG, "API CALL: " + url);
            triggerGetRegions(url);
        } else {
            Log.d(TAG, "REGIONS ISN'T NULL");
            setRegions(shippingFormGenerator, regions, SHIPPING_FORM_TAG);
            setRegions(billingFormGenerator, regions, BILLING_FORM_TAG);
        }
        
        getBaseActivity().showContentContainer(false);
        
    }
    
    /**
     * Hide the default check boxes
     * @param dynamicForm
     * @author sergiopereira
     */
    private void hideDefaultCheckBoxes(DynamicForm dynamicForm){
        DynamicFormItem item = dynamicForm.getItemByKey("is_default_shipping");
        item.getEditControl().setVisibility(View.GONE);
        item = dynamicForm.getItemByKey("is_default_billing");
        item.getEditControl().setVisibility(View.GONE);
    }
    
    /**
     * Method used to set the regions on the respective form
     * @param dynamicForm
     * @param regions
     * @param tag
     * @author sergiopereira
     */
    private void setRegions(DynamicForm dynamicForm, ArrayList<AddressRegion> regions, String tag){
        Log.d(TAG, "SET REGIONS REGIONS: " + tag);

        DynamicFormItem v = dynamicForm.getItemByKey("fk_customer_address_region");
        
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());

        ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<AddressRegion>(
                    getBaseActivity(), 
                    R.layout.form_spinner_item, 
                    regions);
        
        //adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setPrompt("Select country");
        spinner.setAdapter(adapter);
        spinner.setTag(tag);
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }
        
    /**
     * Validate the current region selection and update the cities
     * @param requestedRegionAndFields
     * @param cities
     * @author sergiopereira
     */
    private void setCitiesOnSelectedRegion(String requestedRegionAndFields, ArrayList<AddressCity> cities){
        if(requestedRegionAndFields.equals(selectedRegionOnShipping)) setCities(shippingFormGenerator, cities, SHIPPING_FORM_TAG);
        if(requestedRegionAndFields.equals(selectedRegionOnBilling)) setCities(billingFormGenerator, cities, BILLING_FORM_TAG);
    }
    
    /**
     * Method used to set the cities on the respective form
     * @param dynamicForm
     * @param cities
     * @param tag
     * @author sergiopereira
     */
    private void setCities(DynamicForm dynamicForm, ArrayList<AddressCity> cities, String tag){
        // SHIPPING
        DynamicFormItem v = dynamicForm.getItemByKey("fk_customer_address_city");
        ViewGroup group = (ViewGroup) v.getControl();
        group.removeAllViews();
        
        IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
        spinner.setLayoutParams(group.getLayoutParams());
        
        ArrayAdapter<AddressCity> adapter = new ArrayAdapter<AddressCity>(
                getBaseActivity(), 
                R.layout.form_spinner_item, 
                cities);
        //adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setPrompt("Select country");
        spinner.setAdapter(adapter);
        spinner.setTag(tag);
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
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
        if(id == R.id.checkout_address_button_enter) onClickCreateAddressButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on the next step button
     * @author sergiopereira
     */
    private void onClickCreateAddressButton() {
        Log.i(TAG, "ON CLICK: CREATE");
        if(billingCheckBox.isChecked() && shippingFormGenerator.validate()) {
            Log.i(TAG, "CREATE ADDRESS: IS SHIPPING AND IS BILLING TOO");    
            triggerCreateAddress(createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS));
            wasCreatedTwoAddresses = false;
        } else if(shippingFormGenerator.validate() && billingFormGenerator.validate()) {
            Log.i(TAG, "CREATE ADDRESS: SHIPPING AND BILLING");
            triggerCreateAddress(createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, ISNT_DEFAULT_BILLING_ADDRESS));
            triggerCreateAddress(createContentValues(billingFormGenerator, ISNT_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS));
            wasCreatedTwoAddresses  = true;
        }
    }
    
    /**
     * Method used to create the content values
     * @param dynamicForm
     * @param isDefaultShipping
     * @param isDefaultBilling
     * @return new content values
     * @author sergiopereira
     */
    private ContentValues createContentValues(DynamicForm dynamicForm, int isDefaultShipping, int isDefaultBilling){
        // Save content values
        ContentValues mContentValues = dynamicForm.save();
        // Get the region
        ViewGroup mRegionGroup = (ViewGroup) dynamicForm.getItemByKey("fk_customer_address_region").getControl();
        IcsSpinner mRegionSpinner = (IcsSpinner) mRegionGroup.getChildAt(0);
        AddressRegion mSelectedRegion = (AddressRegion) mRegionSpinner.getSelectedItem(); 
        Log.d(TAG, "SELECTED REGION: " + mSelectedRegion.getName() + " " + mSelectedRegion.getId());
        // Get the city
        ViewGroup mCityGroup = (ViewGroup) dynamicForm.getItemByKey("fk_customer_address_city").getControl();
        IcsSpinner mCitySpinner = (IcsSpinner) mCityGroup.getChildAt(0);
        AddressCity mSelectedCity = (AddressCity) mCitySpinner.getSelectedItem(); 
        Log.d(TAG, "SELECTED CITY: " + mSelectedCity.getValue() + " " + mSelectedCity.getId() );
        // Get some values
        int mRegionId = mSelectedRegion.getId();
        int mCityId = mSelectedCity.getId();
        String mCityName = mSelectedCity.getValue();
        // Put values
        mContentValues.put("Alice_Module_Customer_Model_AddressForm[is_default_billing]", isDefaultBilling);
        mContentValues.put("Alice_Module_Customer_Model_AddressForm[is_default_shipping]", isDefaultShipping);
        mContentValues.put("Alice_Module_Customer_Model_AddressForm[fk_customer_address_region]", mRegionId);
        mContentValues.put("Alice_Module_Customer_Model_AddressForm[fk_customer_address_city]", mCityId);
        mContentValues.put("Alice_Module_Customer_Model_AddressForm[city]", mCityName);
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
            FormField field = formResponse.theRealFieldMapping.get("fk_customer_address_city");
            String url = field.getDataCalls().get("api_call");
            Log.d(TAG, "API CALL: " + url);
            // Request the cities for this region id 
            int regionId = ((AddressRegion) object).getId();
            // Save the selected region on the respective variable
            String tag = (parent.getTag() != null) ? parent.getTag().toString() : "";
            if (tag.equals(SHIPPING_FORM_TAG)) {
                selectedRegionOnShipping = SHIPPING_FORM_TAG + "_" + regionId;
                triggerGetCities(url, regionId, selectedRegionOnShipping);
            } else if (tag.equals(BILLING_FORM_TAG)) {
                selectedRegionOnBilling = BILLING_FORM_TAG + "_" + regionId;
                triggerGetCities(url, regionId, selectedRegionOnBilling);
            }
        }
    }

    /**
     * ########### ON CHECK CHANGE LISTENER ###########  
     */
    /*
     * (non-Javadoc)
     * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Validate if is billing address
        if(isChecked) billingContainer.setVisibility(View.GONE);
        else billingContainer.setVisibility(View.VISIBLE);
    }
    
    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to create an address
     * @param values
     * @author sergiopereira
     */
    private void triggerCreateAddress(ContentValues values) {
        Log.i(TAG, "TRIGGER: CREATE ADDRESS");
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetNewAddressHelper.FORM_CONTENT_VALUES, values);
        triggerContentEvent(new SetNewAddressHelper(), bundle, this);
    }
    
    /**
     * Trigger to get the address form
     * @author sergiopereira
     */
    private void triggerCreateAddressForm(){
        Log.i(TAG, "TRIGGER: LOGIN FORM");
        triggerContentEvent(new GetFormAddAddressHelper(), null, this);
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
     * @author sergiopereira
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
     * @author sergiopereira
     */
    private void triggerGetCities(String apiCall, int region, String selectedTag){
        Log.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, apiCall);
        bundle.putInt(GetCitiesHelper.REGION_ID_TAG, region);
        bundle.putString(GetCitiesHelper.CUSTOM_TAG, selectedTag);
        triggerContentEventWithNoLoading(new GetCitiesHelper(), bundle, this);
    }
    
    
   
    /**
     * ############# RESPONSE #############
     */
    /**
     * Filter the success response
     * @param bundle
     * @return boolean
     * @author sergiopereira
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
        case INIT_FORMS:
            Log.d(TAG, "RECEIVED INIT_FORMS");
            triggerCreateAddressForm();
            break;
        case GET_CREATE_ADDRESS_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
            // Save and load form
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            formResponse = form;
            loadCreateAddressForm(form);
            break;
        case GET_REGIONS_EVENT:
            Log.d(TAG, "RECEIVED GET_REGIONS_EVENT");
            regions = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            setRegions(shippingFormGenerator, regions, SHIPPING_FORM_TAG);
            setRegions(billingFormGenerator, regions, BILLING_FORM_TAG);
            break;
        case GET_CITIES_EVENT:
            Log.d(TAG, "RECEIVED GET_CITIES_EVENT");
            Log.d(TAG, "REQUESTED REGION FROM FIELD: " + bundle.getString(GetCitiesHelper.CUSTOM_TAG));
            String requestedRegionAndField = bundle.getString(GetCitiesHelper.CUSTOM_TAG);
            ArrayList<AddressCity> cities = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            setCitiesOnSelectedRegion(requestedRegionAndField, cities);
            break;
        case CREATE_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
            if(wasCreatedTwoAddresses) {
                wasCreatedTwoAddresses = false;
                Toast.makeText(getBaseActivity(), "Billing Address created with success!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getBaseActivity(), "Shipping Address created with success!", Toast.LENGTH_SHORT).show();
                //getBaseActivity().onBackPressed();
                getBaseActivity().onSwitchFragment(FragmentType.SHIPPING_METHODS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }

            break;
        default:
            break;
        }
        
        return true;
    }
    
    /**
     * Filter the error response
     * 
     * TODO: ADD ERROR VALIDATIONS
     * 
     * @param bundle
     * @return boolean
     * @author sergiopereira
     */
    protected boolean onErrorEvent(Bundle bundle) {
    	if(!isVisible()){
    		return true;
    	}
        if(getBaseActivity().handleErrorEvent(bundle)){
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        switch (eventType) {
        case INIT_FORMS:
            Log.d(TAG, "RECEIVED INIT_FORMS");
            break;
        case GET_CREATE_ADDRESS_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
            break;
        case GET_REGIONS_EVENT:
            Log.d(TAG, "RECEIVED GET_REGIONS_EVENT");
            break;
        case GET_CITIES_EVENT:
            Log.d(TAG, "RECEIVED GET_CITIES_EVENT");
            break;
        case CREATE_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
            //
            if(wasCreatedTwoAddresses) wasCreatedTwoAddresses = false;
            //
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY); 
                showErrorDialog(errors);
                getBaseActivity().showContentContainer(false);
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
     * @author sergiopereira
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
            Toast.makeText(getBaseActivity(), "Please fill the form!", Toast.LENGTH_SHORT).show();
        }
    }
}

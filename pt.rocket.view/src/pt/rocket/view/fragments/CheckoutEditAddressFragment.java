/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsSpinner;

import pt.rocket.constants.FormConstants;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.forms.FormField;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.AddressCity;
import pt.rocket.framework.objects.AddressRegion;
import pt.rocket.framework.objects.Addresses.Address;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.CreateAddressHelper;
import pt.rocket.helpers.EditAddressHelper;
import pt.rocket.helpers.GetAddressCitiesHelper;
import pt.rocket.helpers.GetAddressRegionsHelper;
import pt.rocket.helpers.GetCreateAddressFormHelper;
import pt.rocket.helpers.GetEditAddressFormHelper;
import pt.rocket.helpers.GetInitFormHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * 
 * FIXME: Waiting for: 
 * > NAFAMZ-5428: MOBILE API - Action is "edit" in the response for Edit Address Form 
 * > NAFAMZ-5429: MOBILE API - Remove unused json fields from Add/Edit Address Form 
 * > NAFAMZ-5431: MOBILE API - The Edit Address Form doesn't have the api calls for regions and cities
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutEditAddressFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutEditAddressFragment.class);

    public static final String SELECTED_ADDRESS = "selected_address";
    
    private static CheckoutEditAddressFragment editAddressFragment;

    private Address selectedAddress;
    
    /**
     * 
     * @return
     */
    public static CheckoutEditAddressFragment getInstance(Bundle bundle) {
        // if (loginFragment == null)
        editAddressFragment = new CheckoutEditAddressFragment();
        editAddressFragment.selectedAddress = bundle.getParcelable(SELECTED_ADDRESS);
        return editAddressFragment;
    }

    private ViewGroup formContainer;

    private DynamicForm formGenerator;

    private Form formResponse;

    /**
     * Empty constructor
     */
    public CheckoutEditAddressFragment() {
        super(EnumSet.of(EventType.GET_EDIT_ADDRESS_FORM_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.MyAccount, 
                R.string.edit_address);
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
        return inflater.inflate(R.layout.checkout_edit_address, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        
        // Create address form
        formContainer = (ViewGroup) view.findViewById(R.id.checkout_edit_form_container);
        // Button
        view.findViewById(R.id.checkout_edit_button_enter).setOnClickListener(this);
        
        // Get and show form
        if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
            triggerInitForm();
        } else if(formResponse != null){
            loadForm(formResponse);
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
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
//        if (null != loginForm) {
//            Iterator<DynamicFormItem> iterator = loginForm.iterator();
//            while (iterator.hasNext()) {
//                DynamicFormItem item = iterator.next();
//                item.saveState(outState);
//            }
//            savedInstanceState = outState;
//        }
//        super.onSaveInstanceState(outState);
//        uiHelper.onSaveInstanceState(outState);
    }
    
    
    
    
    
    /**
     * ############# CLICK LISTENER #############
     */
    
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Login toggle
        if(id == R.id.checkout_edit_button_enter) onClickEditAddressButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    
    
    
    private void onClickEditAddressButton() {
        Log.i(TAG, "ON CLICK: LOGIN");
        ContentValues values = formGenerator.save();
        
        Log.d(TAG, "CURRENT CONTENT VALUES: " + values.toString());

        triggerEditAddress(values);
    }
    
   
    /**
     * ############# RESPONSE #############
     * 
     * TODO: ADD SUCCESS VALIDATIONS
     * 
     */
  
    protected boolean onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case INIT_FORMS:
            Log.d(TAG, "RECEIVED INIT_FORMS");
            triggerEditAddressForm();
            break;
        case GET_EDIT_ADDRESS_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
            // Save and load form
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            loadForm(form);
            break;
        case GET_REGIONS_EVENT:
            Log.d(TAG, "RECEIVED GET_REGIONS_EVENT");
            // TODO: IS NECESSARY?
            break;
        case GET_CITIES_EVENT:
            Log.d(TAG, "RECEIVED GET_CITIES_EVENT");
            // TODO: IS NECESSARY?
            break;
        case EDIT_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED EDIT_ADDRESS_EVENT");
            // TODO: Implement
            Toast.makeText(getBaseActivity(), "EDITED THE DUMMY ADDRESS", Toast.LENGTH_SHORT).show();
            getBaseActivity().onBackPressed();
            break;
        default:
            break;
        }
        
        return true;
    }

    /**
     * Load the dynamic form
     * 
     * @param form
     */
    private void loadForm(Form form) {
        Log.i(TAG, "LOAD FORM: " + form.name);
        formResponse = form;
        formGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), form);
        formContainer.removeAllViews();
        formContainer.addView(formGenerator.getContainer());        
        formContainer.refreshDrawableState();
        getBaseActivity().showContentContainer(false);
        
        
        try {
            Log.d(TAG, "SELECTED ADDRESS: " + selectedAddress.toString());
        } catch (Exception e) {
            Log.d(TAG, "SELECTED ADDRESS IS NULL: " + e.getMessage());
        }
    }


    
    /**
     * TODO: ADD ERROR VALIDATIONS
     * @param bundle
     * @return
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
        case GET_EDIT_ADDRESS_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_EDIT_ADDRESS_FORM_EVENT");
            break;
        case GET_REGIONS_EVENT:
            Log.d(TAG, "RECEIVED GET_REGIONS_EVENT");
            break;
        case GET_CITIES_EVENT:
            Log.d(TAG, "RECEIVED GET_CITIES_EVENT");
            break;
        case EDIT_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED EDIT_ADDRESS_EVENT");
            break;
        default:
            break;
        }
        
        return false;
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    private void triggerEditAddress(ContentValues values) {
        Log.i(TAG, "TRIGGER: EDIT");
        
        // TODO: Remove the true condition
        if (formGenerator.validate() || true) {
            
            Log.d(TAG, "FORM: IS VALID" + values.toString());
            
            values = new ContentValues();
            // 8132, 8131, 8080
            values.put("Alice_Module_Customer_Model_AddressForm[address_id]", "8132");
            values.put("Alice_Module_Customer_Model_AddressForm[first_name]", "MY FIRST NAME EDIT 1");
            values.put("Alice_Module_Customer_Model_AddressForm[last_name]", "MY LAST NAME EDIT 1");
            values.put("Alice_Module_Customer_Model_AddressForm[address1]", "MY ADDRESS EDIT 1");
            values.put("Alice_Module_Customer_Model_AddressForm[address2]", "MY ADDRESS EDIT 2");
            values.put("Alice_Module_Customer_Model_AddressForm[city]", "MY CITY");
            values.put("Alice_Module_Customer_Model_AddressForm[phone]", "123456789");
            values.put("Alice_Module_Customer_Model_AddressForm[fk_customer_address_region]", "234");
            values.put("Alice_Module_Customer_Model_AddressForm[fk_customer_address_city]", "419");
            
            Bundle bundle = new Bundle();
            bundle.putParcelable(EditAddressHelper.FORM_CONTENT_VALUES, values);
            triggerContentEvent(new EditAddressHelper(), bundle, this);
            
        }else {
            Log.d(TAG, "FORM: IS NOT VALID " + values.toString());
        }
        
    }
    
    private void triggerEditAddressForm(){
        Log.i(TAG, "TRIGGER: EDIT FORM");
        triggerContentEvent(new GetEditAddressFormHelper(), null, this);
    }
    
    private void triggerInitForm(){
        Log.i(TAG, "TRIGGER: INIT FORMS");
        triggerContentEvent(new GetInitFormHelper(), null, this);
    }
    
    private void triggerGetRegions(String apiCall){
        Log.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, apiCall);
        triggerContentEventWithNoLoading(new GetAddressRegionsHelper(), bundle, this);
    }
    
    private void triggerGetCities(String apiCall, int region){
        Log.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, apiCall);
        bundle.putInt(GetAddressCitiesHelper.REGION_ID_TAG, region);
        triggerContentEventWithNoLoading(new GetAddressCitiesHelper(), bundle, this);
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
        
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }


}

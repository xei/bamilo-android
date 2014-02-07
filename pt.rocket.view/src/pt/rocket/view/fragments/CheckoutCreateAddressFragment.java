/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.constants.FormConstants;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.forms.FormField;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.AddressCity;
import pt.rocket.framework.objects.AddressRegion;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetInitFormHelper;
import pt.rocket.helpers.address.SetNewAddressHelper;
import pt.rocket.helpers.address.GetFormAddAddressHelper;
import pt.rocket.helpers.address.GetCitiesHelper;
import pt.rocket.helpers.address.GetRegionsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.app.JumiaApplication;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsSpinner;

import de.akquinet.android.androlog.Log;

/**
 * 
 * FIXME: Waiting for: 
 * > NAFAMZ-5429: MOBILE API - Remove unused json fields from Add/Edit Address Form 
 * > NAFAMZ-5430: MOBILE API - The json response for Get Cities should be the same for each regions 
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutCreateAddressFragment extends BaseFragment implements OnClickListener, IResponseCallback, OnItemSelectedListener {

    private static final String TAG = LogTagHelper.create(CheckoutCreateAddressFragment.class);
    
    private static CheckoutCreateAddressFragment createAddressFragment;
    
    /**
     * 
     * @return
     */
    public static CheckoutCreateAddressFragment getInstance(Bundle bundle) {
        // if (loginFragment == null)
        createAddressFragment = new CheckoutCreateAddressFragment();
        return createAddressFragment;
    }

    private ViewGroup formContainer;

    private DynamicForm formGenerator;

    private Form formResponse;

    /**
     * Empty constructor
     */
    public CheckoutCreateAddressFragment() {
        super(EnumSet.of(EventType.GET_CREATE_ADDRESS_FORM_EVENT), 
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
        return inflater.inflate(R.layout.checkout_create_address, viewGroup, false);
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
        formContainer = (ViewGroup) view.findViewById(R.id.checkout_address_form_container);
        // Button
        view.findViewById(R.id.checkout_address_button_enter).setOnClickListener(this);
        
        // Get and show form
        if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
            triggerInitForm();
        } else if(formResponse != null){
            loadForm(formResponse);
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
        if(id == R.id.checkout_address_button_enter) onClickCreateAddressButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    
    
    
    private void onClickCreateAddressButton() {
        Log.i(TAG, "ON CLICK: LOGIN");
        ContentValues values = formGenerator.save();
        
        Log.d(TAG, "CURRENT CONTENT VALUES: " + values.toString());
        // Example:
        // Alice_Module_Customer_Model_AddressForm[is_default_billing]=1 
        // Alice_Module_Customer_Model_AddressForm[address1]=rua da esquina 
        // Alice_Module_Customer_Model_AddressForm[is_default_shipping]=1 
        // Alice_Module_Customer_Model_AddressForm[id_customer_address]= 
        // Alice_Module_Customer_Model_AddressForm[phone]=12345678 
        // Alice_Module_Customer_Model_AddressForm[address2]=rua da direita 
        // Alice_Module_Customer_Model_AddressForm[first_name]=my name 
        // Alice_Module_Customer_Model_AddressForm[last_name]=my last name 
        // Alice_Module_Customer_Model_AddressForm[country]= 
        // Alice_Module_Customer_Model_AddressForm[fk_customer_address_region]= 
        // Alice_Module_Customer_Model_AddressForm[fk_customer_address_city]=

        triggerCreateAddress(values);
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
            triggerCreateAddressForm();
            break;
        case GET_CREATE_ADDRESS_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
            // Save and load form
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            loadForm(form);
            break;
        case GET_REGIONS_EVENT:
            Log.d(TAG, "RECEIVED GET_REGIONS_EVENT");
            
            ArrayList<AddressRegion> regions = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            
            DynamicFormItem v = formGenerator.getItemByKey("fk_customer_address_region");
            
            ViewGroup group = (ViewGroup) v.getControl();
            group.removeAllViews();
            
            IcsSpinner spinner = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
            spinner.setLayoutParams(group.getLayoutParams());

            ArrayAdapter<AddressRegion> adapter = new ArrayAdapter<AddressRegion>(
                        getBaseActivity(), 
                        R.layout.form_spinner_item, 
                        new ArrayList<AddressRegion>(regions));
            //adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
            spinner.setPrompt("Select country");
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
            group.addView(spinner);
            
            
            break;
        case GET_CITIES_EVENT:
            Log.d(TAG, "RECEIVED GET_CITIES_EVENT");
            
            ArrayList<AddressCity> cities = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            
            DynamicFormItem v1 = formGenerator.getItemByKey("fk_customer_address_city");
            ViewGroup group1 = (ViewGroup) v1.getControl();
            group1.removeAllViews();
            
            IcsSpinner spinner1 = (IcsSpinner) View.inflate(getBaseActivity(), R.layout.form_icsspinner, null);
            spinner1.setLayoutParams(group1.getLayoutParams());
            
            ArrayAdapter<AddressCity> adapter1 = new ArrayAdapter<AddressCity>(
                    getBaseActivity(), 
                    R.layout.form_spinner_item, 
                    new ArrayList<AddressCity>(cities));
            //adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
            spinner1.setPrompt("Select country");
            spinner1.setAdapter(adapter1);
            spinner1.setOnItemSelectedListener(this);
            group1.addView(spinner1);
            
            
            break;
        case CREATE_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
            // TODO: Implement
            Toast.makeText(getBaseActivity(), "CREATED THE DUMMY ADDRESS", Toast.LENGTH_SHORT).show();
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
        
        try {
            
            // Get Regions
            FormField field = form.theRealFieldMapping.get("fk_customer_address_region");
            String url = field.getDataCalls().get("api_call");
            Log.d(TAG, "API CALL: " + url);
            triggerGetRegions(url);
            // Get Cities - TODO: Remove the value 240 
            field = form.theRealFieldMapping.get("fk_customer_address_city");
            url = field.getDataCalls().get("api_call");
            Log.d(TAG, "API CALL: " + url);
            triggerGetCities(url, 240);
            
        } catch (Exception e) {
            Log.e(TAG, "EXCEPTION: ", e);
        }

        
        // DynamicFormItem c = formGenerator.getItemByKey("");
        // fk_customer_address_region
        
        //setFormClickDetails();

//        // Show save state
//        if (null != this.savedInstanceState && null != loginForm) {
//            Iterator<DynamicFormItem> iter = loginForm.getIterator();
//            while (iter.hasNext()) {
//                DynamicFormItem item = iter.next();
//                item.loadState(savedInstanceState);
//            }
//        }
        
        formContainer.refreshDrawableState();
        getBaseActivity().showContentContainer(false);
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
            Toast.makeText(getBaseActivity(), "ERROR CREATING THE DUMMY ADDRESS", Toast.LENGTH_SHORT).show();
            break;
        default:
            break;
        }
        
        return false;
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    private void triggerCreateAddress(ContentValues values) {
        Log.i(TAG, "TRIGGER: LOGIN");
        
        // TODO: Remove the true condition
        if (formGenerator.validate() || true) {
            
            Log.d(TAG, "FORM: IS VALID" + values.toString());
            
            values = new ContentValues();
            values.put("Alice_Module_Customer_Model_AddressForm[first_name]", "MY FIRST NAME 1");
            values.put("Alice_Module_Customer_Model_AddressForm[last_name]", "MY LAST NAME 1");
            values.put("Alice_Module_Customer_Model_AddressForm[address1]", "MY ADDRESS 1");
            values.put("Alice_Module_Customer_Model_AddressForm[address2]", "MY ADDRESS 2");
            values.put("Alice_Module_Customer_Model_AddressForm[city]", "MY CITY");
            values.put("Alice_Module_Customer_Model_AddressForm[phone]", "123456789");
            values.put("Alice_Module_Customer_Model_AddressForm[fk_customer_address_region]", "234");
            values.put("Alice_Module_Customer_Model_AddressForm[fk_customer_address_city]", "419");
            //values.put("Alice_Module_Customer_Model_AddressForm[country]", "Germany");
            
            Bundle bundle = new Bundle();
            bundle.putParcelable(SetNewAddressHelper.FORM_CONTENT_VALUES, values);
            triggerContentEvent(new SetNewAddressHelper(), bundle, this);
            
        }else {
            Log.d(TAG, "FORM: IS NOT VALID " + values.toString());
        }
        
    }
    
    private void triggerCreateAddressForm(){
        Log.i(TAG, "TRIGGER: LOGIN FORM");
        triggerContentEvent(new GetFormAddAddressHelper(), null, this);
    }
    
    private void triggerInitForm(){
        Log.i(TAG, "TRIGGER: INIT FORMS");
        triggerContentEvent(new GetInitFormHelper(), null, this);
    }
    
    private void triggerGetRegions(String apiCall){
        Log.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, apiCall);
        triggerContentEventWithNoLoading(new GetRegionsHelper(), bundle, this);
    }
    
    private void triggerGetCities(String apiCall, int region){
        Log.i(TAG, "TRIGGER: GET REGIONS: " + apiCall);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, apiCall);
        bundle.putInt(GetCitiesHelper.REGION_ID_TAG, region);
        triggerContentEventWithNoLoading(new GetCitiesHelper(), bundle, this);
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

    /**
     * ########### ON ITEM SELECTED LISTENER ###########  
     */
    
    @Override
    public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getItemAtPosition(position);
        if(object instanceof AddressRegion) {
            FormField field = formResponse.theRealFieldMapping.get("fk_customer_address_city");
            String url = field.getDataCalls().get("api_call");
            Log.d(TAG, "API CALL: " + url);
            triggerGetCities(url, ((AddressRegion) object).getId());
        }
        
    }

    @Override
    public void onNothingSelected(IcsAdapterView<?> parent) {
        // TODO Auto-generated method stub
        
    }

}

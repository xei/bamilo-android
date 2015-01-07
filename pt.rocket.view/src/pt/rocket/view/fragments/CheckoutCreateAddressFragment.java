/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.absspinner.IcsAdapterView;
import pt.rocket.components.absspinner.IcsAdapterView.OnItemSelectedListener;
import pt.rocket.components.absspinner.IcsSpinner;
import pt.rocket.components.customfontviews.CheckBox;
import pt.rocket.components.customfontviews.EditText;
import pt.rocket.components.customfontviews.TextView;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.forms.FormField;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.AddressCity;
import pt.rocket.framework.objects.AddressRegion;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.tracking.TrackingEvent;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.address.GetCitiesHelper;
import pt.rocket.helpers.address.GetFormAddAddressHelper;
import pt.rocket.helpers.address.GetRegionsHelper;
import pt.rocket.helpers.address.SetNewAddressHelper;
import pt.rocket.helpers.configs.GetInitFormHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.InputType;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.Toast;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
    
    private static final String SHIPPING_REGION_POS = "save_shipping_rg_position";
    
    private static final String BILLING_REGION_POS = "save_billing_rg_position";
    
    private static final String SHIPPING_CITY_POS = "save_shipping_ct_position";
    
    private static final String BILLING_CITY_POS = "save_billing_ct_position";
    
    private static final String SHIPPING_STATE = "shipping_values";
    
    private static final String BILLING_STATE = "billing_values";
    
    private static final int IS_DEFAULT_SHIPPING_ADDRESS = 1;
    
    private static final int IS_DEFAULT_BILLING_ADDRESS = 1;
    
    private static final int ISNT_DEFAULT_SHIPPING_ADDRESS = 0;
    
    private static final int ISNT_DEFAULT_BILLING_ADDRESS = 0;
    
    private ViewGroup mShippingFormContainer;

    private DynamicForm shippingFormGenerator;

    private Form mFormResponse;

    private ViewGroup mBillingIncludeContainer;

    private ViewGroup mBillingFormContainer;

    private DynamicForm billingFormGenerator;

    private ArrayList<AddressRegion> regions;
    
    private String selectedRegionOnShipping = "";
    
    private String selectedRegionOnBilling = "";

    private CheckBox mIsSameCheckBox;

    private TextView mShippingTitle;

    private OrderSummary orderSummary;

    private View mMsgRequired;
    
    private Boolean oneAddressCreated = false;

    private ContentValues mShippingSavedValues;
    
    private ContentValues mBillingSavedValues;

    private boolean isCityIdAnEditText = false;
    
    private ScrollView mScrollViewContainer;
    
    private static final long ERROR_DELAY = 5000;

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
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                R.layout.checkout_create_address_main,
                R.string.checkout_label,
                KeyboardState.ADJUST_CONTENT,
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
        // Validate the saved values 
        if(savedInstanceState != null) {
            // Get the ship content values
            mShippingSavedValues = savedInstanceState.getParcelable(SHIPPING_STATE);
            // Get the bill content values
            mBillingSavedValues = savedInstanceState.getParcelable(BILLING_STATE);
            //Log.d(TAG, "SAVED CONTENT VALUES: " + mShippingSavedValues.toString());
            //Log.d(TAG, "SAVED CONTENT VALUES: " + ((mBillingSavedValues!= null) ? mBillingSavedValues.toString() : "IS NULL") );
        } else {
            Log.i(TAG, "SAVED CONTENT VALUES IS NULL");
        }
        Bundle params = new Bundle();        
        params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        params.putSerializable(TrackerDelegator.GA_STEP_KEY, TrackingEvent.CHECKOUT_STEP_CREATE_ADDRESS);    
        
        TrackerDelegator.trackCheckoutStep(params);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Scroll view
        mScrollViewContainer = (ScrollView) view.findViewById(R.id.checkout_address_form_scroll);
        // Shipping title
        mShippingTitle = (TextView) view.findViewById(R.id.checkout_address_form_shipping_title);
        // Shipping form
        mShippingFormContainer = (ViewGroup) view.findViewById(R.id.checkout_address_form_shipping_container);
        // Billing container
        mBillingIncludeContainer = (ViewGroup) view.findViewById(R.id.checkout_address_form_include_billing);
        // Billing form
        mBillingFormContainer = (ViewGroup) view.findViewById(R.id.checkout_address_form_billing_container);
        // Billing check box
        mIsSameCheckBox = (CheckBox) view.findViewById(R.id.checkout_address_billing_checkbox); 
        mIsSameCheckBox.setOnCheckedChangeListener((OnCheckedChangeListener) this);
        mIsSameCheckBox.setChecked(true);
        // Message
        mMsgRequired = view.findViewById(R.id.checkout_address_required_text);
        mMsgRequired.setOnClickListener(this);
        // Next button
        view.findViewById(R.id.checkout_address_button_enter).setOnClickListener((OnClickListener) this);
        
        //Validate is service is available
        if(JumiaApplication.mIsBound){
            // Get and show form
            if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
                triggerInitForm();
            } else if(mFormResponse != null && orderSummary != null){
                loadCreateAddressForm(mFormResponse);
            } else {
                triggerCreateAddressForm();
            }
        } else {
            showFragmentRetry(this);
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
        TrackerDelegator.trackPage(TrackingPage.NEW_ADDRESS, getLoadTime(), true);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "ON SAVE SATE");
        try {
            // Validate check
            if(mIsSameCheckBox.isChecked()) {
                ContentValues mContentValues = createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
                Log.d(TAG, "CONTENT SHIP VALUES: " + mContentValues.toString());
                outState.putParcelable(SHIPPING_STATE, mContentValues);
            } else {
                ContentValues mShipValues = createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, ISNT_DEFAULT_BILLING_ADDRESS);
                Log.d(TAG, "CONTENT SHIP VALUES: " + mShipValues.toString());
                ContentValues mBillValues = createContentValues(billingFormGenerator, ISNT_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
                Log.d(TAG, "CONTENT BILL VALUES: " + mBillValues.toString());
                
                outState.putParcelable(SHIPPING_STATE, mShipValues);
                outState.putParcelable(BILLING_STATE, mBillValues);
            }
        } catch (ClassCastException e) {
            Log.w(TAG, "INVALID CAST ON CREATE CONTENT VALUES", e);
        } catch (NullPointerException e) {
            Log.w(TAG, "SOME VIEW IS NULL", e);
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
        oneAddressCreated = false;
    }
    
    
    /**
     * Load the dynamic form
     * @param form
     * @author sergiopereira
     */
    private void loadCreateAddressForm(Form form) {
        Log.i(TAG, "LOAD CREATE ADDRESS FORM");
        // Shipping form
        shippingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), form);
        mShippingFormContainer.removeAllViews();
        mShippingFormContainer.addView(shippingFormGenerator.getContainer());                
        mShippingFormContainer.refreshDrawableState();
        // Billing form
        billingFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.ADDRESS_FORM, getActivity(), form);
        mBillingFormContainer.removeAllViews();
        mBillingFormContainer.addView(billingFormGenerator.getContainer());
        mBillingFormContainer.refreshDrawableState();
        // Define if CITY is a List or Text
        isCityIdAnEditText = (shippingFormGenerator.getItemByKey(RestConstants.JSON_CITY_ID_TAG).getEditControl() instanceof EditText);
        // Hide unused fields form
        hideSomeFields(shippingFormGenerator,false);
        hideSomeFields(billingFormGenerator, true);
        // Validate Regions
        if(regions == null) {
            FormField field = form.getFieldKeyMap().get(RestConstants.JSON_REGION_ID_TAG);
            String url = field.getDataCalls().get(RestConstants.JSON_API_CALL_TAG);
            triggerGetRegions(url);
        } else {
            setRegions(shippingFormGenerator, regions, SHIPPING_FORM_TAG);
            setRegions(billingFormGenerator, regions, BILLING_FORM_TAG);
        }
        
        // Show order summary
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, orderSummary);
        
        // Load the saved shipping values
        loadSavedValues(mShippingSavedValues, shippingFormGenerator);
        loadSavedValues(mBillingSavedValues, billingFormGenerator);
        
        // Show
        showFragmentContentContainer();
    }
    
    /**
     * Load the saved values to the respective form 
     * @param savedValues
     * @param dynamicForm
     * @author sergiopereira
     */
    private void loadSavedValues(ContentValues savedValues, DynamicForm dynamicForm){
        // Validate values
        if(savedValues != null) {
            // Get dynamic form and update
            Iterator<DynamicFormItem> iter = dynamicForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();
                try {
                    item.loadState(savedValues);
                    //Log.d(TAG, "CURRENT ITEM: " + item.getControl().getId());
                } catch (NullPointerException e) {
                    Log.w(TAG, "LOAD STATE: NOT CONTAINS KEY " + item.getKey());
                }
            }
        }
    }
    
    /**
     * Hide the default check boxes
     * @param dynamicForm
     * @author sergiopereira
     */
    private void hideSomeFields(DynamicForm dynamicForm, boolean isBilling) {
        DynamicFormItem item = dynamicForm.getItemByKey(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG);
        if (item != null) item.getEditControl().setVisibility(View.GONE);
        item = dynamicForm.getItemByKey(RestConstants.JSON_IS_DEFAULT_BILLING_TAG);
        if (item != null) item.getEditControl().setVisibility(View.GONE);
        // When CITY_ID is EditText use CITY
        if (isCityIdAnEditText) {
            item = dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG);
            if (item != null) item.getControl().setVisibility(View.GONE);
        } else {
            // Use CITY_ID
            item = dynamicForm.getItemByKey(RestConstants.JSON_CITY_TAG);
            item.getControl().setVisibility(View.GONE);
        }
        if(isBilling){
            item = dynamicForm.getItemByKey(RestConstants.JSON_GENDER_TAG);
            if (item != null){
                item.getMandatoryControl().setVisibility(View.GONE);
                item.getEditControl().setVisibility(View.GONE);
            } 
        }
        
        
    }

    /**
     * Method used to set the regions on the respective form
     * @param dynamicForm
     * @param regions
     * @param tag
     * @author sergiopereira
     */
    private void setRegions(DynamicForm dynamicForm, ArrayList<AddressRegion> regions, String tag){
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
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int defaultPosition = 0;
        if(mFormResponse.getFieldKeyMap().get(RestConstants.JSON_REGION_ID_TAG).getValue() != null && Integer.parseInt(mFormResponse.getFieldKeyMap().get(RestConstants.JSON_REGION_ID_TAG).getValue()) > 0){
            int defaultId = Integer.parseInt(mFormResponse.getFieldKeyMap().get(RestConstants.JSON_REGION_ID_TAG).getValue());
            for (int i = 0; i<regions.size(); i++) {
                if(regions.get(i).getId() == defaultId){
                    defaultPosition = i;
                    break;
                }
            } 
        }
        
        setSavedSelectedRegionPos(spinner, tag, defaultPosition);
        spinner.setTag(tag);
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }
    
    /**
     * Load and set the saved region position
     * @param spinner
     * @param tag
     * @author sergiopereira
     */
    private void setSavedSelectedRegionPos(IcsSpinner spinner, String tag, int defaultPosition){
        // Get saved value
        if( tag.equals(SHIPPING_FORM_TAG) && mShippingSavedValues != null && mShippingSavedValues.containsKey(SHIPPING_REGION_POS)) {
            int pos = mShippingSavedValues.getAsInteger(SHIPPING_REGION_POS);
            if( pos > 0 && pos < regions.size()){
                spinner.setSelection(pos);
            } else {
                spinner.setSelection(defaultPosition);
            }
        } else if( tag.equals(BILLING_FORM_TAG) && mBillingSavedValues != null && mBillingSavedValues.containsKey(BILLING_REGION_POS)) {
            int pos = mBillingSavedValues.getAsInteger(BILLING_REGION_POS);
            //Log.d(TAG, "SAVED BILLING REGION VALUE: " + pos);
            if( pos > 0 && pos < regions.size()){
                spinner.setSelection(pos);
            } else {
                spinner.setSelection(defaultPosition);                
            }
        } else {
            spinner.setSelection(defaultPosition);
        }
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
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        setSavedSelectedCityPos(spinner, cities, tag);
        spinner.setTag(tag);
        spinner.setOnItemSelectedListener(this);
        group.addView(spinner);
    }
    
    /**
     * Load and set the saved city position one time
     * @param spinner
     * @param tag
     * @author sergiopereira
     */
    private void setSavedSelectedCityPos(IcsSpinner spinner, ArrayList<AddressCity> array, String tag){
        // Get saved value
        if( tag.equals(SHIPPING_FORM_TAG) && mShippingSavedValues != null && mShippingSavedValues.containsKey(SHIPPING_CITY_POS)) {
            int pos = mShippingSavedValues.getAsInteger(SHIPPING_CITY_POS);
            //Log.d(TAG, "SAVED SHIPPING CITY VALUE: " + pos);
            if( pos > 0 && pos < array.size()) spinner.setSelection(pos);
            // Clean the saved city pos
            mShippingSavedValues.remove(SHIPPING_CITY_POS);
        } 
        else if( tag.equals(BILLING_FORM_TAG) && mBillingSavedValues != null && mBillingSavedValues.containsKey(BILLING_CITY_POS)) {
            int pos = mBillingSavedValues.getAsInteger(BILLING_CITY_POS);
            //Log.d(TAG, "SAVED BILLING CITY VALUE: " + pos);
            if( pos > 0 && pos < array.size()) spinner.setSelection(pos);
            // Clean the saved city pos
            mBillingSavedValues.remove(BILLING_CITY_POS);
        }
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
        //retry button
        else if(id == R.id.fragment_root_retry_button) onClickRetryButton();
        // message view
        else if(id == R.id.checkout_address_required_text) onClickRequired(view);
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click required text.
     * @author paulo
     */
    private void onClickRequired(View view){
        if(view.isShown()) mMsgRequired.setVisibility(View.GONE);
    }
    
    /**
     * Process the click on retry button.
     * @author paulo
     */
    private void onClickRetryButton() {
        Bundle bundle = new Bundle();
        if(null != JumiaApplication.CUSTOMER){
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
            
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }
    
    /**
     * Process the click on the next step button
     * @author sergiopereira
     */
    private void onClickCreateAddressButton() {
        Log.i(TAG, "ON CLICK: CREATE");
        
        // Clean the flag for each click
        oneAddressCreated = false;
        
        // Hide error message
        if(mMsgRequired != null) mMsgRequired.setVisibility(View.GONE);
        
        // Validate spinner
        ViewGroup mRegionGroup = (ViewGroup) shippingFormGenerator.getItemByKey(RestConstants.JSON_REGION_ID_TAG).getControl();
        // Validate if region group is filled
        if(!(mRegionGroup.getChildAt(0) instanceof IcsSpinner)) { 
            Log.w(TAG, "REGION SPINNER NOT FILL YET");
            // Show error message
            if(mMsgRequired != null){
                mMsgRequired.setVisibility(View.VISIBLE);
                changeMessageState(mMsgRequired, true, ERROR_DELAY);
            }
            return; 
        };

        // Validate check
        if(mIsSameCheckBox.isChecked()) {
            Log.i(TAG, "CREATE ADDRESS: IS SHIPPING AND IS BILLING TOO");
            ContentValues mContentValues = createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
            Log.d(TAG, "CONTENT VALUES: " + mContentValues.toString());
            triggerCreateAddress(mContentValues,false);
        } else {
            Log.i(TAG, "CREATE ADDRESS: SHIPPING AND BILLING");
            ContentValues mShipValues = createContentValues(shippingFormGenerator, IS_DEFAULT_SHIPPING_ADDRESS, ISNT_DEFAULT_BILLING_ADDRESS);
            Log.d(TAG, "CONTENT SHIP VALUES: " + mShipValues.toString());
            triggerCreateAddress(mShipValues,false);
            // only to be fired if the first succeds
//            ContentValues mBillValues = createContentValues(billingFormGenerator, ISNT_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
//            Log.d(TAG, "CONTENT BILL VALUES: " + mBillValues.toString());            
//            triggerCreateAddress(mBillValues,true);
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
        ViewGroup mRegionGroup = (ViewGroup) dynamicForm.getItemByKey(RestConstants.JSON_REGION_ID_TAG).getControl();
        // Get spinner
        IcsSpinner mRegionSpinner = (IcsSpinner) mRegionGroup.getChildAt(0);
        // Get selected region
        AddressRegion mSelectedRegion = (AddressRegion) mRegionSpinner.getSelectedItem();
        Log.d(TAG, "SELECTED REGION: " + mSelectedRegion.getName() + " " + mSelectedRegion.getId());
        
        // Used to save state
        int mSelectedPosition = mRegionSpinner.getSelectedItemPosition();
        if(isDefaultShipping == IS_DEFAULT_SHIPPING_ADDRESS) mContentValues.put(SHIPPING_REGION_POS, mSelectedPosition);
        else if(isDefaultBilling == IS_DEFAULT_BILLING_ADDRESS) mContentValues.put(BILLING_REGION_POS, mSelectedPosition);
        
        // Save region id
        int mRegionId = mSelectedRegion.getId();
        // Save city
        String mCityId = "";
        String mCityName = "";
        
        // Get from spinner
        View mControl = dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG).getControl();
        View mCityView = ((ViewGroup) mControl).getChildAt(0);
        if (mCityView instanceof IcsSpinner) {
            IcsSpinner mCitySpinner = (IcsSpinner) mCityView;
            // Get selected city
            AddressCity mSelectedCity = (AddressCity) mCitySpinner.getSelectedItem();
            Log.d(TAG, "SELECTED CITY: " + mSelectedCity.getValue() + " " + mSelectedCity.getId());
            mCityId = "" + mSelectedCity.getId();
            mCityName = mSelectedCity.getValue();
            
            // Used to save state
            int mCitySelectedPosition = mCitySpinner.getSelectedItemPosition();
            if (isDefaultShipping == IS_DEFAULT_SHIPPING_ADDRESS) mContentValues.put(SHIPPING_CITY_POS, mCitySelectedPosition);
            else if (isDefaultBilling == IS_DEFAULT_BILLING_ADDRESS) mContentValues.put(BILLING_CITY_POS, mCitySelectedPosition);
        }
        // Get from edit text
        else if (mCityView instanceof RelativeLayout) {
            /*-mCityView = ((RelativeLayout) mCityView).getChildAt(0);
            if (mCityView instanceof EditText) {
                EditText mCityEdit = (EditText) dynamicForm.getItemByKey(RestConstants.JSON_CITY_ID_TAG).getEditControl();
                String city = mCityEdit.getText().toString(); 
                // use this to load this into EditText after rotations. This value will be submitted to API
                mCityId = city;
                mCityName = city;
                Log.d(TAG, "SELECTED CITY: " + mCityName);
            }*/
        }
        // Unexpected
        else {
            Log.e(TAG, RestConstants.JSON_CITY_ID_TAG + " IS AN UNEXPECTED VIEW: " + mCityView.getClass().getName());
        }
        
        // Put values
        for (Entry<String, Object> value : mContentValues.valueSet()) {
            String key = value.getKey();
            if(key.contains(RestConstants.JSON_IS_DEFAULT_BILLING_TAG)) mContentValues.put(key, isDefaultBilling);
            else if(key.contains(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG)) mContentValues.put(key, isDefaultShipping);
            else if(key.contains(RestConstants.JSON_REGION_ID_TAG)) mContentValues.put(key, mRegionId);
            else if(key.contains(RestConstants.JSON_CITY_ID_TAG)) mContentValues.put(key, mCityId);
            else if(!isCityIdAnEditText && key.contains(RestConstants.JSON_CITY_TAG)) mContentValues.put(key, mCityName);
        }
   
        // return the new content values
        return mContentValues;
    }
    
    /**
     * ########### ON ITEM SELECTED LISTENER ###########  
     */
    /*
     * (non-Javadoc)
     * @see pt.rocket.components.absspinner.IcsAdapterView.OnItemSelectedListener#onNothingSelected(pt.rocket.components.absspinner.IcsAdapterView)
     */
    @Override
    public void onNothingSelected(IcsAdapterView<?> parent) { }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.components.absspinner.IcsAdapterView.OnItemSelectedListener#onItemSelected(pt.rocket.components.absspinner.IcsAdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "CURRENT TAG: " + parent.getTag());
        Object object = parent.getItemAtPosition(position);
        if (object instanceof AddressRegion) {
            FormField field = mFormResponse.getFieldKeyMap().get(RestConstants.JSON_CITY_ID_TAG);
//            // Validate API call
//            if (field.getDataCalls() == null) {
//                Log.w(TAG, "GET CITY: API CALL IS NULL");
//                return;
//            }
            if (InputType.list == field.getInputType()) {
                // Get API call
                String url = field.getDataCalls().get(RestConstants.JSON_API_CALL_TAG);
                Log.d(TAG, "API CALL: " + url);
                if (url != null) {
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
                } else {
                    Log.e(TAG, "No " + RestConstants.JSON_API_CALL_TAG + " on "+ RestConstants.JSON_CITY_ID_TAG);
                }
            } else if (InputType.text == field.getInputType()) {
                // City
                // loadCityEditText(field.getInputType());
            } else {
                Log.e(TAG, RestConstants.JSON_API_CALL_TAG + " with an expected inputType");
                super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "GET CITIES EVENT: IS EMPTY");
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
        updateContainers(isChecked);
    }
    
    /**
     * Update the container according with check box
     * @param isSame
     * @author sergiopereira
     */
    private void updateContainers(Boolean isSame) {
        // Validate if is billing address
        if(isSame) {
            // Set title
            mShippingTitle.setText(getString(R.string.action_label_add_address));
            // Hide billing container
            if (mBillingIncludeContainer != null) {
                mBillingIncludeContainer.setVisibility(View.GONE);
            }
        } else {
            try {
                // make the scroll move downward 200px just to let the user see the new billing address form
                mScrollViewContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollViewContainer.smoothScrollBy(0, 200);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Set title
            mShippingTitle.setText(getString(R.string.billing_shipping_label));
            // Hide billing container
            mBillingIncludeContainer.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to create an address
     * @param values
     * @author sergiopereira
     */
    private void triggerCreateAddress(ContentValues values, boolean isBilling) {
        Log.i(TAG, "TRIGGER: CREATE ADDRESS");
        Bundle args = getArguments();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetNewAddressHelper.FORM_CONTENT_VALUES, values);
        bundle.putBoolean(SetNewAddressHelper.IS_BILLING, isBilling);
        // Validate origin
        if(null != args && args.containsKey(ConstantsIntentExtra.IS_SIGNUP))
            bundle.putBoolean(SetNewAddressHelper.IS_FROM_SIGNUP,args.getBoolean(ConstantsIntentExtra.IS_SIGNUP, false));
        // Trigger
        triggerContentEvent(new SetNewAddressHelper(), bundle, this);

        // Hide the keyboard
        getBaseActivity().hideKeyboard();
    }
    
    /**
     * Trigger to get the address form
     * @author sergiopereira
     */
    private void triggerCreateAddressForm(){
        Log.i(TAG, "TRIGGER: LOGIN FORM");
        Bundle args = getArguments();
        
        if(null != args && args.containsKey(ConstantsIntentExtra.IS_SIGNUP)){
            if(args.getBoolean(ConstantsIntentExtra.IS_SIGNUP, false))
                triggerContentEvent(new GetFormAddAddressHelper(), args, this);            
        } else {
            triggerContentEvent(new GetFormAddAddressHelper(), null, this);    
        }
        
        
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
        
        if(isOnStoppingProcess){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
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
            // Get order summary
            orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
            // Save and load form
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            mFormResponse = form;
            loadCreateAddressForm(form);
            break;
        case GET_REGIONS_EVENT:
            Log.d(TAG, "RECEIVED GET_REGIONS_EVENT");
            regions = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            // Validate response
            if (super.hasContent(regions)){
                setRegions(shippingFormGenerator, regions, SHIPPING_FORM_TAG);
                setRegions(billingFormGenerator, regions, BILLING_FORM_TAG);
            } else {
                Log.w(TAG, "GET REGIONS EVENT: IS EMPTY");
                super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "GET REGIONS EVENT: IS EMPTY");
            }
            break;
        case GET_CITIES_EVENT:
            Log.d(TAG, "RECEIVED GET_CITIES_EVENT");
            Log.d(TAG, "REQUESTED REGION FROM FIELD: " + bundle.getString(GetCitiesHelper.CUSTOM_TAG));
            String requestedRegionAndField = bundle.getString(GetCitiesHelper.CUSTOM_TAG);
            ArrayList<AddressCity> cities = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            setCitiesOnSelectedRegion(requestedRegionAndField, cities);
            break;
        case CREATE_ADDRESS_SIGNUP_EVENT:
        case CREATE_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
            //GTM
            TrackerDelegator.trackAddAddress(true);
            // Waiting for both responses
            if(!mIsSameCheckBox.isChecked() && !oneAddressCreated){
                oneAddressCreated = true;
                
                if (null != billingFormGenerator) {
                    ContentValues mBillValues = createContentValues(billingFormGenerator, ISNT_DEFAULT_SHIPPING_ADDRESS, IS_DEFAULT_BILLING_ADDRESS);
                    triggerCreateAddress(mBillValues, true);
                }
                break;
            }
            
            // Get next step
            FragmentType nextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            if(nextFragment == null || nextFragment == FragmentType.UNKNOWN){
                Log.w(TAG, "NEXT STEP IS UNKNOWN OR NULL -> FALL BACK MY_ADDRESSES");
                nextFragment = FragmentType.MY_ADDRESSES;
            }
            Toast.makeText(getBaseActivity(), getString(R.string.create_addresses_success), Toast.LENGTH_SHORT).show();
            FragmentController.getInstance().popLastEntry(FragmentType.CREATE_ADDRESS.toString());
            getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
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
     * @author sergiopereira
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
        case GET_CREATE_ADDRESS_FORM_EVENT:
            Log.w(TAG, "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
            break;
        case GET_REGIONS_EVENT:
            Log.w(TAG, "RECEIVED GET_REGIONS_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
            break;
        case GET_CITIES_EVENT:
            Log.w(TAG, "RECEIVED GET_CITIES_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_CITIES_EVENT");
            break;
        case CREATE_ADDRESS_SIGNUP_EVENT:
        case CREATE_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
            //GTM
            TrackerDelegator.trackAddAddress(false);
            // Clean flag to wait for both different responses
            oneAddressCreated = false;
            
            //
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                @SuppressWarnings("unchecked")
                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY); 
                showErrorDialog(errors);
                showFragmentContentContainer();
            } else {
                Log.w(TAG, "RECEIVED CREATE_ADDRESS_EVENT: " + errorCode.name());
                super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED CREATE_ADDRESS_EVENT" + errorCode.name());
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
    private void showErrorDialog(HashMap<String, List<String>> errors) {
        Log.d(TAG, "SHOW LOGIN ERROR DIALOG");
        List<String> errorMessages = null;
        if (errors != null) {
            errorMessages = (List<String>) errors.get(RestConstants.JSON_VALIDATE_TAG);
        }
        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
            showFragmentContentContainer();
            dialog = DialogGenericFragment.newInstance(true, true, false,
                    getString(R.string.error_login_title),
                    errorMessages.get(0),
                    getString(R.string.ok_label),
                    "",
                    new OnClickListener() {
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
            if (mMsgRequired != null){
                mMsgRequired.setVisibility(View.VISIBLE);
                changeMessageState(mMsgRequired, true, ERROR_DELAY);

            }
            else Toast.makeText(getBaseActivity(), getString(R.string.register_required_text), Toast.LENGTH_SHORT).show();
        }
    }
    
    
    /**
     * 
     * function that changes visibility state of a view giving a delay 
     * 
     * @param view
     * @param isToHide
     * @param delay
     */
    private void changeMessageState(final View view, final boolean isToHide, long delay){
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isToHide) view.setVisibility(View.GONE);
                else view.setVisibility(View.VISIBLE);
            }
        }, delay);
    }
    
}

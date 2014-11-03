/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.CountryAdapter;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.CountriesConfigsTableHelper;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.database.LastViewedTableHelper;
import pt.rocket.framework.objects.CountryObject;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.helpers.configs.GetCountriesGeneralConfigsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;

/**
 * Fragment used on SplashScreen on First Use
 * 
 * @author sergiopereira
 */
public class ChooseCountryFragment extends BaseFragment implements IResponseCallback, OnClickListener {

    private static final String TAG = LogTagHelper.create(ChooseCountryFragment.class);

    private static final int SHOP_NOT_SELECTED = -1;

    private static ChooseCountryFragment sChangeCountryFragment;

    private Context context;

    private DialogGenericFragment dialog;

    private int selected = SHOP_NOT_SELECTED;

    private CountryAdapter countryAdapter;

    private boolean isChangeCountry;

    /**
     * Get instance
     * @return ChangeCountryFragment
     */
    public static ChooseCountryFragment getInstance() {
        if (sChangeCountryFragment == null) {
            sChangeCountryFragment = new ChooseCountryFragment();
        }

        return sChangeCountryFragment;
    }

    /**
     * Empty constructor
     */
    public ChooseCountryFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Country,
                R.layout.change_country,
                R.string.nav_country,
                KeyboardState.NO_ADJUST_CONTENT);
        // R.string.nav_country
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
        this.setRetainInstance(true);
        Log.i(TAG, "ON CREATE");
        context = getActivity().getApplicationContext();
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        
        if(ShopSelector.getShopId() != null)
            triggerGetJumiaCountries();
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
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
            setList();    
        
        isChangeCountry = true;
        
        if(selected == SHOP_NOT_SELECTED) {
            isChangeCountry = false;
            // getBaseActivity().hideTitle();
            getBaseActivity().getSupportActionBar().setHomeButtonEnabled(false);
        }
        
        if(selected != SHOP_NOT_SELECTED){
            // getBaseActivity().hideTitle();
            // getBaseActivity().setCheckoutHeader(R.string.nav_country);
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#allowBackPressed()
     */
    @Override
    public boolean allowBackPressed() {
        if (selected == SHOP_NOT_SELECTED) {
            getBaseActivity().finish();
        }
        return super.allowBackPressed();
    }
    
    /**
     * #### METHODS ####
     */

    private void setList() {

        // Data
        String[] countries = null;
        String[] flagsList = null;
        SharedPreferences sharedPrefs = context.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String selectedCountry = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ISO, null);
        
        if(JumiaApplication.INSTANCE.countriesAvailable == null || JumiaApplication.INSTANCE.countriesAvailable.size() == 0){
            JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        }
        
        int countriesAvailable = JumiaApplication.INSTANCE.countriesAvailable.size();
        Log.d(TAG, "COUNTRIES SIZE: " + countriesAvailable);
        
        int count = 0;
        countries = new String[countriesAvailable];
        flagsList = new String[countriesAvailable];
        for (CountryObject country : JumiaApplication.INSTANCE.countriesAvailable) {
            countries[count] = country.getCountryName();
            flagsList[count] = country.getCountryFlag();
            if(selectedCountry != null && selectedCountry.equalsIgnoreCase(country.getCountryIso())){
                selected = count;
            }
            count++;
        }
        
        // Inflate
        final ListView countryList = (ListView) getView().findViewById(R.id.change_country_list);
        if(countryAdapter == null){
            countryAdapter = new CountryAdapter(getActivity(), countries, flagsList);
        }
        countryAdapter.updateValues(countries);
        countryList.setAdapter(countryAdapter);
        countryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        if (selected > SHOP_NOT_SELECTED) {
            countryList.setItemChecked(selected, true);
        }

        // Listener
        countryList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryList.setItemChecked(position, true);
                if (position == selected) {
                    return;
                } else if (selected == SHOP_NOT_SELECTED) {
                    setCountry(position);
                } else if (position != selected) {
                    isChangeCountry = true;
                    showWarningDialog(position);
                }
            }
        });
    }

    private void showWarningDialog(final int position) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        dialog = DialogGenericFragment.newInstance(true, true, false,
                getString(R.string.nav_country),
                getString(R.string.nav_country_warning), getString(R.string.cancel_label),
                getString(R.string.yes_label), new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        int id = v.getId();
                        if (id == R.id.button1) {
                            getBaseActivity().onBackPressed();
                        } else if (id == R.id.button2) {
                            setCountry(position);
                        }
                    }
                });
        dialog.show(fm, null);
    }

    protected void setCountry(int position) {
        JumiaApplication.INSTANCE.cleanAllPreviousCountryValues();
        getBaseActivity().updateCartInfo();
        if(isChangeCountry){
            LastViewedTableHelper.deleteAllLastViewed();
            FavouriteTableHelper.deleteAllFavourite();
        }
        
        System.gc();
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ID, JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryIso().toLowerCase());
        editor.putBoolean(Darwin.KEY_COUNTRY_CHANGED, isChangeCountry);
        editor.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true);
        /**
         * Save the Selected Country Configs 
         * KEY_SELECTED_COUNTRY_ID will contain the Country ISO that will be use to identify the selected country al over the App.
         */
        Log.i(TAG, "code1DarwinComponent : selected : "+JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryName());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_NAME, JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryName());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_URL, JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryUrl());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_FLAG, JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryFlag());
        Log.i(TAG, "code1flag : "+calculateMapImageResolution(JumiaApplication.INSTANCE.countriesAvailable.get(position)));
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_MAP_FLAG, calculateMapImageResolution(JumiaApplication.INSTANCE.countriesAvailable.get(position)));
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ISO, JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryIso().toLowerCase());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP, JumiaApplication.INSTANCE.countriesAvailable.get(position).isCountryForceHttps());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_IS_LIVE, JumiaApplication.INSTANCE.countriesAvailable.get(position).isCountryIsLive());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_REST_BASE, getString(R.string.jumia_global_api_version));
        editor.putBoolean(ConstantsSharedPrefs.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
        editor.commit();
        
        
        TrackerDelegator.trackShopchanged();
        ActivitiesWorkFlow.splashActivityNewTask(getActivity());
        getActivity().finish();
    }
    
    private String calculateMapImageResolution(CountryObject mCountryObject){
        String mapImage =  mCountryObject.getCountryMapMdpi();
        DisplayMetrics dm = new DisplayMetrics();
        getBaseActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int dpiClassification = dm.densityDpi;
        switch (dpiClassification) {
        case DisplayMetrics.DENSITY_HIGH:
        	Log.i(TAG, "code1desnsity  DENSITY_HIGH");
            mapImage =  mCountryObject.getCountryMapHdpi();
            break;
        case DisplayMetrics.DENSITY_XHIGH:
        	Log.i(TAG, "code1desnsity  DENSITY_XHIGH");
            mapImage =  mCountryObject.getCountryMapXhdpi();
            break;
        default:
        	Log.i(TAG, "code1desnsity  DENSITY_MEDIUM");
        	mapImage =  mCountryObject.getCountryMapMdpi();
            break;
        }
        return mapImage;
    }
    
    
    /**
     * ############# TRIGGERS ############# 
     */
    
    /**
     * Trigger used to get all Jumia Available countries
     * @author sergiopereira
     */
    private void triggerGetJumiaCountries() {
        //Validate is service is available
        if(JumiaApplication.mIsBound){
            triggerContentEvent(new GetCountriesGeneralConfigsHelper(), null, (IResponseCallback) this);
        } else {
            showFragmentRetry(this);
        }
        
    }

    @Override
    public void onClick(View v) {
        // Validate the view id
        int id = v.getId();
        // Case retry button
        if(id == R.id.fragment_root_retry_button) onClickRetryButton();
        // Case Unknown
        else Log.w(TAG, "WARNING ON CLICK: UNKNWON");
    }
    
    /**
     * Process the click on retry button
     * @author sergiopereira
     */
    private void onClickRetryButton(){
        Log.d(TAG, "ON CLICK: RETRY BUTTON");
        triggerGetJumiaCountries();
    }
    
    
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED SUCCESS EVENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get event type
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate event type
        switch (eventType) {
        case GET_GLOBAL_CONFIGURATIONS:
            Log.d(TAG, "RECEIVED GET_GLOBAL_CONFIGURATIONS");
            // Get countries
            JumiaApplication.INSTANCE.countriesAvailable = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            // Show countries
            setList();
            validateShopSelected();
            showFragmentContentContainer();
            break;
        default:
            Log.w(TAG, "WARNING RECEIVED UNKOWN EVENT: " + eventType.toString());
            break;
        }
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED ERROR EVENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get event type and error type
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        // Validate event type
        switch (eventType) {
        case GET_GLOBAL_CONFIGURATIONS:
            Log.d(TAG, "RECEIVED GET_GLOBAL_CONFIGURATIONS");
            // Show retry view
            showFragmentRetry(this);
            break;
        default:
            Log.w(TAG, "WARNING RECEIVED UNKOWN EVENT: " + eventType.toString());
            break;
        }
    }
    
    
    // TODO: Validate method
    private void validateShopSelected(){
        
        isChangeCountry = true;
        
        if(selected == SHOP_NOT_SELECTED) {
            isChangeCountry = false;
            // getBaseActivity().hideTitle();
            getBaseActivity().getSupportActionBar().setHomeButtonEnabled(false);
        }
        
        if(selected != SHOP_NOT_SELECTED){
            // getBaseActivity().hideTitle();
            // getBaseActivity().setCheckoutHeader(R.string.nav_country);
        }
    }
    
}

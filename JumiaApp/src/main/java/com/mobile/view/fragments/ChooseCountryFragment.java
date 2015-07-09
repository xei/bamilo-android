/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobile.app.JumiaApplication;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.controllers.CountryAdapter;
import com.mobile.helpers.configs.GetCountriesGeneralConfigsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.database.BrandsTableHelper;
import com.mobile.newFramework.database.CountriesConfigsTableHelper;
import com.mobile.newFramework.database.FavouriteTableHelper;
import com.mobile.newFramework.database.LastViewedTableHelper;
import com.mobile.newFramework.objects.configs.CountryObject;
import com.mobile.newFramework.tracking.Ad4PushTracker;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.LogTagHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Fragment used on SplashScreen on First Use
 * 
 * @author sergiopereira
 */
public class ChooseCountryFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = LogTagHelper.create(ChooseCountryFragment.class);

    private static final int SHOP_NOT_SELECTED = -1;

    private Context context;

    private int selected = SHOP_NOT_SELECTED;

    private CountryAdapter countryAdapter;

    private boolean isChangeCountry;

    /**
     * Get instance
     * @return ChangeCountryFragment
     */
    public static ChooseCountryFragment getInstance() {
        return new ChooseCountryFragment();
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        context = getActivity().getApplicationContext();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        
        // Validate the current shop
        if(ShopSelector.getShopId() != null) {
            // Get and show new available countries
            isChangeCountry = true;
            triggerGetJumiaCountries();
        } else {
            // Show available countries from memory/database, loaded in splash screen.
            isChangeCountry = false;
            showAvailableCountries();
            getBaseActivity().getSupportActionBar().setHomeButtonEnabled(false);
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
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
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        Print.i(TAG, "ON DESTROY");
        super.onDestroy();
        // Clean memory
        countryAdapter = null;
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#allowBackPressed()
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

    private void showAvailableCountries() {

        // Data
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String selectedCountry = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ISO, null);
        
        if(JumiaApplication.INSTANCE.countriesAvailable == null || JumiaApplication.INSTANCE.countriesAvailable.size() == 0){
            // Get countries from database
            JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
            // Validate data from database
            if(JumiaApplication.INSTANCE.countriesAvailable.size() == 0) {
                triggerGetJumiaCountries();
                return;   
            }
        } 
        
        int countriesAvailable = JumiaApplication.INSTANCE.countriesAvailable.size();
        Print.d(TAG, "COUNTRIES SIZE: " + countriesAvailable);
        
        int count = 0;
        String[] countries = new String[countriesAvailable];
        String[] flagsList = new String[countriesAvailable];
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
                if (selected == SHOP_NOT_SELECTED) {
                    setCountry(position);
                } else if (position != selected) {
                    isChangeCountry = true;
                    setCountry(position);
                }
            }
        });
    }

    @Deprecated
    private void showWarningDialog(final int position) {

        dismissDialogFragment();

        dialog = DialogGenericFragment.newInstance(true, false,
                getString(R.string.nav_country),
                getString(R.string.nav_country_warning), 
                getString(R.string.cancel_label),
                getString(R.string.yes_label), 
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss dialog
                        dismissDialogFragment();
                        // Validate click
                        int id = v.getId();
                        if (id == R.id.button1) {
                            getBaseActivity().onBackPressed();
                        } else if (id == R.id.button2) {
                            if(!setCountry(position)){
                                getBaseActivity().onBackPressed();
                            }

                        }
                    }
                });
        dialog.show(getBaseActivity().getSupportFragmentManager(), null);
    }

    /**
     * Save the selected country
     * @param position
     *
     * @return true if changed successfully
     * @return false otherwise
     */
    protected boolean setCountry(int position) {

        ArrayList<CountryObject> countriesAvailable = JumiaApplication.INSTANCE.countriesAvailable;

        if(position > -1 && position < countriesAvailable.size() &&     // Position validation
                countriesAvailable.get(position) != null) {             //Null pointer validation

            // Set new country
            SharedPreferences sharedPrefs = getBaseActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(Darwin.KEY_SELECTED_COUNTRY_ID, JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryIso().toLowerCase());
            editor.putBoolean(Darwin.KEY_COUNTRY_CHANGED, isChangeCountry);
            /**
             * Save the Selected Country Configs
             * KEY_SELECTED_COUNTRY_ID will contain the Country ISO that will be use to identify the selected country al over the App.
             */
            Print.i(TAG, "code1DarwinComponent : selected : " + JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryName());
            editor.putString(Darwin.KEY_SELECTED_COUNTRY_NAME, JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryName());
            editor.putString(Darwin.KEY_SELECTED_COUNTRY_URL, JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryUrl());
            editor.putString(Darwin.KEY_SELECTED_COUNTRY_FLAG, JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryFlag());
            editor.putString(Darwin.KEY_SELECTED_COUNTRY_ISO, JumiaApplication.INSTANCE.countriesAvailable.get(position).getCountryIso().toLowerCase());
            editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP, JumiaApplication.INSTANCE.countriesAvailable.get(position).isCountryForceHttps());
            editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_IS_LIVE, JumiaApplication.INSTANCE.countriesAvailable.get(position).isCountryIsLive());
            editor.putBoolean(Darwin.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
            editor.apply();

            // Clean memory
            JumiaApplication.INSTANCE.cleanAllPreviousCountryValues();
            // Is changing country
            if (isChangeCountry) {
                LastViewedTableHelper.deleteAllLastViewed();
                FavouriteTableHelper.deleteAllFavourite();
                BrandsTableHelper.clearBrands();
                TrackerDelegator.trackShopChanged();
            }
            // Clear Ad4Push prefs
            Ad4PushTracker.clearAllSavedData(getBaseActivity().getApplicationContext());
            // Show splash screen
            ActivitiesWorkFlow.splashActivityNewTask(getBaseActivity());
            // Finish MainFragmentActivity
            getBaseActivity().finish();
            return true;
        }
        return false;
    }

    /*
     * ############# TRIGGERS ############# 
     */
    
    /**
     * Trigger used to get all Jumia Available countries
     * @author sergiopereira
     */
    private void triggerGetJumiaCountries() {
        triggerContentEvent(new GetCountriesGeneralConfigsHelper(), null, this);
    }

    /*
     * ############# LISTENERS ############# 
     */
    
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
     * Process the click on retry button
     * @author sergiopereira
     */
    private void onClickRetryButton(){
        Print.d(TAG, "ON CLICK: RETRY BUTTON");
        triggerGetJumiaCountries();
    }
    
    /*
     * ############# RESPONSE ############# 
     */
    
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Print.i(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED SUCCESS EVENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get event type
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate event type
        switch (eventType) {
        case GET_GLOBAL_CONFIGURATIONS:
            Print.d(TAG, "RECEIVED GET_GLOBAL_CONFIGURATIONS");
            // Get countries
            JumiaApplication.INSTANCE.countriesAvailable = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            // Show countries
            showAvailableCountries();
            showFragmentContentContainer();
            break;
        default:
            Print.w(TAG, "WARNING RECEIVED UNKNOWN EVENT: " + eventType.toString());
            break;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED ERROR EVENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get event type and error type
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        if(super.handleErrorEvent(bundle)) return;
        
        // Validate event type
        switch (eventType) {
        case GET_GLOBAL_CONFIGURATIONS:
            Print.d(TAG, "RECEIVED GET_GLOBAL_CONFIGURATIONS");
            // Show retry view
            showFragmentErrorRetry();
            break;
        default:
            Print.w(TAG, "WARNING RECEIVED UNKNOWN EVENT: " + eventType.toString());
            break;
        }
    }
    
}


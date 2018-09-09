package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.controllers.ChooseLanguageController;
import com.bamilo.android.appmodule.bamiloapp.controllers.CountryAdapter;
import com.bamilo.android.appmodule.bamiloapp.helpers.configs.GetAvailableCountriesHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.database.BrandsTableHelper;
import com.bamilo.android.framework.service.database.CountriesConfigsTableHelper;
import com.bamilo.android.framework.service.database.LastViewedTableHelper;
import com.bamilo.android.framework.service.objects.configs.AvailableCountries;
import com.bamilo.android.framework.service.objects.configs.CountryObject;
import com.bamilo.android.framework.service.objects.configs.Languages;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Fragment used on SplashScreen on First Use
 * 
 * @author sergiopereira
 */
public class ChooseCountryFragment extends BaseFragment implements IResponseCallback, OnItemClickListener {

    private static final String TAG = ChooseCountryFragment.class.getSimpleName();

    private static final int SHOP_NOT_SELECTED = -1;

    private Context context;

    private int selected = SHOP_NOT_SELECTED;

    private CountryAdapter countryAdapter;

    private boolean isChangeCountry;

    private ListView mCountryListView;

    /**
     * Empty constructor
     */
    public ChooseCountryFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.COUNTRY,
                R.layout.change_country,
                ShopSelector.getShopId() != null ? R.string.nav_country : IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
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
        // List view
        mCountryListView = (ListView) view.findViewById(R.id.change_country_list);
        // Validate the current shop
        if(ShopSelector.getShopId() != null) {
            // Get and show new available countries
            isChangeCountry = true;
            triggerGetAvailableCountries();
            // Remove the JUMIA icon when is only for change country
            if (getBaseActivity().getSupportActionBar() != null)
                getBaseActivity().getSupportActionBar().setCustomView(null);

        } else {
            // Show available countries from memory/database, loaded in splash screen.
            isChangeCountry = false;
            showAvailableCountries();
            //
            if (getBaseActivity().getSupportActionBar() != null) {
                getBaseActivity().getSupportActionBar().setHomeButtonEnabled(false);
                getBaseActivity().getSupportActionBar().setLogo(null);
            }
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
        String countryIso = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ISO, null);
        String countryUrl = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_URL, null);
        // Validate data
        if(BamiloApplication.INSTANCE.countriesAvailable == null || BamiloApplication.INSTANCE.countriesAvailable.size() == 0){
            // Get countries from database
            BamiloApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
            // Validate data from database
            if(BamiloApplication.INSTANCE.countriesAvailable.size() == 0) {
                triggerGetAvailableCountries();
                return;   
            }
        } 
        
        int countriesAvailable = BamiloApplication.INSTANCE.countriesAvailable.size();
        Print.d(TAG, "COUNTRIES SIZE: " + countriesAvailable);
        
        int count = 0;
        String[] countries = new String[countriesAvailable];
        String[] flagsList = new String[countriesAvailable];
        for (CountryObject country : BamiloApplication.INSTANCE.countriesAvailable) {
            countries[count] = country.getCountryName();
            flagsList[count] = country.getCountryFlag();
            String iso = country.getCountryIso();
            String url = country.getCountryUrl();
            if(countryIso != null && countryIso.equalsIgnoreCase(iso) && TextUtils.equals(countryUrl, url)){
                selected = count;
            }
            count++;
        }
        
        // Inflate

        if(countryAdapter == null){
            countryAdapter = new CountryAdapter(context, countries, flagsList);
        }
        countryAdapter.updateValues(countries);
        mCountryListView.setAdapter(countryAdapter);
        mCountryListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        if (selected > SHOP_NOT_SELECTED) {
            mCountryListView.setItemChecked(selected, true);
        }

        // Listener
        mCountryListView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if(CollectionUtils.isNotEmpty(BamiloApplication.INSTANCE.countriesAvailable)
                && BamiloApplication.INSTANCE.countriesAvailable.size() > position) {
            ((ListView) parent).setItemChecked(position, true);

            final CountryObject countryObject = BamiloApplication.INSTANCE.countriesAvailable.get(position);
            final Languages languages = ChooseLanguageController.getCurrentLanguages(this.getActivity(), countryObject);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    isChangeCountry = true;
                    setCountry(position);
                }
            };

            // If the dialog didn't load means that has no more than one country
            if (!ChooseLanguageController.chooseLanguageDialog(this, languages, runnable)) {
                if (selected == SHOP_NOT_SELECTED) {
                    setCountry(countryObject, position);
                } else if (position != selected) {
                    isChangeCountry = true;
                    setCountry(countryObject, position);
                }
            }
        }
    }

    protected boolean setCountry(CountryObject countryObject, int position){
        CountryPersistentConfigs.eraseCountryPreferences(context);
        CountryPersistentConfigs.saveLanguages(context, countryObject.getLanguages());
        return setCountry(position);
    }

    /**
     * Save the selected country
     * @return true if changed successfully false otherwise
     */
    protected boolean setCountry(int position) {

        ArrayList<CountryObject> countriesAvailable = BamiloApplication.INSTANCE.countriesAvailable;

        if(position > -1 && position < countriesAvailable.size() &&     // Position validation
                countriesAvailable.get(position) != null) {             //Null pointer validation

            CountryObject country = BamiloApplication.INSTANCE.countriesAvailable.get(position);

            // Set new country
            SharedPreferences sharedPrefs = getBaseActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            CountryPersistentConfigs.save(editor, country);
            editor.putBoolean(Darwin.KEY_COUNTRY_CHANGED, isChangeCountry);
            /**
             * Save the Selected Country Configs
             * KEY_SELECTED_COUNTRY_ID will contain the Country ISO that will be use to identify the selected country al over the App.
             */
            //Print.i(TAG, "code1DarwinComponent : selected : " + country.getCountryName());
            editor.putBoolean(Darwin.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
            editor.apply();
            // Clean memory
            BamiloApplication.INSTANCE.cleanAllPreviousCountryValues();
            // Is changing country
            if (isChangeCountry) {
                LastViewedTableHelper.deleteAllLastViewed();
                BrandsTableHelper.clearBrands();
//                TrackerDelegator.trackShopChanged();
            }

            getBaseActivity().restartAppFlow();
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
    private void triggerGetAvailableCountries() {
        triggerContentEvent(new GetAvailableCountriesHelper(), null, this);
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
        triggerGetAvailableCountries();
    }
    
    /*
     * ############# RESPONSE ############# 
     */
    
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED SUCCESS EVENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get event type
        EventType eventType = baseResponse.getEventType();
        // Validate event type
        switch (eventType) {
            case GET_GLOBAL_CONFIGURATIONS:
                Print.d(TAG, "RECEIVED GET_GLOBAL_CONFIGURATIONS");
                // Get countries
                BamiloApplication.INSTANCE.countriesAvailable = (AvailableCountries) baseResponse.getContentData();
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
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED ERROR EVENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get event type and error type
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        if(super.handleErrorEvent(baseResponse)) return;
        
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


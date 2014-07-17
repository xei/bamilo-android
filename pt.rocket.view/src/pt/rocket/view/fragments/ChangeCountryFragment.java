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
import pt.rocket.framework.database.CountriesConfigsTableHelper;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.database.LastViewedTableHelper;
import pt.rocket.framework.objects.CountryObject;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class ChangeCountryFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ChangeCountryFragment.class);

    private static final int SHOP_NOT_SELECTED = -1;

    private static ChangeCountryFragment changeCountryFragment;

    private Context context;

    private DialogGenericFragment dialog;
    
    private int selected = SHOP_NOT_SELECTED;
    
    private CountryAdapter countryAdapter;
    
    private boolean isChangeCountry;

    /**
     * Get instance
     * @return
     */
    public static ChangeCountryFragment getInstance() {
        if (changeCountryFragment == null)
            changeCountryFragment = new ChangeCountryFragment();
        
        return changeCountryFragment;
    }

    /**
     * Empty constructor
     */
    public ChangeCountryFragment() {
        super(IS_NESTED_FRAGMENT, NavigationAction.Country, EnumSet.noneOf(MyMenuItem.class));
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
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        View view = inflater.inflate(R.layout.change_country, container, false);
        return view;
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
            getBaseActivity().hideTitle();
            getBaseActivity().getSupportActionBar().setHomeButtonEnabled(false);
        }
        
        if(selected != SHOP_NOT_SELECTED){
            getBaseActivity().hideTitle();
            getBaseActivity().setCheckoutHeader(R.string.nav_country);
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
    
    
    @Override
    public boolean allowBackPressed() {
        if( selected == SHOP_NOT_SELECTED ){
            ((BaseActivity) getActivity()).finish();
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
        
   
        int count = 0;
        countries = new String[JumiaApplication.INSTANCE.countriesAvailable.size()];
        flagsList = new String[JumiaApplication.INSTANCE.countriesAvailable.size()];
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
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
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
                            ((BaseActivity) getActivity()).onBackPressed();
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
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_REST_BASE, context.getString(R.string.jumia_global_api_version));
        editor.putBoolean(ConstantsSharedPrefs.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
        editor.commit();
        
        
        TrackerDelegator.trackShopchanged(getActivity().getApplicationContext());
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
}

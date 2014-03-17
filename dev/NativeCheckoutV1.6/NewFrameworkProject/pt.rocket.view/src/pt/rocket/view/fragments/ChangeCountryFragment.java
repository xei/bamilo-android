/**
 * 
 */
package pt.rocket.view.fragments;

import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.CountryAdapter;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.ChangeCountryFragmentActivity;
import pt.rocket.view.MainFragmentActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
        super(IS_NESTED_FRAGMENT, NavigationAction.Country);
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
        SharedPreferences sharedPrefs = context.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        selected = sharedPrefs.getInt(ChangeCountryFragmentActivity.KEY_COUNTRY, SHOP_NOT_SELECTED);
        isChangeCountry = true;
        if(selected == SHOP_NOT_SELECTED){
            isChangeCountry = false;
            ((BaseActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
        }
        setList();
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
        ((BaseActivity) getActivity()).showContentContainer(false);
        ((BaseActivity) getActivity()).hideTitle();
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
        String[] countries = getResources().getStringArray(R.array.country_names);
        TypedArray flags = getResources().obtainTypedArray(R.array.country_icons);

        // Inflate
        final ListView countryList = (ListView) getView().findViewById(R.id.change_country_list);
        if(countryAdapter == null){
            countryAdapter = new CountryAdapter(getActivity(), countries, flags);
        }
        
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
        HomeFragment.requestResponse = null;
        JumiaApplication.INSTANCE.currentCategories = null;
        JumiaApplication.INSTANCE.setCart(null);
        getBaseActivity().updateCartInfo();
        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
        
        System.gc();
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(ChangeCountryFragmentActivity.KEY_COUNTRY, position);
        editor.putBoolean(ChangeCountryFragmentActivity.KEY_COUNTRY_CHANGED, isChangeCountry);
        editor.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true);
        editor.commit();
        TrackerDelegator.trackShopchanged(getActivity().getApplicationContext());
        ActivitiesWorkFlow.splashActivityNewTask(getActivity());
        getActivity().finish();
    }

}

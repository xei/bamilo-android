package pt.rocket.view;

import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.CountryAdapter;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.JumiaApplication;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MyLayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.ActionBarSherlockNative;

import de.akquinet.android.androlog.Log;

/**
 * 
 * <p>
 * This class is responsible for validating the user as a registered user, and allow full access and
 * possibility to buy.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential. Written by josedourado, 30/06/2012.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author Hugo Matilla
 * 
 * @date 30/06/2012
 * 
 * @description
 * 
 */

public class ChangeCountryActivity extends SherlockActivity {

    private static final int SHOP_NOT_SELECTED = -1;

    private final String TAG = LogTagHelper.create(ChangeCountryActivity.class);

    public static String KEY_COUNTRY = "country";

    private Dialog dialog;

    private int selected = SHOP_NOT_SELECTED;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.change_country);
        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        selected = sharedPrefs.getInt(ChangeCountryActivity.KEY_COUNTRY, SHOP_NOT_SELECTED);
        setList();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setupActionBar() {
        ActionBarSherlock.unregisterImplementation(ActionBarSherlockNative.class);
        getSupportActionBar().setLogo(R.drawable.lazada_logo_ic);
    }

    private void setList() {

        // Data
        String[] countries = getResources().getStringArray(R.array.country_names);
        TypedArray flags = getResources().obtainTypedArray(R.array.country_icons);

        // Inflate
        final ListView countryList = (ListView) findViewById(R.id.change_country_list);
        CountryAdapter countryAdapter = new CountryAdapter(this, countries, flags);
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

        dialog = new DialogGeneric(this, true, true, false, getString(R.string.nav_country),
                getString(R.string.nav_country_warning), getString(R.string.cancel_label),
                getString(R.string.yes_label), new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        int id = v.getId();
                        if (id == R.id.button1) {
                            finish();
                        } else if (id == R.id.button2) {
                            setCountry(position);
                        }

                    }
                });
        dialog.show();
    }

    protected void setCountry(int position) {
        Log.i(TAG, "New Country array position: " + position);
        SharedPreferences sharedPrefs = getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(KEY_COUNTRY, position);
        editor.commit();
        // Darwin.initialize(DarwinMode.DEBUG, getApplicationContext(), R.drawable.no_image,
        // position);
        // LogOut.cleanAndRestart(activity);
        finish();
        ActivitiesWorkFlow.splashActivityNewTask(this);
    }

    /**
     * Creates the custom layout inflater
     * 
     * @return the inflater
     * 
     *         (non-Javadoc)
     * @see android.app.Activity#getLayoutInflater()
     */
    @Override
    public LayoutInflater getLayoutInflater() {
        return new MyLayoutInflater(this, getOrigInflater());
    }

    /**
     * Creates the custom layout inflater if requested via system service
     * 
     * @return the inflater if requested by name, otherwise the system service
     * 
     *         (non-Javadoc)
     * @see android.app.Activity#getSystemService(java.lang.String)
     */
    @Override
    public Object getSystemService(String name) {
        if (name.equals(LAYOUT_INFLATER_SERVICE)) {
            return new MyLayoutInflater(this, getOrigInflater());
        }

        return super.getSystemService(name);
    }

    /**
     * Creates the original layout inflater
     * 
     * @return the original layout inflater
     */
    private LayoutInflater getOrigInflater() {
        return (LayoutInflater) super.getSystemService(LAYOUT_INFLATER_SERVICE);

    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

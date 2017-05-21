package com.mobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.mobile.app.BamiloApplication;
import com.mobile.service.Darwin;
import com.mobile.service.objects.configs.CountryObject;
import com.mobile.service.objects.configs.Languages;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.TextUtils;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.dialogfragments.DialogLanguagesListAdapter;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.view.R;

import java.util.Locale;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/08/27
 *
 */
public class ChooseLanguageController {

    /**
     * Loads Language chooser dialog.
     */
    public static void loadLanguageDialog(final Fragment fragment, final Languages languages, final Runnable runnable){
        DialogLanguagesListAdapter languagesListAdapter = new DialogLanguagesListAdapter(fragment.getActivity(), languages);
        final int defaultPosition = languages.getSelectedPosition();
        DialogListFragment.newInstance(fragment, new DialogListFragment.OnDialogListListener() {
            @Override
            public void onDialogListItemSelect(int position, String value) {
                if(defaultPosition != position) {
                    languages.setSelected(position);
                    CountryPersistentConfigs.saveLanguages(BamiloApplication.INSTANCE.getApplicationContext(), languages);
                    BamiloApplication.INSTANCE.cleanAllPreviousLanguageValues();
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }

            @Override
            public void onDismiss() {

            }
        }, fragment.getString(R.string.choose_language), languagesListAdapter, defaultPosition).show(fragment.getChildFragmentManager(), null);
    }

    /**
     * Loads chooser dialog in case languages have more than one element.
     * @return True if dialog is loaded correctly. False otherwise.
     */
    public static boolean chooseLanguageDialog(final Fragment fragment, final Languages languages, final Runnable runnable){
        if(!CollectionUtils.isEmpty(languages) && languages.size() > 1) {
            loadLanguageDialog(fragment,languages,runnable);
            return true;
        }
        return false;
    }

    /**
     * Get languages from preferences. If user does not have any yet, default from countryObject is returned.
     * @return The current user preferences.
     */
    public static Languages getCurrentLanguages(Context context, CountryObject country){
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        Languages languages = null;
        // Get saved country info
        String countryIso = settings.getString(Darwin.KEY_SELECTED_COUNTRY_ISO, "");
        String countryUrl = settings.getString(Darwin.KEY_SELECTED_COUNTRY_URL, "");
        // Get current country info
        String iso = country.getCountryIso();
        String url = country.getCountryUrl();
        // Get languages
        if(TextUtils.equalsIgnoreCase(iso, countryIso) && TextUtils.equals(countryUrl, url)) {
            languages = CountryPersistentConfigs.getLanguages(settings);
        }
        // Return languages
        return languages != null ? languages : country.getLanguages();
    }

    /**
     * Set selected language based on current device's language. If there isn't any language matching,
     * default is selected.
     */
    public static void setLanguageBasedOnDevice(Languages languages, String countryCode){
        if(!languages.setSelected(Locale.getDefault().getLanguage()+"_"+countryCode)){
            languages.setDefaultAsSelected();
        }
    }

}

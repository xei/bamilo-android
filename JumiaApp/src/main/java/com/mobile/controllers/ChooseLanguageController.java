package com.mobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.objects.configs.CountryObject;
import com.mobile.newFramework.objects.configs.Languages;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
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
 * @see <a href="http://tutorials.jenkov.com/java-concurrency/volatile.html">volatile</a>
 */
public class ChooseLanguageController {

    /**
     * Loads Language chooser dialog.
     *
     * @param fragment
     * @param languages
     * @param runnable
     */
    public static void loadLanguageDialog(final Fragment fragment, final Languages languages, final Runnable runnable){
        DialogLanguagesListAdapter languagesListAdapter = new DialogLanguagesListAdapter(fragment.getActivity(), languages);
        final int defaultPosition = languages.getSelectedPosition();
        DialogListFragment.newInstance(fragment, new DialogListFragment.OnDialogListListener() {
            @Override
            public void onDialogListItemSelect(int position, String value) {
                if(defaultPosition != position) {
                    languages.setSelected(position);
                    CountryPersistentConfigs.saveLanguages(fragment.getActivity(), languages);
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }

            @Override
            public void onDismiss() {

            }
        }, null, fragment.getString(R.string.choose_language), languagesListAdapter, defaultPosition).show(fragment.getChildFragmentManager(), null);
    }

    /**
     * Loads chooser dialog in case languages have more than one element.
     *
     * @param fragment
     * @param languages
     * @param runnable
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
     *
     * @param context
     * @param countryObject
     * @return The current user preferences.
     */
    public static Languages getCurrentLanguages(Context context, CountryObject countryObject){
        SharedPreferences settings = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);

        Languages languages = null;
        if(countryObject.getCountryIso().toLowerCase().equals(settings.getString(Darwin.KEY_SELECTED_COUNTRY_ISO, ""))) {
            languages = CountryPersistentConfigs.getLanguages(settings);
        }

        if(languages == null){
            languages = countryObject.getLanguages();
        }
        return languages;
    }

    /**
     * Set selected language based on current device's language. If there isn't any language matching,
     * default is selected.
     *
     * @param languages
     * @param countryCode
     */
    public static void setLanguageBasedOnDevice(Languages languages, String countryCode){
        if(!languages.setSelected(Locale.getDefault().getLanguage()+"_"+countryCode)){
            languages.setDefaultAsSelected();
        }
    }

}

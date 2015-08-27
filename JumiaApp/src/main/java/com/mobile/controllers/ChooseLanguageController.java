package com.mobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.objects.configs.CountryObject;
import com.mobile.newFramework.objects.configs.Languages;
import com.mobile.newFramework.utils.Constants;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.dialogfragments.DialogLanguagesListAdapter;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.view.R;

/**
 * Created by rsoares on 8/27/15.
 */
public class ChooseLanguageController {
    public static boolean chooseLanguageDialog(final Fragment fragment, final Languages languages, final Runnable runnable){
        if(languages.size() > 1) {
            DialogLanguagesListAdapter languagesListAdapter = new DialogLanguagesListAdapter(fragment.getActivity(), languages);
            DialogListFragment.newInstance(fragment, new DialogListFragment.OnDialogListListener() {
                @Override
                public void onDialogListItemSelect(int position, String value) {
                    languages.setSelected(position);
                    CountryPersistentConfigs.saveLanguages(fragment.getActivity(), languages);
                    if(runnable != null) {
                        runnable.run();
                    }
                }

                @Override
                public void onDismiss() {

                }
            }, "choose_language", fragment.getString(R.string.choose_language), languagesListAdapter, languages.getSelectedPosition()).show(fragment.getChildFragmentManager(), null);
            return true;
        }
        return false;
    }

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

}

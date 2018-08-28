package com.bamilo.android.framework.service.objects.configs;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/08/26
 */
public class Languages extends ArrayList<Language> implements IJSONSerializable {

    /**
     * Empty Constructor
     */
    public Languages() {
        // ...
    }

    public Languages(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get languages
        JSONArray languages = jsonObject.getJSONArray(RestConstants.LANGUAGES);
        for (int i = 0; i < languages.length(); i++) {
            Language language = new Language();
            language.setLangCode(languages.getJSONObject(i).getString(RestConstants.CODE));
            language.setLangName(languages.getJSONObject(i).getString(RestConstants.NAME));
            language.setIsDefault(languages.getJSONObject(i).getBoolean(RestConstants.DEFAULT));
            this.add(language);
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

//    /**
//     * Get default Language object.
//     * @return
//     */
//    public Language getDefaultLanguage(){
//        for(Language language : this){
//            if(language.isDefault()){
//                return language;
//            }
//        }
//        return null;
//    }

    /**
     * Get selected language object or default.
     */
    public Language getSelectedLanguage() {
        Language languageSelected = null;
        Language languageDefault = null;
        for (Language language : this) {
            if (language.isSelected()) {
                languageSelected = language;
                break;
            }
            if (language.isDefault()) {
                languageDefault = language;
            }
        }

        return languageSelected != null ? languageSelected : languageDefault;
    }

    /**
     * Set language selected based on position.
     */
    public void setSelected(int position) {
        for (int i = 0; i < this.size(); i++) {
            Language language = this.get(i);
            language.setIsSelected(position == i);
        }
    }

    /**
     * Set language selected based on code.
     */
    public boolean setSelected(String code) {
        boolean found = false;
        for (int i = 0; i < this.size(); i++) {
            Language language = this.get(i);
            if (!found && language.getLangCode().equals(code)) {
                language.setIsSelected(true);
                found = true;
            } else {
                language.setIsSelected(false);
            }
        }
        return found;
    }

    /**
     * Get all Language names.
     *
     * @return Array with all language names.
     */
    public ArrayList<String> getLanguageNames() {
        ArrayList<String> messages = new ArrayList<>();
        for (Language language : this) {
            messages.add(language.getLangName());
        }
        return messages;
    }

    /**
     * Get selected language position.
     *
     * @return Selected language position or -1 if not found.
     */
    public int getSelectedPosition() {
        for (int i = 0; i < this.size(); i++) {
            Language language = this.get(i);
            if (language.isSelected()) {
                return i;
            }
        }
        return -1;
    }

//    /**
//     * Get default language position.
//     */
//    public int getDefaultPosition() {
//        for (int i = 0; i < this.size(); i++) {
//            Language language = this.get(i);
//            if (language.isDefault()) {
//                return i;
//            }
//        }
//        return -1;
//    }

    /**
     * Set default language object as selected.
     */
    public void setDefaultAsSelected() {
        for (int i = 0; i < this.size(); i++) {
            Language language = this.get(i);
            language.setIsSelected(language.isDefault());
        }
    }
}

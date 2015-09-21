package com.mobile.newFramework.objects.configs;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/08/26
 *
 */
public class Languages extends ArrayList<Language> implements IJSONSerializable{

    public Languages(){}

    public Languages(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get languages
        JSONArray languages = jsonObject.getJSONArray(RestConstants.JSON_COUNTRY_LANGUAGES);
        for (int i = 0; i < languages.length(); i++) {
            Language language = new Language();
            language.setLangCode(languages.getJSONObject(i).getString(RestConstants.JSON_CODE_TAG));
            language.setLangName(languages.getJSONObject(i).getString(RestConstants.JSON_NAME_TAG));
            language.setIsDefault(languages.getJSONObject(i).getBoolean(RestConstants.JSON_COUNTRY_LANG_DEFAULT));
            this.add(language);

        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    /**
     * Get default Language object.
     * @return
     */
    public Language getDefaultLanguage(){
        for(Language language : this){
            if(language.isDefault()){
                return language;
            }
        }
        return null;
    }

    /**
     * Get selected language object or default.
     * @return
     */
    public Language getSelectedLanguage(){
        Language languageSelected = null;
        Language languageDefault = null;
        for(Language language : this){
            if(language.isSelected()){
                languageSelected = language;
                break;
            }
            if(language.isDefault()){
                languageDefault = language;
            }
        }

        return languageSelected != null ? languageSelected : languageDefault;
    }

    /**
     * Set language selected based on position.
     * @param position
     */
    public void setSelected(int position){
        for(int i = 0; i<this.size();i++){
            Language language = this.get(i);
            language.setIsSelected(position == i);
        }
    }

    /**
     * Set language selected based on code.
     * @param code
     * @return
     */
    public boolean setSelected(String code){
        boolean found = false;
        for(int i = 0; i<this.size();i++){
            Language language = this.get(i);
            if(!found && language.getLangCode().equals(code)){
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
     * @return Array with all language names.
     */
    public ArrayList<String> getLanguageNames(){
        ArrayList<String> messages = new ArrayList<>();
        for(Language language : this){
            messages.add(language.getLangName());
        }
        return messages;
    }

    /**
     * Get selected language position.
     * @return Selected language position or -1 if not found.
     */
    public int getSelectedPosition(){
        for(int i = 0; i<this.size();i++){
            Language language = this.get(i);
            if(language.isSelected()){
                return i;
            }
        }
        return -1;
    }

    /**
     * Get default language position.
     * @return
     */
    public int getDefaultPosition(){
        for(int i = 0; i<this.size();i++){
            Language language = this.get(i);
            if(language.isDefault()){
                return i;
            }
        }
        return -1;
    }

    /**
     * Set default language object as selected.
     */
    public void setDefaultAsSelected(){
        for(int i = 0; i<this.size();i++){
            Language language = this.get(i);
            language.setIsSelected(language.isDefault());
        }
    }
}

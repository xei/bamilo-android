package com.mobile.newFramework.objects.configs;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rsoares on 8/26/15.
 */
public class Languages extends ArrayList<Language> implements IJSONSerializable{

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
    public Language getDefaultLanguage(){
        for(Language language : this){
            if(language.isDefault()){
                return language;
            }
        }
        return null;
    }

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
}

package com.mobile.newFramework.objects.configs;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rsoares on 5/21/15.
 */
public class AvailableCountries extends ArrayList<CountryObject> implements IJSONSerializable {

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONObject bamiloObject = new JSONObject();
        JSONArray data = new JSONArray();
        JSONObject jsonData = new JSONObject();
        jsonData.put("name","staging sosis");
        jsonData.put("url","staging.sosis.center/mobapi/");
        jsonData.put("flag","http://img.freeflagicons.com/thumb/round_icon/iran/iran_256.png");
        jsonData.put("user_agent","M_IRAMZ");
        jsonData.put("country_iso","IR");
        jsonData.put("force_https",false);
        JSONArray languages = new JSONArray();
        JSONObject lang_data = new JSONObject();
        lang_data.put("code","fa_IR");
        lang_data.put("name","fa_IR");
        lang_data.put("default",true);
        languages.put(lang_data);
        jsonData.put("languages",languages);
        data.put(jsonData);
        bamiloObject.put("md5","98bed71d981997b8986a728f69edd509");
        bamiloObject.put("data",data);

        JSONArray sessionJSONArray = null;
        if (null != bamiloObject) {
            sessionJSONArray = bamiloObject.optJSONArray(RestConstants.DATA);
        }

        if(sessionJSONArray != null){
            for (int i = 0; i < sessionJSONArray.length(); i++) {
                CountryObject mCountryObject = new CountryObject();
                try {
                    mCountryObject.initialize(sessionJSONArray.getJSONObject(i));
                    this.add(mCountryObject);
                } catch (JSONException e) {
//                    Log.w(TAG, "WARNING JSON EXCEPTION ON PARSE COUNTRIES", e);
                }
            }
        }

        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }
}

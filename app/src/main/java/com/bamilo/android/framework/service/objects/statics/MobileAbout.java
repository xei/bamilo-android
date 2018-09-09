package com.bamilo.android.framework.service.objects.statics;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rsoares on 10/14/15.
 */
public class MobileAbout extends ArrayList<TargetHelper> implements IJSONSerializable {

    public MobileAbout(){
        super();
    }

    public MobileAbout(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONArray mobileAbout = jsonObject.getJSONArray(RestConstants.MOBILE_ABOUT);
        for(int i = 0; i < mobileAbout.length() ; i++){
            try{
                this.add(new TargetHelper(mobileAbout.getJSONObject(i)));
            } catch (JSONException ex){
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

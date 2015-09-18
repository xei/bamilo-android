package com.mobile.newFramework.objects.configs;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 5/27/15.
 */
public class ApiInformation implements IJSONSerializable {
    private Sections sections;
    private VersionInfo versionInfo;

    public ApiInformation(){
        sections = new Sections();
        versionInfo = new VersionInfo();
    }
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        sections.initialize(jsonObject);
        versionInfo.initialize(jsonObject);
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

    public Sections getSections() {
        return sections;
    }

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }
}

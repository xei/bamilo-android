package com.mobile.newFramework.objects;

import com.mobile.framework.objects.Section;
import com.mobile.framework.rest.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by rsoares on 5/25/15.
 */
public class Sections extends LinkedList<Section> implements  IJSONSerializable{

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONArray sectionsJSONArray = jsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);
        for (int i = 0; i < sectionsJSONArray.length(); ++i) {
            JSONObject sessionObject = sectionsJSONArray.optJSONObject(i);
            Section section = new Section();
            section.initialize(sessionObject);
            this.add(section);
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }
}

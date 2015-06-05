package com.mobile.newFramework.objects.search;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Class used to represent a search suggestion
 * @author sergiopereira
 *
 */
public class Suggestions extends ArrayList<Suggestion> implements IJSONSerializable {

	public final static String TAG = Suggestions.class.getSimpleName();

	@Override
	public RequiredJson getRequiredJson() {
		return RequiredJson.METADATA;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		try {
			JSONArray suggestionsArray = jsonObject.getJSONArray(RestConstants.JSON_SUGGESTIONS_TAG);
            for (int i = 0; i < suggestionsArray.length(); ++i) {
                Suggestion suggestion = new Suggestion();
                suggestion.initialize(suggestionsArray.getJSONObject(i));
                this.add(suggestion);
            }
		} catch (JSONException e) {
			//Log.d(TAG, "error parsing json: ", e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		return null;
	}

}

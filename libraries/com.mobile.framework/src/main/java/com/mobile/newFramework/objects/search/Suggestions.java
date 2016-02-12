package com.mobile.newFramework.objects.search;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Class used to represent a search suggestion
 * @author sergiopereira
 *
 */
public class Suggestions extends ArrayList<Suggestion> implements IJSONSerializable {

	public final static String TAG = Suggestions.class.getSimpleName();

	private static final int FIRST_POSITION = 0;
	private static final int SECOND_POSITION = 1;

	public Suggestions(){}

	public Suggestions(List<Suggestion> suggestions){
		setSuggestions(suggestions);
	}

	public Suggestions(Suggestions suggestions){
		this((List)suggestions);
	}

	public void setSuggestions(List<Suggestion> suggestions){
		for(Suggestion suggestion : suggestions){
			// Order categories after ShopInShop and before Products
			if(suggestion.getType() == Suggestion.SUGGESTION_CATEGORY && size() > FIRST_POSITION && get(FIRST_POSITION).getType() == Suggestion.SUGGESTION_SHOP_IN_SHOP){
				add(SECOND_POSITION,suggestion);
			} else if(suggestion.getType() == Suggestion.SUGGESTION_CATEGORY && size() > FIRST_POSITION){
				add(FIRST_POSITION,suggestion);
			} else {
				add(suggestion);
			}

		}
	}
	@Override
	public int getRequiredJson() {
		return RequiredJson.METADATA;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		try {
			JSONArray suggestionsArray = jsonObject.getJSONArray(RestConstants.SUGGESTIONS);
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

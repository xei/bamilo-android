package com.mobile.service.objects.search;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;

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

	public Suggestions(){}

	public Suggestions(List<Suggestion> suggestions){
		setSuggestions(suggestions);
	}

	public Suggestions(Suggestions suggestions){
		this((List)suggestions);
	}

	public void setSuggestions(List<Suggestion> suggestions){
		int position = FIRST_POSITION;
		for(Suggestion suggestion : suggestions){
			// Order categories after ShopInShop and before Products
			if(suggestion.getType() == Suggestion.SUGGESTION_CATEGORY && size() > FIRST_POSITION && get(FIRST_POSITION).getType() == Suggestion.SUGGESTION_SHOP_IN_SHOP){
                position += 1;
                add(position,suggestion);
			} else if(suggestion.getType() == Suggestion.SUGGESTION_CATEGORY && size() > FIRST_POSITION){
				add(position,suggestion);
                position += 1;
			} else {
				add(suggestion);
			}

		}
	}

    /**
     * Keep the order of the suggestions retrieved by database recently searched.
     * @param suggestions
     */
    public void setRecentSuggestions(List<Suggestion> suggestions){
		for(Suggestion suggestion : suggestions){
            add(suggestion);
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
            String query = jsonObject.getString(RestConstants.SUGGESTION_QUERY);

			if(jsonObject.has(RestConstants.SHOPS)){
				JSONArray shops = jsonObject.optJSONArray(RestConstants.SHOPS);
				for (int i = 0; i < shops.length(); i++) {
					Suggestion suggestion = new Suggestion();
					suggestion.initialize(shops.getJSONObject(i));
					suggestion.setQuery(query);
                    suggestion.setType(Suggestion.SUGGESTION_SHOP_IN_SHOP);
					this.add(suggestion);
				}
			}


			if(jsonObject.has(RestConstants.CATEGORIES)){
				JSONArray categories = jsonObject.optJSONArray(RestConstants.CATEGORIES);
				for (int i = 0; i < categories.length(); i++) {
					Suggestion suggestion = new Suggestion();
					suggestion.initialize(categories.getJSONObject(i));
					suggestion.setQuery(query);
                    suggestion.setType(Suggestion.SUGGESTION_CATEGORY);
					this.add(suggestion);
				}
			}

			if(jsonObject.has(RestConstants.PRODUCTS)){
				JSONArray products = jsonObject.optJSONArray(RestConstants.PRODUCTS);
				if(products != null){
					for (int i = 0; i < products.length(); i++) {
						Suggestion suggestion = new Suggestion();
						suggestion.initialize(products.getJSONObject(i));
                        suggestion.setQuery(query);
                        suggestion.setType(Suggestion.SUGGESTION_PRODUCT);
						this.add(suggestion);
					}
				}
			}

            if(jsonObject.has(RestConstants.QUERIES)){
                JSONArray queries = jsonObject.optJSONArray(RestConstants.QUERIES);
                for (int i = 0; i < queries.length(); i++) {
                    Suggestion suggestion = new Suggestion();
                    suggestion.initialize(queries.getJSONObject(i));
                    suggestion.setQuery(query);
                    suggestion.setType(Suggestion.SUGGESTION_OTHER);
                    this.add(suggestion);
                }
            }

            return true;
		} catch (JSONException e) {
			e.printStackTrace();
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

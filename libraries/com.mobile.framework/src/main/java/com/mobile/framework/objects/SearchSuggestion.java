package com.mobile.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mobile.framework.rest.RestConstants;

import de.akquinet.android.androlog.Log;

/**
 * Class used to represent a search suggestion
 * @author sergiopereira
 *
 */
public class SearchSuggestion implements IJSONSerializable, Parcelable {
	
	private final static String TAG = SearchSuggestion.class.getSimpleName();
	
	private String result;
	
	private int value;
	
	private boolean recentQuery = false;
	
	/**
	 * Empty constructor
	 */
	public SearchSuggestion() { }
	
	/*
	 * (non-Javadoc)
	 * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		try {
			if (jsonObject != null) {
				String item = jsonObject.getString(RestConstants.JSON_ITEM_TAG);
				if (!TextUtils.isEmpty(item)) {
					result = item;

					value = jsonObject.getInt(RestConstants.JSON_RELEVANCE_TAG);

					return true;
				}
			}
		} catch (JSONException e) {
			Log.d(TAG, "error parsing json: ", e);
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
	
	/**
	 * Get the suggestion
	 * @return string
	 */
	public String getResult() {
		return result;
	}
	
	/**
	 * 
	 * @return 
	 */
	public int getResultValue() {
		return value;
	}
	
	/**
	 * Set the suggestion string
	 * @param recentSuggestion
	 * @author sergiopereira
	 */
	public void setResult(String recentSuggestion) {
		this.result = recentSuggestion;
	}

	/**
	 * Set the suggestion as recent
	 * @param recent
	 * @author sergiopereira
	 */
	public void setIsRecentSearch(boolean recent){
		this.recentQuery = recent;
	}
	
	/**
	 * Is a recent query
	 * @return true or false
	 * @author sergiopereira
	 */
	public boolean isRecentQuery() {
		return recentQuery;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(result);
		dest.writeInt(value);
	}

	private SearchSuggestion(Parcel in) {
		result = in.readString();
		value = in.readInt();
	}
	
    public static final Parcelable.Creator<SearchSuggestion> CREATOR = new Parcelable.Creator<SearchSuggestion>() {
        public SearchSuggestion createFromParcel(Parcel in) {
            return new SearchSuggestion(in);
        }

        public SearchSuggestion[] newArray(int size) {
            return new SearchSuggestion[size];
        }
    };
	
}
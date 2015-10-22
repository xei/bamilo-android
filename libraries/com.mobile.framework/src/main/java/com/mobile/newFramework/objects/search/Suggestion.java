package com.mobile.newFramework.objects.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;



/**
 * Class used to represent a search suggestion
 * @author sergiopereira
 *
 */
public class Suggestion implements IJSONSerializable, Parcelable {

	public final static String TAG = Suggestion.class.getSimpleName();

	private String result;

	private int value;

	private boolean recentQuery = false;

	/**
	 * Empty constructor
	 */
	public Suggestion() { }

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

	@Override
	public int getRequiredJson() {
		return RequiredJson.NONE;
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

	private Suggestion(Parcel in) {
		result = in.readString();
		value = in.readInt();
	}

    public static final Creator<Suggestion> CREATOR = new Creator<Suggestion>() {
        public Suggestion createFromParcel(Parcel in) {
            return new Suggestion(in);
        }

        public Suggestion[] newArray(int size) {
            return new Suggestion[size];
        }
    };

}

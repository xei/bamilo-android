package com.mobile.newFramework.objects.search;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Class used to represent a search suggestion
 * @author sergiopereira
 *
 */
public class Suggestion implements IJSONSerializable, Parcelable {

    public static final int SUGGESTION_PRODUCT = 0; //
    public static final int SUGGESTION_SHOP_IN_SHOP = 1; //
    public static final int SUGGESTION_CATEGORY = 2; //
    @IntDef({SUGGESTION_PRODUCT, SUGGESTION_SHOP_IN_SHOP, SUGGESTION_CATEGORY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SuggestionType {}

	public final static String TAG = Suggestion.class.getSimpleName();

	private int mType;

	private String mQuery;

	private String mResult;

	private String mTarget;

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
				String item = jsonObject.getString(RestConstants.ITEM);
				if (!TextUtils.isEmpty(item)) {
					mResult = item;
					value = jsonObject.getInt(RestConstants.RELEVANCE);
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
		return mResult;
	}

	public String getQuery() {
		return mQuery;
	}

    public String getTarget() {
		return mTarget;
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		mType = type;
	}

	/**
	 * Set the suggestion string
	 */
	public void setResult(String recentSuggestion) {
		this.mResult = recentSuggestion;
	}
	public void setQuery(String query) {
		this.mQuery = query;
	}


    public void setTarget(String target) {
		this.mTarget = target;
	}

	/**
	 * Set the suggestion as recent
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
		dest.writeString(mResult);
		dest.writeInt(value);
		dest.writeInt(mType);
	}

	private Suggestion(Parcel in) {
		mResult = in.readString();
		value = in.readInt();
		mType = in.readInt();
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

package com.mobile.newFramework.objects.catalog;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents a category filter for catalog.
 * 
 * @author sergiopereira
 * 
 */
public class CategoryFilterOption extends CatalogFilterOption implements IJSONSerializable {
	
	protected final static String TAG = CategoryFilterOption.class.getSimpleName();
	
	private String mUrl;

	private int mNumber;

	/**
	 * @throws JSONException
	 */
	public CategoryFilterOption(JSONObject jsonObject) throws JSONException {
		super(jsonObject);
	   	// Get category data
		setId(jsonObject.optString(RestConstants.ID));
		setValue(jsonObject.optString(RestConstants.JSON_NAME_TAG));
		setLabel(jsonObject.optString(RestConstants.JSON_NAME_TAG));
		mUrl = jsonObject.optString(RestConstants.URL);
    	mNumber = jsonObject.optInt(RestConstants.JSON_NUMBER_TAG);
	}
    
    /*
     * ########### GETTERS ########### 
     */

	/**
	 * @return the mUrl
	 */
	public String getUrl() {
		return mUrl;
	}

	/**
	 * @return the mNumber
	 */
	public int getNumber() {
		return mNumber;
	}

	/*
     * ########### SETTERS ########### 
     */

	/**
	 * @param url the mUrl to set
	 */
	public void setUrl(String url) {
		this.mUrl = url;
	}

	/**
	 * @param number the mNumber to set
	 */
	public void setNumber(int number) {
		this.mNumber = number;
	}

    
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
    
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
	    super.writeToParcel(dest, flags);
	    dest.writeString(mUrl);
	    dest.writeInt(mNumber);
	}
	
	/**
	 * Parcel constructor
	 */
	private CategoryFilterOption(Parcel in) {
	    super(in);
        mUrl = in.readString();
        mNumber = in.readInt();
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<CategoryFilterOption> CREATOR = new Parcelable.Creator<CategoryFilterOption>() {
        public CategoryFilterOption createFromParcel(Parcel in) {
            return new CategoryFilterOption(in);
        }

        public CategoryFilterOption[] newArray(int size) {
            return new CategoryFilterOption[size];
        }
    };
	
	
}

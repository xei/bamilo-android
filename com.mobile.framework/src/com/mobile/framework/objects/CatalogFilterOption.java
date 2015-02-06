package com.mobile.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class used to represnt a filter option for catalog
 * 
 * @author sergiopereira
 */
public class CatalogFilterOption implements IJSONSerializable, Parcelable {

    private String mId = "";

    private String mLabel = "";

    private String mValue = "";

    private String mCount = "";

    private String mHex = "";

    private String mImg = "";

    private int mMax = -1;

    private int mMin = -1;

    private int mInterval = 0;

    private boolean isSelected;

    private boolean isSectionBrand;

    public CatalogFilterOption(JSONObject jsonObject) throws JSONException {
        initialize(jsonObject);
    }

    public CatalogFilterOption() {
        isSectionBrand = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonOption) throws JSONException {
        // Get id
        mId = jsonOption.optString(RestConstants.JSON_ID_TAG, "");
        // Get label
        mLabel = jsonOption.optString(RestConstants.JSON_LABEL_TAG, "");
        // Get value
        mValue = jsonOption.optString(RestConstants.JSON_VAL_TAG, "");
        // Get products count
        mCount = jsonOption.optString(RestConstants.JSON_PRODUCTS_COUNT_TAG, "");
        // Get hex value
        mHex = jsonOption.optString(RestConstants.JSON_HEX_VALUE_TAG, "");
        // Get image url
        mImg = jsonOption.optString(RestConstants.JSON_IMAGE_URL_TAG, "");
        // Get max
        mMax = jsonOption.optInt(RestConstants.JSON_MAX_TAG);
        // Get min
        mMin = jsonOption.optInt(RestConstants.JSON_MIN_TAG);
        // Get interval
        mInterval = jsonOption.optInt(RestConstants.JSON_INTERVAL_TAG);
        // Set selected
        isSelected = jsonOption.optBoolean(RestConstants.JSON_IS_SELECTED_TAG, false);
        // Set
        isSectionBrand = jsonOption.optBoolean(RestConstants.JSON_IS_SECTION_BRAND_TAG, false);

        return true;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonOptions = new JSONObject();

        try {
            // Get id
            jsonOptions.put(RestConstants.JSON_ID_TAG, mId);
            // Get label
            jsonOptions.put(RestConstants.JSON_LABEL_TAG, mLabel);
            // Get value
            jsonOptions.put(RestConstants.JSON_VAL_TAG, mValue);
            // Get products count
            jsonOptions.put(RestConstants.JSON_PRODUCTS_COUNT_TAG, mCount);
            // Get hex value
            jsonOptions.put(RestConstants.JSON_HEX_VALUE_TAG, mHex);
            // Get image url
            jsonOptions.put(RestConstants.JSON_IMAGE_URL_TAG, mImg);
            // Get max
            jsonOptions.put(RestConstants.JSON_MAX_TAG, mMax);
            // Get min
            jsonOptions.put(RestConstants.JSON_MIN_TAG, mMin);
            // Get interval
            jsonOptions.put(RestConstants.JSON_INTERVAL_TAG, mInterval);

            jsonOptions.put(RestConstants.JSON_IS_SELECTED_TAG, isSelected);
            // Set
            jsonOptions.put(RestConstants.JSON_IS_SECTION_BRAND_TAG, isSectionBrand);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonOptions;
    }

    /**
     * ########### GETTERS ###########
     */

    public String getId() {
        return mId;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getValue() {
        return mValue;
    }

    public String getCount() {
        return mCount;
    }

    public String getHex() {
        return mHex;
    }

    public String getImg() {
        return mImg;
    }

    public int getMax() {
        return mMax;
    }

    public int getMin() {
        return mMin;
    }

    public int getInterval() {
        return mInterval;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isSectionItem() {
        return isSectionBrand;
    }

    /**
     * ########### SETTERS ###########
     */

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    public void setValue(String mValue) {
        this.mValue = mValue;
    }

    public void setCount(String mCount) {
        this.mCount = mCount;
    }

    public void setHex(String mHex) {
        this.mHex = mHex;
    }

    public void setImg(String mImg) {
        this.mImg = mImg;
    }

    public void setMax(int mMax) {
        this.mMax = mMax;
    }

    public void setMin(int mMin) {
        this.mMin = mMin;
    }

    public void setInterval(int mInterval) {
        this.mInterval = mInterval;
    }

    public void setSelected(Boolean bool) {
        this.isSelected = bool;
    }

    public void setSectionBrand(Boolean bool) {
        this.isSectionBrand = bool;
    }
    
    @Override
    public String toString() {
        return "" + mId + "; " + mLabel + "; " + mValue + "; " + mCount + "; " + mHex + "; " + mImg + "; " + mMax + "; " + mMin + "; " + mInterval + "; "
                + isSelected + "; " + isSectionBrand;
    };
    
    /**
     * ############### Parcelable ###############
     */

    /*
     * (non-Javadoc)
     * 
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String json = toJSON().toString();
        dest.writeString(json);
    }

    /**
     * Constructor with parcel
     * 
     * @param in
     */
    protected CatalogFilterOption(Parcel in) {
        String json = in.readString();
        try {
            initialize(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * The creator
     */
    public static final Parcelable.Creator<CatalogFilterOption> CREATOR = new Parcelable.Creator<CatalogFilterOption>() {
        public CatalogFilterOption createFromParcel(Parcel in) {
            return new CatalogFilterOption(in);
        }

        public CatalogFilterOption[] newArray(int size) {
            return new CatalogFilterOption[size];
        }
    };

}
package com.mobile.newFramework.objects.product.pojo;

import android.os.Parcel;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents a product inside a Rich Relevance response
 */
public class ProductRichRelevance extends ProductRegular {

    private String mRichRelevanceClickHash;
    /**
     * Empty constructor
     */
    public ProductRichRelevance() {
        super();
    }

    /*
     * ############ IJSONSerializable ############
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Mandatory
        super.initialize(jsonObject);

        return initializeProductRichRelevance(jsonObject);
    }

    protected final boolean initializeProductRichRelevance(JSONObject jsonObject) throws JSONException {
        // Title
        mName = jsonObject.getString(RestConstants.TITLE);
        // Click Request
        mRichRelevanceClickHash = jsonObject.optString(RestConstants.CLICK_REQUEST);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    /*
     * ############ GETTERS ############
	 */

    public String getName() {
        return mName;
    }

    public String getRichRelevanceClickHash() {
        return mRichRelevanceClickHash;
    }

    /*
	 * ############ PARCELABLE ############
	 */

    protected ProductRichRelevance(Parcel in) {
        super(in);
        mName = in.readString();
        mRichRelevanceClickHash = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mName);
        dest.writeString(mRichRelevanceClickHash);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("unused")
    public static final Creator<ProductRichRelevance> CREATOR = new Creator<ProductRichRelevance>() {
        @Override
        public ProductRichRelevance createFromParcel(Parcel in) {
            return new ProductRichRelevance(in);
        }

        @Override
        public ProductRichRelevance[] newArray(int size) {
            return new ProductRichRelevance[size];
        }
    };

}

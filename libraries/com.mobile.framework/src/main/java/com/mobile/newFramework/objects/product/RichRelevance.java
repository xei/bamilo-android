package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.objects.product.pojo.ProductRichRelevance;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents the all Rich relevance object response
 */
public class RichRelevance implements IJSONSerializable, Parcelable {

    private String mType;
    private boolean mHasData;
    private String mTitle;
    private ArrayList<ProductRegular> mRichRelevanceProducts = new ArrayList<>();

    /**
     * Empty constructor
     */
    public RichRelevance() {
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get rich relevance
        mType = jsonObject.getString(RestConstants.TYPE);
        // Title
        mTitle = jsonObject.getString(RestConstants.TITLE);
        // Has Data
        mHasData = jsonObject.optBoolean(RestConstants.HAS_DATA);
        // Products
        JSONArray richRelevanceData = jsonObject.getJSONArray(RestConstants.DATA);
        if(richRelevanceData != null && richRelevanceData.length() > 0){
            for (int i = 0; i < richRelevanceData.length() ; i++) {
                ProductRichRelevance richRelevanceProduct = new ProductRichRelevance();
                if(richRelevanceProduct.initialize(richRelevanceData.getJSONObject(i)))
                    mRichRelevanceProducts.add(richRelevanceProduct);
            }
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }


    public String getType() {
        return mType;
    }

    public boolean isHasData() {
        return mHasData;
    }

    public String getTitle() {
        return mTitle;
    }

    public ArrayList<ProductRegular> getRichRelevanceProducts() {
        return mRichRelevanceProducts;
    }

    protected RichRelevance(Parcel in) {
        mType = in.readString();
        mHasData = in.readByte() != 0x00;
        mTitle = in.readString();
        if (in.readByte() == 0x01) {
            mRichRelevanceProducts = new ArrayList<>();
            in.readList(mRichRelevanceProducts, ProductRichRelevance.class.getClassLoader());
        } else {
            mRichRelevanceProducts = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
        dest.writeByte((byte) (mHasData ? 0x01 : 0x00));
        dest.writeString(mTitle);
        if (mRichRelevanceProducts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mRichRelevanceProducts);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RichRelevance> CREATOR = new Parcelable.Creator<RichRelevance>() {
        @Override
        public RichRelevance createFromParcel(Parcel in) {
            return new RichRelevance(in);
        }

        @Override
        public RichRelevance[] newArray(int size) {
            return new RichRelevance[size];
        }
    };
}
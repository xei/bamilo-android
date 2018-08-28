package com.bamilo.android.framework.service.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/25/15.
 */
public class GlobalSeller implements IJSONSerializable, Parcelable {

    public GlobalSeller() {
        name = "Jumia";
    }

    public GlobalSeller(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    private String name;
    private String deliveryTime;
    private boolean isGlobal;
    private String info;
    private String shippingInfo;
    private String staticText;
    private String staticLink;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        name = jsonObject.optString(RestConstants.NAME);
        deliveryTime = jsonObject.getString(RestConstants.DELIVERY_TIME);
        isGlobal = jsonObject.getBoolean(RestConstants.IS_GLOBAL);
        if(isGlobal){
            JSONObject global = jsonObject.getJSONObject(RestConstants.GLOBAL);
            info = global.getString(RestConstants.CMS_INFO);
            shippingInfo = global.getString(RestConstants.SHIPPING);
            JSONObject linkObject = global.getJSONObject(RestConstants.LINK);
            staticText = linkObject.optString(RestConstants.TEXT);
            staticLink = linkObject.optString(RestConstants.URL);
        }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.deliveryTime);
        dest.writeByte(isGlobal ? (byte) 1 : (byte) 0);
        dest.writeString(this.info);
        dest.writeString(this.shippingInfo);
        dest.writeString(this.staticText);
        dest.writeString(this.staticLink);
    }

    protected GlobalSeller(Parcel in) {
        this.name = in.readString();
        this.deliveryTime = in.readString();
        this.isGlobal = in.readByte() != 0;
        this.info = in.readString();
        this.shippingInfo = in.readString();
        this.staticText = in.readString();
        this.staticLink = in.readString();
    }

    public static final Creator<GlobalSeller> CREATOR = new Creator<GlobalSeller>() {
        public GlobalSeller createFromParcel(Parcel source) {
            return new GlobalSeller(source);
        }

        public GlobalSeller[] newArray(int size) {
            return new GlobalSeller[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public String getInfo() {
        return info;
    }

    public String getShippingInfo() {
        return shippingInfo;
    }

    public String getStaticText() {
        return staticText;
    }

    public String getStaticLink() {
        return staticLink;
    }
}

package com.mobile.service.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by msilva on 4/11/16.
 */
public class OrderActions implements Parcelable, IJSONSerializable {

    private final static String ACTION_ONLINE_RETURN = "online_return";
    private final static String ACTION_CALL_RETURN = "call_return";

    private String mReturnType;
    private String mTarget;
    private String mTitle;
    private String mBody1;
    private String mBody2;
    private int mReturnableQuantity;

    public OrderActions(){}

    @Override
    public boolean initialize(final JSONObject jsonObject) throws JSONException {
        mReturnType = jsonObject.getString(RestConstants.TYPE);
        if(isCallToReturn()){
            mTitle = jsonObject.getString(RestConstants.TEXT_TITLE);
            mBody1 = jsonObject.getString(RestConstants.TEXT_BODY1);
            mBody2 = jsonObject.getString(RestConstants.TEXT_BODY2);
        } else {
            mTarget = jsonObject.getString(RestConstants.TARGET);
        }

        mReturnableQuantity = jsonObject.getInt(RestConstants.RETURNABLE_QUANTITY);
        return true;
    }

    public int getReturnableQuantity() {
        return mReturnableQuantity;
    }

    @Nullable
    public String getReturnType() {
        return mReturnType;
    }

    @Nullable
    public String getTarget() {
        return mTarget;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getBody1() {
        return mBody1;
    }

    @Nullable
    public String getBody2() {
        return mBody2;
    }

    public boolean isCallToReturn(){
        return TextUtils.equals(getReturnType(), OrderActions.ACTION_CALL_RETURN);
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
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mReturnType);
        dest.writeString(mTarget);
        dest.writeInt(mReturnableQuantity);
        dest.writeString(mTitle);
        dest.writeString(mBody1);
        dest.writeString(mBody2);

    }

    protected OrderActions(final Parcel in) {
        mReturnType = in.readString();
        mTarget = in.readString();
        mReturnableQuantity = in.readInt();
        mTitle = in.readString();
        mBody1 = in.readString();
        mBody2 = in.readString();
    }

    public static final Creator<OrderActions> CREATOR = new Creator<OrderActions>() {
        @Override
        public OrderActions createFromParcel(final Parcel in) {
            return new OrderActions(in);
        }

        @Override
        public OrderActions[] newArray(final int size) {
            return new OrderActions[size];
        }
    };
}

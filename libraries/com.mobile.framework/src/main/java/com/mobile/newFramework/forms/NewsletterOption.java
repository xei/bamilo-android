package com.mobile.newFramework.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONObject;

/**
 * 
 * @author sergiopereira
 * 
 */
public class NewsletterOption implements IJSONSerializable, Parcelable{

    public static final String TAG = NewsletterOption.class.getSimpleName();
    
    public boolean isDefault;
    
    public String key;

    public String value;

    public String label;

    public boolean isSubscribed;
    
    public String name;

    /**
     * @author sergiopereira
     */
    public NewsletterOption(JSONObject object, String name) {
        initialize(object);
        this.name = name.replace("[]", "[" + value + "]");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.framework.interfaces.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject object) {
        isDefault = object.optBoolean(RestConstants.IS_DEFAULT);
        key = object.optString(RestConstants.KEY);
        value = object.optString(RestConstants.VALUE);
        label = object.optString(RestConstants.LABEL);
        isSubscribed = object.optBoolean(RestConstants.USER_SUBSCRIBED);
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
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return label;
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
        dest.writeBooleanArray(new boolean[] {isDefault});
        dest.writeString(key);
        dest.writeString(value);
        dest.writeString(label);
        dest.writeBooleanArray(new boolean[] {isSubscribed});
    }
    
    /**
     * Parcel constructor
     */
    private NewsletterOption(Parcel in) {
        in.readBooleanArray( new boolean[] {isDefault});
        key = in.readString();
        value = in.readString();
        label = in.readString();
        in.readBooleanArray( new boolean[] {isSubscribed});
    }
    
    /**
     * Create parcelable 
     */
    public static final Creator<NewsletterOption> CREATOR = new Creator<NewsletterOption>() {
        public NewsletterOption createFromParcel(Parcel in) {
            return new NewsletterOption(in);
        }

        public NewsletterOption[] newArray(int size) {
            return new NewsletterOption[size];
        }
    };

}

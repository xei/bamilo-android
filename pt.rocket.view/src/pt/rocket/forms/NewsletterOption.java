package pt.rocket.forms;

import org.json.JSONObject;

import pt.rocket.framework.interfaces.IJSONSerializable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author sergiopereira
 * 
 */
public class NewsletterOption implements IJSONSerializable, Parcelable{

    public static final String TAG = NewsletterOption.class.getSimpleName();
    
    public boolean isDefaut;
    
    public String value;
    
    public String label;
    
    public boolean isSubscrided;
    
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
     * @see pt.rocket.framework.interfaces.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject object) {
        isDefaut = object.optBoolean("is_default");
        value = object.optString("value");
        label = object.optString("label");
        isSubscrided = object.optBoolean("user_subscribed");
        return true;
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
        dest.writeBooleanArray(new boolean[] {isDefaut});
        dest.writeString(value);
        dest.writeString(label);
        dest.writeBooleanArray(new boolean[] {isSubscrided});
    }
    
    /**
     * Parcel constructor
     * @param in
     */
    private NewsletterOption(Parcel in) {
        in.readBooleanArray( new boolean[] {isDefaut});
        value = in.readString();
        label = in.readString();
        in.readBooleanArray( new boolean[] {isSubscrided});
    }
    
    /**
     * Create parcelable 
     */
    public static final Parcelable.Creator<NewsletterOption> CREATOR = new Parcelable.Creator<NewsletterOption>() {
        public NewsletterOption createFromParcel(Parcel in) {
            return new NewsletterOption(in);
        }

        public NewsletterOption[] newArray(int size) {
            return new NewsletterOption[size];
        }
    };

}

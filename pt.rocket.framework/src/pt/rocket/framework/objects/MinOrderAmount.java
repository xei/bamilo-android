package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 
 * Class that represents the minimum order amount
 */
public class MinOrderAmount implements IJSONSerializable, Parcelable {

	public final static String TAG = MinOrderAmount.class.getSimpleName();

	double value = 0;

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		JSONObject paymentSettings = jsonObject.optJSONObject(RestConstants.JSON_PAYMENT_SETTINGS_TAG);
		if ( paymentSettings == null) {
			value = 0;
		}
		String valueString = paymentSettings.optString(RestConstants.JSON_CART_MIN_ORDER_TAG);
		if (TextUtils.isEmpty(valueString)) {
			value = 0;
		} else {
			try {
				value = Double.parseDouble(valueString);
			} catch (NumberFormatException e) {
				value = 0;
			}
		}
		
		return true;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	public double getValue() {
		return value;
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
	    dest.writeDouble(value);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private MinOrderAmount(Parcel in) {
	    value = in.readDouble();
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<MinOrderAmount> CREATOR = new Parcelable.Creator<MinOrderAmount>() {
        public MinOrderAmount createFromParcel(Parcel in) {
            return new MinOrderAmount(in);
        }

        public MinOrderAmount[] newArray(int size) {
            return new MinOrderAmount[size];
        }
    };
	
}

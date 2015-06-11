//package com.mobile.framework.objects;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.newFramework.pojo.JsonConstants;
//
///**
// *
// * Class that represents the minimum order amount
// */
//public class MinOrderAmount implements IJSONSerializable, Parcelable {
//
//	public final static String TAG = MinOrderAmount.class.getSimpleName();
//
//	double value = 0;
//
//	@Override
//	public boolean initialize(JSONObject jsonObject) throws JSONException {
//		JSONObject paymentSettings = jsonObject.optJSONObject(JsonConstants.JSON_PAYMENT_SETTINGS_TAG);
//		try {
//			value = Double.parseDouble(paymentSettings.optString(JsonConstants.JSON_CART_MIN_ORDER_TAG));
//		} catch (NullPointerException e) {
//			value = 0;
//		} catch (NumberFormatException e) {
//			value = 0;
//		}
//
//		return true;
//	}
//
//	@Override
//	public JSONObject toJSON() {
//		return null;
//	}
//
//	public double getValue() {
//		return value;
//	}
//
//
//    /**
//     * ########### Parcelable ###########
//     * @author sergiopereira
//     */
//
//    /*
//     * (non-Javadoc)
//     * @see android.os.Parcelable#describeContents()
//     */
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
//	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//	    dest.writeDouble(value);
//	}
//
//	/**
//	 * Parcel constructor
//	 * @param in
//	 */
//	private MinOrderAmount(Parcel in) {
//	    value = in.readDouble();
//    }
//
//	/**
//	 * Create parcelable
//	 */
//	public static final Parcelable.Creator<MinOrderAmount> CREATOR = new Parcelable.Creator<MinOrderAmount>() {
//        public MinOrderAmount createFromParcel(Parcel in) {
//            return new MinOrderAmount(in);
//        }
//
//        public MinOrderAmount[] newArray(int size) {
//            return new MinOrderAmount[size];
//        }
//    };
//
//}

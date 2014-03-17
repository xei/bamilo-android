package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class Order implements IJSONSerializable, Parcelable {

	public final static String TAG = LogTagHelper.create(Order.class);

	private PaymentMethods mPaymentOptions;

	private String mOrderNumber;

	private String mFirstName;

	private String mLastName;

	/**
	 * OrderTracker empty constructor.
	 */
	public Order() {
	}

	/**
	 * OrderTracker empty constructor.
	 * 
	 * @throws JSONException
	 */
	public Order(JSONObject jsonObject) throws JSONException {
		initialize(jsonObject);
	}

	/**
	 * @return the mPaymentOptions
	 */
	public PaymentMethods getPaymentOptions() {
		return mPaymentOptions;
	}

	/**
	 * @param mPaymentOptions
	 *            the mPaymentOptions to set
	 */
	public void setPaymentOptions(PaymentMethods mPaymentOptions) {
		this.mPaymentOptions = mPaymentOptions;
	}

	/**
	 * @return the mOrderNumber
	 */
	public String getOrderNumber() {
		return mOrderNumber;
	}

	/**
	 * @return the mFirstName
	 */
	public String getFirstName() {
		return mFirstName;
	}

	/**
	 * @return the mLastName
	 */
	public String getLastName() {
		return mLastName;
	}

	/**
	 * @param mOrderNumber
	 *            the mOrderNumber to set
	 */
	public void setOrderNumber(String mOrderNumber) {
		this.mOrderNumber = mOrderNumber;
	}

	/**
	 * @param mFirstName
	 *            the mFirstName to set
	 */
	public void setFirstName(String mFirstName) {
		this.mFirstName = mFirstName;
	}

	/**
	 * @param mLastName
	 *            the mLastName to set
	 */
	public void setLastName(String mLastName) {
		this.mLastName = mLastName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {

		// Metada:

		// Cash On Delivery:
		// {
		// "success": true,
		// "messages": {
		// "success": [
		// "ORDER_SUCCESS"
		// ]
		// },
		// "session": {
		// "id": "hec322fvnb97f0kqp7mv6s2e81",
		// "expire": null,
		// "YII_CSRF_TOKEN": "92945c37307600580b25eee7e6d6fa690a9aa126"
		// },
		// "metadata": {
		// "order_nr": "300012712",
		// "customer_first_name": "mob nsme",
		// "customer_last_name": "mob last",
		// "payment": []
		// }
		// }

		// Get order number
		mOrderNumber = jsonObject.getString("order_nr");
		// Get first name
		mFirstName = jsonObject.optString("customer_first_name");
		// Get last name
		mLastName = jsonObject.optString("customer_last_name");

		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		// TODO
		return null;
	}

	/**
	 * ########### Parcelable ###########
	 * 
	 * @author sergiopereira
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
		dest.writeString(mOrderNumber);
		dest.writeString(mFirstName);
		dest.writeString(mLastName);
		// dest.writeString(last_order_update);
		// dest.writeList(orderTracketItems);
	}

	/**
	 * Parcel constructor
	 * 
	 * @param in
	 */
	private Order(Parcel in) {
		mOrderNumber = in.readString();
		mFirstName = in.readString();
		mLastName = in.readString();
		// in.readList(orderTracketItems,
		// OrderTrackerItem.class.getClassLoader());
	}

	/**
	 * Create parcelable
	 */
	public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
		public Order createFromParcel(Parcel in) {
			return new Order(in);
		}

		public Order[] newArray(int size) {
			return new Order[size];
		}
	};

}

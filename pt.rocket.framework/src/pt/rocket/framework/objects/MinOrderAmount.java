package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;

import android.text.TextUtils;

/**
 * 
 * Class that represents the minimum order amount
 */
public class MinOrderAmount implements IJSONSerializable {

	private final static String TAG = MinOrderAmount.class.getSimpleName();

//	private final static String JSON_PAYMENT_SETTINGS_TAG = "payment_settings";
//	private final static String JSON_CART_MIN_ORDER_TAG = "cart_min_order_amount";

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

}

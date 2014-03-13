/**
 * 
 */
package pt.rocket.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Voucher;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

/**
 * Set Voucher helper
 * 
 * @author Manuel Silva
 * 
 */
public class SetVoucherHelper extends BaseHelper {

    private static String TAG = SetVoucherHelper.class.getSimpleName();

    public static final String VOUCHER_PARAM = "couponcode";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.ADD_VOUCHER.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        if(args.getParcelable(VOUCHER_PARAM) == null){
            bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, new ContentValues());
        } else {
            bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(VOUCHER_PARAM));
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_VOUCHER);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        android.util.Log
                .d("TRACK", "parseResponseBundle SetVoucherHelper " + jsonObject.toString());
        Log.i(TAG, "code1coupon : " + jsonObject.toString());
        Voucher mVoucher = new Voucher();

        mVoucher.setCartValue(jsonObject.optString(RestConstants.JSON_CART_VALUE_TAG));
        mVoucher.setCouponMoneyValue(jsonObject.optString(RestConstants.JSON_CART_COUPON_VALUE_TAG));

        JSONObject messageArray = jsonObject.optJSONObject(RestConstants.JSON_MESSAGES_TAG);
        if (messageArray != null && messageArray.length() > 0) {
            Iterator keys = messageArray.keys();
            if (messageArray != null && messageArray.length() > 0) {
                HashMap<String, String> messages = new HashMap<String, String>();
                while (keys.hasNext()) {
                    try {
                        String key = keys.next().toString();
                        messages.put(key, messageArray.getString(key));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mVoucher.setMessages(messages);
            }
        }

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, mVoucher);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_VOUCHER);

        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle SetVoucherHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_VOUCHER);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseResponseErrorBundle SetVoucherHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_VOUCHER);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}

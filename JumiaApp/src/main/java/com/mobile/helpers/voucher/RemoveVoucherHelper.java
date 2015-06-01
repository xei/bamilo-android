/**
 * 
 */
package com.mobile.helpers.voucher;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.objects.Voucher;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.voucher.RemoveVoucher;

import java.util.Map;

import de.akquinet.android.androlog.Log;

/**
 * Set Voucher helper
 * 
 * @author Manuel Silva
 * 
 */
public class RemoveVoucherHelper extends SuperBaseHelper {

    private static String TAG = RemoveVoucherHelper.class.getSimpleName();

    public static final String VOUCHER_PARAM = "couponcode";

    @Override
    public EventType getEventType() {
        return EventType.REMOVE_VOUCHER;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return SuperBaseHelper.convertContentValuesToMap((ContentValues) args.getParcelable(VOUCHER_PARAM));
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new RemoveVoucher(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.success);
        Voucher voucher = (Voucher) baseResponse.metadata.getData();
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, voucher);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.message);
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }



//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.REMOVE_VOUCHER.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(VOUCHER_PARAM));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_VOUCHER);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d("TRACK", "parseResponseBundle SetVoucherHelper " + jsonObject.toString());
//        Log.i(TAG, "code1coupon : " + jsonObject.toString());
//        Voucher mVoucher = new Voucher();
//
//        mVoucher.setCartValue(jsonObject.optString(RestConstants.JSON_CART_VALUE_TAG));
//        mVoucher.setCouponMoneyValue(jsonObject.optString(RestConstants.JSON_CART_COUPON_VALUE_TAG));
//
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, mVoucher);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_VOUCHER);
//
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle SetVoucherHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_VOUCHER);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseResponseErrorBundle SetVoucherHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_VOUCHER);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}

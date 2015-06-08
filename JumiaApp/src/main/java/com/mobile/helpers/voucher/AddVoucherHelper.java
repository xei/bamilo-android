package com.mobile.helpers.voucher;

import android.os.Bundle;

import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.objects.voucher.Voucher;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;

/**
 * Set Voucher helper
 */
public class AddVoucherHelper extends SuperBaseHelper {

    private static String TAG = AddVoucherHelper.class.getSimpleName();

    public static final String VOUCHER_PARAM = "couponcode";

    @Override
    public EventType getEventType() {
        return EventType.ADD_VOUCHER;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new AddVoucher(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.addVoucher);
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        Voucher voucher = (Voucher) baseResponse.getMetadata().getData();
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, voucher);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }


//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.ADD_VOUCHER.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        if(args == null){
//            bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, new ContentValues());
//        } else {
//            bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(VOUCHER_PARAM));
//        }
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
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_VOUCHER);
//
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle SetVoucherHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_VOUCHER);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseResponseErrorBundle SetVoucherHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_VOUCHER);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}

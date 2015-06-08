package com.mobile.helpers.products;

import android.os.Bundle;

import com.mobile.framework.output.Print;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.objects.product.SuperValidProducts;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.product.ValidateProducts;

/**
 * Validate one or more products on the API and actualize their information
 *
 * @author Paulo Carvalho
 */
public class ValidateProductHelper extends SuperBaseHelper {

    private static final String TAG = ValidateProductHelper.class.getSimpleName();

    public static final String VALIDATE_PRODUCTS_KEY = "products[%d]";


    @Override
    public EventType getEventType() {
        return EventType.VALIDATE_PRODUCTS;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new ValidateProducts(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.validateProducts);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        SuperValidProducts validProducts = (SuperValidProducts) baseResponse.getMetadata().getData();
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, validProducts);
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
//        Log.i(TAG, "ON GENERATE BUNDLE");
//        ContentValues values = args.getParcelable(Constants.CONTENT_VALUES);
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, values);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.VALIDATE_PRODUCTS);
//        return bundle;
//    }

//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "ON PARSE RESPONSE");
//        boolean status;
//        try {
//            JSONArray validProductsArray = jsonObject.optJSONArray(RestConstants.JSON_VALID_TAG);
//            if (validProductsArray != null) {
//                ArrayList<LastViewedAddableToCart> validProducts = new ArrayList<>();
//                if (validProductsArray.length() > 0) {
//                    for (int i = 0; i < validProductsArray.length(); i++) {
//                        LastViewedAddableToCart lastViewedAddableToCart = new LastViewedAddableToCart();
//                        status = lastViewedAddableToCart.initialize(validProductsArray.getJSONObject(i));
//                        lastViewedAddableToCart.setComplete(true);
//                        validProducts.add(lastViewedAddableToCart);
//
//                        if (!status) {
//                            return parseErrorBundle(bundle);
//                        }
//                    }
//                }
//                updateRecentViewedDatabaseThread(validProducts);
//                bundle.putSerializable(Constants.BUNDLE_RESPONSE_KEY, validProducts);
//                bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.VALIDATE_PRODUCTS);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return parseErrorBundle(bundle);
//        }
//
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.i(TAG, "ON PARSE ERROR RESPONSE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.VALIDATE_PRODUCTS);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.i(TAG, "ON ERROR RESPONSE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.VALIDATE_PRODUCTS);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }

//    /**
//     * using a new Thread to update the database data in a background thread, not blocking the UI thread
//     */
//    private void updateRecentViewedDatabaseThread(final ArrayList<LastViewedAddableToCart> validProducts) {
//
//        new Thread(new Runnable() {
//            public void run() {
//                updateRecentlyViewedDatabase(validProducts);
//            }
//        }).start();
//
//    }
//
//
//    /**
//     * update recentely viewed products on database
//     *
//     * @param validProducts
//     * @return
//     */
//    private boolean updateRecentlyViewedDatabase(ArrayList<LastViewedAddableToCart> validProducts) {
//
//        if (CollectionUtils.isEmpty(validProducts)) {
//            LastViewedTableHelper.deleteAllLastViewed();
//            return false;
//        } else {
//            LastViewedTableHelper.updateLastViewed(validProducts);
//            return true;
//        }
//    }
}

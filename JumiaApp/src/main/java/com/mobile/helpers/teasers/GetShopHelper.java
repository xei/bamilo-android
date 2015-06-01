/**
 *
 */
package com.mobile.helpers.teasers;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.objects.StaticPage;
import com.mobile.framework.service.RemoteService;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.home.GetShopInShopPage;

import de.akquinet.android.androlog.Log;

/**
 * Helper used to get the inner shop.
 *
 * @author sergiopereira
 */
public class GetShopHelper extends SuperBaseHelper {

    private static String TAG = GetShopHelper.class.getSimpleName();

    public static final String INNER_SHOP_TAG = "key";

    @Override
    public EventType getEventType() {
        return EventType.GET_SHOP_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RemoteService.completeUri(Uri.parse(args.getString(Constants.BUNDLE_URL_KEY))).toString();
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new GetShopInShopPage(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.success);
        // TODO: CREATE NEW OBJECT
        StaticPage staticPage = (StaticPage) baseResponse.metadata.getData();
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putString(Constants.BUNDLE_RESPONSE_KEY, staticPage.getHtml());
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
//        bundle.putString(Constants.BUNDLE_URL_KEY, args.getString(Constants.BUNDLE_URL_KEY));
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "ON PARSE RESPONSE");
//        try {
//            StaticPage staticPage = new StaticPage();
//            staticPage.initialize(jsonObject);
//            bundle.putString(Constants.BUNDLE_RESPONSE_KEY, staticPage.getHtml());
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return parseErrorBundle(bundle);
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "ON PARSE ERROR BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "ON RESPONSE ERROR BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}

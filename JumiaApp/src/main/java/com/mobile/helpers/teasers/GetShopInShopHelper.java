///**
// *
// */
//package com.mobile.helpers.teasers;
//
//import android.net.Uri;
//import android.os.Bundle;
//
//import com.mobile.helpers.SuperBaseHelper;
//import com.mobile.newFramework.requests.BaseRequest;
//import com.mobile.newFramework.requests.RequestBundle;
//import com.mobile.newFramework.rest.RestUrlUtils;
//import com.mobile.newFramework.rest.interfaces.AigApiInterface;
//import com.mobile.newFramework.utils.Constants;
//import com.mobile.newFramework.utils.EventType;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
//// * Helper used to get the inner shop.
// *
// * @author sergiopereira
// * @deprecated Please use the GetStaticPageHelper.
// */
//@Deprecated
//public class GetShopInShopHelper extends SuperBaseHelper {
//
//    private static String TAG = GetShopInShopHelper.class.getSimpleName();
//
//    public static final String INNER_SHOP_TAG = "key";
//
//    @Override
//    public EventType getEventType() {
//        return EventType.GET_SHOP_EVENT;
//    }
//
//    @Override
//    protected String getRequestUrl(Bundle args) {
//        return RestUrlUtils.completeUri(Uri.parse(mEventType.action)).toString();
//    }
//
//    @Override
//    protected Map<String, String> getRequestData(Bundle args) {
//        Map<String, String> data = new HashMap<>();
//        data.put(INNER_SHOP_TAG, args.getString(Constants.BUNDLE_URL_KEY));
//        return data;
//    }
//
//    @Override
//    protected void onRequest(RequestBundle requestBundle) {
//        new BaseRequest(requestBundle, this).execute(AigApiInterface.getShopInShop);
//    }
//
//}

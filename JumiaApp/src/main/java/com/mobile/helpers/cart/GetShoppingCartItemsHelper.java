/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartItemsHelper extends SuperBaseHelper {
    
    private static String TAG = GetShoppingCartItemsHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_SHOPPING_CART_ITEMS_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new GetShoppingCart(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getShoppingCart);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        ShoppingCart cart = (ShoppingCart) baseResponse.getMetadata().getData();
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getCartValue());
        // Create bundle
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }

//   private static final EventType EVENT_TYPE = EventType.GET_SHOPPING_CART_ITEMS_EVENT;

//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT.action);
//
//        // If event hasn't priority IS_NOT_PRIORITARY is defined, otherwise is defined IS_PRIORITY
//        if(args != null && !args.getBoolean(Constants.BUNDLE_PRIORITY_KEY)){
//            bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
//        } else  {
//            bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        }
//
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        String md5 = Utils.uniqueMD5(EVENT_TYPE.name());
//        bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
//        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "ON PARSE REPONSE" + jsonObject.toString());
//        JumiaApplication.INSTANCE.setCart(null);
//        Log.d(TAG, "CLEAN CART");
//        ShoppingCart cart = new ShoppingCart();
//        try {
//            cart.initialize(jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JumiaApplication.INSTANCE.setCart(cart);
//        Log.d(TAG, "ADD CART: " + cart.getCartValue());
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetShoppingCartItemsHelper");
//
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}

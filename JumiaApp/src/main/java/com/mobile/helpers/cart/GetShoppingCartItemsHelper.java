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
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getShoppingCart);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);

        //TODO move to observable
        ShoppingCart cart = (ShoppingCart) baseResponse.getMetadata().getData();
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());
        // Create bundle
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
    }

}

/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.TrackerDelegator;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartAddBundleHelper extends SuperBaseHelper {
    
    private static String TAG = GetShoppingCartAddBundleHelper.class.getSimpleName();
    
    public static final String BUNDLE_ID = "bundleId";

    // TODO: USE placeholders

//  product-item-selector[0]
//  ...
    public static final String PRODUCT_SKU_TAG = "product-item-selector[";
    
//  product-simple-selector[0]
//  ... 
    public static final String PRODUCT_SIMPLE_SKU_TAG = "product-simple-selector[";


    @Override
    public EventType getEventType() {
        return EventType.ADD_PRODUCT_BUNDLE;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.addBundleShoppingCart);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);

        //TODO move to observable
        JumiaApplication.INSTANCE.setCart(null);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getMetadata().getData();
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount(), cart.getAttributeSetIdList());

//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
    }

}

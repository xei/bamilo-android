package com.mobile.helpers.products.wishlist;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Catalog Page helper
 *
 * @author sergiopereira
 *
 */
public class RemoveFromWishListHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.REMOVE_PRODUCT_FROM_WISH_LIST;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.removeFromWishList);
    }

}

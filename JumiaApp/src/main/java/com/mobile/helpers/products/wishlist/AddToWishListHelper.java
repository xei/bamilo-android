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
public class AddToWishListHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.ADD_PRODUCT_TO_WISH_LIST;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.addToWishList);
    }

}

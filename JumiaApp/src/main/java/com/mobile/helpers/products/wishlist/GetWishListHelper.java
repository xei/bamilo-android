package com.mobile.helpers.products.wishlist;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.catalog.CatalogPage;
import com.mobile.newFramework.objects.product.WishList;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Catalog Page helper
 *
 * @author sergiopereira
 *
 */
public class GetWishListHelper extends SuperBaseHelper {

    private static String TAG = GetWishListHelper.class.getSimpleName();

    public static final int MAX_ITEMS_PER_PAGE = CatalogPage.MAX_ITEMS_PER_PAGE;

    public static final int FIRST_PAGE_NUMBER = 1;

    @Override
    public EventType getEventType() {
        return EventType.GET_WISH_LIST;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getWishList);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        WishList wishList = (WishList) baseResponse.getMetadata().getData();
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, wishList);
    }

    @Override
    public void createErrorBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createErrorBundleParams(baseResponse, bundle);
    }

}

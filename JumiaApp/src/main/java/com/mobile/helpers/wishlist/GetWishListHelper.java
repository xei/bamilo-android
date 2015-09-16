package com.mobile.helpers.wishlist;


import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.product.WishList;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Catalog Page helper
 *
 * @author sergiopereira
 */
public class GetWishListHelper extends SuperBaseHelper {

    public static String TAG = GetWishListHelper.class.getSimpleName();

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
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, wishList);
    }

    @Override
    public void createErrorBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createErrorBundleParams(baseResponse, bundle);
    }

    /**
     * Method used to create the request bundle.
     */
    public static Bundle createBundle(int page) {
        // Item data
        ContentValues values = new ContentValues();
        values.put(RestConstants.PAGE, page);
        values.put(RestConstants.PER_PAGE, IntConstants.MAX_ITEMS_PER_PAGE);
        // Request data
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, page == IntConstants.FIRST_PAGE ? EventTask.NORMAL_TASK : EventTask.SMALL_TASK);
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}

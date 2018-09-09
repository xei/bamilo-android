package com.bamilo.android.appmodule.bamiloapp.helpers.wishlist;


import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.objects.product.WishList;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.cache.WishListCache;

/**
 * Helper used to get the default wish list.
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
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        // Save new wish list
        WishListCache.set(((WishList) baseResponse.getMetadata().getData()).getWishListCache());
    }

    /**
     * Method used to create the request bundle.
     */
    public static Bundle createBundle(int page) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.PAGE, page);
        values.put(RestConstants.PER_PAGE, IntConstants.MAX_ITEMS_PER_PAGE);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, page == IntConstants.FIRST_PAGE ? EventTask.NORMAL_TASK : EventTask.ACTION_TASK);
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }

}

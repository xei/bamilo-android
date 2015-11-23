/**
 *
 */
package com.mobile.helpers.teasers;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the inner shop.
 *
 * @author sergiopereira
 */
public class GetShopInShopHelper extends SuperBaseHelper {

    private static String TAG = GetShopInShopHelper.class.getSimpleName();

    public static final String INNER_SHOP_TAG = "key";

    @Override
    public EventType getEventType() {
        return EventType.GET_SHOP_EVENT;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getShopInShop);
    }

    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(String staticKey) {
        // Item data
        ContentValues values = new ContentValues();
        values.put(INNER_SHOP_TAG, staticKey);
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}

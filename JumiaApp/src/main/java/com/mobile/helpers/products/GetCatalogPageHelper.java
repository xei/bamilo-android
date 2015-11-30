package com.mobile.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.catalog.Catalog;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

import java.util.Map;

/**
 * Get Catalog Page helper
 *
 * @author sergiopereira
 */
public class GetCatalogPageHelper extends SuperBaseHelper {

    protected static String TAG = GetCatalogPageHelper.class.getSimpleName();

    // TODO: API SHOULD RETURN THIS PARAMETER RESPONSE
    private int mCurrentPage = IntConstants.FIRST_PAGE;

    // Request parameters
    public static final String PAGE = "page";
    public static final String MAX_ITEMS = "maxitems";
    public static final String SORT = "sort";
    public static final String DIRECTION = "dir";
    public static final String QUERY = "q";
    public static final String CATEGORY = "category";
    public static final String BRAND = "brand";
    public static final String HASH = "hash";

    @Override
    public EventType getEventType() {
        return EventType.GET_CATALOG_EVENT;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        // Get catalog parameters
        ContentValues catalogArguments = args.getParcelable(Constants.BUNDLE_PATH_KEY);
        // Get page number
        if (catalogArguments != null) {
            mCurrentPage = catalogArguments.getAsInteger(PAGE);
        }
        return super.getRequestData(args);
    }

    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCatalog);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        Catalog catalog = (Catalog) baseResponse.getMetadata().getData();
        catalog.getCatalogPage().setPage(mCurrentPage);
    }

}

package com.mobile.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.objects.catalog.Catalog;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;

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
            mCurrentPage = catalogArguments.getAsInteger(RestConstants.PAGE);
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
        Catalog catalog = (Catalog) baseResponse.getContentData();
        catalog.getCatalogPage().setPage(mCurrentPage);
    }

}

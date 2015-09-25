package com.mobile.helpers.products;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.catalog.Catalog;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;

import java.util.Map;

/**
 * Get Catalog Page helper
 *
 * @author sergiopereira
 */
public class GetCatalogPageHelper extends SuperBaseHelper {

    protected static String TAG = GetCatalogPageHelper.class.getSimpleName();

    public static final String URL = Constants.BUNDLE_URL_KEY;
    //
    private int mCurrentPage = IntConstants.FIRST_PAGE;
    // Request parameters
    public static final String PAGE = "page";
    public static final String MAX_ITEMS = "maxitems";
    public static final String SORT = "sort";
    public static final String DIRECTION = "dir";
    public static final String QUERY = "q";
    public static final String CATEGORY = "category";
    public static final String BRAND = "brand";

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCTS_EVENT;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        // Get catalog parameters
        ContentValues catalogArguments = args.getParcelable(Constants.BUNDLE_DATA_KEY);
        // Get page number
        mCurrentPage = catalogArguments.getAsInteger(PAGE);
        //
        return super.getRequestData(args);
    }


    @Override
    protected String getRequestUrl(Bundle args) {
        // Get catalog URL
        String baseUrl = args.getString(URL);
        // Case search then url is empty
        if (TextUtils.isEmpty(baseUrl)) baseUrl = mEventType.action;
        //
        return RestUrlUtils.completeUri(Uri.parse(baseUrl)).toString();
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        // Get catalog URL
        String baseUrl = args.getString(URL);
        // Case search then url is empty
        if (TextUtils.isEmpty(baseUrl)) baseUrl = mEventType.action;
        //
        return RestUrlUtils.completeUri(Uri.parse(baseUrl)).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCatalogFiltered);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        Catalog catalog = (Catalog) baseResponse.getMetadata().getData();
        catalog.getCatalogPage().setPage(mCurrentPage);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, catalog.getCatalogPage());
    }

    @Override
    public void createErrorBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createErrorBundleParams(baseResponse, bundle);
        //TODO move to observable
        // Validate Featured Box
        Catalog catalog = (Catalog) baseResponse.getMetadata().getData();
        if(baseResponse.getError().getErrorCode() == ErrorCode.REQUEST_ERROR && catalog != null){
            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, catalog.getFeaturedBox());
            bundle.putInt(Constants.BUNDLE_OBJECT_TYPE_KEY, IntConstants.FEATURE_BOX_TYPE);
        }
    }

}

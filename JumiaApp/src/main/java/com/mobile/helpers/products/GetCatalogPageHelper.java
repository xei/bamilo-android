package com.mobile.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.catalog.Catalog;
import com.mobile.newFramework.objects.catalog.CatalogPage;
import com.mobile.newFramework.pojo.BaseResponse;
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
 *
 */
public class GetCatalogPageHelper extends SuperBaseHelper {

    protected static String TAG = GetCatalogPageHelper.class.getSimpleName();

    public static final int MAX_ITEMS_PER_PAGE = CatalogPage.MAX_ITEMS_PER_PAGE;

    public static final int FIRST_PAGE_NUMBER = 1;

    public static final String URL = Constants.BUNDLE_URL_KEY;

    public static final int FEATURE_BOX_TYPE = 999;
    //
    private int mCurrentPage = FIRST_PAGE_NUMBER;
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
        return convertContentValuesToMap(catalogArguments);
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
            bundle.putInt(Constants.BUNDLE_OBJECT_TYPE_KEY, FEATURE_BOX_TYPE);
        }
    }

}

/**
 * 
 */
package com.mobile.helpers.search;

import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.objects.ProductsPage;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import de.akquinet.android.androlog.Log;

/**
 * Search a Product using sku
 * 
 * @author sergiopereira
 * 
 */
public class GetSearchProductHelper extends BaseHelper {
    
    private static String TAG = GetSearchProductHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.SEARCH_PRODUCT;
    
    public static final String SKU_TAG = "sku";
    
    ProductsPage mProductsPage= new ProductsPage();

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "DEEP LINK ON GENERATE REQUEST");
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        Uri uri = Uri.parse(EVENT_TYPE.action).buildUpon().appendQueryParameter(SKU_TAG, args.getString(SKU_TAG)).build();
        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "ON PARSE RESPONSE BUNDLE");
        CompleteProduct product = new CompleteProduct();
        product.initialize(jsonObject);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, product);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON PARSE RESPONSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}

/**
 * 
 */
package com.mobile.helpers.products;

import org.json.JSONObject;

import android.os.Bundle;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.ProductBundle;
import com.mobile.framework.objects.ProductsPage;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import de.akquinet.android.androlog.Log;

/**
 * Get Product Bundle Information helper
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetProductBundleHelper extends BaseHelper {

    private static String TAG = GetProductBundleHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_PRODUCT_BUNDLE;

    public static final String PRODUCT_SKU = "productSku";

    ProductsPage mProductsPage = new ProductsPage();

    @Override
    public Bundle generateRequestBundle(Bundle args) {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY,
                EventType.GET_PRODUCT_BUNDLE.action + args.getString(PRODUCT_SKU));
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_BUNDLE);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d("TRACK", "parseResponseBundle GetProductsBundleHelper");

        ProductBundle productBundle = new ProductBundle();
        productBundle.initialize(jsonObject);
        // boolean status = product.initialize(jsonObject);
        // // Validate product initialization
        // if(!status) return parseErrorBundle(bundle);
        // // Return product
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, productBundle);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_BUNDLE);
        return bundle;

    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetBundleHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_BUNDLE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_BUNDLE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}

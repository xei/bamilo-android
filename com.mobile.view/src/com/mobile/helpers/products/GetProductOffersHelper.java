/**
 * 
 */
package com.mobile.helpers.products;

import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.ProductOffers;
import com.mobile.framework.objects.ProductsPage;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import de.akquinet.android.androlog.Log;

/**
 * Get Product Offers
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetProductOffersHelper extends BaseHelper {

    private static String TAG = GetProductOffersHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.GET_PRODUCT_OFFERS;

    public static final String ALL_OFFERS = "all_offers";
    public static final String PRODUCT_URL = "productUrl";

    ProductsPage mProductsPage = new ProductsPage();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        Uri uri = Uri.parse(args.getString(PRODUCT_URL)).buildUpon().appendQueryParameter(ALL_OFFERS, "1").build();

        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY,
                HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_OFFERS);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d("TRACK", "parseResponseBundle GetProductOffersHelper");
        ProductOffers productOffers = new ProductOffers(jsonObject);
       
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, productOffers);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_OFFERS);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetProductOffersHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_OFFERS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetProductOffersHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_OFFERS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    
}

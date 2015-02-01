/**
 * 
 */
package pt.rocket.helpers.products;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.ProductOffers;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.net.Uri;
import android.os.Bundle;
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
        try {
            Log.d("OFFER", "OBJECT:"+jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

/**
 * 
 */
package pt.rocket.helpers.products;

import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.os.Bundle;

/**
 * Get Product Information helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetProductHelper extends BaseHelper {
    
    private static String TAG = GetProductHelper.class.getSimpleName();
    
    public static final String PRODUCT_URL = "productUrl";
    
    ProductsPage mProductsPage= new ProductsPage();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, args.getString(PRODUCT_URL));
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_EVENT);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        de.akquinet.android.androlog.Log.d("TRACK", "parseResponseBundle GetProductsHelper");
        
        CompleteProduct product = new CompleteProduct();
        product.initialize(jsonObject);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, product);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_EVENT);
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.GET_PRODUCT_EVENT);
//        Log.i("REQUEST", "event type response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        de.akquinet.android.androlog.Log.d(TAG, "parseErrorBundle GetTeasersHelper");
     
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}

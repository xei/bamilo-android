/**
 * 
 */
package pt.rocket.helpers;

import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;
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
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        android.util.Log.d("TRACK", "parseResponseBundle GetProductsHelper");
        
        CompleteProduct product = new CompleteProduct();
        product.initialize(jsonObject);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, product);

        return bundle;
    }

    @Override   
    public Bundle parseErrorBundle(Bundle bundle) {
    	android.util.Log.d("TRACK", "parseErrorBundle GetCategoriesHelper");
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetCategories");
        return bundle;
    }
}

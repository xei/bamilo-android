/**
 * 
 */
package pt.rocket.helpers;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.ProductRatingPage;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * Get Product Reviews helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetWriteReviewHelper extends BaseHelper {

    private static String TAG = GetWriteReviewHelper.class.getSimpleName();

    public static final String PRODUCT_URL = "productUrl";
    public static final String PAGE_NUMBER = "pageNumber";

    ProductsPage mProductsPage = new ProductsPage();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        Uri uri = Uri.parse(args.getString(PRODUCT_URL)).buildUpon().appendQueryParameter(RestContract.REST_PARAM_RATING, "1")
                .appendQueryParameter(RestContract.REST_PARAM_PAGE, String.valueOf(args.getInt(PAGE_NUMBER))).build();

        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY,
                HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        android.util.Log.d("TRACK", "parseResponseBundle GetProductReviewsHelper");
        JSONObject dataObject = null;
        try {
            dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ProductRatingPage rating = new ProductRatingPage();
        try {
            rating.initialize(dataObject);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, rating);

        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d("TRACK", "parseErrorBundle GetCategoriesHelper");
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetCategories");
        return bundle;
    }
}

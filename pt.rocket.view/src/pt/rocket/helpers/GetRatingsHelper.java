/**
 * 
 */
package pt.rocket.helpers;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;
import pt.rocket.pojo.EventType;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * Get Ratings helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetRatingsHelper extends BaseHelper {

    private static String TAG = GetRatingsHelper.class.getSimpleName();

    public static final String PRODUCT_URL = "productUrl";

    ProductsPage mProductsPage = new ProductsPage();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_RATING_OPTIONS_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY,
                HelperPriorityConfiguration.CATEGORIES_PRIORITY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        android.util.Log.d("TRACK", "parseResponseBundle GetRatingsHelper");
        HashMap<String, HashMap<String, String>> ratingOptions = new HashMap<String, HashMap<String, String>>();
        JSONArray dataArray = null;
        try {
            dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject ratingOption = null;
        JSONObject optionObject = null;
        HashMap<String, String> option;
        int size = dataArray.length();
        int optionsSize = 0;
        for (int i = 0; i < size; i++) {
            JSONArray optionsArray = null;
            try {
                ratingOption = dataArray.getJSONObject(i);

                optionsArray = ratingOption.getJSONArray(RestConstants.JSON_OPTIONS_TAG);

                option = new HashMap<String, String>();
                optionsSize = optionsArray.length();
                for (int k = 0; k < optionsSize; k++) {
                    optionObject = optionsArray.getJSONObject(k);
                    option.put(optionObject.getString(RestConstants.JSON_PROD_VALUE_TAG),
                            optionObject.getString(RestConstants.JSON_ID_RATING_OPTION_TAG));
                }

                ratingOptions.put(ratingOption.getString(RestConstants.JSON_CODE_TAG), option);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, (Parcelable) ratingOptions);

        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d("TRACK", "parseErrorBundle GetCategoriesHelper");
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetCategories");
        return bundle;
    }
}

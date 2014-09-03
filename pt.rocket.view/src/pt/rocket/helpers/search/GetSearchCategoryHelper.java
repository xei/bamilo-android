/**
 * 
 */
package pt.rocket.helpers.search;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Category;
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
 * Class used to get a category from an id
 * 
 * @author sergiopereira 
 * 
 */
public class GetSearchCategoryHelper extends BaseHelper {
    
    private static String TAG = GetSearchCategoryHelper.class.getSimpleName();
    
    private static final EventType type = EventType.GET_CATEGORIES_EVENT;
    
    public static final String CATEGORY_TAG = "category";

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        Uri uri = Uri.parse(type.action).buildUpon().appendQueryParameter(CATEGORY_TAG, args.getString(CATEGORY_TAG)).build();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CATEGORIES_EVENT);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "PARSE RESPONSE BUNDLE");
        try {
        	de.akquinet.android.androlog.Log.d("TRACK", "parseResponseBundle GetCategoriesHelper");
        	JSONArray categoriesArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
            int categoriesArrayLenght = categoriesArray.length();
            ArrayList<Category> categories = new ArrayList<Category>();
            for (int i = 0; i < categoriesArrayLenght; ++i) {
                JSONObject categoryObject = categoriesArray.getJSONObject(i);
                Category category = new Category();
                category.initialize(categoryObject);
                categories.add(category);
            }
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, categories);
        } catch (JSONException e) {
            Log.w(TAG, "ERROR ON PARSING JSON OBJECT", e);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CATEGORIES_EVENT);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE ERROR BUNDLE");
        de.akquinet.android.androlog.Log.d(TAG, "parseErrorBundle GetCategoriesHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CATEGORIES_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE RESPONSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CATEGORIES_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}

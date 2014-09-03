/**
 * 
 */
package pt.rocket.helpers.categories;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.database.CategoriesTableHelper;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Class used to get categories from API:<br>
 * - All categories (No argmuments)<br>
 * - Root categories (Arguments with paginate=1)<br>
 * - Specific category (Arguments with paginate=1 and category=<url_key>)<br>
 * 
 * @author sergiopereira
 * 
 */
public class GetCategoriesPerLevelsHelper extends BaseHelper {
    
    private static String TAG = GetCategoriesPerLevelsHelper.class.getSimpleName();
    
    private static final EventType TYPE = EventType.GET_CATEGORIES_EVENT;
    
    public static final String PAGINATE_KEY = "paginate";
    
    public static final String CATEGORY_KEY = "category";
    
    public static final String PAGINATE_ENABLE = "1";
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.i(TAG, "ON REQUEST");
        Bundle bundle = new Bundle();
        String url = (args != null) ? buildUriWithParameters(TYPE.action, args) : TYPE.action; 
        bundle.putString(Constants.BUNDLE_URL_KEY, url);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.i(TAG, "ON PARSE RESPONSE");
        try {
            // Get data
        	JSONArray categoriesArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
            int categoriesArrayLenght = categoriesArray.length();
            ArrayList<Category> categories = new ArrayList<Category>();
            // For each child
            for (int i = 0; i < categoriesArrayLenght; ++i) {
                // Get category
                JSONObject categoryObject = categoriesArray.getJSONObject(i);
                Category category = new Category();
                category.initialize(categoryObject);
                categories.add(category);
            }
            // Add categories
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, categories);
            // Save categories for tracking
            CategoriesTableHelper.saveCategories(categories);
            
        } catch (JSONException e) {
            e.printStackTrace();
            return parseErrorBundle(bundle);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetCategoriesHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
  
}

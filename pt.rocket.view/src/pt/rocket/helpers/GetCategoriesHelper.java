/**
 * 
 */
package pt.rocket.helpers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.database.CategoriesTableHelper;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.os.Bundle;
import android.util.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetCategoriesHelper extends BaseHelper {
    
    public static final String CATEGORY_URL = "category_url";
    
    private static String TAG = GetCategoriesHelper.class.getSimpleName();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_CATEGORIES_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CATEGORIES_EVENT);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        try {// TODO add further object parsing possibilities : for example data
             // not being an array but a dictionary
        	android.util.Log.d("TRACK", "parseResponseBundle GetCategoriesHelper");
        	JSONArray categoriesArray = jsonObject
                    .getJSONArray(RestConstants.JSON_DATA_TAG);
            int categoriesArrayLenght = categoriesArray
                    .length();
            ArrayList<Category> categories = new ArrayList<Category>();
            for (int i = 0; i < categoriesArrayLenght; ++i) {
                JSONObject categoryObject = categoriesArray
                        .getJSONObject(i);
                Category category = new Category();
                category.initialize(categoryObject);
                categories.add(category);
            }
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, categories);
            CategoriesTableHelper.saveCategories(categories);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CATEGORIES_EVENT);
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.GET_CATEGORIES_EVENT);
//        Log.i("REQUEST", "event type response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetCategoriesHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CATEGORIES_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CATEGORIES_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}

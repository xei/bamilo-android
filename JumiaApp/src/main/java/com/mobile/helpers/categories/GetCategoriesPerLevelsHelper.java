package com.mobile.helpers.categories;

import android.os.Bundle;

import com.mobile.framework.output.Print;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.category.Categories;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.categories.GetCategoriesPaginated;

/**
 * Class used to get categories from API:<br>
 * - All categories (No argmuments)<br>
 * - Root categories (Arguments with paginate=1)<br>
 * - Specific category (Arguments with paginate=1 and category=<url_key>)<br>
 * 
 * @author sergiopereira
 * 
 */
public class GetCategoriesPerLevelsHelper extends SuperBaseHelper {
    
    public static String TAG = GetCategoriesPerLevelsHelper.class.getSimpleName();
    
    public static final String PAGINATE_KEY = "paginate";
    
    public static final String CATEGORY_KEY = "category";
    
    public static final String PAGINATE_ENABLE = "1";

    @Override
    public void onRequest(RequestBundle requestBundle) {
        // Request
        new GetCategoriesPaginated(requestBundle, this).execute();
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_CATEGORIES_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        Categories categories = (Categories) baseResponse.getMetadata().getData();
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, categories);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }

//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.i(TAG, "ON REQUEST");
//        Bundle bundle = new Bundle();
//        String url = (args != null) ? buildUriWithParameters(EVENT_TYPE.action, args) : EVENT_TYPE.action;
//        bundle.putString(Constants.BUNDLE_URL_KEY, url);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "ON PARSE RESPONSE");
//        try {
//            // Get data
//        	JSONArray categoriesArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//            int categoriesArrayLenght = categoriesArray.length();
//            ArrayList<Category> categories = new ArrayList<>();
//            // For each child
//            for (int i = 0; i < categoriesArrayLenght; ++i) {
//                // Get category
//                JSONObject categoryObject = categoriesArray.getJSONObject(i);
//                Category category = new Category();
//                category.initialize(categoryObject);
//                categories.add(category);
//            }
//            // Add categories
//            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, categories);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return parseErrorBundle(bundle);
//        }
//
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetCategoriesHelper");
//
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
    
}

/**
 * 
 */
package pt.rocket.helpers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.objects.SearchSuggestion;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Get Search Suggestion  helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetSearchSuggestionHelper extends BaseHelper {
    
    private static String TAG = GetSearchSuggestionHelper.class.getSimpleName();
    
    public static final String SEACH_PARAM = "searchParam";
    
    ProductsPage mProductsPage= new ProductsPage();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        Uri uri = Uri.parse(EventType.GET_SEARCH_SUGGESTIONS_EVENT.action).buildUpon().appendQueryParameter("q", args.getString(SEACH_PARAM)).build();
        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        android.util.Log.d("TRACK", "parseResponseBundle GetProductsHelper");
        ArrayList<SearchSuggestion> suggestions = new ArrayList<SearchSuggestion>();

        JSONArray suggestionsArray = null;
        try {
            suggestionsArray = jsonObject.getJSONArray(RestConstants.JSON_SUGGESTIONS_TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < suggestionsArray.length(); ++i) {
            SearchSuggestion suggestion = new SearchSuggestion();
            try {
                suggestion.initialize(suggestionsArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            suggestions.add(suggestion);
        }

        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, suggestions);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.GET_SEARCH_SUGGESTIONS_EVENT);
//        Log.i("REQUEST", "event type response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return bundle;
    }
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetSuggestionsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseResponseErrorBundle GetSuggestionsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
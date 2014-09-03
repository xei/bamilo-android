/**
 * 
 */
package pt.rocket.helpers.search;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.database.SearchRecentQueriesTableHelper;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.SearchSuggestion;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import pt.rocket.interfaces.IResponseCallback;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import de.akquinet.android.androlog.Log;

/**
 * Get Search Suggestion  helper
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class GetSearchSuggestionHelper extends BaseHelper {
    
    private static final String TAG = GetSearchSuggestionHelper.class.getSimpleName();

    private static final EventType type = EventType.GET_SEARCH_SUGGESTIONS_EVENT;
    
    public static final String SEACH_PARAM = "searchParam";

    private String mQuery;

    public GetSearchSuggestionHelper(){
        super();
    }
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        // Get the current query
        mQuery = args.getString(SEACH_PARAM);
        // Request suggestions
        Uri uri = Uri.parse(EventType.GET_SEARCH_SUGGESTIONS_EVENT.action).buildUpon().appendQueryParameter("q", mQuery).build();
        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
        return bundle;
    }

    /**
     * Constructor used to get only the recent queries
     * 
     * @param requester
     */
    public GetSearchSuggestionHelper(IResponseCallback requester) {
        Log.d(TAG, "ON CONSTRUCTOR");
        // Get all items on database
        getSearchSuggestionList(requester);
    }

    /**
     * Get all recent queries and deliver them to the <code>requester</code>
     * 
     * @param requester
     */
    private void getSearchSuggestionList(IResponseCallback requester) {
        Log.d(TAG, "ON GET_SEARCH_SUGGESTIONS_EVENT");
        ArrayList<SearchSuggestion> suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putSerializable(Constants.BUNDLE_RESPONSE_KEY, suggestions);
        requester.onRequestComplete(bundle);
    }

    /**
     * Save the recent query on the database
     * @param query
     * @author sergiopereira
     */
    public static void saveSearchQuery(final String query){
        Log.d(TAG, "SAVE SEARCH QUERY: " + query);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchRecentQueriesTableHelper.insertQuery(query);
            }
        }).start();
    }
    
    /**
     * Update the recent query on the database
     * @param query
     * @author sergiopereira
     */
    public static void updateSearchQuery(final String query){
        Log.d(TAG, "UPDATE SEARCH QUERY: " + query);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchRecentQueriesTableHelper.updateRecentQuery(query);
            }
        }).start();
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "ON PARSE RESPONSE");

        ArrayList<SearchSuggestion> suggestions = new ArrayList<SearchSuggestion>();
        
        // Get recent queries
        try {
            if(TextUtils.isEmpty(mQuery)) suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
            else suggestions = SearchRecentQueriesTableHelper.getFilteredRecentQueries(mQuery);
        } catch (SQLiteException e) {
            Log.w(TAG, "ERROR ON GET RECENT QUERIES: " + mQuery);
        }
        // Parse response
        try {
            JSONArray suggestionsArray = jsonObject.getJSONArray(RestConstants.JSON_SUGGESTIONS_TAG);
            for (int i = 0; i < suggestionsArray.length(); ++i) {
                SearchSuggestion suggestion = new SearchSuggestion();
                suggestion.initialize(suggestionsArray.getJSONObject(i));
                suggestions.add(suggestion);
            }
        } catch (JSONException e) {
            Log.w(TAG, "ERROR PARSING SUGGESTIONS", e);
            return parseErrorBundle(bundle);
        }
        
        bundle.putString(SEACH_PARAM, mQuery);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, suggestions);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON PARSE ERROR BUNDLE");
        
        // Get the recent queries 
        ArrayList<SearchSuggestion> suggestions = new ArrayList<SearchSuggestion>();
        // Get recent queries
        try {
            if(TextUtils.isEmpty(mQuery)) suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
            else suggestions = SearchRecentQueriesTableHelper.getFilteredRecentQueries(mQuery);
        } catch (SQLiteException e) {
            Log.w(TAG, "ERROR ON GET RECENT QUERIES: " + mQuery);
        }
        Log.d(TAG, "SUGGESTION: " + suggestions.size());
        
        // Add error if no match
        if(suggestions.size() > 0 ) bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, false);
        bundle.putString(SEACH_PARAM, mQuery);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, suggestions);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON PARSE RESPONSE ERROR BUNDLE");
//        bundle.putString(SEACH_PARAM, mQuery);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
        return parseErrorBundle(bundle);
    }
}

package com.mobile.helpers.search;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;

import com.mobile.framework.database.SearchRecentQueriesTableHelper;
import com.mobile.framework.output.Print;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.newFramework.objects.search.Suggestions;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.search.SearchSuggestions;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Get Search Suggestion  helper
 *
 * @author Manuel Silva
 * @modified sergiopereira
 *
 */
public class GetSearchSuggestionsHelper extends SuperBaseHelper {

    private static final String TAG = GetSearchSuggestionsHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_SEARCH_SUGGESTIONS_EVENT;

    public static final String SEACH_PARAM = "searchParam";

    public static final String QUERY = "q";

    private String mQuery;

    /*
     * ############# SEARCH SUGGESTIONS COMPONENT ####################
     */

    public GetSearchSuggestionsHelper() {
        super();
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_SEARCH_SUGGESTIONS_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return super.getRequestUrl(args);
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        // Get the current query
        mQuery = args.getString(SEACH_PARAM);
        Map<String, String> data = new HashMap<>();
        data.put(QUERY, mQuery);
        return data;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new SearchSuggestions(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getSearchSuggestions);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        // Get recent queries
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        try {
            if(TextUtils.isEmpty(mQuery)) {
                suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
            } else {
                suggestions = SearchRecentQueriesTableHelper.getFilteredRecentQueries(mQuery);
            }
        } catch (SQLiteException e) {
            Print.w(TAG, "ERROR ON GET RECENT QUERIES: " + mQuery);
        } catch (InterruptedException e) {
            Print.w(TAG, "WARNING: IE ON GET RECENT SEARCHES", e);
        }
        //
        Suggestions searchSuggestions = (Suggestions) baseResponse.getMetadata().getData();
        CollectionUtils.addAll(suggestions, searchSuggestions);
        //
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putString(SEACH_PARAM, mQuery);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, suggestions);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        // Get the recent queries
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        // Get recent queries
        try {
            if(TextUtils.isEmpty(mQuery)) {
                suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
            } else {
                suggestions = SearchRecentQueriesTableHelper.getFilteredRecentQueries(mQuery);
            }
        } catch (SQLiteException e) {
            Print.w(TAG, "ERROR ON GET RECENT QUERIES: " + mQuery);
        } catch (InterruptedException e) {
            Print.w(TAG, "WARNING: IE ON GET RECENT SEARCHES", e);
        }
        Print.d(TAG, "SUGGESTION: " + suggestions.size());

        // Add error if no match
        Bundle bundle = generateErrorBundle(baseResponse);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, suggestions.size() > 0);
        bundle.putString(SEACH_PARAM, mQuery);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, suggestions);
        mRequester.onRequestError(bundle);
    }

    /*
     * ############# RECENT SEARCHES VIEW ####################
     */

    /**
     * Constructor used to get only the recent queries
     *
     * @param requester
     */
    public GetSearchSuggestionsHelper(IResponseCallback requester) {
        Print.d(TAG, "ON CONSTRUCTOR");
        // Get all items on database
        getSearchSuggestionList(requester);
    }

    /**
     * Get all recent queries and deliver them to the <code>requester</code>
     *
     * @param requester
     */
    private void getSearchSuggestionList(IResponseCallback requester) {
        Print.d(TAG, "ON GET_SEARCH_SUGGESTIONS_EVENT");

        ArrayList<Suggestion> suggestions = new ArrayList<>();
        try {
            suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
        } catch (InterruptedException e) {
            Print.w(TAG, "WARNING: IE ON GET RECENT SEARCHES", e);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putSerializable(Constants.BUNDLE_RESPONSE_KEY, suggestions);
        requester.onRequestComplete(bundle);
    }

    /**
     * Save the recent query on the database
     * @param query
     * @author sergiopereira
     */
    public static void saveSearchQuery(final String query){
        Print.d(TAG, "SAVE SEARCH QUERY: " + query);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchRecentQueriesTableHelper.insertQuery(query);
            }
        }).start();
    }



//    public GetSearchSuggestionHelper(){
//        super();
//    }
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        // Get the current query
//        mQuery = args.getString(SEACH_PARAM);
//        // Request suggestions
//        Uri uri = Uri.parse(EventType.GET_SEARCH_SUGGESTIONS_EVENT.action).buildUpon().appendQueryParameter("q", mQuery).build();
//        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
//        return bundle;
//    }

//    /**
//     * Update the recent query on the database
//     * @param query
//     * @author sergiopereira
//     */
//    public static void updateSearchQuery(final String query){
//        Log.d(TAG, "UPDATE SEARCH QUERY: " + query);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SearchRecentQueriesTableHelper.updateRecentQuery(query);
//            }
//        }).start();
//    }

//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "ON PARSE RESPONSE");
//
//        ArrayList<SearchSuggestion> suggestions = new ArrayList<>();
//
//        // Get recent queries
//        try {
//            if(TextUtils.isEmpty(mQuery)) suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
//            else suggestions = SearchRecentQueriesTableHelper.getFilteredRecentQueries(mQuery);
//        } catch (SQLiteException e) {
//            Log.w(TAG, "ERROR ON GET RECENT QUERIES: " + mQuery);
//        } catch (InterruptedException e) {
//            Log.w(TAG, "WARNING: IE ON GET RECENT SEARCHES", e);
//        }
//
//        // Parse response
//        try {
//            JSONArray suggestionsArray = jsonObject.getJSONArray(RestConstants.JSON_SUGGESTIONS_TAG);
//            for (int i = 0; i < suggestionsArray.length(); ++i) {
//                SearchSuggestion suggestion = new SearchSuggestion();
//                suggestion.initialize(suggestionsArray.getJSONObject(i));
//                suggestions.add(suggestion);
//            }
//        } catch (JSONException e) {
//            Log.w(TAG, "ERROR PARSING SUGGESTIONS", e);
//            return parseErrorBundle(bundle);
//        }
//
//        bundle.putString(SEACH_PARAM, mQuery);
//        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, suggestions);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
//        return bundle;
//    }

//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "ON PARSE ERROR BUNDLE");
//
//        // Get the recent queries
//        ArrayList<SearchSuggestion> suggestions = new ArrayList<>();
//        // Get recent queries
//        try {
//            if(TextUtils.isEmpty(mQuery)) suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
//            else suggestions = SearchRecentQueriesTableHelper.getFilteredRecentQueries(mQuery);
//        } catch (SQLiteException e) {
//            Log.w(TAG, "ERROR ON GET RECENT QUERIES: " + mQuery);
//        } catch (InterruptedException e) {
//            Log.w(TAG, "WARNING: IE ON GET RECENT SEARCHES", e);
//        }
//        Log.d(TAG, "SUGGESTION: " + suggestions.size());
//
//        // Add error if no match
//        if(suggestions.size() > 0 ) bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, false);
//        bundle.putString(SEACH_PARAM, mQuery);
//        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, suggestions);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
//        return bundle;
//    }

//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "ON PARSE RESPONSE ERROR BUNDLE");
////        bundle.putString(SEACH_PARAM, mQuery);
////        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SEARCH_SUGGESTIONS_EVENT);
////        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
////        return bundle;
//        return parseErrorBundle(bundle);
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}

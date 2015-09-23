package com.mobile.helpers.search;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.database.SearchRecentQueriesTableHelper;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.newFramework.objects.search.Suggestions;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);

        //TODO move to observable
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

        SuggestionsStruct suggestionsStruct = new SuggestionsStruct(searchSuggestions);
        suggestionsStruct.setSearchParam(mQuery);
        baseResponse.getMetadata().setData(suggestionsStruct);

    }

    @Override
    public void postError(BaseResponse baseResponse) {
        super.postError(baseResponse);

        //TODO move to observable
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

//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, suggestions.size() > 0);
//        bundle.putString(SEACH_PARAM, mQuery);
        SuggestionsStruct suggestionsStruct = new SuggestionsStruct(suggestions);
        suggestionsStruct.setSearchParam(mQuery);
        baseResponse.getMetadata().setData(suggestionsStruct);
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

//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putSerializable(Constants.BUNDLE_RESPONSE_KEY, suggestions);

        BaseResponse baseResponse = new BaseResponse();
        super.postSuccess(baseResponse);
        SuggestionsStruct suggestionsStruct = new SuggestionsStruct(suggestions);
        suggestionsStruct.setSearchParam(mQuery);
        baseResponse.getMetadata().setData(suggestionsStruct);
        requester.onRequestComplete(baseResponse);
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
                try {
                    SearchRecentQueriesTableHelper.insertQuery(query);
                } catch (IllegalStateException e){
                    // ...
                }
            }
        }).start();
    }

    public class SuggestionsStruct extends Suggestions {

        private String searchParam;

        public SuggestionsStruct(){}

        public SuggestionsStruct(Suggestions suggestions){
            super(suggestions);
        }

        public SuggestionsStruct(List<Suggestion> suggestions){
            super(suggestions);
        }

        public String getSearchParam() {
            return searchParam;
        }

        public void setSearchParam(String searchParam) {
            this.searchParam = searchParam;
        }
    }
}

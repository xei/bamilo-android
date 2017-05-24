package com.mobile.helpers.search;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.service.database.SearchRecentQueriesTableHelper;
import com.mobile.service.objects.search.Suggestion;
import com.mobile.service.objects.search.Suggestions;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;

import java.util.ArrayList;
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

    public static final String SEARCH_PARAM = "searchParam";

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
    protected Map<String, String> getRequestData(Bundle args) {
        // Get the current query
        mQuery = args.getString(SEARCH_PARAM);
        return super.getRequestData(args);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getSearchSuggestions);
    }

    public static Bundle createBundle(String query) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.QUERY, query);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        bundle.putString(SEARCH_PARAM, query);
        return bundle;
    }


    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);

        //TODO move to observable
        // Get recent queries
        ArrayList<Suggestion> recentQueries = new ArrayList<>();
        try {
            if(TextUtils.isEmpty(mQuery) ) {
                recentQueries = SearchRecentQueriesTableHelper.getAllRecentQueries();
            }
        } catch (SQLiteException e) {
            Print.w(TAG, "ERROR ON GET RECENT QUERIES: " + mQuery);
        } catch (InterruptedException e) {
            Print.w(TAG, "WARNING: IE ON GET RECENT SEARCHES", e);
        }
        //
        Suggestions searchSuggestions = (Suggestions) baseResponse.getContentData();
        //add the recent searches in database to the suggestions
        CollectionUtils.addAll(searchSuggestions, recentQueries, 0);
        SuggestionsStruct suggestionsStruct = new SuggestionsStruct();
        suggestionsStruct.setRecentSuggestions(searchSuggestions);
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

        SuggestionsStruct suggestionsStruct = new SuggestionsStruct();
        suggestionsStruct.setRecentSuggestions(suggestions);
        suggestionsStruct.setSearchParam(mQuery);
        baseResponse.getMetadata().setData(suggestionsStruct);
    }

    /*
     * ############# RECENT SEARCHES VIEW ####################
     */

    /**
     * Constructor used to get only the recent queries
     */
    public GetSearchSuggestionsHelper(IResponseCallback requester) {
        Print.d(TAG, "ON CONSTRUCTOR");
        // Get all items on database
        getSearchSuggestionList(requester);
    }

    /**
     * Get all recent queries and deliver them to the <code>requester</code>
     */
    private void getSearchSuggestionList(IResponseCallback requester) {
        Print.d(TAG, "ON GET_SEARCH_SUGGESTIONS_EVENT");

        ArrayList<Suggestion> suggestions = new ArrayList<>();
        try {
            suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
        } catch (InterruptedException e) {
            Print.w(TAG, "WARNING: IE ON GET RECENT SEARCHES", e);
        }

        BaseResponse baseResponse = new BaseResponse();
        super.postSuccess(baseResponse);
        SuggestionsStruct suggestionsStruct = new SuggestionsStruct();
        suggestionsStruct.setRecentSuggestions(suggestions);
        suggestionsStruct.setSearchParam(mQuery);
        baseResponse.getMetadata().setData(suggestionsStruct);
        requester.onRequestComplete(baseResponse);
    }

    /**
     * Save the recent query on the database
     */
    public static void saveSearchQuery(final Suggestion suggestion){
        Print.d(TAG, "SAVE SEARCH QUERY: " + suggestion.getResult());
        /*RecommendManager recommendManager = new RecommendManager();
        recommendManager.sendSearchRecommend(suggestion.getQuery(), new RecommendListCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(String category, List<RecommendedItem> data) {

            }
        });*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SearchRecentQueriesTableHelper.insertQuery(suggestion);
                } catch (Exception e){
                    // ...
                }
            }
        }).start();
    }
}
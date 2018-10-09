package com.bamilo.android.appmodule.bamiloapp.helpers.search;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.database.SearchRecentQueriesTableHelper;
import com.bamilo.android.framework.service.objects.search.Suggestion;
import com.bamilo.android.framework.service.objects.search.Suggestions;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;

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
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {
        }

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
        // Get all items on database
        getSearchSuggestionList(requester);
    }

    /**
     * Get all recent queries and deliver them to the <code>requester</code>
     */
    private void getSearchSuggestionList(IResponseCallback requester) {
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        try {
            suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
        } catch (InterruptedException ignored) {
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
        new Thread(() -> {
            try {
                SearchRecentQueriesTableHelper.insertQuery(suggestion);
            } catch (Exception ignored){
            }
        }).start();
    }
}

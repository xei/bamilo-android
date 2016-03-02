package com.mobile.helpers.search;

import android.content.Context;

import com.mobile.app.JumiaApplication;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.database.SearchRecentQueriesTableHelper;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.TextUtils;

import java.util.ArrayList;

/**
 * Created by msilva on 2/8/16.
 */
public class SearchSuggestionClient {

    public SearchSuggestionClient(){

    }

    public void getSuggestions(Context context, IResponseCallback responseCallback, String searchTerm, boolean useAlgolia) {

        if(TextUtils.isEmpty(searchTerm)) {
            try {
                ArrayList<Suggestion> suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
                SuggestionsStruct suggestionsStruct = new SuggestionsStruct();
                suggestionsStruct.setRecentSuggestions(suggestions);
                suggestionsStruct.setSearchParam(searchTerm);
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.getMetadata().setData(suggestionsStruct);
                responseCallback.onRequestComplete(baseResponse);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if(searchTerm.length() >= 2) {
            if(useAlgolia){
                new AlgoliaHelper(context, responseCallback).getSuggestions(searchTerm);
            } else {
                JumiaApplication.INSTANCE.sendRequest(new GetSearchSuggestionsHelper(), GetSearchSuggestionsHelper.createBundle(searchTerm), responseCallback);
            }
        }

    }
}

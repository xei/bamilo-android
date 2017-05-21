package com.mobile.helpers.search;

import android.content.Context;

import com.mobile.app.BamiloApplication;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.service.database.SearchRecentQueriesTableHelper;
import com.mobile.service.objects.search.Suggestion;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.utils.TextUtils;

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

                BamiloApplication.INSTANCE.sendRequest(new GetSearchSuggestionsHelper(), GetSearchSuggestionsHelper.createBundle(searchTerm), responseCallback);
                //Should be move to Search
                /*RecommendManager recommendManager = new RecommendManager();
                recommendManager.sendSearchRecommend(searchTerm, new RecommendListCompletionHandler() {
                    @Override
                    public void onRecommendedRequestComplete(String category, List<RecommendedItem> data) {

                    }
                });*/
            }
        }

    }
}

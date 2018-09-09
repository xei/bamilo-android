package com.bamilo.android.appmodule.bamiloapp.helpers.search;

import android.content.Context;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.database.SearchRecentQueriesTableHelper;
import com.bamilo.android.framework.service.objects.search.Suggestion;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.TextUtils;

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

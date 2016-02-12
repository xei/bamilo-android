package com.mobile.helpers.search;

import android.content.Context;

import com.mobile.app.JumiaApplication;
import com.mobile.interfaces.IResponseCallback;

/**
 * Created by msilva on 2/8/16.
 */
public class SearchSuggestionClient {

    public SearchSuggestionClient(){

    }

    public void getSuggestions(Context context, IResponseCallback responseCallback, String searchTerm, boolean useAlgolia) {

        if(useAlgolia && searchTerm.length() > 2) new AlgoliaHelper(context, JumiaApplication.INSTANCE.getAlgoliaClient(), responseCallback).getSuggestions(searchTerm);
        else JumiaApplication.INSTANCE.sendRequest(new GetSearchSuggestionsHelper(), GetSearchSuggestionsHelper.createBundle(searchTerm), responseCallback);
    }
}

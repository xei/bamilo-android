package com.mobile.helpers.search;

import com.mobile.service.objects.search.Suggestion;
import com.mobile.service.objects.search.Suggestions;

import java.util.List;

/**
 * Created by msilva on 2/10/16.
 */
public class SuggestionsStruct extends Suggestions {

    private String searchParam;

    public SuggestionsStruct(){}

    public SuggestionsStruct(Suggestions suggestions){
        super(suggestions);
    }

    public SuggestionsStruct(List<Suggestion> suggestions){
        super(suggestions);
    }

    public void setSuggestions(List<Suggestion> suggestions){
        super.setSuggestions(suggestions);
    }

    /*
     * Keep the order of the suggestions retrieved by database recently searched.
     */
    public void setRecentSuggestions(List<Suggestion> suggestions){
        super.setRecentSuggestions(suggestions);
    }

    public String getSearchParam() {
        return searchParam;
    }

    public void setSearchParam(String searchParam) {
        this.searchParam = searchParam;
    }
}
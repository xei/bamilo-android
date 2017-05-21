package com.mobile.helpers.search;

/**
 * Created by Narbeh M. on 4/30/17.
 */

public final class SearchHelper {
    public static String getSearchTermsCommaSeparated(String query) {
        return query.replaceAll(" ", ",");
    }
}

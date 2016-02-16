package com.mobile.helpers.search;

import android.content.Context;
import android.support.annotation.NonNull;

import com.algolia.search.saas.APIClient;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.IndexQuery;
import com.algolia.search.saas.Query;
import com.algolia.search.saas.TaskParams;
import com.algolia.search.saas.listeners.APIClientListener;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.database.SearchRecentQueriesTableHelper;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.newFramework.objects.search.Suggestions;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.view.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by msilva on 2/8/16.
 */
public class AlgoliaHelper {
    private static final String TAG = AlgoliaHelper.class.getName();
    // Algolia Client
    private final APIClient mAlgoliaAPIClient;
    private final IResponseCallback mIResponseCallback;
    private final String mNamespacePrefix;
    private final static String _CATEGORIES = "_categories";
    private final static String _PRODUCTS_POPULAR = "_products_popular";
    private final static String _SHOPINSHOP = "_shopinshop";
    private final static String QUERY = "query";
    private final static String ID_CATALOG_CATEGORY = "id_catalog_category";

    private final static int HITS_PER_PAGE = 3;
    private final static int MAX_NUMBER_OFF_FACETS = 4;

    private SuggestionsStruct mSuggestionsStruct;
    private final Context mContext;

    public AlgoliaHelper(Context context, APIClient algoliaAPIClient, IResponseCallback responseCallback) {
        this.mContext = context;
        this.mAlgoliaAPIClient = algoliaAPIClient;
        this.mIResponseCallback = responseCallback;
        this.mNamespacePrefix = CountryPersistentConfigs.getAlgoliaInfoByKey(context, Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_PREFIX);
    }

    public void getSuggestions(@NonNull final String searchQuery){
        if(mAlgoliaAPIClient == null){
            Print.i(TAG , "code1algolia: ERROR: mAlgoliaAPIClient is null");
            return;
        }

        mSuggestionsStruct = new SuggestionsStruct();
        ArrayList<String> attributesToRetrieve = new ArrayList<>();
        attributesToRetrieve.add(RestConstants.SKU);
        attributesToRetrieve.add(RestConstants.LOCALIZABLE_ATTRIBUTES+"."+ ShopSelector.getCountryCode()+"."+RestConstants.NAME);

        ArrayList<String> facets = new ArrayList<>();
        facets.add(RestConstants.FACET_CATEGORY);

        List<IndexQuery> queries = new ArrayList<IndexQuery>();

        Query queryProductsPopular = new Query(searchQuery)
                .setHitsPerPage(HITS_PER_PAGE)
                .setAttributesToRetrieve(attributesToRetrieve)
                .setFacets(facets)
                .setMaxNumberOfFacets(MAX_NUMBER_OFF_FACETS)
                .setAttributesToHighlight(facets);

        queries.add(new IndexQuery(mNamespacePrefix+_PRODUCTS_POPULAR, queryProductsPopular));
        queries.add(new IndexQuery(mNamespacePrefix+_SHOPINSHOP, new Query(searchQuery).setHitsPerPage(1)));

        mAlgoliaAPIClient.multipleQueriesASync(queries, new APIClientListener() {
            @Override
            public void APIResult(APIClient client, TaskParams.Client context, JSONObject result) {
                BaseResponse response = new BaseResponse();
                String searchTerm = searchQuery;
                try {
                    searchTerm = result.getJSONArray(RestConstants.RESULTS).getJSONObject(1).getString(QUERY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final Suggestions suggestions = getProductsAndShopSuggestions(result, searchTerm);
                mSuggestionsStruct = new SuggestionsStruct(suggestions);
                mSuggestionsStruct.setSearchParam(searchTerm);
                Print.i(TAG, "code1highlight : "+ result);
                response.getMetadata().setData(mSuggestionsStruct);

                getCategoriesNames(result);

                mIResponseCallback.onRequestComplete(response);
            }

            @Override
            public void APIError(APIClient client, TaskParams.Client context, AlgoliaException e) {
                BaseResponse response = new BaseResponse();
                ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
                try {
                    suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                mSuggestionsStruct = new SuggestionsStruct(suggestions);
                mSuggestionsStruct.setSearchParam(searchQuery);
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.getMetadata().setData(mSuggestionsStruct);
                mIResponseCallback.onRequestComplete(baseResponse);
            }
        });

    }

    private void getCategoriesNames(JSONObject result){
        List<String> facetsFilter =new ArrayList<>();
        try {
            JSONArray results = result.getJSONArray(RestConstants.RESULTS);
            JSONObject facets = results.getJSONObject(0).getJSONObject(RestConstants.FACETS);
            JSONObject facetCategory = facets.getJSONObject(RestConstants.FACET_CATEGORY);

            Iterator<String> iter = facetCategory.keys();
            while (iter.hasNext()) {
                String key = iter.next();

                facetsFilter.add(ID_CATALOG_CATEGORY+":"+key);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String ffacetsFilter = "("+android.text.TextUtils.join(",",facetsFilter)+")";
        ArrayList<String> attributesToRetrieve = new ArrayList<>();
        attributesToRetrieve.add(RestConstants.LOCALIZABLE_ATTRIBUTES+"."+ ShopSelector.getCountryCode()+"."+RestConstants.NAME);

        Query q = new Query()
                .setAttributesToRetrieve(attributesToRetrieve)
                .setFacetFilters(ffacetsFilter);
        List<IndexQuery> queries = new ArrayList<IndexQuery>();

        IndexQuery iq = new IndexQuery(mNamespacePrefix+_CATEGORIES, q);
        queries.add(iq);
        mAlgoliaAPIClient.multipleQueriesASync(queries, new APIClientListener() {
            @Override
            public void APIResult(APIClient client, TaskParams.Client context, JSONObject result) {
                BaseResponse response = new BaseResponse();

                final Suggestions suggestions = getCategoriesSuggestions(result, mSuggestionsStruct.getSearchParam());
                mSuggestionsStruct.setSuggestions(suggestions);
                response.getMetadata().setData(mSuggestionsStruct);
                mIResponseCallback.onRequestComplete(response);
            }

            @Override
            public void APIError(APIClient client, TaskParams.Client context, AlgoliaException e) {
                BaseResponse response = new BaseResponse();
                response.getMetadata().setData(null);
                mIResponseCallback.onRequestError(response);
            }
        });

    }

    /**
     *
     * @param response
     * @return
     */
    private Suggestions getProductsAndShopSuggestions(JSONObject response, final String query){
        final Suggestions suggestions = new Suggestions();
        try {
            JSONArray results = response.getJSONArray(RestConstants.RESULTS);

            JSONArray shopHits = results.getJSONObject(1).getJSONArray(RestConstants.HITS);

            for (int j = 0; j <  shopHits.length(); j++){
                Suggestion suggestion = new Suggestion();
                JSONObject shop = shopHits.getJSONObject(j);
                String name = shop.getString(RestConstants.DOMAIN);
                final String capitalizedName = name.substring(0,1).toUpperCase() + name.substring(1);
                suggestion.setQuery(query);
                suggestion.setResult(capitalizedName);
                suggestion.setTarget(name);
                suggestion.setType(Suggestion.SUGGESTION_SHOP_IN_SHOP);
                suggestions.add(suggestion);
            }

            JSONArray hits = results.getJSONObject(0).getJSONArray(RestConstants.HITS);
            for (int i = 0; i <  hits.length(); i++){
                Suggestion suggestion = new Suggestion();
                JSONObject product = hits.getJSONObject(i);
                String name = product.getJSONObject(RestConstants.LOCALIZABLE_ATTRIBUTES).getJSONObject(ShopSelector.getCountryCode()).getString("name");
                suggestion.setQuery(query);
                suggestion.setResult(name);
                suggestion.setTarget(product.getString(RestConstants.SKU));
                suggestion.setType(Suggestion.SUGGESTION_PRODUCT);
                suggestions.add(suggestion);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return suggestions;
    }

    /**
     *
     * @param response
     * @return
     */
    private Suggestions getCategoriesSuggestions(JSONObject response, final String searchTerm){
        final Suggestions suggestions = new Suggestions();
        try {
            JSONArray results = response.getJSONArray(RestConstants.RESULTS);
            JSONArray hits = results.getJSONObject(0).getJSONArray(RestConstants.HITS);
            for (int i = 0; i <  hits.length(); i++){
                Suggestion suggestion = new Suggestion();
                JSONObject category = hits.getJSONObject(i).getJSONObject(RestConstants.LOCALIZABLE_ATTRIBUTES).getJSONObject(ShopSelector.getCountryCode());
                String name = category.getString(RestConstants.NAME);
                suggestion.setQuery(searchTerm);
                suggestion.setResult(name);
                suggestion.setTarget(name);
                suggestion.setType(Suggestion.SUGGESTION_CATEGORY);
                suggestions.add(suggestion);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return suggestions;
    }

}

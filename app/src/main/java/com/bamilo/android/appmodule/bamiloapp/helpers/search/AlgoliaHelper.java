//package com.bamilo.android.appmodule.bamiloapp.helpers.search;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//
//import com.algolia.search.saas.APIClient;
//import com.algolia.search.saas.AlgoliaException;
//import com.algolia.search.saas.IndexQuery;
//import com.algolia.search.saas.Query;
//import com.algolia.search.saas.TaskParams;
//import com.algolia.search.saas.listeners.APIClientListener;
//import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
//import com.bamilo.android.framework.service.Darwin;
//import com.bamilo.android.framework.service.database.SearchRecentQueriesTableHelper;
//import com.bamilo.android.framework.service.objects.search.Suggestion;
//import com.bamilo.android.framework.service.objects.search.Suggestions;
//import com.bamilo.android.framework.service.pojo.BaseResponse;
//import com.bamilo.android.framework.service.pojo.RestConstants;
//import com.bamilo.android.framework.service.utils.TextUtils;
//import com.bamilo.android.framework.service.utils.shop.ShopSelector;
//import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
//import com.bamilo.android.R;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * Created by msilva on 2/8/16.
// */
//public class AlgoliaHelper {
//    private static final String TAG = AlgoliaHelper.class.getName();
//    // Algolia Client
//    private APIClient mAlgoliaAPIClient;
//    private final IResponseCallback mIResponseCallback;
//    private final String mNamespacePrefix;
//    private final static String _CATEGORIES = "_categories";
//    private final static String _PRODUCTS_POPULAR = "_products_search";
//    private final static String _SHOPINSHOP = "_shopinshop";
//    private final static String QUERY = "query";
//    private final static String ID_CATALOG_CATEGORY = "id_catalog_category";
//    private final static String OPEN_PARENTISIS = "(";
//    private final static String CLOSE_PARENTISIS = ")";
//    private final static String COMMA = ",";
//    private final static String POINT = ".";
//    private final static String TWO_POINTS = ":";
//
//    private final static int HITS_PER_PAGE = 3;
//    private final static int MAX_NUMBER_OFF_FACETS = 4;
//
//    private SuggestionsStruct mSuggestionsStruct;
//    private final Context mContext;
//
//
//    public AlgoliaHelper(Context context, IResponseCallback responseCallback) {
//        this.mContext = context;
//        this.mIResponseCallback = responseCallback;
//        this.mNamespacePrefix = CountryPersistentConfigs.getAlgoliaInfoByKey(context, Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_PREFIX);
//    }
//
//    public void getSuggestions(@NonNull final String searchQuery){
//        if(getAlgoliaClient() == null){
//            return;
//        }
//
//        mSuggestionsStruct = new SuggestionsStruct();
//        ArrayList<String> attributesToRetrieve = new ArrayList<>();
//        attributesToRetrieve.add(RestConstants.ITEM_SKU);
//        attributesToRetrieve.add(RestConstants.BRAND);
//        attributesToRetrieve.add(RestConstants.LOCALIZABLE_ATTRIBUTES+"."+ ShopSelector.getCountryCode()+"."+RestConstants.NAME);
//
//        ArrayList<String> facets = new ArrayList<>();
//        facets.add(RestConstants.FACET_CATEGORY);
//
//        List<IndexQuery> queries = new ArrayList<>();
//
//        Query queryProductsPopular = new Query(searchQuery)
//                .setHitsPerPage(HITS_PER_PAGE)
//                .setAttributesToRetrieve(attributesToRetrieve)
//                .setFacets(facets)
//                .setMaxNumberOfFacets(MAX_NUMBER_OFF_FACETS)
//                .setAttributesToHighlight(facets);
//
//        queries.add(new IndexQuery(mNamespacePrefix+_PRODUCTS_POPULAR, queryProductsPopular));
//        queries.add(new IndexQuery(mNamespacePrefix+_SHOPINSHOP, new Query(searchQuery).setHitsPerPage(1)));
//
//        mAlgoliaAPIClient.multipleQueriesASync(queries, new APIClientListener() {
//            @Override
//            public void APIResult(APIClient client, TaskParams.Client context, JSONObject result) {
//                BaseResponse response = new BaseResponse();
//                String searchTerm = searchQuery;
//                try {
//                    searchTerm = result.getJSONArray(RestConstants.RESULTS).getJSONObject(1).getString(QUERY);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                final Suggestions suggestions = getProductsAndShopSuggestions(result, searchTerm);
//                mSuggestionsStruct = new SuggestionsStruct(suggestions);
//                mSuggestionsStruct.setSearchParam(searchTerm);
//                response.getMetadata().setData(mSuggestionsStruct);
//
//                getCategoriesNames(result);
//
//                mIResponseCallback.onRequestComplete(response);
//            }
//
//            @Override
//            public void APIError(APIClient client, TaskParams.Client context, AlgoliaException e) {
//                BaseResponse response = new BaseResponse();
//                ArrayList<Suggestion> suggestions = new ArrayList<>();
//                try {
//                    suggestions = SearchRecentQueriesTableHelper.getAllRecentQueries();
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
//                mSuggestionsStruct = new SuggestionsStruct();
//                mSuggestionsStruct.setRecentSuggestions(suggestions);
//                mSuggestionsStruct.setSearchParam(searchQuery);
//                BaseResponse baseResponse = new BaseResponse();
//                baseResponse.getMetadata().setData(mSuggestionsStruct);
//                mIResponseCallback.onRequestComplete(baseResponse);
//            }
//        });
//
//    }
//
//    private void getCategoriesNames(JSONObject result){
//        List<String> facetsFilter =new ArrayList<>();
//        try {
//            JSONArray results = result.getJSONArray(RestConstants.RESULTS);
//            JSONObject facets = results.getJSONObject(0).getJSONObject(RestConstants.FACETS);
//            JSONObject facetCategory = facets.getJSONObject(RestConstants.FACET_CATEGORY);
//
//            Iterator<String> iter = facetCategory.keys();
//            while (iter.hasNext()) {
//                String key = iter.next();
//
//                facetsFilter.add(ID_CATALOG_CATEGORY+TWO_POINTS+key);
//            }
//            String ffacetsFilter = OPEN_PARENTISIS+android.text.TextUtils.join(COMMA,facetsFilter)+CLOSE_PARENTISIS;
//            ArrayList<String> attributesToRetrieve = new ArrayList<>();
//            attributesToRetrieve.add(RestConstants.LOCALIZABLE_ATTRIBUTES+POINT+ ShopSelector.getCountryCode()+POINT+RestConstants.NAME);
//
//            Query q = new Query()
//                    .setAttributesToRetrieve(attributesToRetrieve)
//                    .setFacetFilters(ffacetsFilter);
//            List<IndexQuery> queries = new ArrayList<>();
//
//            IndexQuery iq = new IndexQuery(mNamespacePrefix+_CATEGORIES, q);
//            queries.add(iq);
//            mAlgoliaAPIClient.multipleQueriesASync(queries, new APIClientListener() {
//                @Override
//                public void APIResult(APIClient client, TaskParams.Client context, JSONObject result) {
//                    BaseResponse response = new BaseResponse();
//
//                    final Suggestions suggestions = getCategoriesSuggestions(result, mSuggestionsStruct.getSearchParam());
//                    mSuggestionsStruct.setSuggestions(suggestions);
//                    response.getMetadata().setData(mSuggestionsStruct);
//                    mIResponseCallback.onRequestComplete(response);
//                }
//
//                @Override
//                public void APIError(APIClient client, TaskParams.Client context, AlgoliaException e) {
//                    BaseResponse response = new BaseResponse();
//                    response.getMetadata().setData(null);
//                    mIResponseCallback.onRequestError(response);
//                }
//            });
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    private Suggestions getProductsAndShopSuggestions(JSONObject response, final String query){
//        final Suggestions suggestions = new Suggestions();
//        try {
//            JSONArray results = response.getJSONArray(RestConstants.RESULTS);
//
//            JSONArray shopHits = results.getJSONObject(1).getJSONArray(RestConstants.HITS);
//
//            for (int j = 0; j <  shopHits.length(); j++){
//                Suggestion suggestion = new Suggestion();
//                JSONObject shop = shopHits.getJSONObject(j);
//                String name = shop.getString(RestConstants.DOMAIN);
//                String target = shop.optString(RestConstants.POINTER);
//                final String capitalizedName = name.substring(0,1).toUpperCase() + name.substring(1);
//                suggestion.setQuery(query);
//                suggestion.setResult(capitalizedName);
//                suggestion.setTarget(TextUtils.isNotEmpty(target) ? target : name);
//                suggestion.setType(Suggestion.SUGGESTION_SHOP_IN_SHOP);
//                suggestions.add(suggestion);
//            }
//
//            JSONArray hits = results.getJSONObject(0).getJSONArray(RestConstants.HITS);
//            for (int i = 0; i <  hits.length(); i++){
//                Suggestion suggestion = new Suggestion();
//                JSONObject product = hits.getJSONObject(i);
//                String name = product.getJSONObject(RestConstants.LOCALIZABLE_ATTRIBUTES).getJSONObject(ShopSelector.getCountryCode()).getString(RestConstants.NAME);
//                String brand = product.getJSONObject(RestConstants.BRAND).getString(RestConstants.NAME);
//                suggestion.setQuery(query);
//                suggestion.setResult(String.format(mContext.getString(R.string.first_space_second_placeholder),brand,  name));
//                suggestion.setTarget(product.getString(RestConstants.ITEM_SKU));
//                suggestion.setType(Suggestion.SUGGESTION_PRODUCT);
//                suggestions.add(suggestion);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return suggestions;
//    }
//
//    private Suggestions getCategoriesSuggestions(JSONObject response, final String searchTerm){
//        final Suggestions suggestions = new Suggestions();
//        try {
//            JSONArray results = response.getJSONArray(RestConstants.RESULTS);
//            JSONArray hits = results.getJSONObject(0).getJSONArray(RestConstants.HITS);
//            for (int i = 0; i <  hits.length(); i++){
//                Suggestion suggestion = new Suggestion();
//                JSONObject category = hits.getJSONObject(i).getJSONObject(RestConstants.LOCALIZABLE_ATTRIBUTES).getJSONObject(ShopSelector.getCountryCode());
//                String name = category.getString(RestConstants.NAME);
//                String target = category.getString(RestConstants.URL_KEY);
//                suggestion.setQuery(searchTerm);
//                suggestion.setResult(name);
//                suggestion.setTarget(target);
//                suggestion.setType(Suggestion.SUGGESTION_CATEGORY);
//                suggestions.add(suggestion);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return suggestions;
//    }
//
//    private APIClient getAlgoliaClient() {
//        if(mAlgoliaAPIClient == null){
//            mAlgoliaAPIClient = new APIClient(CountryPersistentConfigs.getAlgoliaInfoByKey(mContext, Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_APP_ID), CountryPersistentConfigs.getAlgoliaInfoByKey(mContext, Darwin.KEY_SELECTED_COUNTRY_ALGOLIA_API_KEY));
//        }
//        return mAlgoliaAPIClient;
//    }
//
//}

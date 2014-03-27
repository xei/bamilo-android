///**
// * Service responsible handling all the requests that involve products.
// * This includes getting suggestions when searching products, getting product listings, and getting the product details.
// * 
// * @author Guilherme Silva
// * 
// * @version 1.01
// * 
// * 2012/06/18
// * 
// * Copyright (c) Rocket Internet All Rights Reserved
// */
//package pt.rocket.helpers;
//
//import java.util.ArrayList;
//import java.util.EnumSet;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import pt.rocket.framework.utils.ProductSort;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import pt.rocket.framework.enums.RequestType;
//import pt.rocket.framework.objects.CompleteProduct;
//import pt.rocket.framework.objects.ProductRatingPage;
//import pt.rocket.framework.objects.ProductsPage;
//import pt.rocket.framework.objects.SearchSuggestion;
//import pt.rocket.framework.rest.RestConstants;
//import pt.rocket.framework.rest.RestContract;
//import pt.rocket.framework.utils.Constants;
//import pt.rocket.framework.utils.Direction;
//import pt.rocket.framework.utils.Utils;
//import android.content.ContentValues;
//import android.net.Uri;
//import android.net.Uri.Builder;
//import android.os.Bundle;
//import android.text.TextUtils;
//import de.akquinet.android.androlog.Log;
//
///**
// * Product service responsible for all the events related to the product such as the
// * GET_PRODUCTS_EVENT, GET_TOP_BRANDS_EVENT, GET_BEST_SELLER_LIST_EVENT.
// * 
// * @author GuilhermeSilva
// * 
// */
//public class ProductHelper {
//
//    private static final String TAG = ProductHelper.class.getSimpleName();
//
//    private HashMap<String, HashMap<String, String>> ratingOptions = null;
//    private CompleteProduct currentProduct = null;
//
//    /**
//	 * 
//	 */
//    public ProductHelper() {
//        // super(EnumSet.noneOf(EventType.class), EnumSet
//        // .of(EventType.INIT_SHOP, EventType.GET_PRODUCTS_EVENT, EventType.GET_PRODUCT_EVENT,
//        // EventType.GET_SEARCH_SUGGESTIONS_EVENT,
//        // EventType.GET_PRODUCT_REVIEWS_EVENT, EventType.REVIEW_PRODUCT_EVENT,
//        // EventType.GET_RATING_OPTIONS_EVENT));
//    }
//
//    /**
//     * Gets the complete product for a certain url. This function tries to get the product from the
//     * product cache. If it succeds, than it triggers the GetProductcompletedEvent. If not, if gets
//     * the product from the api, then it registers the product and then it triggers the
//     * GetProductCompleteEvent.
//     * 
//     * @param event
//     *            url of the product.
//     */
//    public Bundle getProduct(Bundle bundle) {
//        Log.d(TAG, "going to get product from " + event.value);
//        RestServiceHelper.requestGet(event.value, new ResponseReceiver<CompleteProduct>(event) {
//
//            @Override
//            public CompleteProduct parseResponse(JSONObject metadataObject) throws JSONException {
//                currentProduct = new CompleteProduct();
//                currentProduct.initialize(metadataObject);
//                return currentProduct;
//            }
//        }, event.metaData);
//        // }
//    }
//
//    // /**
//    // * Function that fetches the all the fields suggestions for a certain query.
//    // * Warning. the query must have at least 1 character.
//    // *
//    // * @param event
//    // */
//    // public static void getSearchAutoComplete(final GetSearchSuggestionsEvent event) {
//    //
//    // Uri uri = Uri.parse(event.eventType.action).buildUpon().appendQueryParameter("q",
//    // event.value).build();
//    //
//    // RestServiceHelper.requestGet(uri, new ResponseReceiver<List<SearchSuggestion>>(event) {
//    //
//    // public List<SearchSuggestion> parseResponse(JSONObject metadataObject) throws JSONException {
//    // ArrayList<SearchSuggestion> suggestions = new ArrayList<SearchSuggestion>();
//    //
//    // JSONArray suggestionsArray = metadataObject.getJSONArray(RestConstants.JSON_SUGGESTIONS_TAG);
//    // for (int i = 0; i < suggestionsArray.length(); ++i) {
//    // SearchSuggestion suggestion = new SearchSuggestion();
//    // suggestion.initialize(suggestionsArray.getJSONObject(i));
//    // suggestions.add(suggestion);
//    // }
//    // return suggestions;
//    // }
//    // }, event.metaData);
//    // }
//
//    // public static void getProducts(final GetProductsEvent event) {
//    // Uri productsUri;
//    // if (TextUtils.isEmpty(event.productUrl)) {
//    // productsUri = Uri.parse(event.eventType.action);
//    // } else {
//    // productsUri = Uri.parse(event.productUrl);
//    // }
//    // Builder uriBuilder = productsUri.buildUpon();
//    //
//    // if (!TextUtils.isEmpty(event.searchQuery)) {
//    // uriBuilder.appendQueryParameter("q", event.searchQuery);
//    // }
//    //
//    // if (event.pageNumber > 0)
//    // uriBuilder.appendQueryParameter("page", "" + event.pageNumber);
//    //
//    // if (event.totalCount > 0)
//    // uriBuilder.appendQueryParameter("maxitems", "" + event.totalCount);
//    //
//    // if (event.sort != ProductSort.NONE) {
//    // String sortString = "";
//    // switch (event.sort) {
//    // case ProductSort.POPULARITY:
//    // // url.append("sort=popularity&");
//    // sortString = "popularity";
//    // break;
//    // case ProductSort.NAME:
//    // // url.append("sort=name&");
//    // sortString = "name";
//    // break;
//    // case ProductSort.PRICE:
//    // // url.append("sort=price&");
//    // sortString = "price";
//    // break;
//    // case ProductSort.BRAND:
//    // // url.append("sort=brand&");
//    // sortString = "brand";
//    // break;
//    // }
//    // if (!TextUtils.isEmpty(sortString)) {
//    // uriBuilder.appendQueryParameter("sort", "" + sortString);
//    // }
//    //
//    // sortString = "";
//    // switch (event.direction) {
//    // case Direction.ASCENDENT:
//    // // url.append("dir=asc");
//    // sortString = "asc";
//    // break;
//    // case Direction.DESCENDENT:
//    // // url.append("dir=desc");
//    // sortString = "desc";
//    // break;
//    // }
//    // if (!TextUtils.isEmpty(sortString)) {
//    // uriBuilder.appendQueryParameter("dir", "" + sortString);
//    // }
//    //
//    // }
//    //
//    // RestServiceHelper.requestGet(uriBuilder.build(), new ResponseReceiver<ProductsPage>(event) {
//    //
//    // @Override
//    // public ProductsPage parseResponse(JSONObject metadataObject) throws JSONException {
//    // ProductsPage products = new ProductsPage();
//    // products.initialize(metadataObject);
//    // return products;
//    // }
//    // }, event.metaData);
//    // }
//
//    /**
//     * Gets the rating options and triggers the GetRatingOptionsCompletedEvent.
//     * 
//     * @param event
//     */
//    public HashMap<String, HashMap<String, String>> getRatingOptions() {// final RequestEvent event)
//                                                                        // {
//
//        // if (null == ratingOptions) {
//        //
//        // RestServiceHelper.requestGet(event.eventType.action, new ResponseReceiver<HashMap<String,
//        // HashMap<String, String>>>(event) {
//        //
//        // @Override
//        // public HashMap<String, HashMap<String, String>> parseResponse(JSONObject metadataObject)
//        // throws JSONException {
//        // ratingOptions = new HashMap<String, HashMap<String, String>>();
//        // JSONArray dataArray = metadataObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//        // JSONObject ratingOption = null;
//        // JSONObject optionObject = null;
//        // HashMap<String, String> option;
//        // int size = dataArray.length();
//        // int optionsSize = 0;
//        // for (int i = 0; i < size; i++) {
//        // ratingOption = dataArray.getJSONObject(i);
//        // JSONArray optionsArray = ratingOption.getJSONArray(RestConstants.JSON_OPTIONS_TAG);
//        // option = new HashMap<String, String>();
//        // optionsSize = optionsArray.length();
//        // for (int k = 0; k < optionsSize; k++) {
//        // optionObject = optionsArray.getJSONObject(k);
//        // option.put(optionObject.getString(RestConstants.JSON_PROD_VALUE_TAG),
//        // optionObject.getString(RestConstants.JSON_ID_RATING_OPTION_TAG));
//        // }
//        // ratingOptions.put(ratingOption.getString(RestConstants.JSON_CODE_TAG), option);
//        // }
//        // return ratingOptions;
//        // }
//        // }, event.metaData);
//        //
//        // } else {
//        // sendRatingOptionsResult(event);
//        // }
//        return ratingOptions;
//    }
//
//    //
//    // private void sendRatingOptionsResult(RequestEvent event) {
//    // EventManager.getSingleton().triggerResponseEvent(
//    // new ResponseResultEvent<Map<String, ? extends Map<String, String>>>(event, ratingOptions,
//    // null, new Bundle()));
//    // }
//
//    // /**
//    // * Gets the products review for a certain product.
//    // *
//    // * @param productURL
//    // * @param pageNumber
//    // */
//    // public static void getProductReviews(final GetProductReviewsEvent event) {
//    //
//    // Uri uri =
//    // Uri.parse(event.productUrl).buildUpon().appendQueryParameter(RestContract.REST_PARAM_RATING,
//    // "1")
//    // .appendQueryParameter(RestContract.REST_PARAM_PAGE,
//    // String.valueOf(event.pageNumber)).build();
//    //
//    // RestServiceHelper.requestGet(uri, new ResponseReceiver<ProductRatingPage>(event) {
//    //
//    // @Override
//    // public ProductRatingPage parseResponse(JSONObject metadataObject) throws JSONException {
//    // JSONObject dataObject = metadataObject.getJSONObject(RestConstants.JSON_DATA_TAG);
//    // ProductRatingPage rating = new ProductRatingPage();
//    // rating.initialize(dataObject);
//    // return rating;
//    // }
//    // }, event.metaData);
//    // }
//
//    /**
//     * Inserts a product review in the server and triggers the ReviewProductCompletedEvent.
//     * 
//     * @param productSKU
//     *            Sku of the product.
//     * @param productReviewCommentCreated
//     *            Object with the comment data.
//     */
//    public void reviewProduct(final ReviewProductEvent event) {
//
//        // ContentValues values = new ContentValues();
//        // event.productReviewCreated.addParameters(values);
//        // values.put(RestConstants.REVIEW_PRODUCT_SKU_FIELD, event.productSKU);
//        // if (event.customerId != -1) {
//        // values.put(RestConstants.REVIEW_CUSTOMER_ID, event.customerId);
//        // }
//        //
//        // for (Entry<String, HashMap<String, String>> option : ratingOptions.entrySet()) {
//        //
//        // values.put(RestConstants.REVIEW_OPTION_FIELD + option.getKey(),
//        // option.getValue().get(String.valueOf(event.productReviewCreated.getRating().get(option.getKey()).intValue())));
//        //
//        // }
//        //
//        // RestServiceHelper.requestPost(event.eventType.action, values, new
//        // ResponseReceiver<Void>(event) {
//        //
//        // @Override
//        // public Void parseResponse(JSONObject response) {
//        // return null;
//        // }
//        // }, event.metaData);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework .event.IEvent)
//     */
//
//    public Bundle handleEvent(EventType eventType, Bundle bundle) {
//        Bundle responseBundle;
//        switch (eventType) {
//        case INIT_SHOP:
//            ratingOptions = null;
//            break;
//        case GET_PRODUCTS_EVENT:
//            responseBundle = getProducts(bundle);
//            break;
//        case GET_PRODUCT_EVENT:
//            responseBundle = getProduct(bundle);
//            break;
//        case GET_SEARCH_SUGGESTIONS_EVENT:
//            responseBundle = getSearchAutoComplete(bundle);
//            break;
//        case GET_PRODUCT_REVIEWS_EVENT:
//            responseBundle = getProductReviews(bundle);
//            break;
//        case REVIEW_PRODUCT_EVENT:
//            responseBundle = reviewProduct(bundle);
//            break;
//        case GET_RATING_OPTIONS_EVENT:
//            responseBundle = getRatingOptions(bundle);
//            break;
//        }
//    }
//}
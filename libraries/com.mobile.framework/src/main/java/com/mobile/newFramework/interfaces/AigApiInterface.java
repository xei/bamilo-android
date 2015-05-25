package com.mobile.newFramework.interfaces;

import com.mobile.newFramework.objects.CompleteProduct;
import com.mobile.newFramework.objects.HomePageObject;
import com.mobile.newFramework.objects.AvailableCountries;
import com.mobile.newFramework.objects.CountryConfigs;
import com.mobile.newFramework.objects.Sections;
import com.mobile.newFramework.pojo.BaseResponse;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;


public interface AigApiInterface {

    /*
     * ## CONFIGS
     */

    @GET("/")
    void getAvailableCountries(Callback<BaseResponse<AvailableCountries>> callback);

    @GET("/")
    void getCountryConfigurations(Callback<BaseResponse<CountryConfigs>> callback);

    @GET("/")
    void getApiInformation(Callback<BaseResponse<Sections>> callback);

    @GET("/")
    void getImageResolutions(Callback<BaseResponse> callback);

    /*
     * ## FORMS
     */

    @GET("/forms/index")
    void getFormsIndex(Callback<BaseResponse> callback);

    /*
     * ## CATALOG
     */

    @GET("/")
    void getCatalogFiltered(@QueryMap Map<String, String> data, Callback<BaseResponse> callback);

    /*
     * ## CATEGORIES
     */

    @GET("/")
    void getCategoriesPaginated(@QueryMap Map<String, String> data, Callback<BaseResponse> callback);

    /*
     * ## HOME
     */

    @GET("/")
    void getHome(Callback<BaseResponse<HomePageObject>> callback);

    /*
     * ## SHOP IN SHOP
     */

    @GET("/")
    void getShopInShop(@QueryMap Map<String, String> data, Callback<BaseResponse<HomePageObject>> callback);

    /*
     * ## CAMPAIGN
     */

    @GET("/")
    void getCampaign(@QueryMap Map<String, String> data, Callback<BaseResponse> callback);

    /*
     * ## PRODUCT
     */

    @GET("/")
    void getProductDetail(Callback<BaseResponse<CompleteProduct>> callback);

    /*
     * ## SEARCH SUGGESTIONS
     */

    @GET("/search/suggest")
    void getSearchSuggestions(Callback<BaseResponse> callback);

    /*
     * ## CART
     */

    @GET("/")
    void getShoppingCart(Callback<BaseResponse> callback);

    /*
     * ## SESSION
     */

    @FormUrlEncoded
    @POST("/")
    void loginCustomer(@FieldMap Map<String, String> data , Callback<BaseResponse> callback);


}

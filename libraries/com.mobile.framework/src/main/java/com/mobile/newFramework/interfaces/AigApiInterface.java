package com.mobile.newFramework.interfaces;

import com.mobile.framework.objects.CountryConfigs;
import com.mobile.newFramework.pojo.BaseResponse;

import retrofit.Callback;
import retrofit.http.GET;


public interface AigApiInterface {

    /*
     * ## CONFIGS
     */

    @GET("/availablecountries")
    void getAvailableCountries(Callback<BaseResponse> callback);

    @GET("/main/md5/")
    void getApiInformation(Callback<BaseResponse> callback);

    @GET("/main/imageresolutions/")
    void getImageResolutions(Callback<BaseResponse> callback);

    @GET("/main/getstatic?key=terms_mobile")
    void getTermsConditions(Callback<BaseResponse> callback);

    /*
     * ## FORMS
     */

    @GET("/forms/index/")
    void getFormsIndex(Callback<BaseResponse> callback);

    /*
     * ## CATALOG
     */

    @GET("/catalog/categories/")
    void getCatalog(Callback<BaseResponse> callback);

    @GET("/search/")
    void getSearchCatalog(Callback<BaseResponse> callback);

    /*
     * ## HOME
     */

    @GET("/main/home/")
    void getHome(Callback<BaseResponse> callback);

    /*
     * ## PRODUCT
     */

    @GET("{request}")
    void getProduct(Callback<BaseResponse> callback);

    /*
     * ## SEARCH SUGGESTIONS
     */

    @GET("/search/suggest/")
    void getSearchSuggestions(Callback<BaseResponse> callback);

    /*
     * ## CART
     */

    @GET("/order/cartdata/")
    void getCart(Callback<BaseResponse> callback);

    @GET("/getconfigurations")
    void getCountryConfigurations(Callback<BaseResponse<CountryConfigs>> callback);

}

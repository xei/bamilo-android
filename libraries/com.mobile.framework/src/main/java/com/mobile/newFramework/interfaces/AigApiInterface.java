package com.mobile.newFramework.interfaces;

import com.mobile.newFramework.objects.AvailableCountries;
import com.mobile.newFramework.objects.CountryConfigs;
import com.mobile.newFramework.objects.Sections;
import com.mobile.newFramework.pojo.BaseResponse;

import retrofit.Callback;
import retrofit.http.GET;


public interface AigApiInterface {

    /*
     * ## CONFIGS
     */

    @GET("/availablecountries")
    void getAvailableCountries(Callback<BaseResponse<AvailableCountries>> callback);

    @GET("/main/getconfigurations")
    void getCountryConfigurations(Callback<BaseResponse<CountryConfigs>> callback);

    @GET("/main/md5/")
    void getApiInformation(Callback<BaseResponse<Sections>> callback);

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

}

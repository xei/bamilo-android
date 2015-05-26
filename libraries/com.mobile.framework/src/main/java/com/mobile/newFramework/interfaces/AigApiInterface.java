package com.mobile.newFramework.interfaces;

import com.mobile.newFramework.objects.AvailableCountries;
import com.mobile.newFramework.objects.Campaign;
import com.mobile.newFramework.objects.CatalogPage;
import com.mobile.newFramework.objects.CompleteProduct;
import com.mobile.newFramework.objects.CountryConfigs;
import com.mobile.newFramework.objects.Customer;
import com.mobile.newFramework.objects.HomePageObject;
import com.mobile.newFramework.objects.Sections;
import com.mobile.newFramework.objects.ShoppingCart;
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

    @GET("/")
    void getFormsIndex(Callback<BaseResponse> callback);

    /*
     * ## CATALOG
     */

    @GET("/")
    void getCatalogFiltered(@QueryMap Map<String, String> data, Callback<BaseResponse<CatalogPage>> callback);

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
    void getCampaign(@QueryMap Map<String, String> data, Callback<BaseResponse<Campaign>> callback);

    /*
     * ## PRODUCT
     */

    @GET("/")
    void getProductDetail(Callback<BaseResponse<CompleteProduct>> callback);

    @GET("/")
    void getProductBundle(Callback<BaseResponse<BaseResponse>> callback);

    @GET("/")
    void getProductReviews(@QueryMap Map<String, String> data, Callback<BaseResponse<BaseResponse>> callback);

    @GET("/")
    void getProductOffers(@QueryMap Map<String, String> data, Callback<BaseResponse<BaseResponse>> callback);

    /*
     * ## SEARCH SUGGESTIONS
     */

    @GET("/search/suggest")
    void getSearchSuggestions(Callback<BaseResponse> callback);

    /*
     * ## CART
     */

    @GET("/")
    void getShoppingCart(Callback<BaseResponse<ShoppingCart>> callback);

    @FormUrlEncoded
    @POST("/")
    void addItemShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    @FormUrlEncoded
    @POST("/")
    void addBundleShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    @FormUrlEncoded
    @POST("/")
    void addMultipleItemsShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    @FormUrlEncoded
    @POST("/")
    void updateQuantityShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    @FormUrlEncoded
    @POST("/")
    void removeAllShoppingCart(Callback<BaseResponse<ShoppingCart>> callback);

    @FormUrlEncoded
    @POST("/")
    void removeItemShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    /*
     * ## VOUCHER
     */

    @FormUrlEncoded
    @POST("/")
    void addVoucher(@FieldMap Map<String, String> data, Callback<BaseResponse<BaseResponse>> callback);

    @GET("/")
    void removeVoucher(Callback<BaseResponse<BaseResponse>> callback);

    /*
     * ## SESSION
     */

    @GET("/")
    void logoutCustomer(Callback<BaseResponse<BaseResponse>> callback);

    @FormUrlEncoded
    @POST("/")
    void loginCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    @FormUrlEncoded
    @POST("/")
    void loginFacebookCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    @FormUrlEncoded
    @POST("/")
    void registerCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    @FormUrlEncoded
    @POST("/")
    void signUpCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

}

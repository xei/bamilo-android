package com.mobile.newFramework.interfaces;

import com.mobile.newFramework.forms.SuperForm;
import com.mobile.newFramework.forms.SuperFormData;
import com.mobile.newFramework.objects.configs.ApiInformation;
import com.mobile.newFramework.objects.configs.AvailableCountries;
import com.mobile.newFramework.objects.campaign.Campaign;
import com.mobile.newFramework.objects.catalog.CatalogPage;
import com.mobile.newFramework.objects.Categories;
import com.mobile.newFramework.objects.CompleteProduct;
import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.newFramework.objects.user.Customer;
import com.mobile.newFramework.objects.ProductBundle;
import com.mobile.newFramework.objects.ProductOffers;
import com.mobile.newFramework.objects.ProductRatingPage;
import com.mobile.newFramework.objects.ShoppingCart;
import com.mobile.newFramework.objects.Voucher;
import com.mobile.newFramework.objects.home.HomePageObject;
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
    void getApiInformation(Callback<BaseResponse<ApiInformation>> callback);

    @GET("/")
    void getImageResolutions(Callback<BaseResponse> callback);

    /*
     * ## FORMS
     */

    @GET("/")
    void getLoginForm(Callback<BaseResponse<SuperForm>> callback);

    @GET("/")
    void getFormsIndex(Callback<BaseResponse<SuperFormData>> callback);

    @GET("/")
    void getRatingForm(Callback<BaseResponse<SuperForm>> callback);

    @GET("/")
    void getReviewForm(Callback<BaseResponse<SuperForm>> callback);

    @GET("/")
    void getSellerReviewForm(Callback<BaseResponse<SuperForm>> callback);

    @GET("/")
    void getRegisterForm(Callback<BaseResponse<SuperForm>> callback);

    @GET("/")
    void getSignupForm(Callback<BaseResponse<SuperForm>> callback);

    @GET("/")
    void getForgotPasswordForm(Callback<BaseResponse<SuperForm>> callback);
    /*
     * ## CATALOG
     */

    @GET("/")
    void getCatalogFiltered(@QueryMap Map<String, String> data, Callback<BaseResponse<CatalogPage>> callback);

    /*
     * ## CATEGORIES
     */

    @GET("/")
    void getCategoriesPaginated(@QueryMap Map<String, String> data, Callback<BaseResponse<Categories>> callback);

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
    void getProductBundle(Callback<BaseResponse<ProductBundle>> callback);

    @GET("/")
    void getProductReviews(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductRatingPage>> callback);

    @GET("/")
    void getProductOffers(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductOffers>> callback);

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
    void addVoucher(@FieldMap Map<String, String> data, Callback<BaseResponse<Voucher>> callback);

    @GET("/")
    void removeVoucher(Callback<BaseResponse<Voucher>> callback);

    /*
     * ## SESSION
     */

    @GET("/")
    void logoutCustomer(Callback<BaseResponse<Void>> callback);

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
    
    /*
     * ## RATINGS/REVIEWS
     */
    @GET("/")
    void getReviews(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductRatingPage>> callback);

    @FormUrlEncoded
    @POST("/")
    void setRatingReview(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    @FormUrlEncoded
    @POST("/")
    void setSellerReview(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

}

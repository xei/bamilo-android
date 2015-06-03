package com.mobile.newFramework.interfaces;

import com.mobile.framework.objects.Promotion;
import com.mobile.framework.objects.StaticPage;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormsIndex;
import com.mobile.newFramework.objects.AddressCities;
import com.mobile.newFramework.objects.AddressRegions;
import com.mobile.newFramework.objects.Addresses;
import com.mobile.newFramework.objects.OrderTracker;
import com.mobile.newFramework.objects.SuperGetBillingForm;
import com.mobile.newFramework.objects.SuperOrder;
import com.mobile.newFramework.objects.SuperSetBillingAddress;
import com.mobile.newFramework.objects.Voucher;
import com.mobile.newFramework.objects.campaign.Campaign;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.objects.catalog.Catalog;
import com.mobile.newFramework.objects.category.Categories;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.checkout.CheckoutStepObject;
import com.mobile.newFramework.objects.checkout.SuperCheckoutFinish;
import com.mobile.newFramework.objects.checkout.SuperGetPaymentMethodsForm;
import com.mobile.newFramework.objects.checkout.SuperGetShippingMethodsForm;
import com.mobile.newFramework.objects.checkout.SuperNativeCheckoutAvailability;
import com.mobile.newFramework.objects.checkout.SuperSetPaymentMethod;
import com.mobile.newFramework.objects.checkout.SuperSetShippingMethod;
import com.mobile.newFramework.objects.configs.ApiInformation;
import com.mobile.newFramework.objects.configs.AvailableCountries;
import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.newFramework.objects.home.HomePageObject;
import com.mobile.newFramework.objects.product.CompleteProduct;
import com.mobile.newFramework.objects.product.ProductBundle;
import com.mobile.newFramework.objects.product.ProductOffers;
import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.objects.product.SuperValidProducts;
import com.mobile.newFramework.objects.search.Suggestions;
import com.mobile.newFramework.objects.user.Customer;
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

    @GET("/")
    void getTermsAndConditions(@QueryMap Map<String, String> data, Callback<BaseResponse<BaseResponse>> callback);

    @GET("/")
    void getPromotions(@QueryMap Map<String, String> data, Callback<BaseResponse<Promotion>> callback);

    /*
     * ## FORMS
     */

    @GET("/")
    void getLoginForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getFormsIndex(Callback<BaseResponse<FormsIndex>> callback);

    @GET("/")
    void getRatingForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getReviewForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getSellerReviewForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getRegisterForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getSignUpForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getForgotPasswordForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getCreateAddressForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getEditAddressForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getNewsletterForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getShippingMethodsForm(Callback<BaseResponse<SuperGetShippingMethodsForm>> callback);

    @GET("/")
    void getPaymentMethodsForm(Callback<BaseResponse<SuperGetPaymentMethodsForm>> callback);

    @GET("/")
    void getBillingForm(Callback<BaseResponse<BaseResponse>> callback);


    /*
     * ## CATALOG
     */

    @GET("/")
    void getCatalogFiltered(@QueryMap Map<String, String> data, Callback<BaseResponse<Catalog>> callback);

    @GET("/")
    void searchSku(@QueryMap Map<String, String> data, Callback<BaseResponse<CompleteProduct>> callback);

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
    void getShopInShop(@QueryMap Map<String, String> data, Callback<BaseResponse<StaticPage>> callback);

    /*
     * ## CAMPAIGN
     */

    @GET("/")
    void getCampaign(@QueryMap Map<String, String> data, Callback<BaseResponse<Campaign>> callback);

    /*
     * ## PRODUCT
     */

    @GET("/")
    void searchProductDetail(@QueryMap Map<String, String> data, Callback<BaseResponse<CompleteProduct>> callback);

    @GET("/")
    void getProductDetail(Callback<BaseResponse<CompleteProduct>> callback);

    @GET("/")
    void getProductBundle(Callback<BaseResponse<ProductBundle>> callback);

    @GET("/")
    void getProductOffers(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductOffers>> callback);

    @FormUrlEncoded
    @POST("/")
    void validateProducts(@FieldMap Map<String, String> data, Callback<BaseResponse<SuperValidProducts>> callback);

    /*
     * ## SEARCH SUGGESTIONS
     */

    @GET("/")
    void getSearchSuggestions(@QueryMap Map<String, String> data, Callback<BaseResponse<Suggestions>> callback);

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
    void removeVoucher(Callback<BaseResponse<ShoppingCart>> callback);

    /*
     * ## SESSION
     */

    @GET("/")
    void logoutCustomer(Callback<BaseResponse<Void>> callback);

    @FormUrlEncoded
    @POST("/")
    void loginCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepLogin>> callback);

    @FormUrlEncoded
    @POST("/")
    void loginFacebookCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    @FormUrlEncoded
    @POST("/")
    void registerCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    @FormUrlEncoded
    @POST("/")
    void signUpCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    @FormUrlEncoded
    @POST("/")
    void forgotPassword(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    @FormUrlEncoded
    @POST("/")
    void changePassword(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    @FormUrlEncoded
    @POST("/")
    void subscribeNewsletter(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    @GET("/")
    void getCustomerDetails(Callback<BaseResponse<Customer>> callback);

    @GET("/")
    void getAddressesList(Callback<BaseResponse<Addresses>> callback);

    @FormUrlEncoded
    @POST("/")
    void createAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepObject>> callback);

    @FormUrlEncoded
    @POST("/")
    void editAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    @FormUrlEncoded
    @POST("/")
    void setDefaultShippingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse> callback);

    @FormUrlEncoded
    @POST("/")
    void setDefaultBillingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse> callback);

    @GET("/")
    void getBillingAddressForm(Callback<BaseResponse<SuperGetBillingForm>> callback);

    @FormUrlEncoded
    @POST("/")
    void setBillingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<SuperSetBillingAddress>> callback);

    @GET("/")
    void getRegions(Callback<BaseResponse<AddressRegions>> callback);

    @GET("/")
    void getCities(@QueryMap Map<String, String> data, Callback<BaseResponse<AddressCities>> callback);

    /*
     * ## RATINGS/REVIEWS
     */
    @GET("/")
    void getProductReviews(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductRatingPage>> callback);

    @FormUrlEncoded
    @POST("/")
    void setRatingReview(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    @FormUrlEncoded
    @POST("/")
    void setSellerReview(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    @GET("/")
    void getSellerReviews(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductRatingPage>> callback);


    /*
    * ## ORDERS
    */
    @GET("/")
    void trackOrder(@QueryMap Map<String, String> data, Callback<BaseResponse<OrderTracker>> callback);

    @GET("/")
    void getOrdersList(@QueryMap Map<String, String> data, Callback<BaseResponse<SuperOrder>> callback);

    /*
    * ## CHECKOUT
    */

    @GET("/")
    void getNativeCheckoutAvailable(Callback<BaseResponse<BaseResponse<SuperNativeCheckoutAvailability>>> callback);

    @GET("/")
    void checkoutFinishOrder(@QueryMap Map<String, String> data, Callback<BaseResponse<BaseResponse>> callback);

    @FormUrlEncoded
    @POST("/")
    void setShippingMethod(@FieldMap Map<String, String> data, Callback<BaseResponse<SuperSetShippingMethod>> callback);

    @FormUrlEncoded
    @POST("/")
    void setPaymentMethod(@FieldMap Map<String, String> data, Callback<BaseResponse<SuperSetPaymentMethod>> callback);

    @FormUrlEncoded
    @POST("/")
    void checkoutFinish(@FieldMap Map<String, String> data, Callback<BaseResponse<SuperCheckoutFinish>> callback);

}

package com.mobile.newFramework.rest.interfaces;

import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormsIndex;
import com.mobile.newFramework.objects.addresses.AddressCities;
import com.mobile.newFramework.objects.addresses.AddressRegions;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.objects.campaign.Campaign;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.objects.catalog.Catalog;
import com.mobile.newFramework.objects.category.Categories;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.checkout.CheckoutStepObject;
import com.mobile.newFramework.objects.checkout.CheckoutStepSignUp;
import com.mobile.newFramework.objects.checkout.SuperCheckoutFinish;
import com.mobile.newFramework.objects.checkout.SuperGetBillingForm;
import com.mobile.newFramework.objects.checkout.SuperGetPaymentMethodsForm;
import com.mobile.newFramework.objects.checkout.SuperGetShippingMethodsForm;
import com.mobile.newFramework.objects.checkout.SuperNativeCheckoutAvailability;
import com.mobile.newFramework.objects.checkout.SuperSetBillingAddress;
import com.mobile.newFramework.objects.checkout.SuperSetPaymentMethod;
import com.mobile.newFramework.objects.checkout.SuperSetShippingMethod;
import com.mobile.newFramework.objects.configs.ApiInformation;
import com.mobile.newFramework.objects.configs.AvailableCountries;
import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.home.HomePageObject;
import com.mobile.newFramework.objects.orders.OrderTracker;
import com.mobile.newFramework.objects.orders.SuperOrder;
import com.mobile.newFramework.objects.product.CompleteProduct;
import com.mobile.newFramework.objects.product.ProductBundle;
import com.mobile.newFramework.objects.product.ProductOffers;
import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.objects.product.SuperValidProducts;
import com.mobile.newFramework.objects.search.Suggestions;
import com.mobile.newFramework.objects.statics.StaticPage;
import com.mobile.newFramework.objects.statics.StaticTermsConditions;
import com.mobile.newFramework.objects.voucher.Voucher;
import com.mobile.newFramework.pojo.BaseResponse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;


public interface AigApiInterface {

    Map<String, Method> methods = new HashMap<>();

    class Service {
        public static void init() {
            if (methods.isEmpty()) {
                for (Method method : AigApiInterface.class.getMethods()) {
                    methods.put(method.getName(), method);
                }
            }
        }

        public static Method getMethod(String name) {
            return methods.get(name);
        }
    }

    /*
     * ## CONFIGS
     */

    @GET("/")
    void getAvailableCountries(Callback<BaseResponse<AvailableCountries>> callback);

    String getAvailableCountries = "getAvailableCountries";

    @GET("/")
    void getCountryConfigurations(Callback<BaseResponse<CountryConfigs>> callback);

    String getCountryConfigurations = "getCountryConfigurations";

    @GET("/")
    void getApiInformation(Callback<BaseResponse<ApiInformation>> callback);

    String getApiInformation = "getApiInformation";

    @GET("/")
    void getImageResolutions(Callback<BaseResponse> callback);

    String getImageResolutions = "getImageResolutions";

    @GET("/")
    void getTermsAndConditions(@QueryMap Map<String, String> data, Callback<BaseResponse<StaticTermsConditions>> callback);

    String getTermsAndConditions = "getTermsAndConditions";

    /*
     * ## FORMS
     */

    @GET("/")
    void getLoginForm(Callback<BaseResponse<Form>> callback);

    String getLoginForm = "getLoginForm";

    @GET("/")
    void getFormsIndex(Callback<BaseResponse<FormsIndex>> callback);

    String getFormsIndex = "getFormsIndex";

    @GET("/")
    void getRatingForm(Callback<BaseResponse<Form>> callback);

    String getRatingForm = "getRatingForm";

    @GET("/")
    void getReviewForm(Callback<BaseResponse<Form>> callback);

    String getReviewForm = "getReviewForm";

    @GET("/")
    void getSellerReviewForm(Callback<BaseResponse<Form>> callback);

    String getSellerReviewForm = "getSellerReviewForm";

    @GET("/")
    void getRegisterForm(Callback<BaseResponse<Form>> callback);

    String getRegisterForm = "getRegisterForm";

    @GET("/")
    void getSignUpForm(Callback<BaseResponse<Form>> callback);

    String getSignUpForm = "getSignUpForm";

    @GET("/")
    void getForgotPasswordForm(Callback<BaseResponse<Form>> callback);

    String getForgotPasswordForm = "getForgotPasswordForm";

    @GET("/")
    void getCreateAddressForm(Callback<BaseResponse<Form>> callback);

    String getCreateAddressForm = "getCreateAddressForm";

    @GET("/")
    void getEditAddressForm(Callback<BaseResponse<Form>> callback);

    String getEditAddressForm = "getEditAddressForm";

    @GET("/")
    void getNewsletterForm(Callback<BaseResponse<Form>> callback);

    String getNewsletterForm = "getNewsletterForm";

    @GET("/")
    void getShippingMethodsForm(Callback<BaseResponse<SuperGetShippingMethodsForm>> callback);

    String getShippingMethodsForm = "getShippingMethodsForm";

    @GET("/")
    void getPaymentMethodsForm(Callback<BaseResponse<SuperGetPaymentMethodsForm>> callback);

    String getPaymentMethodsForm = "getPaymentMethodsForm";

    @GET("/")
    void getBillingForm(Callback<BaseResponse<BaseResponse>> callback);

    String getBillingForm = "getBillingForm";


    /*
     * ## CATALOG
     */

    @GET("/")
    void getCatalogFiltered(@QueryMap Map<String, String> data, Callback<BaseResponse<Catalog>> callback);

    String getCatalogFiltered = "getCatalogFiltered";

    @GET("/")
    void searchSku(@QueryMap Map<String, String> data, Callback<BaseResponse<CompleteProduct>> callback);

    String searchSku = "searchSku";

    /*
     * ## CATEGORIES
     */

    @GET("/")
    void getCategoriesPaginated(@QueryMap Map<String, String> data, Callback<BaseResponse<Categories>> callback);

    String getCategoriesPaginated = "getCategoriesPaginated";

    /*
     * ## HOME
     */

    @GET("/")
    void getHome(Callback<BaseResponse<HomePageObject>> callback);

    String getHome = "getHome";

    /*
     * ## SHOP IN SHOP
     */

    @GET("/")
    void getShopInShop(@QueryMap Map<String, String> data, Callback<BaseResponse<StaticPage>> callback);

    String getShopInShop = "getShopInShop";

    /*
     * ## CAMPAIGN
     */

    @GET("/")
    void getCampaign(@QueryMap Map<String, String> data, Callback<BaseResponse<Campaign>> callback);

    String getCampaign = "getCampaign";

    /*
     * ## PRODUCT
     */

    @GET("/")
    void searchProductDetail(@QueryMap Map<String, String> data, Callback<BaseResponse<CompleteProduct>> callback);

    String searchProductDetail = "searchProductDetail";

    @GET("/")
    void getProductDetail(Callback<BaseResponse<CompleteProduct>> callback);

    String getProductDetail = "getProductDetail";

    @GET("/")
    void getProductBundle(Callback<BaseResponse<ProductBundle>> callback);

    String getProductBundle = "getProductBundle";

    @GET("/")
    void getProductOffers(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductOffers>> callback);

    String getProductOffers = "getProductOffers";

    @FormUrlEncoded
    @POST("/")
    void validateProducts(@FieldMap Map<String, String> data, Callback<BaseResponse<SuperValidProducts>> callback);

    String validateProducts = "validateProducts";

    /*
     * ## SEARCH SUGGESTIONS
     */

    @GET("/")
    void getSearchSuggestions(@QueryMap Map<String, String> data, Callback<BaseResponse<Suggestions>> callback);

    String getSearchSuggestions = "getSearchSuggestions";

    /*
     * ## CART
     */

    @GET("/")
    void getShoppingCart(Callback<BaseResponse<ShoppingCart>> callback);

    String getShoppingCart = "getShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void addItemShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    String addItemShoppingCart = "addItemShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void addBundleShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    String addBundleShoppingCart = "addBundleShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void addMultipleItemsShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    String addMultipleItemsShoppingCart = "addMultipleItemsShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void updateQuantityShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    String updateQuantityShoppingCart = "updateQuantityShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void removeAllShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    String removeAllShoppingCart = "removeAllShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void removeItemShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<ShoppingCart>> callback);

    String removeItemShoppingCart = "removeItemShoppingCart";

    /*
     * ## VOUCHER
     */

    @FormUrlEncoded
    @POST("/")
    void addVoucher(@FieldMap Map<String, String> data, Callback<BaseResponse<Voucher>> callback);

    String addVoucher = "addVoucher";

    @GET("/")
    void removeVoucher(Callback<BaseResponse<ShoppingCart>> callback);

    String removeVoucher = "removeVoucher";

    /*
     * ## SESSION
     */

    @GET("/")
    void logoutCustomer(Callback<BaseResponse<Void>> callback);

    String logoutCustomer = "logoutCustomer";

    @FormUrlEncoded
    @POST("/")
    void loginCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepLogin>> callback);

    String loginCustomer = "loginCustomer";

    @FormUrlEncoded
    @POST("/")
    void loginFacebookCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepLogin>> callback);

    String loginFacebookCustomer = "loginFacebookCustomer";

    @FormUrlEncoded
    @POST("/")
    void signUpCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepSignUp>> callback);

    String registerCustomer = "registerCustomer";

    @FormUrlEncoded
    @POST("/")
    void registerCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    String signUpCustomer = "signUpCustomer";

    @FormUrlEncoded
    @POST("/")
    void forgotPassword(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    String forgotPassword = "forgotPassword";

    @FormUrlEncoded
    @POST("/")
    void changePassword(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    String changePassword = "changePassword";

    @FormUrlEncoded
    @POST("/")
    void subscribeNewsletter(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    String subscribeNewsletter = "subscribeNewsletter";

    @GET("/")
    void getCustomerDetails(Callback<BaseResponse<Customer>> callback);

    String getCustomerDetails = "getCustomerDetails";

    @GET("/")
    void getAddressesList(Callback<BaseResponse<Addresses>> callback);

    String getAddressesList = "getAddressesList";

    @FormUrlEncoded
    @POST("/")
    void createAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepObject>> callback);

    String createAddress = "createAddress";

    @FormUrlEncoded
    @POST("/")
    void editAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    String editAddress = "editAddress";

    @FormUrlEncoded
    @POST("/")
    void setDefaultShippingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse> callback);

    String setDefaultShippingAddress = "setDefaultShippingAddress";

    @FormUrlEncoded
    @POST("/")
    void setDefaultBillingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse> callback);

    String setDefaultBillingAddress = "setDefaultBillingAddress";

    @GET("/")
    void getBillingAddressForm(Callback<BaseResponse<SuperGetBillingForm>> callback);

    String getBillingAddressForm = "getBillingAddressForm";

    @FormUrlEncoded
    @POST("/")
    void setBillingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<SuperSetBillingAddress>> callback);

    String setBillingAddress = "setBillingAddress";

    @GET("/")
    void getRegions(Callback<BaseResponse<AddressRegions>> callback);

    String getRegions = "getRegions";

    @GET("/")
    void getCities(@QueryMap Map<String, String> data, Callback<BaseResponse<AddressCities>> callback);

    String getCities = "getCities";

    /*
     * ## RATINGS/REVIEWS
     */
    @GET("/")
    void getProductReviews(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductRatingPage>> callback);

    String getProductReviews = "getProductReviews";

    @FormUrlEncoded
    @POST("/")
    void setRatingReview(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    String setRatingReview = "setRatingReview";

    @FormUrlEncoded
    @POST("/")
    void setSellerReview(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    String setSellerReview = "setSellerReview";

    @GET("/")
    void getSellerReviews(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductRatingPage>> callback);

    String getSellerReviews = "getSellerReviews";

    /*
    * ## ORDERS
    */
    @GET("/")
    void trackOrder(@QueryMap Map<String, String> data, Callback<BaseResponse<OrderTracker>> callback);

    String trackOrder = "trackOrder";

    @GET("/")
    void getOrdersList(@QueryMap Map<String, String> data, Callback<BaseResponse<SuperOrder>> callback);

    String getOrdersList = "getOrdersList";

    /*
    * ## CHECKOUT
    */

    @GET("/")
    void getNativeCheckoutAvailable(Callback<BaseResponse<SuperNativeCheckoutAvailability>> callback);

    String getNativeCheckoutAvailable = "getNativeCheckoutAvailable";

    @FormUrlEncoded
    @POST("/")
    void setShippingMethod(@FieldMap Map<String, String> data, Callback<BaseResponse<SuperSetShippingMethod>> callback);

    String setShippingMethod = "setShippingMethod";

    @FormUrlEncoded
    @POST("/")
    void setPaymentMethod(@FieldMap Map<String, String> data, Callback<BaseResponse<SuperSetPaymentMethod>> callback);

    String setPaymentMethod = "setPaymentMethod";

    @FormUrlEncoded
    @POST("/")
    void checkoutFinish(@FieldMap Map<String, String> data, Callback<BaseResponse<SuperCheckoutFinish>> callback);

    String checkoutFinish = "checkoutFinish";

}

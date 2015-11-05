package com.mobile.newFramework.rest.interfaces;

import com.mobile.newFramework.forms.AddressForms;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormsIndex;
import com.mobile.newFramework.objects.addresses.AddressCities;
import com.mobile.newFramework.objects.addresses.AddressPostalCodes;
import com.mobile.newFramework.objects.addresses.AddressRegions;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.objects.addresses.PhonePrefixes;
import com.mobile.newFramework.objects.campaign.Campaign;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.catalog.Catalog;
import com.mobile.newFramework.objects.category.Categories;
import com.mobile.newFramework.objects.checkout.CheckoutFinish;
import com.mobile.newFramework.objects.checkout.CheckoutFormBilling;
import com.mobile.newFramework.objects.checkout.CheckoutFormPayment;
import com.mobile.newFramework.objects.checkout.CheckoutFormShipping;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.checkout.CheckoutStepObject;
import com.mobile.newFramework.objects.checkout.SetBillingAddress;
import com.mobile.newFramework.objects.checkout.SetPaymentMethod;
import com.mobile.newFramework.objects.checkout.SetShippingMethod;
import com.mobile.newFramework.objects.configs.ApiInformation;
import com.mobile.newFramework.objects.configs.AvailableCountries;
import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.customer.CustomerEmailCheck;
import com.mobile.newFramework.objects.home.HomePageObject;
import com.mobile.newFramework.objects.orders.MyOrder;
import com.mobile.newFramework.objects.orders.OrderTracker;
import com.mobile.newFramework.objects.product.BundleList;
import com.mobile.newFramework.objects.product.OfferList;
import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.objects.product.ValidProductList;
import com.mobile.newFramework.objects.product.WishList;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.objects.search.Suggestions;
import com.mobile.newFramework.objects.statics.MobileAbout;
import com.mobile.newFramework.objects.statics.StaticPage;
import com.mobile.newFramework.objects.statics.StaticTermsConditions;
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
    String getAvailableCountries = "getAvailableCountries";

    /*
     * ## CONFIGS
     */
    String getCountryConfigurations = "getCountryConfigurations";
    String getApiInformation = "getApiInformation";
    String getImageResolutions = "getImageResolutions";
    String getTermsAndConditions = "getTermsAndConditions";
    String getLoginForm = "getLoginForm";
    String getFormsIndex = "getFormsIndex";
    String getRatingForm = "getRatingForm";
    String getReviewForm = "getReviewForm";
    String getSellerReviewForm = "getSellerReviewForm";
    String getRegisterForm = "getRegisterForm";

    /*
     * ## FORMS
     */
    String getSignUpForm = "getSignUpForm";
    String getForgotPasswordForm = "getForgotPasswordForm";
    String getCreateAddressForm = "getCreateAddressForm";
    String getEditAddressForm = "getEditAddressForm";
    String getNewsletterForm = "getNewsletterForm";
    String getShippingMethodsForm = "getShippingMethodsForm";
    String getPaymentMethodsForm = "getPaymentMethodsForm";
    String getUserDataForm = "getUserDataForm";
    String getCatalogFiltered = "getCatalogFiltered";
    String searchSku = "searchSku";
    String getCategoriesPaginated = "getCategoriesPaginated";
    String getHome = "getHome";
    String getShopInShop = "getShopInShop";
    String getCampaign = "getCampaign";
    String getProductDetail = "getProductDetail";
    String getProductBundle = "getProductBundle";
    String getProductOffers = "getProductOffers";
    String validateProducts = "validateProducts";
    String getSearchSuggestions = "getSearchSuggestions";
    String getShoppingCart = "getShoppingCart";
    String addItemShoppingCart = "addItemShoppingCart";
    String addBundleShoppingCart = "addBundleShoppingCart";
    String addMultipleItemsShoppingCart = "addMultipleItemsShoppingCart";
    String updateQuantityShoppingCart = "updateQuantityShoppingCart";
    String removeAllShoppingCart = "removeAllShoppingCart";
    String removeItemShoppingCart = "removeItemShoppingCart";
    String addVoucher = "addVoucher";
    String removeVoucher = "removeVoucher";

    /*
     * ## CATALOG
     */
    String setUserData = "setUserData";
    String logoutCustomer = "logoutCustomer";
    String loginCustomer = "loginCustomer";
    String loginFacebookCustomer = "loginFacebookCustomer";

    /*
     * ## CATEGORIES
     */
    String signUpCustomer = "signUpCustomer";
    String registerCustomer = "registerCustomer";

    /*
     * ## HOME
     */
    String forgotPassword = "forgotPassword";
    String changePassword = "changePassword";

    /*
     * ## SHOP IN SHOP
     */
    String subscribeNewsletter = "subscribeNewsletter";
    String getCustomerDetails = "getCustomerDetails";

    /*
     * ## CAMPAIGN
     */
    String getAddressesList = "getAddressesList";
    String createAddress = "createAddress";

    /*
     * ## PRODUCT
     */
    String editAddress = "editAddress";
    String setDefaultShippingAddress = "setDefaultShippingAddress";
    String setDefaultBillingAddress = "setDefaultBillingAddress";
    String getBillingAddressForm = "getBillingAddressForm";
    String setBillingAddress = "setBillingAddress";
    String getRegions = "getRegions";
    String getCities = "getCities";
    String getPostalCodes = "getPostalCodes";

    /*
     * ## SEARCH SUGGESTIONS
     */
    String getProductReviews = "getProductReviews";
    String setRatingReview = "setRatingReview";

    /*
     * ## CART
     */
    String setSellerReview = "setSellerReview";
    String getSellerReviews = "getSellerReviews";
    String trackOrder = "trackOrder";
    String getOrdersList = "getOrdersList";
    String setShippingMethod = "setShippingMethod";
    String setPaymentMethod = "setPaymentMethod";
    String checkoutFinish = "checkoutFinish";
    String getChangePasswordForm = "getChangePasswordForm";
    String getWishList = "getWishList";
    String addToWishList = "addToWishList";
    String removeFromWishList = "removeFromWishList";
    String emailCheck = "emailCheck";
    String getPhonePrefixes = "getPhonePrefixes";
    String getFaqTerms = "getFaqTerms";

    /*
     * ## VOUCHER
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
     * ## SESSION
     */

    @GET("/")
    void getTermsAndConditions(@QueryMap Map<String, String> data, Callback<BaseResponse<StaticTermsConditions>> callback);

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
    void getCreateAddressForm(Callback<BaseResponse<AddressForms>> callback);

    @FormUrlEncoded
    @POST("/")
    void getEditAddressForm(@FieldMap Map<String, String> data, Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getNewsletterForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getShippingMethodsForm(Callback<BaseResponse<CheckoutFormShipping>> callback);

    @GET("/")
    void getPaymentMethodsForm(Callback<BaseResponse<CheckoutFormPayment>> callback);

    @GET("/")
    void getUserDataForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getCatalogFiltered(@QueryMap Map<String, String> data, Callback<BaseResponse<Catalog>> callback);

    @GET("/")
    void searchSku(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductComplete>> callback);

    @GET("/")
    void getCategoriesPaginated(Callback<BaseResponse<Categories>> callback);

    @GET("/")
    void getHome(Callback<BaseResponse<HomePageObject>> callback);

    @GET("/")
    void getShopInShop(@QueryMap Map<String, String> data, Callback<BaseResponse<StaticPage>> callback);

    @GET("/")
    void getCampaign(@QueryMap Map<String, String> data, Callback<BaseResponse<Campaign>> callback);

    @GET("/")
    void getProductDetail(@QueryMap Map<String, String> data, Callback<BaseResponse<ProductComplete>> callback);

    @GET("/")
    void getProductBundle(Callback<BaseResponse<BundleList>> callback);

    @GET("/")
    void getProductOffers(@QueryMap Map<String, String> data, Callback<BaseResponse<OfferList>> callback);

    @FormUrlEncoded
    @POST("/")
    void validateProducts(@FieldMap Map<String, String> data, Callback<BaseResponse<ValidProductList>> callback);

    @GET("/")
    void getSearchSuggestions(@QueryMap Map<String, String> data, Callback<BaseResponse<Suggestions>> callback);

    @GET("/")
    void getShoppingCart(Callback<BaseResponse<PurchaseEntity>> callback);

    @FormUrlEncoded
    @POST("/")
    void addItemShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    @FormUrlEncoded
    @POST("/")
    void addBundleShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    @FormUrlEncoded
    @POST("/")
    void addMultipleItemsShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    @FormUrlEncoded
    @POST("/")
    void updateQuantityShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    @FormUrlEncoded
    @POST("/")
    void removeAllShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    @FormUrlEncoded
    @POST("/")
    void removeItemShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    @FormUrlEncoded
    @POST("/")
    void addVoucher(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    @GET("/")
    void removeVoucher(Callback<BaseResponse<PurchaseEntity>> callback);

    @FormUrlEncoded
    @POST("/")
    void setUserData(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    @GET("/")
    void logoutCustomer(Callback<BaseResponse<Void>> callback);

    @FormUrlEncoded
    @POST("/")
    void loginCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepLogin>> callback);

    @FormUrlEncoded
    @POST("/")
    void loginFacebookCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepLogin>> callback);

    @FormUrlEncoded
    @POST("/")
    void signUpCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepLogin>> callback);

    @FormUrlEncoded
    @POST("/")
    void registerCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

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
    void setDefaultShippingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    @FormUrlEncoded
    @POST("/")
    void setDefaultBillingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    @GET("/")
    void getBillingAddressForm(Callback<BaseResponse<CheckoutFormBilling>> callback);

    @FormUrlEncoded
    @POST("/")
    void setBillingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<SetBillingAddress>> callback);

    /*
    * ## CHECKOUT
    */

    @GET("/")
    void getRegions(Callback<BaseResponse<AddressRegions>> callback);

    @GET("/")
    void getCities(@QueryMap Map<String, String> data, Callback<BaseResponse<AddressCities>> callback);

    @GET("/")
    void getPostalCodes(@QueryMap Map<String, String> data, Callback<BaseResponse<AddressPostalCodes>> callback);

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
    void getOrdersList(@QueryMap Map<String, String> data, Callback<BaseResponse<MyOrder>> callback);

    @FormUrlEncoded
    @POST("/")
    void setShippingMethod(@FieldMap Map<String, String> data, Callback<BaseResponse<SetShippingMethod>> callback);

    @FormUrlEncoded
    @POST("/")
    void setPaymentMethod(@FieldMap Map<String, String> data, Callback<BaseResponse<SetPaymentMethod>> callback);

    @FormUrlEncoded
    @POST("/")
    void checkoutFinish(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutFinish>> callback);

    @GET("/")
    void getChangePasswordForm(Callback<BaseResponse<Form>> callback);

    @GET("/")
    void getWishList(@QueryMap Map<String, String> data, Callback<BaseResponse<WishList>> callback);

    @FormUrlEncoded
    @POST("/")
    void addToWishList(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    @FormUrlEncoded
    @POST("/")
    void removeFromWishList(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    @FormUrlEncoded
    @POST("/")
    void emailCheck(@FieldMap Map<String, String> data, Callback<BaseResponse<CustomerEmailCheck>> callback);

    @GET("/")
    void getPhonePrefixes(Callback<BaseResponse<PhonePrefixes>> callback);

    @GET("/")
    void getFaqTerms(Callback<BaseResponse<MobileAbout>> callback);

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
}

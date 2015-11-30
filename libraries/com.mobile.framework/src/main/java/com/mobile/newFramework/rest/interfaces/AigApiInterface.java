package com.mobile.newFramework.rest.interfaces;

import com.mobile.newFramework.forms.AddressForms;
import com.mobile.newFramework.forms.Form;
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
import com.mobile.newFramework.objects.home.object.TeaserRichRelevanceObject;
import com.mobile.newFramework.objects.orders.MyOrder;
import com.mobile.newFramework.objects.orders.OrderStatus;
import com.mobile.newFramework.objects.product.BundleList;
import com.mobile.newFramework.objects.product.OfferList;
import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.objects.product.ValidProductList;
import com.mobile.newFramework.objects.product.WishList;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.objects.search.Suggestions;
import com.mobile.newFramework.objects.statics.MobileAbout;
import com.mobile.newFramework.objects.statics.StaticPage;
import com.mobile.newFramework.pojo.BaseResponse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
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

    /*
     * ## FORMS
     */

    @GET("/{url}")
    void getLoginForm(Callback<BaseResponse<Form>> callback);
    String getLoginForm = "getLoginForm";

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
    void getCreateAddressForm(Callback<BaseResponse<AddressForms>> callback);

    String getCreateAddressForm = "getCreateAddressForm";

    @FormUrlEncoded
    @POST("/")
    void getEditAddressForm(@FieldMap Map<String, String> data, Callback<BaseResponse<Form>> callback);
    String getEditAddressForm = "getEditAddressForm";

    @GET("/")
    void getNewsletterForm(Callback<BaseResponse<Form>> callback);

    String getNewsletterForm = "getNewsletterForm";

    @GET("/")
    void getShippingMethodsForm(Callback<BaseResponse<CheckoutFormShipping>> callback);

    String getShippingMethodsForm = "getShippingMethodsForm";

    @GET("/")
    void getPaymentMethodsForm(Callback<BaseResponse<CheckoutFormPayment>> callback);

    String getPaymentMethodsForm = "getPaymentMethodsForm";

    @GET("/")
    void getUserDataForm(Callback<BaseResponse<Form>> callback);

    String getUserDataForm = "getUserDataForm";

    /*
     * ## CATEGORIES
     */

    @GET("/")
    void getCategoriesPaginated(Callback<BaseResponse<Categories>> callback);

    String getCategoriesPaginated = "getCategoriesPaginated";

    /*
     * ## HOME
     */

    @GET("/")
    void getHome(Callback<BaseResponse<HomePageObject>> callback);
    String getHome = "getHome";

    /*
     * ## RICH RELEVANCE
     */

    @GET("/{path}")
    void getRichRelevance(@Path(value="path", encode=false) String path, Callback<BaseResponse<TeaserRichRelevanceObject>> callback);
    String getRichRelevance = "getRichRelevance";

    /*
     * ## SHOP IN SHOP
     */

    @GET("/")
    void getShopInShop(@QueryMap Map<String, String> data, Callback<BaseResponse<StaticPage>> callback);
    String getShopInShop = "getShopInShop";

    @GET("/")
    void getProductBundle(Callback<BaseResponse<BundleList>> callback);

    String getProductBundle = "getProductBundle";


    @FormUrlEncoded
    @POST("/")
    void validateProducts(@FieldMap Map<String, String> data, Callback<BaseResponse<ValidProductList>> callback);

    String validateProducts = "validateProducts";

    /*
     * ## CART
     */

    @GET("/")
    void getShoppingCart(Callback<BaseResponse<PurchaseEntity>> callback);

    String getShoppingCart = "getShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void addItemShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    String addItemShoppingCart = "addItemShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void addBundleShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    String addBundleShoppingCart = "addBundleShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void addMultipleItemsShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    String addMultipleItemsShoppingCart = "addMultipleItemsShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void updateQuantityShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    String updateQuantityShoppingCart = "updateQuantityShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void removeAllShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    String removeAllShoppingCart = "removeAllShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void removeItemShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    String removeItemShoppingCart = "removeItemShoppingCart";

    /*
     * ## VOUCHER
     */

    @FormUrlEncoded
    @POST("/")
    void addVoucher(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);

    String addVoucher = "addVoucher";

    @GET("/")
    void removeVoucher(Callback<BaseResponse<PurchaseEntity>> callback);

    String removeVoucher = "removeVoucher";

    /*
     * ## SESSION
     */

    @FormUrlEncoded
    @POST("/")
    void setUserData(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    String setUserData = "setUserData";

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
    void signUpCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepLogin>> callback);

    String signUpCustomer = "signUpCustomer";


    @FormUrlEncoded
    @POST("/")
    void registerCustomer(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);

    String registerCustomer = "registerCustomer";

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
    void setDefaultShippingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    String setDefaultShippingAddress = "setDefaultShippingAddress";

    @FormUrlEncoded
    @POST("/")
    void setDefaultBillingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    String setDefaultBillingAddress = "setDefaultBillingAddress";

    @GET("/")
    void getBillingAddressForm(Callback<BaseResponse<CheckoutFormBilling>> callback);

    String getBillingAddressForm = "getBillingAddressForm";

    @FormUrlEncoded
    @POST("/")
    void setBillingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<SetBillingAddress>> callback);

    String setBillingAddress = "setBillingAddress";

    @GET("/")
    void getRegions(Callback<BaseResponse<AddressRegions>> callback);

    String getRegions = "getRegions";

    @GET("/")
    void getCities(@QueryMap Map<String, String> data, Callback<BaseResponse<AddressCities>> callback);

    String getCities = "getCities";

    @GET("/")
    void getPostalCodes(@Path(value="path", encode=false) String path, Callback<BaseResponse<AddressPostalCodes>> callback);

    String getPostalCodes = "getPostalCodes";

    /*
     * ## RATINGS/REVIEWS
     */

    @FormUrlEncoded
    @POST("/")
    void setRatingReview(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    String setRatingReview = "setRatingReview";

    @FormUrlEncoded
    @POST("/")
    void setSellerReview(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);

    String setSellerReview = "setSellerReview";

    /*
    * ## ORDERS
    */
    @GET("/")
    void trackOrder(@QueryMap Map<String, String> data, Callback<BaseResponse<OrderStatus>> callback);

    String trackOrder = "trackOrder";

    @GET("/")
    void getOrdersList(@QueryMap Map<String, String> data, Callback<BaseResponse<MyOrder>> callback);
    String getOrdersList = "getOrdersList";

    /*
    * ## CHECKOUT
    */

    @FormUrlEncoded
    @POST("/")
    void setShippingMethod(@FieldMap Map<String, String> data, Callback<BaseResponse<SetShippingMethod>> callback);

    String setShippingMethod = "setShippingMethod";

    @FormUrlEncoded
    @POST("/")
    void setPaymentMethod(@FieldMap Map<String, String> data, Callback<BaseResponse<SetPaymentMethod>> callback);

    String setPaymentMethod = "setPaymentMethod";

    @FormUrlEncoded
    @POST("/")
    void checkoutFinish(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutFinish>> callback);

    String checkoutFinish = "checkoutFinish";

    @GET("/")
    void getChangePasswordForm(Callback<BaseResponse<Form>> callback);
    String getChangePasswordForm = "getChangePasswordForm";

    @GET("/")
    void getWishList(@QueryMap Map<String, String> data, Callback<BaseResponse<WishList>> callback);
    String getWishList = "getWishList";

    @FormUrlEncoded
    @POST("/")
    void addToWishList(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String addToWishList = "addToWishList";

    @FormUrlEncoded
    @POST("/")
    void removeFromWishList(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String removeFromWishList = "removeFromWishList";

    @GET("/{path}")
    void emailCheck(@Path(value="path", encode=false) String path, Callback<BaseResponse<CustomerEmailCheck>> callback);
    String emailCheck = "emailCheck";

    @GET("/")
    void getPhonePrefixes(Callback<BaseResponse<PhonePrefixes>> callback);
    String getPhonePrefixes = "getPhonePrefixes";

    @GET("/")
    void getFaqTerms(Callback<BaseResponse<MobileAbout>> callback);
    String getFaqTerms = "getFaqTerms";


    /*
     * TODO : ADD HERE NEW MOB API INTERFACE v2.0
     */

    /*
     * ## CATALOG
     */
    @GET("/{path}")
    void getCatalog(@Path(value="path", encode=false) String path, Callback<BaseResponse<Catalog>> callback);
    String getCatalog = "getCatalog";

    /*
    * ## CAMPAIGN
    */
    @GET("/{path}")
    void getCampaign(@Path(value="path", encode=false) String path, Callback<BaseResponse<Campaign>> callback);
    String getCampaign = "getCampaign";

    /*
     * ## PRODUCT
     */

    @GET("/{path}")
    void getProductDetail(@Path(value="path", encode=false) String path, Callback<BaseResponse<ProductComplete>> callback);
    String getProductDetail = "getProductDetail";

    @GET("/{path}")
    void getProductDetailReviews(@Path(value="path", encode=false) String path, Callback<BaseResponse<ProductRatingPage>> callback);
    String getProductDetailReviews = "getProductDetailReviews";

    @GET("/{path}")
    void getProductOffers(@Path (value="path", encode=false) String offersParameters, Callback<BaseResponse<OfferList>> callback);
    String getProductOffers = "getProductOffers";


    @GET("/{path}")
    void getProductBundles(@Path(value="path", encode=false) String path,Callback<BaseResponse<BundleList>> callback);
    String getProductBundles = "getProductBundles";

    /*
     * ## STATIC
     */

    @GET("/{path}")
    void getStaticPage(@Path(value="path", encode=false) String path, Callback<BaseResponse<StaticPage>> callback);
    String getStaticPage = "getStaticPage";


    /*
     * ## SEARCH SUGGESTIONS
     */

    @GET("/{path}")
    void getSearchSuggestions(@Path(value="path", encode=false) String path, Callback<BaseResponse<Suggestions>> callback);
    String getSearchSuggestions = "getSearchSuggestions";

}

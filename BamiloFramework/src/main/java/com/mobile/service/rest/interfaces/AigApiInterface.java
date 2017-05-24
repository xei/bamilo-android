package com.mobile.service.rest.interfaces;

import com.mobile.service.forms.AddressForms;
import com.mobile.service.forms.Form;
import com.mobile.service.forms.ReturnReasonForm;
import com.mobile.service.objects.ExternalLinksSection;
import com.mobile.service.objects.addresses.AddressCities;
import com.mobile.service.objects.addresses.AddressPostalCodes;
import com.mobile.service.objects.addresses.AddressRegions;
import com.mobile.service.objects.addresses.Addresses;
import com.mobile.service.objects.addresses.PhonePrefixes;
import com.mobile.service.objects.addresses.ReturnReasons;
import com.mobile.service.objects.campaign.Campaign;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.objects.catalog.Catalog;
import com.mobile.service.objects.category.Categories;
import com.mobile.service.objects.checkout.CheckoutFinish;
import com.mobile.service.objects.checkout.CheckoutStepLogin;
import com.mobile.service.objects.checkout.CheckoutStepObject;
import com.mobile.service.objects.checkout.MultiStepAddresses;
import com.mobile.service.objects.checkout.MultiStepPayment;
import com.mobile.service.objects.checkout.MultiStepShipping;
import com.mobile.service.objects.configs.ApiInformation;
import com.mobile.service.objects.configs.AvailableCountries;
import com.mobile.service.objects.configs.CountryConfigs;
import com.mobile.service.objects.customer.Customer;
import com.mobile.service.objects.customer.CustomerEmailCheck;
import com.mobile.service.objects.home.HomePageObject;
import com.mobile.service.objects.orders.MyOrder;
import com.mobile.service.objects.orders.OrderStatus;
import com.mobile.service.objects.product.BundleList;
import com.mobile.service.objects.product.OfferList;
import com.mobile.service.objects.product.ProductRatingPage;
import com.mobile.service.objects.product.RichRelevance;
import com.mobile.service.objects.product.ValidProductList;
import com.mobile.service.objects.product.WishList;
import com.mobile.service.objects.product.pojo.ProductComplete;
import com.mobile.service.objects.search.Suggestions;
import com.mobile.service.objects.statics.MobileAbout;
import com.mobile.service.objects.statics.StaticPage;
import com.mobile.service.pojo.BaseResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.RestMethod;


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

    /**
     * The below code allows DELETE Method to have a request body and it creates a new method (BODY_DELETE)
     * See Jake Wharton comment here http://stackoverflow.com/questions/22572301/retrofit-throwing-illegalargumentexception-exception-for-asynchronous-formurlenc
     * This method (BODY_DELETE) should be used whenever we need to add a request body on a DELETE method.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @RestMethod(value = "DELETE", hasBody = true)
    @interface BODY_DELETE { String value(); }

    /*
     * ########## HTTP GET ##########
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

    @GET("/")
    void getNewsletterForm(Callback<BaseResponse<Form>> callback);
    String getNewsletterForm = "getNewsletterForm";

    @GET("/")
    void getNewsletterUnSubscribeForm(Callback<BaseResponse<Form>> callback);
    String getNewsletterUnSubscribeForm = "getNewsletterUnSubscribeForm";

    @GET("/")
    void getUserDataForm(Callback<BaseResponse<Form>> callback);
    String getUserDataForm = "getUserDataForm";

    @GET("/")
    void getCategoriesPaginated(Callback<BaseResponse<Categories>> callback);
    String getCategoriesPaginated = "getCategoriesPaginated";

    @GET("/")
    void getExternalLinks(Callback<BaseResponse<ExternalLinksSection>> callback);
    String getExternalLinks = "getExternalLinks";

    @GET("/")
    void getHome(Callback<BaseResponse<HomePageObject>> callback);
    String getHome = "getHome";

    @GET("/{path}")
    void getRichRelevance(@Path(value="path", encode=false) String path, Callback<BaseResponse<RichRelevance>> callback);
    String getRichRelevance = "getRichRelevance";

    @GET("/")
    void getProductBundle(Callback<BaseResponse<BundleList>> callback);
    String getProductBundle = "getProductBundle";

    @GET("/{path}")
    void getCatalog(@Path(value="path", encode=false) String path, Callback<BaseResponse<Catalog>> callback);
    String getCatalog = "getCatalog";

    @GET("/{path}")
    void getCampaign(@Path(value="path", encode=false) String path, Callback<BaseResponse<Campaign>> callback);
    String getCampaign = "getCampaign";

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
    void getProductBundles(@Path(value="path", encode=false) String path, Callback<BaseResponse<BundleList>> callback);
    String getProductBundles = "getProductBundles";

    @GET("/{path}")
    void getStaticPage(@Path(value="path", encode=false) String path, Callback<BaseResponse<StaticPage>> callback);
    String getStaticPage = "getStaticPage";

    @GET("/")
    void getFaqTerms(Callback<BaseResponse<MobileAbout>> callback);
    String getFaqTerms = "getFaqTerms";

    @GET("/{path}")
    void getSearchSuggestions(@Path(value="path", encode=false) String path, Callback<BaseResponse<Suggestions>> callback);
    String getSearchSuggestions = "getSearchSuggestions";

    @GET("/{path}")
    void trackOrder(@Path(value="path", encode=false) String path, Callback<BaseResponse<OrderStatus>> callback);
    String trackOrder = "trackOrder";

    @GET("/{path}")
    void getOrdersList(@Path(value="path", encode=false) String path, Callback<BaseResponse<MyOrder>> callback);
    String getOrdersList = "getOrdersList";

    @GET("/")
    void getPhonePrefixes(Callback<BaseResponse<PhonePrefixes>> callback);
    String getPhonePrefixes = "getPhonePrefixes";

    @GET("/")
    void getChangePasswordForm(Callback<BaseResponse<Form>> callback);
    String getChangePasswordForm = "getChangePasswordForm";

    @GET("/{path}")
    void getWishList(@Path(value="path", encode=false) String path, Callback<BaseResponse<WishList>> callback);
    String getWishList = "getWishList";

    @GET("/")
    void getCustomerDetails(Callback<BaseResponse<Customer>> callback);
    String getCustomerDetails = "getCustomerDetails";

    @GET("/")
    void getAddressesList(Callback<BaseResponse<Addresses>> callback);
    String getAddressesList = "getAddressesList";

    @GET("/")
    void getRegions(Callback<BaseResponse<AddressRegions>> callback);
    String getRegions = "getRegions";

    @GET("/")
    void getCities(Callback<BaseResponse<AddressCities>> callback);
    String getCities = "getCities";

    @GET("/")
    void getPostalCodes(Callback<BaseResponse<AddressPostalCodes>> callback);
    String getPostalCodes = "getPostalCodes";

    @GET("/")
    void logoutCustomer(Callback<BaseResponse<Void>> callback);
    String logoutCustomer = "logoutCustomer";

    @GET("/")
    void getShoppingCart(Callback<BaseResponse<PurchaseEntity>> callback);
    String getShoppingCart = "getShoppingCart";

    @GET("/")
    void getMultiStepAddresses(Callback<BaseResponse<MultiStepAddresses>> callback);
    String getMultiStepAddresses = "getMultiStepAddresses";

    @GET("/")
    void getMultiStepShipping(Callback<BaseResponse<MultiStepShipping>> callback);
    String getMultiStepShipping = "getMultiStepShipping";

    @GET("/")
    void getMultiStepPayment(Callback<BaseResponse<MultiStepPayment>> callback);
    String getMultiStepPayment = "getMultiStepPayment";

    @GET("/")
    void getMultiStepFinish(Callback<BaseResponse<PurchaseEntity>> callback);
    String getMultiStepFinish = "getMultiStepFinish";

    @GET("/")
    void getReturnReasonForm(Callback<BaseResponse<ReturnReasonForm>> callback);
    String getReturnReasonForm = "getReturnReasonForm";

    @GET("/")
    void getReturnReasons(Callback<BaseResponse<ReturnReasons>> callback);
    String getReturnReasons = "getReturnReasons";

    @GET("/")
    void getReturnMethodsForm(Callback<BaseResponse<Form>> callback);
    String getReturnMethodsForm = "getReturnMethodsForm";

    @GET("/")
    void getReturnRefundForm(Callback<BaseResponse<Form>> callback);
    String getReturnRefundForm = "getReturnRefundForm";

    /*
     * ########## HTTP POST ########## TODO : ADD HERE NEW MOB API INTERFACE v2.0
     */

    @FormUrlEncoded
    @POST("/")
    void getSubCategoriesPaginated(@FieldMap Map<String, String> data, Callback<BaseResponse<Categories>> callback);
    String getSubCategoriesPaginated = "getSubCategoriesPaginated";

    @FormUrlEncoded
    @POST("/")
    void getEditAddressForm(@FieldMap Map<String, String> data, Callback<BaseResponse<Form>> callback);
    String getEditAddressForm = "getEditAddressForm";

    @FormUrlEncoded
    @BODY_DELETE("/")
    void removeCustomerAddressForm(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String removeCustomerAddressForm = "removeCustomerAddressForm";

    @FormUrlEncoded
    @POST("/")
    void validateProducts(@Field("products[]") ArrayList<String> keys, Callback<BaseResponse<ValidProductList>> callback);
    String validateProducts = "validateProducts";

    @FormUrlEncoded
    @POST("/")
    void addItemShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);
    String addItemShoppingCart = "addItemShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void addBundleShoppingCart(@FieldMap Map<String, String> data, @Field("product_list[]") ArrayList<String> keys, Callback<BaseResponse<PurchaseEntity>> callback);
    String addBundleShoppingCart = "addBundleShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void addMultipleItemsShoppingCart(@Field("product_list[]") ArrayList<String> keys, Callback<BaseResponse<PurchaseEntity>> callback);
    String addMultipleItemsShoppingCart = "addMultipleItemsShoppingCart";

    @FormUrlEncoded
    @POST("/")
    void addVoucher(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);
    String addVoucher = "addVoucher";

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
    void forgotPassword(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String forgotPassword = "forgotPassword";

    @FormUrlEncoded
    @POST("/")
    void changePassword(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String changePassword = "changePassword";

    @FormUrlEncoded
    @POST("/")
    void subscribeNewsletter(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String subscribeNewsletter = "subscribeNewsletter";

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
    void setRatingReview(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String setRatingReview = "setRatingReview";

    @FormUrlEncoded
    @POST("/")
    void setSellerReview(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String setSellerReview = "setSellerReview";

    @FormUrlEncoded
    @POST("/")
    void addToWishList(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String addToWishList = "addToWishList";

    @FormUrlEncoded
    @POST("/")
    void setMultiStepAddresses(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepObject>> callback);
    String setMultiStepAddresses = "setMultiStepAddresses";

    @FormUrlEncoded
    @POST("/")
    void setMultiStepShipping(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepObject>> callback);
    String setMultiStepShipping = "setMultiStepShipping";

    @FormUrlEncoded
    @POST("/")
    void setMultiStepPayment(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutStepObject>> callback);
    String setMultiStepPayment = "setMultiStepPayment";

    @FormUrlEncoded
    @POST("/")
    void setMultiStepFinish(@FieldMap Map<String, String> data, Callback<BaseResponse<CheckoutFinish>> callback);
    String setMultiStepFinish = "setMultiStepFinish";

    @FormUrlEncoded
    @POST("/")
    void submitFormAction(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String submitFormAction = "submitFormAction";

    @FormUrlEncoded
    @POST("/")
    void setReturnFinish(@FieldMap Map<String, String> data, Callback<BaseResponse<OrderStatus>> callback);
    String setReturnFinish = "setReturnFinish";

    @FormUrlEncoded
    @POST("/")
    void emailCheck(@FieldMap Map<String, String> data, Callback<BaseResponse<CustomerEmailCheck>> callback);
    String emailCheck = "emailCheck";

    /*
     * ########## HTTP PUT ##########  TODO : ADD HERE NEW MOB API INTERFACE v2.0
     */

    @FormUrlEncoded
    @PUT("/")
    void setDefaultShippingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String setDefaultShippingAddress = "setDefaultShippingAddress";

    @FormUrlEncoded
    @PUT("/")
    void setDefaultBillingAddress(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String setDefaultBillingAddress = "setDefaultBillingAddress";

    @FormUrlEncoded
    @PUT("/")
    void setUserData(@FieldMap Map<String, String> data, Callback<BaseResponse<Customer>> callback);
    String setUserData = "setUserData";

    @FormUrlEncoded
    @PUT("/")
    void updateQuantityShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);
    String updateQuantityShoppingCart = "updateQuantityShoppingCart";
    
    /*
     * ########## HTTP DELETE ##########  TODO : ADD HERE NEW MOB API INTERFACE v2.0
     */

    @FormUrlEncoded
    @BODY_DELETE("/")
    void removeItemShoppingCart(@FieldMap Map<String, String> data, Callback<BaseResponse<PurchaseEntity>> callback);
    String removeItemShoppingCart = "removeItemShoppingCart";

    @DELETE("/")
    void removeVoucher(Callback<BaseResponse<PurchaseEntity>> callback);
    String removeVoucher = "removeVoucher";
    
    @FormUrlEncoded
    @BODY_DELETE("/")
    void removeFromWishList(@FieldMap Map<String, String> data, Callback<BaseResponse<Void>> callback);
    String removeFromWishList = "removeFromWishList";

    @DELETE("/")
    void clearShoppingCart(Callback<BaseResponse<Void>> callback);
    String clearShoppingCart = "clearShoppingCart";

}
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
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.checkout.CheckoutStepObject;
import com.mobile.newFramework.objects.checkout.MultiStepAddresses;
import com.mobile.newFramework.objects.checkout.MultiStepPayment;
import com.mobile.newFramework.objects.checkout.MultiStepShipping;
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
     * ########## HTTP GET ########## TODO : ADD HERE NEW MOB API INTERFACE v2.0
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
    void getUserDataForm(Callback<BaseResponse<Form>> callback);
    String getUserDataForm = "getUserDataForm";

    @GET("/")
    void getCategoriesPaginated(Callback<BaseResponse<Categories>> callback);
    String getCategoriesPaginated = "getCategoriesPaginated";

    @GET("/")
    void getHome(Callback<BaseResponse<HomePageObject>> callback);
    String getHome = "getHome";

    @GET("/{path}")
    void getRichRelevance(@Path(value="path", encode=false) String path, Callback<BaseResponse<TeaserRichRelevanceObject>> callback);
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

    @GET("/{path}")
    void emailCheck(@Path(value="path", encode=false) String path, Callback<BaseResponse<CustomerEmailCheck>> callback);
    String emailCheck = "emailCheck";

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

    /*
     * ########## HTTP POST ########## TODO : ADD HERE NEW MOB API INTERFACE v2.0
     */

    @FormUrlEncoded
    @POST("/")
    void getEditAddressForm(@FieldMap Map<String, String> data, Callback<BaseResponse<Form>> callback);
    String getEditAddressForm = "getEditAddressForm";

    @FormUrlEncoded
    @POST("/")
    void validateProducts( @Field("products[]") ArrayList<String> keys, Callback<BaseResponse<ValidProductList>> callback);
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

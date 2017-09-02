package com.mobile.test.suites;

import com.mobile.service.rest.AigHttpClient;
import com.mobile.service.rest.configs.AigRestContract;
import com.mobile.service.utils.output.Print;
import com.mobile.test.AigChangePasswordFormTest;
import com.mobile.test.AigForgotPasswordTest;
import com.mobile.test.AigGetAddressesListTest;
import com.mobile.test.AigGetApiInformationTest;
import com.mobile.test.AigGetAvailableCountriesTest;
import com.mobile.test.AigGetCampaignTest;
import com.mobile.test.AigGetCatalogFilteredTest;
import com.mobile.test.AigGetCategoriesPaginatedTest;
import com.mobile.test.AigGetCategoryPaginatedTest;
import com.mobile.test.AigGetCitiesTest;
import com.mobile.test.AigGetCountryConfigurationsTest;
import com.mobile.test.AigGetCreateAddressFormTest;
import com.mobile.test.AigGetCustomerDetailsTest;
import com.mobile.test.AigGetEditAddressFormTest;
import com.mobile.test.AigGetForgotPasswordFormTest;
import com.mobile.test.AigGetHomePageTest;
import com.mobile.test.AigGetLoginFormTest;
import com.mobile.test.AigGetNewsletterFormTest;
import com.mobile.test.AigGetOrdersListTest;
import com.mobile.test.AigGetProductBundleTest;
import com.mobile.test.AigGetProductDetailTest;
import com.mobile.test.AigGetProductOffersTest;
import com.mobile.test.AigGetRatingFormTest;
import com.mobile.test.AigGetRegionsTest;
import com.mobile.test.AigGetRegisterFormTest;
import com.mobile.test.AigGetReviewFormTest;
import com.mobile.test.AigGetReviewsTest;
import com.mobile.test.AigGetSellerReviewFormTest;
import com.mobile.test.AigGetShoppingCartTest;
import com.mobile.test.AigGetSignUpFormTest;
import com.mobile.test.AigLoginCustomerTest;
import com.mobile.test.AigLogoutCustomerTest;
import com.mobile.test.AigSearchSkuTest;
import com.mobile.test.AigShoppingCartAddItemTest;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//import com.mobile.test.AigGetShopInShopTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AigGetLoginFormTest.class,
        AigLoginCustomerTest.class,
        AigGetAvailableCountriesTest.class,
        AigGetCountryConfigurationsTest.class,
        //AigGetShippingMethodsFormTest.class, //TODO add product to cart
        //AigGetPaymentMethodsFormTest.class, //TODO ERROR_PARSING_SERVER_DATA
        //AigGetBillingAddressFormTest.class,
        AigGetAddressesListTest.class,
        AigGetRegionsTest.class,
        AigGetCitiesTest.class,
        AigGetCustomerDetailsTest.class,
        AigGetEditAddressFormTest.class,
        AigGetOrdersListTest.class,
        AigGetShoppingCartTest.class,
        AigLogoutCustomerTest.class,
        AigChangePasswordFormTest.class,
        AigForgotPasswordTest.class,
        AigGetApiInformationTest.class,
        AigGetCampaignTest.class,
        AigGetCatalogFilteredTest.class,
        AigGetCategoriesPaginatedTest.class,
        AigGetCategoryPaginatedTest.class,
        AigGetCreateAddressFormTest.class,

        AigGetForgotPasswordFormTest.class,
        //AigGetFormsIndexTest.class,
        AigGetHomePageTest.class,
       // AigGetImageResolutionsTest.class, TODO Response is not being parsed
        AigGetNewsletterFormTest.class,
        AigGetProductBundleTest.class,
        AigGetProductDetailTest.class,
        AigGetProductOffersTest.class,
        AigGetRatingFormTest.class,
        AigGetRegisterFormTest.class,
        AigGetReviewFormTest.class,
        AigGetReviewsTest.class,
        AigGetSellerReviewFormTest.class,
        //AigGetShopInShopTest.class,
        AigGetSignUpFormTest.class,
        AigSearchSkuTest.class,
        AigShoppingCartAddItemTest.class,

})

public class AigMobApiNigeriaTestSuite {

    public static final String TAG = AigMobApiNigeriaTestSuite.class.getSimpleName();
        public static String HOST = "https://alice-staging.jumia.com.ng/mobapi/v1.8";
        //public static String HOST = "http://alice-integration.jumia.com.ng/mobapi/v1.8";

        @BeforeClass
    public static void setUp() {
        Print.initializeTestingMode();
        //AigRestContract.initializeTestingMode();
        AigHttpClient.initializeTestingMode();
        AigRestContract.AUTHENTICATION_USER_AGENT = "NGAMZ";
        Print.i(TAG, "SETUP");
    }


}

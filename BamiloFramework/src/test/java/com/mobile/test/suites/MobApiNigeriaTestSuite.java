package com.mobile.test.suites;

import com.mobile.service.rest.AigHttpClient;
import com.mobile.service.utils.output.Print;
import com.mobile.test.AigGetApiInformationTest;
import com.mobile.test.AigGetAvailableCountriesTest;
import com.mobile.test.AigGetCampaignTest;
import com.mobile.test.AigGetCatalogFilteredTest;
import com.mobile.test.AigGetCategoriesPaginatedTest;
import com.mobile.test.AigGetCategoryPaginatedTest;
import com.mobile.test.AigGetCountryConfigurationsTest;
import com.mobile.test.AigGetHomePageTest;
import com.mobile.test.AigGetImageResolutionsTest;
import com.mobile.test.AigGetProductBundleTest;
import com.mobile.test.AigGetProductDetailTest;
import com.mobile.test.AigGetProductOffersTest;
import com.mobile.test.AigGetShoppingCartTest;
import com.mobile.test.AigLogoutCustomerTest;
import com.mobile.test.AigShoppingCartAddItemTest;
import com.mobile.test.LoginCustomerTest;
import com.mobile.test.ShoppingCartUpdateQuantityTest;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//import com.mobile.test.AigGetShopInShopTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        // SPLASH REQUESTS
        AigGetAvailableCountriesTest.class,
        AigGetCountryConfigurationsTest.class,
        AigGetApiInformationTest.class,
        AigGetImageResolutionsTest.class,
        // HOME REQUESTS
        AigGetHomePageTest.class,
        AigGetShoppingCartTest.class,
        LoginCustomerTest.class,

        // NAVIGATION
        AigGetCategoriesPaginatedTest.class,
        AigGetCategoryPaginatedTest.class,
        // CATALOG
        AigGetCatalogFilteredTest.class,
        // PRODUCT DETAIL
        AigGetProductDetailTest.class,
        AigGetProductBundleTest.class,
        AigGetProductOffersTest.class,
        // CAMPAIGN
        AigGetCampaignTest.class,
        // SHOP IN SHOP
        //AigGetShopInShopTest.class,
        // CART
        AigGetShoppingCartTest.class,
        AigShoppingCartAddItemTest.class,
        ShoppingCartUpdateQuantityTest.class,
        AigLogoutCustomerTest.class,
})

public class MobApiNigeriaTestSuite {
        @BeforeClass
        public static void setUp() {
                //configure the request host that all tests will use
                System.setProperty("request_host", "http://alice-staging.jumia.com.ng/mobapi");

                Print.initializeTestingMode();
                AigHttpClient.initializeTestingMode();

                System.out.println("UGANDA TEST SUITE STARTED");
        }
}

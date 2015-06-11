package com.mobile.test.suites;

import com.mobile.test.GetApiInformationTest;
import com.mobile.test.GetAvailableCountriesTest;
import com.mobile.test.GetCampaignTest;
import com.mobile.test.GetCatalogFilteredTest;
import com.mobile.test.GetCategoriesPaginatedTest;
import com.mobile.test.GetCategoryPaginatedTest;
import com.mobile.test.GetCountryConfigurationsTest;
import com.mobile.test.GetHomePageTest;
import com.mobile.test.GetImageResolutionsTest;
import com.mobile.test.GetProductBundleTest;
import com.mobile.test.GetProductDetailTest;
import com.mobile.test.GetProductOffersTest;
import com.mobile.test.GetShopInShopTest;
import com.mobile.test.GetShoppingCartTest;
import com.mobile.test.LoginCustomerTest;
import com.mobile.test.ShoppingCartAddItemTest;
import com.mobile.test.ShoppingCartUpdateQuantityTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        // SPLASH REQUESTS
        GetAvailableCountriesTest.class,
        GetCountryConfigurationsTest.class,
        GetApiInformationTest.class,
        GetImageResolutionsTest.class,
        // HOME REQUESTS
        GetHomePageTest.class,
        GetShoppingCartTest.class,
        LoginCustomerTest.class,
        // NAVIGATION
        GetCategoriesPaginatedTest.class,
        GetCategoryPaginatedTest.class,
        // CATALOG
        GetCatalogFilteredTest.class,
        // PRODUCT DETAIL
        GetProductDetailTest.class,
        GetProductBundleTest.class,
        GetProductOffersTest.class,
        // CAMPAIGN
        GetCampaignTest.class,
        // SHOP IN SHOP
        GetShopInShopTest.class,
        // CART
        GetShoppingCartTest.class,
        ShoppingCartAddItemTest.class,
        ShoppingCartUpdateQuantityTest.class,
})

public class MobApiNigeriaTestSuite {



}

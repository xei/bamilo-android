package com.mobile.test.suites;

import com.mobile.test.GetApiInformationTest;
import com.mobile.test.GetAvailableCountriesTest;
import com.mobile.test.GetCountryConfigurationsTest;
import com.mobile.test.GetHomePageTest;
import com.mobile.test.GetImageResolutionsTest;
import com.mobile.test.GetShoppingCartTest;
import com.mobile.test.LoginCustomerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GetAvailableCountriesTest.class,
        GetCountryConfigurationsTest.class,
        GetApiInformationTest.class,
        GetImageResolutionsTest.class,
        GetHomePageTest.class,
        GetShoppingCartTest.class,
        LoginCustomerTest.class,
})

public class MobApiNigeriaTestSuite {


}

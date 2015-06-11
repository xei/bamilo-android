package com.mobile.test.suites;

import com.mobile.test.ChangePasswordTest;
import com.mobile.test.GetCustomerDetailsTest;
import com.mobile.test.GetOrdersListTest;
import com.mobile.test.LoginCustomerTest;
import com.mobile.test.SubscribeNewsletterTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({

        LoginCustomerTest.class,
        SubscribeNewsletterTest.class,
        GetCustomerDetailsTest.class,
        ChangePasswordTest.class,
        GetOrdersListTest.class,


})

public class MobApiNigeriaAccountTestSuite {


}

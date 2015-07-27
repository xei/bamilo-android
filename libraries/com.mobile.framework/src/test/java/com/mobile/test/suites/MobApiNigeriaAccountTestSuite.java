package com.mobile.test.suites;

import com.mobile.test.ChangePasswordTest;
import com.mobile.test.AigGetCustomerDetailsTest;
import com.mobile.test.AigGetOrdersListTest;
import com.mobile.test.LoginCustomerTest;
import com.mobile.test.SubscribeNewsletterTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({

        LoginCustomerTest.class,
        SubscribeNewsletterTest.class,
        AigGetCustomerDetailsTest.class,
        ChangePasswordTest.class,
        AigGetOrdersListTest.class,


})

public class MobApiNigeriaAccountTestSuite {


}

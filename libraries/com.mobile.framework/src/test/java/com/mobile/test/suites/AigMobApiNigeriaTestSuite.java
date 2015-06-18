package com.mobile.test.suites;

import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.AigLoginCustomerTest;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AigLoginCustomerTest.class,
        //LogoutCustomerTest.class,
})

public class AigMobApiNigeriaTestSuite {

    @BeforeClass
    public static void setUp() {
        Print.initializeTestingMode();
        //AigRestContract.initializeTestingMode();
        AigHttpClient.initializeTestingMode();
        System.out.println("TEST SUITE STARTED");
    }


}

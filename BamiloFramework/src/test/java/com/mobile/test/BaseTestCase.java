package com.mobile.test;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;
import com.mobile.newFramework.utils.output.Print;

import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;

public class BaseTestCase extends TestCase implements AigResponseCallback {

    protected CountDownLatch mCountDownLatch;

    RequestBundle requestBundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Print.initializeTestingMode();
        AigHttpClient.initializeTestingMode();
        mCountDownLatch = new CountDownLatch(1);
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
    }

    @Override
    public void onRequestError(BaseResponse response) {
    }
}


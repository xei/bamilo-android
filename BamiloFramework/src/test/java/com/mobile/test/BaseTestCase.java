package com.mobile.test;

import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.AigHttpClient;
import com.mobile.service.rest.interfaces.AigResponseCallback;
import com.mobile.service.utils.output.Print;

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


package com.mobile.test;

import android.content.Context;

import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;

import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;

public class BaseTestCase extends TestCase implements AigResponseCallback {

    protected CountDownLatch mCountDownLatch;

    protected final static Context IS_AUTOMATED_TEST = null;

    RequestBundle requestBundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mCountDownLatch = new CountDownLatch(1);
    }

    @Override
    public void onRequestComplete(BaseResponse response) { }

    @Override
    public void onRequestError(BaseResponse response) { }
}


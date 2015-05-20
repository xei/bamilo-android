package com.mobile.test;

import android.content.Context;

import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;

/**
 * Created by spereira on 5/20/15.
 */
public class BaseTestCase extends TestCase {

    protected CountDownLatch mCountDownLatch;

    protected final static Context IS_AUTOMATED_TEST = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mCountDownLatch = new CountDownLatch(1);
    }

}


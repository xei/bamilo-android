package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public abstract class AigTestCase extends TestCase implements AigResponseCallback {

    protected CountDownLatch mCountDownLatch;

    protected RequestBundle mRequestBundle;

    protected EventType mEventType;

    protected String mInterfaceName;

    protected String mUrl;

    protected Map<String, String> mData;

    public AigTestCase() {
        mEventType = getEventType();
        mInterfaceName = getAigInterfaceName();
        mUrl = getUrl();
        mData = getData();
    }

    public abstract EventType getEventType();

    public abstract String getAigInterfaceName();

    public abstract String getUrl();

    public abstract Map<String, String> getData();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mRequestBundle = new RequestBundle.Builder().setUrl(mUrl).setData(mData).build();
        mCountDownLatch = new CountDownLatch(1);
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        new BaseRequest(mRequestBundle, this).execute(mInterfaceName);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
    }

    @Override
    public void onRequestError(BaseResponse response) {
    }

     public void analyzeOnErrorEvent(final BaseResponse response) {

        //final JumiaError jumiaError = response.getError();
        ErrorCode errorCode = response.getError().getErrorCode();
        Map<String, List<String>> errorMessages = response.getErrorMessages();
        // emit a test fail on main thread so that it can be catched and reported by the fail caching mechanism
        Assert.fail("Request failed error code: " + errorCode + ". Message: " + (errorMessages != null ? errorMessages.toString() : " no message") + " when requesting: " + getEventType());
    }
}



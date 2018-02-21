package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigResponseCallback;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;

import junit.framework.TestCase;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public abstract class AigTestCase extends TestCase implements AigResponseCallback {

    protected CountDownLatch mCountDownLatch;

    protected RequestBundle mRequestBundle;

    protected EventType mEventType;

    protected String mInterfaceName;

    protected String mUrl;

    protected Map<String, String> mData;

    private BaseResponse mAigResponse;

    /*
     * ############# TEST CONSTRUCTOR #############
     */

    public AigTestCase() {
        // ...
    }

    public abstract EventType getEventType();

    public abstract String getAigInterfaceName();

    public abstract String getUrl();

    public abstract Map<String, String> getData();

    /*
     * ############# JUNIT #############
     */

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mEventType = getEventType();
        mInterfaceName = getAigInterfaceName();
        mUrl = getUrl();
        mData = getData();
        mRequestBundle = new RequestBundle.Builder().setEndPoint(mUrl).addQueryData(mData).build();
        mCountDownLatch = new CountDownLatch(1);
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        // Request
        onRequest();
        // Wait
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Response
        testResponse(mAigResponse);
    }

    public abstract void testResponse(BaseResponse response);

    /*
     * ############# RETROFIT INTERFACE #############
     */

    private void onRequest() {
        new BaseRequest(mRequestBundle, this).execute(mInterfaceName);
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        Print.d("TEST SUCCESS: " + response.hadSuccess());
        // Save response
        mAigResponse = response;
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        Print.d("TEST ERROR: " + response.hadSuccess());
        // Save response
        mAigResponse = response;
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    /*
     * ############# COMMON METHODS #############
     */

//     public void analyzeOnErrorEvent(final ServerResponse response) {
//        //final JumiaError jumiaError = response.getError();
//        ErrorCode errorCode = response.getError().getErrorCode();
//        Map<String, List<String>> errorMessages = response.getErrorMessages();
//        // emit a test fail on main thread so that it can be catched and reported by the fail caching mechanism
//        Assert.fail("Request failed error code: " + errorCode + ". Message: " + (errorMessages != null ? errorMessages.toString() : " no message") + " when requesting: " + getEventType());
//    }
}



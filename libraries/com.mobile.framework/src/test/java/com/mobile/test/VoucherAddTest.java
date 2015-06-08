package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.objects.Voucher;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.voucher.AddVoucher;

import java.util.HashMap;

public class VoucherAddTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HashMap<String, String> data = new HashMap<>();
        data.put("couponcode", "123456");
        requestBundle = new RequestBundle.Builder()
                .setUrl("http://integration-www.jumia.ug/mobapi/v1.7/order/addvoucher/")
                .setCache(EventType.ADD_VOUCHER.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
//        new BaseRequest<Voucher>(IS_AUTOMATED_TEST,requestBundle,this){
//            @Override
//            public void execute() {
//
//            }
//        }.executeUseReflection("addVoucher");

        new AddVoucher(requestBundle, this).execute();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        System.out.println("TEST SUCCESS: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        System.out.println("TEST ERROR: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

}

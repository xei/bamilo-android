package com.mobile.extlibraries.emarsys;

import com.mobile.view.newfragments.PaymentMethod;

/**
 * Created by Arash Hassanpour on 4/4/2017.
 */

public class EmarsysPushIdentifier extends EmarsysContactIdentifier{

    public String pushToken;



    public EmarsysPushIdentifier(String appId, String hwId, String token) {
        super(appId, hwId);
        pushToken = token;
    }
}

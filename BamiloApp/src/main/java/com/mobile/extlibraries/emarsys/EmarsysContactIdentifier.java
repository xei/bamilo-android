package com.mobile.extlibraries.emarsys;

/**
 * Created by Arash Hassanpour on 4/4/2017.
 */

public class EmarsysContactIdentifier {
    public String applicationId;
    public String hardwareId;

    public EmarsysContactIdentifier(String appId, String hwId)
    {
        applicationId = appId;
        hardwareId = hwId;
    }
}

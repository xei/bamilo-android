package com.mobile.extlibraries.emarsys;

import java.util.Map;

/**
 * Created by Arash Hassanpour on 4/4/2017.
 */

public interface EmarsysMobileEngageInterface {


    //public EmarsysMobileEngage sharedInstance;

    //POST users/login
    void sendLogin(String pushToken, EmarsysMobileEngageResponse completion);

    //POST events/message_open
    void sendOpen(String sid, EmarsysMobileEngageResponse completion);

    //POST events/<event-name>
    void sendCustomEvent(String event, Map<String, String> attributes, EmarsysMobileEngageResponse completion);

    //POST logout
    void sendLogout(EmarsysMobileEngageResponse completion);


}

package com.mobile.libraries.emarsys;

import java.util.Dictionary;
import java.util.Map;

/**
 * Created by Arash Hassanpour on 4/5/2017.
 */

public interface EmarsysDataManagerInterface {




    void anonymousLogin(EmarsysPushIdentifier contact, DataCompletion completion);

    void login(EmarsysPushIdentifier contact, String contactFieldId, String contactFieldValue, DataCompletion completion);

    void openMessageEvent(EmarsysContactIdentifier contact, String sid, DataCompletion completion);

    void customEvent(EmarsysContactIdentifier contact, String event, Map<String,String> attributes, DataCompletion completion);

    void logout(EmarsysContactIdentifier contact, DataCompletion completion);

}

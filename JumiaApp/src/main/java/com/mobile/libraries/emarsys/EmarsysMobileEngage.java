package com.mobile.libraries.emarsys;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.mobile.app.JumiaApplication;
import com.mobile.view.R;
import com.pushwoosh.PushManager;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by Arash Hassanpour on 4/4/2017.
 */

public class EmarsysMobileEngage {
    private static String cContactFieldId = "4819"; //Field Editor (found in 'Admin') - Emarsys Dashboard

    private Context context;

    static EmarsysDataManager instance;

    public EmarsysDataManager sharedInstance() {
        if (instance == null) {
            instance = new EmarsysDataManager();
            instance = instance.init(context);
        }

        return instance;
    }
    public EmarsysMobileEngage(Context context)
    {
        this.context = context;
    }

    public void sendLogin(String pushToken, final EmarsysMobileEngageResponse completion) {
        if(JumiaApplication.isCustomerLoggedIn()) {

            DataCompletion response = new DataCompletion() {
                @Override
                public void completion(Object data, ArrayList<String> errorMessages) {
                    completion.EmarsysMobileEngageResponse(errorMessages == null);
                }
            };

            sharedInstance().login((EmarsysPushIdentifier) getIdentifier(pushToken), cContactFieldId, ""+JumiaApplication.CUSTOMER.getId(), response);
        } else {

            DataCompletion response = new DataCompletion() {
                @Override
                public void completion(Object data, ArrayList<String> errorMessages) {
                    handleEmarsysMobileEngageResponse(data, errorMessages, completion);

                }
            };

            sharedInstance().anonymousLogin((EmarsysPushIdentifier) getIdentifier(pushToken), response);

        }
    }

    public void sendOpen(String sid, final EmarsysMobileEngageResponse completion) {
        DataCompletion response = new DataCompletion() {
            @Override
            public void completion(Object data, ArrayList<String> errorMessages) {
                handleEmarsysMobileEngageResponse(data, errorMessages, completion);

            }
        };
        sharedInstance().openMessageEvent((EmarsysContactIdentifier)getIdentifier(null), sid, response);

    }

    public void sendCustomEvent(String event, Map<String, Object> attributes, final EmarsysMobileEngageResponse completion) {

        DataCompletion response = new DataCompletion() {
            @Override
            public void completion(Object data, ArrayList<String> errorMessages) {
                handleEmarsysMobileEngageResponse(data, errorMessages, completion);

            }
        };

        sharedInstance().customEvent((EmarsysContactIdentifier)getIdentifier(null), event, attributes, response);
    }

    public void sendLogout(final EmarsysMobileEngageResponse completion) {
        DataCompletion response = new DataCompletion() {
            @Override
            public void completion(Object data, ArrayList<String> errorMessages) {
                handleEmarsysMobileEngageResponse(data, errorMessages, completion);

            }
        };

        sharedInstance().logout((EmarsysContactIdentifier)getIdentifier(null), response);
    }

    Object getIdentifier(String pushToken) {

        String appId = context.getResources().getString(R.string.PW_APPID);
        String hwid = PushManager.getPushwooshHWID(context);

        if(pushToken != null) {
            return new EmarsysPushIdentifier(appId, hwid, pushToken);
        } else {
            return new EmarsysContactIdentifier(appId, hwid);
        }
    }

    void handleEmarsysMobileEngageResponse(Object data, ArrayList<String> error, EmarsysMobileEngageResponse completion) {
        if(completion != null) {
            if(error == null) {
                completion.EmarsysMobileEngageResponse(true);
            } else {
                completion.EmarsysMobileEngageResponse(false);
            }
        }
    }

}

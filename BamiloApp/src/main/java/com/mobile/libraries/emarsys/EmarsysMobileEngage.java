package com.mobile.libraries.emarsys;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.mobile.app.BamiloApplication;
import com.mobile.view.R;
import com.pushwoosh.PushManager;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by Arash Hassanpour on 4/4/2017.
 */

public class EmarsysMobileEngage {
    private static final String cContactFieldId = "4819"; //Field Editor (found in 'Admin') - Emarsys Dashboard

    private Context mContext = null;
    private static EmarsysMobileEngage instance = null;
    private EmarsysMobileEngage(Context context) {
        this.mContext = context;
        this.emarsysDataManager = new EmarsysDataManager(context);
    }

    protected EmarsysDataManager emarsysDataManager = null;

    public static EmarsysMobileEngage getInstance(Context context) {
        if(instance == null) {
            instance = new EmarsysMobileEngage(context.getApplicationContext());
        }
        return instance;
    }

    public void sendLogin(String pushToken, final EmarsysMobileEngageResponse completion) {
        if(BamiloApplication.isCustomerLoggedIn()) {
            DataCompletion response = new DataCompletion() {
                @Override
                public void completion(Object data, ArrayList<String> errorMessages) {
                    handleEmarsysMobileEngageResponse(errorMessages, completion);
                }
            };

            this.emarsysDataManager.login((EmarsysPushIdentifier) getIdentifier(pushToken), cContactFieldId, "" + BamiloApplication.CUSTOMER.getId(), response);
        } else {
            DataCompletion response = new DataCompletion() {
                @Override
                public void completion(Object data, ArrayList<String> errorMessages) {
                    handleEmarsysMobileEngageResponse(errorMessages, completion);
                }
            };

            this.emarsysDataManager.anonymousLogin((EmarsysPushIdentifier) getIdentifier(pushToken), response);
        }
    }

    public void sendOpen(String sid, final EmarsysMobileEngageResponse completion) {
        DataCompletion response = new DataCompletion() {
            @Override
            public void completion(Object data, ArrayList<String> errorMessages) {
                handleEmarsysMobileEngageResponse(errorMessages, completion);

            }
        };

        this.emarsysDataManager.openMessageEvent((EmarsysContactIdentifier)getIdentifier(null), sid, response);
    }

    public void sendCustomEvent(String event, Map<String, Object> attributes, final EmarsysMobileEngageResponse completion) {
        DataCompletion response = new DataCompletion() {
            @Override
            public void completion(Object data, ArrayList<String> errorMessages) {
                handleEmarsysMobileEngageResponse(errorMessages, completion);

            }
        };

        this.emarsysDataManager.customEvent((EmarsysContactIdentifier)getIdentifier(null), event, attributes, response);
    }

    public void sendLogout(final EmarsysMobileEngageResponse completion) {
        DataCompletion response = new DataCompletion() {
            @Override
            public void completion(Object data, ArrayList<String> errorMessages) {
                handleEmarsysMobileEngageResponse(errorMessages, completion);

            }
        };

        this.emarsysDataManager.logout((EmarsysContactIdentifier)getIdentifier(null), response);
    }

    Object getIdentifier(String pushToken) {
        String appId = this.mContext.getResources().getString(R.string.PW_APPID);
        String hwid = PushManager.getPushwooshHWID(this.mContext);

        if(pushToken == null) {
            return new EmarsysContactIdentifier(appId, hwid);
        } else {
            return new EmarsysPushIdentifier(appId, hwid, pushToken);
        }
    }

    void handleEmarsysMobileEngageResponse(ArrayList<String> error, final EmarsysMobileEngageResponse completion) {
        if(completion != null) {
            completion.EmarsysMobileEngageResponse(error == null);
        }
    }
}

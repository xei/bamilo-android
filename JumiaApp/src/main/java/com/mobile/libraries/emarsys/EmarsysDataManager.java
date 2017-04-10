package com.mobile.libraries.emarsys;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static com.mobile.libraries.emarsys.RequestExecutionType.REQUEST_EXEC_IN_BACKGROUND;

/**
 * Created by Arash Hassanpour on 4/4/2017.
 */

public class EmarsysDataManager implements EmarsysDataManagerInterface {
    private String kApplicationId = "application_id";
    private String kHardwareId = "hardware_id";
    private String kPlatform = "platform";
    private String kLanguage = "language";
    private String kTimezone = "timezone";
    private String kDeviceModel = "device_model";
    private String kApplicationVersion = "application_version";
    private String kOSVersion = "os_version";
    private String kPushToken = "push_token";
    private String kContactFieldId = "contact_field_id";
    private String kContactFieldValue = "contact_field_value";
    private String kSID = "sid";
    private Context mContext;

    EmarsysContactIdentifier getEmarsysContactIdentifier(String appId, String hwId) {
        EmarsysContactIdentifier emarsysUserIdentifier = new EmarsysContactIdentifier(appId, hwId);

        return emarsysUserIdentifier;
    }

    EmarsysPushIdentifier getEmarsysPushIdentifier(String appId, String hwid, String pushToken) {
        EmarsysPushIdentifier emarsysPushIdentifier = new EmarsysPushIdentifier(appId, hwid, pushToken);

        return emarsysPushIdentifier;
    }

    static EmarsysDataManager instance;
    static EmarsysRequestManager requestManager;
    public EmarsysDataManager init(Context context) {
        if (requestManager == null) {
            this.requestManager = EmarsysRequestManager.initWithBaseUrl(context, "https://push.eservice.emarsys.net/api/mobileengage/v2/");
        }
        mContext = context;
        return this;
    }

    @Override
    public void anonymousLogin(EmarsysPushIdentifier contact, DataCompletion completion) {
        Map<String,String> params = commonLoginParams(mContext, contact);
        executeLogin(params, completion);
    }

    @Override
    public void login(EmarsysPushIdentifier contact, String contactFieldId, String contactFieldValue, DataCompletion completion) {
        Map<String,String> params = commonLoginParams(mContext, contact);
        params.put(kContactFieldId, contactFieldId);
        params.put(kContactFieldValue, contactFieldValue);
        executeLogin(params, completion);
    }

    @Override
    public void openMessageEvent(EmarsysContactIdentifier contact, String sid, DataCompletion completion) {
        Map<String,String> map =  new HashMap<String,String>();
        map.put(kSID, sid);
        customEvent(contact, "message_open", map, completion);

    }

    @Override
    public void customEvent(EmarsysContactIdentifier contact, String event, Map<String,String> attributes, DataCompletion completion) {
        Map<String, String> params = new HashMap<String, String>();

        params.put(kApplicationId, contact.applicationId);
        params.put(kHardwareId, contact.hardwareId);
        executeEvent(event, params, completion);

    }

    @Override
    public void logout(EmarsysContactIdentifier contact, DataCompletion completion) {
        Map<String, String> params = new HashMap<String, String>();

        params.put(kApplicationId, contact.applicationId);
        params.put(kHardwareId, contact.hardwareId);
        executeLogout(params, completion);
    }

    public static EmarsysDataManager sharedInstance(Context context) {
        if (instance == null) {
            instance = new EmarsysDataManager();
            instance = instance.init(context);
        }

        return instance;
    }


    private Map<String, String> commonLoginParams(Context context, EmarsysPushIdentifier contact) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(kApplicationId, contact.applicationId);
        params.put(kHardwareId, contact.hardwareId);
        params.put(kPlatform, "android");
        params.put(kLanguage, "fa");
        params.put(kTimezone, getTimeZone());
        params.put(kDeviceModel, getDeviceName());
        params.put(kApplicationVersion, getAppVersionNumber(context));
        params.put(kOSVersion, ""+android.os.Build.VERSION.SDK_INT);
        params.put(kPushToken, contact.pushToken.trim().compareTo("")==0 ? "false": contact.pushToken);

        return params;
    }

    public static String getTimeZone()
    {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("Z");
        String gmt = sdf.format(today);
        return gmt;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        }
        return manufacturer.toUpperCase() + " " + model.toUpperCase();
    }

    public static String getAppVersionNumber(Context context)
    {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = null;

            info = manager.getPackageInfo(context.getPackageName(), 0);

            String version = info.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    private void executeLogin(Map<String, String> params, final DataCompletion completion) {

        RequestCompletion requestCompletion = new RequestCompletion() {
            @Override
            public void completion(int status, Object response, ArrayList<String> errors) {
                handleEmarsysDataManagerResponse(status, response, errors, completion);
            }
        };

        requestManager.asyncPOST("users/login", params, REQUEST_EXEC_IN_BACKGROUND, requestCompletion);
    }

    void executeEvent(String event, Map<String, String> params, final DataCompletion completion) {

        RequestCompletion requestCompletion = new RequestCompletion() {
            @Override
            public void completion(int status, Object response, ArrayList<String> errors) {
                handleEmarsysDataManagerResponse(status, response, errors, completion);
            }
        };

        requestManager.asyncPOST(String.format("events/%s", event), params, REQUEST_EXEC_IN_BACKGROUND, requestCompletion);
    }

    void executeLogout(Map<String, String> params, final DataCompletion completion) {

        RequestCompletion requestCompletion = new RequestCompletion() {
            @Override
            public void completion(int status, Object response, ArrayList<String> errors) {
                handleEmarsysDataManagerResponse(status, response, errors, completion);
            }
        };

        requestManager.asyncPOST("users/logout", params, REQUEST_EXEC_IN_BACKGROUND, requestCompletion);

    }

    private void handleEmarsysDataManagerResponse(int statusCode, Object data, ArrayList<String> errorMessages, DataCompletion completion) {
        if(statusCode == HTTPStatusCode.CREATED.getValue() || statusCode == HTTPStatusCode.SUCCESSFUL.getValue()) {
            completion.completion(data, null);
        } else {
            //EmarsysError emarsysError = new EmarsysError("com.bamilo.android", "" + statusCode, errorMessages.get(0));
            completion.completion(null, errorMessages);

        }
    }
}

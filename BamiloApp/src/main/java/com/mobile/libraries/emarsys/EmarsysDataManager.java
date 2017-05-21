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
import java.util.Locale;
import java.util.Map;


import static com.mobile.libraries.emarsys.RequestExecutionType.REQUEST_EXEC_IN_BACKGROUND;

/**
 * Created by Arash Hassanpour on 4/4/2017.
 */

public class EmarsysDataManager implements EmarsysDataManagerInterface {
    private static final String kApplicationId = "application_id";
    private static final String kHardwareId = "hardware_id";
    private static final String kPlatform = "platform";
    private static final String kLanguage = "language";
    private static final String kTimezone = "timezone";
    private static final String kDeviceModel = "device_model";
    private static final String kApplicationVersion = "application_version";
    private static final String kOSVersion = "os_version";
    private static final String kPushToken = "push_token";
    private static final String kContactFieldId = "contact_field_id";
    private static final String kContactFieldValue = "contact_field_value";
    private static final String kSID = "sid";

    private Context mContext = null;

    protected EmarsysRequestManager emarsysRequestManager = null;

    public EmarsysDataManager(Context context) {
        this.mContext = context;
        this.emarsysRequestManager = EmarsysRequestManager.initWithBaseUrl(this.mContext, "https://push.eservice.emarsys.net/api/mobileengage/v2/");
    }

    @Override
    public void anonymousLogin(EmarsysPushIdentifier contact, DataCompletion completion) {
        Map<String,Object> params = commonLoginParams(mContext, contact);
        executeLogin(params, completion);
    }

    @Override
    public void login(EmarsysPushIdentifier contact, String contactFieldId, String contactFieldValue, DataCompletion completion) {
        Map<String,Object> params = commonLoginParams(mContext, contact);
        params.put(kContactFieldId, contactFieldId);
        params.put(kContactFieldValue, contactFieldValue);
        executeLogin(params, completion);
    }

    @Override
    public void openMessageEvent(EmarsysContactIdentifier contact, String sid, DataCompletion completion) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put(kSID, sid);
        customEvent(contact, "message_open", map, completion);
    }

    @Override
    public void customEvent(EmarsysContactIdentifier contact, String event, Map<String,Object> attributes, DataCompletion completion) {
        //Map<String, Object> params = new HashMap<String, Object>();

        attributes.put(kApplicationId, contact.applicationId);
        attributes.put(kHardwareId, contact.hardwareId);
        executeEvent(event, attributes, completion);
    }

    @Override
    public void logout(EmarsysContactIdentifier contact, DataCompletion completion) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(kApplicationId, contact.applicationId);
        params.put(kHardwareId, contact.hardwareId);
        executeLogout(params, completion);
    }

    private Map<String, Object> commonLoginParams(Context context, EmarsysPushIdentifier contact) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(kApplicationId, contact.applicationId);
        params.put(kHardwareId, contact.hardwareId);
        params.put(kPlatform, "android");
        params.put(kLanguage, Locale.getDefault().getLanguage());
        params.put(kTimezone, getTimeZone());
        params.put(kDeviceModel, getDeviceName());
        params.put(kApplicationVersion, getAppVersionNumber(context));
        params.put(kOSVersion, ""+android.os.Build.VERSION.SDK_INT);
        params.put(kPushToken, contact.pushToken.trim().compareTo("")==0 ? "false": contact.pushToken);

        return params;
    }

    public static String getTimeZone() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("Z", Locale.ENGLISH);
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

    public static String getAppVersionNumber(Context context) {
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

    private void executeLogin(Map<String, Object> params, final DataCompletion completion) {
        RequestCompletion requestCompletion = new RequestCompletion() {
            @Override
            public void completion(int status, Object response, ArrayList<String> errors) {
                handleEmarsysDataManagerResponse(status, response, errors, completion);
            }
        };

        this.emarsysRequestManager.asyncPOST("users/login", params, REQUEST_EXEC_IN_BACKGROUND, requestCompletion);
    }

    void executeEvent(String event, Map<String, Object> params, final DataCompletion completion) {
        RequestCompletion requestCompletion = new RequestCompletion() {
            @Override
            public void completion(int status, Object response, ArrayList<String> errors) {
                handleEmarsysDataManagerResponse(status, response, errors, completion);
            }
        };

        this.emarsysRequestManager.asyncPOST(String.format("events/%s", event), params, REQUEST_EXEC_IN_BACKGROUND, requestCompletion);
    }

    void executeLogout(Map<String, Object> params, final DataCompletion completion) {
        RequestCompletion requestCompletion = new RequestCompletion() {
            @Override
            public void completion(int status, Object response, ArrayList<String> errors) {
                handleEmarsysDataManagerResponse(status, response, errors, completion);
            }
        };

        this.emarsysRequestManager.asyncPOST("users/logout", params, REQUEST_EXEC_IN_BACKGROUND, requestCompletion);
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

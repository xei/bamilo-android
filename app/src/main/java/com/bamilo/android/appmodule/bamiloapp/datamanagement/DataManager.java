package com.bamilo.android.appmodule.bamiloapp.datamanagement;

import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.Metadata;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;

import java.util.HashMap;
import java.util.Map;

public final class DataManager {
    private static DataManager mInstance;
    private static Map<String, Object> offlineDataMap;

    private DataManager() {
        offlineDataMap = new HashMap<>();
    }

    public static synchronized DataManager getInstance() {
        if (mInstance == null) {
            mInstance = new DataManager();
        }
        return mInstance;
    }

    public void loadData(SuperBaseHelper helper, final Bundle args, final IResponseCallback callback) {
        Object offlineData = checkOfflineData(offlineDataMap, args);
        if (null != offlineData) {
            BaseResponse<Object> response = new BaseResponse<>();
            response.setEventType(helper.getEventType());
            response.setEventTask(helper.getEventTask());
            Metadata<Object> metadata = new Metadata<>();
            metadata.setData(offlineData);
            response.setMetadata(metadata);
            if (callback != null) {
                callback.onRequestComplete(response);
            }
        } else {
            BamiloApplication.INSTANCE.sendRequest(helper, args, new IResponseCallback() {
                @Override
                public void onRequestComplete(BaseResponse baseResponse) {
                    Object data = baseResponse.getContentData();
                    putOfflineData(data, args);
                    if (callback != null) {
                        callback.onRequestComplete(baseResponse);
                    }
                }

                @Override
                public void onRequestError(BaseResponse baseResponse) {
                    if(callback != null) {
                        callback.onRequestError(baseResponse);
                    }
                }
            });
        }
    }

    private void putOfflineData(Object data, Bundle args) {
        String endpoint = args.getString(Constants.BUNDLE_END_POINT_KEY, null);
        if (endpoint != null && data != null) {
            offlineDataMap.put(endpoint, data);
        }
    }

    private Object checkOfflineData(Map<String, Object> offlineData, Bundle args) {
        String endpoint = args.getString(Constants.BUNDLE_END_POINT_KEY, null);
        if (endpoint == null) {
            return null;
        }
        return offlineData.get(endpoint);
    }
}

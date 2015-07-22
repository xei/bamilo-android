package com.mobile.helpers;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class SuperBaseHelper implements AigResponseCallback {

    private static String TAG = SuperBaseHelper.class.getSimpleName();

    protected IResponseCallback mRequester;

    protected EventType mEventType;

    private EventTask mEventTask;

    private boolean prioritary;

    public SuperBaseHelper(){
        mEventType = getEventType();
    }

    public final void sendRequest(Bundle args, IResponseCallback requester) {
        mRequester = requester;
        setEventTask(args);
        setPriority(args);
        onRequest(createRequest(args));
    }

    protected RequestBundle createRequest(Bundle args) {
        // Create builder
        RequestBundle.Builder requestBundleBuilder = new RequestBundle.Builder()
                .setUrl(getRequestUrl(args))
                .setCache(mEventType.cacheTime);
        // Validate data
        Map<String, String> data = getRequestData(args);
        if (data != null) {
            requestBundleBuilder.setData(data);
        }
        //
        return requestBundleBuilder.build();
    }

    protected String getRequestUrl(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(mEventType.action)).toString();
    }

    protected Map<String, String> getRequestData(Bundle args) {
        return (args != null && args.containsKey(Constants.BUNDLE_DATA_KEY))? convertContentValuesToMap((ContentValues) args.getParcelable(Constants.BUNDLE_DATA_KEY)) : null;
    }

    /**
     *  Returns the helper's priority
     * @return
     */
    public boolean hasPriority(){
        return HelperPriorityConfiguration.IS_PRIORITARY;
    }

    protected abstract void onRequest(RequestBundle requestBundle);

    public abstract EventType getEventType();

    // TODO: Move this method to CollectionUtils
    public static Map<String, String> convertContentValuesToMap(ContentValues contentValues) {
        Map<String, String> data = new HashMap<>();
        for (Map.Entry entrySet: contentValues.valueSet()) {
            data.put(entrySet.getKey().toString(), entrySet.getValue() != null ? entrySet.getValue().toString() : null);
        }
        return data;
    }

    public EventTask getEventTask() {
        if (mEventTask == null) {
            mEventTask = setEventTask();
        }
        return mEventTask;
    }

    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle){
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, getEventTask());
        bundle.putString(Constants.BUNDLE_RESPONSE_SUCCESS_MESSAGE_KEY, baseResponse.getMessage());
    }

    public void createErrorBundleParams(BaseResponse baseResponse, Bundle bundle){
        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, baseResponse.getError().getErrorCode());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, prioritary);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, getEventTask());
        bundle.putSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY, (Serializable) baseResponse.getErrorMessages());
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
    }

    protected EventTask setEventTask(){
        return EventTask.NORMAL_TASK;
    }

    /**
     * Method to define event task on request helper init.
     * If args hasn't value at BUNDLE_EVENT_TASK, default is defined.
     *
     * @param args arguments with event task.
     */
    private void setEventTask(Bundle args){
        Serializable evenTask = args != null ? args.getSerializable(Constants.BUNDLE_EVENT_TASK) : null;
        mEventTask = evenTask instanceof EventTask ? (EventTask)evenTask : setEventTask();
    }

    /**
     * Method to define priority on request helper init.
     * If args hasn't value at BUNDLE_PRIORITY_KEY, helper's default is defined.
     *
     * @param args arguments with priority.
     */
    private void setPriority(Bundle args){
        prioritary = args != null && args.containsKey(Constants.BUNDLE_PRIORITY_KEY) ? args.getBoolean(Constants.BUNDLE_PRIORITY_KEY) : hasPriority();
    }

    @Override
    public final void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        Bundle bundle = new Bundle();
        createSuccessBundleParams(baseResponse, bundle);
        if(mRequester != null) {
            mRequester.onRequestComplete(bundle);
        } else {
            Print.w(TAG, "WARNING: REQUESTER IS NULL ON REQUEST COMPLETE FOR " + mEventType);
        }
    }

    @Override
    public final void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = new Bundle();
        createErrorBundleParams(baseResponse, bundle);
        if(mRequester != null) {
            mRequester.onRequestError(bundle);
        } else {
            Print.w(TAG, "WARNING: REQUESTER IS NULL ON REQUEST ERROR FOR " + mEventType);
        }
    }

}

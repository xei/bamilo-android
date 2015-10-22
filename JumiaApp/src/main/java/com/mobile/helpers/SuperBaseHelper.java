package com.mobile.helpers;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.io.Serializable;
import java.util.Map;

public abstract class SuperBaseHelper implements AigResponseCallback {

    private static String TAG = SuperBaseHelper.class.getSimpleName();

    protected IResponseCallback mRequester;

    protected EventType mEventType;

    private EventTask mEventTask;

    private boolean prioritary;

    protected ContentValues mParameters;

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

    /**
     * Get the url from bundle.<br>
     * TODO: Remove the temporary fix to support catalog >= v1.7.
     * @return
     */
    protected String getRequestUrl(Bundle args) {
        String baseUrl = (args != null) ? args.getString(Constants.BUNDLE_URL_KEY) : null;

        if(TextUtils.isEmpty(baseUrl)) {
            return RestUrlUtils.completeUri(Uri.parse(mEventType.action)).toString();
        } 
        // TODO: Temporary fix to support catalog v1.7 and v1.8.
        else {
            Uri uri = Uri.parse(baseUrl);
            appendParameters(RestUrlUtils.getQueryParameters(uri));
            if (uri.getHost() != null) {
                return uri.getScheme()+"://" + uri.getHost() + uri.getPath();
            } else {
                return RestUrlUtils.completeUri(Uri.parse(mEventType.action)).toString();
            }
        }
    }

    protected Map<String, String> getRequestData(Bundle args) {
        if (args != null && args.containsKey(Constants.BUNDLE_DATA_KEY)){
            appendParameters((ContentValues) args.getParcelable(Constants.BUNDLE_DATA_KEY));
        }

        return CollectionUtils.isNotEmpty(mParameters) ? CollectionUtils.convertContentValuesToMap(mParameters): null;
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

    public EventTask getEventTask() {
        if (mEventTask == null) {
            mEventTask = setEventTask();
        }
        return mEventTask;
    }

    public void postSuccess(BaseResponse baseResponse){
        baseResponse.setEventType(mEventType);
        baseResponse.setEventTask(getEventTask());
    }

    public void postError(BaseResponse baseResponse){
        baseResponse.setEventType(mEventType);
        baseResponse.setEventTask(getEventTask());
        baseResponse.setPrioritary(prioritary);
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
        postSuccess(baseResponse);
        if(mRequester != null) {
            mRequester.onRequestComplete(baseResponse);
        } else {
            Print.w(TAG, "WARNING: REQUESTER IS NULL ON REQUEST COMPLETE FOR " + mEventType);
        }
    }

    @Override
    public final void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        postError(baseResponse);
        if(mRequester != null) {
            mRequester.onRequestError(baseResponse);
        } else {
            Print.w(TAG, "WARNING: REQUESTER IS NULL ON REQUEST ERROR FOR " + mEventType);
        }
    }

    protected final void appendParameters(ContentValues parameters){
        if(CollectionUtils.isNotEmpty(parameters)){
            if(this.mParameters == null){
                this.mParameters = new ContentValues();
            }
            this.mParameters.putAll(parameters);
        }
    }

}

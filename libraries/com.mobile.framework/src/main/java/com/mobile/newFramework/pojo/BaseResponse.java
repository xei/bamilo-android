package com.mobile.newFramework.pojo;

import android.support.annotation.Nullable;

import com.mobile.newFramework.rest.errors.AigError;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to represent a MobApi response.
 *
 * @author spereira
 */
public class BaseResponse<T> {

    private boolean mSuccess;
    private HashMap<String, String> mErrorMessages;
    private HashMap<String, String> mSuccessMessages;
    private HashMap<String, String> mValidateMessages;
    private AigError mAigError;
    private Metadata<T> mMetadata;
    private EventType mEventType;
    private EventTask mEventTask;
    private boolean isPriority;

    public BaseResponse() {
        setMetadata(new Metadata<T>());
    }

    public BaseResponse(EventType eventType, @ErrorCode.Code int errorCode) {
        setEventType(eventType);
        AigError aigError = new AigError();
        aigError.setCode(errorCode);
        setError(aigError);
    }

    public boolean hadSuccess() {
        return mSuccess;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public String getErrorMessage() {
        return concatMessages(mErrorMessages);
    }

    public String getSuccessMessage() {
        return concatMessages(mSuccessMessages);
    }

    public String getValidateMessage() {
        return concatMessages(mValidateMessages);
    }

    @Nullable
    public HashMap<String, String> getErrorMessages() {
        return mErrorMessages;
    }

    @Nullable
    public HashMap<String, String> getSuccessMessages() {
        return mSuccessMessages;
    }

    @Nullable
    public HashMap<String, String> getValidateMessages() {
        return mValidateMessages;
    }

    public T getContentData() {
        return mMetadata.getData();
    }

    /**
     * To get the response data use the {@link #getContentData()} method.
     */
    public Metadata<T> getMetadata() {
        return mMetadata;
    }

    public void setMetadata(Metadata<T> mMetadata) {
        this.mMetadata = mMetadata;
    }

    public AigError getError() {
        return mAigError;
    }

    public EventType getEventType() {
        return mEventType;
    }

    public EventTask getEventTask() {
        return mEventTask;
    }

    public void setEventType(EventType mEventType) {
        this.mEventType = mEventType;
    }

    public void setEventTask(EventTask mEventTask) {
        this.mEventTask = mEventTask;
    }

    public void setPriority(boolean priority) {
        this.isPriority = priority;
    }

    public void setErrorMessages(@Nullable HashMap<String, String> map) {
        this.mErrorMessages = map;
    }

    public void setSuccessMessages(@Nullable HashMap<String, String> map) {
        this.mSuccessMessages = map;
    }

    public void setValidateMessages(@Nullable HashMap<String, String> map) {
        this.mValidateMessages = map;
    }

    public void setError(AigError mAigError) {
        this.mAigError = mAigError;
    }

    public void setSuccess(boolean mSuccess) {
        this.mSuccess = mSuccess;
    }

    private String concatMessages(@Nullable Map<String, String> map) {
        String message = "";
        if(CollectionUtils.isNotEmpty(map)) {
            for(Map.Entry<String, String> entry: map.entrySet()) {
                message += entry.getValue() + "\n";
            }
        }
        return message;
    }

}



package com.mobile.newFramework.pojo;

import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.rest.errors.JumiaError;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

import java.util.List;
import java.util.Map;

/**
 * Created by spereira on 5/19/15.
 */
public class BaseResponse<T>{
    private boolean success;
    private Map<String, String> successMessages;
    private Map<String,List<String>> errorMessages;
    //private remove message variable as soon as possible
    private String message;
    private Metadata<T> metadata;
    private Map<String, String> sessions;
    private JumiaError error;

    private EventType eventType;
    private EventTask eventTask;
    private boolean prioritary;

    public BaseResponse(){
        setMetadata(new Metadata<T>());
    }

    public BaseResponse(EventType eventType, ErrorCode errorCode){
        setEventType(eventType);
        JumiaError jumiaError = new JumiaError();
        jumiaError.setErrorCode(errorCode);
        setError(jumiaError);
    }

    public boolean hadSuccess() {
        return success;
    }


    public Map<String, String> getSuccessMessages() {
        return successMessages;
    }

    public void setSuccessMessages(Map<String, String> successMessages) {
        this.successMessages = successMessages;
    }

    public Map<String, List<String>> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Map<String, List<String>> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Metadata<T> getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata<T> metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getSessions() {
        return sessions;
    }

    public void setSessions(Map<String, String> sessions) {
        this.sessions = sessions;
    }

    public JumiaError getError() {
        return error;
    }

    public void setError(JumiaError error) {
        this.error = error;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventTask getEventTask() {
        return eventTask;
    }

    public void setEventTask(EventTask eventTask) {
        this.eventTask = eventTask;
    }

    public boolean isPrioritary() {
        return prioritary;
    }

    public void setPrioritary(boolean prioritary) {
        this.prioritary = prioritary;
    }
}



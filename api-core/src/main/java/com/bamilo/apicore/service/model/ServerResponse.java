package com.bamilo.apicore.service.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created on 12/19/2017.
 * All model objects should implement this interface
 */

public abstract class ServerResponse {

    @SerializedName(JsonConstants.RestConstants.SUCCESS)
    @Expose
    private boolean success;

    @Expose
    @SerializedName(JsonConstants.RestConstants.MESSAGES)
    private ServerMessages messages;

    public ServerResponse(JsonObject jsonObject, Gson gson) {
        initializeWithJson(jsonObject, gson);
    }

    public abstract EventType getEventType();

    public abstract EventTask getEventTask();

    protected abstract void initializeWithJson(JsonObject jsonObject, Gson gson);

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ServerMessages getMessages() {
        return messages;
    }

    public void setMessages(ServerMessages messages) {
        this.messages = messages;
    }

    public static class ServerError {
        @Expose
        @SerializedName("reason")
        private String reason;
        @Expose
        @SerializedName("message")
        private String message;
        @Expose
        @SerializedName("code")
        private int errorCode;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }
    }

    public static class ServerMessages {
        @Expose
        @SerializedName(JsonConstants.RestConstants.ERROR)
        private List<ServerError> errors;

        public List<ServerError> getErrors() {
            return errors;
        }

        public void setErrors(List<ServerError> errors) {
            this.errors = errors;
        }
    }
}

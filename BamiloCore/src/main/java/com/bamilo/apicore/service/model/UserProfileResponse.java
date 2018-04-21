package com.bamilo.apicore.service.model;

import com.bamilo.apicore.service.model.EventTask;
import com.bamilo.apicore.service.model.EventType;
import com.bamilo.apicore.service.model.JsonConstants;
import com.bamilo.apicore.service.model.ServerResponse;
import com.bamilo.apicore.service.model.data.profile.UserProfile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohsen on 2/26/18.
 */

public class UserProfileResponse extends ServerResponse {

    @Expose
    @SerializedName(JsonConstants.RestConstants.METADATA)
    private UserProfileMetaData userProfileMetaData;

    public UserProfileResponse() {
        super(null, null);
    }

    public UserProfileResponse(JsonObject jsonObject, Gson gson) {
        super(jsonObject, gson);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_CUSTOMER;
    }

    @Override
    public EventTask getEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected void initializeWithJson(JsonObject jsonObject, Gson gson) {
        if (gson != null && jsonObject != null) {
            UserProfileResponse response = gson.fromJson(jsonObject, UserProfileResponse.class);
            this.userProfileMetaData = response.userProfileMetaData;
            this.setSuccess(response.isSuccess());
            this.setMessages(response.getMessages());
        }
    }

    public UserProfile getUserProfile() {
        if (userProfileMetaData == null) {
            return null;
        }
        return userProfileMetaData.getUserProfile();
    }

    public String getWarningMessage() {
        if (userProfileMetaData == null) {
            return null;
        }
        return userProfileMetaData.getWarningMessage();
    }

    public UserProfileMetaData getUserProfileMetaData() {
        return userProfileMetaData;
    }

    public void setUserProfileMetaData(UserProfileMetaData userProfileMetaData) {
        this.userProfileMetaData = userProfileMetaData;
    }

    public static class UserProfileMetaData {
        @Expose
        @SerializedName("customer_entity")
        private UserProfile userProfile;
        @Expose
        @SerializedName("warning_message")
        private String warningMessage;

        public UserProfile getUserProfile() {
            return userProfile;
        }

        public void setUserProfile(UserProfile userProfile) {
            this.userProfile = userProfile;
        }

        public String getWarningMessage() {
            return warningMessage;
        }

        public void setWarningMessage(String warningMessage) {
            this.warningMessage = warningMessage;
        }
    }
}

package com.mobile.newFramework.rest;

import com.mobile.framework.objects.Errors;
import com.mobile.framework.objects.Success;
import com.mobile.framework.rest.RestConstants;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.BaseResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by rsoares on 5/21/15.
 */
public class ResponseConverter implements Converter{

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        try {
            String bodyJson = IOUtils.toString(body.in());
            JSONObject responseJsonObject = new JSONObject(bodyJson);
            return parseResponse(responseJsonObject, type);
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        return null;
    }

    protected String getType(Object type){
        if(type instanceof Class){
            return ((Class) type).getName();
        } else if(type instanceof ParameterizedType){
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            return getType(types[types.length-1]);
        }
        return null;
    }

    protected BaseResponse parseResponse(JSONObject responseJsonObject, Type dataType)
            throws JSONException, IllegalAccessException, ClassNotFoundException, InstantiationException {

        BaseResponse<?> response = new BaseResponse<>();
        response.setSuccess(responseJsonObject.optBoolean(RestConstants.JSON_SUCCESS_TAG, false));
        if(response.hadSuccess()) {
            parseSuccessResponse(response, responseJsonObject, dataType);
        } else {
            parseUnsuccessResponse(response, responseJsonObject, dataType);
        }
        return response;
    }

    protected void parseSuccessResponse(BaseResponse<?> baseResponse, JSONObject responseJsonObject, Type dataType)
            throws JSONException, IllegalAccessException, InstantiationException, ClassNotFoundException {

        // Data
        if (responseJsonObject.has(RestConstants.JSON_METADATA_TAG)) {
            baseResponse.getMetadata().setData(getData(responseJsonObject, dataType));
            //TODO change to use method getMessages when response from API is coming correctly
        }

        // Messages
        JSONObject messagesJsonObject = responseJsonObject.optJSONObject(RestConstants.JSON_MESSAGES_TAG);
        baseResponse.setMessage(handleSuccessMessage(messagesJsonObject));

        try {
            baseResponse.setSuccessMessages(Success.createMap(messagesJsonObject));
        } catch (JSONException ex){
            //TODO
        }
        baseResponse.setErrorMessages(Errors.createErrorMessageMap(messagesJsonObject));

        //Sessions
        baseResponse.setSessions(getSessions(responseJsonObject));
        //md5
        baseResponse.getMetadata().setMd5(getMd5(responseJsonObject));
    }

    protected void parseUnsuccessResponse(BaseResponse<?> baseResponse, JSONObject responseJsonObject, Type dataType) throws JSONException {
        //body data
        try{
            baseResponse.getMetadata().setData(getData(responseJsonObject, dataType));
        } catch (ClassNotFoundException | InstantiationException | NullPointerException | JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //TODO change to use method getMessages when response from API is coming correctly
        baseResponse.setErrorMessages(Errors.createErrorMessageMap(responseJsonObject.optJSONObject(RestConstants.JSON_MESSAGES_TAG)));
        baseResponse.setSessions(getSessions(responseJsonObject));
    }

    protected IJSONSerializable getData(JSONObject responseJsonObject, Type dataType)
            throws JSONException, IllegalAccessException, InstantiationException, ClassNotFoundException {

        String objectType = getType(dataType);
        if(!objectType.equals(Void.class.getName())) {

            IJSONSerializable iJsonSerializable = new DeserializableFactory().createObject(objectType);
            iJsonSerializable.initialize(getJsonToInitialize(responseJsonObject, iJsonSerializable));
            return iJsonSerializable;
        }
        return null;
    }

    //TODO temporary method
    protected String handleSuccessMessage(JSONObject messagesObject){
        String successMessage = null;
        try {
        if (messagesObject != null) {
            JSONArray successArray = messagesObject.optJSONArray(RestConstants.JSON_SUCCESS_TAG);
            if(successArray != null){
                successMessage = successArray.getString(0);
            }
        }
        } catch (JSONException e){
            return successMessage;
        }
        return successMessage;
    }

    protected Map<String, List<String>> getMessages(JSONObject responseJsonObject) {
        Map<String, List<String>> messages = new HashMap<>();
        try {
            if (responseJsonObject.has(RestConstants.JSON_MESSAGES_TAG)) {
                JSONObject messagesJsonObject = responseJsonObject.getJSONObject(RestConstants.JSON_MESSAGES_TAG);
                Iterator<?> keys = messagesJsonObject.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    List<String> stringList = new LinkedList<>();
                    if (messagesJsonObject.get(key) instanceof JSONArray) {
                        JSONArray jsonArray = messagesJsonObject.getJSONArray(key);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stringList.add(jsonArray.getString(i));
                        }
                    }
                    messages.put(key, stringList);
                }
            }
        } finally {
            return messages;
        }
    }

    protected Map<String, String> getSessions(JSONObject responseJsonObject) {
        Map<String, String> sessions = new HashMap<>();
        try {
            if (responseJsonObject.has(RestConstants.JSON_SESSION_TAG)) {
                JSONObject sessionJsonObject = responseJsonObject.getJSONObject(RestConstants.JSON_SESSION_TAG);
                Iterator<?> keys = sessionJsonObject.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (sessionJsonObject.get(key) instanceof String) {
                        sessions.put(key, sessionJsonObject.getString(key));
                    }
                }
            }
        } finally {
            return sessions;
        }
    }

    protected JSONObject getJsonToInitialize(JSONObject responseJsonObject, final IJSONSerializable iJsonSerializable) throws JSONException {
        RequiredJson requiredJson = iJsonSerializable.getRequiredJson();
        if(requiredJson == RequiredJson.METADATA){
            return responseJsonObject.getJSONObject(RestConstants.JSON_METADATA_TAG);
        } else if(requiredJson == RequiredJson.OBJECT_DATA){
            return responseJsonObject.getJSONObject(RestConstants.JSON_METADATA_TAG).getJSONObject(RestConstants.JSON_DATA_TAG);
        }
        return responseJsonObject;
    }

    public String getMd5(JSONObject responseJsonObject) {
        if(responseJsonObject.has(RestConstants.JSON_METADATA_TAG)){
            return responseJsonObject.optJSONObject(RestConstants.JSON_METADATA_TAG).optString(RestConstants.JSON_MD5_TAG, null);
        }
        return null;
    }
}

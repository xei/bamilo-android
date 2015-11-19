package com.mobile.newFramework.rest;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.pojo.Success;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okio.Okio;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by rsoares on 5/21/15.
 */
public class AigResponseConverter implements Converter {

    private static final String TAG = AigResponseConverter.class.getSimpleName();

    /**
     * Convert json response
     */
    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        Print.i(TAG, "PARSE FROM BODY");
        try {
            String bodyJson = Okio.buffer(Okio.source(body.in())).readUtf8();
            if(bodyJson.length() > 3500){
                int part = 0;
                int partNumber = 1;
                while(part+3500 < bodyJson.length()){
                    Print.i(TAG, "code1size PART"+partNumber+" "+bodyJson.substring(part,part+3500));
                    partNumber++;
                    part +=3500;
                }
                Print.i(TAG, "code1size PART"+partNumber+" "+bodyJson.substring(part,bodyJson.length()));
            } else {
                Print.i(TAG, "code1size "+bodyJson);
            }

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

    /**
     * Get object type
     */
    protected String getType(Object type){
        Print.i(TAG, "GET OBJECT TYPE: " + type);
        if(type instanceof Class){
            return ((Class) type).getName();
        } else if(type instanceof ParameterizedType){
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            return getType(types[types.length-1]);
        }
        return null;
    }

    /**
     * Parse response
     */
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

    /**
     * Parse success response
     */
    protected void parseSuccessResponse(BaseResponse<?> baseResponse, JSONObject responseJsonObject, Type dataType)
            throws JSONException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Print.i(TAG, "PARSE SUCCESS RESPONSE");
        // Data
        //TODO change to use method getMessages when response from API is coming correctly
        if (responseJsonObject.has(RestConstants.JSON_METADATA_TAG)) {
            baseResponse.getMetadata().setData(getData(responseJsonObject, dataType));
        }

        // Messages
        JSONObject messagesJsonObject = responseJsonObject.optJSONObject(RestConstants.JSON_MESSAGES_TAG);
        baseResponse.setMessage(handleSuccessMessage(messagesJsonObject));

        try {
            baseResponse.setSuccessMessages(Success.createMap(messagesJsonObject));
        } catch (JSONException e){
            Print.i(TAG, "WARNING: JSE ON CREATE SUCCESS MESSAGES", e);
        }
        baseResponse.setErrorMessages(Errors.createErrorMessageMap(messagesJsonObject));

        //Sessions
        baseResponse.setSessions(getSessions(responseJsonObject));
        //md5
        baseResponse.getMetadata().setMd5(getMd5(responseJsonObject));
    }


    /**
     * Get unsuccess messages
     */
    protected void parseUnsuccessResponse(BaseResponse<?> baseResponse, JSONObject responseJsonObject, Type dataType) throws JSONException {
        Print.i(TAG, "PARSE FAILURE RESPONSE");
        //body data
        try{
            baseResponse.getMetadata().setData(getData(responseJsonObject, dataType));
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }
        //TODO change to use method getMessages when response from API is coming correctly
        baseResponse.setErrorMessages(Errors.createErrorMessageMap(responseJsonObject.optJSONObject(RestConstants.JSON_MESSAGES_TAG)));
        baseResponse.setSessions(getSessions(responseJsonObject));
    }

    /**
     * Get data
     */
    protected IJSONSerializable getData(JSONObject responseJsonObject, Type dataType)
            throws JSONException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        String objectType = getType(dataType);
        Print.i(TAG, "GET DATA: " + objectType);
        if(objectType != null && !objectType.equals(Void.class.getName())) {
            IJSONSerializable iJsonSerializable = new DeserializableFactory().createObject(objectType);
            iJsonSerializable.initialize(getJsonToInitialize(responseJsonObject, iJsonSerializable));
            return iJsonSerializable;
        }
        return null;
    }

    /**
     * Get success messages
     */
    protected String handleSuccessMessage(JSONObject messagesObject) {
        String successMessage = null;
        try {
            if (messagesObject != null) {
                JSONArray successArray = messagesObject.optJSONArray(RestConstants.JSON_SUCCESS_TAG);
                if (successArray != null) {
                    successMessage = successArray.getString(0);
                }
            }
        } catch (JSONException e) {
            Print.w(TAG, "WARNING HANDLE SUCCESS MESSAGE", e);
        }
        return successMessage;
    }

//    /**
//     * Get messages
//     */
//    protected Map<String, List<String>> getMessages(JSONObject responseJsonObject) {
//        Map<String, List<String>> messages = new HashMap<>();
//        try {
//            if (responseJsonObject.has(RestConstants.JSON_MESSAGES_TAG)) {
//                JSONObject messagesJsonObject = responseJsonObject.getJSONObject(RestConstants.JSON_MESSAGES_TAG);
//                Iterator<?> keys = messagesJsonObject.keys();
//
//                while (keys.hasNext()) {
//                    String key = (String) keys.next();
//                    List<String> stringList = new LinkedList<>();
//                    if (messagesJsonObject.get(key) instanceof JSONArray) {
//                        JSONArray jsonArray = messagesJsonObject.getJSONArray(key);
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            stringList.add(jsonArray.getString(i));
//                        }
//                    }
//                    messages.put(key, stringList);
//                }
//            }
//        } finally {
//            return messages;
//        }
//    }

    /**
     * Get session
     */
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

    /**
     * Get data from:</br>
     * -  metadata</br>
     * -  metadata -> data</br>
     */
    protected JSONObject getJsonToInitialize(JSONObject responseJsonObject, final IJSONSerializable iJsonSerializable) throws JSONException {
        Print.i(TAG, "GET DATA FROM JSON");
        @RequiredJson.JsonStruct int requiredJson = iJsonSerializable.getRequiredJson();
        if(requiredJson == RequiredJson.METADATA){
            return responseJsonObject.getJSONObject(RestConstants.JSON_METADATA_TAG);
        } else if(requiredJson == RequiredJson.OBJECT_DATA){
            return responseJsonObject.getJSONObject(RestConstants.JSON_METADATA_TAG).getJSONObject(RestConstants.JSON_DATA_TAG);
        } else if(requiredJson == RequiredJson.ARRAY_DATA_FIRST){
            return responseJsonObject.getJSONObject(RestConstants.JSON_METADATA_TAG).getJSONArray(RestConstants.JSON_DATA_TAG).getJSONObject(0);
        }
        return responseJsonObject;
    }

    /**
     * Md5 from metadata
     */
    public String getMd5(JSONObject responseJsonObject) {
        if(responseJsonObject.has(RestConstants.JSON_METADATA_TAG)){
            return responseJsonObject.optJSONObject(RestConstants.JSON_METADATA_TAG).optString(RestConstants.JSON_MD5_TAG, null);
        }
        return null;
    }
}

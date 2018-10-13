package com.bamilo.android.framework.service.rest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.rest.errors.AigError;
import com.bamilo.android.framework.service.rest.errors.ErrorCode;
import com.bamilo.android.framework.service.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import okio.Okio;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Class used to convert the JSON response into an object.
 * @author rsoares
 */
public class AigResponseConverter implements Converter {

    private static final String TAG = AigResponseConverter.class.getSimpleName();

    /**
     * Convert json response
     */
    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        try {
            String bodyJson = Okio.buffer(Okio.source(body.in())).readUtf8();
            return parseResponse(new JSONObject(bodyJson), type);
        } catch (Exception e) {
            //Print.w(TAG, "ON FROM BODY RESPONSE", e);
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
    private BaseResponse parseResponse(JSONObject jsonObject, Type dataType) throws NullPointerException, JSONException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        BaseResponse<?> response = new BaseResponse<>();
        // Get success
        response.setSuccess(jsonObject.optBoolean(RestConstants.SUCCESS));
        // Get messages
        parseResponseMessages(response, jsonObject);
        // Validate error messages
        validateErrorMessages(response);
        // Get data
        parseResponseData(response, jsonObject, dataType);
        // return response
        return response;
    }

    /**
     * Method used to save md5 and data.
     */
    private void parseResponseData(@NonNull BaseResponse<?> response, @NonNull JSONObject json, Type type) throws NullPointerException, JSONException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        // Case success get metadata || Case error get metadata if exists
        if(response.hadSuccess() || json.has(RestConstants.METADATA)) {
            response.getMetadata().setMd5(getMd5(json));
            response.getMetadata().setData(getData(json, type));
        }
    }

    /**
     * Method used to save messages.
     */
    private void parseResponseMessages(@NonNull BaseResponse<?> baseResponse, @NonNull JSONObject response) {
        // Get messages field
        JSONObject messages = response.optJSONObject(RestConstants.MESSAGES);
        if (messages != null) {
            // Get error messages
            baseResponse.setErrorMessages(parseMessages(messages.optJSONArray(RestConstants.ERROR)));
            // Get success messages
            baseResponse.setSuccessMessages(parseMessages(messages.optJSONArray(RestConstants.SUCCESS)));
            // Get validate messages
            baseResponse.setValidateMessages(parseMessages(messages.optJSONArray(RestConstants.VALIDATE)));
        }
    }

    /**
     * Validate the error code and create respective {@link AigError}.
     */
    private void validateErrorMessages(@NonNull BaseResponse<?> baseResponse) {
        // Case CUSTOMER_NOT_LOGGED_IN (231)
        if (CollectionUtils.containsKey(baseResponse.getErrorMessages(), String.valueOf(ErrorCode.CUSTOMER_NOT_LOGGED_IN))) {
            baseResponse.setError(new AigError().setCode(ErrorCode.CUSTOMER_NOT_LOGGED_IN));
        }
        // Case generic REQUEST_ERROR
        else {
            baseResponse.setError(new AigError().setCode(ErrorCode.REQUEST_ERROR));
        }
    }

    /**
     * Method used to parse messages.
     */
    private HashMap<String, String> parseMessages(@Nullable JSONArray json) {
        HashMap<String, String> map = null;
        if (CollectionUtils.isNotEmpty(json)) {
            map = new HashMap<>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject item = json.optJSONObject(i);
                if (item != null) {
                    // Case error message
                    String key = item.has(RestConstants.CODE) ? item.optString(RestConstants.CODE) : item.optString(RestConstants.REASON);
                    // Case validation message
                    if(item.has(RestConstants.FIELD)) key = item.optString(RestConstants.FIELD);
                    // Save <key, message>
                    map.put(key, item.optString(RestConstants.MESSAGE));
                }
            }
        }
        return map;
    }

    /**
     * Get data
     */
    private @Nullable IJSONSerializable getData(JSONObject responseJsonObject, Type dataType) throws NullPointerException, JSONException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        String objectType = getType(dataType);
        if(objectType != null && !objectType.equals(Void.class.getName())) {
            IJSONSerializable iJsonSerializable = new DeserializableFactory().createObject(objectType);
            if (iJsonSerializable != null) {
                iJsonSerializable.initialize(getJsonToInitialize(responseJsonObject, iJsonSerializable));
            }
            return iJsonSerializable;
        }
        return null;
    }

    /**
     * Get data from:</br>
     * -  metadata</br>
     * -  metadata -> data</br>
     */
    private JSONObject getJsonToInitialize(JSONObject responseJsonObject, IJSONSerializable iJsonSerializable) throws JSONException {
        int requiredJson = iJsonSerializable.getRequiredJson();
        // Get json from metadata
        if(requiredJson == RequiredJson.METADATA){
            return responseJsonObject.getJSONObject(RestConstants.METADATA);
        }
        // Get json from metadata -> data
        else if(requiredJson == RequiredJson.OBJECT_DATA){
            return responseJsonObject.getJSONObject(RestConstants.METADATA).getJSONObject(RestConstants.DATA);
        }
        return responseJsonObject;
    }

    /**
     * Md5 from metadata
     */
    private @Nullable String getMd5(@NonNull JSONObject responseJsonObject) {
        if(responseJsonObject.has(RestConstants.METADATA)){
            return responseJsonObject.optJSONObject(RestConstants.METADATA).optString(RestConstants.MD5, null);
        }
        return null;
    }

}

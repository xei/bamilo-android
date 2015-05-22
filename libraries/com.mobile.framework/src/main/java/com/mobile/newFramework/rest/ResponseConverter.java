package com.mobile.newFramework.rest;

import com.mobile.framework.objects.IJSONSerializable;
import com.mobile.newFramework.pojo.BaseResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

            IJSONSerializable ijsonSerializable = new DeserializableFactory().createObject(getType(type));

            String bodyJson = IOUtils.toString(body.in());
            JSONObject jsonObject = new JSONObject(bodyJson);
            ijsonSerializable.initialize(jsonObject.getJSONObject("metadata"));
            BaseResponse response = new BaseResponse();
            response.metadata.setData(ijsonSerializable);

            return response;
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        return null;
    }

    public String getType(Object type){
        if(type instanceof Class){
            return ((Class) type).getName();
        } else if(type instanceof ParameterizedType){
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            return getType(types[types.length-1]);
        }
        return null;
    }
}

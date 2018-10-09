package com.bamilo.android.framework.service.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents an input form.
 *
 * @author GuilhermeSilva
 */
public class Form implements IJSONSerializable, Parcelable {

    private int mType;
    private String mMethod;
    private String mAction;
    private HashMap<String, Form> mSubFormsMap;
    private LinkedHashMap<String, FormField> mFormFieldsMap;
    private boolean hideAsterisks;

    @Override
    @RequiredJson.JsonStruct
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    /**
     * Form empty constructor.
     */
    public Form() {
        this.mMethod = "";
        this.mAction = "";
        this.mSubFormsMap = new HashMap<>();
        this.mFormFieldsMap = new LinkedHashMap<>();
    }

    public Map<String, Form> getSubFormsMap() {
        return mSubFormsMap;
    }

    public List<FormField> getFields() {
        return new ArrayList<>(mFormFieldsMap.values());
    }

    public String getAction() {
        return mAction;
    }

    public Map<String, FormField> getFieldKeyMap() {
        return mFormFieldsMap;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public int getType() {
        return this.mType;
    }

    public void hideAsterisks() {
        hideAsterisks = true;
    }

    public boolean isToHideAsterisks() {
        return hideAsterisks;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject json) throws JSONException {
        JSONObject jsonObject = json.getJSONObject(RestConstants.FORM_ENTITY);
        initAsSubForm(jsonObject);
        return true;
    }

    public boolean initAsSubForm(JSONObject jsonObject) {
        try {
            mMethod = jsonObject.optString(RestConstants.METHOD);
            mAction = jsonObject.optString(RestConstants.ACTION);
            JSONArray fieldsArray = jsonObject.getJSONArray(RestConstants.FIELDS);
            for (int i = 0; i < fieldsArray.length(); ++i) {
                FormField field = new FormField();
                if (field.initialize(fieldsArray.getJSONObject(i))) {
                    mFormFieldsMap.put(field.getKey(), field);
                }
            }
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.TYPE, mType);
            jsonObject.put(RestConstants.METHOD, mMethod);
            jsonObject.put(RestConstants.ACTION, mAction);
            JSONArray fieldArray = new JSONArray();
            for (FormField field : mFormFieldsMap.values()) {
                fieldArray.put(field.toJSON());
            }
            jsonObject.put(RestConstants.FIELDS, fieldArray);
        } catch (JSONException e) {
        }
        return jsonObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mType);
        dest.writeString(mMethod);
        dest.writeString(mAction);
        dest.writeMap(mSubFormsMap);
        dest.writeMap(mFormFieldsMap);
        dest.writeByte((byte) (hideAsterisks ? 0x01 : 0x00));
    }

    /**
     * Parcel constructor
     */
    @SuppressWarnings("unchecked")
    private Form(Parcel in) {
        mType = in.readInt();
        mMethod = in.readString();
        mAction = in.readString();
        mSubFormsMap = new HashMap<>();
        in.readMap(mSubFormsMap, Form.class.getClassLoader());

        mFormFieldsMap = new LinkedHashMap<>();
        in.readMap(mFormFieldsMap, FormField.class.getClassLoader());

        hideAsterisks = in.readByte() != 0x00;
    }

    /**
     * Create parcelable
     */
    public static final Creator<Form> CREATOR = new Creator<Form>() {
        public Form createFromParcel(Parcel in) {
            return new Form(in);
        }

        public Form[] newArray(int size) {
            return new Form[size];
        }
    };

}

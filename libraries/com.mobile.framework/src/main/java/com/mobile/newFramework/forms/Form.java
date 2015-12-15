package com.mobile.newFramework.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents an input form.
 *
 * @author GuilhermeSilva
 */
public class Form implements IJSONSerializable, Parcelable {

    public final static String TAG = Form.class.getSimpleName();

    private int mType;
    private String method;
    private String action;
    private final ArrayList<FormField> fields;
    private Map<String, Form> subForms;
    private Map<String, FormField> mFieldKeyMap;
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
        this.method = "";
        this.action = "";
        this.fields = new ArrayList<>();
        this.subForms = new HashMap<>();
        this.mFieldKeyMap = new HashMap<>();
    }

    public Map<String, Form> getSubForms() {
        return subForms;
    }

    public ArrayList<FormField> getFields() {
        return fields;
    }

    public String getAction() {
        return action;
    }

    public Map<String, FormField> getFieldKeyMap() {
        return mFieldKeyMap;
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
            method = jsonObject.optString(RestConstants.METHOD);
            action = jsonObject.optString(RestConstants.ACTION);
            JSONArray fieldsArray = jsonObject.getJSONArray(RestConstants.FIELDS);
            for (int i = 0; i < fieldsArray.length(); ++i) {
                FormField field = new FormField(this);
                if (field.initialize(fieldsArray.getJSONObject(i))) {
                    fields.add(field);
                    mFieldKeyMap.put(field.getKey(), field);
                }
            }
        } catch (JSONException e) {
            Print.d("initialize: error parsing jsonobject" + e);
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
            jsonObject.put(RestConstants.METHOD, method);
            jsonObject.put(RestConstants.ACTION, action);
            JSONArray fieldArray = new JSONArray();
            for (FormField field : fields) {
                fieldArray.put(field.toJSON());
            }
            jsonObject.put(RestConstants.FIELDS, fieldArray);
        } catch (JSONException e) {
            Print.d("trying to create json objects failed" + e);
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
        dest.writeString(method);
        dest.writeString(action);
        dest.writeList(fields);
    }

    /**
     * Parcel constructor
     */
    private Form(Parcel in) {
        mType = in.readInt();
        method = in.readString();
        action = in.readString();
        fields = new ArrayList<>();
        in.readArrayList(FormField.class.getClassLoader());
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

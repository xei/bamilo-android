package com.mobile.newFramework.objects.home.object;

import android.os.Parcel;

import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent a form inside a teaser on the Home Page.
 *
 * @author Paulo Carvalho
 */
public class TeaserFormObject extends BaseTeaserObject {
    private Form mForm;

    /**
     * Constructor
     */
    public TeaserFormObject(int teaserTypeId) {
        super(teaserTypeId);
    }

    /*
     * ########## GETTERS ##########
     */

    public Form getForm() {
        return mForm;
    }

    /*
     * ########## JSON ##########
     */

    /**
     * Initialize
     *
     * @param jsonObject JSONObject containing the parameters of the object
     * @throws JSONException
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get data
        JSONObject formData = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
        if (formData != null) {
            mForm = new Form();
            mForm.initialize(formData);
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    /*
     * ########## PARCELABLE ##########
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(mForm);
    }

    private TeaserFormObject(Parcel in) {
        super(in);
        mForm = (Form) in.readValue(Form.class.getClassLoader());
    }

    public static final Creator<TeaserFormObject> CREATOR = new Creator<TeaserFormObject>() {
        public TeaserFormObject createFromParcel(Parcel source) {
            return new TeaserFormObject(source);
        }

        public TeaserFormObject[] newArray(int size) {
            return new TeaserFormObject[size];
        }
    };
}

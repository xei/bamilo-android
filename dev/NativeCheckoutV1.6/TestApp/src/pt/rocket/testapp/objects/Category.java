package pt.rocket.testapp.objects;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import pt.rocket.framework.interfaces.IJSONSerializable;

public class Category implements IJSONSerializable, Parcelable {

    private String id;
    private String name;
    
    public Category(){
        id="";
        name="";
    }

    public Category(Parcel in) {
        id = in.readString();
        name = in.readString();

    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // TODO Auto-generated method stub
        out.writeString(id);
        out.writeString(name);

    }
    
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    
    @Override
    public boolean initialize(JSONObject obj) {
        // TODO Auto-generated method stub
        id = obj.optString(JSONConstants.JSON_CATALOG_CATEGORY_ID_TAG, "");
        name = obj.optString(JSONConstants.JSON_NAME_TAG, "");

        return true;
    }

}

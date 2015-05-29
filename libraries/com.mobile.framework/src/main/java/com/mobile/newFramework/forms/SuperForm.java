//package com.mobile.newFramework.forms;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.framework.rest.RestConstants;
//import com.mobile.framework.utils.EventType;
//import com.mobile.newFramework.objects.IJSONSerializable;
//import com.mobile.newFramework.objects.Image;
//import com.mobile.newFramework.objects.RequiredJson;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
///**
// * Defines the super object to handle data from the form.
// * @author Paulo Carvalho
// *
// */
//public class SuperForm extends Form implements IJSONSerializable {
//
//    //private ArrayList<Form> forms;
//
//    public SuperForm() {
//    }
//
//
//    /* (non-Javadoc)
//     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
//     */
//    @Override
//    public boolean initialize(JSONObject jsonObject) {
//        //forms = new ArrayList<>();
//        JSONArray dataObject;
//        try {
//            dataObject = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//            for (int i = 0; i < dataObject.length(); ++i) {
//                Form form = new Form();
//                form.setEventType(EventType.GET_FORM_RATING_EVENT);
//                JSONObject formObject = dataObject.getJSONObject(i);
//                if (!form.initialize(formObject)) {
//                    System.out.println("Error initializing the form using the data");
//                }
//                //forms.add(form);
//                break;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
////    /* (non-Javadoc)
////     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
////     */
////    @Override
////    public JSONObject toJSON() {
////        return null;
////    }
//
//    @Override
//    public RequiredJson getRequiredJson() {
//        return RequiredJson.METADATA;
//    }
//
////    @Override
////    public int describeContents() {
////        return 0;
////    }
//
////    @Override
////    public void writeToParcel(Parcel dest, int flags) {
////        dest.writeList(forms);
////    }
//
////    /**
////     * Parcel constructor
////     * @param in
////     */
////    private SuperForm(Parcel in) {
////        forms = new ArrayList<Form>();
////        in.readList(forms, Image.class.getClassLoader());
////    }
////
////    /**
////     * Create parcelable
////     */
////    public static final Creator<SuperForm> CREATOR = new Creator<SuperForm>() {
////        public SuperForm createFromParcel(Parcel in) {
////            return new SuperForm(in);
////        }
////
////        public SuperForm[] newArray(int size) {
////            return new SuperForm[size];
////        }
////    };
////
////    public ArrayList<Form> getForms() {
////        return forms;
////    }
////
////    public void setForms(ArrayList<Form> forms) {
////        this.forms = forms;
////    }
//}

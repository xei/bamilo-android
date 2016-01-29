package com.mobile.newFramework.forms;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PaymentMethodForm implements Parcelable {

    private final static String PAYMENT_METHOD_AUTO_SUBMIT_EXTERNAL = "auto-submit-external";
    private final static String PAYMENT_METHOD_SUBMIT_EXTERNAL = "submit-external";
    private final static String PAYMENT_METHOD_AUTO_REDIRECT_EXTERNAL = "auto-redirect-external";
    private final static String PAYMENT_METHOD_RENDER_INTERNAL = "render-internal";
    private final static int METHOD_OTHER = 0;
    public final static int METHOD_AUTO_SUBMIT_EXTERNAL = 1;
    public final static int METHOD_SUBMIT_EXTERNAL = 2;
    public final static int METHOD_AUTO_REDIRECT_EXTERNAL = 3;
    public final static int METHOD_RENDER_INTERNAL = 4;
    private static final String TAG = PaymentMethodForm.class.getName();

    private int payment_type;
    private String action;
    @RequestType
    private int method;
    private String id;
    private String name;
    private String redirect;
    private ContentValues contentValues;
    private String order_nr;
    private String customer_first_name;
    private String customer_last_name;

    public static final int GET = 0;
    public static final int POST = 1;
    public static final int DELETE = 2;
    public static final int PUT = 3;
    @IntDef({ GET, POST, DELETE, PUT })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestType {}

    public PaymentMethodForm() {

    }

    /**
     * "metadata": { "order_nr": "300085512", "customer_first_name": "bill mob",
     * "customer_last_name": "bill", "payment": { "type": "submit-external", "method": "post",
     * "form": { "id": "Wallety", "name": "Wallety", "method": "post", "action":
     * "https://www.wallety.com/checkout/checkout", "fields": [ { "key": "amount", "type": "hidden",
     * "rules": null, "value": "118897.00", "label": "", "name": "amount", "id": "amount" }, {
     * "key": "desc", "type": "hidden", "rules": null, "value": "Purchase -", "label": "", "name":
     * "desc", "id": "desc" }, { "key": "gid", "type": "hidden", "rules": null, "value": "84",
     * "label": "", "name": "gid", "id": "gid" }, { "key": "merchinvno", "type": "hidden", "rules":
     * null, "value": "300085512", "label": "", "name": "merchinvno", "id": "merchinvno" }, { "key":
     * "redirect", "type": "hidden", "rules": null, "value":
     * "https://alice-staging.jumia.co.ke/checkout/wallety/success", "label": "", "name":
     * "redirect", "id": "redirect" }, { "key": "check_sum", "type": "hidden", "rules": null,
     * "value": "8861F0742AEDAC546F535C263B27FC8D", "label": "", "name": "check_sum", "id":
     * "check_sum" }, { "key": "checkout", "rules": null, "type": "image", "value": "", "label": "",
     * "name": "checkout", "id": "checkout" } ] } }
     */
    public void initialize(JSONObject jsonObject) {
        JSONObject mJSONObject = jsonObject.optJSONObject("payment");
        if (mJSONObject == null || mJSONObject.length() == 0) {
            setPaymentType(METHOD_OTHER);
            setOrderNumber(jsonObject.optString(RestConstants.ORDER_NR));
            return;
        }

        String type = mJSONObject.optString(RestConstants.TYPE);
        if (type.equalsIgnoreCase(PAYMENT_METHOD_AUTO_SUBMIT_EXTERNAL)) {
            setPaymentType(METHOD_AUTO_SUBMIT_EXTERNAL);
        } else if (type.equalsIgnoreCase(PAYMENT_METHOD_SUBMIT_EXTERNAL)) {
            setPaymentType(METHOD_SUBMIT_EXTERNAL);
        } else if (type.equalsIgnoreCase(PAYMENT_METHOD_AUTO_REDIRECT_EXTERNAL)){
            setPaymentType(METHOD_AUTO_REDIRECT_EXTERNAL);
        } else if(type.equalsIgnoreCase(PAYMENT_METHOD_RENDER_INTERNAL)){
            setPaymentType(METHOD_RENDER_INTERNAL);
        }

        String method = mJSONObject.optString(RestConstants.METHOD);
        if (method.equalsIgnoreCase("get")) {
            setMethod(GET);
        } else {
            setMethod(POST);
        }

        try {
            JSONObject formJson = mJSONObject.optJSONObject(RestConstants.FORM);
            if(formJson == null || formJson.length() == 0 ){
                String url = mJSONObject.optString(RestConstants.URL);
                setAction(url);
                return;
            }

            setAction(formJson.optString(RestConstants.ACTION));
            setId(formJson.optString(RestConstants.ID));
            setName(formJson.optString(RestConstants.NAME));

            ContentValues mContentValues = new ContentValues();
            JSONArray mJSONArray = formJson.getJSONArray(RestConstants.FIELDS);
            for (int i = 0; i < mJSONArray.length(); i++) {
                JSONObject element = mJSONArray.getJSONObject(i);
                String key = element.getString(RestConstants.KEY);
                if (!key.equalsIgnoreCase("redirect")) {
                    mContentValues.put(key, element.getString(RestConstants.VALUE));
                } else if (key.equalsIgnoreCase("redirect") || key.equalsIgnoreCase("return_url")){
                    setRedirect(element.getString(RestConstants.VALUE));
                    mContentValues.put(key, element.getString(RestConstants.VALUE));
                }
            }
//            //Print.i(TAG, "code1content : " + mContentValues.toString());
            setContentValues(mContentValues);

        } catch (JSONException e) {
            e.printStackTrace();
            
        }
    }

    /**
     * @param payment_type
     *            the payment_type to set
     */
    public void setPaymentType(int payment_type) {
        this.payment_type = payment_type;
    }

    /**
     * Get the url that is going to be use to process the payment.
     * 
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Set the url that is going to be use to process the payment.
     * 
     * @param action
     *            the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the method
     */
    @RequestType
    public int getMethod() {
        return method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod(@RequestType int method) {
        this.method = method;
    }

    public boolean isExternalPayment() {
        return payment_type == METHOD_SUBMIT_EXTERNAL ||
                payment_type == METHOD_AUTO_SUBMIT_EXTERNAL ||
                payment_type == METHOD_AUTO_REDIRECT_EXTERNAL ||
                payment_type == METHOD_RENDER_INTERNAL;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the url to catch when the external provider redirects to success page.
     * 
     * @param redirect
     *            the redirect to set
     */
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    /**
     * List of options/values to send to external provider.
     * 
     * @return the contentValues
     */
    public ContentValues getContentValues() {
        return this.contentValues;
    }

    /**
     * Set the list of options/values to send to external provider.
     * 
     * @param contentValues
     *            the contentValues to set
     */
    public void setContentValues(ContentValues contentValues) {
        this.contentValues = contentValues;
    }

    /**
     * @param order_nr
     *            the order_nr to set
     */
    public void setOrderNumber(String order_nr) {
        this.order_nr = order_nr;
    }

    /*
     * ############## PARCELABLE ##############
     */

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(payment_type);
        dest.writeString(action);
        dest.writeInt(method);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(redirect);
        dest.writeParcelable(contentValues, flags);
        dest.writeString(order_nr);
        dest.writeString(customer_first_name);
        dest.writeString(customer_last_name);
    }

    /**
     * Parcel constructor
     */
    @SuppressWarnings("ResourceType")
    private PaymentMethodForm(Parcel in) {
        payment_type = in.readInt();
        action = in.readString();
        method = in.readInt();
        id = in.readString();
        name = in.readString();
        redirect = in.readString();
        contentValues = in.readParcelable(ContentValues.class.getClassLoader());
        order_nr = in.readString();
        customer_first_name = in.readString();
        customer_last_name = in.readString();
    }


    /**
     * Create parcelable
     */
    public static final Creator<PaymentMethodForm> CREATOR = new Creator<PaymentMethodForm>() {
        public PaymentMethodForm createFromParcel(Parcel in) {
            return new PaymentMethodForm(in);
        }

        public PaymentMethodForm[] newArray(int size) {
            return new PaymentMethodForm[size];
        }
    };
}

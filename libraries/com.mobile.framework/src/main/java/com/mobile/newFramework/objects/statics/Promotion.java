//package com.mobile.newFramework.objects.statics;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.newFramework.objects.IJSONSerializable;
//import com.mobile.newFramework.objects.RequiredJson;
//import com.mobile.newFramework.pojo.RestConstants;
//import com.mobile.newFramework.utils.LogTagHelper;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
//
///**
// * Class that represents a Promotion.
// */
//public class Promotion implements IJSONSerializable, Parcelable {
//
//	public final static String TAG = LogTagHelper.create( Promotion.class );
//
//    private String title;
//    private String description;
//    private String couponCode;
//    private String termsConditions;
//    private String endDate;
//    private boolean isValid;
//
//    /**
//     * Category empty constructor.
//     */
//    public Promotion() {
//        title = "defaultName";
//        description = "";
//        couponCode = "";
//        termsConditions = "";
//        endDate = "";
//        isValid = true;
//    }
//
//    public String getTitle(){
//    	return this.title;
//    }
//
//    public String getDescription(){
//    	return this.description;
//    }
//
//    public String getCouponCode(){
//    	return this.couponCode;
//    }
//
//    public String getTermsConditions(){
//    	return this.termsConditions;
//    }
//
//    public String getEndDate(){
//    	return this.endDate;
//    }
//
//    public boolean getIsStillValid(){
//    	return this.isValid;
//    }
//
//
//    @Override
//    public RequiredJson getRequiredJson() {
//        return RequiredJson.OBJECT_DATA;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
//     */
//    @Override
//    public boolean initialize(JSONObject jsonObject) {
//        title = jsonObject.optString(RestConstants.JSON_TITLE_TAG);
//        title = title.replace("[bold]", "<b>");
//        title = title.replace("[/bold]", "</b><code>");
//		description = jsonObject.optString(RestConstants.JSON_DESCRIPTION_TAG);
//		description = description.replace("[bold]", "<b>");
//		description = description.replace("[/bold]", "</b><code>");
//		couponCode = jsonObject.optString(RestConstants.JSON_COUPON_CODE_TAG);
//		termsConditions = jsonObject.optString(RestConstants.JSON_TERMS_CONDITIONS_TAG);
//		termsConditions = termsConditions.replace("[bold]", "<b>");
//		termsConditions = termsConditions.replace("[/bold]", "</b><code>");
//		endDate = jsonObject.optString(RestConstants.JSON_END_DATE_TAG);
//		isValid = compareDates();
//        return true;
//    }
//
//    /**
//     * Verify if the promotion has expired, using the end date.
//     */
//    private boolean compareDates(){
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
//		Date strDate = null;
//		try {
//			strDate = sdf.parse(endDate);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		Calendar c = Calendar.getInstance();
//		/**
//		 * (strDate.getTime()+(1000*60*60*24) )
//		 * 1000*60*60*24 is one day in milliseconds and needs to be added in order to include the end day.
//		 */
//		return strDate != null && ((strDate.getTime()+ (1000*60*60*24)) < c.getTimeInMillis());
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//     */
//    @Override
//    public JSONObject toJSON() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put(RestConstants.JSON_TITLE_TAG, title);
//            jsonObject.put(RestConstants.JSON_DESCRIPTION_TAG, description);
//            jsonObject.put(RestConstants.JSON_COUPON_CODE_TAG, couponCode);
//            jsonObject.put(RestConstants.JSON_TERMS_CONDITIONS_TAG, termsConditions);
//            jsonObject.put(RestConstants.JSON_END_DATE_TAG, endDate);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }
//
//
//    /**
//     * ########### Parcelable ###########
//     * @author sergiopereira
//     */
//
//    /*
//     * (non-Javadoc)
//     * @see android.os.Parcelable#describeContents()
//     */
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
//	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//	    dest.writeString(title);
//	    dest.writeString(description);
//	    dest.writeString(couponCode);
//	    dest.writeString(termsConditions);
//	    dest.writeString(endDate);
//	    dest.writeBooleanArray(new boolean[] {isValid});
//	}
//
//	/**
//	 * Parcel constructor
//	 */
//	private Promotion(Parcel in) {
//        title = in.readString();
//        description = in.readString();
//        couponCode = in.readString();
//        termsConditions = in.readString();
//        endDate = in.readString();
//        in.readBooleanArray(new boolean[] {isValid});
//    }
//
//	/**
//	 * Create parcelable
//	 */
//	public static final Parcelable.Creator<Promotion> CREATOR = new Parcelable.Creator<Promotion>() {
//        public Promotion createFromParcel(Parcel in) {
//            return new Promotion(in);
//        }
//
//        public Promotion[] newArray(int size) {
//            return new Promotion[size];
//        }
//    };
//
//}

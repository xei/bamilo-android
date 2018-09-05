package com.bamilo.android.framework.service.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that keeps each Payment Description
 * @author Manuel Silva
 *
 */
public class PaymentInfo implements Parcelable{
	private String text;
	private String image;
	private String tooltip_text;
	private String cvc_text;

	public PaymentInfo() {
		this.setText("");
		this.setImage("");
		this.setTooltipText("");
		this.setCvcText("");
	}

	
	public void initialize(JSONObject mJSONObject){
		
		this.text = mJSONObject.optString(RestConstants.TEXT);
		this.tooltip_text = mJSONObject.optString(RestConstants.TOOLTIP_TEXT);
		this.cvc_text = mJSONObject.optString(RestConstants.CVC_TEXT);

		try {
			this.image = mJSONObject
					.optJSONObject("icons")
					.optJSONArray("enable")
					.get(0)
					.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		
		
		
	}
    
    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeString(text);
	    dest.writeString(image);
	    dest.writeString(tooltip_text);
	    dest.writeString(cvc_text);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private PaymentInfo(Parcel in) {
		this.text = in.readString();
		this.image = in.readString();
		this.tooltip_text = in.readString();
		this.cvc_text = in.readString();
		
    }
		
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the images
	 */
	public String getImage() {
		return image;
	}


	/**
	 * @param images the images to set
	 */
	public void setImage(String images) {
		this.image = images;
	}

	/**
	 * @return the tooltip_text
	 */
	public String getTooltipText() {
		return tooltip_text;
	}


	/**
	 * @param tooltip_text the tooltip_text to set
	 */
	public void setTooltipText(String tooltip_text) {
		this.tooltip_text = tooltip_text;
	}

	/**
	 * @return the cvc_text
	 */
	public String getCvcText() {
		return cvc_text;
	}


	/**
	 * @param cvc_text the cvc_text to set
	 */
	public void setCvcText(String cvc_text) {
		this.cvc_text = cvc_text;
	}

	/**
	 * Create parcelable 
	 */
	public static final Creator<PaymentInfo> CREATOR = new Creator<PaymentInfo>() {
        public PaymentInfo createFromParcel(Parcel in) {
            return new PaymentInfo(in);
        }

        public PaymentInfo[] newArray(int size) {
            return new PaymentInfo[size];
        }
    };
	
}

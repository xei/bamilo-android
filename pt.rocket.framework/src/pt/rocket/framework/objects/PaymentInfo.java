package pt.rocket.framework.objects;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that keeps each Payment Description
 * @author Manuel Silva
 *
 */
public class PaymentInfo implements Parcelable{
	private String text;
	private ArrayList<String> images;
	private String tooltip_text;
	private String cvc_text;

	public PaymentInfo() {
		this.setText("");
		this.setImages(new ArrayList<String>());
		this.setTooltipText("");
		this.setCvcText("");
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
	    dest.writeList(images);
	    dest.writeString(tooltip_text);
	    dest.writeString(cvc_text);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private PaymentInfo(Parcel in) {
		this.text = in.readString();
		in.readList(images, null);
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
	public ArrayList<String> getImages() {
		return images;
	}


	/**
	 * @param images the images to set
	 */
	public void setImages(ArrayList<String> images) {
		this.images = images;
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
	public static final Parcelable.Creator<PaymentInfo> CREATOR = new Parcelable.Creator<PaymentInfo>() {
        public PaymentInfo createFromParcel(Parcel in) {
            return new PaymentInfo(in);
        }

        public PaymentInfo[] newArray(int size) {
            return new PaymentInfo[size];
        }
    };
	
}

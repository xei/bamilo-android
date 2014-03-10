package pt.rocket.framework.objects;

import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;

public class Voucher implements Parcelable {
	private HashMap<String, String> messages;
	private String cartValue;
	private String couponMoneyValue;
	public Voucher() {
		this.messages = new HashMap<String, String>();
		this.cartValue = "";
		this.couponMoneyValue = "";
	}

	/**
	 * @return the messages
	 */
	public HashMap<String, String> getMessages() {
		return messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(HashMap<String, String> messages) {
		this.messages = messages;
	}

	/**
	 * @return the cartValue
	 */
	public String getCartValue() {
		return cartValue;
	}

	/**
	 * @param cartValue the cartValue to set
	 */
	public void setCartValue(String cartValue) {
		this.cartValue = cartValue;
	}

	/**
	 * @return the couponMoneyValue
	 */
	public String getCouponMoneyValue() {
		return couponMoneyValue;
	}

	/**
	 * @param couponMoneyValue the couponMoneyValue to set
	 */
	public void setCouponMoneyValue(String couponMoneyValue) {
		this.couponMoneyValue = couponMoneyValue;
	}
	
	public boolean isPaymentNeeded(){
		boolean result = true;
		int value = Integer.parseInt(cartValue);
		if(value == 0) result = false;
		return result;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeMap(messages);
		dest.writeString(cartValue);
		dest.writeString(couponMoneyValue);
		
	}
	
	   /**
     * Parcel constructor
     * @param in
     */
    private Voucher(Parcel in) {
    	in.readMap(messages, null);
    	cartValue = in.readString();;
    	couponMoneyValue = in.readString();;
    }
	
	/**
     * Create parcelable 
     */
    public static final Parcelable.Creator<Voucher> CREATOR = new Parcelable.Creator<Voucher>() {
        public Voucher createFromParcel(Parcel in) {
            return new Voucher(in);
        }

        public Voucher[] newArray(int size) {
            return new Voucher[size];
        }
    };
	
}

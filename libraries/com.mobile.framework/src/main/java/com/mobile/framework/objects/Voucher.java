package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Voucher implements Parcelable {
    
	private String cartValue;
	private String couponMoneyValue;
	
	public Voucher() {
		this.cartValue = "";
		this.couponMoneyValue = "";
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
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(cartValue);
		dest.writeString(couponMoneyValue);
		
	}
	
	   /**
     * Parcel constructor
     * @param in
     */
    private Voucher(Parcel in) {
    	cartValue = in.readString();
    	couponMoneyValue = in.readString();
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

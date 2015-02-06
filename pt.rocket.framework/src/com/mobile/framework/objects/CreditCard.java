/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * Credit card class. Holds all the fields of the credit card.
 * 
 * @author Guilherme Silva
 */
public class CreditCard implements Parcelable {

	private String cvc;
	private int expiryMonth;
	private int expiryYear;
	private String holderName;
	private long number;

	public CreditCard(String cvc, int expiryMonth, int expiryYear, String holderName, long number) {
		this.cvc = cvc;
		this.expiryMonth = expiryMonth;
		this.expiryYear = expiryYear;
		this.holderName = holderName;
		this.number = number;
	}

	public String getCvc() {
		return cvc;
	}

	public int getExpiryMonth() {
		return expiryMonth;
	}

	public int getExpiryYear() {
		return expiryYear;
	}

	public String getHolderName() {
		return holderName;
	}

	public long getNumber() {
		return number;
	}
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
    
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
	    dest.writeString(cvc);
	    dest.writeInt(expiryMonth);
	    dest.writeInt(expiryYear);
	    dest.writeString(holderName);
	    dest.writeLong(number);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private CreditCard(Parcel in) {
		this.cvc = in.readString();
		this.expiryMonth = in.readInt();
		this.expiryYear = in.readInt();
		this.holderName = in.readString();
		this.number = in.readLong();
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<CreditCard> CREATOR = new Parcelable.Creator<CreditCard>() {
        public CreditCard createFromParcel(Parcel in) {
            return new CreditCard(in);
        }

        public CreditCard[] newArray(int size) {
            return new CreditCard[size];
        }
    };
}

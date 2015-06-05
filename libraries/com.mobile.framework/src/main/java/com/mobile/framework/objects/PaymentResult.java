///**
// * @author Guilherme Silva
// *
// * @version 1.01
// *
// * 2012/06/18
// *
// * Copyright (c) Rocket Internet All Rights Reserved
// */
//package com.mobile.framework.objects;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
///**
// * Class that represents the Payment result for the old api
// * @author Guilherme Silva
// *
// * TODO:Please check if class is deprecated and if so delete it
// */
//public class PaymentResult implements Parcelable{
//	String authCode;
//	String dccAmount;
//	String dccSignature;
//	String fraudResult;
//	String issuerUrl;
//	String md;
//	String paResult;
//	long pspReference;
//	String refusalReason;
//	String resultCode;
//
//	public PaymentResult() {
//		this.authCode = "";
//		this.dccAmount = "";
//		this.dccSignature = "";
//		this.fraudResult = "";
//		this.issuerUrl = "";
//		this.md = "";
//		this.paResult = "";
//		this.pspReference = 0;
//		this.refusalReason = "";
//		this.resultCode = "";
//	}
//
//	public PaymentResult(String authCode, String dccAmount,
//			String dccSignature, String fraudResult, String issuerUrl,
//			String md, String paResult, long pspReference,
//			String refusalReason, String resultCode) {
//
//		this.authCode = authCode;
//		this.dccAmount = dccAmount;
//		this.dccSignature = dccSignature;
//		this.fraudResult = fraudResult;
//		this.issuerUrl = issuerUrl;
//		this.md = md;
//		this.paResult = paResult;
//		this.pspReference = pspReference;
//		this.refusalReason = refusalReason;
//		this.resultCode = resultCode;
//	}
//
//	/**
//	 * @return the authCode
//	 */
//	public String getAuthCode() {
//		return authCode;
//	}
//
//	/**
//	 * @return the dccAmount
//	 */
//	public String getDccAmount() {
//		return dccAmount;
//	}
//
//	/**
//	 * @return the dccSignature
//	 */
//	public String getDccSignature() {
//		return dccSignature;
//	}
//
//	/**
//	 * @return the fraudResult
//	 */
//	public String getFraudResult() {
//		return fraudResult;
//	}
//
//	/**
//	 * @return the issuerUrl
//	 */
//	public String getIssuerUrl() {
//		return issuerUrl;
//	}
//
//	/**
//	 * @return the md
//	 */
//	public String getMd() {
//		return md;
//	}
//
//	/**
//	 * @return the paResult
//	 */
//	public String getPaResult() {
//		return paResult;
//	}
//
//	/**
//	 * @return the pspReference
//	 */
//	public long getPspReference() {
//		return pspReference;
//	}
//
//	/**
//	 * @return the refusalReason
//	 */
//	public String getRefusalReason() {
//		return refusalReason;
//	}
//
//	/**
//	 * @return the resultCode
//	 */
//	public String getResultCode() {
//		return resultCode;
//	}
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
//	    dest.writeString(authCode);
//	    dest.writeString(dccAmount);
//	    dest.writeString(dccSignature);
//	    dest.writeString(fraudResult);
//	    dest.writeString(issuerUrl);
//	    dest.writeString(md);
//	    dest.writeString(paResult);
//	    dest.writeLong(pspReference);
//	    dest.writeString(refusalReason);
//	    dest.writeString(resultCode);
//	}
//
//	/**
//	 * Parcel constructor
//	 * @param in
//	 */
//	private PaymentResult(Parcel in) {
//		this.authCode = in.readString();
//		this.dccAmount = in.readString();
//		this.dccSignature = in.readString();
//		this.fraudResult = in.readString();
//		this.issuerUrl = in.readString();
//		this.md = in.readString();
//		this.paResult = in.readString();
//		this.pspReference = in.readLong();
//		this.refusalReason = in.readString();
//		this.resultCode = in.readString();
//    }
//
//	/**
//	 * Create parcelable
//	 */
//	public static final Parcelable.Creator<PaymentResult> CREATOR = new Parcelable.Creator<PaymentResult>() {
//        public PaymentResult createFromParcel(Parcel in) {
//            return new PaymentResult(in);
//        }
//
//        public PaymentResult[] newArray(int size) {
//            return new PaymentResult[size];
//        }
//    };
//
//}

/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

public class PaymentResult {
	String authCode;
	String dccAmount;
	String dccSignature;
	String fraudResult;
	String issuerUrl;
	String md;
	String paResult;
	long pspReference;
	String refusalReason;
	String resultCode;

	public PaymentResult() {
		this.authCode = "";
		this.dccAmount = "";
		this.dccSignature = "";
		this.fraudResult = "";
		this.issuerUrl = "";
		this.md = "";
		this.paResult = "";
		this.pspReference = 0;
		this.refusalReason = "";
		this.resultCode = "";
	}

	public PaymentResult(String authCode, String dccAmount,
			String dccSignature, String fraudResult, String issuerUrl,
			String md, String paResult, long pspReference,
			String refusalReason, String resultCode) {

		this.authCode = authCode;
		this.dccAmount = dccAmount;
		this.dccSignature = dccSignature;
		this.fraudResult = fraudResult;
		this.issuerUrl = issuerUrl;
		this.md = md;
		this.paResult = paResult;
		this.pspReference = pspReference;
		this.refusalReason = refusalReason;
		this.resultCode = resultCode;
	}

	/**
	 * @return the authCode
	 */
	public String getAuthCode() {
		return authCode;
	}

	/**
	 * @return the dccAmount
	 */
	public String getDccAmount() {
		return dccAmount;
	}

	/**
	 * @return the dccSignature
	 */
	public String getDccSignature() {
		return dccSignature;
	}

	/**
	 * @return the fraudResult
	 */
	public String getFraudResult() {
		return fraudResult;
	}

	/**
	 * @return the issuerUrl
	 */
	public String getIssuerUrl() {
		return issuerUrl;
	}

	/**
	 * @return the md
	 */
	public String getMd() {
		return md;
	}

	/**
	 * @return the paResult
	 */
	public String getPaResult() {
		return paResult;
	}

	/**
	 * @return the pspReference
	 */
	public long getPspReference() {
		return pspReference;
	}

	/**
	 * @return the refusalReason
	 */
	public String getRefusalReason() {
		return refusalReason;
	}

	/**
	 * @return the resultCode
	 */
	public String getResultCode() {
		return resultCode;
	}
}

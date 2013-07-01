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

/**
 * 
 * Credit card class. Holds all the fields of the credit card.
 * 
 * @author Guilherme Silva
 */
public class CreditCard {

	private String cvc;
	private int expiryMonth;
	private int expiryYear;
	private String holderName;
	private long number;

	public CreditCard(String cvc, int expiryMonth, int expiryYear,
			String holderName, long number) {
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
}

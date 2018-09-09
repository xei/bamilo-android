package com.bamilo.android.framework.service.objects.orders;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.objects.addresses.Address;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/28/2017.
 */

public class PackagedOrder implements IJSONSerializable {
    // Order info
    private String orderId;
    private int productsCount;
    private String creationDate;
    private String cms;

    // Customer info
    private String customerFirstName;
    private String customerLastName;

    // Payment info
    private String paymentMethodName;
    private int deliveryCost;
    private int totalCost;

    // Addresses
    private Address billingAddress;
    private Address shippingAddress;

    // Packages
    private List<Package> packages;


    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        orderId = jsonObject.optString(RestConstants.ORDER_NUMBER);
        productsCount = jsonObject.optInt(RestConstants.TOTAL_PRODUCT_COUNT);
        creationDate = jsonObject.optString(RestConstants.CREATION_DATE);
        cms = jsonObject.optString(RestConstants.CMS);

        JSONObject customerObject = jsonObject.optJSONObject(RestConstants.CUSTOMER);
        if (customerObject != null) {
            customerFirstName = customerObject.optString(RestConstants.FIRST_NAME);
            customerLastName = customerObject.optString(RestConstants.LAST_NAME);
        }

        JSONObject paymentObject = jsonObject.optJSONObject(RestConstants.PAYMENT);
        if (paymentObject != null) {
            paymentMethodName = paymentObject.optString(RestConstants.METHOD);
            deliveryCost = paymentObject.optInt(RestConstants.DELIVERY_COST);
            totalCost = paymentObject.optInt(RestConstants.TOTAL_COST);
        }

        JSONObject billingAddressObject = jsonObject.optJSONObject(RestConstants.BILLING_ADDRESS);
        if (billingAddressObject != null) {
            billingAddress = new Address();
            billingAddress.initialize(billingAddressObject);
        }
        JSONObject shippingAddressObject = jsonObject.optJSONObject(RestConstants.SHIPPING_ADDRESS);
        if (shippingAddressObject != null) {
            shippingAddress = new Address();
            shippingAddress.initialize(shippingAddressObject);
        }

        JSONArray packagesJsonArray = jsonObject.optJSONArray(RestConstants.PACKAGES);
        if (packagesJsonArray != null) {
            packages = new ArrayList<>();
            for (int i = 0; i < packagesJsonArray.length(); i++) {
                Package tempPackage = new Package();
                tempPackage.initialize(packagesJsonArray.getJSONObject(i));
                packages.add(tempPackage);
            }
        }

        return false;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(int productsCount) {
        this.productsCount = productsCount;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public int getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(int deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCms() {
        return cms;
    }

    public void setCms(String cms) {
        this.cms = cms;
    }
}

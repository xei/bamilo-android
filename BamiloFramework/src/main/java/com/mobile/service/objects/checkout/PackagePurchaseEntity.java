package com.mobile.service.objects.checkout;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.addresses.Address;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mohsen on 3/12/18.
 */

public class PackagePurchaseEntity implements IJSONSerializable {

    private List<CartPackage> packages;
    private double total;
    private double totalConverted;
    private double subTotal;
    private double subTotalConverted;
    private int cartCount;
    private double vatValue;
    private String vatLabel;
    private double shippingValue;
    private double extraCosts;
    private double sumCostsValue;
    private double couponDiscount;
    private String couponCode;
    private HashMap<String, String> priceRules;
    private String shippingMethod;
    private String paymentMethod;
    private Address billingAddress;
    private Address shippingAddress;
    private boolean isVatEnabled;
    private ArrayList<Fulfillment> fulfillmentList;
    private boolean hasFreeShipping;
    private double subTotalUnDiscounted;
    private String deliveryNotice;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        if (jsonObject != null) {
            // Cart entity
            jsonObject = jsonObject.getJSONObject(RestConstants.CART_ENTITY);
            // Total
            total = jsonObject.getDouble(RestConstants.TOTAL);
            totalConverted = jsonObject.getDouble(RestConstants.TOTAL_CONVERTED);
            // Get cart sub total
            subTotal = jsonObject.optDouble(RestConstants.SUB_TOTAL);
            subTotalUnDiscounted = jsonObject.optDouble(RestConstants.SUB_TOTAL_UN_DISCOUNTED);
            subTotalConverted = jsonObject.optDouble(RestConstants.SUB_TOTAL_CONVERTED);
            // Vat
            JSONObject vatObject = jsonObject.optJSONObject(RestConstants.VAT);
            if (vatObject != null) {
                vatValue = vatObject.optDouble(RestConstants.VALUE);
                vatLabel = vatObject.optString(RestConstants.LABEL);
                isVatEnabled = vatObject.optBoolean(RestConstants.LABEL_CONFIGURATION);
            }
            // Delivery
            JSONObject deliveryObject = jsonObject.optJSONObject(RestConstants.DELIVERY);
            if (deliveryObject != null) {
                shippingValue = deliveryObject.optDouble(RestConstants.AMOUNT);
            }
            // Coupon
            JSONObject couponObject = jsonObject.optJSONObject(RestConstants.COUPON);
            if (couponObject != null) {
                couponCode = couponObject.optString(RestConstants.CODE);
                couponDiscount = couponObject.optDouble(RestConstants.VALUE);
            }
            // Costs
            sumCostsValue = jsonObject.optDouble(RestConstants.SUM_COSTS_VALUE);
            // Extra costs
            extraCosts = jsonObject.optDouble(RestConstants.EXTRA_COSTS);
            // Get cart count
            cartCount = jsonObject.getInt(RestConstants.TOTAL_PRODUCTS);

            packages = new ArrayList<>();
            JSONArray packagesArray = jsonObject.optJSONArray(RestConstants.PACKAGES);
            if (packagesArray != null && packagesArray.length() > 0) {
                for (int i = 0; i < packagesArray.length(); i++) {
                    CartPackage cartPackage = new CartPackage();
                    cartPackage.initialize(packagesArray.optJSONObject(i));
                    packages.add(cartPackage);
                }
            }

            // Price rules
            JSONArray priceRules = jsonObject.optJSONArray(RestConstants.PRICE_RULES);
            if (priceRules != null && priceRules.length() > 0) {
                this.priceRules = new HashMap<>();
                for (int i = 0; i < priceRules.length(); i++) {
                    JSONObject pRulesElement = priceRules.optJSONObject(i);
                    if (pRulesElement != null) {
                        this.priceRules.put(pRulesElement.getString(RestConstants.LABEL), pRulesElement.getString(RestConstants.VALUE));
                    }
                }
            }

            // Get shipping method
            JSONObject jsonShip = jsonObject.optJSONObject(RestConstants.SHIPPING_METHOD);
            if (jsonShip != null) {
                shippingMethod = jsonShip.optString(RestConstants.METHOD);
            }
            // Get payment method
            JSONObject jsonPay = jsonObject.optJSONObject(RestConstants.PAYMENT_METHOD);
            if (jsonPay != null) {
                paymentMethod = jsonPay.optString(RestConstants.LABEL);
            }
            // Get billing address
            JSONObject jsonBilAddress = jsonObject.optJSONObject(RestConstants.BILLING_ADDRESS);
            if (jsonBilAddress != null) {
                billingAddress = new Address();
                billingAddress.initialize(jsonBilAddress);
            }
            // Get shipping address
            JSONObject jsonShipAddress = jsonObject.optJSONObject(RestConstants.SHIPPING_ADDRESS);
            if (jsonShipAddress != null) {
                shippingAddress = new Address();
                shippingAddress.initialize(jsonShipAddress);
            }
            // Get fulfilment
            JSONArray fulfillmentArray = jsonObject.optJSONArray(RestConstants.FULFILLMENT);
            if (fulfillmentArray != null) {
                fulfillmentList = new ArrayList<>();
                for (int i = 0; i < fulfillmentArray.length(); i++) {
                    fulfillmentList.add(new Fulfillment(fulfillmentArray.getJSONObject(i)));
                }
            }

            deliveryNotice = jsonObject.optString(RestConstants.DELIVERY_NOTICE);
            return true;
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

    public void setPackages(List<CartPackage> packages) {
        this.packages = packages;
    }

    public List<CartPackage> getPackages() {
        return packages;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTotalConverted() {
        return totalConverted;
    }

    public void setTotalConverted(double totalConverted) {
        this.totalConverted = totalConverted;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getSubTotalConverted() {
        return subTotalConverted;
    }

    public void setSubTotalConverted(double subTotalConverted) {
        this.subTotalConverted = subTotalConverted;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public double getVatValue() {
        return vatValue;
    }

    public void setVatValue(double vatValue) {
        this.vatValue = vatValue;
    }

    public String getVatLabel() {
        return vatLabel;
    }

    public void setVatLabel(String vatLabel) {
        this.vatLabel = vatLabel;
    }

    public double getShippingValue() {
        return shippingValue;
    }

    public void setShippingValue(double shippingValue) {
        this.shippingValue = shippingValue;
    }

    public double getExtraCosts() {
        return extraCosts;
    }

    public void setExtraCosts(double extraCosts) {
        this.extraCosts = extraCosts;
    }

    public double getSumCostsValue() {
        return sumCostsValue;
    }

    public void setSumCostsValue(double sumCostsValue) {
        this.sumCostsValue = sumCostsValue;
    }

    public boolean hasCouponDiscount() {
        return TextUtils.isNotEmpty(couponCode);
    }

    public double getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(double couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public HashMap<String, String> getPriceRules() {
        return priceRules;
    }

    public void setPriceRules(HashMap<String, String> priceRules) {
        this.priceRules = priceRules;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public boolean hasShippingAddress() {
        return shippingAddress != null;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public boolean isVatEnabled() {
        return isVatEnabled;
    }

    public void setVatEnabled(boolean vatEnabled) {
        isVatEnabled = vatEnabled;
    }

    public ArrayList<Fulfillment> getFulfillmentList() {
        return fulfillmentList;
    }

    public void setFulfillmentList(ArrayList<Fulfillment> fulfillmentList) {
        this.fulfillmentList = fulfillmentList;
    }

    public boolean hasFreeShipping() {
        return hasFreeShipping;
    }

    public void setHasFreeShipping(boolean hasFreeShipping) {
        this.hasFreeShipping = hasFreeShipping;
    }

    public double getSubTotalUnDiscounted() {
        return subTotalUnDiscounted;
    }

    public void setSubTotalUnDiscounted(double subTotalUnDiscounted) {
        this.subTotalUnDiscounted = subTotalUnDiscounted;
    }

    public String getDeliveryNotice() {
        return deliveryNotice;
    }

    public void setDeliveryNotice(String deliveryNotice) {
        this.deliveryNotice = deliveryNotice;
    }
}

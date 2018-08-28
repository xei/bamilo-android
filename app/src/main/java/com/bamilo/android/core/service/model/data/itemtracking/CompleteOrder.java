
package com.bamilo.android.core.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompleteOrder {

    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("customer")
    @Expose
    private Customer customer;
    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("grand_total")
    @Expose
    private Long grandTotal;
    @SerializedName("payment")
    @Expose
    private Payment payment;
    @SerializedName("cardNumber")
    @Expose
    private String cardNumber;
    @SerializedName("billing_address")
    @Expose
    private Address billingAddress;
    @SerializedName("shipping_address")
    @Expose
    private Address shippingAddress;
    @SerializedName("packages")
    @Expose
    private List<Package> packages = null;
    @SerializedName("total_products_count")
    @Expose
    private Long totalProductsCount;
    @SerializedName("cancellation")
    @Expose
    private OrderCancellation cancellation;
    @SerializedName("cms")
    @Expose
    private String cms;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Long grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
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

    public Long getTotalProductsCount() {
        return totalProductsCount;
    }

    public void setTotalProductsCount(Long totalProductsCount) {
        this.totalProductsCount = totalProductsCount;
    }

    public String getCms() {
        return cms;
    }

    public void setCms(String cms) {
        this.cms = cms;
    }

    public OrderCancellation getCancellation() {
        return cancellation;
    }

    public void setCancellation(OrderCancellation cancellation) {
        this.cancellation = cancellation;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}

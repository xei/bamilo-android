
package com.bamilo.android.core.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Address {

    @SerializedName("id_sales_order_address")
    @Expose
    private String idSalesOrderAddress;
    @SerializedName("address1")
    @Expose
    private String address1;
    @SerializedName("address_type")
    @Expose
    private String addressType;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("is_billing")
    @Expose
    private Boolean isBilling;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("postcode")
    @Expose
    private String postcode;
    @SerializedName("address2")
    @Expose
    private String address2;

    public String getIdSalesOrderAddress() {
        return idSalesOrderAddress;
    }

    public void setIdSalesOrderAddress(String idSalesOrderAddress) {
        this.idSalesOrderAddress = idSalesOrderAddress;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Boolean getIsBilling() {
        return isBilling;
    }

    public void setIsBilling(Boolean isBilling) {
        this.isBilling = isBilling;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

}

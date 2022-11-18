package com.example.customerdatabaseprojectii.entity;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Customers {

    private int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private int divisionID;
    private Timestamp createDateTime;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;

    @Override
    public String toString() {
        return "Customers{" +
                "customerID=" + customerID +
                ", customerName='" + customerName + '\'' +
                ", address='" + address + '\'' +
                ", postalCode=" + postalCode +
                ", phoneNumber=" + phoneNumber +
                ", divisionID=" + divisionID +
                '}';
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }
}

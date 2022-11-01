package com.example.customerdatabaseprojectii.entity;

public class Contacts {

    private int contactID;
    private String contactName;
    private String email;

    @Override
    public String toString() {
        return "Contacts{" +
                "contactID=" + contactID +
                ", contactName='" + contactName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



}

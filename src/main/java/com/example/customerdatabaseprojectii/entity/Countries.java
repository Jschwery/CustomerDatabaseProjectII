package com.example.customerdatabaseprojectii.entity;

import java.sql.Time;
import java.sql.Timestamp;

public class Countries {
    private int countryID;
    private String country;
    private Time creationDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;

    @Override
    public String toString() {
        return "Countries{" +
                "countryID=" + countryID +
                ", country='" + country + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                '}';
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Time getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Time creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}

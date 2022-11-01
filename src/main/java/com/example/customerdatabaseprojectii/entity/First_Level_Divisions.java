package com.example.customerdatabaseprojectii.entity;

import java.sql.Time;
import java.sql.Timestamp;

public class First_Level_Divisions {

    private int divisionID;
    private String division;
    private Time creationDate;
    private String createdBy;
    private Timestamp lastUpdate;

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    private String lastUpdatedBy;
    private int countryID;

    @Override
    public String toString() {
        return "First_Level_Divisions{" +
                "divisionID=" + divisionID +
                ", division='" + division + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", countryID=" + countryID +
                '}';
    }

    public int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
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

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
}

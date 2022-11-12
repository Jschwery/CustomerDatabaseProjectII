package com.example.customerdatabaseprojectii.entity;

import java.sql.Time;
import java.sql.Timestamp;

public class First_Level_Divisions {

    private int divisionID;
    private String division;
    private int countryID;

    public First_Level_Divisions(){}

    @Override
    public String toString() {
        return "First_Level_Divisions{" +
                "divisionID=" + divisionID +
                ", division='" + division + '\'' +
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

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
}

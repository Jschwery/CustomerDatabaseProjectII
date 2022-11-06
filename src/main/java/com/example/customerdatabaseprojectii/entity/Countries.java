package com.example.customerdatabaseprojectii.entity;

import java.sql.Time;
import java.sql.Timestamp;

public class Countries {
    private int countryID;
    private String country;


    @Override
    public String toString() {
        return "Countries{" +
                "countryID=" + countryID +
                ", country='" + country + '\'' +
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
}

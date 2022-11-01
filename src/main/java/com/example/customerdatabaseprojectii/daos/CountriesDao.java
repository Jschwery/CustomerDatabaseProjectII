package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.entity.Countries;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountriesDao {
    public String getCountriesQuery() {
        return countriesQuery;
    }
    private String countriesQuery = "SELECT * FROM countries";

    public ObservableList<Countries> addContactToObservableList() throws SQLException {
        Connection countriesConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(getCountriesQuery(), countriesConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<Countries> observableCountriesList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Countries country = new Countries();
                country.setCountryID(rs.getInt("Country_ID"));
                country.setCountry(rs.getString("Country"));
                country.setCreationDate(rs.getTime("Create_Date"));
                country.setCreatedBy(rs.getString("Created_By"));
                country.setLastUpdate(rs.getTimestamp("Last_Update"));
                country.setLastUpdatedBy(rs.getString("Last_Updated_By"));

                observableCountriesList.add(country);
            }
        }
        return observableCountriesList;
    }
}

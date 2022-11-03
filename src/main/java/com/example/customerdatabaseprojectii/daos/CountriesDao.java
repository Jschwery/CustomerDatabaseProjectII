package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Countries;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountriesDao {
    private static final String countriesQuery = "SELECT * FROM countries";
    private static final ObservableList<Countries> observableCountriesList = FXCollections.observableArrayList();

    public void addCountriesToObservableList(Countries country){
        observableCountriesList.add(country);
    }

    public ObservableList<Countries> getObservableCountries(){
        return observableCountriesList;
    }

    public ObservableList<Countries> addCountriesToObservableList() throws SQLException {
        Connection countriesConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(countriesQuery, countriesConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
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
        return getObservableCountries();
    }
}

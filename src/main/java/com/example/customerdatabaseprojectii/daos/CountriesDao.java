package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Countries;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountriesDao implements Dao<Countries>{
    private static final String getAllCountriesQuery = "SELECT * FROM countries";
    private static final String addCountryQuery = "INSERT INTO countries (Country_ID, Country) VALUES (?, ?)";
    private static final String updatedCountryQuery = "UPDATE countries SET Country=? " +
            "WHERE Country_ID=?";
    private static final String deleteCountryQuery = "DELETE FROM countries WHERE Country_ID=?";


    @Override
    public String dbInsert(Countries country) throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(addCountryQuery).orElse(null);
        if (ps != null) {
            ps.setInt(1, country.getCountryID());
            ps.setString(2, country.getCountry());

            return String.format("%d countries updated", ps.executeUpdate());
        }
        return "null";
    }

    @Override
    public ObservableList<Countries> getAllFromDB() throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(getAllCountriesQuery).orElse(null);
        ObservableList<Countries> countries = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Countries country = new Countries();
                country.setCountryID(rs.getInt("Country_ID"));
                country.setCountry(rs.getString("Country"));

                countries.add(country);
            }
        }
        return countries;
    }

    @Override
    public String updateDB(Countries country) throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(updatedCountryQuery).orElse(null);
        if(ps != null){
            ps.setString(1, country.getCountry());
            ps.setInt(2, country.getCountryID());
            return String.format("%d countries updated", ps.executeUpdate());
        }
        return "null";
    }

    @Override
    public String deleteFromDB(Countries country) throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(deleteCountryQuery).orElse(null);
        if(ps != null) {
            ps.setInt(1, country.getCountryID());
            int numCountriesDeleted = ps.executeUpdate();
            return String.format("%d countries deleted", numCountriesDeleted);
        }
        return null;
    }
}

package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Countries;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountriesDao implements Dao<Countries>{
    private static final String getAllCountriesQuery = "SELECT * FROM countries";
    private static final String addCountryQuery = "INSERT INTO countries (Country_ID, Country) VALUES (?, ?)";
    private static final String updatedCountryQuery = "UPDATE countries SET Country=? " +
            "WHERE Country_ID=?";
    private static final String deleteCountryQuery = "DELETE FROM countries WHERE Country_ID=?";

    /**
     *
     * @param country Takes in a country to be inserted into the database
     * @return returns true if the country was inserted successfully
     * @throws SQLException
     */
    @Override
    public boolean dbInsert(Countries country) throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(addCountryQuery).orElse(null);
        if (ps != null) {
            ps.setInt(1, country.getCountryID());
            ps.setString(2, country.getCountry());

            System.out.printf("%d countries updated%n", ps.executeUpdate());
            return true;
        }
        return false;
    }

    /**
     * returns all the countries from the database
     * @return
     * @throws SQLException
     */
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

    /**
     *
     * @param country the country to be updated in the database
     * @return returns true if update was successful and false otherwise
     * @throws SQLException
     */
    @Override
    public boolean updateDB(Countries country) throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(updatedCountryQuery).orElse(null);
        if(ps != null){
            ps.setString(1, country.getCountry());
            ps.setInt(2, country.getCountryID());
            System.out.printf("%d countries updated%n", ps.executeUpdate());
            return true;
        }
        return false;
    }

    /**
     *
     * @param country takes a country to match and delete from the database
     * @return a string of the string of the number of countries deleted
     * @throws SQLException
     */
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

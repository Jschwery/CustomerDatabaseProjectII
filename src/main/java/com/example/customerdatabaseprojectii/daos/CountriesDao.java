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
        ObservableList<Appointments> observableCountriesList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
        }

    }

}

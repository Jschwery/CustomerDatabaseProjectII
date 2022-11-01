package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomersDao {
    public String getCustomersQuery() {
        return customersQuery;
    }

    private String customersQuery = "SELECT * FROM customers";

    public ObservableList<Customers> addCustomersToObservableList() throws SQLException {
        Connection customersConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(getCustomersQuery(), customersConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<Appointments> observableCustomersList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
        }

    }


}

package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Countries;
import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.util.DbConnection;
import com.example.customerdatabaseprojectii.view.CustomerFormController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CustomersDao {

    private static final String customersQuery = "SELECT * FROM customers";

    public void addCustomersToObservableListFromDB() throws SQLException {
        Connection customersConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(customersQuery, customersConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if (ps != null) {            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Customers customer = new Customers();
                customer.setCustomerID(rs.getInt("Customer_ID"));
                customer.setCustomerName(rs.getString("Customer_Name"));
                customer.setAddress(rs.getString("Address"));
                customer.setPostalCode(rs.getInt("Postal_Code"));
                customer.setPhoneNumber(rs.getLong("Phone"));
                customer.setCreatedBy(rs.getString("Created_By"));
                customer.setLastUpdate(rs.getTimestamp("Last_Update"));
                customer.setLastUpdatedBy(rs.getString("Last_Updated_By"));
                customer.setCreationDate(rs.getTime("Create_Date"));

                CustomerFormController.getAllCustomers().add(customer);
            }
        }
    }
}

package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.util.DbConnection;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import com.example.customerdatabaseprojectii.view.CustomerMainController;
import com.example.customerdatabaseprojectii.view.LoginController;

import java.sql.*;

public class CustomersDao {

    public static int idCount;

    private static final String customersQuery = "SELECT * FROM customers";
    private static final String updateCustomerQuery = "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, " +
            "Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, " +
            "Division_ID = ? WHERE Customer_ID = ?";
    private static final String customerInsertQuery = "INSERT INTO customers (Customer_ID, Customer_Name, Address," +
            "Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID)" +
            "VALUES (?,?,?,?,?,?,?,?,?,?)";

    public static void insertCustomerIntoDatabase(Customers customer) throws SQLException {
        Connection con = DbConnection.getConnection();
        DbConnection.makePreparedStatement(customerInsertQuery,con);
        PreparedStatement statement = DbConnection.getPreparedStatement();
        assert statement != null;
        statement.setInt(1, idCount);
        statement.setString(2, customer.getCustomerName());
        statement.setString(3, customer.getAddress());
        statement.setString(4, customer.getPostalCode());
        statement.setString(5, customer.getPhoneNumber());
        statement.setDate(6, Date.valueOf(RelatedTime.getLocalDate()));
        statement.setString(7, "Admin");
        statement.setTimestamp(8, Timestamp.valueOf(RelatedTime.getCurrentDateTime()));
        statement.setString(9, LoginController.getCurrentlyLoggedInUser().getUsername());
        statement.setInt(10, customer.getDivisionID());

        statement.execute();
        idCount++;
    }

    public static void updateCustomerInDatabase(Customers customer) throws SQLException {
        Connection con = DbConnection.getConnection();
        DbConnection.makePreparedStatement(updateCustomerQuery,con);
        PreparedStatement statement = DbConnection.getPreparedStatement();
        assert statement != null;

        //the customer values need to be imported from the fields
        statement.setInt(1, customer.getCustomerID());
        statement.setString(2, customer.getCustomerName());
        statement.setString(3, customer.getAddress());
        statement.setString(4, customer.getPostalCode());
        statement.setString(5, customer.getPhoneNumber());
        statement.setDate(6, Date.valueOf(RelatedTime.getLocalDate()));
        statement.setString(7, "Admin");
        statement.setInt(8, customer.getDivisionID());
        statement.setInt(9, customer.getCustomerID());

        statement.execute();
    }

    public static void addCustomersToObservableListFromDB() throws SQLException {
        Connection customersConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(customersQuery, customersConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Customers customer = new Customers();
                customer.setCustomerID(rs.getInt("Customer_ID"));
                customer.setCustomerName(rs.getString("Customer_Name"));
                customer.setAddress(rs.getString("Address"));
                customer.setPostalCode(rs.getString("Postal_Code"));
                customer.setPhoneNumber(rs.getString("Phone"));
                customer.setDivisionID(rs.getInt("Division_ID"));

                CustomerMainController.getAllCustomers().add(customer);
            }
        }
    }
}

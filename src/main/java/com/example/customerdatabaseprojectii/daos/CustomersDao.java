package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.util.DbConnection;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import com.example.customerdatabaseprojectii.view.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CustomersDao implements Dao<Customers> {

    public static int idCount = 0;

    static {
        try {
            idCount = getNumberOfCustomer()+1;
            if(idCount >= 0){
                throw new RuntimeException("Customer id cannot be below or equal to zero");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String customersQuery = "SELECT * FROM customers";
    private static final String updateCustomerQuery = "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, " +
            "Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, " +
            "Division_ID = ? WHERE Customer_ID = ?";
    private static final String customerInsertQuery = "INSERT INTO customers (Customer_ID, Customer_Name, Address," +
            "Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID)" +
            "VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String customerDeleteQuery = "DELETE FROM customer WHERE Customer_ID = ?";

    public static void insertCustomerIntoDatabase(Customers customer) throws SQLException {
       PreparedStatement statement = DbConnection.dbStatementTemplate(customersQuery).orElse(null);

        if(statement!= null) {
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
    }

    public static int getNumberOfCustomer() throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(customersQuery).orElse(null);
        ObservableList<Customers> customers = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Customers customer = new Customers();
                customers.add(customer);

            }
            return customers.size();
        }
        return -1;
    }

    @Override
    public String dbInsert(Customers customer) throws SQLException {
        PreparedStatement statement = DbConnection.dbStatementTemplate(customerInsertQuery).orElse(null);

        if (statement != null) {
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

            int numberOfCustomerInserted = statement.executeUpdate();
            idCount++;
            return String.format("%d customers inserted", numberOfCustomerInserted);
        }
        return "null";
    }

    @Override
    public ObservableList<Customers> getAllFromDB() throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(customersQuery).orElse(null);
        ObservableList<Customers> customersObservableList = FXCollections.observableArrayList();
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

                customersObservableList.add(customer);
            }
        }
        return customersObservableList;
    }

    @Override
    public String updateDB(Customers customer) throws SQLException {
        PreparedStatement statement = DbConnection.dbStatementTemplate(updateCustomerQuery).orElse(null);
        if (statement != null) {

            statement.setInt(1, customer.getCustomerID());
            statement.setString(2, customer.getCustomerName());
            statement.setString(3, customer.getAddress());
            statement.setString(4, customer.getPostalCode());
            statement.setString(5, customer.getPhoneNumber());
            statement.setDate(6, Date.valueOf(RelatedTime.getLocalDate()));
            statement.setString(7, "Admin");
            statement.setInt(8, customer.getDivisionID());
            statement.setInt(9, customer.getCustomerID());

            return String.format("%d customers updated", statement.executeUpdate());
        }
        return "null";
    }
    @Override
    public String deleteFromDB(Customers customer) throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(customerDeleteQuery).orElse(null);
        if(ps != null) {
            ps.setInt(1, customer.getCustomerID());

            return String.format("%d customers deleted", ps.executeUpdate());
        }
        return "null";
    }
}

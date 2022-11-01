package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactsDao {

    private String contactQuery = "SELECT * FROM contacts";
    private String getAllContactsQuery() {
        return contactQuery;
    }

    public ObservableList<Contacts> addContactToObservableList() throws SQLException {
        Connection contactsConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(getAllContactsQuery(), contactsConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<Contacts> observableContactsList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Contacts contact = new Contacts();
                contact.setContactID(rs.getInt("Contact_ID"));
                contact.setContactName(rs.getString("Contact_Name"));
                contact.setEmail(rs.getString("Email"));

                observableContactsList.add(contact);
            }
        }
        return observableContactsList;
    }
}

//TODO
/*
The login is not getting saved to the database,
try and create a basic button and method to extract information from one of the tables and print it to the console

 */
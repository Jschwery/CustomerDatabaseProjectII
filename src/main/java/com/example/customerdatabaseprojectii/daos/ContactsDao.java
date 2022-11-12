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
import java.util.Objects;

public class ContactsDao {

    private static final String contactQuery = "SELECT * FROM contacts";

    public static ObservableList<String> returnRelatedContactNames() throws SQLException {
        Connection contactsConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(contactQuery, contactsConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<String> observableContactsList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Contacts contact = new Contacts();
                contact.setContactName(rs.getString("Contact_Name"));
                observableContactsList.add(contact.getContactName());
            }
        }
        return observableContactsList;
    }

    public static Integer returnContactIDbyName(String contactName) throws SQLException {
        Connection contactsConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(contactQuery, contactsConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<Integer> observableContactsList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Contacts contact = new Contacts();
                contact.setContactID(rs.getInt("Contact_ID"));
                contact.setContactName(rs.getString("Contact_Name"));
                contact.setEmail(rs.getString("Email"));
                System.out.println(contact.getContactName() + ": "+ contact.getContactID());
                if(Objects.equals(contact.getContactName(), contactName)){
                    return contact.getContactID();
                }
            }
        }
        return -1;
    }
    public static ObservableList<Contacts> addContactToObservableList() throws SQLException {
        Connection contactsConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(contactQuery, contactsConnection);
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


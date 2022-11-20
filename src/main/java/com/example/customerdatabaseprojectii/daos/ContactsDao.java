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

public class ContactsDao implements Dao<Contacts>{

    private static final String contactQuery = "SELECT * FROM contacts";
    private static final String insertContactQuery = "INSERT INTO contacts (Contact_ID, Contact_Name, Email)" +
            "VALUES (?,?,?)";
    private static final String updateContactQuery = "UPDATE contacts SET Contact_Name = ?, Email = ? WHERE Contact_ID = ?";
    private static final String deleteContactQuery = "DELETE FROM contacts WHERE Contact_ID = ?";

    public ContactsDao() throws SQLException {}

    public int returnContactIDbyName(String name) throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(contactQuery).orElse(null);
        if(ps != null) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Contacts contact = new Contacts();
                contact.setContactID(rs.getInt("Contact_ID"));
                contact.setContactName(rs.getString("Contact_Name"));

                if(Objects.equals(contact.getContactName().toUpperCase(), name.toUpperCase())){
                    return contact.getContactID();
                }
            }
        }
        return -1;
    }


    private static int getNumberOfContacts() throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(contactQuery).orElse(null);
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            ObservableList<Contacts> contact = FXCollections.observableArrayList();
            while (rs.next()) {
                Contacts contacts = new Contacts();
                contacts.setContactName(rs.getString("Contact_Name"));

                contact.add(contacts);
            }
            return contact.size();
        }
        return -1;
    }

    @Override
    public String dbInsert(Contacts contact) throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(insertContactQuery).orElse(null);
        if(ps != null) {
            ps.setInt(1, getNumberOfContacts() + 1);
            ps.setString(2, contact.getContactName());
            ps.setString(3, contact.getEmail());
            int rowsUpdated = ps.executeUpdate();
            return String.format("Rows updated: %d\n", rowsUpdated);
        }
        System.out.println("Unsuccessfully inserted contact into database");
        return "null";
    }

    @Override
    public ObservableList<Contacts> getAllFromDB() throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(contactQuery).orElse(null);
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

    @Override
    public String updateDB(Contacts contact) throws SQLException {
        PreparedStatement statement = DbConnection.dbStatementTemplate(updateContactQuery).orElse(null);
        if (statement != null) {
            statement.setString(1, contact.getContactName());
            statement.setString(2, contact.getEmail());
            statement.setInt(3, contact.getContactID());

            int rowsUpdated = statement.executeUpdate();
            return String.format("Rows updated: %d\n", rowsUpdated);
        }
        else {
            System.out.println("Unsuccessfully updated contact to database");
            return "null";
        }
    }

    @Override
    public String deleteFromDB(Contacts contact) throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(deleteContactQuery).orElse(null);
        if(ps != null) {
            ps.setInt(1, contact.getContactID());

            int rowsDeleted = ps.executeUpdate();
            return String.format("%d rows deleted", rowsDeleted);
        }
        System.out.println("Unsuccessfully deleted contact from database");
        return "null";
    }
}


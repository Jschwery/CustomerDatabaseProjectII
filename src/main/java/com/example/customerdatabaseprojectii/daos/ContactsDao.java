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
    private static int contactIDCount = 0;
    static {
        try {
            contactIDCount = getNumberOfContacts() + 1;
            if(contactIDCount <= 0){
                throw new RuntimeException("Contact ID cannot be less than or equal to zero");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static final String contactQuery = "SELECT * FROM contacts";
    private static final String insertContactQuery = "INSERT INTO contacts (Contact_ID, Contact_Name, Email)" +
            "VALUES (?,?,?)";
    private static final String updateContactQuery = "UPDATE contacts SET Contact_Name = ?, Email = ? WHERE Contact_ID = ?";
    private static final String deleteContactQuery = "DELETE FROM contacts WHERE Contact_ID = ?";

    public ContactsDao() throws SQLException {}

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
        ps.setInt(1, contact.getContactID());
        ps.setString(2, contact.getContactName());
        ps.setString(3, contact.getEmail());

        int rowsUpdated = ps.executeUpdate();
        return String.format("Rows updated: %d\n", rowsUpdated);
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
        return "null";
    }
}


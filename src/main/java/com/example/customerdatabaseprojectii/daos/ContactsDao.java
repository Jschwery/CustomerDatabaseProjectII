package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactsDao {

    private static final String contactQuery = "SELECT * FROM contacts";

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

//TODO
/*
DB connection now works

But the DAOs is either not properly filling in the entities with the neccessary data
then adding them to an observable list

setcellvalue --> set property value is not able to get the data from the objects set in the tableView
soo... is the tableView being filled with the data from set items


 */
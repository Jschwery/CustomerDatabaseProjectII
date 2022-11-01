package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Contacts;
import javafx.collections.ObservableList;

public class ContactsDao {

    private String contactQuery = "SELECT * FROM contacts";
    private String getAllContactsQuery(){
        return contactQuery;
    }

    public ObservableList<Contacts> addContactToObservableList(){
    return null;
    }

}

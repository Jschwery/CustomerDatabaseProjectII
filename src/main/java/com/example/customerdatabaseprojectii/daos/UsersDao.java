package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersDao {
    public String getUsersQuery() {
        return usersQuery;
    }

    private String usersQuery = "SELECT * FROM users";

    public ObservableList<Users> addContactToObservableList() throws SQLException {
        Connection usersConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(getUsersQuery(), usersConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<Users> observableUsersList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
        }

    }

}

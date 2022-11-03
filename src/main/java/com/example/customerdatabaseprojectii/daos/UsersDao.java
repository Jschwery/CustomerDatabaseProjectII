package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersDao {

    private static final String usersQuery = "SELECT * FROM users";

    public ObservableList<Users> addUserToObservableList() throws SQLException {
        Connection usersConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(usersQuery, usersConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<Users> observableUsersList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users user = new Users();
                user.setUserId(rs.getInt("User_ID"));
                user.setUsername(rs.getString("User_Name"));
                user.setPassword(rs.getString("User_Password"));
                user.setCreateDate(rs.getTime("Create_Date"));
                user.setCreatedBy(rs.getString("Created_By"));
                user.setLastUpdate(rs.getTimestamp("Last_Update"));
                user.setLastUpdatedBy(rs.getString("Last_Updated_By"));

                observableUsersList.add(user);
            }
        }
        return observableUsersList;
    }
}

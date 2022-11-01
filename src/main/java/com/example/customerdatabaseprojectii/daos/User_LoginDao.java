package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.User_Login;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class User_LoginDao {
    private String userLoginQuery = "SELECT * FROM user-login";

    public String getUserLoginQuery() {
        return userLoginQuery;
    }

    public boolean accountVerified(User_Login login) throws SQLException {
        Connection actConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(getUserLoginQuery(), actConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if(ps != null){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String username = rs.getString("user_name"); //check username from sql table
                String password = rs.getString("password"); //check password from sql table

                if(Objects.equals(login.getUserName(), username) && Objects.equals(login.getUserPassword(), password)){
                    return true;
                }
                else{
                    System.out.println("No username + password combo found in database");
                }
            }
        }
        return false;
    }
}
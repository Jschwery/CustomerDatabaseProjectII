package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersDao implements Dao<Users> {

    private static final String usersQuery = "SELECT * FROM users";


    private static final String insertUserQuery = "Insert INTO users (User_Name, Password) " +
            "VALUES (?,?)";
    private static final String updateUserQuery = "UPDATE users SET User_Name = ?, Password = ? WHERE User_ID = ?";
    private static final String deleteUserQuery = "DELETE FROM users WHERE User_ID = ?";

    public String getUserNameByID(int userID) throws SQLException {
        Connection usersConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(usersQuery, usersConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users tempUser = new Users();
                tempUser.setUsername(rs.getString("User_Name"));
                tempUser.setPassword(rs.getString("Password"));
                tempUser.setUser_ID(rs.getInt("User_ID"));

                if (tempUser.getUser_ID() == userID) {
                    return tempUser.getUsername();
                }
            }
        }
        System.out.printf("No user found with the ID: %d\n", userID);
        return "no user found";
    }

    public boolean verifyUserFromDB(Users user) throws SQLException {
        Connection usersConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(usersQuery, usersConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users tempUser = new Users();
                tempUser.setUsername(rs.getString("User_Name"));
                tempUser.setPassword(rs.getString("Password"));
                try {
                    if (user.getUsername().equals(tempUser.getUsername()) && user.getPassword().equals(tempUser.getPassword())) {
                        System.out.printf("User found: %s%n", user);
                        return true;
                    }
                }catch (NullPointerException e){
                    return false;
                }
            }
        }
        System.out.printf("User not found: %s%n", user);
        return false;
    }

    @Override
    public String dbInsert(Users user) throws SQLException {
            PreparedStatement ps = DbConnection.dbStatementTemplate(insertUserQuery).orElse(null);
            if (ps != null) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                int rowAffected = ps.executeUpdate();
               return String.format("%d number of rows were affected with the update", rowAffected);
            }
            System.out.println("Unsuccessfully inserted user into database");
            return "null";
        }
    @Override
    public ObservableList<Users> getAllFromDB() throws SQLException {

        PreparedStatement tempPs = DbConnection.dbStatementTemplate(usersQuery).orElse(null);
        ObservableList<Users> observableUsersList = FXCollections.observableArrayList();
        if (tempPs != null) {
            ResultSet rs = tempPs.executeQuery();
            while (rs.next()) {
                Users tempUser = new Users();
                tempUser.setUsername(rs.getString("User_Name"));
                tempUser.setPassword(rs.getString("Password"));
                tempUser.setUser_ID(rs.getInt("User_ID"));

                observableUsersList.add(tempUser);
            }
        }
        return observableUsersList;
    }

    @Override
    public String updateDB(Users user) throws SQLException {

        PreparedStatement ps = DbConnection.dbStatementTemplate(updateUserQuery).orElse(null);

        if(ps!= null){
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getUser_ID());
            ps.executeUpdate();
            return String.format("User with %d has been updated\nUsername: %s\nPassword: %s", user.getUser_ID(), user.getUsername(), user.getPassword());
        }
        System.out.println("Unsuccessfully updated User to database");
        return "null";
    }

    @Override
    public String deleteFromDB(Users user) throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(deleteUserQuery).orElse(null);
        if(ps!=null){
            ps.setInt(1, user.getUser_ID());
            return String.format("%d users deleted", ps.executeUpdate());
        }
        System.out.println("Unsuccessfully deleted User from database");
        return "null";
    }
}

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

public class UsersDao {

    private static final String usersQuery = "SELECT * FROM users";


    private static final String insertUserQuery = "Insert INTO users (User_Name, Password) " +
            "VALUES (?,?)";

    public static String getUserNameByID(int userID) throws SQLException {
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
        return "no user found";
    }

    public static boolean verifyUserFromDB(Users user) throws SQLException {
        Connection usersConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(usersQuery, usersConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users tempUser = new Users();
                tempUser.setUsername(rs.getString("User_Name"));
                tempUser.setPassword(rs.getString("Password"));


                if (user.getUsername().equals(tempUser.getUsername()) && user.getPassword().equals(tempUser.getPassword())) {
                    System.out.printf("User found: %s%n", user);
                    return true;
                }
            }
        }
        System.out.printf("User not found: %s%n", user);
        return false;
    }


    /**
     *
     * @return Returns an Observable list of users that are extracted from the Database and
     *  sets each User field with types correlating to those in the database
     * @throws SQLException
     */
    public static ObservableList<Users> getAllUsersObservableList() throws SQLException {
        Connection tempUsersConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(usersQuery, tempUsersConnection);
        PreparedStatement tempPs = DbConnection.getPreparedStatement();
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

    /**
     *
     * @param user
     * @throws SQLException
     */
    public static void insertUserLoginIntoDB(Users user){
        try {
        Connection connection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(insertUserQuery, connection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if (ps != null) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            int rowAffected = ps.executeUpdate();
            System.out.printf("%d number of rows were affected with the update", rowAffected);
            }
        }catch (SQLException e){
            System.out.println("Unable to add user to database");
            Alerter.warningAlert("Username already in use!");
        }
    }
}

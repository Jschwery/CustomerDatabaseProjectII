package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class UsersDao implements Dao<Users> {

    private static final String usersQuery = "SELECT * FROM users";


    private static final String insertUserQuery = "Insert INTO users (User_Name, Password, Create_Date, Created_By, Last_Update, Last_Updated_By) " +
            "VALUES (?,?,?,?,?,?)";
    private static final String updateUserQuery = "UPDATE users SET User_Name = ?, Password = ?, Last_Update = ?, Last_Updated_By = ? WHERE User_ID = ?";
    private static final String deleteUserQuery = "DELETE FROM users WHERE User_ID = ?";


    /**
     *
     * @param userID takes in the userID
     * @return returns the username that matches the userID
     * @throws SQLException
     */
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

    /**
     *
     * @param user takes in a user to check whether they have an account or not
     * @return returns true if the user has an account or false otherwise
     * @throws SQLException
     */
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

    /**
     *
     * @param user takes in a user to be inserted into the database
     * @return false if the user is not found
     * @throws SQLException
     */
    @Override
    public boolean dbInsert(Users user) throws SQLException {
            PreparedStatement ps = DbConnection.dbStatementTemplate(insertUserQuery).orElse(null);
            if (ps != null) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(4, user.getUsername());
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(6, user.getUsername());

                int rowAffected = ps.executeUpdate();
                System.out.printf("%d number of rows were affected with the update%n", rowAffected);
                return true;
            }
            System.out.println("Unsuccessfully inserted user into database");
            return false;
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
                tempUser.setCreateDateTime(rs.getTimestamp("Create_Date"));

                observableUsersList.add(tempUser);
            }
        }
        return observableUsersList;
    }

    /**
     *
     * @param user takes in a user to update the database value of that particular user
     * @return returns true if the user is updated, false otherwise
     * @throws SQLException
     */
    @Override
    public boolean updateDB(Users user) throws SQLException {

        PreparedStatement ps = DbConnection.dbStatementTemplate(updateUserQuery).orElse(null);

        if(ps!= null){
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getUser_ID());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(5, user.getUsername());
            ps.executeUpdate();
            System.out.printf("User with %d has been updated\nUsername: %s\nPassword: %s%n", user.getUser_ID(), user.getUsername(), user.getPassword());
            return true;
        }
        System.out.println("Unsuccessfully updated User to database");
        return false;
    }

    /**
     *
     * @param user takes in the user to be deleted
     * @return returns true if the user was deleted successfully or false otherwise
     * @throws SQLException
     */
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

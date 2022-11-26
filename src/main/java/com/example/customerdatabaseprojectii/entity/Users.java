package com.example.customerdatabaseprojectii.entity;


import java.sql.Time;
import java.sql.Timestamp;

public class Users {
    private String username;
    private String password;
    private Timestamp createDateTime;
    private int user_ID;


    @Override
    public String toString() {
        return "Users{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }
    public void setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
    }
    public int getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(int user_ID) {
        this.user_ID = user_ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

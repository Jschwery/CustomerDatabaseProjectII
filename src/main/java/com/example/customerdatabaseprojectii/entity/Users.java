package com.example.customerdatabaseprojectii.entity;


import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Users {
    private String username;
    private String password;
    private Timestamp createDateTime;
    private int user_ID = 0;
    public static Map<Integer, Integer> count = new HashMap<>();

    public static void setCount(Users user, Integer logged){
        count.put(user.getUser_ID(), logged);
    }

    public int getUserLogInCount() {
        return userLogInCount;
    }


    public void setUserLogInCount(int userLogInCount) {
        this.userLogInCount = userLogInCount;
    }

    private int userLogInCount;

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

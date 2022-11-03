package com.example.customerdatabaseprojectii.util;

import java.sql.*;

public class tester {

    public tester() throws SQLException {

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "root","Bearsfan580!");
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM appointments");

        ResultSet set = stmt.executeQuery();
        while(set.next()) {
            System.out.println(set.getString("Title"));
        }
        con.close();
    }
}

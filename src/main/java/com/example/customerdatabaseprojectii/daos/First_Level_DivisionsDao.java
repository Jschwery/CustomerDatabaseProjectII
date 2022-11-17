package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.First_Level_Divisions;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class First_Level_DivisionsDao{

    private static final String frstLvlDvsnQuery = "SELECT * FROM first_level_divisions";

    public ObservableList<First_Level_Divisions> getAll() throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(frstLvlDvsnQuery).orElse(null);
        ObservableList<First_Level_Divisions> observableFrstLvlDvsnList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                First_Level_Divisions div = new First_Level_Divisions();
                div.setCountryID(rs.getInt("Division_ID"));
                div.setDivision(rs.getString("Division"));
                div.setCountryID(rs.getInt("Country_ID"));

                observableFrstLvlDvsnList.add(div);
            }
        }
        return observableFrstLvlDvsnList;
    }
}

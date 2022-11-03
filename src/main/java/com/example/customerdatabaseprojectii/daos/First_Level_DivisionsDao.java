package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.First_Level_Divisions;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class First_Level_DivisionsDao {

    private static final String frstLvlDvsnQuery = "SELECT * FROM first_level_divisions";

    public ObservableList<First_Level_Divisions> addDivisionToObservableList() throws SQLException {
        Connection frstLvlDvsnConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(frstLvlDvsnQuery, frstLvlDvsnConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<First_Level_Divisions> observableFrstLvlDvsnList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                First_Level_Divisions div = new First_Level_Divisions();
                div.setCountryID(rs.getInt("Division_ID"));
                div.setDivision(rs.getString("Division"));
                div.setCreationDate(rs.getTime("Create_Date"));
                div.setCreatedBy(rs.getString("Created_By"));
                div.setLastUpdate(rs.getTimestamp("Last_Update"));
                div.setLastUpdatedBy(rs.getString("Last_Updated_By"));
                div.setCountryID(rs.getInt("Country_ID"));

                observableFrstLvlDvsnList.add(div);
            }
        }
        return observableFrstLvlDvsnList;
    }
}

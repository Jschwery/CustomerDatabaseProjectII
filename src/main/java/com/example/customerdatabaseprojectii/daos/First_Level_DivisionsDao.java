package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.entity.First_Level_Divisions;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class First_Level_DivisionsDao {
    public String getFrstLvlDvsnQuery() {
        return frstLvlDvsnQuery;
    }

    private String frstLvlDvsnQuery = "SELECT * FROM first_level_divisions";

    public ObservableList<First_Level_Divisions> addContactToObservableList() throws SQLException {
        Connection frstLvlDvsnConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(getFrstLvlDvsnQuery(), frstLvlDvsnConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<First_Level_Divisions> observableFrstLvlDvsnList = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
        }

    }

}

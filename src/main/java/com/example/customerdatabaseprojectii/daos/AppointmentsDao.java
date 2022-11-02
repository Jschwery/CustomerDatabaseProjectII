package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AppointmentsDao {
    private String appointmentsQuery = "SELECT * FROM appointments";

    public ObservableList<Appointments> generateAppointmentList() throws SQLException {
        Connection apptConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(appointmentsQuery, apptConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<Appointments> observableAppointmentList = FXCollections.observableArrayList();
        if(ps != null){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Appointments apt = new Appointments();
                apt.setAppointmentID(rs.getInt("Appointment_ID"));
                apt.setTitle(rs.getString("Title"));
                apt.setDescription(rs.getString("Description"));
                apt.setLocation(rs.getString("Location"));
                apt.setType(rs.getString("Type"));
                apt.setStartDateTime(rs.getTimestamp("Start"));
                apt.setEndDateTime(rs.getTimestamp("End"));
                apt.setCreateDateTime(rs.getTime("Create_Date"));
                apt.setLastUpdate(rs.getTimestamp("Last_Update"));
                apt.setLastUpdatedBy(rs.getString("Last_Updated_By"));
                apt.setCustomerID(rs.getInt("Customer_ID"));
                apt.setUsersID(rs.getInt("User_ID"));
                apt.setContactsID(rs.getInt("Contact_ID"));
                observableAppointmentList.add(apt);
            }
        }
        else{
            System.out.println("Prepared Statement is Null");
        }
        return observableAppointmentList;
    }
}

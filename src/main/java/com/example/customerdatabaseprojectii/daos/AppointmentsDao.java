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


    private static final String appointmentsQuery = "SELECT * FROM appointments";
    private static final ObservableList<Appointments> observableAppointmentList = FXCollections.observableArrayList();

    public static ObservableList<Appointments> getObservableAppointments(){
        return observableAppointmentList;
    }
    public void addAppointmentToObservableList(Appointments app){
        observableAppointmentList.add(app);
    }

    public static ObservableList<Appointments> generateAppointmentList() throws SQLException {
        Connection apptConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(appointmentsQuery, apptConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
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
                apt.setCreatedBy(rs.getString("Created_By"));
                apt.setLastUpdate(rs.getTimestamp("Last_Update"));
                apt.setLastUpdatedBy(rs.getString("Last_Updated_By"));
                apt.setCustomerID(rs.getInt("Customer_ID"));
                apt.setUsersID(rs.getInt("User_ID"));
                apt.setContactsID(rs.getInt("Contact_ID"));

                observableAppointmentList.add(apt);
            }
        }
        return getObservableAppointments();
    }
}

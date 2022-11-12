package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.util.DbConnection;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentsDao {


    private static final String appointmentsQuery = "SELECT * FROM appointments";
    private static final String insertAppointmenteQuery = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, " +
            "Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String appointmentUpdateQuery = "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, " +
            "Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, " +
            "Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";


    private static final ObservableList<Appointments> observableAppointmentList = FXCollections.observableArrayList();

    public static ObservableList<Appointments> getObservableAppointments(){
        return observableAppointmentList;
    }
    public void addAppointmentToObservableList(Appointments app){
        observableAppointmentList.add(app);
    }

    public static void updateAppointment(Appointments appointment) throws SQLException {
        Connection apptConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(appointmentUpdateQuery, apptConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if (ps != null) {
            ps.setInt(1, appointment.getAppointmentID());
            ps.setString(2, appointment.getTitle());
            ps.setString(3, appointment.getDescription());
            ps.setString(4, appointment.getLocation());
            ps.setString(5, appointment.getType());
            ps.setTimestamp(6, Timestamp.valueOf(appointment.getStartDateTime()));
            ps.setTimestamp(7, Timestamp.valueOf(appointment.getEndDateTime()));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, UsersDao.getUserNameByID(appointment.getUsersID()));
            ps.setInt(10, appointment.getCustomerID());
            ps.setInt(11, appointment.getUsersID());
            ps.setInt(12, appointment.getContactsID());
            ps.setInt(13, appointment.getAppointmentID());

            try {
                ps.execute();
            }catch(SQLIntegrityConstraintViolationException e){
                System.out.println("Parent table no matching id");
            }
            System.out.println("Successfully inserted appointment into database" +
                    "\nTime: " + LocalTime.now());
        }
    }
    //todo
    public static void insertAppointmentIntoDB(Appointments appointment) throws SQLException {
        Connection apptConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(insertAppointmenteQuery, apptConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if(ps!=null) {
            ps.setInt(1, appointment.getAppointmentID());
            ps.setString(2, appointment.getTitle());
            ps.setString(3, appointment.getDescription());
            ps.setString(4, appointment.getLocation());
            ps.setString(5, appointment.getType());
            ps.setTimestamp(6, Timestamp.valueOf(appointment.getStartDateTime()));
            ps.setTimestamp(7, Timestamp.valueOf(appointment.getEndDateTime()));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, UsersDao.getUserNameByID(appointment.getUsersID()));
            ps.setTimestamp(10, Timestamp.valueOf(RelatedTime.getCurrentDateTime()));
            ps.setString(11,UsersDao.getUserNameByID(appointment.getUsersID()));
            ps.setInt(12, appointment.getCustomerID());
            ps.setInt(13, appointment.getUsersID());
            ps.setInt(14, appointment.getContactsID());

            ps.execute();
            System.out.println("Successfully inserted appointment into database" +
                    "\nTime: "+ LocalTime.now());
        }
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
                apt.setStartDateTime(rs.getTimestamp("Start").toLocalDateTime());
                apt.setEndDateTime(rs.getTimestamp("End").toLocalDateTime());
                apt.setCustomerID(rs.getInt("Customer_ID"));
                apt.setUsersID(rs.getInt("User_ID"));
                apt.setContactsID(rs.getInt("Contact_ID"));

                observableAppointmentList.add(apt);
            }
        }
        return getObservableAppointments();
    }
}

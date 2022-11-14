package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.DbConnection;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import com.example.customerdatabaseprojectii.view.AppointmentFormController;
import com.example.customerdatabaseprojectii.view.CustomerMainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

public class AppointmentsDao {
    private static final String appointmentsQuery = "SELECT * FROM appointments";
    private static final String insertAppointmenteQuery = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, " +
            "Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final ObservableList<Appointments> observableAppointmentList = FXCollections.observableArrayList();

    public static ObservableList<Appointments> getObservableAppointments() {
        return observableAppointmentList;
    }

    public void addAppointmentToObservableList(Appointments app) {
        observableAppointmentList.add(app);
    }


    public static int appointmentCount = 0;

    private static final String appointmentUpdateQuery = "UPDATE appointments SET Title = ?, Description = ?, " +
            "Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, " +
            "Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

    public static void updateAppointment(Appointments appointment) throws SQLException {
        Connection apptConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(appointmentUpdateQuery, apptConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if (ps != null) {
            ps.setString(1, appointment.getTitle());
            ps.setString(2, appointment.getDescription());
            ps.setString(3, appointment.getLocation());
            ps.setString(4, appointment.getType());
            ps.setTimestamp(5, Timestamp.valueOf(appointment.getStartDateTime()));
            ps.setTimestamp(6, Timestamp.valueOf(appointment.getEndDateTime()));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, UsersDao.getUserNameByID(appointment.getUsersID()));
            ps.setInt(9, appointment.getCustomerID());
            ps.setInt(10, appointment.getUsersID());
            ps.setInt(11, appointment.getContactsID());
            ps.setInt(12, appointment.getAppointmentID());
            System.out.println("Appointment: "+ appointment);
            System.out.println(appointment.getCustomerID());
            if (UsersDao.getAllUsersObservableList().stream().
                    anyMatch(m -> Objects.equals(m.getUser_ID(), appointment.getUsersID())) &&
                    CustomerMainController.getAllCustomers().stream().
                            anyMatch(m -> Objects.equals(m.getCustomerID(), appointment.getCustomerID())) &&
                    ContactsDao.addContactToObservableList().stream().
                            anyMatch(m -> Objects.equals(m.getContactID(), appointment.getContactsID()))) {
                    ps.executeUpdate();
                    System.out.println("Successfully inserted appointment into database" +
                            "\nTime: " + LocalTime.now());
                    AppointmentFormController.isValidated = true;


            } else {
                AppointmentFormController.isValidated = false;
                System.out.println("Not valid");
            }
        }
    }


//            } catch (SQLIntegrityConstraintViolationException e) {
//                if (UsersDao.getAllUsersObservableList().stream().noneMatch(s -> Objects.equals(s.getUser_ID(), appointment.getUsersID()))) {
//                    Alerter.warningAlert("User not found with the ID: " + appointment.getUsersID());
//                    AppointmentFormController.isValidated = false;
//                }
//                if (CustomerMainController.getAllCustomers().stream().noneMatch(s -> Objects.equals(s.getCustomerID(), appointment.getCustomerID()))) {
//                    Alerter.warningAlert("Customer does not exist with the ID: " + appointment.getCustomerID());
//                    AppointmentFormController.isValidated = false;
//                }
//            }


    public static void insertAppointmentIntoDB(Appointments appointment) throws SQLException {
        Connection apptConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(insertAppointmenteQuery, apptConnection);
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
            ps.setTimestamp(10, Timestamp.valueOf(RelatedTime.getCurrentDateTime()));
            ps.setString(11, UsersDao.getUserNameByID(appointment.getUsersID()));
            ps.setInt(12, appointment.getCustomerID());
            ps.setInt(13, appointment.getUsersID());
            ps.setInt(14, appointment.getContactsID());


            ps.execute();
            appointmentCount++;
            System.out.println("Successfully inserted appointment into database" +
                    "\nTime: " + LocalTime.now());
            AppointmentFormController.isValidated = true;
            if (UsersDao.getAllUsersObservableList().stream().anyMatch(m -> Objects.equals(m.getUser_ID(), appointment.getUsersID()))) {
                Alerter.warningAlert("User with that ID already exists: " + appointment.getUsersID());
                AppointmentFormController.isValidated = false;
            }
        }

        //an appointment can only be fit to one customer and one contact
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

package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.util.DbConnection;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import com.example.customerdatabaseprojectii.view.CustomerMainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class AppointmentsDao implements Dao<Appointments> {
    ContactsDao cd = new ContactsDao();
    UsersDao ud = new UsersDao();
    private static final String appointmentsQuery = "SELECT * FROM appointments";
    private static final String insertAppointmenteQuery = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, " +
            "Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static int appointmentCount = 0;

    private static final String appointmentUpdateQuery = "UPDATE appointments SET Title = ?, Description = ?, " +
            "Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, " +
            "Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
    private static final String appointmentDeleteQuery = "DELETE FROM appointments WHERE Appointment_ID = ?";

    public AppointmentsDao() throws SQLException {}


    @Override
    public String dbInsert(Appointments appointment) throws SQLException {
        Connection apptConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(insertAppointmenteQuery, apptConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        if (ps != null) {
            ps.setInt(1, appointment.getAppointmentID());
            ps.setString(2, appointment.getTitle());
            ps.setString(3, appointment.getDescription());
            ps.setString(4, appointment.getLocation());
            ps.setString(5, appointment.getType());
            ps.setTimestamp(6, appointment.getStartDateTime());
            ps.setTimestamp(7, appointment.getEndDateTime());
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, ud.getUserNameByID(appointment.getUsersID()));
            ps.setTimestamp(10, Timestamp.valueOf(RelatedTime.getCurrentDateTime()));
            ps.setString(11, ud.getUserNameByID(appointment.getUsersID()));
            ps.setInt(12, appointment.getCustomerID());
            ps.setInt(13, appointment.getUsersID());
            ps.setInt(14, appointment.getContactsID());

            //if the user does not already have an appointment & the time of the appointment is not already taken
            //allow insertion
            if (ud.getAllFromDB().stream().noneMatch(m -> Objects.equals(m.getUser_ID(), appointment.getUsersID()))&& ) {
                int rowsUpdated = ps.executeUpdate();
                appointmentCount++;
                System.out.printf("%d rows successfully inserted appointment into database" +
                        "\nTime: " + LocalTime.now(), rowsUpdated);
            }
        }
        return "";
    }

    @Override
    public ObservableList<Appointments> getAllFromDB() throws SQLException {
        Connection apptConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(appointmentsQuery, apptConnection);
        PreparedStatement ps = DbConnection.getPreparedStatement();
        ObservableList<Appointments> apps = FXCollections.observableArrayList();
        if (ps != null) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Appointments apt = new Appointments();
                apt.setAppointmentID(rs.getInt("Appointment_ID"));
                apt.setTitle(rs.getString("Title"));
                apt.setDescription(rs.getString("Description"));
                apt.setLocation(rs.getString("Location"));
                apt.setType(rs.getString("Type"));
                apt.setStartDateTime(Timestamp.valueOf(rs.getTimestamp("Start").toLocalDateTime()));
                apt.setEndDateTime(Timestamp.valueOf(rs.getTimestamp("End").toLocalDateTime()));
                apt.setCustomerID(rs.getInt("Customer_ID"));
                apt.setUsersID(rs.getInt("User_ID"));
                apt.setContactsID(rs.getInt("Contact_ID"));

                apps.add(apt);
            }
        }
        return apps;
    }

    @Override
    public String updateDB(Appointments appointment) throws SQLException {
            Connection apptConnection = DbConnection.getConnection();
            DbConnection.makePreparedStatement(appointmentUpdateQuery, apptConnection);
            PreparedStatement ps = DbConnection.getPreparedStatement();
            if (ps != null) {
                ps.setString(1, appointment.getTitle());
                ps.setString(2, appointment.getDescription());
                ps.setString(3, appointment.getLocation());
                ps.setString(4, appointment.getType());
                ps.setTimestamp(5, appointment.getStartDateTime());
                ps.setTimestamp(6, appointment.getEndDateTime());
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(8, ud.getUserNameByID(appointment.getUsersID()));
                ps.setInt(9, appointment.getCustomerID());
                ps.setInt(10, appointment.getUsersID());
                ps.setInt(11, appointment.getContactsID());
                ps.setInt(12, appointment.getAppointmentID());
                System.out.println("Appointment: "+ appointment);
                System.out.println(appointment.getCustomerID());
                if (ud.getAllFromDB().stream().
                        anyMatch(m -> Objects.equals(m.getUser_ID(), appointment.getUsersID())) &&
                        CustomerMainController.getAllCustomers().stream().
                                anyMatch(m -> Objects.equals(m.getCustomerID(), appointment.getCustomerID())) &&
                        cd.getAllFromDB().stream().
                                anyMatch(m -> Objects.equals(m.getContactID(), appointment.getContactsID()))) {
                    int rowsUpdated = ps.executeUpdate();
                    System.out.println("Successfully inserted appointment into database" +
                            "\nTime: " + LocalTime.now());
                    return String.format("%d rows updated", rowsUpdated);
                } else {
                    System.out.println("Not valid");
                }
            }
            return "";
    }

    @Override
    public String deleteFromDB(Appointments appointment) throws SQLException {

        PreparedStatement ps = DbConnection.dbStatementTemplate(appointmentDeleteQuery).orElse(null);
        assert ps != null;
        ps.setInt(1, appointment.getAppointmentID());

        int numAppsDeleted = ps.executeUpdate();

        return String.format("%d appointments deleted!", numAppsDeleted);

    }
}

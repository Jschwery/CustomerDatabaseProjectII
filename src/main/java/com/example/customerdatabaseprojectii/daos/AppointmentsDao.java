package com.example.customerdatabaseprojectii.daos;

import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.DbConnection;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import com.example.customerdatabaseprojectii.view.AppointmentFormController;
import com.example.customerdatabaseprojectii.view.CustomerMainController;
import com.example.customerdatabaseprojectii.view.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class AppointmentsDao implements Dao<Appointments> {
    ContactsDao cd = new ContactsDao();
    UsersDao ud = new UsersDao();
    AppointmentFormController afc = new AppointmentFormController();
    private static final String appointmentsQuery = "SELECT * FROM appointments";
    private static final String insertAppointmenteQuery = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, " +
            "Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String appointmentUpdateQuery = "UPDATE appointments SET Title = ?, Description = ?, " +
            "Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, " +
            "Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
    private static final String appointmentDeleteQuery = "DELETE FROM appointments WHERE Appointment_ID = ?";

    public AppointmentsDao() throws SQLException {}

    /**
     * @param appointment Takes in an appointment to be inserted to the databse
     * @return returns true if the appointment was successfully inserted and complies with the business
     * requirements of submitting an appointment
     * @throws SQLException
     */
    @Override
    public boolean dbInsert(Appointments appointment) throws SQLException, IOException {

        ZonedDateTime userZdtStart = ZonedDateTime.of(appointment.getStartDateTime(), ZoneId.systemDefault());
        ZonedDateTime userZdtEnd = ZonedDateTime.of(appointment.getEndDateTime(), ZoneId.systemDefault());
        ZonedDateTime estZdtStart = userZdtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime estZdtEnd = userZdtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalDateTime estLocalTimeStart = estZdtStart.toLocalDateTime();
        LocalDateTime estLocalTimeEnd = estZdtEnd.toLocalDateTime();

        PreparedStatement ps = DbConnection.dbStatementTemplate(insertAppointmenteQuery).orElse(null);
        if (ps != null) {
            ps.setInt(1, appointment.getAppointmentID());
            ps.setString(2, appointment.getTitle());
            ps.setString(3, appointment.getDescription());
            ps.setString(4, appointment.getLocation());
            ps.setString(5, appointment.getType());
            ps.setTimestamp(6, Timestamp.valueOf(estLocalTimeStart));
            ps.setTimestamp(7, Timestamp.valueOf(estLocalTimeEnd));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, ud.getUserNameByID(appointment.getUsersID()));
            ps.setTimestamp(10, Timestamp.valueOf(RelatedTime.getCurrentDateTime()));
            ps.setString(11, ud.getUserNameByID(appointment.getUsersID()));
            ps.setInt(12, appointment.getCustomerID());
            ps.setInt(13, appointment.getUsersID());
            ps.setInt(14, appointment.getContactsID());

            int rowsUpdated = ps.executeUpdate();
            if(rowsUpdated > 0) {
                System.out.printf("%d rows successfully inserted appointment into database" +
                        "\nTime: %s", rowsUpdated, LocalTime.now());
                return true;
            }
        }
        return false;
    }

    /**
     * @return returns an observable list of all the appointments currently stored in the database
     * @throws SQLException
     */
    public ObservableList<Appointments> getAllFromDB() throws SQLException {
        PreparedStatement ps = DbConnection.dbStatementTemplate(appointmentsQuery).orElse(null);
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
                apt.setStartDateTime(rs.getTimestamp("Start").toLocalDateTime());
                apt.setEndDateTime(rs.getTimestamp("End").toLocalDateTime());
                apt.setCustomerID(rs.getInt("Customer_ID"));
                apt.setUsersID(rs.getInt("User_ID"));
                apt.setContactsID(rs.getInt("Contact_ID"));

                apps.add(apt);
            }
        }
        return apps;
    }

    /**
     * @param appointment takes in an appointment to update, and checks to see if the appointment is in the database, and whether the
     *                    foreign keys that are required are also present before submitting the appointment
     * @return returns true if the appointment has been updated, and false otherwise
     * @throws SQLException
     */
    @Override
    public boolean updateDB(Appointments appointment) throws SQLException, MalformedURLException {
            CustomerMainController mc = new CustomerMainController();
            PreparedStatement ps = DbConnection.dbStatementTemplate(appointmentUpdateQuery).orElse(null);

            ZonedDateTime userZdtStart = ZonedDateTime.of(appointment.getStartDateTime(), ZoneId.systemDefault());
            ZonedDateTime userZdtEnd = ZonedDateTime.of(appointment.getEndDateTime(), ZoneId.systemDefault());
            ZonedDateTime estZdtStart = userZdtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime estZdtEnd = userZdtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));
            LocalDateTime estLocalTimeStart = estZdtStart.toLocalDateTime();
            LocalDateTime estLocalTimeEnd = estZdtEnd.toLocalDateTime();

        if (ps != null) {
                ps.setString(1, appointment.getTitle());
                ps.setString(2, appointment.getDescription());
                ps.setString(3, appointment.getLocation());
                ps.setString(4, appointment.getType());
                ps.setTimestamp(5, Timestamp.valueOf(estLocalTimeStart));
                ps.setTimestamp(6, Timestamp.valueOf(estLocalTimeEnd));
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
                        mc.getAllCustomers().stream().
                                anyMatch(m -> Objects.equals(m.getCustomerID(), appointment.getCustomerID())) &&
                        cd.getAllFromDB().stream().
                                anyMatch(m -> Objects.equals(m.getContactID(), appointment.getContactsID()))) {
                    int rowsUpdated = ps.executeUpdate();
                    System.out.println("Successfully inserted appointment into database" +
                            "\nTime: " + LocalTime.now());
                    System.out.printf("%d rows updated%n", rowsUpdated);
                    return true;
                } else {
                    System.out.println("Not valid");
                }
            }
            System.out.println("Unsuccessfully updated appointment to database");
            return false;
    }

    /**
     *
     * @param appointment the appointment that is to be deleted from the database
     * @return  returns a string of all the amount of appointments that have been deleted from the db
     * @throws SQLException
     */
    @Override
    public String deleteFromDB(Appointments appointment) throws SQLException {

        PreparedStatement ps = DbConnection.dbStatementTemplate(appointmentDeleteQuery).orElse(null);
        assert ps != null;
        ps.setInt(1, appointment.getAppointmentID());

        int numAppsDeleted = ps.executeUpdate();

        return String.format("%d appointments deleted!", numAppsDeleted);

    }
}

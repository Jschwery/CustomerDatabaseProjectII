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

    @Override
    public boolean dbInsert(Appointments appointment) throws SQLException {

        PreparedStatement ps = DbConnection.dbStatementTemplate(insertAppointmenteQuery).orElse(null);
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

            Optional<Appointments> findAppointment = getAllFromDB().stream().filter(app -> Objects.equals(app.getUsersID(), appointment.getUsersID())).findFirst();
            DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
            ZoneId userZone = RelatedTime.getUserTimeZone();
            if (findAppointment.isPresent()) {
                LocalDateTime toDisplayStart = RelatedTime.changeTimeBusinessToUserLocal(userZone.toString(), findAppointment.get().getStartDateTime());
                LocalDateTime toDisplayEnd = RelatedTime.changeTimeBusinessToUserLocal(userZone.toString(), findAppointment.get().getEndDateTime());
                LocalTime start = LocalTime.parse(toDisplayStart.toLocalTime().toString(), hourAndMinuteFormat);
                LocalDate startDate = toDisplayStart.toLocalDate();
                System.out.println(startDate);
                LocalTime end = LocalTime.parse(toDisplayEnd.toLocalTime().toString(), hourAndMinuteFormat);
                Alerter.informationAlert(String.format("User already has an appointment scheduled:\n%s\n\nStart: %s\nEnd: %s ", startDate, start, end));
                return false;
            }
            if (afc.compareAppointmentToBusiness(appointment) && getAllFromDB().stream().
                    filter(app -> Objects.equals(app.getUsersID(), LoginController.
                            getCurrentlyLoggedInUser().getUser_ID())).noneMatch(obj -> true) ||
                    getAllFromDB().stream().filter(a -> Objects.equals(a.getUsersID(), appointment.getUsersID())).noneMatch(obj -> true)) {

                int rowsUpdated = ps.executeUpdate();
                System.out.printf("%d rows successfully inserted appointment into database" +
                        "\nTime: " + LocalTime.now(), rowsUpdated);
                return true;
            }
        }
        return false;
    }

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
    public boolean updateDB(Appointments appointment) throws SQLException {
            CustomerMainController mc = new CustomerMainController();
            PreparedStatement ps = DbConnection.dbStatementTemplate(appointmentUpdateQuery).orElse(null);
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

    @Override
    public String deleteFromDB(Appointments appointment) throws SQLException {

        PreparedStatement ps = DbConnection.dbStatementTemplate(appointmentDeleteQuery).orElse(null);
        assert ps != null;
        ps.setInt(1, appointment.getAppointmentID());

        int numAppsDeleted = ps.executeUpdate();

        return String.format("%d appointments deleted!", numAppsDeleted);

    }
}

package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.daos.ContactsDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class AppointmentFormController implements Initializable {

    @FXML
    TextField afTitle;
    @FXML
    TextField afDescription;
    @FXML
    TextField afLocation;
    @FXML
    TextField afType;
    @FXML
    TextField afCustomerID;
    @FXML
    TextField afUserID;
    @FXML
    DatePicker afStartDatePicker;
    @FXML
    DatePicker afEndDatePicker;
    @FXML
    ComboBox<String> afStartTimePicker;
    @FXML
    ComboBox<String> afEndTimePicker;
    @FXML
    ComboBox<String> afContact;
    @FXML
    Label appointmentVarTitle;


    public static boolean isModified = false;


    /**
     * Iterates through an observable list of contacts returned from the database, and compares each contacts appointmentID
     * with the appointmentID field from the selected appointment from the tableview of appointments
     * then populates the combobox in the form with the contact name related to that appointment
     *
     * @throws SQLException
     */
    public void populateContactList() throws SQLException {
        afContact.setItems(ContactsDao.returnRelatedContactNames());
    }

    public void closeSceneWindow() {
        Stage stage = (Stage) afContact.getScene().getWindow();
        stage.close();
    }

    /**
     * We first create a datetime formatter so that we can format the times to only pertain to
     * hours and minutes, as we only want to create appointments per 30 minutes.
     */
    public ObservableList<String> setTimeComboBoxes() {
        DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
        ObservableList<String> availableAppointmentTimeSlots = FXCollections.observableArrayList();
        LocalTime firstAppointmentAvailable = LocalTime.MIDNIGHT.plusHours(8);//starting at 8am local time
        LocalTime lastAppointmentAvailable = LocalTime.MIDNIGHT.minusHours(2);//ending at 10pm local time
        while (firstAppointmentAvailable.isBefore(lastAppointmentAvailable)) {
            availableAppointmentTimeSlots.add(hourAndMinuteFormat.format(firstAppointmentAvailable));//adding the formatted time that will only include hours & minutes
            firstAppointmentAvailable = firstAppointmentAvailable.plusMinutes(15);
        }
        return availableAppointmentTimeSlots;
    }


    //times stored in utc
    //local time will be checked against est business hours
    //before being stored in the database as utc




    /*
    the appointment will be submitted to the databse in UTC




    The appointment will be displayed to the user in their local time

    The appointment will be converted to a zonedate_time of eastern to compare with the companies hours and select the correct
    time to remove from their available appointments, and be within their working hours
     */


    public void addAppointmentClicked(ActionEvent event) throws SQLException {
        Appointments scheduleAppointment = new Appointments();

        scheduleAppointment.setTitle(afTitle.getText());
        scheduleAppointment.setDescription(afDescription.getText());
        scheduleAppointment.setLocation(afLocation.getText());
        scheduleAppointment.setType(afType.getText());
        scheduleAppointment.setCustomerID(Integer.parseInt(afCustomerID.getText()));
        scheduleAppointment.setUsersID(Integer.parseInt(afUserID.getText()));
        scheduleAppointment.setContactsID(ContactsDao.returnContactIDbyName(afCustomerID.getText()));
        DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
        LocalDate localStartDate = afStartDatePicker.getValue();
        LocalDate localEndDate = afEndDatePicker.getValue();
        LocalTime localEndTime = RelatedTime.formattedTimeParser(hourAndMinuteFormat, afEndTimePicker.getValue());
        LocalTime localStartTime = RelatedTime.formattedTimeParser(hourAndMinuteFormat, afStartTimePicker.getValue());
        LocalDateTime localStartDT = LocalDateTime.of(localStartDate, localStartTime);
        LocalDateTime localEndDT = LocalDateTime.of(localEndDate, localEndTime);
        scheduleAppointment.setStartDateTime(localStartDT);
        scheduleAppointment.setEndDateTime(localEndDT);
        try {
            if (!isModified) {
                if (compareAppointmentToBusiness(scheduleAppointment)) {
                    AppointmentsDao.insertAppointmentIntoDB(scheduleAppointment);
                    //if the appointment complies to business times from the usertime then allow it to
                    //be submitted to the database in utc which is
                }
            }
            }catch(SQLException e){
                e.printStackTrace();
                System.out.println("There was an error inserting the appointment into the database");
            }
        try {
            if (isModified) {
                if (compareAppointmentToBusiness(scheduleAppointment)) {
                    AppointmentsDao.updateAppointment(scheduleAppointment);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        isModified = false;
        closeSceneWindow();
    }



    public static void takeAppointmentDisplayToUser(Appointments appointments) {
    //we get the local time and set it to


    }

    /**
     *
     * @param appointments takes in an appointment to compare the times with the business times to make sure they coincide.
     *                     The method will also remove the selected time from the appointment time start observable list
     * @return returns true or false whether the appointment fits the business schedule
     */
    public boolean compareAppointmentToBusiness(Appointments appointments) {
        DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
        ZonedDateTime userZdt = ZonedDateTime.of(appointments.getStartDateTime(), ZoneId.systemDefault());
        ZonedDateTime estZdt = userZdt.withZoneSameInstant(ZoneId.of("America/New_York"));

        int checkWithinBusinessWeek = appointments.getStartDateTime().toLocalDate().getDayOfWeek().getValue();
        LocalTime estLocalTime = estZdt.toLocalTime();
        LocalTime businessOpenTime = LocalTime.of(8, 0, 0);
        LocalTime businessCloseTime = LocalTime.of(22, 0, 0);
        int startBusinessWeek = DayOfWeek.MONDAY.getValue();
        int endBusinessWeek = DayOfWeek.FRIDAY.getValue();
        List<String> match = new ArrayList<>(afStartTimePicker.getItems());

        if (estLocalTime.isAfter(businessOpenTime) && estLocalTime.isBefore(businessCloseTime)) {
            if (checkWithinBusinessWeek > startBusinessWeek && checkWithinBusinessWeek < endBusinessWeek) {
                afStartTimePicker.getItems().removeIf(s -> RelatedTime.formattedTimeParser(hourAndMinuteFormat, s).equals(estLocalTime));
                afEndTimePicker.getItems().removeIf(s -> RelatedTime.formattedTimeParser(hourAndMinuteFormat, s).equals(estLocalTime));
                //formatting the time from the combobox into a local time, and comparing it with the ESTlocal time
                //if there is a match then remove it from the local time
            }
            return true;
        }
        System.out.println(match);
        return false;
    }


    //TODO create autogenerated appointmentID
    //also add the textfield for customer_ID and display it, but make it disabled, and prompt text with the value

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        afStartTimePicker.setItems(setTimeComboBoxes());
        afEndTimePicker.setItems(setTimeComboBoxes());
        if(isModified) {
            try {
                populateContactList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            appointmentVarTitle.setText("Update Appointment");
            afTitle.setText(AppointmentsMainController.selectedAppointment.getTitle());
            afDescription.setText(AppointmentsMainController.selectedAppointment.getDescription());
            afLocation.setText(AppointmentsMainController.selectedAppointment.getLocation());
            afType.setText(AppointmentsMainController.selectedAppointment.getType());
            afCustomerID.setText(String.valueOf(AppointmentsMainController.selectedAppointment.getCustomerID()));
            afUserID.setText(String.valueOf(AppointmentsMainController.selectedAppointment.getUsersID()));
        }
        if(!isModified) {
            appointmentVarTitle.setText("Schedule Appointment");
            try {
                populateContactList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.daos.ContactsDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.util.Alerter;
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
import java.sql.Time;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
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
    TextField afAppointmentID;
    @FXML
    DatePicker afDatePicker;
    @FXML
    ComboBox<String> afStartTimePicker;
    @FXML
    ComboBox<String> afEndTimePicker;
    @FXML
    ComboBox<String> afContact;
    @FXML
    Label appointmentVarTitle;


    public static boolean isModified = false;
    public static boolean isValidated = false;

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

    public boolean fieldValidator(){
        if(afAppointmentID.getText() == null){
            Alerter.warningAlert("Please fill in the Appointment ID field!");
            return false;
        }
        if(afDescription.getText() == null){
            Alerter.warningAlert("Please fill in the Description field!");
            return false;
        }
        if(afLocation.getText() == null){
            Alerter.warningAlert("Please fill in the Location field!");
            return false;
        }
        if(afType.getText() == null){
            Alerter.warningAlert("Please fill in the Type field!");
            return false;
        }
        if(afCustomerID.getText() == null){
            Alerter.warningAlert("Please fill in the CustomerID field!");
            return false;
        }
        if(afUserID.getText() == null){
            Alerter.warningAlert("Please fill in the UserID field!");
            return false;
        }
        if(afStartTimePicker.getSelectionModel().getSelectedItem() == null){
            Alerter.warningAlert("Please select a start time for the appointment!");
            return false;
        }
        if(afEndTimePicker.getSelectionModel().getSelectedItem() == null){
            Alerter.warningAlert("Please select a end time for the appointment!");
            return false;
        }
        if(afDatePicker.getValue() == null){
            Alerter.warningAlert("Please select a start date for the appointment!");
            return false;
        }

        System.out.println("Appointment fields validated");
        return true;
    }

    /*
    we have our appointment that we are going to add to the db
    appointment id is 0, and contact id is -1
     */
    public void addAppointmentClicked(ActionEvent event) throws SQLException {
        Appointments scheduleAppointment = new Appointments();

        scheduleAppointment.setAppointmentID(Integer.parseInt(afAppointmentID.getText()));
        scheduleAppointment.setTitle(afTitle.getText());
        scheduleAppointment.setDescription(afDescription.getText());
        scheduleAppointment.setLocation(afLocation.getText());
        scheduleAppointment.setType(afType.getText());
        scheduleAppointment.setCustomerID(Integer.parseInt(afCustomerID.getText()));
        scheduleAppointment.setUsersID(Integer.parseInt(afUserID.getText()));
        scheduleAppointment.setContactsID(ContactsDao.returnContactIDbyName(afContact.getSelectionModel().getSelectedItem()));
        DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalDate localAppointmentDate = afDatePicker.getValue();
            LocalTime localEndTime = RelatedTime.formattedTimeParser(hourAndMinuteFormat, afEndTimePicker.getValue());
            LocalTime localStartTime = RelatedTime.formattedTimeParser(hourAndMinuteFormat, afStartTimePicker.getValue());
            LocalDateTime localDateTimeStartAppointment = LocalDateTime.of(localAppointmentDate, localStartTime);
            LocalDateTime localDateTimeEndAppointment = LocalDateTime.of(localAppointmentDate, localEndTime);
            scheduleAppointment.setStartDateTime(localDateTimeStartAppointment);
            scheduleAppointment.setEndDateTime(localDateTimeEndAppointment);
        }catch (NullPointerException e){
            System.out.println("Date or time selection is null");
        }

        /*First validate all fields and selections,
        if the appointment complies to business times from the usertime then allow it to
        be submitted to the database in utc which is*/
        try {
            if (!isModified) {
                System.out.println(scheduleAppointment);
                if(fieldValidator()) {
                    if (compareAppointmentToBusiness(scheduleAppointment)) {
                        AppointmentsDao.insertAppointmentIntoDB(scheduleAppointment);
                        isModified = false;
                        if(isValidated) {
                            closeSceneWindow();
                        }
                    }
                }
            }
            }catch(SQLException e){
                e.printStackTrace();
                System.out.println("There was an error inserting the appointment into the database");
            }
        try {
            if(fieldValidator()) {
                if (isModified) {
                    System.out.println(scheduleAppointment);
                    if (compareAppointmentToBusiness(scheduleAppointment)) {
                        AppointmentsDao.updateAppointment(scheduleAppointment);
                        isModified = false;
                        if(isValidated) {
                            closeSceneWindow();
                        }
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
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

        ZonedDateTime userZdtStart = ZonedDateTime.of(appointments.getStartDateTime(), ZoneId.systemDefault());
        ZonedDateTime userZdtEnd = ZonedDateTime.of(appointments.getEndDateTime(), ZoneId.systemDefault());
        ZonedDateTime estZdtStart = userZdtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime estZdtEnd = userZdtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

        int checkWithinBusinessWeek = appointments.getStartDateTime().toLocalDate().getDayOfWeek().getValue();
        LocalTime estLocalTimeStart = estZdtStart.toLocalTime();
        LocalTime estLocalTimeEnd = estZdtEnd.toLocalTime();
        LocalTime businessOpenTime = LocalTime.of(8, 0, 0);
        LocalTime businessCloseTime = LocalTime.of(22, 0, 0);
        int startBusinessWeek = DayOfWeek.MONDAY.getValue();
        int endBusinessWeek = DayOfWeek.FRIDAY.getValue();
        System.out.println("Appointment Duration: " + Duration.between(estLocalTimeStart, estLocalTimeEnd).getSeconds());

        if (estLocalTimeStart.isBefore(businessOpenTime)) {
            Alerter.informationAlert("The appointment time: " + estLocalTimeStart + " is before the business open time: " + businessOpenTime);
            isValidated= false;
            return false;
        }
        if (estLocalTimeStart.isAfter(businessCloseTime)) {
            Alerter.informationAlert("The appointment time: " + estLocalTimeStart + " is after the business close time: " + businessCloseTime);
            isValidated= false;
            return false;
        }
        if (checkWithinBusinessWeek > endBusinessWeek) {
            Alerter.informationAlert("The business is closed on: " + appointments.getStartDateTime().toLocalDate().getDayOfWeek() + "\nBusiness days are Monday-Friday");
            isValidated= false;
            return false;
        }
        if(appointments.getEndDateTime().isBefore(appointments.getStartDateTime())){
            Alerter.informationAlert("Appointment start time must be selected before end time!");
            isValidated= false;
            return false;
        }
        if(Duration.between(estLocalTimeStart, estLocalTimeEnd).getSeconds() > 3600){
            Alerter.informationAlert("Appointments have a max time duration of 60 minutes."+"\nSelected appointment duration: "+
                    Duration.between(estLocalTimeStart, estLocalTimeEnd).getSeconds() / 60 + " minutes.");
            isValidated= false;
            return false;
        }


        if (estLocalTimeStart.isAfter(businessOpenTime) && estLocalTimeStart.isBefore(businessCloseTime)) {
            if (checkWithinBusinessWeek > startBusinessWeek && checkWithinBusinessWeek < endBusinessWeek) {
                afStartTimePicker.getItems().removeIf(s -> RelatedTime.formattedTimeParser(hourAndMinuteFormat, s).equals(estLocalTimeStart));
                afEndTimePicker.getItems().removeIf(s -> RelatedTime.formattedTimeParser(hourAndMinuteFormat, s).equals(estLocalTimeStart));
                //formatting the time from the combobox into a local time, and comparing it with the ESTlocal time
                //if there is a match then remove it from the local time
            }
            isValidated = true;
            return true;
        }
        isValidated = false;
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
            afAppointmentID.setText(String.valueOf(AppointmentsMainController.selectedAppointment.getAppointmentID()));
            appointmentVarTitle.setText("Update Appointment");
            afTitle.setText(AppointmentsMainController.selectedAppointment.getTitle());
            afDescription.setText(AppointmentsMainController.selectedAppointment.getDescription());
            afLocation.setText(AppointmentsMainController.selectedAppointment.getLocation());
            afType.setText(AppointmentsMainController.selectedAppointment.getType());
            afCustomerID.setText(String.valueOf(AppointmentsMainController.selectedAppointment.getCustomerID()));
            afUserID.setText(String.valueOf(AppointmentsMainController.selectedAppointment.getUsersID()));
            if(AppointmentsMainController.selectedAppointment.getStartDateTime() != null){
                afDatePicker.setValue(AppointmentsMainController.selectedAppointment.getStartDateTime().toLocalDate());
            }
            if(AppointmentsMainController.selectedAppointment.getStartDateTime()!=null){
                afStartTimePicker.setValue(AppointmentsMainController.selectedAppointment.getStartDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            if(AppointmentsMainController.selectedAppointment.getEndDateTime()!=null){
                afStartTimePicker.setValue(AppointmentsMainController.selectedAppointment.getEndDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
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

package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.daos.ContactsDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import com.example.customerdatabaseprojectii.util.Validator;
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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AppointmentFormController{

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

    public void initializeData(ObservableList<Customer> customers, Appointments appointment, Consumer<Appointment> onComplete){
        this.onComplete = onComplete;
        this.customers.addAll(customers);

        loadContacts();
        loadStartEndTimes();
        setNewAppointmentTimes();
        loadAppointment(appointment);
    }

    @FXML
    void initialize(){

    }

    public static boolean isModified = false;
    public static boolean isValidated = false;
    ContactsDao cd = new ContactsDao();

    public static Map<Integer, Appointments> usersIDToAppointment = new HashMap<>();
    static DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");

    public AppointmentFormController() throws SQLException {}

    public void populateContactList() throws SQLException {
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        for(Contacts contact : cd.getAllFromDB()){
            contactNames.add(contact.getContactName());}
        afContact.setItems(contactNames);
    }

    public void closeSceneWindow() {
        Stage stage = (Stage) afContact.getScene().getWindow();
        stage.close();
    }

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

    public boolean fieldValidator() {
        if (afAppointmentID.getText() == null) {
            Alerter.warningAlert("Please fill in the Appointment ID field!");
            return false;
        }
        if (afDescription.getText() == null) {
            Alerter.warningAlert("Please fill in the Description field!");
            return false;
        }
        if (afLocation.getText() == null) {
            Alerter.warningAlert("Please fill in the Location field!");
            return false;
        }
        if (afType.getText() == null) {
            Alerter.warningAlert("Please fill in the Type field!");
            return false;
        }
        if (afCustomerID.getText() == null) {
            Alerter.warningAlert("Please fill in the CustomerID field!");
            return false;
        }
        if (afUserID.getText() == null) {
            Alerter.warningAlert("Please fill in the UserID field!");
            return false;
        }
        if (afStartTimePicker.getSelectionModel().getSelectedItem() == null) {
            Alerter.warningAlert("Please select a start time for the appointment!");
            return false;
        }
        if (afEndTimePicker.getSelectionModel().getSelectedItem() == null) {
            Alerter.warningAlert("Please select a end time for the appointment!");
            return false;
        }
        if (afDatePicker.getValue() == null) {
            Alerter.warningAlert("Please select a start date for the appointment!");
            return false;
        }
        System.out.println("Appointment fields validated");
        return true;
    }

    public static void insertAppointmentIntoMap(Integer id, Appointments appointment) {
        if (!isAppointmentTimeTaken(LocalTime.parse(appointment.getStartDateTime().
                        toLocalDateTime().toLocalTime().format(hourAndMinuteFormat)),
                LocalTime.parse(appointment.getEndDateTime().toLocalDateTime().toLocalTime().format(hourAndMinuteFormat)))) {
            usersIDToAppointment.put(id, appointment);
            System.out.println("Complete & appointment is mapped");
        } else {
            Alerter.informationAlert("Cannot create an appointment because that time slot is reserved already!");
        }
    }

    public static boolean isAppointmentTimeTaken(LocalTime appointmentStart, LocalTime appointmentEnd) {
        for (Map.Entry<Integer, Appointments> entry : usersIDToAppointment.entrySet()) {
            Appointments appointments = entry.getValue();
            if (appointmentStart.isAfter
                    (LocalTime.parse(appointments.getStartDateTime().toLocalDateTime().toLocalTime().
                            format(hourAndMinuteFormat))) && appointmentStart.
                    isBefore(LocalTime.parse(appointments.getEndDateTime().
                            toLocalDateTime().toLocalTime().format(hourAndMinuteFormat))) || appointmentEnd.
                    isAfter(LocalTime.parse(appointments.getStartDateTime().toLocalDateTime().toLocalTime().
                            format(hourAndMinuteFormat))) && appointmentEnd.
                    isBefore(LocalTime.parse(appointments.getEndDateTime().toLocalDateTime().toLocalTime().format(hourAndMinuteFormat)))) {
                return true;
            }
        }
        return false;
    }

    public boolean fieldValidator(Appointments scheduleAppointment) throws SQLException {
        ContactsDao cd = new ContactsDao();

        if (Validator.intChecker(afAppointmentID.getText(), "Please enter number characters for AppointmentID text field!")) {
            scheduleAppointment.setAppointmentID(Integer.parseInt(afAppointmentID.getText()));
        } else {
            return false;
        }
        if (Validator.stringChecker(afTitle.getText(), "Please only enter alphabetical characters for Title field!")) {
            scheduleAppointment.setTitle(afTitle.getText());
        } else {
            return false;
        }
        if (Validator.stringChecker(afDescription.getText(), "Please only enter alphabetical characters for Description field!")) {
            scheduleAppointment.setDescription(afDescription.getText());
        } else {
            return false;
        }
        if (Validator.stringChecker(afLocation.getText(), "Please only enter alphabetical characters for Location field!")) {
            scheduleAppointment.setLocation(afLocation.getText());
        } else {
            return false;
        }
        if (Validator.stringChecker(afType.getText(), "Please only enter alphabetical characters for Type field!")) {
            scheduleAppointment.setType(afType.getText());
        } else {
            return false;
        }
        if (Validator.intChecker(afCustomerID.getText(), "Please only enter number characters for Description field!")) {
            scheduleAppointment.setCustomerID(Integer.parseInt(afCustomerID.getText()));
        } else {
            return false;
        }
        if (Validator.intChecker(afUserID.getText(), "Please only enter number characters for UserID field!")) {
            scheduleAppointment.setUsersID(Integer.parseInt(afUserID.getText()));
        } else {
            return false;
        }
        if (Validator.intChecker(String.valueOf(cd.returnContactIDbyName(afContact.getValue())), "Issue obtaining contactID")) {
            scheduleAppointment.setContactsID(cd.returnContactIDbyName(afContact.getValue()));
        } else {
            return false;
        }
        return true;
    }

    public void addAppointmentClicked(ActionEvent event) throws SQLException {
        Appointments appointmentstemp = AppointmentsMainController.selectedAppointment;
//        int appIndex = AppointmentsMainController.getAppointmentIndex(appointmentstemp, "ALL");
        Appointments scheduleAppointment = new Appointments();
        fieldValidator(scheduleAppointment);
        DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalDate localAppointmentDate = afDatePicker.getValue();
            LocalTime localEndTime = RelatedTime.formattedTimeParser(hourAndMinuteFormat, afEndTimePicker.getValue());
            LocalTime localStartTime = RelatedTime.formattedTimeParser(hourAndMinuteFormat, afStartTimePicker.getValue());
            LocalDateTime localDateTimeStartAppointment = LocalDateTime.of(localAppointmentDate, localStartTime);
            LocalDateTime localDateTimeEndAppointment = LocalDateTime.of(localAppointmentDate, localEndTime);
            scheduleAppointment.setStartDateTime(Timestamp.valueOf(localDateTimeStartAppointment));
            scheduleAppointment.setEndDateTime(Timestamp.valueOf(localDateTimeEndAppointment));
        } catch (NullPointerException e) {
            System.out.println("Date or time selection is null");
        }
    }


    public void resetBoxes() {
        afDescription.clear();
        afLocation.clear();
        afType.clear();
        afCustomerID.clear();
        afUserID.clear();
        afDatePicker.setValue(null);
        afStartTimePicker.setValue(null);//maybe""?
        afEndTimePicker.setValue(null);
        afCustomerID.clear();
        afUserID.clear();
        afContact.setValue(null);
    }

    public boolean compareAppointmentToBusiness(Appointments appointments) {
        DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");

        ZonedDateTime userZdtStart = ZonedDateTime.of(appointments.getStartDateTime().toLocalDateTime(), ZoneId.systemDefault());
        ZonedDateTime userZdtEnd = ZonedDateTime.of(appointments.getEndDateTime().toLocalDateTime(), ZoneId.systemDefault());
        ZonedDateTime estZdtStart = userZdtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime estZdtEnd = userZdtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

        int checkWithinBusinessWeek = appointments.getStartDateTime().toLocalDateTime().toLocalDate().getDayOfWeek().getValue();
        LocalTime estLocalTimeStart = estZdtStart.toLocalTime();
        LocalTime estLocalTimeEnd = estZdtEnd.toLocalTime();
        LocalTime businessOpenTime = LocalTime.of(8, 0, 0);
        LocalTime businessCloseTime = LocalTime.of(22, 0, 0);
        int startBusinessWeek = DayOfWeek.MONDAY.getValue();
        int endBusinessWeek = DayOfWeek.FRIDAY.getValue();
        System.out.println("Appointment Duration: " + Duration.between(estLocalTimeStart, estLocalTimeEnd).getSeconds());

        if (estLocalTimeStart.isBefore(businessOpenTime)) {
            Alerter.informationAlert("The appointment time: " + estLocalTimeStart + " is before the business open time: " + businessOpenTime);
            isValidated = false;
            return false;
        }
        if (estLocalTimeStart.isAfter(businessCloseTime)) {
            Alerter.informationAlert("The appointment time: " + estLocalTimeStart + " is after the business close time: " + businessCloseTime);
            isValidated = false;
            return false;
        }
        if (checkWithinBusinessWeek > endBusinessWeek) {
            Alerter.informationAlert("The business is closed on: " + appointments.getStartDateTime().toLocalDateTime().toLocalDate().getDayOfWeek() + "\nBusiness days are Monday-Friday");
            isValidated = false;
            return false;
        }
        if (appointments.getEndDateTime().before(appointments.getStartDateTime())) {
            Alerter.informationAlert("Appointment start time must be selected before end time!");
            isValidated = false;
            return false;
        }
        if (Duration.between(estLocalTimeStart, estLocalTimeEnd).getSeconds() > 3600) {
            Alerter.informationAlert("Appointments have a max time duration of 60 minutes." + "\nSelected appointment duration: " +
                    Duration.between(estLocalTimeStart, estLocalTimeEnd).getSeconds() / 60 + " minutes.");
            isValidated = false;
            return false;
        }
        if (estLocalTimeStart.isAfter(businessOpenTime) && estLocalTimeStart.isBefore(businessCloseTime)) {
            if (checkWithinBusinessWeek > startBusinessWeek && checkWithinBusinessWeek < endBusinessWeek) {
                afStartTimePicker.getItems().removeIf(s -> RelatedTime.formattedTimeParser(hourAndMinuteFormat, s).equals(estLocalTimeStart));
                afEndTimePicker.getItems().removeIf(s -> RelatedTime.formattedTimeParser(hourAndMinuteFormat, s).equals(estLocalTimeStart));
            }
            isValidated = true;
            return true;
        }
        isValidated = false;
        return false;
    }
}




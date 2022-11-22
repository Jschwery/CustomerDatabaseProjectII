package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.daos.ContactsDao;
import com.example.customerdatabaseprojectii.daos.CustomersDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import com.example.customerdatabaseprojectii.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AppointmentFormController {

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
    DatePicker afDatePickerStart;
    @FXML
    DatePicker afDatePickerEnd;
    @FXML
    ComboBox<String> afStartTimePicker;
    @FXML
    ComboBox<String> afEndTimePicker;
    @FXML
    ComboBox<String> afContact;
    @FXML
    Label appointmentVarTitle;
    @FXML
    ComboBox<String> afSelectCustomer;

    Consumer<Appointments> appointmentHandler;
    Supplier<ObservableList<String>> customersSupplier;
    ObservableList<Contacts> contactsObservableList;
    Appointments appointment;
    public static boolean isModified = false;
    ContactsDao contactsDao = new ContactsDao();
    ObservableList<Customers> observableListOfCustomers;
    static DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
    @FXML
    void initialize(){
        setTimeComboBoxes();
    }

    public void appointmentInit(ObservableList<Contacts> contactsObservableList, Appointments appointment,
                                Consumer<Appointments> appointmentHandler, Supplier<ObservableList<String>> customersSupplier,
                                ObservableList<Customers> customersList) throws SQLException {
        this.appointmentHandler = appointmentHandler;
        this.contactsObservableList = contactsObservableList;
        this.appointment = appointment;
        this.customersSupplier = customersSupplier;
        this.observableListOfCustomers = customersList;
        if(appointment == null){
            this.appointment = new Appointments();
        }
        afSelectCustomer.setItems(customersSupplier.get());
        afStartTimePicker.setItems(setTimeComboBoxes());
        afEndTimePicker.setItems(setTimeComboBoxes());
        populateContactList();

        if(isModified) {
            assert appointment != null;
            fillAppointmentData(appointment);
        }
        if(!isModified){
            AppointmentsDao ad = new AppointmentsDao();
            afAppointmentID.setText(String.valueOf(ad.getAllFromDB().size() + 1));
            afUserID.setPromptText(String.format("Your ID: %d", LoginController.getCurrentlyLoggedInUser().getUser_ID()));
        }
    }

    public String getContactNameByID(int contactID) throws SQLException {
        Optional<Contacts> contact = contactsObservableList.stream().filter(c -> Objects.equals(c.getContactID(), contactID)).findFirst();
        if(contact.isPresent()){
            return contact.get().getContactName();
        }
        System.out.println("No contact found with the entered ID");
        return "";
    }

    public String getCustomerNameByID(int customerID) throws SQLException {
        Optional<Customers> customers = observableListOfCustomers.stream().filter(c -> Objects.equals(c.getCustomerID(), customerID)).findFirst();

        if(customers.isPresent()){
            return customers.get().getCustomerName();
        }
        System.out.println("No customer found with the entered id");
        return "";
    }
    /**
     *
     * @param event when the add button is clicked
     *              all the textbox information and selection information will be added to a map,
     *              that maps the customerid to the appointment, and then adds the appointment to the database
     *              and to the observable list
     * @throws SQLException
     */
    public void getAppointmentDataAndSubmit(ActionEvent event) throws SQLException, IOException {
        try {
            DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
            LocalDate localAppointmentDateEnd = afDatePickerEnd.getValue();
            LocalDate localAppointmentDateStart = afDatePickerStart.getValue();
            LocalTime localEndTime = RelatedTime.formattedTimeParser(hourAndMinuteFormat, afEndTimePicker.getValue());
            LocalTime localStartTime = RelatedTime.formattedTimeParser(hourAndMinuteFormat, afStartTimePicker.getValue());
            LocalDateTime localDateTimeStartAppointment = LocalDateTime.of(localAppointmentDateStart, localStartTime);
            LocalDateTime localDateTimeEndAppointment = LocalDateTime.of(localAppointmentDateEnd, localEndTime);
            appointment.setStartDateTime(Timestamp.valueOf(localDateTimeStartAppointment));
            appointment.setEndDateTime(Timestamp.valueOf(localDateTimeEndAppointment));
        }catch (NullPointerException e){
            Alerter.warningAlert("Please fill in all the fields!");
        }
        if(fieldValidator(appointment) && compareAppointmentToBusiness(appointment)){
            appointmentHandler.accept(appointment);
            if(isModified){//checks if the customer already had an appointment and will update the map with the new appointment, if so.
                for (Map.Entry<Integer, Appointments> entry : AppointmentMainController.customerIDToAppointment.entrySet()) {
                    if(entry.getKey().equals(appointment.getContactsID())){
                        AppointmentMainController.customerIDToAppointment.put(appointment.getCustomerID(), appointment);
                    }
                }
            }else{
                insertAppointmentIntoMap(appointment.getCustomerID(), appointment);
            }
            Main.playSound("src/main/resources/selectrewardsound.wav");
            resetBoxes();
            isModified = false;
            Stage stage = (Stage) afType.getScene().getWindow();
            stage.close();
        }
    }

    public int findCustomerIDByName(String customerName){
        CustomersDao cd = new CustomersDao();
        try {
            for (Customers customer : cd.getAllFromDB()){
                if(Objects.equals(customerName.toUpperCase(), customerName.toUpperCase())){
                    return customer.getCustomerID();
                }
            }
        }catch (SQLException s){
            s.printStackTrace();
        }
        System.out.println("No customer found with the name entered");
        return -1;
    }

    public void fillAppointmentData(Appointments appointment) throws SQLException {
        DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");

        afDatePickerStart.setValue(appointment.getStartDateTime().toLocalDateTime().toLocalDate());
        afDatePickerEnd.setValue(appointment.getStartDateTime().toLocalDateTime().toLocalDate());
        afStartTimePicker.setValue(appointment.getStartDateTime().toLocalDateTime().toLocalTime().format(hourAndMinuteFormat));
        afEndTimePicker.setValue(appointment.getEndDateTime().toLocalDateTime().toLocalTime().format(hourAndMinuteFormat));
        afType.setText(appointment.getType());
        afUserID.setText(String.valueOf(appointment.getUsersID()));
        afSelectCustomer.setValue(getCustomerNameByID(appointment.getCustomerID()));
        afLocation.setText(appointment.getLocation());
        afDescription.setText(appointment.getDescription());
        afContact.setValue(getContactNameByID(appointment.getContactsID()));
        afAppointmentID.setText(String.valueOf(appointment.getAppointmentID()));
        afTitle.setText(appointment.getTitle());
    }

    public AppointmentFormController() throws SQLException {}

    public void populateContactList() throws SQLException {
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        for(Contacts contact : contactsDao.getAllFromDB()){
            contactNames.add(contact.getContactName());}
        afContact.setItems(contactNames);
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

    //customerID mapped to an appointment
    public static void insertAppointmentIntoMap(Integer id, Appointments appointment) {
        if (!isAppointmentTimeTaken(LocalTime.parse(appointment.getStartDateTime().
                        toLocalDateTime().toLocalTime().format(hourAndMinuteFormat)),
                LocalTime.parse(appointment.getEndDateTime().toLocalDateTime().toLocalTime().format(hourAndMinuteFormat)))) {
            AppointmentMainController.customerIDToAppointment.put(id, appointment);
            System.out.println("Complete & appointment is mapped");
        } else {
            Alerter.informationAlert("Cannot create an appointment because that time slot is reserved already!");
        }
    }

    public static boolean isAppointmentTimeTaken(LocalTime appointmentStart, LocalTime appointmentEnd) {
        for (Map.Entry<Integer, Appointments> entry : AppointmentMainController.customerIDToAppointment.entrySet()) {
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
        if (Validator.intChecker(String.valueOf(findCustomerIDByName(afSelectCustomer.getValue())), "Please only enter number characters for Description field!")) {
            scheduleAppointment.setCustomerID(findCustomerIDByName(afSelectCustomer.getValue()));
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

    public void resetBoxes() {
        afDescription.clear();
        afLocation.clear();
        afType.clear();
        afSelectCustomer.setValue(null);
        afUserID.clear();
        afDatePickerEnd.setValue(null);
        afDatePickerStart.setValue(null);
        afStartTimePicker.setValue(null);//maybe""?
        afEndTimePicker.setValue(null);
        afUserID.clear();
        afContact.setValue(null);
    }

    public void cancelButtonClicked(ActionEvent event){
        Stage stage = (Stage) afTitle.getScene().getWindow();
        stage.close();
    }

    public boolean compareAppointmentToBusiness(Appointments appointments) {

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
            return false;
        }
        if (estLocalTimeStart.isAfter(businessCloseTime)) {
            Alerter.informationAlert("The appointment time: " + estLocalTimeStart + " is after the business close time: " + businessCloseTime);
            return false;
        }
        if (checkWithinBusinessWeek > endBusinessWeek) {
            Alerter.informationAlert("The business is closed on: " + appointments.getStartDateTime().toLocalDateTime().toLocalDate().getDayOfWeek() + "\nBusiness days are Monday-Friday");
            return false;
        }
        if (appointments.getEndDateTime().before(appointments.getStartDateTime())) {
            Alerter.informationAlert("Appointment start time must be selected before end time!");
            return false;
        }
        if (Duration.between(estLocalTimeStart, estLocalTimeEnd).getSeconds() > 3600) {
            Alerter.informationAlert("Appointments have a max time duration of 60 minutes." + "\nSelected appointment duration: " +
                    Duration.between(estLocalTimeStart, estLocalTimeEnd).getSeconds() / 60 + " minutes.");
            return false;
        }
        /*if the time is after the business opens and before they close and within the business week
            return true and validated set true
        */
        if (estLocalTimeStart.isAfter(businessOpenTime) && estLocalTimeStart.isBefore(businessCloseTime)) {
            return checkWithinBusinessWeek > startBusinessWeek && checkWithinBusinessWeek < endBusinessWeek;
        }
        System.out.println("Didn't pass the business test");
        return false;
    }
}




package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.daos.ContactsDao;
import com.example.customerdatabaseprojectii.daos.CustomersDao;
import com.example.customerdatabaseprojectii.daos.UsersDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import com.example.customerdatabaseprojectii.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AppointmentFormController {

    public static boolean isModified = false;
    static DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
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
    ObservableList<Appointments> appointmentList;
    ContactsDao contactsDao = new ContactsDao();
    ObservableList<Customers> observableListOfCustomers;

    public AppointmentFormController() throws SQLException {
    }

    /**
     * @throws SQLException
     * @lambda lambda used to insert each appointment into the map, which is gets from the database
     * which is needed to correctly map each user to an appointment
     * <p>
     * Upon application startup maps each user, to their appointment IF there is an appointment correlated to that
     * particular user.
     */
    public static void fillUserAppointmentMap() throws SQLException {
        AppointmentsDao ad = new AppointmentsDao();
        ad.getAllFromDB().forEach(app -> insertAppointmentIntoMap(app.getUsersID(), app));
    }

    //customerID mapped to an appointment
    public static void insertAppointmentIntoMap(Integer id, Appointments appointment) {
        if (!isAppointmentTimeTaken(LocalTime.parse(appointment.getStartDateTime().
                        toLocalTime().format(hourAndMinuteFormat)),
                LocalTime.parse(appointment.getEndDateTime().toLocalTime().format(hourAndMinuteFormat)))) {
            AppointmentMainController.userIDToAppointment.put(id, appointment);
            System.out.println("Complete & appointment is mapped");
        } else {
            System.out.printf("Could not add appointment for user with ID: %d%n", appointment.getUsersID());
        }
    }

    /**
     * @param appointmentStart the appointment start time to be checked
     * @param appointmentEnd   the appointment end time to be checked
     * @return returns true if the appointment time slot is already in use, and false if the time slot is already taken
     */
    public static boolean isAppointmentTimeTaken(LocalTime appointmentStart, LocalTime appointmentEnd) {
        for (Map.Entry<Integer, Appointments> entry : AppointmentMainController.userIDToAppointment.entrySet()) {
            Appointments appointments = entry.getValue();
            if (appointmentStart.isAfter
                    (LocalTime.parse(appointments.getStartDateTime().toLocalTime().
                            format(hourAndMinuteFormat))) && appointmentStart.
                    isBefore(LocalTime.parse(appointments.getEndDateTime().
                            toLocalTime().format(hourAndMinuteFormat))) || appointmentEnd.
                    isAfter(LocalTime.parse(appointments.getStartDateTime().toLocalTime().
                            format(hourAndMinuteFormat))) && appointmentEnd.
                    isBefore(LocalTime.parse(appointments.getEndDateTime().toLocalTime().format(hourAndMinuteFormat)))) {
                return true;
            }
        }
        return false;
    }

    @FXML
    void initialize() {
        setTimeComboBoxes();
    }

    public void appointmentInit(ObservableList<Contacts> contactsObservableList, Appointments appointment,
                                Consumer<Appointments> appointmentHandler, Supplier<ObservableList<String>> customersSupplier,
                                ObservableList<Customers> customersList, ObservableList<Appointments> appointmentList) throws SQLException {
        this.appointmentHandler = appointmentHandler;
        this.contactsObservableList = contactsObservableList;
        this.appointment = appointment;
        this.customersSupplier = customersSupplier;
        this.observableListOfCustomers = customersList;
        this.appointmentList = appointmentList;
        if (appointment == null) {
            this.appointment = new Appointments();
        }
        afSelectCustomer.setItems(customersSupplier.get());
        afStartTimePicker.setItems(setTimeComboBoxes());
        afEndTimePicker.setItems(setTimeComboBoxes());
        populateContactList();

        if (isModified) {
            assert appointment != null;
            fillAppointmentData(appointment);
        }
        if (!isModified) {
            AppointmentsDao ad = new AppointmentsDao();
            afAppointmentID.setText(String.valueOf(ad.getAllFromDB().size() + 1));
            afUserID.setPromptText(String.format("Your ID: %d", LoginController.getCurrentlyLoggedInUser().getUser_ID()));
        }
    }

    public String getContactNameByID(int contactID) throws SQLException {
        Optional<Contacts> contact = contactsObservableList.stream().filter(c -> Objects.equals(c.getContactID(), contactID)).findFirst();
        if (contact.isPresent()) {
            return contact.get().getContactName();
        }
        System.out.println("No contact found with the entered ID");
        return "";
    }

    public String getCustomerNameByID(int customerID) throws SQLException {
        Optional<Customers> customers = observableListOfCustomers.stream().filter(c -> Objects.equals(c.getCustomerID(), customerID)).findFirst();

        if (customers.isPresent()) {
            return customers.get().getCustomerName();
        }
        System.out.println("No customer found with the entered id");
        return "";
    }

    /**
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
            appointment.setStartDateTime(localDateTimeStartAppointment);
            appointment.setEndDateTime(localDateTimeEndAppointment);
        } catch (NullPointerException e) {
            Alerter.warningAlert("Please fill in all the fields!");
        }
        if (fieldValidator(appointment) && compareAppointmentToBusiness(appointment)) {
            appointment.setTitle(afTitle.getText());
            appointment.setLocation(afLocation.getText());
            appointment.setDescription(afDescription.getText());

            Main.playSound("src/main/resources/selectrewardsound.wav");
            appointmentHandler.accept(appointment);
            resetBoxes();
            isModified = false;
            Stage stage = (Stage) afType.getScene().getWindow();
            stage.close();
        }
    }

    public int findCustomerIDByName(String customerName) {
        CustomersDao cd = new CustomersDao();
        try {
            for (Customers customer : cd.getAllFromDB()) {
                if (Objects.equals(customerName.toUpperCase(), customer.getCustomerName().toUpperCase())) {
                    return customer.getCustomerID();
                }
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
        System.out.println("No customer found with the name entered");
        return -1;
    }

    /**
     * @param appointment Takes in an appointment and
     * @throws SQLException
     */
    public void fillAppointmentData(Appointments appointment) throws SQLException {
        DateTimeFormatter hourAndMinuteFormat = DateTimeFormatter.ofPattern("HH:mm");
        ZonedDateTime userStartZdt = ZonedDateTime.of(appointment.getStartDateTime(), RelatedTime.getUserTimeZone());
        ZonedDateTime userEndZdt = ZonedDateTime.of(appointment.getEndDateTime(), RelatedTime.getUserTimeZone());

        afDatePickerStart.setValue(appointment.getStartDateTime().toLocalDate());
        afDatePickerEnd.setValue(appointment.getStartDateTime().toLocalDate());
        afStartTimePicker.setValue(userStartZdt.toLocalDateTime().format(hourAndMinuteFormat));
        afEndTimePicker.setValue(userEndZdt.toLocalDateTime().format(hourAndMinuteFormat));
        afType.setText(appointment.getType());
        afUserID.setText(String.valueOf(appointment.getUsersID()));
        afSelectCustomer.setValue(getCustomerNameByID(appointment.getCustomerID()));
        afLocation.setText(appointment.getLocation());
        afDescription.setText(appointment.getDescription());
        afContact.setValue(getContactNameByID(appointment.getContactsID()));
        afAppointmentID.setText(String.valueOf(appointment.getAppointmentID()));
        afTitle.setText(appointment.getTitle());
    }

    public void populateContactList() throws SQLException {
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        for (Contacts contact : contactsDao.getAllFromDB()) {
            contactNames.add(contact.getContactName());
        }
        afContact.setItems(contactNames);
    }

    /**
     * @return returns an observable list that will be used to set the time available for appointments
     * which is from 8 am until 10pm, with 15 minute increments
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

    /**
     * @param scheduleAppointment takes in an appointment and sets the appointments values if the text fields
     *                            comply with the type expected
     * @return returns true if all the fields are validated, and false if any of the fields fails to validate
     * @throws SQLException
     */
    public boolean fieldValidator(Appointments scheduleAppointment) throws SQLException {
        ContactsDao cd = new ContactsDao();

        if (Validator.intChecker(afAppointmentID.getText(), "Please enter number characters for AppointmentID text field!")) {
            scheduleAppointment.setAppointmentID(Integer.parseInt(afAppointmentID.getText()));
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

    /**
     * resets all the textfields and comboboxes
     */
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

    /**
     * closes the scene
     *
     * @param event
     */
    public void cancelButtonClicked(ActionEvent event) {
        Stage stage = (Stage) afTitle.getScene().getWindow();
        stage.close();
    }

    /**
     * @param appointments takes an appointment and makes sure that its start and end data & time comply with
     *                     the hours days that the business is open
     * @return returns true if the appointment complies, and false otherwise
     */
    public boolean compareAppointmentToBusiness(Appointments appointments) throws IOException {

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
            Main.playSound("src/main/resources/errorSound.wav");
            Alerter.informationAlert("The appointment time: " + estLocalTimeStart + " is before the business open time: " + businessOpenTime);
            return false;
        }
        if (estLocalTimeStart.isAfter(businessCloseTime)) {
            Main.playSound("src/main/resources/errorSound.wav");
            Alerter.informationAlert("The appointment time: " + estLocalTimeStart + " is after the business close time: " + businessCloseTime);
            return false;
        }
        if (checkWithinBusinessWeek > endBusinessWeek) {
            Main.playSound("src/main/resources/errorSound.wav");
            Alerter.informationAlert("The business is closed on: " + appointments.getStartDateTime().toLocalDate().getDayOfWeek() + "\nBusiness days are Monday-Friday");
            return false;
        }
        if (appointments.getEndDateTime().isBefore(appointments.getStartDateTime())) {
            Main.playSound("src/main/resources/errorSound.wav");
            Alerter.informationAlert("Appointment start time must be selected before end time!");
            return false;
        }
        if (Duration.between(estLocalTimeStart, estLocalTimeEnd).getSeconds() > 3600) {
            Main.playSound("src/main/resources/errorSound.wav");
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




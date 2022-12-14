package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.daos.ContactsDao;
import com.example.customerdatabaseprojectii.daos.CustomersDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AppointmentMainController implements Initializable {

    public static Map<Integer, Appointments> userIDToAppointment = new HashMap<>();
    protected static Appointments selectedAppointment = null;
    static Users currentUser = LoginController.getCurrentlyLoggedInUser();
    @FXML
    Label appointmentTableLabel;
    @FXML
    ComboBox<String> appointmentsSwitchTableComboBox;
    @FXML
    Button appointmentsSwitchTableButton;
    @FXML
    TabPane appointmentsTabPane;
    @FXML
    Tab appointmentsWeeklyTab;
    @FXML
    Tab appointmentsMonthlyTab;
    @FXML
    Tab appointmentsAllTab;
    //Weekly columns
    @FXML
    TableColumn<Appointments, Integer> aptWeekID;
    @FXML
    TableColumn<Appointments, String> aptWeekTitle;
    @FXML
    TableColumn<Appointments, String> aptWeekDescription;
    @FXML
    TableColumn<Appointments, String> aptWeekLocation;
    @FXML
    TableColumn<Appointments, String> aptWeekType;
    @FXML
    TableColumn<Appointments, LocalDateTime> aptWeekStartDate;
    @FXML
    TableColumn<Appointments, LocalDateTime> aptWeekEndDate;
    @FXML
    TableColumn<Appointments, Integer> aptWeekCustomerID;
    @FXML
    TableColumn<Appointments, Integer> aptWeekUserID;
    @FXML
    TableColumn<Appointments, Integer> aptWeekContactID;
    //Monthly columns
    @FXML
    TableColumn<Appointments, Integer> aptMonthID;
    @FXML
    TableColumn<Appointments, String> aptMonthTitle;
    @FXML
    TableColumn<Appointments, String> aptMonthDescription;
    @FXML
    TableColumn<Appointments, String> aptMonthLocation;
    @FXML
    TableColumn<Appointments, String> aptMonthType;
    @FXML
    TableColumn<Appointments, LocalDateTime> aptMonthStartDate;
    @FXML
    TableColumn<Appointments, LocalDateTime> aptMonthEndDate;
    @FXML
    TableColumn<Appointments, Integer> aptMonthCustomerID;
    @FXML
    TableColumn<Appointments, Integer> aptMonthUserID;
    @FXML
    TableColumn<Appointments, Integer> aptMonthContactID;
    //All columns
    @FXML
    TableColumn<Appointments, Integer> aptAllID;
    @FXML
    TableColumn<Appointments, String> aptAllTitle;
    @FXML
    TableColumn<Appointments, String> aptAllDescription;
    @FXML
    TableColumn<Appointments, String> aptAllLocation;
    @FXML
    TableColumn<Appointments, String> aptAllType;
    @FXML
    TableColumn<Appointments, LocalDateTime> aptAllStartDate;
    @FXML
    TableColumn<Appointments, LocalDateTime> aptAllEndDate;
    @FXML
    TableColumn<Appointments, Integer> aptAllCustomerID;
    @FXML
    TableColumn<Appointments, Integer> aptAllUserID;
    @FXML
    TableColumn<Appointments, Integer> aptAllContactID;
    @FXML
    TableView<Appointments> aptWeeklyTableView;
    @FXML
    TableView<Appointments> aptMonthlyTableView;
    @FXML
    TableView<Appointments> aptAllTableView;
    @FXML
    Button wkAddAppointmentButton;
    @FXML
    Button wkUpdateAppointmentButton;
    @FXML
    Button wkDeleteAppointmentButton;
    @FXML
    Label timeZoneText;
    @FXML
    Button mnthAddAppointmentButton;
    @FXML
    Button mnthUpdateAppointmentButton;
    @FXML
    Button mnthDeleteAppointmentButton;
    @FXML
    Button allAddAppointmentButton;
    @FXML
    Button allUpdateAppointmentButton;
    @FXML
    Button allDeleteAppointmentButton;
    AppointmentsDao ad = new AppointmentsDao();
    CustomersDao cd = new CustomersDao();
    ContactsDao contactsDao = new ContactsDao();
    ObservableList<Appointments> appointmentsList = FXCollections.observableArrayList();
    ObservableList<Customers> customersObservableList = cd.getAllFromDB();
    ObservableList<Contacts> contactsObservableList = contactsDao.getAllFromDB();

    public AppointmentMainController() throws SQLException {
    }

    /**
     * @param appointment the appointment in which you are looking for the index
     * @return returns the index of the appointment within the observable list
     */
    public int getIndexOfAppointment(Appointments appointment) {
        return appointmentsList.indexOf(appointment);
    }

    /**
     * sets the selected appointment with a mouse click on the appointment tableview
     *
     * @param event
     */
    public void setSelectedAppointment(MouseEvent event) {
        if (appointmentsAllTab.isSelected()) {
            selectedAppointment = aptAllTableView.getSelectionModel().getSelectedItem();
        } else if (appointmentsWeeklyTab.isSelected()) {
            selectedAppointment = aptWeeklyTableView.getSelectionModel().getSelectedItem();
        } else if (appointmentsMonthlyTab.isSelected()) {
            selectedAppointment = aptMonthlyTableView.getSelectionModel().getSelectedItem();
        }
    }

    public void setTextLabel() {
        ZoneId userTimeZone = ZoneId.systemDefault();
        timeZoneText.setText(String.format("Time Zone: %s", userTimeZone));
    }

    /**
     * @param event when the delete button is clicked, it will make sure that the appointment selected is not null
     *              and if it is, then alert the user that they are trying to delete an appointment that is not selected.
     *              If the appointment is not null then it will first delete the appointment from the db, then from the list that
     *              the tableview is using, then refresh the list of the tableview
     * @throws SQLException
     * @throws IOException
     */
    public void deleteAppointment(ActionEvent event) throws SQLException, IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this appointment?");
        Optional<ButtonType> buttonResult = alert.showAndWait();

        if (buttonResult.orElse(null) == ButtonType.OK && selectedAppointment != null) {
            ad.deleteFromDB(selectedAppointment);
            appointmentsList.remove(selectedAppointment);
            setTableFilteredAppointments();
            Main.playSound("src/main/resources/errorSound.wav");
            if (buttonResult.orElse(null) == ButtonType.CANCEL) {
                setTableFilteredAppointments();
            }
        }
        else if(Objects.equals(selectedAppointment, null)){
            Alerter.informationAlert("No appointment selected to delete");
        }
    }

    /**
     * @param event when the add appointment button is clicked the scenes will switch and
     *              the formInit method will be called with the parameters set
     * @throws IOException
     */
    public void addAppointmentForm(ActionEvent event) throws IOException {
        if (Objects.equals(selectedAppointment, null)) {
            URL path = new File("src/main/java/com/example/customerdatabaseprojectii/view/AppointmentForm.fxml").toURI().toURL();
            AppointmentFormController.isModified = false;
            FXMLLoader fxL = new FXMLLoader();
            fxL.setLocation(path);
            Parent node = fxL.load();
            AppointmentFormController formController = fxL.getController();//getting controller of appointmentform
            Consumer<Appointments> addAppointment = appointment -> {//consumer takes appointment, inserts to db, and the appointment list, then refresh tables
                appointment.setCreatedBy(currentUser.getUsername());
                appointment.setLastUpdatedBy(currentUser.getUsername());
                appointment.setUsersID(currentUser.getUser_ID());
                try {
                    if (ad.dbInsert(appointment)) {
                        appointmentsList.add(appointment);
                        setTableFilteredAppointments();
                    }
                } catch (SQLException | IOException s) {
                    s.printStackTrace();
                }
            };
            Supplier<ObservableList<String>> customerNameSupplier = () -> {
                try {
                    CustomersDao cDao = new CustomersDao();
                    ObservableList<String> customerList = FXCollections.observableArrayList();
                    for (Customers c : cDao.getAllFromDB()) {
                        customerList.add(c.getCustomerName());
                    }
                    return customerList;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            };
            try {
                formController.appointmentInit(contactsObservableList, selectedAppointment, addAppointment,
                        customerNameSupplier, customersObservableList, appointmentsList);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Stage appointmentFormStage = new Stage();
            appointmentFormStage.setScene(new Scene(node));
            appointmentFormStage.setResizable(false);
            appointmentFormStage.initStyle(StageStyle.DECORATED);

            appointmentFormStage.show();
        } else {
            Alerter.warningAlert("You are trying to add an appointment that already exists, unselecting appointment");
            selectedAppointment = null;
        }
    }

    /**
     * @param event when update is clicked get the selected appointment,
     *              get the controller of the appointment form so that the values from the field
     *              can be submitted to the database and list via the consumer passed in, that takes the appointment
     * @throws IOException
     */
    public void updateAppointmentForm(ActionEvent event) throws IOException {
        if (selectedAppointment != null) {
            URL path = new File("src/main/java/com/example/customerdatabaseprojectii/view/AppointmentForm.fxml").toURI().toURL();
            FXMLLoader fxL = new FXMLLoader();
            fxL.setLocation(path);
            Parent node = fxL.load();
            AppointmentFormController afc = fxL.getController();
            AppointmentFormController.isModified = true;
            Consumer<Appointments> updateAppointment = appointment -> {
                appointment.setCreatedBy(currentUser.getUsername());
                appointment.setLastUpdatedBy(currentUser.getUsername());
                appointment.setUsersID(currentUser.getUser_ID());
                if (selectedAppointment != null) {
                    try {
                        ad.updateDB(appointment);
                        appointmentsList.set(getIndexOfAppointment(appointment), appointment);
                        setTableFilteredAppointments();
                    } catch (SQLException | MalformedURLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            };
            try {
                afc.appointmentInit(contactsObservableList, selectedAppointment, updateAppointment, () -> {
                    try {
                        CustomersDao cDao = new CustomersDao();
                        ObservableList<String> customerList = FXCollections.observableArrayList();
                        for (Customers c : cDao.getAllFromDB()) {
                            customerList.add(c.getCustomerName());
                        }
                        return customerList;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }, customersObservableList, appointmentsList);
            } catch (SQLException s) {
                s.printStackTrace();
            }
            Stage appointmentFormStage = new Stage();
            appointmentFormStage.setScene(new Scene(node));
            appointmentFormStage.setResizable(false);
            appointmentFormStage.initStyle(StageStyle.DECORATED);
            appointmentFormStage.show();
        } else {
            Alerter.warningAlert("Please select a appointment!");
        }
    }

    /**
     * @throws SQLException
     * @lambda lambdas used to filter the start date time of the appointments which is needed to find out whether
     * the appointment should be placed into the weekly, monthly, or all list to be displayed
     * <p>
     * sets the appointments table with appointments filtered by the length until their start date
     * it does this by getting the name of the tab and if its weekly, then it will filter by all the appointments
     * that week, same for monthly, and if it's neither all the appointments will be displayed
     */
    public void setTableFilteredAppointments() throws SQLException {
        Set<TableView<Appointments>> appointmentTablesSet = new HashSet<>();
        appointmentTablesSet.add(aptWeeklyTableView);
        appointmentTablesSet.add(aptMonthlyTableView);
        appointmentTablesSet.add(aptAllTableView);
        appointmentTablesSet.forEach(t -> {
            if (t != null) {
                t.refresh();
            }
        });
        appointmentsList.setAll(ad.getAllFromDB());

        appointmentsList.forEach(appointment -> {
            ZonedDateTime estZoneStartTime = ZonedDateTime.of(appointment.getStartDateTime(), ZoneId.of("America/New_York"));
            ZonedDateTime estZoneEndTime = ZonedDateTime.of(appointment.getEndDateTime(), ZoneId.of("America/New_York"));
            ZonedDateTime userZoneStartTime = estZoneStartTime.withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime userZoneEndTime = estZoneEndTime.withZoneSameInstant(ZoneId.systemDefault());
            LocalDateTime userLocalStartTime = userZoneStartTime.toLocalDateTime();
            LocalDateTime userLocalEndTime = userZoneEndTime.toLocalDateTime();
            appointment.setStartDateTime(userLocalStartTime);
            appointment.setEndDateTime(userLocalEndTime);
        });
        if (appointmentsWeeklyTab.isSelected() && Objects.equals(appointmentsWeeklyTab.getText(), "Weekly")) {
            ObservableList<Appointments> weeklyList = appointmentsList.stream().filter(all -> all.getStartDateTime().
                            isAfter(RelatedTime.getCurrentDateTime()) && all.getEndDateTime().
                            isBefore(RelatedTime.getCurrentDateTime().plusWeeks(1)) && all.getEndDateTime().
                            isAfter(all.getStartDateTime())).
                    collect(Collectors.toCollection(FXCollections::observableArrayList));
            aptWeeklyTableView.setItems(weeklyList);
            return;
        }
        if (appointmentsMonthlyTab.isSelected() && Objects.equals(appointmentsMonthlyTab.getText(), "Monthly")) {
            ObservableList<Appointments> monthlyList = appointmentsList.stream().filter(all -> all.getStartDateTime().
                            isAfter(RelatedTime.getCurrentDateTime().plusWeeks(1)) &&
                            all.getStartDateTime().isBefore(RelatedTime.getCurrentDateTime().plusMonths(1))
                            && all.getEndDateTime().isBefore(RelatedTime.getCurrentDateTime().plusMonths(1))
                            && all.getEndDateTime().isAfter(all.getStartDateTime())).
                    collect(Collectors.toCollection(FXCollections::observableArrayList));
            aptMonthlyTableView.setItems(monthlyList);
            return;
        }
        aptAllTableView.setItems(appointmentsList);
    }

    /**
     * Switches the scene to Customer, Appointments, or Reports based off of the value selected in the combobox
     *
     * @param event
     * @throws IOException
     */
    public void switchTablesClicked(ActionEvent event) throws IOException {
        try {
            switch (appointmentsSwitchTableComboBox.getValue()) {
                case "Customers":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/CustomerMain.fxml", Main.getMainStage(), 519, 646, "Customers", false);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Appointments":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/AppointmentsMain.fxml", Main.getMainStage(), 468, 893, "Appointments", false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Reports":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/ReportsMain.fxml", Main.getMainStage(), 558, 791, "Reports", false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        } catch (Exception e) {
            System.out.println("Exception happened in getting combobox value");
            Alerter.informationAlert(String.format("Could not find the table: %s. Please try again later!", appointmentsSwitchTableComboBox.getValue()));
        }
    }

    /**
     * Checks the user location, and translates all the labels to french if french is detected,
     * there is also a manual way to test this, for people who don't have their default language in French
     * but would like to see labels in French
     */
    public void checkUserLocation() throws MalformedURLException {
        if ((System.getProperty("user.language").equals("fr") || Objects.equals(LoginController.bundleGen("Lang").getString("loginText"), "Connexion"))) {
            appointmentsWeeklyTab.setText("Hebdomadaire");
            appointmentsMonthlyTab.setText("Mensuel");
            appointmentsAllTab.setText("Toute");
            appointmentTableLabel.setText("Rendez-vous");
            appointmentsSwitchTableButton.setText("Tableau de commutation");
            appointmentsSwitchTableComboBox.setPromptText("Les tables");

            aptWeekTitle.setText("Titre");
            aptWeekDescription.setText("La description");
            aptWeekLocation.setText("Emplacement");
            aptWeekType.setText("Taper");
            aptWeekStartDate.setText("Date de d??but");
            aptWeekEndDate.setText("Date de fin");
            aptWeekCustomerID.setText("Identifiant du client");
            aptWeekUserID.setText("Identifiant d'utilisateur");
            aptWeekContactID.setText("Identifiant de contact");
            wkAddAppointmentButton.setText("Ajouter");
            wkUpdateAppointmentButton.setText("Mettre ?? jour");
            wkDeleteAppointmentButton.setText("Effacer");

            aptMonthTitle.setText("Titre");
            aptMonthDescription.setText("La description");
            aptMonthLocation.setText("Emplacement");
            aptMonthType.setText("Taper");
            aptMonthStartDate.setText("Date de d??but");
            aptMonthEndDate.setText("Date de fin");
            aptMonthCustomerID.setText("Identifiant du client");
            aptMonthUserID.setText("Identifiant d'utilisateur");
            aptMonthContactID.setText("Identifiant de contact");
            mnthAddAppointmentButton.setText("Ajouter");
            mnthUpdateAppointmentButton.setText("Mettre ?? jour");
            mnthDeleteAppointmentButton.setText("Effacer");

            aptAllTitle.setText("Titre");
            aptAllDescription.setText("La description");
            aptAllLocation.setText("Emplacement");
            aptAllType.setText("Taper");
            aptAllStartDate.setText("Date de d??but");
            aptAllEndDate.setText("Date de fin");
            aptAllCustomerID.setText("Identifiant du client");
            aptAllUserID.setText("Identifiant d'utilisateur");
            aptAllContactID.setText("Identifiant de contact");
            allAddAppointmentButton.setText("Ajouter");
            allUpdateAppointmentButton.setText("Mettre ?? jour");
            allDeleteAppointmentButton.setText("Effacer");

            appointmentsSwitchTableButton.setPrefWidth(195);
            appointmentsSwitchTableButton.setLayoutX(675);
            appointmentsSwitchTableComboBox.setLayoutX(525);
        }
    }

    /**
     * Initializes the columns to take values of the object type that is set to the table, each
     * column in the table correlates to a variable of the object that is set to the tableview
     */
    public void setColumns() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        aptWeekID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        aptWeekTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        aptWeekDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        aptWeekLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        aptWeekType.setCellValueFactory(new PropertyValueFactory<>("type"));
        aptWeekStartDate.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        aptWeekEndDate.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        aptWeekCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        aptWeekUserID.setCellValueFactory(new PropertyValueFactory<>("usersID"));
        aptWeekContactID.setCellValueFactory(new PropertyValueFactory<>("contactsID"));
        aptWeekStartDate.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointments, LocalDateTime> call(TableColumn<Appointments, LocalDateTime> weekStartDate) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean blank) {
                        super.updateItem(item, blank);
                        if (blank) {
                            setText(null);
                        } else {
                            setText(item.atZone(ZoneId.systemDefault()).format(dateFormat));
                        }
                    }
                };
            }
        });
        aptWeekEndDate.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointments, LocalDateTime> call(TableColumn<Appointments, LocalDateTime> weekEndDate) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean blank) {
                        super.updateItem(item, blank);
                        if (blank) {
                            setText(null);
                        } else {
                            setText(item.atZone(ZoneId.systemDefault()).format(dateFormat));
                        }
                    }
                };
            }
        });

        aptMonthID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        aptMonthTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        aptMonthDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        aptMonthLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        aptMonthType.setCellValueFactory(new PropertyValueFactory<>("type"));
        aptMonthStartDate.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        aptMonthEndDate.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        aptMonthCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        aptMonthUserID.setCellValueFactory(new PropertyValueFactory<>("usersID"));
        aptMonthContactID.setCellValueFactory(new PropertyValueFactory<>("contactsID"));
        aptMonthStartDate.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointments, LocalDateTime> call(TableColumn<Appointments, LocalDateTime> monthStartDate) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean blank) {
                        super.updateItem(item, blank);
                        if (blank) {
                            setText(null);
                        } else {
                            setText(item.atZone(ZoneId.systemDefault()).format(dateFormat));
                        }
                    }
                };
            }
        });
        aptMonthEndDate.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointments, LocalDateTime> call(TableColumn<Appointments, LocalDateTime> setStartDateAll) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean blank) {
                        super.updateItem(item, blank);
                        if (blank) {
                            setText(null);
                        } else {
                            setText(item.atZone(ZoneId.systemDefault()).format(dateFormat));
                        }
                    }
                };
            }
        });

        aptAllID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        aptAllTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        aptAllDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        aptAllLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        aptAllType.setCellValueFactory(new PropertyValueFactory<>("type"));
        aptAllStartDate.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        aptAllEndDate.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        aptAllCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        aptAllUserID.setCellValueFactory(new PropertyValueFactory<>("usersID"));
        aptAllContactID.setCellValueFactory(new PropertyValueFactory<>("contactsID"));
        aptAllStartDate.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointments, LocalDateTime> call(TableColumn<Appointments, LocalDateTime> setStartDateAll) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean blank) {
                        super.updateItem(item, blank);
                        if (blank) {
                            setText(null);
                        } else {
                            setText(item.atZone(ZoneId.systemDefault()).format(dateFormat));
                        }
                    }
                };
            }
        });
        aptAllEndDate.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointments, LocalDateTime> call(TableColumn<Appointments, LocalDateTime> setEndDateAll) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean blank) {
                        super.updateItem(item, blank);
                        if (blank) {
                            setText(null);
                        } else {
                            setText(item.atZone(ZoneId.systemDefault()).format(dateFormat));
                        }
                    }
                };
            }
        });
    }

    /**
     * Checks if the user has an appointment within 15 minutes of logging in
     *
     * @throws IOException
     */
    public void checkForUpcomingUserAppointment() throws IOException {
        Users loggedInUser = LoginController.getCurrentlyLoggedInUser();
        LocalDateTime userTime = LocalDateTime.now(RelatedTime.getUserTimeZone());
        ZonedDateTime estTime = ZonedDateTime.of(userTime, ZoneId.of("America/New_York"));

        if (userIDToAppointment.containsKey(loggedInUser.getUser_ID())) {
            Appointments appointment = userIDToAppointment.get(loggedInUser.getUser_ID());
            ZonedDateTime userTimeOfAppointment = ZonedDateTime.of(appointment.getStartDateTime(), RelatedTime.getUserTimeZone());
            LocalTime userAppointmentTimeLocal = userTimeOfAppointment.toLocalTime();
            if (appointment.getStartDateTime().isAfter(estTime.toLocalDateTime()) &&
                    appointment.getStartDateTime().isBefore(estTime.toLocalDateTime().plusMinutes(15))) {
                Main.playSound("src/main/resources/notification.wav");
                Alerter.informationAlert(String.format("Appointment ID: %d\nDate: %s\n\nYou have an upcoming appointment at %s",
                        appointment.getAppointmentID(), appointment.getStartDateTime().toLocalDate(), userAppointmentTimeLocal));
            } else {
                Alerter.informationAlert("No upcoming appointments!");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextLabel();
        try {
            setTableFilteredAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            AppointmentFormController.fillUserAppointmentMap();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            checkForUpcomingUserAppointment();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<String> tableComboList = FXCollections.observableArrayList();
        tableComboList.add("Appointments");
        tableComboList.add("Customers");
        tableComboList.add("Reports");
        try {
            checkUserLocation();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        setColumns();
        appointmentsSwitchTableComboBox.setItems(tableComboList);
    }
}

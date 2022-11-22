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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AppointmentMainController implements Initializable {

    public static Map<Integer, Appointments> customerIDToAppointment = new HashMap<>();
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

    public int getIndexOfAppointment(Appointments appointment) {
        return appointmentsList.indexOf(appointment);
    }

    public void setSelectedAppointment(MouseEvent event) {
        if (appointmentsAllTab.isSelected()) {
            selectedAppointment = aptAllTableView.getSelectionModel().getSelectedItem();
        } else if (appointmentsWeeklyTab.isSelected()) {
            selectedAppointment = aptWeeklyTableView.getSelectionModel().getSelectedItem();
        } else if (appointmentsMonthlyTab.isSelected()) {
            selectedAppointment = aptMonthlyTableView.getSelectionModel().getSelectedItem();
        }
    }

    public void deleteAppointment(ActionEvent event) throws SQLException, IOException {
        if (selectedAppointment != null) {
            ad.deleteFromDB(selectedAppointment);
            appointmentsList.remove(selectedAppointment);
            Main.playSound("src/main/resources/errorSound.wav");
        } else {
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
            AppointmentFormController formController = fxL.getController();
            Consumer<Appointments> addAppointment = appointment -> {
                appointment.setCreatedBy(currentUser.getUsername());
                appointment.setLastUpdatedBy(currentUser.getUsername());
                appointment.setUsersID(currentUser.getUser_ID());
                try {
                    String s = ad.dbInsert(appointment);
                    System.out.println(s);
                    if (!Objects.equals(s, "")) {
                        appointmentsList.add(appointment);
                    }
                } catch (SQLException s) {
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
                formController.appointmentInit(contactsObservableList, selectedAppointment, addAppointment, customerNameSupplier, customersObservableList);
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
                    } catch (SQLException throwables) {
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
                }, customersObservableList);
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

    //if the appointment is within the next seven days
    public void setTableFilteredAppointments() throws SQLException {
        appointmentsList.setAll(ad.getAllFromDB());

        if (appointmentsWeeklyTab.isSelected() && Objects.equals(appointmentsWeeklyTab.getText(), "Weekly")) {
            ObservableList<Appointments> weeklyList = appointmentsList.stream().filter(all -> all.getStartDateTime().toLocalDateTime().
                            isAfter(RelatedTime.getCurrentDateTime()) && all.getEndDateTime().toLocalDateTime().
                            isBefore(RelatedTime.getCurrentDateTime().plusWeeks(1)) && all.getEndDateTime().
                            toLocalDateTime().isAfter(all.getStartDateTime().toLocalDateTime())).
                    collect(Collectors.toCollection(FXCollections::observableArrayList));
            aptWeeklyTableView.setItems(weeklyList);
            return;
        }
        if (appointmentsMonthlyTab.isSelected() && Objects.equals(appointmentsMonthlyTab.getText(), "Monthly")) {
            ObservableList<Appointments> monthlyList = appointmentsList.stream().filter(all -> all.getStartDateTime().toLocalDateTime().
                            isAfter(RelatedTime.getCurrentDateTime().plusWeeks(1)) &&
                            all.getStartDateTime().toLocalDateTime().isBefore(RelatedTime.getCurrentDateTime().plusMonths(1))
                            && all.getEndDateTime().toLocalDateTime().isBefore(RelatedTime.getCurrentDateTime().plusMonths(1))
                            && all.getEndDateTime().toLocalDateTime().isAfter(all.getStartDateTime().toLocalDateTime())).
                    collect(Collectors.toCollection(FXCollections::observableArrayList));
            aptMonthlyTableView.setItems(monthlyList);
            return;
        }
        aptAllTableView.setItems(appointmentsList);
    }

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

    public void checkUserLocation() {
        if ((System.getProperty("user.language").equals("fr") || LoginController.changeLang)) {
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
            aptWeekStartDate.setText("Date de début");
            aptWeekEndDate.setText("Date de fin");
            aptWeekCustomerID.setText("Identifiant du client");
            aptWeekUserID.setText("Identifiant d'utilisateur");
            aptWeekContactID.setText("Identifiant de contact");
            wkAddAppointmentButton.setText("Ajouter");
            wkUpdateAppointmentButton.setText("Mettre à jour");
            wkDeleteAppointmentButton.setText("Effacer");

            aptMonthTitle.setText("Titre");
            aptMonthDescription.setText("La description");
            aptMonthLocation.setText("Emplacement");
            aptMonthType.setText("Taper");
            aptMonthStartDate.setText("Date de début");
            aptMonthEndDate.setText("Date de fin");
            aptMonthCustomerID.setText("Identifiant du client");
            aptMonthUserID.setText("Identifiant d'utilisateur");
            aptMonthContactID.setText("Identifiant de contact");
            mnthAddAppointmentButton.setText("Ajouter");
            mnthUpdateAppointmentButton.setText("Mettre à jour");
            mnthDeleteAppointmentButton.setText("Effacer");

            aptAllTitle.setText("Titre");
            aptAllDescription.setText("La description");
            aptAllLocation.setText("Emplacement");
            aptAllType.setText("Taper");
            aptAllStartDate.setText("Date de début");
            aptAllEndDate.setText("Date de fin");
            aptAllCustomerID.setText("Identifiant du client");
            aptAllUserID.setText("Identifiant d'utilisateur");
            aptAllContactID.setText("Identifiant de contact");
            allAddAppointmentButton.setText("Ajouter");
            allUpdateAppointmentButton.setText("Mettre à jour");
            allDeleteAppointmentButton.setText("Effacer");

            appointmentsSwitchTableButton.setPrefWidth(195);
            appointmentsSwitchTableButton.setLayoutX(675);
            appointmentsSwitchTableComboBox.setLayoutX(525);
        }
    }

    public void setColumns() {
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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setTableFilteredAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<String> tableComboList = FXCollections.observableArrayList();

        tableComboList.add("Appointments");
        tableComboList.add("Customers");
        tableComboList.add("Reports");

        checkUserLocation();
        setColumns();
        appointmentsSwitchTableComboBox.setItems(tableComboList);
    }
}

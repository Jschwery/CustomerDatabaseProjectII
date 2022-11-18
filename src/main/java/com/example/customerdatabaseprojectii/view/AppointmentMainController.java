package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.daos.CustomersDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AppointmentMainController {

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
    static Users currentUser = LoginController.getCurrentlyLoggedInUser();
    ObservableList<Appointments> appointmentsList = FXCollections.observableArrayList();
    public static boolean appointmentAddedSuccess = false;
    public static Map<Integer, Appointments> customerIDToAppointment = new HashMap<>();
    ObservableList<Customers> customersObservableList = cd.getAllFromDB();

    protected static Appointments selectedAppointment = null;

    public AppointmentMainController() throws SQLException {}

    public void setSelectedAppointment(MouseEvent event){
        if(appointmentsAllTab.isSelected()){
            selectedAppointment = aptAllTableView.getSelectionModel().getSelectedItem();
        }else if(appointmentsWeeklyTab.isSelected()){
            selectedAppointment = aptWeeklyTableView.getSelectionModel().getSelectedItem();
        }else if(appointmentsMonthlyTab.isSelected()){
            selectedAppointment = aptMonthlyTableView.getSelectionModel().getSelectedItem();
        }
        System.out.println("Selected appointment: " + selectedAppointment);

    }

    public void deleteAppointment(ActionEvent event) throws SQLException {
        if(selectedAppointment != null){
            ad.deleteFromDB(selectedAppointment);
            appointmentsList.remove(selectedAppointment);
        }else {
            Alerter.informationAlert("No appointment selected to delete");
        }
    }

    /**
     *
     * @param event when the add appointment button is clicked the scenes will switch and
     *              the formInit method will be called with the parameters set
     * @throws IOException
     */
    public void addAppointmentForm(ActionEvent event) throws IOException{
        if (Objects.equals(selectedAppointment, null)) {

            AppointmentFormController.isModified = false;
            FXMLLoader fxL = new FXMLLoader();
            fxL.setLocation(new URL("src/main/java/com/example/customerdatabaseprojectii/view/AppointmentFormController.java"));
            Parent node = fxL.load();

            AppointmentFormController formController = fxL.getController();
            Consumer<Appointments> addAppointment = appointment -> {
                appointment.setCreatedBy(currentUser.getUsername());
                appointment.setLastUpdatedBy(currentUser.getUsername());
                appointment.setUsersID(currentUser.getUser_ID());
                try {String s = ad.dbInsert(appointment);
                    System.out.println(s);} catch (SQLException e) {e.printStackTrace();}
                if(appointmentAddedSuccess){
                    appointmentsList.add(appointment);
                }
            };
            try {
                formController.appointmentInit(customersObservableList, selectedAppointment, addAppointment);
            }catch (SQLException s){
                s.printStackTrace();
            }
            Stage appointmentFormStage = new Stage();
            appointmentFormStage.setScene(new Scene(node));
            appointmentFormStage.setResizable(false);
            appointmentFormStage.initStyle(StageStyle.DECORATED);

            appointmentFormStage.show();
        }
        else {
            Alerter.warningAlert("You are trying to add an appointment that already exists, unselecting appointment");
            selectedAppointment = null;
        }
    }

    public int getIndexOfAppointment(Appointments appointment){
        return appointmentsList.indexOf(appointment);
    }


    public void updateAppointmentForm(ActionEvent event) throws IOException{
        if (selectedAppointment != null) {
            FXMLLoader fxL = new FXMLLoader();
            fxL.setLocation(new URL("src/main/java/com/example/customerdatabaseprojectii/view/AppointmentForm.fxml"));
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
                afc.appointmentInit(customersObservableList, selectedAppointment, updateAppointment);
            } catch (SQLException e) {
                e.printStackTrace();
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
    public void setTableFilteredAppointments(){
        if(appointmentsWeeklyTab.isSelected() && Objects.equals(appointmentsAllTab.getText(), "Weekly")){
            ObservableList<Appointments> weeklyList = appointmentsList.stream().filter(all-> all.getStartDateTime().toLocalDateTime().isAfter(RelatedTime.getCurrentDateTime()) &&
                    all.getEndDateTime().toLocalDateTime().isBefore(RelatedTime.getCurrentDateTime().plusWeeks(1)) && all.getEndDateTime().toLocalDateTime().isAfter(all.getStartDateTime().toLocalDateTime())).collect(Collectors.toCollection(FXCollections::observableArrayList));
                    aptWeeklyTableView.setItems(weeklyList);
                    return;
        }if(appointmentsMonthlyTab.isSelected() && Objects.equals(appointmentsMonthlyTab.getText(), "Monthly")){
            ObservableList<Appointments> monthlyList = appointmentsList.stream().filter(all -> all.getStartDateTime().toLocalDateTime().isAfter(RelatedTime.getCurrentDateTime().plusWeeks(1)) &&
                    all.getStartDateTime().toLocalDateTime().isBefore(RelatedTime.getCurrentDateTime().plusMonths(1)) && all.getEndDateTime().toLocalDateTime().isBefore(RelatedTime.getCurrentDateTime().plusMonths(1)) &&
                    all.getEndDateTime().toLocalDateTime().isAfter(all.getStartDateTime().toLocalDateTime())).collect(Collectors.toCollection(FXCollections::observableArrayList));
                    aptMonthlyTableView.setItems(monthlyList);
                    return;
        }
        if(appointmentsAllTab.isSelected()){
            aptAllTableView.setItems(appointmentsList);
        }
    }

    public void switchTablesClicked(ActionEvent event) throws IOException {
        try {
            switch (appointmentsSwitchTableComboBox.getValue()) {
                case "Customers":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/CustomerMain.fxml", Main.getMainStage(), 750, 750, "Customers", false);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Appointments":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/AppointmentsMain.fxml", Main.getMainStage(), 750, 750, "Appointments", false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Reports":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/ReportsMain.fxml", Main.getMainStage(), 750, 750, "Reports", false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        } catch (Exception e) {
            System.out.println("Exception happened in getting combobox value");
            Alerter.informationAlert(String.format("Could not find the table: %s. Please try again later!", appointmentsSwitchTableComboBox.getValue()));
        }
    }

    public void checkUserLocation(){
        if((System.getProperty("user.language").equals("fr") || LoginController.changeLang)){
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
        }
    }
    public void setColumns(){
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

    @FXML
    public void initialize() {
        setTableFilteredAppointments();
        ObservableList<String> tableComboList = FXCollections.observableArrayList();

        tableComboList.add("Appointments");
        tableComboList.add("Customers");
        tableComboList.add("Reports");

        checkUserLocation();
        setColumns();
        appointmentsSwitchTableComboBox.setItems(tableComboList);

    }
}

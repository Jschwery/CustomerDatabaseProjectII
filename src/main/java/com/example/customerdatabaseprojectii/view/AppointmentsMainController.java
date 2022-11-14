package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppointmentsMainController implements Initializable{

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

    //use query to get this
    //get appointments that are within the next week
    //get appointments that are within the next month



    public void customerButtonClicked(ActionEvent event) {

    }

    protected static Appointments selectedAppointment = null;


    public void setSelectedAppointment(MouseEvent event){
        if(appointmentsAllTab.isSelected()){
            selectedAppointment = aptAllTableView.getSelectionModel().getSelectedItem();
        }else if(appointmentsWeeklyTab.isSelected()){
            selectedAppointment = aptWeeklyTableView.getSelectionModel().getSelectedItem();
        }else if(appointmentsMonthlyTab.isSelected()){
            selectedAppointment = aptMonthlyTableView.getSelectionModel().getSelectedItem();
        }
    }


    public void deleteAppointment(ActionEvent event){
        System.out.println("appointment deleted");
    }

    public void addAppointmentForm(ActionEvent event) throws IOException {
        if (Objects.equals(selectedAppointment, null)) {
            AppointmentFormController.isModified = false;
            Main.genNewStageAndScene("src/main/java/com/example/customerdatabaseprojectii/view/AppointmentForm.fxml", 568, 340, "Appointment Form");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You are trying to add a new appointment, while one is selected!\nClick 'OK' to deselect the customer, or cancel to escape.");
            Optional<ButtonType> buttonResult = alert.showAndWait();

            if (buttonResult.get() == ButtonType.OK) {
                selectedAppointment = null;
            } else {
                if (buttonResult.get() == ButtonType.CANCEL) {
                    System.out.println("Appointment is still selected");
                }
            }
        }
    }
    public void updateAppointmentForm(ActionEvent event) throws IOException {
        if(selectedAppointment!= null){
            AppointmentFormController.isModified = true;
            Main.genNewStageAndScene("src/main/java/com/example/customerdatabaseprojectii/view/AppointmentForm.fxml", 568, 340, "Customer Form");
        }
        else{
            Alerter.informationAlert("Please select an appointment to modify!");
        }
    }

    public void switchTablesClicked(ActionEvent event) throws IOException {
        try {
            switch (appointmentsSwitchTableComboBox.getValue()) {
                case "Customers":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/CustomerMain.fxml", Main.getMainStage(), 750, 750, "Customers");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Appointments":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/AppointmentsMain.fxml", Main.getMainStage(), 750, 750, "Appointments");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Reports":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/ReportsMain.fxml", Main.getMainStage(), 750, 750, "Reports");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        } catch (Exception e) {
            System.out.println("Exception happened in getting combobox value");
            Alerter.informationAlert(String.format("Could not find the table: %s. Please try again later!", appointmentsSwitchTableComboBox.getValue()));
        }
    }

    static void setItems(){

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> tableComboList = FXCollections.observableArrayList();
        tableComboList.add("Appointments");
        tableComboList.add("Customers");
        tableComboList.add("Reports");

        appointmentsSwitchTableComboBox.setItems(tableComboList);

        /*Lambda used to filter all the appointments in the observable list that are within the next 7 days, then collect
        * them back into a collection via a method reference and this returns an 'observableArrayList' */
        aptWeeklyTableView.setItems(AppointmentsDao.getObservableAppointments().stream().filter(apt -> {
            LocalDateTime ldt = RelatedTime.getCurrentDateTime();
            return ldt.until(apt.getStartDateTime(), ChronoUnit.DAYS) <= 7;
        }).collect(Collectors.toCollection(FXCollections::observableArrayList)));

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


        aptMonthlyTableView.setItems(AppointmentsDao.getObservableAppointments().stream().filter(apt -> {
            LocalDateTime ldt = RelatedTime.getCurrentDateTime();
            return ldt.until(apt.getStartDateTime(), ChronoUnit.MONTHS) <= 1 && ldt.until(apt.getStartDateTime(), ChronoUnit.DAYS) > 7;
        }).collect(Collectors.toCollection(FXCollections::observableArrayList)));

        aptMonthlyTableView.setItems(AppointmentsDao.getObservableAppointments());
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


        aptAllTableView.setItems(AppointmentsDao.getObservableAppointments());
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



}

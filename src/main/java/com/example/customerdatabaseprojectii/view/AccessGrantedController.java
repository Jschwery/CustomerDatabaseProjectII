package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Customers;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class AccessGrantedController implements Initializable {

    @FXML
    TableView<Appointments> accessTableView;
    @FXML
    TableColumn<Appointments, Integer> accessApptID;
    @FXML
    TableColumn<Appointments, String> accessTitle;
    @FXML
    TableColumn<Appointments,String > accessDesc;
    @FXML
    TableColumn<Appointments, String> accessType;
    @FXML
    TableColumn<Appointments, Timestamp> accessStart;
    @FXML
    TableColumn<Appointments, Timestamp> accessEnd;
    @FXML
    TableColumn<Appointments, Time> accessCreateDate;
    @FXML
    TableColumn<Appointments, String> accessCreateBy;
    @FXML
    TableColumn<Appointments, Timestamp> accessLastUpdate;
    @FXML
    TableColumn<Appointments, String> accessLastUpdateBy;
    @FXML
    TableColumn<Appointments, Integer> accessCustID;
    @FXML
    TableColumn<Appointments, Integer> accessUserID;
    @FXML
    TableColumn<Appointments, Integer> accessContactID;
    @FXML
    TableColumn<Appointments,String> accessLoc;
    @FXML
    Button customerFormButton;
    @FXML
    Label tableVarText;
    @FXML
    Label addCustomerLabel;
    @FXML
    TableView<Customers> customerTableView;



    public void customerButtonClicked(ActionEvent event) {
    accessTableView.setVisible(false);
    customerFormButton.setVisible(true);

    tableVarText.setText("Customer");
    addCustomerLabel.setVisible(true);
    customerTableView.setVisible(true);
    }


    public void addCustomerForm(ActionEvent event) throws IOException {
    Main.genNewStageAndScene("src/main/java/com/example/customerdatabaseprojectii/view/CustomerForm.fxml", 568, 340, "Customer Form");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            accessTableView.setItems(AppointmentsDao.generateAppointmentList());

        } catch (SQLException e) {
            e.printStackTrace();
        }


        accessDesc.setCellValueFactory(new PropertyValueFactory<>("description"));


          accessType.setCellValueFactory(new PropertyValueFactory<>("type"));
        accessApptID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        accessLoc.setCellValueFactory(new PropertyValueFactory<>("location"));
        accessStart.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        accessEnd.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        accessCreateDate.setCellValueFactory(new PropertyValueFactory<>("createDateTime"));
        accessCreateBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        accessLastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        accessLastUpdateBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
        accessCustID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        accessUserID.setCellValueFactory(new PropertyValueFactory<>("usersID"));
        accessContactID.setCellValueFactory(new PropertyValueFactory<>("contactsID"));


    }
}

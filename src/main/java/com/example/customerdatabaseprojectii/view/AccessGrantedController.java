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
    TableColumn<Appointments, Integer> appointmentID;
    @FXML
    TableColumn<Appointments, String> title;
    @FXML
    TableColumn<Appointments,String > description;
    @FXML
    TableColumn<Appointments, String> location;
    @FXML
    TableColumn<Appointments, String> type;
    @FXML
    TableColumn<Appointments, Timestamp> startDateTime;
    @FXML
    TableColumn<Appointments, Timestamp> endDateTime;
    @FXML
    TableColumn<Appointments, Time> createDateTime;
    @FXML
    TableColumn<Appointments, String> createdBy;
    @FXML
    TableColumn<Appointments, Timestamp> lastUpdate;
    @FXML
    TableColumn<Appointments, String> lastUpdatedBy;
    @FXML
    TableColumn<Appointments, Integer> customerID;
    @FXML
    TableColumn<Appointments, Integer> usersID;
    @FXML
    TableColumn<Appointments, Integer> contactsID;
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
            ObservableList<Appointments> ls = accessTableView.getItems();
            System.out.println(ls);


        } catch (SQLException e) {
            e.printStackTrace();
        }


//        description.setCellValueFactory(new PropertyValueFactory<>("description"));


//          type.setCellValueFactory(new PropertyValueFactory<>("type"));
//        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
//        location.setCellValueFactory(new PropertyValueFactory<>("location"));
//        startDateTime.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
//        endDateTime.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
//        createDateTime.setCellValueFactory(new PropertyValueFactory<>("createDateTime"));
//        createdBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
//        lastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
//        lastUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
//        customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
//        usersID.setCellValueFactory(new PropertyValueFactory<>("usersID"));
//        contactsID.setCellValueFactory(new PropertyValueFactory<>("contactsID"));


    }
}

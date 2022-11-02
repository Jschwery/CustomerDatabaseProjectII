package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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


    AppointmentsDao aptDao = new AppointmentsDao();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            accessTableView.setItems(aptDao.generateAppointmentList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        startDateTime.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        endDateTime.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        createDateTime.setCellValueFactory(new PropertyValueFactory<>("createDateTime"));
        createdBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        lastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        lastUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
        customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        usersID.setCellValueFactory(new PropertyValueFactory<>("usersID"));
        contactsID.setCellValueFactory(new PropertyValueFactory<>("contactsID"));


    }
}

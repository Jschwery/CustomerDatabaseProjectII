
package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.daos.ContactsDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.util.Alerter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReportsMainController implements Initializable {
    @FXML
    Label reportsTableLabel;
    @FXML
    ComboBox<String> reportsTableSwitchComboBox;
    @FXML
    Button reportsSwitchTableButton;
    @FXML
    TextField reportSearch;
    @FXML
    ComboBox<String> switchCustomerComboBox;
    @FXML
    TableView<Appointments> reportTableView;
    @FXML
    TableColumn<Appointments, Integer>
            reportID;
    @FXML
    TableColumn<Appointments, String> reportTitle;
    @FXML
    TableColumn<Appointments, String> reportType;
    @FXML
    TableColumn<Appointments, String> reportDescription;
    @FXML
    TableColumn<Appointments, LocalDateTime> reportStart;
    @FXML
    TableColumn<Appointments, LocalDateTime> reportEnd;
    @FXML
    TableColumn<Appointments, Integer> reportCustomerID;
    @FXML
    Slider reportSlider;
    @FXML
    BarChart<String, Integer> reportGraph;

    AppointmentsDao ad = new AppointmentsDao();
    ContactsDao cd = new ContactsDao();
    public ReportsMainController() throws SQLException {}

    public Optional<Appointments> findAppointmentByID(Integer contactID) throws SQLException {
        return ad.getAllFromDB().stream().filter(appointment -> Objects.equals(appointment.getContactsID(), contactID)).findFirst();
    }
    public Integer getContactIDByContactName(String contactName) throws SQLException {
        Optional<Contacts> contactsTemp = cd.getAllFromDB().stream().filter(contact -> Objects.equals(contact.getContactName().
                toUpperCase(), contactName.toUpperCase())).findFirst();
        return contactsTemp.map(Contacts::getContactID).orElse(0);
    }

    /**
     * Adds all the appointments from a specific contact selected from the combobox to an observable list
     * filters by using a stream that scans through the contacts database by name and returns the contact ID, then
     * checks if the appointments database has an appointment coorelated to the ID of the contact selected and if
     * so adds the appointment to the table
     *
     * @throws SQLException
     */
    public void contactSwitched() throws SQLException {
        ObservableList<Appointments> appointmentsFilteredByContacts = FXCollections.observableArrayList();
        appointmentsFilteredByContacts = ad.getAllFromDB().stream().filter(appointment -> {
            try {return Objects.equals(appointment.getContactsID(),
                        getContactIDByContactName(switchCustomerComboBox.getValue()));
            } catch (SQLException e) {e.printStackTrace();}
            return false;
        }).collect(Collectors.toCollection(FXCollections::observableArrayList));
        reportTableView.setItems(appointmentsFilteredByContacts);
    }

    public void switchTablesClicked(ActionEvent event) throws IOException {
        try {
            switch (reportsTableSwitchComboBox.getValue()) {
                case "Customers":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/CustomerMain.fxml",
                                Main.getMainStage(), 519, 646, "Customers", false);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Appointments":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/AppointmentsMain.fxml",
                                Main.getMainStage(), 468, 893, "Appointments", false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Reports":
                    try {
                        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/ReportsMain.fxml",
                                Main.getMainStage(), 558, 791, "Reports", false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        } catch (Exception e) {
            System.out.println("Exception happened in getting combobox value");
            Alerter.informationAlert(String.format("Could not find the table: %s. Please try again later!",
                    reportsTableSwitchComboBox.getValue()));
        }
    }


    //get the contacts from the appointments list
    //then go through the contacts list and match the contactID
    //then place inside the combobox the name of the contact
    public void fillContactCombobox() throws SQLException {
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        for(Appointments appointments : ad.getAllFromDB()){
            for(Contacts contact : cd.getAllFromDB()){
                if(Objects.equals(appointments.getContactsID(), contact.getContactID())){
                    contactNames.add(contact.getContactName());
                }
            }
        }
        switchCustomerComboBox.setItems(contactNames);
    }

    public void initializeReportTable() throws SQLException {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        reportTableView.setItems(ad.getAllFromDB());
        reportID.setCellValueFactory(new PropertyValueFactory<>("contactsID"));
        reportTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        reportDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        reportType.setCellValueFactory(new PropertyValueFactory<>("type"));
        reportCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        reportStart.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        reportEnd.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        reportStart.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointments, LocalDateTime> call(TableColumn<Appointments, LocalDateTime> setStartDateTime) {
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
        reportEnd.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointments, LocalDateTime> call(TableColumn<Appointments, LocalDateTime> setEndDateTime) {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fillContactCombobox();
            initializeReportTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObservableList<String> tableComboList = FXCollections.observableArrayList();
        tableComboList.add("Appointments");
        tableComboList.add("Customers");
        tableComboList.add("Reports");

        reportsTableSwitchComboBox.setItems(tableComboList);
    }
}


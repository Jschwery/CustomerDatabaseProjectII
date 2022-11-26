
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
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
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
    TableView<Appointments> reportTableView1;
    @FXML
    TableColumn<Appointments, String> reportType1;
    @FXML
    TableColumn<Appointments, LocalDateTime> reportMonth1;
    @FXML
    TextField reportSearchFilter;
    @FXML
    BarChart<String, Integer> reportGraph2;
    @FXML
    Pane selectFilterPane;
    @FXML
    AnchorPane filterPopupAnchorPane;
    @FXML
    Label varReportLabel;

    //get all the users log ins
    //user log in within 3 days of appointment 5 days of appointment 7 days of appointment and one month of appointment

    public void scanLoginAndGenAverage(){
        Pattern namePattern = Pattern.compile("[]")
    }


    public void setGraphTab(){
        XYChart.Series threeDaysSeries = new XYChart.Series();
        threeDaysSeries.setName("3 Days");
        threeDaysSeries.getData().add("h");

        XYChart.Series fiveDaysSeries = new XYChart.Series();



        XYChart.Series sevenDaysSeries = new XYChart.Series();


        XYChart.Series oneMonthSeries = new XYChart.Series();
    }


    AppointmentsDao ad = new AppointmentsDao();
    ContactsDao cd = new ContactsDao();
    private static Appointments appointmentSelected = null;
    public ReportsMainController() throws SQLException {}

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

    public void setSelectedFilteredAppointment(MouseEvent event){
        appointmentSelected = reportTableView1.getSelectionModel().getSelectedItem();
    }

    public void setFilterByType(ActionEvent event) throws SQLException {
        if(!Objects.equals(appointmentSelected, null)){
                try {//if the selected appointment matches a type in the database then add that to an observable list and set the table
                    Integer num = Math.toIntExact(ad.getAllFromDB().stream().filter(appointment -> Objects.equals(appointment.getType().toUpperCase(),
                            appointmentSelected.getType().toUpperCase())).count());
                    varReportLabel.setVisible(true);
                    varReportLabel.setText(String.format("%d appointments found with the type: %s", num, appointmentSelected.getType()));
                } catch (SQLException e) {e.printStackTrace();}
        }
        filterPopupAnchorPane.setVisible(false);
        selectFilterPane.setVisible(false);
    }
    public void setFilterByMonth(ActionEvent event){
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
        try {//if the selected appointment matches a type in the database then add that to an observable list and set the table
           Integer num = Math.toIntExact(ad.getAllFromDB().stream().filter(appointment -> Objects.
                   equals(appointment.getStartDateTime().toLocalDate().format(monthFormat),
                           appointmentSelected.getStartDateTime().toLocalDate().format(monthFormat))).count());
            varReportLabel.setVisible(true);
            varReportLabel.setText(String.format("%d appointments found with month: %s", num,
                    appointmentSelected.getStartDateTime().toLocalDate().format(monthFormat)));
        } catch (SQLException e) {e.printStackTrace();}
        filterPopupAnchorPane.setVisible(false);
        selectFilterPane.setVisible(false);
    }
    //when select create popup thats asks if they are looking for total type/month
    public void setFilterLabels(ActionEvent event) throws SQLException {
        if(appointmentSelected != null) {
            varReportLabel.setVisible(false);
            filterPopupAnchorPane.setVisible(true);
            selectFilterPane.setVisible(true);
        }
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
    //all appointments we get the type of each appointment, and
    public void filterSearchbar(KeyEvent event) throws SQLException {
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
        if(event.getCode() == KeyCode.ENTER || Objects.equals(reportSearchFilter.getText(), "")){
            String filterString = reportSearchFilter.getText();
            ObservableList<Appointments> appointmentList = ad.getAllFromDB().stream().filter(appointment -> appointment.getType().
                            toUpperCase().contains(filterString.toUpperCase()) ||
                            appointment.getStartDateTime().toLocalDate().format(monthFormat).contains(filterString)).
                    collect(Collectors.toCollection(FXCollections::observableArrayList));
            if(appointmentList.size() <1){
                reportTableView1.setItems(null);
            }else {
                reportTableView1.setItems(appointmentList);
            }
        }
    }

    public void fillContactCombobox() throws SQLException {
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        Set<String> contactNameSet = new HashSet<>();
        for(Appointments appointments : ad.getAllFromDB()){
            for(Contacts contact : cd.getAllFromDB()){
                if(Objects.equals(appointments.getContactsID(), contact.getContactID())){
                    contactNameSet.add(contact.getContactName());
                }
            }
        }
        contactNames.addAll(contactNameSet);
        switchCustomerComboBox.setItems(contactNames);
    }

    public void initializeReportTable() throws SQLException {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
        reportTableView1.setItems(ad.getAllFromDB());
        reportType1.setCellValueFactory(new PropertyValueFactory<>("type"));
        reportMonth1.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        reportMonth1.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointments, LocalDateTime> call(TableColumn<Appointments, LocalDateTime> setStartDateTime) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean blank) {
                        super.updateItem(item, blank);
                        if (blank) {
                            setText(null);
                        } else {
                            setText(item.atZone(ZoneId.systemDefault()).format(monthFormat));
                        }
                    }
                };
            }
        });

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


package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.daos.ContactsDao;
import com.example.customerdatabaseprojectii.daos.UsersDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Contacts;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.Alerter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReportsMainController implements Initializable {
    private static String mostAnxiousUser;
    private static Appointments appointmentSelected = null;
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
    ComboBox<Integer> comboBoxThirdReport;
    @FXML
    Label thirdReportVarLabel;
    @FXML
    Label varReportLabel;
    UsersDao ud = new UsersDao();
    List<String> userNamesFound = new ArrayList<>();
    List<LocalDateTime> userDateFound = new ArrayList<>();

public ReportsMainController() throws SQLException {}
    Map<Integer, Integer> userIDNumberLoginsWithinMonth = new HashMap<>();
    AppointmentsDao ad = new AppointmentsDao();
    ContactsDao cd = new ContactsDao();

    /**
     *
     * @param userName username of user
     * @return id of the user that matches the username entered
     * @throws SQLException
     */
    public Integer findUserIDByName(String userName) throws SQLException {
        Optional<Users> foundUser = ud.getAllFromDB().stream().filter(name -> Objects.equals(name.getUsername().toUpperCase(), userName.toUpperCase())).findFirst();
        return foundUser.map(Users::getUser_ID).orElse(-1);
    }

    /**
     * @param id id of user
     * @return returns the user from the db which matches the entered id
     * @throws SQLException
     */
    public Optional<Users> findUserByID(int id) throws SQLException {
        return ud.getAllFromDB().stream().filter(user -> Objects.equals(user.getUser_ID(), id)).findFirst();
    }
    /**
     * Report choice #3
     * @param event finds from a list of users who have logged into the application, and when selected
     *              prints a report of how many times that user has logged in from the login_activity.txt log file.
     * @throws SQLException
     */
    public void comboClicked(ActionEvent event) throws SQLException {
        Integer userIDWhoHasAppointment = comboBoxThirdReport.getSelectionModel().getSelectedItem();
        Optional<Integer> appointmentCount = ud.getAllFromDB().stream().
                filter(user -> Objects.equals(user.getUser_ID(), userIDWhoHasAppointment)).map(Users::getUserLogInCount).findFirst();
        if(appointmentCount.isPresent()) {
            thirdReportVarLabel.setVisible(true);
            findUserByID(userIDWhoHasAppointment).ifPresent(selectedUser -> thirdReportVarLabel.
                    setText(String.format("User %s with ID: %d has logged in %d times", selectedUser.getUsername(),
                            selectedUser.getUser_ID(), Users.count.get(selectedUser.getUser_ID()))));
        }
    }

    /**
     * @param userID id to find the user by within the map that maps userId to login times
     * @throws SQLException
     */
    public void userMapIterator(int userID) throws SQLException {
            for (Map.Entry<Integer, Integer> entry : Users.count.entrySet()) {
                if (entry.getKey().equals(userID)) {
                    entry.setValue(entry.getValue() + 1);
                }
            }
        }

    /**
     * Initializes the map of users to their login count
     * and sets them all to zero
     */
    public void initUserCountMap(){
        List<Integer> userIDs = userNamesFound.stream().map(username -> {
            try {
                return findUserIDByName(username);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        userIDs.forEach(uID -> Users.count.put(uID, 0));
        System.out.println(userNamesFound);
        System.out.println(userIDs);
        System.out.println("inited the user map");
    }

    /**
     * uses regex pattern and matcher to find the usernames within the login_activity log file and add
     * the user log in activity to a list to later be counted
     * @throws IOException
     * @throws SQLException
     */
    public void fillNameList() throws IOException, SQLException {
        Pattern namePattern = Pattern.compile("[^\\s]([a-zA-Z])+$|[^\\s]([a-zA-Z]+\\d?$)");
        String loginString;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream
                ("userLogInfo/login_activity.txt")));
        while ((loginString = reader.readLine()) != null) {
            Matcher matcher = namePattern.matcher(loginString);
            while (matcher.find()) {
                userNamesFound.add(matcher.group());
            }
        }
    }

    /**
     * Uses regex pattern to find the username and for each time
     * it comes across the username the count for that user will be incremented by one
     * @throws IOException
     * @throws SQLException
     */
    public void scanLoginLog() throws IOException, SQLException {
        Pattern namePattern = Pattern.compile("[^\\s]([a-zA-Z])+$|[^\\s]([a-zA-Z]+\\d?$)");
        String loginString;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream
                ("userLogInfo/login_activity.txt")));
        while ((loginString = reader.readLine()) != null) {
            Matcher matcher = namePattern.matcher(loginString);
            while (matcher.find()) {
                if(findUserByID(findUserIDByName(matcher.group())).isPresent()){
                    Users user = findUserByID(findUserIDByName(matcher.group())).get();
                    Users.setCount(user, Users.count.get(user.getUser_ID()));
                    userMapIterator(user.getUser_ID());
                }
            }
        }
        reader.close();
    }

    /**
     *
     * @param contactName takes in the contact name whose ID is being searched for
     * @return the id of the contact found with the entered name
     * @throws SQLException
     */
    public Integer getContactIDByContactName(String contactName) throws SQLException {
        Optional<Contacts> contactsTemp = cd.getAllFromDB().stream().
                filter(contact -> Objects.equals(contact.getContactName().
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
            try {
                return Objects.equals(appointment.getContactsID(),
                        getContactIDByContactName(switchCustomerComboBox.getValue()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }).collect(Collectors.toCollection(FXCollections::observableArrayList));
        reportTableView.setItems(appointmentsFilteredByContacts);
    }

    public void setSelectedFilteredAppointment(MouseEvent event) {
        appointmentSelected = reportTableView1.getSelectionModel().getSelectedItem();
    }

    /**
     * filter the response to how many appointments are found by type
     * @param event
     * @throws SQLException
     */
    public void setFilterByType(ActionEvent event) throws SQLException {
        if (!Objects.equals(appointmentSelected, null)) {
            try {//if the selected appointment matches a type in the database then add that to an observable list and set the table
                Integer num = Math.toIntExact(ad.getAllFromDB().stream().filter(appointment -> Objects.equals(appointment.getType().toUpperCase(),
                        appointmentSelected.getType().toUpperCase())).count());
                varReportLabel.setVisible(true);
                varReportLabel.setText(String.format("%d appointments found with the type: %s", num, appointmentSelected.getType()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        filterPopupAnchorPane.setVisible(false);
        selectFilterPane.setVisible(false);
    }
    /**
     * filter the response to how many appointments are found by month
     * @param event
     */
    public void setFilterByMonth(ActionEvent event) {
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
        try {//if the selected appointment matches a type in the database then add that to an observable list and set the table
            Integer num = Math.toIntExact(ad.getAllFromDB().stream().filter(appointment -> Objects.
                    equals(appointment.getStartDateTime().toLocalDate().format(monthFormat),
                            appointmentSelected.getStartDateTime().toLocalDate().format(monthFormat))).count());
            varReportLabel.setVisible(true);
            varReportLabel.setText(String.format("%d appointments found with month: %s", num,
                    appointmentSelected.getStartDateTime().toLocalDate().format(monthFormat)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        filterPopupAnchorPane.setVisible(false);
        selectFilterPane.setVisible(false);
    }
    public void setFilterLabels(ActionEvent event) throws SQLException {
        if (appointmentSelected != null) {
            varReportLabel.setVisible(false);
            filterPopupAnchorPane.setVisible(true);
            selectFilterPane.setVisible(true);
        }
    }
    /**
     * @param event on button click switch the tables to Customers, Appointments, or Reports based off of the value stored in the combobox
     * @throws IOException
     */
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

    /**
     * filters the table to appointments that match the entered search query when enter is pressed
     * @param event
     * @throws SQLException
     */
    public void filterSearchbar(KeyEvent event) throws SQLException {
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
        if (event.getCode() == KeyCode.ENTER || Objects.equals(reportSearchFilter.getText(), "")) {
            String filterString = reportSearchFilter.getText();
            ObservableList<Appointments> appointmentList = ad.getAllFromDB().stream().filter(appointment -> appointment.getType().
                            toUpperCase().contains(filterString.toUpperCase()) ||
                            appointment.getStartDateTime().toLocalDate().format(monthFormat).contains(filterString)).
                    collect(Collectors.toCollection(FXCollections::observableArrayList));
            if (appointmentList.size() < 1) {
                reportTableView1.setItems(null);
            } else {
                reportTableView1.setItems(appointmentList);
            }
        }
    }

    /**
     * finds all the usernames found from the login_activity log file
     * then translates their usernames to ids, and if their ids is found in the database fill the combobox
     * with an observable list of those values
     * @throws SQLException
     */
    public void fillReportComboBox() throws SQLException {
        List<Integer> customerIDs = userNamesFound.stream().map(username -> {
            try {
                return findUserIDByName(username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        ObservableList<Integer> users = ud.getAllFromDB().stream().map(Users::getUser_ID).
                filter(user_id -> customerIDs.stream().anyMatch(one -> Objects.equals(one, user_id))).
                collect(Collectors.toCollection(FXCollections::observableArrayList));
        comboBoxThirdReport.setItems(users);
    }

    /**
     * fills the contacts combobox with contacts that currently have appointments stored in the database
     * @throws SQLException
     */
    public void fillContactCombobox() throws SQLException {
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        Set<String> contactNameSet = new HashSet<>();
        for (Appointments appointments : ad.getAllFromDB()) {
            for (Contacts contact : cd.getAllFromDB()) {
                if (Objects.equals(appointments.getContactsID(), contact.getContactID())) {
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
        try {fillContactCombobox();
            fillNameList();
            initializeReportTable();
            fillReportComboBox();
            initUserCountMap();
            scanLoginLog();
        } catch (SQLException | IOException e) {e.printStackTrace();}

        ObservableList<String> tableComboList = FXCollections.observableArrayList();
        tableComboList.add("Appointments");
        tableComboList.add("Customers");
        tableComboList.add("Reports");

        reportsTableSwitchComboBox.setItems(tableComboList);
    }
}


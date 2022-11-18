package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.AppointmentsDao;
import com.example.customerdatabaseprojectii.daos.CustomersDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.DbConnection;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

public class CustomerMainController implements Initializable {
    @FXML
    TableView<Customers> customerTableView;
    @FXML
    ComboBox<String> customerTableSwitchComboBox;
    @FXML
    Label customerLabel;
    @FXML
    TableColumn<Customers, Integer> custCustomerID;
    @FXML
    TableColumn<Customers, String> custCustomerName;
    @FXML
    TableColumn<Customers, String> custAddress;
    @FXML
    TableColumn<Customers, String> custPostal;
    @FXML
    TableColumn<Customers, String> custPhone;
    @FXML
    TableColumn<Customers, Integer> custDivID;
    @FXML
    Label customerDeletedText;
    @FXML
    Button addCustomerButton;
    @FXML
    Button modifyCustomerButton;
    @FXML
    Button deleteCustomerButton;
    @FXML
    Button customerTableSwitchButton;


    private static Customers selectedCustomer = null;
    AppointmentsDao ad = new AppointmentsDao();
    private static final String deleteRelatedAppointmentQuery = "DELETE FROM appointments WHERE Appointment_ID = ?";

    public CustomerMainController() throws SQLException {}

    public static void setSelectedCustomerNull(){
        selectedCustomer = null;
    }
    public static Customers getSelectedCustomer() {
        if(selectedCustomer!=null) {
            return selectedCustomer;
        }else{
            Customers customerDefault = new Customers();
            customerDefault.setDivisionID(0);
            customerDefault.setCustomerID(0);
            customerDefault.setCustomerName("");
            customerDefault.setPostalCode("");
            customerDefault.setPhoneNumber("");
            customerDefault.setAddress("");

            System.out.println("Selected Customer is null, returning default customer");
            return customerDefault;
        }
    }

    private static final ObservableList<Customers> allCustomersObservableList = FXCollections.observableArrayList();
    public static ObservableList<Customers> getAllCustomers() {
        return allCustomersObservableList;
    }
    public static void addCustomerToObservableList(Customers customer) {
        allCustomersObservableList.add(customer);
    }


    public void deleteRelatedAppointments() throws SQLException {
        int customerIDToDelete = getSelectedCustomer().getCustomerID();
        PreparedStatement deleteStatement = DbConnection.getConnection().prepareStatement(deleteRelatedAppointmentQuery);
        deleteStatement.setInt(1, customerIDToDelete);
        for(Appointments app :  ad.getAllFromDB()){
            if(app.getCustomerID() == customerIDToDelete){
                deleteStatement.execute();
                customerDeletedText.setText(String.format("Customer: '%s' has been deleted", getSelectedCustomer().getCustomerName()));
            }
        }
    }

    public void deleteCustomerFromAllCustomers(Customers customer) throws SQLException {
        String deleteQuery = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement deleteStatement = DbConnection.getConnection().prepareStatement(deleteQuery);
        deleteStatement.setInt(1, customer.getCustomerID());

        try {
           deleteRelatedAppointments();
       }catch (SQLException e){
           e.printStackTrace();
           System.out.printf("Unable to remove appointments relating to the customer: %s%n", customer);
       }
        getAllCustomers().removeIf(c -> c.equals(customer));
        deleteStatement.execute();
    }

    public void switchToAddCustomerForm(ActionEvent event) throws IOException {
        CustomerFormController.modifyCustomer = false;
        if (Objects.equals(getSelectedCustomer().getCustomerName(), "") || Objects.equals(getSelectedCustomer(), null)) {
            Main.genNewStageAndScene("src/main/java/com/example/customerdatabaseprojectii/view/CustomerForm.fxml", 340, 568, "Customer Form");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You are trying to add a new customer, with one selected!\nClick 'OK' to deselect the customer, or cancel to escape.");
            Optional<ButtonType> buttonResult = alert.showAndWait();

            if (buttonResult.get() == ButtonType.OK) {
                setSelectedCustomerNull();
            } else {
                if (buttonResult.get() == ButtonType.CANCEL) {
                    System.out.println("Customer is still selected");
                }
            }
        }
    }



    public void setCustomerSelected(){
    try {
        selectedCustomer = getCustomerByIndex(getCustomerIndex(customerTableView.getSelectionModel().getSelectedItem()));
    }catch (IndexOutOfBoundsException e){
        System.out.println("Customer not clicked");
        }
    }
    public void clickOnCustomerToSetSelected(MouseEvent customerClicked){
        setCustomerSelected();
    }

    public static void setCustomerByIndex(int customerIndex, Customers customer) {
    getAllCustomers().set(customerIndex, customer);
    }

    public static Customers getCustomerByIndex(int customerIndex){
        return getAllCustomers().get(customerIndex);
    }

    public static int getCustomerIndex(Customers customer) {
        for (Customers c : getAllCustomers()) {
            if (c.equals(customer)) {
                return getAllCustomers().indexOf(c);
            }
        }
        return -1;
    }
    public void deleteCustomer(ActionEvent event) throws SQLException {
        Customers customerToDel = customerTableView.getSelectionModel().getSelectedItem();

        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this customer?");
            Optional<ButtonType> buttonResult = alert.showAndWait();

            if (buttonResult.get() == ButtonType.OK) {
            deleteCustomerFromAllCustomers(customerToDel);
            customerTableView.setItems(getAllCustomers());

            } else {
                if (buttonResult.get() == ButtonType.CANCEL) {
                    customerTableView.setItems(getAllCustomers());
                }
            }
        }
    }
    Consumer<Customers> customerConsumer = customer ->{
        CustomersDao cd = new CustomersDao();

        if(CustomerFormController.modifyCustomer){
            try {
                String s = cd.updateDB(customer);
                System.out.println(s);
                getAllCustomers().set(CustomerMainController.getCustomerIndex(customer),customer);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            try {
                String s = cd.dbInsert(customer);
                System.out.println(s);
                getAllCustomers().add(customer);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    };
    public void addCustomer(ActionEvent event) throws IOException {
        if (Objects.equals(getSelectedCustomer(), null)) {
            CustomerFormController.modifyCustomer = false;

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new URL("src/main/java/com/example/customerdatabaseprojectii/view/CustomerForm.fxml"));
            Parent node = loader.load();
            CustomerFormController afc = loader.getController();
            afc.customerFormInit(selectedCustomer, customerConsumer);

            Stage customerFormStage = new Stage();
            customerFormStage.setScene(new Scene(node));
            customerFormStage.setResizable(false);
            customerFormStage.initStyle(StageStyle.DECORATED);

            customerFormStage.show();
        } else {
            Alerter.warningAlert("You cannot add a new customer with one selected, unselecting customer.");
            selectedCustomer = null;
        }
    }

    public void modifyCustomer(ActionEvent event) throws IOException {
        if (!Objects.equals(getSelectedCustomer().getCustomerName(), "")) {
            CustomerFormController.modifyCustomer = true;

            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(new URL("src/main/java/com/example/customerdatabaseprojectii/view/CustomerForm.fxml"));
            Parent node = loader.load();
            CustomerFormController afc = loader.getController();

            afc.customerFormInit(selectedCustomer, customerConsumer);

            Stage customerFormStage = new Stage();
            customerFormStage.setScene(new Scene(node));
            customerFormStage.setResizable(false);
            customerFormStage.initStyle(StageStyle.DECORATED);

            customerFormStage.show();
        } else {
            Alerter.warningAlert("You are trying to add an customer that already exists, unselecting customer");
            selectedCustomer = null;
        }

    }




    public void switchTablesClicked(ActionEvent event) throws IOException {
        try {
            switch (customerTableSwitchComboBox.getValue()) {
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
            Alerter.informationAlert(String.format("Could not find the table: %s. Please try again later!", customerTableSwitchComboBox.getValue()));
        }
    }


    public static Optional<Customers> customerIDCountSetter(){
    if(getAllCustomers().size() > 1){
        return getAllCustomers().stream().max(Comparator.comparing(Customers::getCustomerID));
        }
        return Optional.empty();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(System.getProperty("user.language").equals("fr") || LoginController.changeLang){
            custCustomerID.setText("Identifiant du client");
            custCustomerName.setText("Nom");
            custAddress.setText("Adresse");
            custPostal.setText("Poste");
            custPhone.setText("Téléphone");
            addCustomerButton.setText("Ajouter");
            modifyCustomerButton.setText("Modifier");
            deleteCustomerButton.setText("Effacer");
            customerTableSwitchButton.setText("Commutateur");
            customerTableSwitchComboBox.setPromptText("Tableaux");
            customerLabel.setText("Les clients");
        }

        selectedCustomer = null;
        Optional<Customers> customer = customerIDCountSetter();
        if(customer.isPresent()){
            CustomersDao.idCount = customer.get().getCustomerID() + 1;
        }


        ObservableList<String> tableComboList = FXCollections.observableArrayList();
        tableComboList.add("Appointments");
        tableComboList.add("Customers");
        tableComboList.add("Reports");
        customerTableSwitchComboBox.setItems(tableComboList);
        customerTableView.setItems(getAllCustomers());
        System.out.println(getAllCustomers());
        custCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        custCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        custAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        custPostal.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        custPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        custDivID.setCellValueFactory(new PropertyValueFactory<>("divisionID"));



        //all fields must be validated
        //when submit clicked the customer has to be set with the update method to set the customer
        //that was updated into the correct index

        //if there is duplicating check that the table is set to the current observable list that is static in each method that
        //accesses it at the end of the method

        //when user selects a country from the combo box, the first-level-division
        //is to be prepopulated based on the user selection of country, maybe use streams filter based on the user selected country
        //customer_ID is disabled & filled with auto-gen prompt text

        //when customer deleted a custom info prompt is sent to the ui that the user was deleted successfully


        //user can add, update and delete appointments,
        //a contact name is assinged to an appointment using a combobox
        //pulled from the allcontact observable list?
        //when appointment canceled the appointment Id will be displayed and type of appointment in the notification

        //create add appointment new scene, with text boxes that collect the appointmentID, title, descrip, location, contact, type, start date & time with a start
        //date picker and

        //check other app to see how they gen the Appointment_ID, or if they just let the database auto-gen it
        //when updating the appointment, the fields are auto-generated
        //all original appointment info is displayed on the update form in local time
        //so get the location of the user and create the timezone with the
        //information of the country where the user is, and use time format to format where the user is, parse maybe?
        //remember all this time information is displayed in the update form of the original appointment

        //create tabs for appointments, one tab for week, one tab for month
        //all the appointments within a 28 day period from the start of the month till the end of the month
        //are displayed in the month tab
        //all appointments that take place within each 7 days of each other are grouped into weeks
        //day 0-7 grouped into week1, 7-14 in week two, 21-28 in week 4



    }
}

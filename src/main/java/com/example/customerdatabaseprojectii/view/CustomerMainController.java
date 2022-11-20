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

import java.io.File;
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
    CustomersDao cd = new CustomersDao();
    private static final String deleteRelatedAppointmentQuery = "DELETE FROM appointments WHERE Appointment_ID = ?";

    public CustomerMainController() throws SQLException {}

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

    private final ObservableList<Customers> allCustomersObservableList = cd.getAllFromDB();
    public ObservableList<Customers> getAllCustomers() {
        return allCustomersObservableList;
    }
    public void addCustomerToObservableList(Customers customer) {
        allCustomersObservableList.add(customer);
    }

    public void setCustomersList() throws SQLException {
        CustomersDao cd = new CustomersDao();
        getAllCustomers().addAll(cd.getAllFromDB());
    }
    public static void setSelectedCustomerNull(){
        CustomerMainController.selectedCustomer = null;
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


    public Customers getCustomerByIndex(int customerIndex){
        return getAllCustomers().get(customerIndex);
    }

    public int getCustomerIndex(Customers customer) {
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
                if(!Objects.equals(s, "")){
                    getAllCustomers().set(getCustomerIndex(customer),customer);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            try {
                String s = cd.dbInsert(customer);
                if(!Objects.equals(s, "")){
                    getAllCustomers().add(customer);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    };
    public void addCustomer(ActionEvent event) throws IOException {
        if (Objects.equals(getSelectedCustomer(), null)) {
            CustomerFormController.modifyCustomer = false;
            URL path =  new File("src/main/java/com/example/customerdatabaseprojectii/view/CustomerForm.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(path);
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
            URL path =  new File("src/main/java/com/example/customerdatabaseprojectii/view/CustomerForm.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(path);
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
            Alerter.informationAlert(String.format("Could not find the table: %s. Please try again later!", customerTableSwitchComboBox.getValue()));
        }
    }

//
//    public static Optional<Customers> customerIDCountSetter(){
//    if(getAllCustomers().size() > 1){
//        return getAllCustomers().stream().max(Comparator.comparing(Customers::getCustomerID));
//        }
//        return Optional.empty();
//    }

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
            customerTableSwitchButton.setMinWidth(134);
        }

        selectedCustomer = null;
        ObservableList<String> tableComboList = FXCollections.observableArrayList();
        tableComboList.add("Appointments");
        tableComboList.add("Customers");
        tableComboList.add("Reports");
        customerTableSwitchComboBox.setItems(tableComboList);
        customerTableView.setItems(getAllCustomers());
        custCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        custCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        custAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        custPostal.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        custPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        custDivID.setCellValueFactory(new PropertyValueFactory<>("divisionID"));

    }
}

package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.entity.Customers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerFormController implements Initializable {
    @FXML
    TextField cfCustomerName;
    @FXML
    TextField cfCustomerPostal;
    @FXML
    TextField cfCustomerNumber;
    @FXML
    TextField cfCustomerAddress;
    @FXML
    ComboBox<String> cfCustomerCountry;
    @FXML
    ComboBox<String> cfCustomerFirstLevel;
    @FXML
    Button cfCancel;

    private static final ObservableList<Customers> allCustomersObservableList = FXCollections.observableArrayList();

    public static ObservableList<Customers> getAllCustomers(){
        return allCustomersObservableList;
    }

    public static void addCustomerToObservableList(Customers customer){
        allCustomersObservableList.add(customer);
    }



    //need the db connection to pass in query
    //DELETE FROM Appointments where Customer.CustomerID = Customer.CustomerID

    //

    public static void deleteCustomerFromAllCustomers(Customers customer){
        for(Customers c : getAllCustomers()){
            if(c.equals(customer)){
                if(allCustomersObservableList.remove(customer)){
                    System.out.println("Customer Successfully removed");
                }else{
                    System.out.println("Failed to remove customer");
                }
            }
        }
    }

    public static void setCustomerByIndex(int customerIndex, Customers customer){
        getAllCustomers().set(customerIndex, customer);
    }
    public static int getCustomerIndex(Customers customer){
        for(Customers c : getAllCustomers()){
            if(c.equals(customer)){
                return getAllCustomers().indexOf(c);
            }

        }
        return -1;
    }

//    public void deleteCustomer(ActionEvent event) {
//        //part
//        Customers customerToDel = customerTableView.getSelectionModel().getSelectedItem();
//        {
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this part?");
//            Optional<ButtonType> buttonResult = alert.showAndWait();
//
//            if (buttonResult.get() == ButtonType.OK) {
//
//                inventoryPartsTable.setItems(allParts);
//                indexPartUpdater();
//
//            } else {
//                if (buttonResult.get() == ButtonType.CANCEL) {
//                    inventoryPartsTable.setItems(allParts);
//                    return;
//                }
//            }
//        }
//    }

    public void addCustomerLabel(ActionEvent event) throws IOException {
        Customers customerToAdd = new Customers();

        String customerName = cfCustomerName.getText();
        int customerPostal = Integer.parseInt(cfCustomerPostal.getText());
        long customerNumber = Long.parseLong(cfCustomerNumber.getText());
        String customerAddress = cfCustomerAddress.getText();

        String countrySelected = cfCustomerCountry.getValue();
        String firstLevelSelected = cfCustomerFirstLevel.getValue();

        customerToAdd.setCustomerName(customerName);
        customerToAdd.setPostalCode(customerPostal);
        customerToAdd.setPhoneNumber(customerNumber);
        customerToAdd.setAddress(customerAddress);

        //Function here to handle country and firstlevel division data to submit the data entered
        //these will be collected using comboBoxes
        //and translate it into data that is stored in the customer data field within the table

        //when updating customer, customer data autopopulates the from
        //the selected customer to update must be stored in a static variable, so when the
        //form to update is opened grab the information from the static customer object & populate the fields

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


        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/TableMenu.fxml", Main.getMainStage(), 473, 990, "TableView");
    }

    public void cancelButtonClicked(ActionEvent event) throws IOException {
        cfCancel.getScene();
        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/TableMenu.fxml", Main.getMainStage(), 473, 990, "TableView");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<String> customerCountries = FXCollections.observableArrayList();
        ObservableList<String> customerFirstLevelDiv = FXCollections.observableArrayList();
        customerCountries.add("United States");
        customerCountries.add("United Kingdom");
        customerCountries.add("France");
        customerCountries.add("Mexico");
        customerCountries.add("Canada");

        customerFirstLevelDiv.add("14142");
        customerFirstLevelDiv.add("43422");
        customerFirstLevelDiv.add("43439");
        customerFirstLevelDiv.add("93339");
        customerFirstLevelDiv.add("15349");


        cfCustomerCountry.setItems(customerCountries);
        cfCustomerFirstLevel.setItems(customerFirstLevelDiv);
    }
}

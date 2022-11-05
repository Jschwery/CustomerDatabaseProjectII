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
        //and translate it into data that is stored in the customer data field within the table


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

package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.CustomersDao;
import com.example.customerdatabaseprojectii.daos.First_Level_DivisionsDao;
import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.entity.First_Level_Divisions;
import com.example.customerdatabaseprojectii.util.Alerter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class CustomerFormController implements Initializable {
    @FXML
    TextField cfCustomerName;
    @FXML
    TextField cfCustomerAddress;
    @FXML
    TextField cfCustomerPostal;
    @FXML
    TextField cfCustomerNumber;
    @FXML
    ComboBox<String> cfCustomerCountry;
    @FXML
    ComboBox<String> cfCustomerFirstLevel;
    @FXML
    Button cfCancel;


    static boolean modifyCustomer = false;
    private static ObservableList<First_Level_Divisions> unitedStatesFirstLevelDiv = FXCollections.observableArrayList();
    private static ObservableList<First_Level_Divisions> unitedKingdomFirstLevelDiv = FXCollections.observableArrayList();
    private static ObservableList<First_Level_Divisions> canadaFirstLevelDiv = FXCollections.observableArrayList();
    private static ObservableList<String> customerCountries = FXCollections.observableArrayList();


    public void addCountriesToObservableList(){
        customerCountries.add("U.S");
        customerCountries.add("UK");
        customerCountries.add("Canada");
    }

      /*
    What is going on in the application

    First: when uk is selected, it is getting canadas values

    when the save button or cancel button is clicked we need to clear
    the firstlevel divisions & the form textfields


     */


    public void populateObservableFirstLevelDivs(){
        try {
        ObservableList<First_Level_Divisions> firstLevels = First_Level_DivisionsDao.addDivisionToObservableList();
        for(First_Level_Divisions div : firstLevels){
            if(div.getCountryID() == 1) {
                unitedStatesFirstLevelDiv.add(div);
            }
            else if(div.getCountryID() == 3){
                canadaFirstLevelDiv.add(div);
            }
            else if(div.getCountryID() == 2){
                unitedKingdomFirstLevelDiv.add(div);
            }
        }
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Could not generate first level divisions from database");
        }
    }

    /*
    now the combobox displaying the countries is getting duplicating after the save button is clicked

    update customer query has an issue

     */


    public void filterFirstLevelByCountry(ActionEvent event) {

        String countrySelected = cfCustomerCountry.getSelectionModel().getSelectedItem();
        System.out.println(countrySelected);
        System.out.println("UK " + unitedKingdomFirstLevelDiv);
        if (countrySelected != null) {
            switch (countrySelected) {
                case "U.S":
                    ObservableList<String> usFLDName = FXCollections.observableArrayList();
                    for (First_Level_Divisions fld : unitedStatesFirstLevelDiv) {
                        usFLDName.add(fld.getDivision());
                    }
                    if (!usFLDName.isEmpty()) {
                        cfCustomerFirstLevel.setItems(usFLDName);
                    } else {
                        System.out.println("United States has been selected as country," +
                                "but unitedStatesFirstLevelDiv did not contain values");
                    }
                    break;
                case "UK":
                    ObservableList<String> ukFLDName = FXCollections.observableArrayList();
                    for (First_Level_Divisions fld : unitedKingdomFirstLevelDiv) {
                        ukFLDName.add(fld.getDivision());
                    }
                    if (!ukFLDName.isEmpty()) {
                        System.out.println("uk not empty " + unitedKingdomFirstLevelDiv);
                        cfCustomerFirstLevel.setItems(ukFLDName);
                    } else {
                        System.out.println("United Kingdom has been selected as country," +
                                "but unitedKingdomFirstLevelDiv did not contain values");
                    }
                case "Canada":
                    ObservableList<String> canadaFLDName = FXCollections.observableArrayList();
                    for (First_Level_Divisions fld : canadaFirstLevelDiv) {
                        canadaFLDName.add(fld.getDivision());
                    }
                    if (!canadaFLDName.isEmpty()) {
                        cfCustomerFirstLevel.setItems(canadaFLDName);
                    } else {
                        System.out.println("Canada has been selected as country," +
                                "but canadaFirstLevelDiv did not contain values");
                    }
            }
        }else{
            System.out.println("No country selected");
        }
    }
    public int getDivisionIDByDivisionName(){
        String division = cfCustomerFirstLevel.getSelectionModel().getSelectedItem();
        try {
            for (First_Level_Divisions div : First_Level_DivisionsDao.addDivisionToObservableList()) {
                if(div.getDivision().equals(division)){
                    return div.getCountryID();
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }
    public void closeSceneWindow() {
        Stage stage = (Stage) cfCustomerName.getScene().getWindow();
        CustomerMainController.setSelectedCustomerNull();
        clearLists();
        stage.close();
    }
    public void clearLists(){
        unitedKingdomFirstLevelDiv.clear();
        unitedStatesFirstLevelDiv.clear();
        canadaFirstLevelDiv.clear();
        customerCountries.clear();
        cfCustomerName.setText("");
        cfCustomerPostal.setText("");
        cfCustomerNumber.setText("");
        cfCustomerAddress.setText("");
        cfCustomerCountry.setValue("");
        cfCustomerFirstLevel.setValue("");
    }
    /*TODO
    clear countries issue

    we have a country list with countries that we fill the combo box with,
    we can do this on initialize right so it will stay a combo box with countries


    we need to set the lists upon each time we go into the form
    and clear the list each time we submit or cancel

    */

    public void addCustomerClicked(ActionEvent event) throws IOException {
        Customers customerToAdd = new Customers();
        String customerName = cfCustomerName.getText();
        String customerPostal = cfCustomerPostal.getText();
        String customerNumber = cfCustomerNumber.getText();
        String customerAddress = cfCustomerAddress.getText();
        cfCustomerCountry.setPromptText("Country");
        cfCustomerFirstLevel.setPromptText("First Level Div");

        customerToAdd.setCustomerName(customerName);
        customerToAdd.setPostalCode(customerPostal);
        customerToAdd.setPhoneNumber(customerNumber);
        customerToAdd.setAddress(customerAddress);
        customerToAdd.setDivisionID(getDivisionIDByDivisionName());

        if(modifyCustomer){
            customerToAdd.setCustomerID(CustomerMainController.getSelectedCustomer().getCustomerID());
            System.out.println("Adding modified customer back to database & to observable list");
            try {
                CustomersDao.updateCustomerInDatabase(customerToAdd);
            }catch (SQLException e){
                e.printStackTrace();
                Alerter.warningAlert("Error updating customer");
            }
            int indexOfSelectedCustomer = CustomerMainController.getCustomerIndex(CustomerMainController.getSelectedCustomer());
            CustomerMainController.setCustomerByIndex(indexOfSelectedCustomer, customerToAdd);
            CustomerMainController.setSelectedCustomerNull();
        }
        else if(!modifyCustomer){
            customerToAdd.setCustomerID(CustomersDao.idCount);
            System.out.println("Adding new customer to database & to observable list");
            try{
                CustomersDao.insertCustomerIntoDatabase(customerToAdd);
            }catch (SQLException e){
                e.printStackTrace();
                Alerter.warningAlert("Error adding customer");
            }
            CustomerMainController.addCustomerToObservableList(customerToAdd);
        }
        CustomerMainController.setSelectedCustomerNull();
        clearLists();
        closeSceneWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearLists();
        addCountriesToObservableList();
        cfCustomerCountry.setItems(customerCountries);
        populateObservableFirstLevelDivs();
        if(CustomerMainController.getSelectedCustomer()!=null){
            Customers custTemp = CustomerMainController.getSelectedCustomer();
            cfCustomerName.setText(custTemp.getCustomerName());
            cfCustomerAddress.setText(custTemp.getAddress());
            cfCustomerNumber.setText(custTemp.getPhoneNumber());
            cfCustomerPostal.setText(custTemp.getPostalCode());
        }
    }
}

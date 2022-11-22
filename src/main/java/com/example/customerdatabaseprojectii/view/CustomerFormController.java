package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.daos.CustomersDao;
import com.example.customerdatabaseprojectii.daos.First_Level_DivisionsDao;
import com.example.customerdatabaseprojectii.entity.Customers;
import com.example.customerdatabaseprojectii.entity.First_Level_Divisions;
import com.example.customerdatabaseprojectii.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CustomerFormController{
    @FXML
    TextField cfCustomerName;
    @FXML
    TextField cfCustomerAddress;
    @FXML
    TextField cfCustomerPostal;
    @FXML
    TextField cfCustomerID;
    @FXML
    TextField cfCustomerNumber;
    @FXML
    ComboBox<String> cfCustomerCountry;
    @FXML
    ComboBox<String> cfCustomerFirstLevel;
    @FXML
    Button cfCancel;

    static boolean modifyCustomer = false;
    private static final ObservableList<First_Level_Divisions> unitedStatesFirstLevelDiv = FXCollections.observableArrayList();
    private static final ObservableList<First_Level_Divisions> unitedKingdomFirstLevelDiv = FXCollections.observableArrayList();
    private static final ObservableList<First_Level_Divisions> canadaFirstLevelDiv = FXCollections.observableArrayList();
    private static final ObservableList<String> customerCountries = FXCollections.observableArrayList();
    First_Level_DivisionsDao divisions = new First_Level_DivisionsDao();
    CustomersDao cd = new CustomersDao();
    Consumer<Customers> customersConsumer;
    Customers customer;

    public void addCountriesToObservableList(){
        customerCountries.add("U.S");
        customerCountries.add("UK");
        customerCountries.add("Canada");
    }
    public int getCustomerIDCount() throws SQLException {
        return cd.getAllFromDB().size() + 1;
    }

    public String findCountryIDByDivID(int divID) throws SQLException {
        String country;
        if(divID >= 1 && divID <= 54){
            country = "U.S";
        }else if(divID >= 55 && divID <= 72 ){
            country = "Canada";
        }else{
            country = "UK";
        }
        return country;
    }
    public ObservableList<First_Level_Divisions> findFirstLevelsByDivID(int divID) {
        if (divID >= 1 && divID <= 54) {
            return unitedStatesFirstLevelDiv;
        } else if (divID >= 55 && divID <= 72) {
            return canadaFirstLevelDiv;
        } else {
            return unitedKingdomFirstLevelDiv;
        }
    }

    public String findFirstLevelDiv(int divID) throws SQLException {

        Optional<First_Level_Divisions> matchDiv = divisions.getAll().stream().filter(div -> div.getDivisionID() == divID).findFirst();
        if (matchDiv.isPresent()) {
            return matchDiv.get().getDivision();
        }
    return "";
    }

    public void populateObservableFirstLevelDivs(){
      try{
        for(First_Level_Divisions div : divisions.getAll()){
            if(div.getCountryID() == 1) {
                unitedStatesFirstLevelDiv.add(div);
            }
            else if(div.getCountryID() == 2){
                unitedKingdomFirstLevelDiv.add(div);
            }
            else if(div.getCountryID() == 3){
                canadaFirstLevelDiv.add(div);
            }
        }
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Could not generate first level divisions from database");
        }
    }

    public void customerFormInit(Customers selectedCustomer, Consumer<Customers> customerConsumer) throws SQLException {
        this.customersConsumer = customerConsumer;
        this.customer = selectedCustomer;

        if (modifyCustomer && CustomerMainController.getSelectedCustomer().isPresent()) {
            cfCustomerID.setText(String.valueOf(CustomerMainController.getSelectedCustomer().get().getCustomerID()));
            cfCustomerName.setText(customer.getCustomerName());
            cfCustomerAddress.setText(customer.getAddress());
            cfCustomerNumber.setText(customer.getPhoneNumber());
            cfCustomerPostal.setText(customer.getPostalCode());
            cfCustomerCountry.setValue(findCountryIDByDivID(customer.getDivisionID()));
            cfCustomerFirstLevel.setValue(findFirstLevelDiv(customer.getDivisionID()));
        }else{
            cfCustomerID.setText(String.valueOf(getCustomerIDCount()));
        }
    }

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
                    break;
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
                    break;
            }
        }else{
            System.out.println("No country selected");
        }
    }
    public int getDivisionID(String divisionName) {
        First_Level_DivisionsDao fldd = new First_Level_DivisionsDao();
        try {
            for (First_Level_Divisions div : fldd.getAll()) {
                if (Objects.equals(div.getDivision().toUpperCase(), divisionName.toUpperCase())) {
                    return div.getDivisionID();
                }
            }
        } catch (SQLException sq) {
            sq.printStackTrace();
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

    public boolean validateFields(){
        if(Validator.intChecker(cfCustomerID.getText(), "Please only enter number characters for CustomerID text field!")){
            customer.setCustomerID(Integer.parseInt(cfCustomerID.getText()));
        }else{
            return false;
        }
        if(Validator.stringChecker(cfCustomerName.getText(), "Please only enter alphabetical characters for CustomerName field!")){
            customer.setCustomerName(cfCustomerName.getText());
        }else{
            return false;
        }
        if(Validator.stringChecker(cfCustomerAddress.getText(), "Please only enter alphabetical characters for CustomerAddress text field!")){
            customer.setAddress(cfCustomerAddress.getText());
        }else{
            return false;
        }if(Validator.intChecker(cfCustomerPostal.getText(), "Please only enter numerical characters for Postal text field!")){
            customer.setPostalCode(cfCustomerPostal.getText());
        }else{
            return false;
        }
        return true;
    }

    public void addCustomerClicked(ActionEvent event) throws IOException {
        customer = new Customers();

        validateFields();
        customer.setPhoneNumber(cfCustomerNumber.getText());
        customer.setDivisionID(getDivisionID(cfCustomerFirstLevel.getValue()));

        System.out.println(customer);
        customersConsumer.accept(customer);
        CustomerMainController.setSelectedCustomerNull();
        clearLists();
        modifyCustomer = false;
        closeSceneWindow();
    }

    @FXML
    public void initialize() throws SQLException {
        clearLists();
        addCountriesToObservableList();
        populateObservableFirstLevelDivs();

        cfCustomerCountry.setItems(customerCountries);
        if(CustomerMainController.getSelectedCustomer().isPresent()) {
            ObservableList<String> fldNames = FXCollections.observableArrayList();
            ObservableList<First_Level_Divisions> divs = findFirstLevelsByDivID(CustomerMainController.getSelectedCustomer().get().getDivisionID());
            for(First_Level_Divisions fld : divs) {
                fldNames.add(fld.getDivision());
            }
            cfCustomerFirstLevel.setItems(fldNames);
        }
    }
}

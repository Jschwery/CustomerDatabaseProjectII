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
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;

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

    public void populateObservableFirstLevelDivs(){
      try{
        for(First_Level_Divisions div : divisions.getAll()){
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

    public void customerFormInit(Customers selectedCustomer, Consumer<Customers> customerConsumer) {
        this.customersConsumer = customerConsumer;
        this.customer = selectedCustomer;

        if (modifyCustomer && CustomerMainController.getSelectedCustomer() != null) {
            cfCustomerName.setText(customer.getCustomerName());
            cfCustomerAddress.setText(customer.getAddress());
            cfCustomerNumber.setText(customer.getPhoneNumber());
            cfCustomerPostal.setText(customer.getPostalCode());
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

    public void addCustomerClicked(ActionEvent event) throws IOException {
        customer.setCustomerID(Integer.parseInt(cfCustomerID.getText()));
        customer.setCustomerName(cfCustomerName.getText());
        customer.setAddress(cfCustomerAddress.getText());
        customer.setPhoneNumber(cfCustomerNumber.getText());
        customer.setPostalCode(cfCustomerPostal.getText());
        customer.setDivisionID(getDivisionID(cfCustomerFirstLevel.getValue()));

        customersConsumer.accept(customer);
        CustomerMainController.setSelectedCustomerNull();
        clearLists();
        modifyCustomer = false;
        closeSceneWindow();
    }

    @FXML
    public void initialize() {
        clearLists();
        addCountriesToObservableList();
        cfCustomerCountry.setItems(customerCountries);
        populateObservableFirstLevelDivs();
    }
}

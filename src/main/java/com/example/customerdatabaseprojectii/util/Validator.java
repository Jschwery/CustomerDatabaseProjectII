package com.example.customerdatabaseprojectii.util;

import javafx.scene.control.Alert;

public class Validator {

    /**
     * @param checkForInt string that will be parsed to check whether it is an int or not
     * @param textToDisplay text that will be displayed within the Information alert popup to the user if the string passed in did not contain an int.
     * @return true if the entered string was of type int, false otherwise.
     */
    public static boolean intChecker(String checkForInt, String textToDisplay) {
        try {
            Integer.parseInt(checkForInt);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("An int was not entered");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, textToDisplay);
            alert.showAndWait();
            return false;
        }
    }

    /**
     * @param checkForString string that will be parsed to check whether it is a string or not
     * @param textToDisplay text that will be displayed within the Information alert popup to the user if the string passed in did not contain a string.
     * @return true if the entered string was of type string, false otherwise.
     */
    public static boolean stringChecker(String checkForString, String textToDisplay){
        try{
            for(int i = 0; i < checkForString.length(); i++){
                if(Character.isDigit((checkForString.charAt(i)))){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, textToDisplay);
                    alert.showAndWait();
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param checkDouble string that will be parsed to check whether it is a double or not
     * @param textToDisplay text that will be displayed within the Information alert popup to the user if the string passed in did not contain a double.
     * @return true if the entered double was of type double, false otherwise.
     */
    public static boolean doubleChecker(String checkDouble, String textToDisplay) {
        try {
            Double.parseDouble(checkDouble);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("A Double was not entered");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, textToDisplay);
            alert.showAndWait();
            return false;
        }
    }

}

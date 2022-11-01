package com.example.customerdatabaseprojectii.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Alerter {

    public static void confirmationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        Optional<ButtonType> buttonResult = alert.showAndWait();

        if (buttonResult.get() == ButtonType.OK) {
            System.out.println("Okay buttonClicked!");
        } else {
            if (buttonResult.get() == ButtonType.CANCEL) {
                System.out.println("Cancel button clicked!");
            }
        }
    }
    public static void informationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        Optional<ButtonType> buttonResult = alert.showAndWait();

        if (buttonResult.get() == ButtonType.OK) {
            System.out.println("Okay buttonClicked!");
        } else {
            if (buttonResult.get() == ButtonType.CANCEL) {
                System.out.println("Cancel button clicked!");
            }
        }
    }
    public static void warningAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        Optional<ButtonType> buttonResult = alert.showAndWait();

        if (buttonResult.get() == ButtonType.OK) {
            System.out.println("Okay buttonClicked!");
        } else {
            if (buttonResult.get() == ButtonType.CANCEL) {
                System.out.println("Cancel button clicked!");
            }
        }
    }




}
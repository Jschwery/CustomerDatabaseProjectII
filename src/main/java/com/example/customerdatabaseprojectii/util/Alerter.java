package com.example.customerdatabaseprojectii.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Alerter {

    public static void informationAlert(String message) {
        String userLang = System.getProperty("user.language");

        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        Optional<ButtonType> buttonResult = alert.showAndWait();
        if(userLang.equals("fr")){
            alert.setTitle("informations");
        }
        if (buttonResult.get() == ButtonType.OK) {
            System.out.println("Okay buttonClicked!");
        } else {
            if (buttonResult.get() == ButtonType.CANCEL) {
                System.out.println("Cancel button clicked!");
            }
        }
    }
    public static void warningAlert(String message) {
        String userLang = System.getProperty("user.language");

        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        Optional<ButtonType> buttonResult = alert.showAndWait();
        if (userLang.equals("fr")) {
            alert.setTitle("Attention");
        }
        if (buttonResult.get() == ButtonType.OK) {
            System.out.println("Okay buttonClicked!");
        } else {
            if (buttonResult.get() == ButtonType.CANCEL) {
                System.out.println("Cancel button clicked!");
            }
        }
    }
}


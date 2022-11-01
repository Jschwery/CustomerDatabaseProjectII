package com.example.customerdatabaseprojectii;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        URL url =  new File("src/main/java/com/example/customerdatabaseprojectii/view/Login.fxml").toURI().toURL();
        Parent node = FXMLLoader.load(url);
        Scene scene = new Scene(node);
        stage.setTitle("Login");
        stage.setMinWidth(485);
        stage.setMinHeight(495);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
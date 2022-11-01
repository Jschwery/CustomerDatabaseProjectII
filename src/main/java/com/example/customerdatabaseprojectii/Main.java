package com.example.customerdatabaseprojectii;

import com.example.customerdatabaseprojectii.util.DbConnection;
import com.example.customerdatabaseprojectii.util.SceneChanger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/Login.fxml",
                stage,495, 485, "Login");
    }

    public static void main(String[] args) {
        DbConnection.makeConnection();
        launch(args);
        DbConnection.closeConnection();
    }
    public static Stage getMainStage(){
        return mainStage;
    }
    public static void changeScene(String url, Stage stage, int minHeight, int minWidth, String title) throws IOException {
        URL path =  new File(url).toURI().toURL();
        Parent node = FXMLLoader.load(path);
        Scene scene = new Scene(node);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);
        stage.show();
    }
}
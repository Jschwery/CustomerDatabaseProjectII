package com.example.customerdatabaseprojectii;

import com.example.customerdatabaseprojectii.daos.UsersDao;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.DbConnection;
//import com.example.customerdatabaseprojectii.util.GenLists;
import com.example.customerdatabaseprojectii.view.AppointmentFormController;
import com.example.customerdatabaseprojectii.view.CreateUsersController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.*;

public class Main extends Application {
    private static Stage mainStage;
    public static void playSound(String fileUrl) throws IOException{
        URL soundFile = new File(fileUrl).toURI().toURL();
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(-15.0f);
            clip.start();
        }catch (UnsupportedAudioFileException | LineUnavailableException e){
            e.printStackTrace();
        }
    }

    //mess arround with the auto file creation for users already in the database
    //for user in database if file contains user.get username create username.txt if user.getusername.txt !exists

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/Login.fxml",
                stage,495, 485, "Login", false);
    }
    public static void main(String[] args) throws SQLException, IOException {
        DbConnection.makeConnection();
//        getDBUsersAndCreateFiles();
        launch(args);
        DbConnection.closeConnection();
    }
    public static Stage getMainStage(){
        return mainStage;
    }

    public static void changeScene(String url, Stage stage, int minHeight, int minWidth, String title, boolean setResize) throws IOException {
        URL path =  new File(url).toURI().toURL();
        Parent node = FXMLLoader.load(path);
        Scene scene = new Scene(node);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);
        stage.setResizable(setResize);

        stage.show();
    }
}

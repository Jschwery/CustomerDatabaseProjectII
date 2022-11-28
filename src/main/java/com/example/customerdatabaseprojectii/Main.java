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

    /**
     * This method can be used to play sound files whenever a certain action takes place
     * @param fileUrl the Url of the wav audio file to be played
     * @throws IOException
     */
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

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/Login.fxml",
                stage,495, 485, "Login", false);
    }
    public static void main(String[] args) throws SQLException, IOException {
        DbConnection.makeConnection();
        launch(args);
        DbConnection.closeConnection();
    }
    public static Stage getMainStage(){
        return mainStage;
    }

    /**
     *
     * @param url The URL of the fxml file for the scene to load
     * @param stage The stage to set the scene, can pass in a new stage to be loaded or the main stage can be used
     * @param minHeight set the minHeight of the scene to be loaded
     * @param minWidth set the minWidth of the scene to be loaded
     * @param title set the title of the scene to be loaded
     * @param setResize set the ability to resize the scene
     * @throws IOException
     */
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

package com.example.customerdatabaseprojectii;

import com.example.customerdatabaseprojectii.daos.UsersDao;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.DbConnection;
//import com.example.customerdatabaseprojectii.util.GenLists;
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

    /*
    get all the users in the database and create a file for all of them

    need to have a check that scans through the files and if the substring of the file until the _ is their username
    then don't create a new file

    need method that will return the file that matches the criteria of finding the file based of the username
    Pattern pattern = Pattern.compile([a-zA-Z0-9]+_);
    Matcher.match(file.toString);
    for(File file : filelist){

    if(file.toString().

    }

     */

    public static void matchFileCriteria(File[] filesList, Users user) throws IOException, SQLException {
        CreateUsersController controller = new CreateUsersController();
        UsersDao ud = new UsersDao();
        Pattern pattern = Pattern.compile("(userLogInfo\\*[a-zA-Z0-9]+.txt)", Pattern.CASE_INSENSITIVE);
        if(filesList.length<= ud.getAllFromDB().size()){
            controller.storeCreationDate(user);
        }else{
            for(File file : filesList){
                Matcher matcher = pattern.matcher(file.toString());
                if(matcher.matches()){
                    controller.storeCreationDate(user);
                }
            }
        }
    }

    public static void getDBUsersAndCreateFiles() throws SQLException, IOException {
        UsersDao userDao = new UsersDao();
        File file = new File("userLogInfo");
        File[] filesList = file.listFiles();
        for(Users user : userDao.getAllFromDB()){
            assert filesList != null;
            matchFileCriteria(filesList, user);
        }
    }

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
        getDBUsersAndCreateFiles();
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

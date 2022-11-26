package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.UsersDao;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.Alerter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class CreateUsersController {
    @FXML
    TextField usernameTextField;
    @FXML
    TextField passwordTextField;
    @FXML
    PasswordField reenterPasswordTextField;
    UsersDao ud = new UsersDao();


    private static final Map<Integer, File> userIdToFileMap = new HashMap<>();
    public static Map<Integer, File> getUserIdToFileMap(){
        return userIdToFileMap;
    }
    //we are going to start by looping through all the
    //this is getting all the users in the database and finding the user that
    public void addUserIDToFileMap(Integer userID, File file) throws SQLException {
        Optional<Users> userFoundByID = ud.getAllFromDB().stream().filter(k -> Objects.equals(k.getUser_ID(), userID)).findFirst();
        if(userFoundByID.isPresent()){
            userIdToFileMap.put(userID, file);
        }else{
            System.out.println("No User found with the entered ID");
        }
    }

    public static boolean scanUserLogDirectory(File fileToCheck){
        File directory = new File("userLogInfo");
        File[] directoryFileListing = directory.listFiles();

        if(directoryFileListing != null){
            for(File file : directoryFileListing){
                if(Objects.equals(fileToCheck, file)){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean accountSubmitEvaluator(String uName, String pWord, String rePWord) {
        if (uName == null || pWord == null || rePWord == null) {
            Alerter.informationAlert("Please check that the fields are all filled in!");
            throw new RuntimeException();
        }
        if (uName.length() > 35) {
            Alerter.warningAlert("Username cannot be greater than 35 characters");
            return false;
        }
        if (pWord.length() > 35) {
            Alerter.warningAlert("Password cannot be greater than 35 characters");
            return false;
        }
        if (rePWord.length() > 35) {
            Alerter.warningAlert("Password cannot be greater than 35 characters");
            return false;
        }
        if (!Objects.equals(pWord, rePWord)) {
            Alerter.warningAlert("Make sure the password you entered matches the reentered password!");
            return false;
        }
        return true;
    }
    public void cancelButtonClicked(ActionEvent event) {
        try {
            Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/Login.fxml", Main.getMainStage(), 495, 485, "Login", false);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Could not switch back to login");
        }
    }
    public void storeCreationDate(Users user) throws IOException, SQLException {
        LocalDate attemptDate = LocalDateTime.now().toLocalDate();
        Optional<Timestamp> getCreateTime = ud.getAllFromDB().stream().filter(u -> Objects.equals(u.getUser_ID(), user.getUser_ID())).map(Users::getCreateDateTime).findFirst();

        File file = new File(String.format("userLogInfo/%s.txt", user.getUsername()));
        if (!scanUserLogDirectory(file)) {
            try {
                ud.getAllFromDB().forEach(k -> {
                    try {
                        addUserIDToFileMap(k.getUser_ID(), new File(String.format("userLogInfo/%s.txt", user.getUsername())));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                addUserIDToFileMap(user.getUser_ID(), file);
                BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("userLogInfo/%s.txt", user.getUsername())));
                if (getCreateTime.isPresent()) {
                    bw.write(String.format("User: %s\n\nCreated on: %s ", user, getCreateTime.get()));
                    bw.flush();
                    bw.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(String.format("%s Logged in attempt on: %s\n", user.getUsername(), attemptDate));
            writer.flush();
            writer.close();
        }
    }
    public void submitUserClicker(ActionEvent event) throws SQLException {
        Users user = new Users();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String reenterPassword = reenterPasswordTextField.getText();
        user.setUsername(usernameTextField.getText());
        user.setPassword(passwordTextField.getText());

        if (accountSubmitEvaluator(username, password, reenterPassword)) {
            user.setUsername(username);
            user.setPassword(password);
            try {
                if (!ud.verifyUserFromDB((user))) {
                    ud.dbInsert(user);
                    System.out.println("User successfully submitted to database!");
                    Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/Login.fxml", Main.getMainStage(), 495, 485, "Login", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Unable to add user to database");
                Alerter.warningAlert(String.format("The username: '%s' is taken!", username));
            }
            if (ud.verifyUserFromDB(user)) {
                System.out.println("User successfully added to the database!");
            } else {
                System.out.println("User was not added to the database!");
            }
            try {
                storeCreationDate(user);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.printf("User file already exists for %s", user);
            }
        }
    }
}







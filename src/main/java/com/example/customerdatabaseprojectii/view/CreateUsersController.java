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
import java.util.Objects;
import java.util.Random;

public class CreateUsersController {
    @FXML
    TextField usernameTextField;
    @FXML
    TextField passwordTextField;
    @FXML
    PasswordField reenterPasswordTextField;
    UsersDao ud = new UsersDao();


    public void closeSceneWindow() {
        Stage stage = (Stage) passwordTextField.getScene().getWindow();
        stage.close();
    }
    public int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
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
    public void storeCreationDate(Users user) throws IOException {
        LocalDate attemptDate = LocalDateTime.now().toLocalDate();
        Timestamp attemptTimestamp = Timestamp.valueOf(LocalDateTime.now());
        File file = new File(String.format("userLogInfo/%s_%d%d.txt", user.getUsername(), getRandomNumber(1, 100), getRandomNumber(1,100)));

        if(!file.exists()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(user + "\nCREATED ON: "+attemptDate + "\nTime Created: " + attemptTimestamp);
            bw.close();
        }else{
            System.out.println("User file already exists!");
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







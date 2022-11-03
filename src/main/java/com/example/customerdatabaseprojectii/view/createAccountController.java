package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.User_LoginDao;
import com.example.customerdatabaseprojectii.entity.User_Login;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.DbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class createAccountController {

    @FXML
    TextField firstNameTextField;
    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField passwordTextField;
    @FXML
    PasswordField reenterPasswordTextField;


    public boolean accountSubmitEvaluator(String fName, String uName, String pWord, String rePWord){
        if(fName == null || uName == null || pWord == null || rePWord == null){
            Alerter.informationAlert("Please check that the fields are all filled in!");
            throw new RuntimeException();
        }
        if(fName.length() > 30){
            Alerter.warningAlert("Firstname cannot be greater than 30 characters");
            return false;
        }
        if(uName.length() > 35){
            Alerter.warningAlert("Username cannot be greater than 35 characters");
            return false;
        }
        if(pWord.length() > 35){
            Alerter.warningAlert("Password cannot be greater than 35 characters");
            return false;
        }
        if(rePWord.length() > 35){
            Alerter.warningAlert("Password cannot be greater than 35 characters");
            return false;
        }
        if(!Objects.equals(pWord, rePWord)){
            Alerter.warningAlert("Make sure the password you entered matches the reentered password!");
            return false;
        }
    return true;
    }

    public void returnToLogin(ActionEvent event) throws IOException {
        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/Login.fxml", Main.getMainStage(), 495, 485, "Login");
    }

    public void accountSubmitted(ActionEvent event){
        String firstName = firstNameTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String reenterPassword = reenterPasswordTextField.getText();
        User_Login userLogin = new User_Login();
        User_LoginDao userDao = new User_LoginDao();
        if(accountSubmitEvaluator(firstName,username,password, reenterPassword)){
            userLogin.setFirstName(firstName);
            userLogin.setUserName(username);
            userLogin.setUserPassword(password);
            try {
                userDao.insertUserLoginIntoDB((userLogin));
                Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/Login.fxml", Main.getMainStage(), 495, 485, "Login");
            }catch (SQLException | IOException e){
                e.printStackTrace();
                System.out.println("User Not entered into the database successfully");
                Alerter.warningAlert("User was not successfully entered into the database, please try again!");
            }
        }
    }
}

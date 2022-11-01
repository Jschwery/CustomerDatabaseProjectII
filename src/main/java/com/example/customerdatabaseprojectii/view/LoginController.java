package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.User_LoginDao;
import com.example.customerdatabaseprojectii.entity.User_Login;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    Button loginButton;
    @FXML
    TextField usernameTextEntry;
    @FXML
    TextField passwordTextEntry;






    public void ButtonClick(ActionEvent event) {
        String username = usernameTextEntry.getText();
        String password = passwordTextEntry.getText();
        User_LoginDao loginDao = new User_LoginDao();
        User_Login loginUser = new User_Login();
        loginUser.setUserName(username);
        loginUser.setUserPassword(password);
        try {
            if (loginDao.accountVerified(loginUser)){
                Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/AccessGranted.fxml",
                        Main.getMainStage(), 450, 385,"Welcome + ");//get user name from database
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
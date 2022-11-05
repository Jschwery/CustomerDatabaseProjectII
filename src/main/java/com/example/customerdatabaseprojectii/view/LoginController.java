package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.User_LoginDao;
import com.example.customerdatabaseprojectii.entity.User_Login;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    Button loginButton;
    @FXML
    TextField usernameTextEntry;
    @FXML
    TextField passwordTextEntry;
    @FXML
    Label timeLabel;


    public void anotherButtonClickTest(ActionEvent event) throws IOException {
        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/AccessGranted.fxml",
                Main.getMainStage(), 450, 385,"Welcome + ");
    }

    public void createAccountLinkClicked(ActionEvent event) throws IOException {
        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/CreateUser.fxml",
                Main.getMainStage(), 450, 385,"Welcome + ");
    }

    public void detectAndTranslate(){
        ZoneId userTimeZone = ZoneId.systemDefault();


    }

    public void setTimeLabel() {
        ZoneId userTimeZone = ZoneId.systemDefault();
        timeLabel.setText(String.valueOf(userTimeZone));
    }

    public void ButtonClick(ActionEvent event) throws SQLException, IOException {
        String username = usernameTextEntry.getText();
        String password = passwordTextEntry.getText();
        User_LoginDao loginDao = new User_LoginDao();
        User_Login loginUser = new User_Login();
        loginUser.setUserName(username);
        loginUser.setUserPassword(password);
        try {
            if (loginDao.accountVerified(loginUser)){
                Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/AccessGranted.fxml",
                        Main.getMainStage(), 450, 385, String.format("Welcome %s!", Objects.requireNonNull(User_LoginDao.getLogin().getFirstName())));//get user name from database
            }

        }catch (Exception e) {
            if (loginDao.accountVerified(loginUser)) {
                Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/AccessGranted.fxml",
                        Main.getMainStage(), 450, 385, "Welcome!");
                        e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTimeLabel();

    }
}
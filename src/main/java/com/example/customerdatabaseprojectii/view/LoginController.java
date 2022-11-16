package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.UsersDao;
import com.example.customerdatabaseprojectii.entity.Appointments;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.Alerter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class LoginController implements Initializable {

    @FXML
    Button loginButton;
    @FXML
    TextField usernameTextEntry;
    @FXML
    TextField passwordTextEntry;
    @FXML
    Label timeLabel;
    @FXML
    Hyperlink createAccountLink;
    @FXML
    Button langSwitchButton;
    @FXML
    Label loginText;
    @FXML
    ToggleButton frenchToggle;

    private static Users currentlyLoggedIn = null;
    public static Users getCurrentlyLoggedInUser(){
        return currentlyLoggedIn;
    }

    public void translateLabels() {
        String userLang = System.getProperty("user.language");
        Locale frenchLocale = new Locale("fr", "FR");
        ResourceBundle frenchBundle = ResourceBundle.getBundle("Lang", frenchLocale);
        try {
            if (Objects.equals(userLang, "fr")) {
                Locale.setDefault(frenchLocale);
                loginButton.setText(frenchBundle.getString("loginButtonText"));
                usernameTextEntry.setPromptText(frenchBundle.getString("usernamePromptText"));
                passwordTextEntry.setPromptText(frenchBundle.getString("passwordPromptText"));
                createAccountLink.setText(frenchBundle.getString("createAccountHyperLink"));
                loginText.setText(frenchBundle.getString("loginText"));
                loginText.setLayoutX(130);
            }
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
    }

    public void anotherButtonClickTest(ActionEvent event) throws IOException {
        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/TableMenu.fxml",
                Main.getMainStage(), 450, 385, "Welcome + ");
    }

    public void createAccountLinkClicked(ActionEvent event) throws IOException {
        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/CreateUser.fxml",
                Main.getMainStage(), 450, 385, "Welcome + ");
    }

    public void setTimeLabel() {
        ZoneId userTimeZone = ZoneId.systemDefault();
        timeLabel.setText(String.valueOf(userTimeZone));
    }
    public void ButtonClick(ActionEvent event) throws SQLException, IOException {
        String username = usernameTextEntry.getText();
        String password = passwordTextEntry.getText();
        Users loginUser = new Users();
        loginUser.setUsername(username);
        loginUser.setPassword(password);
        currentlyLoggedIn = loginUser;
        try {
            if (UsersDao.verifyUserFromDB(loginUser)){
                if(changeLang || Objects.equals(System.getProperty("user.language"), "fr")){
                    Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/appointmentsMain.fxml",
                            Main.getMainStage(), 450, 385, String.format("Bienvenu %s!", Objects.requireNonNull(loginUser.getUsername())));
                }else{
                    Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/appointmentsMain.fxml",
                            Main.getMainStage(), 450, 385, String.format("Welcome %s!", Objects.requireNonNull(loginUser.getUsername())));
                }
            } else {
                String userLang = System.getProperty("user.language");
                if(Objects.equals(userLang, "fr") || frenchToggle.isSelected()){
                    Alerter.informationAlert("identifiant ou mot de passe incorrect");
                }else {
                    Alerter.informationAlert("Incorrect username or password!");
                }
            }
        } catch (Exception e) {
            if (UsersDao.verifyUserFromDB(loginUser)) {
                Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/appointmentsMain.fxml",
                        Main.getMainStage(), 450, 385, "Welcome!");
                e.printStackTrace();
            } else {
                Alerter.informationAlert("Incorrect username or password!");
            }
        }
    }
    public static boolean changeLang = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        translateLabels();
        setTimeLabel();
        Locale frenchLocale = new Locale("fr", "FR");
        ResourceBundle frenchBundle = ResourceBundle.getBundle("Lang", frenchLocale);
        frenchToggle.setOnAction(action -> {
            if (frenchToggle.isSelected()) {
                changeLang = true;
                Locale.setDefault(frenchLocale);
                frenchToggle.setText("English");
                loginButton.setText(frenchBundle.getString("loginButtonText"));
                usernameTextEntry.setPromptText(frenchBundle.getString("usernamePromptText"));
                passwordTextEntry.setPromptText(frenchBundle.getString("passwordPromptText"));
                createAccountLink.setText(frenchBundle.getString("createAccountHyperLink"));
                loginText.setText(frenchBundle.getString("loginText"));
                loginText.setLayoutX(130);

            } else {
                loginButton.setText("Login");
                usernameTextEntry.setPromptText("Username");
                passwordTextEntry.setPromptText("Password");
                createAccountLink.setText("Create Account");
                loginText.setText("Login");
                frenchToggle.setText("French");
                loginText.setLayoutX(162);
            }
        });
    }
}
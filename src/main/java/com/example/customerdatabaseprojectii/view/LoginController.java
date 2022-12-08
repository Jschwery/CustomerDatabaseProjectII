package com.example.customerdatabaseprojectii.view;

import com.example.customerdatabaseprojectii.Main;
import com.example.customerdatabaseprojectii.daos.UsersDao;
import com.example.customerdatabaseprojectii.entity.Users;
import com.example.customerdatabaseprojectii.util.Alerter;
import com.example.customerdatabaseprojectii.util.RelatedTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

    public LoginController() throws MalformedURLException {}
    private static Users currentlyLoggedIn = null;
    public static boolean changeLang = false;
    UsersDao ud = new UsersDao();
    public static Users getCurrentlyLoggedInUser(){
        return currentlyLoggedIn;
    }
    public final static Locale userLocale = Locale.getDefault();
    private final ResourceBundle userBundle = bundleGen("Lang");

    public static ResourceBundle bundleGen(String baseName) throws MalformedURLException {
    File file = new File(baseName + userLocale);
    URL[] urls = {file.toURI().toURL()};
    ClassLoader loader = new URLClassLoader(urls);
    return ResourceBundle.getBundle(baseName, Locale.getDefault(), loader);
    }


    /**
     * Translates the labels to French if the user language system property is set to French.
     */
    public void translateLabels() throws MalformedURLException {
            loginButton.setText(userBundle.getString("loginButtonText"));
            usernameTextEntry.setPromptText(userBundle.getString("usernamePromptText"));
            passwordTextEntry.setPromptText(userBundle.getString("passwordPromptText"));
            createAccountLink.setText(userBundle.getString("createAccountHyperLink"));
            loginText.setText(userBundle.getString("loginText"));
            if(Objects.equals(userBundle.getString("loginText"), "Connexion") || changeLang) {
                loginText.setLayoutX(130);
            }
    }

    /**
     *
     * @param userName username of the user being searched for
     * @return an Optional indicating that the user may or may not have been found
     * @throws SQLException
     */
    public Optional<Users> findUserByUsername(String userName) throws SQLException {
        return ud.getAllFromDB().stream().filter(u -> Objects.equals(u.getUsername().toUpperCase(), userName.toUpperCase())).findFirst();
    }

    /**
     * @param event create account clicked and the user is switched to the create account scene
     * @throws IOException
     */
    public void createAccountLinkClicked(ActionEvent event) throws IOException {
        Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/CreateUser.fxml",
                Main.getMainStage(), 450, 385, "Welcome", false);
    }

    /**
     * Sets the label for the users specific time Zone
     */
    public void setTimeLabel() {
        ZoneId userTimeZone = ZoneId.systemDefault();
        timeLabel.setText(String.valueOf(userTimeZone));
    }

    /**
     * @param event login clicked, verifies the username and password entered exists within the database
     *              and sets the currently logged-in user
     * @throws SQLException
     * @throws IOException
     */
    public void ButtonClick(ActionEvent event) throws SQLException, IOException {
        String username = usernameTextEntry.getText();
        String password = passwordTextEntry.getText();
        System.out.println("Loggedin user = " + findUserByUsername(username).orElse(null));

        currentlyLoggedIn = findUserByUsername(username).orElse(null);
        try {
            if (ud.verifyUserFromDB(currentlyLoggedIn) && Objects.equals(passwordTextEntry.getText(), currentlyLoggedIn.getPassword())) {
                BufferedWriter writer = new BufferedWriter(new FileWriter("userLogInfo/login_activity.txt", true));
                writer.write(String.format("Username: %s\nLog in attempt: %s\n\n", username, LocalDateTime.now()));
                writer.flush();
                writer.close();
                if (changeLang || Objects.equals(userBundle.getString("loginText"), "Connexion")) {
                    Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/appointmentsMain.fxml",
                            Main.getMainStage(), 450, 385, String.format("Bienvenu %s!", Objects.requireNonNull(currentlyLoggedIn.getUsername())), false);
                } else {
                    Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/appointmentsMain.fxml",
                            Main.getMainStage(), 450, 385, String.format("Welcome %s!", Objects.requireNonNull(currentlyLoggedIn.getUsername())), false);
                }
            } else {
                if (Objects.equals(userBundle.getString("loginText"), "Connexion") || frenchToggle.isSelected()) {
                    Alerter.informationAlert("identifiant ou mot de passe incorrect");
                    Main.playSound("src/main/resources/errorSound.wav");
                } else {
                    System.out.println("username");
                    System.out.println(currentlyLoggedIn.getUsername());
                    System.out.println("password");
                    System.out.println(currentlyLoggedIn.getPassword());
                    BufferedWriter writer = new BufferedWriter(new FileWriter("userLogInfo/login_activity.txt", true));
                    if (Objects.equals(findUserByUsername(username).orElse(null), null)) {
                        System.out.println(currentlyLoggedIn.getUsername()+ " "  + "Username: " + username);
                        writer.flush();
                        writer.close();
                        Main.playSound("src/main/resources/errorSound.wav");
                        Alerter.informationAlert(String.format("Username %s not found!", username));
                    } else if (Objects.equals(currentlyLoggedIn.getUsername(), username) &&
                            !Objects.equals(currentlyLoggedIn.getPassword(), password)) {
                        writer.write(String.format("""
                                        Username: '%s' failed to login on %s
                                        Expected username: %s
                                        Expected password: %s
                                        Entered username: %s
                                        Entered password: %s

                                        """,
                                currentlyLoggedIn.getUsername(), LocalDateTime.now(), currentlyLoggedIn.getUsername(),
                                currentlyLoggedIn.getPassword(), username, password));
                        writer.flush();
                        writer.close();
                        Main.playSound("src/main/resources/errorSound.wav");
                        Alerter.informationAlert("Incorrect username or password!");
                    }
                }
            }
        } catch (Exception e) {
            if (ud.verifyUserFromDB(currentlyLoggedIn) && Objects.equals(passwordTextEntry.getText(), currentlyLoggedIn.getPassword())) {
                Main.changeScene("src/main/java/com/example/customerdatabaseprojectii/view/appointmentsMain.fxml",
                        Main.getMainStage(), 450, 385, "Welcome!", false);
                e.printStackTrace();
            } else {
                Main.playSound("src/main/resources/errorSound.wav");
                Alerter.informationAlert("Incorrect username or password!");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale frenchLocale = new Locale("fr", "FR");
        ResourceBundle frenchBundle = ResourceBundle.getBundle("Lang", frenchLocale);
        try {translateLabels();} catch (MalformedURLException e) {e.printStackTrace();}
        setTimeLabel();

        frenchToggle.setOnAction(action -> {
            if (frenchToggle.isSelected()) {
                changeLang = true;
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
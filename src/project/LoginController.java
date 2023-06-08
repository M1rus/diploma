package project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginController {

    @FXML
    private Button cancelButton;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private Button loginButton;

    private String role;

    public void loginButtonOnAction(ActionEvent e) {
        if (!loginField.getText().isBlank() && !passwordField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setTextFill(Color.RED);
            loginMessageLabel.setText("Заповніть поля");
        }
    }

    public void cancelButtonOnAction(ActionEvent e) {
        Stage loginStage = (Stage) cancelButton.getScene().getWindow();
        loginStage.close();
    }

    public void validateLogin() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT UL.Password, LR.RoleName\n" +
                "FROM UserLogin UL\n" +
                "JOIN LoginRole LR ON UL.idLR = LR.idLoginRole\n" +
                "WHERE BINARY UL.Username = '"+ loginField.getText() +"'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet verifyLoginQueryResult = statement.executeQuery(verifyLogin);

            if (verifyLoginQueryResult.next()) {
                String storedHash = verifyLoginQueryResult.getString("Password");
                String enteredPassword = passwordField.getText();
                if (BCrypt.checkpw(enteredPassword, storedHash)) {
                    role = verifyLoginQueryResult.getString("RoleName");
                    toTheMainPage();
                } else {
                    loginMessageLabel.setTextFill(Color.RED);
                    loginMessageLabel.setText("Невірний логін, або пароль");
                }
            } else {
                loginMessageLabel.setTextFill(Color.RED);
                loginMessageLabel.setText("Невірний логін, або пароль");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRole() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        String userRole = "SELECT LR.RoleName\n" +
                "FROM UserLogin UL\n" +
                "JOIN LoginRole LR ON UL.idLR = LR.idLoginRole\n" +
                "WHERE BINARY UL.Username = '"+ loginField.getText() +"'";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet userRoleQueryResult = statement.executeQuery(userRole);
            if (userRoleQueryResult.next()) {
                role = userRoleQueryResult.getString("RoleName");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return role;
    }

    public void toTheMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainPageView.fxml"));
            Parent root = loader.load();
            MainPageController mainController = loader.getController();
            mainController.setRole(getRole());
            Stage mainStage = new Stage();
            mainStage.setTitle("Головна сторінка");
            mainStage.setScene(new Scene(root));
            mainStage.show();

            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
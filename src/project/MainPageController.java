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
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
public class MainPageController {

    private String role;
    @FXML
    private Button sgButton;
    @FXML
    private Button analiticsButton;
    @FXML
    private Button mechButton;
    @FXML
    private Button stockButton;
    @FXML
    private Button reportButton;
    @FXML
    private Button returnButton;
    @FXML
    private Button planButton;
    @FXML
    private Button usersButton;


    public MainPageController() {
    }

    public void setRole(String role) {
        this.role = role;
        roleCheck();
    }

    public void roleCheck() {
        switch (role) {
            case "Бухгалтер":
                usersButton.setVisible(false);
                sgButton.setDisable(true);
                mechButton.setDisable(true);
                stockButton.setDisable(true);
                analiticsButton.setDisable(true);
                break;
            case "Завсклад":
                usersButton.setVisible(false);
                mechButton.setDisable(true);
                reportButton.setDisable(true);
                analiticsButton.setDisable(true);
                break;
            case "Инженер":
                usersButton.setVisible(false);
                sgButton.setDisable(true);
                reportButton.setDisable(true);
                analiticsButton.setDisable(true);
                break;
            case "Агроном":
                usersButton.setVisible(false);
                mechButton.setDisable(true);
                reportButton.setDisable(true);
                analiticsButton.setDisable(true);
                break;
            default:
                break;
        }
    }

    public void sgButtonOnAction() {
        toTheSgPage();
    }
    public void stockButtonOnAction() {
        toTheStockPage();
    }

    public void mechButtonOnAction () {
        toTheMechPage();
    }
    public void reportButtonOnAction() {
        toTheReportPage();
    }
    public void planButtonOnAction() {
        toThePlanPage();
    }
    public void analiticsButtonOnAction() {
        toTheAnaliticsPage();
    }
    public void returnButtonOnAction() {
        toTheLoginPage();
    }
    public void usersButtonOnAction() {
        toTheUsersPage();
    }
    public void shareButtonOnAction() {
        toTheSharePage();
    }


    public void toTheSgPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/SgPage.fxml"));
            Parent root = loader.load();

            SgPageController sgPageController = loader.getController();
            sgPageController.setRole(role);

            Stage sgStage = new Stage();
            sgStage.setTitle("Продукція");
            sgStage.setScene(new Scene(root));
            sgStage.show();

            Stage mainStage = (Stage) sgButton.getScene().getWindow();
            mainStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toTheStockPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/StockView.fxml"));
            Parent root = loader.load();

            StockController stockController = loader.getController();
            stockController.setRole(role);

            Stage stockStage = new Stage();
            stockStage.setTitle("Склад");
            stockStage.setScene(new Scene(root));
            stockStage.show();

            Stage mainStage = (Stage) stockButton.getScene().getWindow();
            mainStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTheMechPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MechView.fxml"));
            Parent root = loader.load();

            MechController mechController = loader.getController();
            mechController.setRole(role);

            Stage mechStage = new Stage();
            mechStage.setTitle("Техніка");
            mechStage.setScene(new Scene(root));
            mechStage.show();

            Stage mainStage = (Stage) mechButton.getScene().getWindow();
            mainStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toTheReportPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ReportView.fxml"));
            Parent root = loader.load();
            ReportController reportController = loader.getController();
            reportController.setRole(role);

            Stage reportStage = new Stage();
            reportStage.setTitle("Отримання/витрати");
            reportStage.setScene(new Scene(root));
            reportStage.show();

            Stage mainStage = (Stage) reportButton.getScene().getWindow();
            mainStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toThePlanPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/PlanView.fxml"));
            Parent root = loader.load();
            PlanController planController = loader.getController();
            planController.setRole(role);

            Stage planStage = new Stage();
            planStage.setTitle("Планування заходів");
            planStage.setScene(new Scene(root));
            planStage.show();

            Stage mainStage = (Stage) planButton.getScene().getWindow();
            mainStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTheSharePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ShareView.fxml"));
            Parent root = loader.load();
            ShareController shareController = loader.getController();
            shareController.setRole(role);

            Stage shareStage = new Stage();
            shareStage.setTitle("Паї");
            shareStage.setScene(new Scene(root));
            shareStage.show();

            Stage mainStage = (Stage) analiticsButton.getScene().getWindow();
            mainStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTheAnaliticsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/AnaliticsView.fxml"));
            Parent root = loader.load();
            AnaliticsController analiticsController = loader.getController();
            analiticsController.setRole(role);

            Stage analiticsStage = new Stage();
            analiticsStage.setTitle("Аналітика");
            analiticsStage.setScene(new Scene(root));
            analiticsStage.show();

            Stage mainStage = (Stage) analiticsButton.getScene().getWindow();
            mainStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toTheLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/LoginView.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Авторизація");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            Stage mainStage = (Stage) returnButton.getScene().getWindow();
            mainStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTheUsersPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/UsersView.fxml"));
            Parent root = loader.load();
            Stage usersStage = new Stage();
            usersStage.setTitle("Користувачі");
            usersStage.setScene(new Scene(root));
            usersStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

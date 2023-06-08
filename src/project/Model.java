package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class Model extends Application {
    @Override
    public void start(Stage loginStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("view/LoginView.fxml"));
        loginStage.setTitle("Авторизація");
        loginStage.setScene(new Scene(root,610,420));
        loginStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
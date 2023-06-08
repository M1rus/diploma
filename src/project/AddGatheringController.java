package project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddGatheringController {
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField quantityField;
    @FXML
    private ComboBox productNameComboBar;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label errorLabel;
    @FXML
    private String formattedDate;

    @FXML
    private void initialize() {
        populateProductNameComboBar();
        dateConverter();
    }
    private void populateProductNameComboBar() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        try {
            String query = "SELECT Name FROM Production";
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            productNameComboBar.getItems().clear();

            while (resultSet.next()) {
                String productName = resultSet.getString("Name");
                productNameComboBar.getItems().add(productName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void dateConverter () {
        datePicker.setOnAction(event -> {
            LocalDate selectedDate = datePicker.getValue();
            formattedDate = selectedDate.format(DateTimeFormatter.ISO_DATE);
        });
    }

    public void addButtonOnAction () {
        if (formattedDate == null || productNameComboBar.getValue() == null || quantityField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Заповніть всі дані");
        } else {
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            try {
                String productName = (String) productNameComboBar.getValue();
                int quantity = Integer.parseInt(quantityField.getText());

                String updateQuery = "UPDATE ProductStock " +
                        "JOIN Production ON ProductStock.idProduct = Production.idProduction " +
                        "SET Quantity = Quantity + ? " +
                        "WHERE Production.Name = ?";

                PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);
                updateStatement.setFloat(1, quantity);
                updateStatement.setString(2, productName);
                updateStatement.executeUpdate();

                String insertQuery = "INSERT INTO ProductionGathering (idProduct, Quantity, Date) " +
                        "SELECT idProduction, ?, ? " +
                        "FROM Production " +
                        "WHERE Production.Name = ?";

                PreparedStatement insertStatement = connectDB.prepareStatement(insertQuery);
                insertStatement.setFloat(1, quantity);
                insertStatement.setString(2, formattedDate);
                insertStatement.setString(3, productName);
                insertStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void cancelButtonOnAction(ActionEvent e) {
        Stage thisStage = (Stage) cancelButton.getScene().getWindow();
        thisStage.close();
    }
}

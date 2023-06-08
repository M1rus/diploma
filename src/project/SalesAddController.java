package project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import project.DBConnection;

import java.sql.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SalesAddController {
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField customerInfo;
    @FXML
    private ComboBox productNameComboBar;
    @FXML
    private Label errorLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label availabilityLabel;
    private float availability;
    private float totalPrice;

    @FXML
    private void initialize() {
        populateProductNameComboBar();
    }

    private void populateProductNameComboBar() {
        // Отримати з'єднання з базою даних
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        try {
            // Виконати запит до бази даних
            String query = "SELECT Name FROM Production";
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Очистити ComboBox перед заповненням
            productNameComboBar.getItems().clear();

            // Додати значення до ComboBox
            while (resultSet.next()) {
                String productName = resultSet.getString("Name");
                productNameComboBar.getItems().add(productName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public float getAvailability() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String availabilityQuery = "SELECT ps.Quantity, ps.ReservedQuantity\n" +
                    "FROM Production p\n" +
                    "JOIN ProductStock ps ON p.idProduction = ps.idProduct\n" +
                    "WHERE p.Name = '" + productNameComboBar.getValue() + "';";
            Statement availabilityStatement = connectDB.createStatement();
            ResultSet availabilityQueryResult = availabilityStatement.executeQuery(availabilityQuery);
            if (availabilityQueryResult.next()) {
                availability = availabilityQueryResult.getFloat("Quantity") - availabilityQueryResult.getFloat("ReservedQuantity");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return availability;
    }

    public float getTotalPrice() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String totalPriceQuery = "SELECT Price\n" +
                    "FROM Production\n" +
                    "WHERE Name = '" + productNameComboBar.getValue() + "';";
            Statement totalPriceStatement = connectDB.createStatement();
            ResultSet totalPriceQueryResult = totalPriceStatement.executeQuery(totalPriceQuery);
            if (totalPriceQueryResult.next()) {
                float quantity = Float.parseFloat(quantityField.getText());
                totalPrice = quantity * totalPriceQueryResult.getFloat("Price"); // Присваиваем значение полю role
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalPrice;
    }

    public void totalPriceButtonOnAction() {
        if (productNameComboBar.getValue() == null || quantityField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Оберіть товар та кількість");
        } else {
            getTotalPrice();
            totalPriceLabel.setText(Float.toString(totalPrice));

        }
    }

    public void checkAvailabilityButtonOnAction() {
        if (productNameComboBar.getValue() == null) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Оберіть товар");
        } else {
            getAvailability();
            availabilityLabel.setText(Float.toString(availability));
        }
    }

    public void addButtonOnAction() {
        getAvailability();
        getTotalPrice();
        float quantity = Float.parseFloat(quantityField.getText());
        if (quantity > availability) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Нема стіки");
        }
        else if (productNameComboBar.getValue() == null || quantityField.getText().isBlank() || customerInfo.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Заповніть всі дані");
        }
        else {
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Додано");
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            try {
                String inputCustomerInfo = customerInfo.getText();
                String status = "Попередньо";
                String productName = (String) productNameComboBar.getValue();
                String insertQuery = "INSERT INTO ProductionSales (CustomerInfo, idProductToSale, Quantity, Status, TotalPrice)\n" +
                        "SELECT ?, idProduction, ?, ?, ?\n" +
                        "FROM Production\n" +
                        "WHERE Name = ?;";


                PreparedStatement insertStatement = connectDB.prepareStatement(insertQuery);
                insertStatement.setString(1, inputCustomerInfo);
                insertStatement.setFloat(2, quantity);
                insertStatement.setString(3, status);
                insertStatement.setFloat(4, totalPrice);
                insertStatement.setString(5, productName);
                insertStatement.executeUpdate();

                String updateQuery = "UPDATE ProductStock " +
                        "JOIN Production ON ProductStock.idProduct = Production.idProduction " +
                        "SET ReservedQuantity = ReservedQuantity + ? " +
                        "WHERE Production.Name = ?";

                PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);
                updateStatement.setFloat(1, quantity);
                updateStatement.setString(2, productName);
                updateStatement.executeUpdate();
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

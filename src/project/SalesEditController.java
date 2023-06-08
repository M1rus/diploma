package project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class SalesEditController {
    private String orderStatus;
    private String orderCustomerInfo;
    private String orderQuantity;
    private String productToSale;
    private float totalPrice;
    private float availability;
    @FXML
    private Label errorLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label availabilityLabel;
    @FXML
    private ComboBox orderStatusComboBox;
    @FXML
    private ComboBox productNameComboBar;
    @FXML
    private Button editButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField orderIdTextField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField customerInfoField;
    @FXML
    private void initialize() {
        populateProductNameComboBar();
        orderStatusComboBox.getItems().addAll("Попередньо", "В процесі", "Завершено");
        productNameComboBar.setDisable(true);
        customerInfoField.setDisable(true);
        quantityField.setDisable(true);
        orderStatusComboBox.setDisable(true);
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

    public boolean checkIfOrderExists() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        int orderId = Integer.parseInt(orderIdTextField.getText());
        try {
            String existQuery = "SELECT idProductionSales FROM ProductionSales WHERE idProductionSales = ?";
            PreparedStatement existStatement = connectDB.prepareStatement(existQuery);
            existStatement.setInt(1, orderId);
            ResultSet resultSet = existStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getOrderCustomerInfo() {
        int orderId = Integer.parseInt(orderIdTextField.getText());
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String orderCustomerInfoQuery = "SELECT CustomerInfo\n" +
                    "FROM ProductionSales\n" +
                    "WHERE idProductionSales = '" + orderId + "';";
            Statement orderCustomerInfoStatement = connectDB.createStatement();
            ResultSet orderCustomerInfoQueryResult = orderCustomerInfoStatement.executeQuery(orderCustomerInfoQuery);
            if (orderCustomerInfoQueryResult.next()) {
                orderCustomerInfo = orderCustomerInfoQueryResult.getString("CustomerInfo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderCustomerInfo;
    }

    private String getProductToSaleName () {
        int orderId = Integer.parseInt(orderIdTextField.getText());
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String productToSaleQuery = "SELECT p.Name\n" +
                    "FROM ProductionSales ps\n" +
                    "JOIN Production p ON ps.idProductToSale = p.idProduction\n" +
                    "WHERE ps.idProductionSales = '" + orderId + "';";
            Statement productToSaleStatement = connectDB.createStatement();
            ResultSet productToSaleQueryResult = productToSaleStatement.executeQuery(productToSaleQuery);
            if (productToSaleQueryResult.next()) {
                productToSale = productToSaleQueryResult.getString("Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productToSale;
    }

    private String getOrderQuantity () {
        int orderId = Integer.parseInt(orderIdTextField.getText());
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String orderQuantityQuery = "SELECT Quantity\n" +
                    "FROM ProductionSales\n" +
                    "WHERE idProductionSales = '" + orderId + "';";
            Statement orderQuantityStatement = connectDB.createStatement();
            ResultSet orderQuantityQueryResult = orderQuantityStatement.executeQuery(orderQuantityQuery);
            if (orderQuantityQueryResult.next()) {
                orderQuantity = Float.toString(orderQuantityQueryResult.getFloat("Quantity"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderQuantity;
    }
    private String getOrderStatus () {
        int orderId = Integer.parseInt(orderIdTextField.getText());
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String orderStatusQuery = "SELECT Status\n" +
                    "FROM ProductionSales\n" +
                    "WHERE idProductionSales = '" + orderId + "';";
            Statement orderStatusStatement = connectDB.createStatement();
            ResultSet orderStatusQueryResult = orderStatusStatement.executeQuery(orderStatusQuery);
            if (orderStatusQueryResult.next()) {
                orderStatus = orderStatusQueryResult.getString("Status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderStatus;
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
                availability = availabilityQueryResult.getFloat("Quantity") - availabilityQueryResult.getFloat("ReservedQuantity") + Float.parseFloat(quantityField.getText());
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
            availabilityLabel.setText(Float.toString(availability));
        }
    }

    public void showOrderInfoButtonOnAction() {
        checkIfOrderExists();
        boolean exists = checkIfOrderExists();
        if (orderIdTextField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть номер замолвення");
        }
        else if (exists == false) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Замовлення не існує");
        }
        else {
            getOrderCustomerInfo();
            getProductToSaleName();
            getOrderQuantity();
            getOrderStatus();
            customerInfoField.setText(orderCustomerInfo);
            productNameComboBar.setValue(productToSale);
            quantityField.setText(orderQuantity);
            orderStatusComboBox.setValue(orderStatus);
            productNameComboBar.setDisable(false);
            orderIdTextField.setDisable(false);
            quantityField.setDisable(false);
            orderStatusComboBox.setDisable(false);
            getAvailability();
            if (orderStatusComboBox.getValue() == "Попередньо") {
                productNameComboBar.setDisable(true);
            }
            else if (orderStatusComboBox.getValue() == "В процесі") {
                productNameComboBar.setDisable(true);
                customerInfoField.setDisable(true);
                quantityField.setDisable(true);
            }
            else if (orderStatusComboBox.getValue() == "Завершено") {
                productNameComboBar.setDisable(true);
                customerInfoField.setDisable(true);
                quantityField.setDisable(true);
                orderStatusComboBox.setDisable(true);
            }
        }
    }

    public void editButtonOnAction() {
        getTotalPrice();
        float quantity = Float.parseFloat(quantityField.getText());
        if (quantity > availability) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Нема стіки");
        }
        else if (productNameComboBar.getValue() == null || quantityField.getText().isBlank() || customerInfoField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Заповніть всі дані");
        }
        else {
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Додано");
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            if (orderStatusComboBox.getValue() == "В процесі" || orderStatusComboBox.getValue() == "Попередньо") {
                try {
                    int orderId = Integer.parseInt(orderIdTextField.getText());
                    String inputCustomerInfo = customerInfoField.getText();
                    String status = (String) orderStatusComboBox.getValue();
                    String productName = (String) productNameComboBar.getValue();
                    String updateSaleQuery = "UPDATE ProductionSales\n" +
                            "SET CustomerInfo = ?, idProductToSale = (SELECT idProduction FROM Production WHERE Name = ?), Quantity = ?, Status = ?, TotalPrice = ?\n" +
                            "WHERE idProductionSales = ?;";


                    PreparedStatement updateSaleStatement = connectDB.prepareStatement(updateSaleQuery);
                    updateSaleStatement.setString(1, inputCustomerInfo);
                    updateSaleStatement.setString(2, productName);
                    updateSaleStatement.setFloat(3, quantity);
                    updateSaleStatement.setString(4, status);
                    updateSaleStatement.setFloat(5, totalPrice);
                    updateSaleStatement.setInt(6, orderId);
                    updateSaleStatement.executeUpdate();

                    String updateQuery = "UPDATE ProductStock " +
                            "JOIN Production ON ProductStock.idProduct = Production.idProduction " +
                            "SET ReservedQuantity = ReservedQuantity - (" + orderQuantity + " - ?) " +
                            "WHERE Production.Name = ?";

                    PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);
                    updateStatement.setFloat(1, quantity);
                    updateStatement.setString(2, productName);
                    updateStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (orderStatusComboBox.getValue() == "Завершено") {
                try {
                    int orderId = Integer.parseInt(orderIdTextField.getText());
                    LocalDate currentDate = LocalDate.now();
                    String inputCustomerInfo = customerInfoField.getText();
                    String status = (String) orderStatusComboBox.getValue();
                    String productName = (String) productNameComboBar.getValue();
                    String updateSaleQuery = "UPDATE ProductionSales\n" +
                            "SET CustomerInfo = ?, idProductToSale = (SELECT idProduction FROM Production WHERE Name = ?), Quantity = ?, Status = ?, TotalPrice = ?, DateOfCompletion = ?\n" +
                            "WHERE idProductionSales = ?;";


                    PreparedStatement updateSaleStatement = connectDB.prepareStatement(updateSaleQuery);
                    updateSaleStatement.setString(1, inputCustomerInfo);
                    updateSaleStatement.setString(2, productName);
                    updateSaleStatement.setFloat(3, quantity);
                    updateSaleStatement.setString(4, status);
                    updateSaleStatement.setFloat(5, totalPrice);
                    updateSaleStatement.setObject(6, currentDate);
                    updateSaleStatement.setInt(7, orderId);
                    updateSaleStatement.executeUpdate();

                    String updateQuery = "UPDATE ProductStock " +
                            "JOIN Production ON ProductStock.idProduct = Production.idProduction " +
                            "SET ReservedQuantity = ReservedQuantity - " + quantity + ", Quantity = Quantity - " + quantity + " " +
                            "WHERE Production.Name = ?";

                    PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);
                    updateStatement.setString(1, productName);
                    updateStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void deleteButtonOnAction () {
        if (orderIdTextField.getText().isBlank()) {
            errorLabel.setText("Введіть номер замовлення");
        } else {
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            if (orderStatusComboBox.getValue() == "В процесі" || orderStatusComboBox.getValue() == "Попередньо") {
                try {
                    int orderId = Integer.parseInt(orderIdTextField.getText());
                    String deleteSaleQuery = "DELETE FROM ProductionSales\n" +
                            "WHERE idProductionSales = ?;";
                    PreparedStatement deleteSaleStatement = connectDB.prepareStatement(deleteSaleQuery);
                    deleteSaleStatement.setInt(1, orderId);
                    deleteSaleStatement.executeUpdate();
                    String updateQuery = "UPDATE ProductStock " +
                            "JOIN Production ON ProductStock.idProduct = Production.idProduction " +
                            "SET ReservedQuantity = ReservedQuantity - " + orderQuantity + " " +
                            "WHERE Production.Name = ?";

                    PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);
                    updateStatement.setString(1, productToSale);
                    updateStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (orderStatusComboBox.getValue() == "Завершено") {
                try {
                    int orderId = Integer.parseInt(orderIdTextField.getText());
                    String deleteSaleQuery = "DELETE FROM ProductionSales\n" +
                            "WHERE idProductionSales = ?;";
                    PreparedStatement deleteSaleStatement = connectDB.prepareStatement(deleteSaleQuery);
                    deleteSaleStatement.setInt(1, orderId);
                    deleteSaleStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void cancelButtonOnAction(ActionEvent e) {
        Stage thisStage = (Stage) cancelButton.getScene().getWindow();
        thisStage.close();
    }
}

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

public class StockRequestEditController {
    private String description;
    private String type;
    private String requestName;
    private String requestQuantity;

    private String totalPrice;
    private String requestStatus;
    @FXML
    private Label errorLabel;
    @FXML
    private ComboBox typeComboBox;
    @FXML
    private ComboBox requestStatusComboBox;
    @FXML
    private Button removeButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button editButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField requestIdField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField totalPriceField;
    @FXML
    private void initialize() {
        populateTypeComboBox();
        requestStatusComboBox.getItems().addAll("Додано", "В процесі", "Завершено");
    }

    private void populateTypeComboBox() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        try {
            String query = "SELECT TypeName FROM ConsumableType";
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            typeComboBox.getItems().clear();
            while (resultSet.next()) {
                String typeName = resultSet.getString("typeName");
                typeComboBox.getItems().add(typeName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfRequestExists() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        int requestId = Integer.parseInt(requestIdField.getText());
        try {
            String existQuery = "SELECT idConsumableRequest FROM ConsumableRequest WHERE idConsumableRequest = ?";
            PreparedStatement existStatement = connectDB.prepareStatement(existQuery);
            existStatement.setInt(1, requestId);
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

    private String getRequestQuantity () {
        int requestId = Integer.parseInt(requestIdField.getText());
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String requestQuantityQuery = "SELECT Quantity\n" +
                    "FROM ConsumableRequest\n" +
                    "WHERE idConsumableRequest = '" + requestId + "';";
            Statement requestQuantityStatement = connectDB.createStatement();
            ResultSet requestQuantityQueryResult = requestQuantityStatement.executeQuery(requestQuantityQuery);
            if (requestQuantityQueryResult.next()) {
                requestQuantity = Float.toString(requestQuantityQueryResult.getFloat("Quantity"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestQuantity;
    }

    private String getRequestConsName () {
        int requestId = Integer.parseInt(requestIdField.getText());
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String nameQuery = "SELECT сs.Name\n" +
                    "FROM ConsumableStock сs \n" +
                    "JOIN ConsumableRequest cr ON cr.idCons = idConsumable \n" +
                    "WHERE cr.idConsumableRequest = " + requestId + ";";
            Statement nameStatement = connectDB.createStatement();
            ResultSet nameQueryResult = nameStatement.executeQuery(nameQuery);
            if (nameQueryResult.next()) {
                requestName = nameQueryResult.getString("Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestName;
    }
    private String getDescription () {
        int requestId = Integer.parseInt(requestIdField.getText());
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String descriptionQuery = "SELECT cs.Description\n" +
                    "FROM ConsumableRequest cr\n" +
                    "JOIN ConsumableStock cs ON cr.idCons = cs.idConsumable\n" +
                    "WHERE cr.idConsumableRequest = "+ requestId +";";
            Statement descriptionStatement = connectDB.createStatement();
            ResultSet descriptionQueryResult = descriptionStatement.executeQuery(descriptionQuery);
            if (descriptionQueryResult.next()) {
                description = descriptionQueryResult.getString("Description");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return description;
    }

    public String getType() {
        int requestId = Integer.parseInt(requestIdField.getText());
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String typeQuery = "SELECT ct.TypeName\n" +
                    "FROM ConsumableRequest cr\n" +
                    "JOIN ConsumableStock cs ON cr.idCons = cs.idConsumable\n" +
                    "JOIN ConsumableType ct ON cs.idConsType = ct.idConsumableType\n" +
                    "WHERE cr.idConsumableRequest = "+ requestId +";";
            Statement typeStatement = connectDB.createStatement();
            ResultSet typeQueryResult = typeStatement.executeQuery(typeQuery);
            if (typeQueryResult.next()) {
                type = typeQueryResult.getString("TypeName");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }
    private String getRequestStatus () {
        int requestId = Integer.parseInt(requestIdField.getText());
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String requestStatusQuery = "SELECT Status\n" +
                    "FROM ConsumableRequest\n" +
                    "WHERE idConsumableRequest = '" + requestId + "';";
            Statement requestStatusStatement = connectDB.createStatement();
            ResultSet requestStatusQueryResult = requestStatusStatement.executeQuery(requestStatusQuery);
            if (requestStatusQueryResult.next()) {
                requestStatus = requestStatusQueryResult.getString("Status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestStatus;
    }
    private String getRequestTotalPrice() {
        int requestId = Integer.parseInt(requestIdField.getText());
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String requestTotalPriceQuery = "SELECT TotalPrice\n" +
                    "FROM ConsumableRequest\n" +
                    "WHERE idConsumableRequest = '" + requestId + "';";
            Statement requestTotalPriceStatement = connectDB.createStatement();
            ResultSet requestTotalPriceQueryResult = requestTotalPriceStatement.executeQuery(requestTotalPriceQuery);
            if (requestTotalPriceQueryResult.next()) {
                totalPrice = Float.toString(requestTotalPriceQueryResult.getFloat("TotalPrice"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalPrice;
    }

    public void checkRequestButtonOnAction() {
        boolean exists = checkIfRequestExists();
        if (requestIdField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть номер");
        }
        else if (exists == false) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Запиту не існує");
            typeComboBox.setDisable(false);
            descriptionField.setDisable(false);
        }
        else if (exists){
            checkIfRequestExists();
            getType();
            getDescription();
            getRequestQuantity();
            getRequestStatus();
            getRequestConsName();
            getRequestTotalPrice();
            requestStatusComboBox.setValue(requestStatus);
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Існує");
            typeComboBox.setValue(type);
            descriptionField.setText(description);
            nameField.setText(requestName);
            quantityField.setText(requestQuantity);
            typeComboBox.setDisable(true);
            descriptionField.setDisable(true);
            nameField.setDisable(true);
            quantityField.setDisable(true);
            if (requestStatusComboBox.getValue() == "Додано") {
                requestStatusComboBox.getItems().clear();
                requestStatusComboBox.getItems().addAll("В процесі", "Завершено");
                requestStatusComboBox.setValue(requestStatus);
                requestStatusComboBox.setDisable(false);
                removeButton.setDisable(false);
                editButton.setDisable(false);
                totalPriceField.setDisable(false);
            } else if (requestStatusComboBox.getValue() == "В процесі") {
                requestStatusComboBox.getItems().clear();
                requestStatusComboBox.getItems().addAll( "Завершено");
                requestStatusComboBox.setValue(requestStatus);
                requestStatusComboBox.setDisable(false);
                removeButton.setDisable(false);
                editButton.setDisable(false);
                totalPriceField.setText(totalPrice);
                totalPriceField.setDisable(false);
            } else if (requestStatusComboBox.getValue() == "Завершено") {
                requestStatusComboBox.setValue(requestStatus);
                requestStatusComboBox.setDisable(true);
                removeButton.setDisable(true);
                editButton.setDisable(true);
                totalPriceField.setText(totalPrice);
                totalPriceField.setDisable(true);
            }


        }
    }

    public void editButtonOnAction() {
        if (totalPriceField.getText().isBlank() || requestStatusComboBox.getValue() == null) {
            errorLabel.setText("Заповніть всі дані");
            errorLabel.setTextFill(Color.RED);
        } else {
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            int requestId = Integer.parseInt(requestIdField.getText());
            String status = (String) requestStatusComboBox.getValue();
            float totalPrice = Float.parseFloat(totalPriceField.getText());
            if (requestStatusComboBox.getValue().equals("В процесі")) {
                try {
                    String editQuery = "UPDATE ConsumableRequest\n" +
                            "SET Status = '" + status + "', TotalPrice = " + totalPrice + "\n" +
                            "WHERE idConsumableRequest = " + requestId + ";";
                    PreparedStatement editStatement = connectDB.prepareStatement(editQuery);
                    editStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (requestStatusComboBox.getValue().equals("Завершено")) {
                try {
                    float quantity = Float.parseFloat(quantityField.getText());
                    LocalDate currentDate = LocalDate.now();
                    String editQuery = "UPDATE ConsumableRequest\n" +
                            "SET Status = '" + status + "', TotalPrice = "+ totalPrice +" , DateOfCompletion = ? \n" +
                            "WHERE idConsumableRequest = " + requestId + ";";
                    PreparedStatement editStatement = connectDB.prepareStatement(editQuery);
                    editStatement.setObject(1, currentDate);
                    editStatement.executeUpdate();
                    String updateQuery = "UPDATE ConsumableStock\n" +
                            "SET Quantity = Quantity + "+ quantity +"\n" +
                            "WHERE Name = '" + nameField.getText() + "';";
                    PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);
                    updateStatement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void removeButtonOnAction () {
        if (requestIdField.getText().isBlank()) {
            errorLabel.setText("Введіть номер замовлення");
        } else {
            int requestId = Integer.parseInt(requestIdField.getText());
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            try {
                String deleteQuery = "DELETE FROM ConsumableRequest\n" +
                        "WHERE idConsumableRequest = "+ requestId +";";
                PreparedStatement deleteStatement = connectDB.prepareStatement(deleteQuery);
                deleteStatement.executeUpdate();
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

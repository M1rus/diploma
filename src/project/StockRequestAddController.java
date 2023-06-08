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

public class StockRequestAddController {
    private String description;
    private String type;
    @FXML
    private Label errorLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox typeComboBox;
    @FXML
    private TextField nameField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField descriptionField;
    @FXML
    private void initialize() {
        populateTypeComboBox();
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

    public boolean checkIfConsumableExists() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        String name = nameField.getText();
        try {
            String existQuery = "SELECT Name FROM ConsumableStock WHERE Name = ?";
            PreparedStatement existStatement = connectDB.prepareStatement(existQuery);
            existStatement.setString(1, name);
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
    private String getDescription () {
        String name = nameField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String descriptionQuery = "SELECT Description\n" +
                    "FROM ConsumableStock\n" +
                    "WHERE Name = '" + name + "';";
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
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String typeQuery = "SELECT сt.TypeName\n" +
                    "FROM ConsumableType сt \n" +
                    "JOIN ConsumableStock cs ON cs.idConsType = idConsumableType \n" +
                    "WHERE cs.Name = '" + nameField.getText() + "';";
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

    public void checkConsumableButtonOnAction() {
        checkIfConsumableExists();
        boolean exists = checkIfConsumableExists();
        if (nameField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть назву");
        }
        else if (exists == false) {
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Новий розхідник");
            typeComboBox.setDisable(false);
            descriptionField.setDisable(false);
        }
        else if (exists){
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Існує");
            getType();
            getDescription();
            typeComboBox.setValue(type);
            descriptionField.setText(description);
            typeComboBox.setDisable(true);
            descriptionField.setDisable(true);
        }
    }

    public void addButtonOnAction() {
        if (typeComboBox.getValue() == null || quantityField.getText().isBlank() || nameField.getText().isBlank() || descriptionField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Заповніть всі дані");
        } else {
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            checkIfConsumableExists();
            String description = descriptionField.getText();
            String type = (String) typeComboBox.getValue();
            String name = nameField.getText();
            String status = "Додано";
            float quantity = Float.parseFloat(quantityField.getText());
            boolean exists = checkIfConsumableExists();
            if (exists) {
                try {
                    String insertQuery = "INSERT INTO ConsumableRequest (idCons, Quantity, Status)\n" +
                            "SELECT cs.idConsumable, ?, ?\n" +
                            "FROM ConsumableStock cs\n" +
                            "JOIN ConsumableType ct ON cs.idConsType = ct.idConsumableType\n" +
                            "WHERE cs.Name = ?\n" +
                            "AND ct.TypeName = ?\n" +
                            "AND cs.Description = ?\n" +
                            "LIMIT 1;";
                    PreparedStatement insertStatement = connectDB.prepareStatement(insertQuery);
                    insertStatement.setFloat(1, quantity);
                    insertStatement.setString(2, status);
                    insertStatement.setString(3, name);
                    insertStatement.setString(4, type);
                    insertStatement.setString(5, description);
                    insertStatement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (!exists) {
                try {
                    String insertConsumableStockQuery = "INSERT INTO ConsumableStock (Name, idConsType, Description, Quantity) " +
                            "SELECT ?, ct.idConsumableType, ?, ? " +
                            "FROM ConsumableType ct " +
                            "WHERE ct.TypeName = ?";
                    PreparedStatement insertConsumableStockStatement = connectDB.prepareStatement(insertConsumableStockQuery);
                    insertConsumableStockStatement.setString(1, nameField.getText());
                    insertConsumableStockStatement.setString(2, descriptionField.getText());
                    insertConsumableStockStatement.setFloat(3, Float.parseFloat(quantityField.getText()));
                    insertConsumableStockStatement.setString(4, typeComboBox.getValue().toString());

                    insertConsumableStockStatement.executeUpdate();

                    String insertQuery = "INSERT INTO ConsumableRequest (idCons, Quantity, Status)\n" +
                            "SELECT cs.idConsumable, ?, ?\n" +
                            "FROM ConsumableStock cs\n" +
                            "JOIN ConsumableType ct ON cs.idConsType = ct.idConsumableType\n" +
                            "WHERE cs.Name = ?\n" +
                            "AND ct.TypeName = ?\n" +
                            "AND cs.Description = ?\n" +
                            "LIMIT 1;";
                    PreparedStatement insertStatement = connectDB.prepareStatement(insertQuery);
                    insertStatement.setFloat(1, quantity);
                    insertStatement.setString(2, status);
                    insertStatement.setString(3, name);
                    insertStatement.setString(4, type);
                    insertStatement.setString(5, description);
                    insertStatement.executeUpdate();

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

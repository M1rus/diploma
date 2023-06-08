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

public class MechAddRemoveController {
    private String description;
    private String type;
    private String name;
    private String status;
    private String ownership;
    @FXML
    private Label errorLabel;
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox typeComboBox;
    @FXML
    private ComboBox statusComboBox;
    @FXML
    private ComboBox ownershipComboBox;
    @FXML
    private TextField nameField;
    @FXML
    private TextField bodyNumField;
    @FXML
    private TextField descriptionField;
    @FXML
    private void initialize() {
        populateTypeComboBox();
        statusComboBox.getItems().addAll("Доступна", "В ремонті", "Зайнята");
        ownershipComboBox.getItems().addAll("Арендована", "Власна");
    }

    private void populateTypeComboBox() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        try {
            String query = "SELECT TypeName FROM MechType";
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

    public boolean checkIfMechExists() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        String bodyNum = bodyNumField.getText();
        try {
            String existQuery = "SELECT BodyNum FROM MechList WHERE BodyNum = ?";
            PreparedStatement existStatement = connectDB.prepareStatement(existQuery);
            existStatement.setString(1, bodyNum);
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

    private String getName () {
        String bodyNum = bodyNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String descriptionQuery = "SELECT Name\n" +
                    "FROM MechList\n" +
                    "WHERE BodyNum = '" + bodyNum + "';";
            Statement descriptionStatement = connectDB.createStatement();
            ResultSet descriptionQueryResult = descriptionStatement.executeQuery(descriptionQuery);
            if (descriptionQueryResult.next()) {
                name = descriptionQueryResult.getString("Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    private String getType() {
        String bodyNum = bodyNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String typeQuery = "SELECT mt.TypeName\n" +
                    "FROM MechType mt \n" +
                    "JOIN MechList ml ON ml.idMT = idMechType \n" +
                    "WHERE ml.BodyNum = '" + bodyNum + "';";
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

    private String getStatus() {
        String bodyNum = bodyNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String statusQuery = "SELECT Status\n" +
                    "FROM MechList\n" +
                    "WHERE BodyNum = '" + bodyNum + "';";
            Statement statusStatement = connectDB.createStatement();
            ResultSet statusQueryResult = statusStatement.executeQuery(statusQuery);
            if (statusQueryResult.next()) {
                status = statusQueryResult.getString("Status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }


    private String getDescription () {
        String bodyNum = bodyNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String descriptionQuery = "SELECT Description\n" +
                    "FROM MechList\n" +
                    "WHERE BodyNum = '" + bodyNum + "';";
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

    private String getOwnership () {
        String bodyNum = bodyNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String ownershipQuery = "SELECT Ownership\n" +
                    "FROM MechList\n" +
                    "WHERE BodyNum = '" + bodyNum + "';";
            Statement ownershipStatement = connectDB.createStatement();
            ResultSet ownershipQueryResult = ownershipStatement.executeQuery(ownershipQuery);
            if (ownershipQueryResult.next()) {
                ownership = ownershipQueryResult.getString("Ownership");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ownership;
    }


    public void checkButtonOnAction() {
        checkIfMechExists();
        boolean exists = checkIfMechExists();
        if (bodyNumField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть номер кузову");
        }
        else if (exists == false) {
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Нова техніка");
            typeComboBox.setDisable(false);
            descriptionField.setDisable(false);
            ownershipComboBox.setDisable(false);
            addButton.setDisable(false);
            nameField.setDisable(false);
            bodyNumField.setDisable(false);
        }
        else if (exists){
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Існує");
            typeComboBox.setDisable(true);
            descriptionField.setDisable(true);
            nameField.setDisable(true);
            bodyNumField.setDisable(true);
            getName();
            getType();
            getStatus();
            getDescription();
            getOwnership();
            nameField.setText(name);
            typeComboBox.setValue(type);
            statusComboBox.setValue(status);
            descriptionField.setText(description);
            ownershipComboBox.setValue(ownership);
            addButton.setDisable(true);
        }
    }

    public void abortButtonOnAction() {
        typeComboBox.setDisable(false);
        descriptionField.setDisable(false);
        ownershipComboBox.setDisable(false);
        addButton.setDisable(false);
        nameField.setDisable(false);
        bodyNumField.setDisable(false);
        addButton.setDisable(false);
    }

    public void addButtonOnAction() {
        if (typeComboBox.getValue() == null || statusComboBox.getValue() == null || nameField.getText().isBlank() || descriptionField.getText().isBlank() || ownershipComboBox.getValue() == null || bodyNumField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Заповніть всі дані");
        } else {
            checkIfMechExists();
            boolean exists = checkIfMechExists();
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            if (exists) {
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText("Техніка існує");
            } else if (!exists) {
                try {
                    String bodynum = bodyNumField.getText();
                    String mechInsertQuery = "INSERT INTO MechList (Name, IdMT, Status, Description, BodyNum, Ownership)\n" +
                            "SELECT ?, MT.idMechType, ?, ?, '"+ bodynum +"', ?\n" +
                            "FROM MechType MT\n" +
                            "WHERE MT.TypeName = ?;";
                    PreparedStatement mechInsertStatement = connectDB.prepareStatement(mechInsertQuery);
                    mechInsertStatement.setString(1, nameField.getText());
                    mechInsertStatement.setString(2, (String) statusComboBox.getValue());
                    mechInsertStatement.setString(3, descriptionField.getText());
                    mechInsertStatement.setString(4, (String) ownershipComboBox.getValue());
                    mechInsertStatement.setString(5, (String) typeComboBox.getValue());
                    mechInsertStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeButtonOnAction() {
        if (bodyNumField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть номер кузову");
        } else {
            checkIfMechExists();
            boolean exists = checkIfMechExists();
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            if (exists) {
                try {
                    String mechDeleteQuery = "DELETE FROM MechList WHERE BodyNum = ?";
                    PreparedStatement mechDeleteStatement = connectDB.prepareStatement(mechDeleteQuery);
                    mechDeleteStatement.setString(1, bodyNumField.getText());
                    mechDeleteStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText("Не існує");
            }
        }
    }
    public void editButtonOnAction() {
        if (statusComboBox.getValue() == null || ownershipComboBox.getValue() == null) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть дані");
        }
        checkIfMechExists();
        boolean exists = checkIfMechExists();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        if (exists) {
            try {
                String mechEditQuery = "UPDATE MechList SET Status = ?, Ownership = ? WHERE BodyNum = ?";
                PreparedStatement mechEditStatement = connectDB.prepareStatement(mechEditQuery);
                mechEditStatement.setString(1, (String) statusComboBox.getValue());
                mechEditStatement.setString(2, (String) ownershipComboBox.getValue());
                mechEditStatement.setString(3, bodyNumField.getText());
                mechEditStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Не існує");
        }
    }
    public void cancelButtonOnAction(ActionEvent e) {
        Stage thisStage = (Stage) cancelButton.getScene().getWindow();
        thisStage.close();
    }
}

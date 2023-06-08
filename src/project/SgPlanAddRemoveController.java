package project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SgPlanAddRemoveController {
    private int usedProdId;
    private int idUsedCons;
    private int idUsedMech;
    private String description;
    private float availability;
    private float conQuantity;
    private float totalUsedConsumableQuantity;
    private String type;
    private String productionName;
    private String usedMechName;
    private String usedConsumableName;
    private String usedShareId;
    private String plannedDate;
    private String quantity;

    private String status;

    private String formattedDate;
    @FXML
    private Label errorLabel;
    @FXML
    private Label availabilityLabel;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox productionComboBox;
    @FXML
    private ComboBox usedMechComboBox;
    @FXML
    private ComboBox consComboBox;
    @FXML
    private ComboBox shareComboBox;
    @FXML
    private ComboBox typeComboBox;
    @FXML
    private ComboBox statusComboBox;
    @FXML
    private DatePicker plannedDatePicker;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField planIdField;
    @FXML
    private TextField descriptionField;
    @FXML
    private void initialize() {
        populateConsumableComboBox();
        populateProductionComboBox();
        populateUsedMechComboBox();
        populateShareComboBox();
        statusComboBox.getItems().addAll("Заплановано", "Виконано");
        typeComboBox.getItems().addAll("Посів", "Обробка", "Збір");
        addButton.setDisable(true);
        editButton.setDisable(true);
        removeButton.setDisable(true);
        dateConverter();
    }

    private void populateProductionComboBox() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        try {
            String query = "SELECT Name FROM Production";
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            productionComboBox.getItems().clear();
            while (resultSet.next()) {
                String prodName = resultSet.getString("Name");
                productionComboBox.getItems().add(prodName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateUsedMechComboBox() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        try {
            String query = "SELECT Name FROM Mechlist \n" +
                    "WHERE Status = 'Доступна' ";
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            usedMechComboBox.getItems().clear();
            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                usedMechComboBox.getItems().add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateConsumableComboBox() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        try {
            String query = "SELECT Name FROM ConsumableStock \n" +
                    "WHERE idConsType = 1";
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            consComboBox.getItems().clear();
            while (resultSet.next()) {
                String consName = resultSet.getString("Name");
                consComboBox.getItems().add(consName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void populateShareComboBox() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        try {
            String query = "SELECT idShare FROM Share \n" +
                    "WHERE Status = 'Вільний' ";
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            shareComboBox.getItems().clear();
            while (resultSet.next()) {
                String idShare = resultSet.getString("idShare");
                shareComboBox.getItems().add(idShare);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void getAvailability() {
        float quantity = getConsStockQuantity();
        float totalConsumableQuantity = getTotalConsumableQuantity();
        availability = quantity - totalConsumableQuantity;
    }

    private float getConsStockQuantity() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String quantityQuery = "SELECT Quantity FROM ConsumableStock WHERE Name = ?";
            PreparedStatement quantityStatement = connectDB.prepareStatement(quantityQuery);
            quantityStatement.setString(1, (String) consComboBox.getValue());
            ResultSet quantityQueryResult = quantityStatement.executeQuery();
            if (quantityQueryResult.next()) {
                conQuantity = quantityQueryResult.getFloat("Quantity");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conQuantity;
    }

    private float getTotalConsumableQuantity() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String totalQuantityQuery = "SELECT SUM(ConsumableQuantity) AS TotalConsumableQuantity\n" +
                    "FROM ProductionAction pa\n" +
                    "JOIN ConsumableStock cs ON pa.idConsumable = cs.idConsumable\n" +
                    "WHERE cs.Name = ? AND pa.Status = 'Заплановано'";
            PreparedStatement totalQuantityStatement = connectDB.prepareStatement(totalQuantityQuery);
            totalQuantityStatement.setString(1, (String) consComboBox.getValue());
            ResultSet totalQuantityQueryResult = totalQuantityStatement.executeQuery();
            if (totalQuantityQueryResult.next()) {
                totalUsedConsumableQuantity = totalQuantityQueryResult.getFloat("TotalConsumableQuantity");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalUsedConsumableQuantity;
    }

    public boolean checkIfPlanExists() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        String planId = planIdField.getText();
        try {
            String existQuery = "SELECT idProductionAction FROM ProductionAction WHERE idProductionAction = ?";
            PreparedStatement existStatement = connectDB.prepareStatement(existQuery);
            existStatement.setString(1, planId);
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

    private String getProduction () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String productionNameQuery = "SELECT p.Name\n" +
                    "FROM Production p \n" +
                    "JOIN ProductionAction pa ON pa.idProduction = p.idProduction \n" +
                    "WHERE pa.idProductionAction = " + planId + ";";
            Statement productionNameStatement = connectDB.createStatement();
            ResultSet productionNameQueryResult = productionNameStatement.executeQuery(productionNameQuery);
            if (productionNameQueryResult.next()) {
                productionName = productionNameQueryResult.getString("Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productionName;
    }
    private String getUsedMech () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String usedMechQuery = "SELECT ml.Name\n" +
                    "FROM MechList ml \n" +
                    "JOIN ProductionAction pa ON pa.idMech = ml.idMech \n" +
                    "WHERE pa.idProductionAction = " + planId + ";";
            Statement usedMechStatement = connectDB.createStatement();
            ResultSet usedMechQueryResult = usedMechStatement.executeQuery(usedMechQuery);
            if (usedMechQueryResult.next()) {
                usedMechName = usedMechQueryResult.getString("Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedMechName;
    }
    private String getUsedConsumable () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String usedConsumableQuery = "SELECT cs.Name\n" +
                    "FROM ConsumableStock cs \n" +
                    "JOIN ProductionAction pa ON pa.idConsumable = cs.idConsumable \n" +
                    "WHERE pa.idProductionAction = " + planId + ";";
            Statement usedConsumableStatement = connectDB.createStatement();
            ResultSet usedConsumableQueryResult = usedConsumableStatement.executeQuery(usedConsumableQuery);
            if (usedConsumableQueryResult.next()) {
                usedConsumableName = usedConsumableQueryResult.getString("Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedConsumableName;
    }
    private String getUsedShare () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String usedShareQuery = "SELECT s.idShare\n" +
                    "FROM Share s \n" +
                    "JOIN ProductionAction pa ON pa.idShare = s.idShare \n" +
                    "WHERE pa.idProductionAction = " + planId + ";";
            Statement usedShareStatement = connectDB.createStatement();
            ResultSet usedShareQueryResult = usedShareStatement.executeQuery(usedShareQuery);
            if (usedShareQueryResult.next()) {
                usedShareId = usedShareQueryResult.getString("idShare");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedShareId;
    }

    private String getType () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String typeNameQuery = "SELECT Type FROM ProductionAction WHERE idProductionAction = " + planId + "";
            Statement typeNameStatement = connectDB.createStatement();
            ResultSet typeNameQueryResult = typeNameStatement.executeQuery(typeNameQuery);
            if (typeNameQueryResult.next()) {
                type = typeNameQueryResult.getString("Type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    private String getStatus () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String statusQuery = "SELECT Status FROM ProductionAction WHERE idProductionAction = " + planId + "";
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

    private String getPlannedDate() {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String plannedDateQuery = "SELECT PlannedDate FROM ProductionAction WHERE idProductionAction = " + planId + "";
            Statement plannedDateStatement = connectDB.createStatement();
            ResultSet plannedDateQueryResult = plannedDateStatement.executeQuery(plannedDateQuery);
            if (plannedDateQueryResult.next()) {
                plannedDate = plannedDateQueryResult.getString("PlannedDate");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plannedDate;
    }


    private String getDescription () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String descriptionQuery = "SELECT Description FROM ProductionAction WHERE idProductionAction = " + planId + "";
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
    private String getQuantity () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String quantityQuery = "SELECT ConsumableQuantity FROM ProductionAction WHERE idProductionAction = " + planId + "";
            Statement quantityStatement = connectDB.createStatement();
            ResultSet quantityQueryResult = quantityStatement.executeQuery(quantityQuery);
            if (quantityQueryResult.next()) {
                quantity = quantityQueryResult.getString("ConsumableQuantity");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quantity;
    }
    private int getProductionIdByName() throws SQLException {
        getProduction();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT idProduction FROM Production WHERE Name = ?";
        PreparedStatement statement = connectDB.prepareStatement(query);
        statement.setString(1, (String) productionComboBox.getValue());
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            usedProdId = resultSet.getInt("idProduction");
            return usedProdId;
        }

        return -1;
    }

    private int getMechIdByName() throws SQLException {
        getUsedMech();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT idMech FROM MechList WHERE Name = ?";
        PreparedStatement statement = connectDB.prepareStatement(query);
        statement.setString(1, (String) usedMechComboBox.getValue());
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            idUsedMech = resultSet.getInt("idMech");
            return idUsedMech;
        }

        return -1;
    }

    private int getConsumableIdByName() throws SQLException {
        getUsedConsumable();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT idConsumable FROM ConsumableStock WHERE Name = ?";
        PreparedStatement statement = connectDB.prepareStatement(query);
        statement.setString(1, (String) consComboBox.getValue());
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            idUsedCons = resultSet.getInt("idConsumable");
            return idUsedCons;
        }

        return -1;
    }
    public void dateConverter () {
        plannedDatePicker.setOnAction(event -> {
            LocalDate selectedDate = plannedDatePicker.getValue();
            formattedDate = selectedDate.format(DateTimeFormatter.ISO_DATE);
        });
    }

    public void checkButtonOnAction() {
        checkIfPlanExists();
        boolean exists = checkIfPlanExists();
        if (planIdField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть номер заходу");
        }
        else if (exists == false) {
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Новий захід");
            productionComboBox.setDisable(false);
            usedMechComboBox.setDisable(false);
            consComboBox.setDisable(false);
            shareComboBox.setDisable(false);
            typeComboBox.setDisable(false);
            statusComboBox.setDisable(false);
            descriptionField.setDisable(false);
            plannedDatePicker.setDisable(false);
            addButton.setDisable(false);
            editButton.setDisable(true);
            removeButton.setDisable(true);
        }
        else if (exists){
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Існує");
            planIdField.setDisable(true);
            productionComboBox.setDisable(true);
            usedMechComboBox.setDisable(true);
            consComboBox.setDisable(true);
            shareComboBox.setDisable(true);
            typeComboBox.setDisable(true);
            statusComboBox.setDisable(false);
            descriptionField.setDisable(true);
            plannedDatePicker.setDisable(true);
            quantityField.setDisable(true);
            getProduction();
            getUsedMech();
            getUsedConsumable();
            getUsedShare();
            getType();
            getStatus();
            getDescription();
            getPlannedDate();
            getQuantity();
            productionComboBox.setValue(productionName);
            usedMechComboBox.setValue(usedMechName);
            consComboBox.setValue(usedConsumableName);
            shareComboBox.setValue(usedShareId);
            typeComboBox.setValue(type);
            statusComboBox.setValue(status);
            descriptionField.setText(description);
            plannedDatePicker.setValue(LocalDate.parse(plannedDate));
            quantityField.setText(quantity);
            addButton.setDisable(true);
            editButton.setDisable(false);
            removeButton.setDisable(false);
            if (statusComboBox.getValue() == "Виконано") {
                statusComboBox.setDisable(true);
            }
        }
    }

    public void abortButtonOnAction() {
        planIdField.setDisable(false);
        productionComboBox.setDisable(false);
        usedMechComboBox.setDisable(false);
        consComboBox.setDisable(false);
        shareComboBox.setDisable(false);
        typeComboBox.setDisable(false);
        statusComboBox.setDisable(false);
        descriptionField.setDisable(false);
        plannedDatePicker.setDisable(false);
        quantityField.setDisable(false);
        addButton.setDisable(true);
        editButton.setDisable(true);
        removeButton.setDisable(true);
    }

    public void addButtonOnAction() {
        getAvailability();
        float quantity = Float.parseFloat(quantityField.getText());
        if (productionComboBox.getValue() == null || usedMechComboBox.getValue() == null || consComboBox.getValue() == null || shareComboBox.getValue() == null || typeComboBox.getValue() == null || statusComboBox.getValue() == null || descriptionField.getText().isBlank() || plannedDatePicker.getValue() == null || quantityField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Заповніть всі дані");
        }
        if (quantity > availability) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Розхідника не достатньо");
        }
        else {
            checkIfPlanExists();
            boolean exists = checkIfPlanExists();
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            if (exists) {
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText("Техніка існує");
            } else if (!exists) {
                try {
                    getConsumableIdByName();
                    getMechIdByName();
                    getProductionIdByName();
                    String addQuery = "INSERT INTO ProductionAction (idProduction, idMech, idConsumable, ConsumableQuantity, idShare, Type, Status, PlannedDate, Description) \n" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement addStatement = connectDB.prepareStatement(addQuery);
                    addStatement.setInt(1, usedProdId);
                    addStatement.setInt(2, idUsedMech);
                    addStatement.setInt(3, idUsedCons);
                    addStatement.setFloat(4, Float.parseFloat(quantityField.getText()));
                    addStatement.setInt(5, Integer.parseInt((String) shareComboBox.getValue()));
                    addStatement.setString(6, (String) typeComboBox.getValue());
                    addStatement.setString(7, "Заплановано");
                    addStatement.setString(8, formattedDate);
                    addStatement.setString(9, descriptionField.getText());
                    addStatement.executeUpdate();

                    String updateMechQuery = "UPDATE MechList\n" +
                            "SET Status = 'Задіяно'\n" +
                            "WHERE idMech = "+idUsedMech+";";

                    PreparedStatement updateMechStatement = connectDB.prepareStatement(updateMechQuery);
                    updateMechStatement.executeUpdate();

                    String updateShareQuery = "UPDATE Share\n" +
                            "SET Status = 'Зайнято'\n" +
                            "WHERE idShare = "+Integer.parseInt((String) shareComboBox.getValue())+";";

                    PreparedStatement updateShareStatement = connectDB.prepareStatement(updateShareQuery);
                    updateShareStatement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeButtonOnAction() {
        if (planIdField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть номер плану");
        } else {
            checkIfPlanExists();
            boolean exists = checkIfPlanExists();
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            if (exists) {
                try {
                    String planDeleteQuery = "DELETE FROM ProductionAction WHERE idProductionAction = ?";
                    PreparedStatement planDeleteStatement = connectDB.prepareStatement(planDeleteQuery);
                    planDeleteStatement.setInt(1, Integer.parseInt(planIdField.getText()));
                    planDeleteStatement.executeUpdate();

                    String updateMechQuery = "UPDATE MechList\n" +
                            "SET Status = 'Доступна'\n" +
                            "WHERE idMech = "+idUsedMech+";";

                    PreparedStatement updateMechStatement = connectDB.prepareStatement(updateMechQuery);
                    updateMechStatement.executeUpdate();

                    String updateShareQuery = "UPDATE Share\n" +
                            "SET Status = 'Вільний'\n" +
                            "WHERE idShare = "+Integer.parseInt((String) shareComboBox.getValue())+";";

                    PreparedStatement updateShareStatement = connectDB.prepareStatement(updateShareQuery);
                    updateShareStatement.executeUpdate();
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
        if (statusComboBox.getValue() == null) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть дані");
        }
        float quantity = Float.parseFloat(quantityField.getText());
        checkIfPlanExists();
        boolean exists = checkIfPlanExists();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        if (exists) {
            if (statusComboBox.getValue().equals("Виконано")) {
                try {
                    getQuantity();
                    getConsumableIdByName();
                    getMechIdByName();
                    String planEditQuery = "UPDATE ProductionAction SET Status = ? WHERE idProductionAction = ?";
                    PreparedStatement planEditStatement = connectDB.prepareStatement(planEditQuery);
                    planEditStatement.setString(1, (String) statusComboBox.getValue());
                    planEditStatement.setInt(2, Integer.parseInt(planIdField.getText()));
                    planEditStatement.executeUpdate();

                    String updateQuery = "UPDATE ConsumableStock\n" +
                            "SET Quantity = Quantity - "+ quantity +" \n" +
                            "WHERE idConsumable = "+ idUsedCons +";";

                    PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);
                    updateStatement.executeUpdate();

                    String updateMechQuery = "UPDATE MechList\n" +
                            "SET Status = 'Доступна'\n" +
                            "WHERE idMech = "+idUsedMech+";";

                    PreparedStatement updateMechStatement = connectDB.prepareStatement(updateMechQuery);
                    updateMechStatement.executeUpdate();

                    String updateShareQuery = "UPDATE Share\n" +
                            "SET Status = 'Вільний'\n" +
                            "WHERE idShare = "+Integer.parseInt((String) shareComboBox.getValue())+";";

                    PreparedStatement updateShareStatement = connectDB.prepareStatement(updateShareQuery);
                    updateShareStatement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    getConsumableIdByName();
                    String planEditQuery = "UPDATE ProductionAction SET Status = ? WHERE idProductionAction = ?";
                    PreparedStatement planEditStatement = connectDB.prepareStatement(planEditQuery);
                    planEditStatement.setString(1, (String) statusComboBox.getValue());
                    planEditStatement.setInt(2, Integer.parseInt(planIdField.getText()));
                    planEditStatement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
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

    public void checkAvailabilityButtonOnAction() {
        if (consComboBox.getValue() == null) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Оберіть розхідник");
        } else {
            getAvailability();
            availabilityLabel.setText(Float.toString(availability));
        }
    }
}

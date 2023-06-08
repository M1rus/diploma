package project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MechPlanAddRemoveController {
    private int usedProdId;
    private int idUsedCons;
    private int idUsedMech;
    private String description;
    private String price;
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
    private ComboBox usedMechComboBox;
    @FXML
    private ComboBox consComboBox;
    @FXML
    private ComboBox statusComboBox;
    @FXML
    private DatePicker plannedDatePicker;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField planIdField;
    @FXML
    private TextField descriptionField;
    @FXML
    private void initialize() {
        populateConsumableComboBox();
        populateUsedMechComboBox();
        statusComboBox.getItems().addAll("Заплановано", "Виконано");
        addButton.setDisable(true);
        editButton.setDisable(true);
        removeButton.setDisable(true);
        dateConverter();
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
                    "WHERE idConsType = 2";
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
                    "FROM MechAction ma\n" +
                    "JOIN ConsumableStock cs ON ma.idConsumable = cs.idConsumable\n" +
                    "WHERE cs.Name = ? AND ma.Status = 'Заплановано'";
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
            String existQuery = "SELECT idMechAction FROM MechAction WHERE idMechAction = ?";
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

    private String getUsedMech () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String usedMechQuery = "SELECT ml.Name\n" +
                    "FROM MechList ml \n" +
                    "JOIN MechAction ma ON ma.idMech = ml.idMech \n" +
                    "WHERE ma.idMechAction = " + planId + ";";
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
                    "JOIN MechAction ma ON ma.idConsumable = cs.idConsumable \n" +
                    "WHERE ma.idMechAction = " + planId + ";";
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
    private String getStatus () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String statusQuery = "SELECT Status FROM MechAction WHERE idMechAction = " + planId + "";
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
            String plannedDateQuery = "SELECT PlannedDate FROM MechAction WHERE idMechAction = " + planId + "";
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
            String descriptionQuery = "SELECT Description FROM MechAction WHERE idMechAction = " + planId + "";
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
            String quantityQuery = "SELECT ConsumableQuantity FROM MechAction WHERE idMechAction = " + planId + "";
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

    private String getPrice () {
        String planId = planIdField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String priceQuery = "SELECT Price FROM MechAction WHERE idMechAction = " + planId + "";
            Statement priceStatement = connectDB.createStatement();
            ResultSet priceQueryResult = priceStatement.executeQuery(priceQuery);
            if (priceQueryResult.next()) {
                price = priceQueryResult.getString("Price");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return price;
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
            usedMechComboBox.setDisable(false);
            consComboBox.setDisable(false);
            statusComboBox.setDisable(false);
            descriptionField.setDisable(false);
            priceField.setDisable(false);
            plannedDatePicker.setDisable(false);
            addButton.setDisable(false);
            editButton.setDisable(true);
            removeButton.setDisable(true);
        }
        else if (exists){
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Існує");
            planIdField.setDisable(true);
            usedMechComboBox.setDisable(true);
            consComboBox.setDisable(true);
            statusComboBox.setDisable(false);
            descriptionField.setDisable(true);
            plannedDatePicker.setDisable(true);
            quantityField.setDisable(true);
            priceField.setDisable(true);
            getUsedMech();
            getUsedConsumable();
            getStatus();
            getDescription();
            getPlannedDate();
            getQuantity();
            getPrice();
            usedMechComboBox.setValue(usedMechName);
            consComboBox.setValue(usedConsumableName);
            statusComboBox.setValue(status);
            descriptionField.setText(description);
            plannedDatePicker.setValue(LocalDate.parse(plannedDate));
            quantityField.setText(quantity);
            priceField.setText(price);
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
        usedMechComboBox.setDisable(false);
        consComboBox.setDisable(false);
        statusComboBox.setDisable(false);
        descriptionField.setDisable(false);
        plannedDatePicker.setDisable(false);
        quantityField.setDisable(false);
        priceField.setDisable(false);
        addButton.setDisable(true);
        editButton.setDisable(true);
        removeButton.setDisable(true);
    }

    public void addButtonOnAction() {
        getAvailability();
        float quantity = Float.parseFloat(quantityField.getText());
        if (usedMechComboBox.getValue() == null || consComboBox.getValue() == null || statusComboBox.getValue() == null || descriptionField.getText().isBlank() || plannedDatePicker.getValue() == null) {
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
                    String addQuery = "INSERT INTO MechAction (idMech, idConsumable, ConsumableQuantity,Status, Description, Price, PlannedDate) \n" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement addStatement = connectDB.prepareStatement(addQuery);
                    addStatement.setInt(1, idUsedMech);
                    addStatement.setInt(2, idUsedCons);
                    addStatement.setFloat(3, Float.parseFloat(quantityField.getText()));
                    addStatement.setString(4, "Заплановано");
                    addStatement.setString(5, descriptionField.getText());
                    addStatement.setFloat(6, Float.parseFloat(priceField.getText()));
                    addStatement.setString(7, formattedDate);
                    addStatement.executeUpdate();

                    String updateMechQuery = "UPDATE MechList\n" +
                            "SET Status = 'Обслуговується'\n" +
                            "WHERE idMech = "+idUsedMech+";";

                    PreparedStatement updateMechStatement = connectDB.prepareStatement(updateMechQuery);
                    updateMechStatement.executeUpdate();
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
                    String planDeleteQuery = "DELETE FROM MechAction WHERE idMechAction = ?";
                    PreparedStatement planDeleteStatement = connectDB.prepareStatement(planDeleteQuery);
                    planDeleteStatement.setInt(1, Integer.parseInt(planIdField.getText()));
                    planDeleteStatement.executeUpdate();

                    String updateMechQuery = "UPDATE MechList\n" +
                            "SET Status = 'Доступна'\n" +
                            "WHERE idMech = "+idUsedMech+";";

                    PreparedStatement updateMechStatement = connectDB.prepareStatement(updateMechQuery);
                    updateMechStatement.executeUpdate();
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
                    String planEditQuery = "UPDATE MechAction SET Status = ? WHERE idMechAction = ?";
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
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    getConsumableIdByName();
                    String planEditQuery = "UPDATE MechAction SET Status = ? WHERE idMechAction = ?";
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

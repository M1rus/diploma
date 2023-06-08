package project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ShareAddRemoveController {
    private String area;
    private String type;

    private String formattedDate;
    private String dateOfAcquisition;
    private String ownerInfo;
    private String status;
    private String rentSum;
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
    private DatePicker acquisitionDatePicker;
    @FXML
    private TextField areaField;
    @FXML
    private TextField shareNumField;
    @FXML
    private TextField rentSumField;
    @FXML
    private TextField ownerInfoField;
    @FXML
    private void initialize() {
        dateConverter();
        typeComboBox.getItems().addAll("СГ");
        statusComboBox.getItems().addAll("Доступна", "В ремонті", "Зайнята");
    }

    public boolean checkIfShareExists() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        String shareID = shareNumField.getText();
        try {
            String existQuery = "SELECT idShare FROM Share WHERE idShare = ?";
            PreparedStatement existStatement = connectDB.prepareStatement(existQuery);
            existStatement.setString(1, shareID);
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

    private String getArea() {
        String shareID = shareNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String areaQuery = "SELECT Area\n" +
                    "FROM Share\n" +
                    "WHERE idShare = '" + shareID + "';";
            Statement areaStatement = connectDB.createStatement();
            ResultSet areaQueryResult = areaStatement.executeQuery(areaQuery);
            if (areaQueryResult.next()) {
                area = areaQueryResult.getString("Area");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return area;
    }

    private String getType() {
        String shareID = shareNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String typeQuery = "SELECT Type\n" +
                    "FROM Share\n" +
                    "WHERE idShare = '" + shareID + "';";
            Statement typeStatement = connectDB.createStatement();
            ResultSet typeQueryResult = typeStatement.executeQuery(typeQuery);
            if (typeQueryResult.next()) {
                type = typeQueryResult.getString("Type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    private String getDateOfAcquisition () {
        String shareID = shareNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String dateOfAcquisitionQuery = "SELECT DateOfAcquisition\n" +
                    "FROM Share\n" +
                    "WHERE idShare = '" + shareID + "';";
            Statement dateOfAcquisitionStatement = connectDB.createStatement();
            ResultSet dateOfAcquisitionQueryResult = dateOfAcquisitionStatement.executeQuery(dateOfAcquisitionQuery);
            if (dateOfAcquisitionQueryResult.next()) {
                dateOfAcquisition = dateOfAcquisitionQueryResult.getString("DateOfAcquisition");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateOfAcquisition;
    }


    private String getOwnerInfo () {
        String shareID = shareNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String ownerInfoQuery = "SELECT OwnerInfo\n" +
                    "FROM Share\n" +
                    "WHERE idShare = '" + shareID + "';";
            Statement ownerInfoStatement = connectDB.createStatement();
            ResultSet ownerInfoQueryResult = ownerInfoStatement.executeQuery(ownerInfoQuery);
            if (ownerInfoQueryResult.next()) {
                ownerInfo = ownerInfoQueryResult.getString("OwnerInfo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ownerInfo;
    }

    private String getStatus () {
        String shareID = shareNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String statusQuery = "SELECT Status\n" +
                    "FROM Share\n" +
                    "WHERE idShare = '" + shareID + "';";
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
    private String getRentSum () {
        String shareID = shareNumField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String rentSumQuery = "SELECT RentSum\n" +
                    "FROM Share\n" +
                    "WHERE idShare = '" + shareID + "';";
            Statement rentSumStatement = connectDB.createStatement();
            ResultSet rentSumQueryResult = rentSumStatement.executeQuery(rentSumQuery);
            if (rentSumQueryResult.next()) {
                rentSum = rentSumQueryResult.getString("RentSum");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rentSum;
    }




    public void checkButtonOnAction() {
        checkIfShareExists();
        boolean exists = checkIfShareExists();
        if (shareNumField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть номер паю");
        }
        else if (exists == false) {
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Новий пай");
            areaField.setDisable(false);
            typeComboBox.setDisable(false);
            acquisitionDatePicker.setDisable(false);
            ownerInfoField.setDisable(false);
            statusComboBox.setDisable(false);
            rentSumField.setDisable(false);
        }
        else if (exists){
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Існує");
            areaField.setDisable(true);
            typeComboBox.setDisable(true);
            acquisitionDatePicker.setDisable(true);
            ownerInfoField.setDisable(true);
            getArea();
            getType();
            getDateOfAcquisition();
            getOwnerInfo();
            getStatus();
            getRentSum();
            areaField.setText(area);
            typeComboBox.setValue(type);
            acquisitionDatePicker.setValue(LocalDate.parse(dateOfAcquisition));
            ownerInfoField.setText(ownerInfo);
            statusComboBox.setValue(status);
            rentSumField.setText(rentSum);
            addButton.setDisable(true);
        }
    }

    public void abortButtonOnAction() {
        areaField.setDisable(false);
        typeComboBox.setDisable(false);
        acquisitionDatePicker.setDisable(false);
        ownerInfoField.setDisable(false);
        statusComboBox.setDisable(false);
        rentSumField.setDisable(false);
        addButton.setDisable(false);
    }

    public void dateConverter () {
        acquisitionDatePicker.setOnAction(event -> {
            LocalDate selectedDate = acquisitionDatePicker.getValue();
            formattedDate = selectedDate.format(DateTimeFormatter.ISO_DATE);
        });
    }

    public void addButtonOnAction() {
        if (typeComboBox.getValue() == null || statusComboBox.getValue() == null || areaField.getText().isBlank() || acquisitionDatePicker.getValue() == null || ownerInfoField.getText().isBlank() || rentSumField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Заповніть всі дані");
        } else {
            checkIfShareExists();
            boolean exists = checkIfShareExists();
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            if (exists) {
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText("Техніка існує");
            } else if (!exists) {
                try {
                    String shareInsertQuery = "INSERT INTO Share (Area, Type, DateOfAcquisition, OwnerInfo, Status, RentSum)\n" +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement shareInsertStatement = connectDB.prepareStatement(shareInsertQuery);
                    shareInsertStatement.setFloat(1, Float.parseFloat(areaField.getText()));
                    shareInsertStatement.setString(2, (String) typeComboBox.getValue());
                    shareInsertStatement.setString(3, formattedDate);
                    shareInsertStatement.setString(4, ownerInfoField.getText());
                    shareInsertStatement.setString(5, (String) statusComboBox.getValue());
                    shareInsertStatement.setFloat(6, Float.parseFloat(rentSumField.getText()));
                    shareInsertStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeButtonOnAction() {
        if (shareNumField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть номер кузову");
        } else {
            checkIfShareExists();
            boolean exists = checkIfShareExists();
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            if (exists) {
                try {
                    String shareDeleteQuery = "DELETE FROM Share WHERE idShare = ?";
                    PreparedStatement shareDeleteStatement = connectDB.prepareStatement(shareDeleteQuery);
                    shareDeleteStatement.setInt(1, Integer.parseInt(shareNumField.getText()));
                    shareDeleteStatement.executeUpdate();
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
        if (statusComboBox.getValue() == null || ownerInfoField.getText().isBlank() || rentSumField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть дані");
        }
        checkIfShareExists();
        boolean exists = checkIfShareExists();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        if (exists) {
            try {
                String shareEditQuery = "UPDATE Share SET Status = ?, OwnerInfo = ?, RentSum = ? WHERE BodyNum = ?";
                PreparedStatement shareEditStatement = connectDB.prepareStatement(shareEditQuery);
                shareEditStatement.setString(1, (String) statusComboBox.getValue());
                shareEditStatement.setString(2, ownerInfoField.getText());
                shareEditStatement.setFloat(3, Float.parseFloat(rentSumField.getText()));
                shareEditStatement.setInt(2, Integer.parseInt(shareNumField.getText()));
                shareEditStatement.executeUpdate();
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

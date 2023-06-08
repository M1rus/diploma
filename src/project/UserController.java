package project;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private String name;
    private String surname;
    private String role;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField passwordField;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField loginField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private ComboBox roleComboBox;
    @FXML
    private TableView<ObservableList<String>> userTableView;
    private String[] columnNames;
    private ObservableList<ObservableList<String>> data;
    String query = "SELECT UserLogin.Username, UserLogin.Name, UserLogin.Surname, LoginRole.RoleName\n" +
            "FROM UserLogin\n" +
            "JOIN LoginRole ON UserLogin.idLR = LoginRole.idLoginRole;";

    @FXML
    private void initialize() {
        columnNames = new String[]{"Логін", "Ім'я", "Прізвище", "Роль"};
        data = FXCollections.observableArrayList();
        populateRoleComboBox();
        loadDataFromDatabase();
        getNumberOfColumnsFromDatabase();
        createTableView();
    }

    private void populateRoleComboBox() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String query = "SELECT RoleName FROM LoginRole";
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            roleComboBox.getItems().clear();
            while (resultSet.next()) {
                String typeName = resultSet.getString("RoleName");
                roleComboBox.getItems().add(typeName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfUserExists() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        String login = loginField.getText();
        try {
            String existQuery = "SELECT Username FROM UserLogin WHERE BINARY Username = ?";
            PreparedStatement existStatement = connectDB.prepareStatement(existQuery);
            existStatement.setString(1, login);
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

    private String getUserName() {
        String login = loginField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String nameQuery = "SELECT Name FROM UserLogin WHERE BINARY Username = '" + login + "'";
            Statement nameStatement = connectDB.createStatement();
            ResultSet nameQueryResult = nameStatement.executeQuery(nameQuery);
            if (nameQueryResult.next()) {
                name = nameQueryResult.getString("Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    private String getUserSurname() {
        String login = loginField.getText();
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String surnameQuery = "SELECT Surname FROM UserLogin WHERE BINARY Username = '" + login + "'";
            Statement surnameStatement = connectDB.createStatement();
            ResultSet surnameQueryResult = surnameStatement.executeQuery(surnameQuery);
            if (surnameQueryResult.next()) {
                surname = surnameQueryResult.getString("Surname");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surname;
    }

    public String getRole() {
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        String login = loginField.getText();
        try {
            String roleQuery = "SELECT LoginRole.RoleName\n" +
                    "FROM UserLogin\n" +
                    "JOIN LoginRole ON UserLogin.idLR = LoginRole.idLoginRole\n" +
                    "WHERE UserLogin.Username = '" + login + "';";
            Statement roleStatement = connectDB.createStatement();
            ResultSet roleQueryResult = roleStatement.executeQuery(roleQuery);
            if (roleQueryResult.next()) {
                role = roleQueryResult.getString("RoleName");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return role;
    }

    private void createTableView() {
        userTableView.setTableMenuButtonVisible(true);
        userTableView.getColumns().addListener((ListChangeListener<TableColumn<ObservableList<String>, ?>>) c -> {
            while (c.next()) {
                if (c.wasReplaced()) {
                    List<? extends TableColumn<ObservableList<String>, ?>> changedColumns = c.getAddedSubList();
                    for (TableColumn<ObservableList<String>, ?> column : changedColumns) {
                        column.setMinWidth(200);
                    }
                }
            }
        });

        int numColumns = getNumberOfColumnsFromDatabase();

        for (int i = 1; i <= numColumns; i++) {
            final int columnIndex = i - 1;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnNames[i - 1]);
            column.setCellValueFactory(cellData -> {
                ObservableList<String> row = cellData.getValue();
                if (row.size() > columnIndex) {
                    return new SimpleStringProperty(row.get(columnIndex));
                } else {
                    return new SimpleStringProperty("");
                }
            });
            column.setMinWidth(200);
            userTableView.getColumns().add(column);
        }

        data = FXCollections.observableArrayList();
        loadDataFromDatabase();

        userTableView.setItems(data);
    }
    private void updateTableView() {
        userTableView.setTableMenuButtonVisible(true);
        userTableView.getColumns().addListener((ListChangeListener<TableColumn<ObservableList<String>, ?>>) c -> {
            while (c.next()) {
                if (c.wasReplaced()) {
                    List<? extends TableColumn<ObservableList<String>, ?>> changedColumns = c.getAddedSubList();
                    for (TableColumn<ObservableList<String>, ?> column : changedColumns) {
                        column.setMinWidth(200);
                    }
                }
            }
        });

        List<Double> columnWidths = new ArrayList<>();
        for (TableColumn<ObservableList<String>, ?> column : userTableView.getColumns()) {
            columnWidths.add(column.getWidth());
        }

        userTableView.getColumns().clear();
        data.clear();

        int numColumns = getNumberOfColumnsFromDatabase();

        for (int i = 1; i <= numColumns; i++) {
            final int columnIndex = i - 1;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnNames[i - 1]);
            column.setCellValueFactory(cellData -> {
                ObservableList<String> row = cellData.getValue();
                if (row.size() > columnIndex) {
                    return new SimpleStringProperty(row.get(columnIndex));
                } else {
                    return new SimpleStringProperty("");
                }
            });
            column.setMinWidth(200);
            column.setPrefWidth(columnWidths.get(columnIndex));
            userTableView.getColumns().add(column);
        }

        loadDataFromDatabase();

        userTableView.setItems(data);
    }
    private void loadDataFromDatabase() {
        data.clear();

        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int numColumns = resultSet.getMetaData().getColumnCount();

            if (userTableView != null) {
                userTableView.getColumns().clear();
            }

            for (int i = 1; i <= numColumns; i++) {
                final int columnIndex = i - 1;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnNames[i - 1]);
                column.setCellValueFactory(cellData -> {
                    ObservableList<String> row = cellData.getValue();
                    if (row.size() > columnIndex) {
                        return new SimpleStringProperty(row.get(columnIndex));
                    } else {
                        return new SimpleStringProperty("");
                    }
                });
                if (userTableView != null) {
                    userTableView.getColumns().add(column);
                }
            }

            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= numColumns; i++) {
                    row.add(resultSet.getString(i));
                }
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getNumberOfColumnsFromDatabase() {
        int numColumns = 0;
        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            numColumns = resultSet.getMetaData().getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return numColumns;
    }

    public void checkButtonOnAction() {
        checkIfUserExists();
        boolean exists = checkIfUserExists();
        if (loginField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть логін");
        }
        else if (exists == false) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Користувача не існує");
        }
        else if (exists){
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Існує");
            getUserSurname();
            getUserName();
            getRole();
            nameField.setText(name);
            surnameField.setText(surname);
            roleComboBox.setValue(role);
        }
    }
    public void registerButtonOnAction() {
        checkIfUserExists();
        boolean exists = checkIfUserExists();
        if (loginField.getText().isBlank() || passwordField.getText().isBlank() || nameField.getText().isBlank() || surnameField.getText().isBlank() || roleComboBox.getValue() == null) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Заповніть всі поля");
        } else if (exists) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Користувач зтаким логіном вже існує");
        } else if (!exists) {
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Зареєстровано");
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            try {
                String insertQuery = "INSERT INTO UserLogin (Username, Password, Name, Surname, idLR)\n" +
                        "SELECT ?, ?, ?, ?, LoginRole.idLoginRole\n" +
                        "FROM LoginRole\n" +
                        "WHERE LoginRole.RoleName = ?;";
                PreparedStatement insertStatement = connectDB.prepareStatement(insertQuery);
                insertStatement.setString(1, loginField.getText());
                String hashedPassword = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());
                insertStatement.setString(2, hashedPassword);
                insertStatement.setString(3, nameField.getText());
                insertStatement.setString(4, surnameField.getText());
                insertStatement.setString(5, (String) roleComboBox.getValue());
                insertStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void editButtonOnAction() {
        checkIfUserExists();
        boolean exists = checkIfUserExists();
        if (loginField.getText().isBlank() || passwordField.getText().isBlank() || nameField.getText().isBlank() || surnameField.getText().isBlank() || roleComboBox.getValue() == null) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Заповніть всі поля");
        } else if (!exists) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Користувача з таким логіном не знайдено");
        } else if (exists) {
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Дані змінено");
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            try {
                String editQuery = "UPDATE UserLogin\n" +
                        "JOIN LoginRole ON UserLogin.idLR = LoginRole.idLoginRole\n" +
                        "SET UserLogin.Password = ?,\n" +
                        "    UserLogin.Name = ?,\n" +
                        "    UserLogin.Surname = ?,\n" +
                        "    LoginRole.RoleName = ?\n" +
                        "WHERE UserLogin.Username = ?;";
                PreparedStatement editStatement = connectDB.prepareStatement(editQuery);
                String hashedPassword = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());
                editStatement.setString(1, hashedPassword);
                editStatement.setString(2, nameField.getText());
                editStatement.setString(3, surnameField.getText());
                editStatement.setString(4, (String) roleComboBox.getValue());
                editStatement.setString(5, loginField.getText());
                editStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void deleteButtonOnAction() {
        checkIfUserExists();
        boolean exists = checkIfUserExists();
        if (loginField.getText().isBlank()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Введіть логін");
        } else if (!exists) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Користувача з таким логіном не знайдено");
        } else if (exists) {
            DBConnection connectNow = new DBConnection();
            Connection connectDB = connectNow.getConnection();
            errorLabel.setText("Користувача видалено");
            try {
                String deleteQuery = "DELETE FROM UserLogin WHERE Username = '"+ loginField.getText() +"';";
                PreparedStatement deleteStatement = connectDB.prepareStatement(deleteQuery);
                deleteStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cancelButtonOnAction(ActionEvent e) {
        Stage thisStage = (Stage) cancelButton.getScene().getWindow();
        thisStage.close();
    }
}
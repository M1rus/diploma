package project;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StockController {
    private String role;

    @FXML
    private HBox hBoxBottom;
    @FXML
    private Button returnButton;

    @FXML
    private Button consButton;

    @FXML
    private Button requestButton;

    @FXML
    private AnchorPane anchorPaneTable;

    @FXML
    private HBox anchorPaneActionButton;

    private Button button1;
    private Button button2;
    private Button button3;

    private Button button4;

    private String[] columnNames;

    private boolean consButtonStatus = false;
    private boolean requestButtonStatus = false;

    private ObservableList<ObservableList<String>> data;
    private TableView<ObservableList<String>> tableView;

    String query = null;
    public void setRole(String role) {
        this.role = role;
        roleCheck();
    }

    public void roleCheck() {
        if (role.equals("Агроном")) {
            requestButton.setDisable(true);
        }
        else if (role.equals("Инженер")) {
            requestButton.setDisable(true);
        } else {
            requestButton.setDisable(false);
        }
    }

    @FXML
    private void initialize() {
        data = FXCollections.observableArrayList();
        button1 = createButton("");
        button2 = createButton("");
        button3 = createButton("");
        button4 = createButton("Додати відомість");

        consButton.setOnAction(event -> {
            consButtonStatus = true;
            requestButtonStatus = false;
            updateButtonsInHBox(consButton, button1, button2, button3);
            if (role.equals("Агроном")) {
                button2.setDisable(true);
                button1.setDisable(false);
            } else if (role.equals("Инженер")) {
                button2.setDisable(false);
                button1.setDisable(true);
            } else {
                button2.setDisable(false);
                button1.setDisable(false);
            }
        });

        requestButton.setOnAction(event -> {
            consButtonStatus = false;
            requestButtonStatus = true;
            updateButtonsInHBox(requestButton, button1, button2, button3);
            updateButtonInAnchorPane(requestButton);
        });

        button1.setOnAction(event -> {
            if (consButtonStatus) {
                columnNames = new String[]{"Номер", "Назва", "Сфера", "Опис", "Кількість"};
                query = "SELECT cs.idConsumable, cs.Name, ct.TypeName, cs.Description, cs.Quantity\n" +
                        "FROM ConsumableStock cs\n" +
                        "JOIN ConsumableType ct ON cs.idConsType = ct.idConsumableType\n" +
                        "WHERE ct.TypeName = 'СГ';";
            }
            else if (requestButtonStatus) {
                columnNames = new String[]{"Номер", "Назва", "Сфера", "Опис", "Кількість", "Статус"};
                query = "SELECT cr.idConsumableRequest, cs.Name, ct.TypeName, cs.Description, cs.Quantity, cr.Status\n" +
                        "FROM ConsumableRequest cr\n" +
                        "JOIN ConsumableStock cs ON cr.idCons = cs.idConsumable\n" +
                        "JOIN ConsumableType ct ON cs.idConsType = ct.idConsumableType\n" +
                        "WHERE cr.Status = 'Додано';";
            }
            getNumberOfColumnsFromDatabase();
            loadDataFromDatabase();
            createOrUpdateTableView();
            updateButtonInAnchorPane(button1);
        });

        button2.setOnAction(event -> {
            if (consButtonStatus) {
                columnNames = new String[]{"Номер", "Назва", "Сфера", "Опис", "Кількість"};
                query = "SELECT cs.idConsumable, cs.Name, ct.TypeName, cs.Description, cs.Quantity\n" +
                        "FROM ConsumableStock cs\n" +
                        "JOIN ConsumableType ct ON cs.idConsType = ct.idConsumableType\n" +
                        "WHERE ct.TypeName = 'Тех';";
            }
            else if (requestButtonStatus) {
                columnNames = new String[]{"Номер", "Назва", "Сфера", "Опис", "Кількість", "Статус", "Загальна ціна"};
                query = "SELECT cr.idConsumableRequest, cs.Name, ct.TypeName, cs.Description, cs.Quantity, cr.Status, cr.TotalPrice \n" +
                        "FROM ConsumableRequest cr\n" +
                        "JOIN ConsumableStock cs ON cr.idCons = cs.idConsumable\n" +
                        "JOIN ConsumableType ct ON cs.idConsType = ct.idConsumableType\n" +
                        "WHERE cr.Status = 'В процесі';";
            }
            getNumberOfColumnsFromDatabase();
            loadDataFromDatabase();
            createOrUpdateTableView();
            updateButtonInAnchorPane(button2);
        });
        button3.setOnAction(event -> {
            columnNames = new String[]{"Номер", "Назва", "Сфера", "Опис", "Кількість", "Статус", "Загальна ціна", "Дата виконання"};
            query = "SELECT cr.idConsumableRequest, cs.Name, ct.TypeName, cs.Description, cs.Quantity, cr.Status, cr.TotalPrice, cr.DateOfCompletion \n" +
                    "FROM ConsumableRequest cr\n" +
                    "JOIN ConsumableStock cs ON cr.idCons = cs.idConsumable\n" +
                    "JOIN ConsumableType ct ON cs.idConsType = ct.idConsumableType\n" +
                    "WHERE cr.Status = 'Завершено';";
            getNumberOfColumnsFromDatabase();
            loadDataFromDatabase();
            createOrUpdateTableView();
            updateButtonInAnchorPane(button2);
        });
    }

    private void updateButtonInAnchorPane(Button sourceButton, Button... buttons) {
        anchorPaneActionButton.getChildren().clear();

        if (consButtonStatus) {
            if (sourceButton == button1 || sourceButton == button2) {
                Button button4 = createButton("Додати/редагувати");
                anchorPaneActionButton.getChildren().add(button4);
                button4.setOnAction(event -> {
                    toTheStockAddRemove();
                });
                if (role.equals("Агроном")) {
                    button4.setDisable(true);
                } else if (role.equals("Инженер")) {
                    button4.setDisable(true);
                } else {
                    button4.setDisable(false);
                }
            } else if (sourceButton == requestButton) {
                anchorPaneActionButton.getChildren().clear();
            }
        } else if (requestButtonStatus) {
            if (sourceButton == button1 || sourceButton == button2 || sourceButton == button3) {
                Button button4 = createButton("Додати");
                Button button5 = createButton("Редагувати");
                button4.setOnAction(event -> {
                    toTheStockRequestAddPage();
                });
                button5.setOnAction(event -> {
                    toTheStockRequestEditPage();
                });
                anchorPaneActionButton.getChildren().addAll(button4, button5);
            } else if (sourceButton == requestButton) {
                anchorPaneActionButton.getChildren().clear();
            }
        }
    }

    private void updateButtonsInHBox(Button sourceButton, Button... buttons) {
        hBoxBottom.getChildren().clear();

        if (sourceButton == consButton) {
            button1.setText("Сільске господарство");
            button2.setText("Запчастини");
            hBoxBottom.getChildren().addAll(button1, button2);
        } else if (sourceButton == requestButton) {
            button1.setText("Додані");
            button2.setText("В процесі");
            button3.setText("Завершені");
            hBoxBottom.getChildren().addAll(button1, button2, button3);
        }
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        return button;
    }

    private void createOrUpdateTableView() {
        if (tableView == null) {
            data.clear();
            createTableView();
        } else {
            data.clear();
            updateTableView();
        }
    }

    private void createTableView() {
        tableView = new TableView<>();
        tableView.setLayoutX(19);
        tableView.setLayoutY(49);
        tableView.setPrefWidth(1161);
        tableView.setPrefHeight(537);
        tableView.setTableMenuButtonVisible(true);
        tableView.getColumns().addListener((ListChangeListener<TableColumn<ObservableList<String>, ?>>) c -> {
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
            column.setMinWidth(200); // Мінімальна ширина стовпця
            tableView.getColumns().add(column);
        }

        data = FXCollections.observableArrayList();
        loadDataFromDatabase();

        tableView.setItems(data);

        AnchorPane.setTopAnchor(tableView, 49.0);
        AnchorPane.setLeftAnchor(tableView, 19.0);
        AnchorPane.setRightAnchor(tableView, 20.0);
        AnchorPane.setBottomAnchor(tableView, 14.0);

        anchorPaneTable.getChildren().add(tableView);
    }

    private void updateTableView() {
        tableView.setLayoutX(19);
        tableView.setLayoutY(49);
        tableView.setPrefWidth(1161);
        tableView.setPrefHeight(537);
        tableView.setTableMenuButtonVisible(true);
        tableView.getColumns().addListener((ListChangeListener<TableColumn<ObservableList<String>, ?>>) c -> {
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
        for (TableColumn<ObservableList<String>, ?> column : tableView.getColumns()) {
            columnWidths.add(column.getWidth());
        }

        tableView.getColumns().clear();
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
            column.setMinWidth(200); // Мінімальна ширина стовпця
            column.setPrefWidth(columnWidths.get(columnIndex)); // Встановлюємо попередній розмір стовпця
            tableView.getColumns().add(column);
        }

        loadDataFromDatabase();

        tableView.setItems(data);

        AnchorPane.setTopAnchor(tableView, 49.0);
        AnchorPane.setLeftAnchor(tableView, 19.0);
        AnchorPane.setRightAnchor(tableView, 20.0);
        AnchorPane.setBottomAnchor(tableView, 14.0);
    }

    private void loadDataFromDatabase() {
        data.clear();

        DBConnection connectNow = new DBConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int numColumns = resultSet.getMetaData().getColumnCount();

            if (tableView != null) {
                tableView.getColumns().clear();
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
                if (tableView != null) {
                    tableView.getColumns().add(column);
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
    public void toTheStockAddRemove() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("view/StockAddRemoveView.fxml"));
            Stage stockAddRemoveAddStage = new Stage();
            stockAddRemoveAddStage.setTitle("Додати/прибрати розхідник");
            stockAddRemoveAddStage.setScene(new Scene(root, 600, 400));
            stockAddRemoveAddStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTheStockRequestAddPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("view/StockRequestAddView.fxml"));
            Stage requestAddStage = new Stage();
            requestAddStage.setTitle("Додати запит на поповнення");
            requestAddStage.setScene(new Scene(root, 600, 400));
            requestAddStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTheStockRequestEditPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("view/StockRequestEditView.fxml"));
            Stage requestEditStage = new Stage();
            requestEditStage.setTitle("Редагувати запит на поповнення");
            requestEditStage.setScene(new Scene(root, 600, 457));
            requestEditStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void returnButtonOnAction () {
        toTheMainPage();
    }

    public void toTheMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainPageView.fxml"));
            Parent root = loader.load();

            MainPageController mainPageController = loader.getController();
            mainPageController.setRole(role);

            Stage mainStage = new Stage();
            mainStage.setTitle("Sg Page");
            mainStage.setScene(new Scene(root));
            mainStage.show();

            Stage reportStage = (Stage) returnButton.getScene().getWindow();
            reportStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

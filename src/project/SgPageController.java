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

public class SgPageController {
    private String role;

    @FXML
    private HBox hBoxBottom;

    @FXML
    private Button zbButton;
    @FXML
    private Button returnButton;

    @FXML
    private Button salesButton;

    @FXML
    private AnchorPane anchorPaneTable;

    @FXML
    private HBox anchorPaneActionButton;

    private Button button1;
    private Button button2;
    private Button button3;

    private Button button4;

    private String[] columnNames;

    private boolean zbButtonStatus = false;
    private boolean salesButtonStatus = false;

    private ObservableList<ObservableList<String>> data;
    private TableView<ObservableList<String>> tableView;

    String query = null;
    public void setRole(String role) {
        this.role = role;
        roleCheck();
    }
    public void roleCheck() {
        if (role.equals("Директор")) {
            salesButton.setDisable(false);
        }
        else {
            salesButton.setDisable(true);
        }
    }

    @FXML
    private void initialize() {
        data = FXCollections.observableArrayList();
        button1 = createButton("Здійснені");
        button2 = createButton("Здійснені");
        button3 = createButton("Здійснені");
        button4 = createButton("Додати відомість");

        zbButton.setOnAction(event -> {
            zbButtonStatus = true;
            salesButtonStatus = false;
            updateButtonsInHBox(zbButton, button1, button2, button3);
            if (role.equals("Завсклад")) {
                button1.setDisable(true);
            } else {
                button1.setDisable(false);
            }
        });

        salesButton.setOnAction(event -> {
            zbButtonStatus = false;
            salesButtonStatus = true;
            updateButtonsInHBox(salesButton, button1, button2, button3);
            updateButtonInAnchorPane(salesButton);
        });

        button1.setOnAction(event -> {
            if (zbButtonStatus) {
                columnNames = new String[]{"Назва", "Кількість", "Дата"};
                query = "SELECT p.Name, pg.Quantity, Date\n" +
                        "FROM Production p\n" +
                        "JOIN ProductionGathering pg ON p.idProduction = pg.idProduct";
            }
            else if (salesButtonStatus) {
                columnNames = new String[]{"Номер", "Назва", "Покупець", "Кількість", "Статус", "Загальна ціна"};
                query = "SELECT PS.idProductionSales, P.Name, PS.CustomerInfo, PS.Quantity, PS.Status, PS.TotalPrice\n" +
                        "FROM ProductionSales PS\n" +
                        "JOIN Production P ON PS.idProductToSale = P.idProduction\n" +
                        "WHERE PS.Status = 'Попередньо';";
            }
            getNumberOfColumnsFromDatabase();
            loadDataFromDatabase();
            createOrUpdateTableView();
            updateButtonInAnchorPane(button1);
        });

        button2.setOnAction(event -> {
            if (zbButtonStatus) {
                columnNames = new String[]{"Номер", "Назва", "Опис", "Кількість", "Зарезервовано"};
                query = "SELECT p.idProduction, p.Name, p.Description, ps.Quantity, ps.ReservedQuantity\n" +
                        "FROM Production p\n" +
                        "JOIN ProductStock ps ON p.idProduction = ps.idProduct";
            }
            else if (salesButtonStatus) {
                columnNames = new String[]{"Номер", "Назва", "Покупець", "Кількість", "Статус", "Загальна ціна"};
                query = "SELECT PS.idProductionSales, P.Name, PS.CustomerInfo, PS.Quantity, PS.Status, PS.TotalPrice\n" +
                        "FROM ProductionSales PS\n" +
                        "JOIN Production P ON PS.idProductToSale = P.idProduction\n" +
                        "WHERE PS.Status = 'В процесі';";
            }
            getNumberOfColumnsFromDatabase();
            loadDataFromDatabase();
            createOrUpdateTableView();
            updateButtonInAnchorPane(button2);
        });
        button3.setOnAction(event -> {
            columnNames = new String[]{"Номер", "Назва", "Покупець", "Кількість", "Статус", "Загальна ціна"};
            query = "SELECT PS.idProductionSales, P.Name, PS.CustomerInfo, PS.Quantity, PS.Status, PS.TotalPrice\n" +
                    "FROM ProductionSales PS\n" +
                    "JOIN Production P ON PS.idProductToSale = P.idProduction\n" +
                    "WHERE PS.Status = 'Завершено';";
            getNumberOfColumnsFromDatabase();
            loadDataFromDatabase();
            createOrUpdateTableView();
            updateButtonInAnchorPane(button2);
        });
    }

    private void updateButtonInAnchorPane(Button sourceButton, Button... buttons) {
        anchorPaneActionButton.getChildren().clear();

        if (zbButtonStatus) {
            if (sourceButton == button1) {
                Button button4 = createButton("Додати відомість");
                anchorPaneActionButton.getChildren().add(button4);
                button4.setOnAction(event -> {
                    toTheGathViewPage();
                });

            } else if (sourceButton == button2) {
                anchorPaneActionButton.getChildren().clear();
            } else if (sourceButton == salesButton) {
                anchorPaneActionButton.getChildren().clear();
            }
        } else if (salesButtonStatus) {
            if (sourceButton == button1 || sourceButton == button2 || sourceButton == button3) {
                Button button4 = createButton("Додати");
                Button button5 = createButton("Редагувати");
                button4.setOnAction(event -> {
                    toTheSalesAddPage();
                });
                button5.setOnAction(event -> {
                    toTheSalesEditPage();
                });
                anchorPaneActionButton.getChildren().addAll(button4, button5);
            } else if (sourceButton == salesButton) {
                anchorPaneActionButton.getChildren().clear();
            }
        }
    }

    private void updateButtonsInHBox(Button sourceButton, Button... buttons) {
        hBoxBottom.getChildren().clear();

        if (sourceButton == zbButton) {
            button1.setText("Збори");
            button2.setText("Каталог");
            hBoxBottom.getChildren().addAll(button1, button2);
        } else if (sourceButton == salesButton) {
            button1.setText("Попередньо");
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
        tableView.getStylesheets().add("allstyle.css");
        tableView.getStyleClass().add("table-view .column-header-background");
        tableView.getStyleClass().add("table-view .column-header, .table-view .table-cell");
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

    public void toTheGathViewPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("view/AddGatheringView.fxml"));
            Stage addGatheringStage = new Stage();
            addGatheringStage.setTitle("Додати відомість по зборах");
            addGatheringStage.setScene(new Scene(root, 600, 400));
            addGatheringStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTheSalesAddPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("view/SalesAddView.fxml"));
            Stage salesAddStage = new Stage();
            salesAddStage.setTitle("Додати продаж");
            salesAddStage.setScene(new Scene(root, 600, 400));
            salesAddStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toTheSalesEditPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("view/SalesEditView.fxml"));
            Stage salesEditStage = new Stage();
            salesEditStage.setTitle("Редагувати продаж");
            salesEditStage.setScene(new Scene(root, 600, 400));
            salesEditStage.show();
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
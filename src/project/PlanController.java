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
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlanController {

    private String role;
    @FXML
    private AnchorPane anchorPaneTable;
    @FXML
    private Button mechButton;
    @FXML
    private Button sgButton;
    @FXML
    private Button addRemoveButton;
    @FXML
    private Button returnButton;
    private String[] columnNames;
    private ObservableList<ObservableList<String>> data;
    private TableView<ObservableList<String>> tableView;
    private boolean mechButtonStatus;
    private boolean sgButtonStatus;

    String query = null;
    public void setRole(String role) {
        this.role = role;
    }

    @FXML
    private void initialize() {
        addRemoveButton.setVisible(false);
        data = FXCollections.observableArrayList();
        mechButton.setOnAction(event -> {
            columnNames = new String[]{"Номер заходу", "Техніка", "Розхідник", "Використано розхідника", "Статус", "Опис", "Ціна", "Дата",};
            query = "SELECT MechAction.idMechAction, MechList.Name AS MechName, ConsumableStock.Name AS ConsumableName, MechAction.ConsumableQuantity, MechAction.Status, MechAction.Description, MechAction.Price, MechAction.PlannedDate\n" +
                    "FROM MechAction\n" +
                    "JOIN MechList ON MechAction.idMech = MechList.idMech\n" +
                    "JOIN ConsumableStock ON MechAction.idConsumable = ConsumableStock.idConsumable;";
            getNumberOfColumnsFromDatabase();
            loadDataFromDatabase();
            createOrUpdateTableView();
            mechButtonStatus = true;
            sgButtonStatus = false;
            addRemoveButton.setVisible(true);
        });
        sgButton.setOnAction(event -> {
            columnNames = new String[]{"Номер заходу", "Продукція", "Техніка", "Розхідник", "Використано розхідника", "Номер паю", "Тип", "Статус", "Дата", "Опис"};
            query = "SELECT PA.idProductionAction, P.Name AS ProductionName, ML.Name AS MechListName, CS.Name AS ConsumableName, PA.ConsumableQuantity, PA.idShare, PA.Type, PA.Status, PA.PlannedDate, PA.Description\n" +
                    "FROM ProductionAction PA\n" +
                    "JOIN Production P ON PA.idProduction = P.idProduction\n" +
                    "JOIN MechList ML ON PA.idMech = ML.idMech\n" +
                    "JOIN ConsumableStock CS ON PA.idConsumable = CS.idConsumable\n" +
                    "JOIN Share S ON PA.idShare = S.idShare;";
            getNumberOfColumnsFromDatabase();
            loadDataFromDatabase();
            createOrUpdateTableView();
            mechButtonStatus = false;
            sgButtonStatus = true;
            addRemoveButton.setVisible(true);
        });
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
    public void availableButtonOnAction() {
        columnNames = new String[]{"Назва", "Тип", "Статус", "Опис", "Номер кузову", "Власність"};
        query = "SELECT ML.Name, MT.TypeName, ML.Status, ML.Description, ML.BodyNum, ML.Ownership\n" +
                "FROM MechList ML\n" +
                "JOIN MechType MT ON ML.IdMT = MT.idMechType\n" +
                "WHERE ML.Status = 'Доступна';";
        getNumberOfColumnsFromDatabase();
        loadDataFromDatabase();
        createOrUpdateTableView();
    }
    public void otherButtonOnAction() {
        columnNames = new String[]{"Назва", "Тип", "Статус", "Опис", "Номер кузову", "Власність"};
        query = "SELECT ML.Name, MT.TypeName, ML.Status, ML.Description, ML.BodyNum, ML.Ownership\n" +
                "FROM MechList ML\n" +
                "JOIN MechType MT ON ML.IdMT = MT.idMechType\n" +
                "WHERE ML.Status <> 'Доступна';";
        getNumberOfColumnsFromDatabase();
        loadDataFromDatabase();
        createOrUpdateTableView();
    }

    private void toTheMechPlanAddRemovePage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("view/MechPlanAddRemoveView.fxml"));
            Stage mechPlanAddStage = new Stage();
            mechPlanAddStage.setTitle("Технічний захід");
            mechPlanAddStage.setScene(new Scene(root, 600, 540));
            mechPlanAddStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void toTheSgPlanAddRemove() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("view/SgPlanAddRemoveView.fxml"));
            Stage sgPlanAddRemoveStage = new Stage();
            sgPlanAddRemoveStage.setTitle("Аграрний захід");
            sgPlanAddRemoveStage.setScene(new Scene(root, 600, 540));
            sgPlanAddRemoveStage.show();
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

    public void addRemoveButtonOnAction () {
        if (sgButtonStatus) {
            toTheSgPlanAddRemove();
        } else if (mechButtonStatus) {
            toTheMechPlanAddRemovePage();
        }
    }
}

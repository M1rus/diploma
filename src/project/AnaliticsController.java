package project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.*;

public class AnaliticsController {
    @FXML
    private Button returnButton;
    private String role;
    public void setRole(String role) {
        this.role = role;
    }
    @FXML
    private LineChart<String, Number> gettingsLine;
    @FXML
    private LineChart<String, Number> spendingsLine;
    @FXML
    private StackedBarChart <String, Number> productionSellingsStackedBar;

    public void initialize() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Diploma", "root", "Jks03211055__");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        gettingsLine.setTitle("Продажі");
        xAxis.setLabel("Рік");
        yAxis.setLabel("Total Price");
        spendingsLine.setTitle("Витрати");
        xAxis.setLabel("Year");
        yAxis.setLabel("Total Price");
        productionSellingsStackedBar.setTitle("Продажі продукції");
        xAxis.setLabel("Year");
        yAxis.setLabel("Total Price");

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT YEAR(DateOfCompletion) AS Year, SUM(TotalPrice) AS TotalPrice\n" +
                    "FROM ProductionSales\n" +
                    "WHERE DateOfCompletion IS NOT NULL\n" +
                    "GROUP BY YEAR(DateOfCompletion)\n" +
                    "ORDER BY YEAR(DateOfCompletion) ASC;");

            XYChart.Series<String, Number> gettings = new XYChart.Series<>();
            gettings.setName("Сума продажів");

            while (resultSet.next()) {
                String year = resultSet.getString("Year");
                double totalPrice = resultSet.getDouble("TotalPrice");
                gettings.getData().add(new XYChart.Data<>(year, totalPrice));
            }

            gettingsLine.getData().add(gettings);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT \n" +
                    "cr.Year,\n" +
                    "cr.TotalPriceSum,\n" +
                    "ma.MechActionSum,\n" +
                    "rs.RentSum\n" +
                    "FROM \n" +
                    "(\n" +
                    "SELECT \n" +
                    "YEAR(cr.DateOfCompletion) AS Year,\n" +
                    "SUM(cr.TotalPrice) AS TotalPriceSum\n" +
                    "FROM \n" +
                    "ConsumableRequest cr\n" +
                    "WHERE \n" +
                    "cr.Status = 'Завершено'\n" +
                    "AND YEAR(cr.DateOfCompletion) IS NOT NULL\n" +
                    "GROUP BY \n" +
                    "YEAR(cr.DateOfCompletion)\n" +
                    ") cr\n" +
                    "LEFT JOIN (\n" +
                    "SELECT \n" +
                    "YEAR(ma.PlannedDate) AS Year,\n" +
                    "SUM(ma.Price) AS MechActionSum\n" +
                    "FROM \n" +
                    "MechAction ma\n" +
                    "WHERE \n" +
                    "ma.Status = 'Завершено'\n" +
                    "AND YEAR(ma.PlannedDate) IS NOT NULL\n" +
                    "GROUP BY \n" +
                    "YEAR(ma.PlannedDate)\n" +
                    ") ma ON cr.Year = ma.Year\n" +
                    "CROSS JOIN (\n" +
                    "SELECT \n" +
                    "SUM(s.RentSum) AS RentSum\n" +
                    "FROM \n" +
                    "Share s\n" +
                    ") rs;");

            XYChart.Series<String, Number> spendings = new XYChart.Series<>();
            spendings.setName("Сума витрат");

            while (resultSet.next()) {
                String year = resultSet.getString("Year");
                float totalPrice = resultSet.getFloat("TotalPriceSum") + resultSet.getFloat("MechActionSum") + resultSet.getFloat("RentSum");
                spendings.getData().add(new XYChart.Data<>(year, totalPrice));
            }

            spendingsLine.getData().add(spendings);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT p.Name, YEAR(ps.DateOfCompletion) AS Year, SUM(ps.TotalPrice) AS TotalPrice\n" +
                    "FROM ProductionSales ps\n" +
                    "INNER JOIN Production p ON ps.idProductToSale = p.idProduction\n" +
                    "WHERE ps.DateOfCompletion IS NOT NULL\n" +
                    "GROUP BY p.Name, YEAR(ps.DateOfCompletion)\n" +
                    "ORDER BY p.Name, YEAR(ps.DateOfCompletion);");

            XYChart.Series<String, Number>[] seriesArray = new XYChart.Series[resultSet.getMetaData().getColumnCount() - 1];
            String[] namesArray = new String[seriesArray.length];

            for (int i = 0; i < seriesArray.length; i++) {
                seriesArray[i] = new XYChart.Series<>();
            }

            int seriesIndex = -1;
            String prevName = null;

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                String year = resultSet.getString("Year");
                double totalPrice = resultSet.getDouble("TotalPrice");

                if (!name.equals(prevName)) {
                    seriesIndex++;
                    namesArray[seriesIndex] = name;
                    prevName = name;
                }

                seriesArray[seriesIndex].getData().add(new XYChart.Data<>(year, totalPrice));
            }

            for (int i = 0; i <= seriesIndex; i++) {
                seriesArray[i].setName(namesArray[i]);
                productionSellingsStackedBar.getData().add(seriesArray[i]);
            }
        } catch (SQLException e) {
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

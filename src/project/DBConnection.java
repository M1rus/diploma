package project;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public Connection dbLink;

    public Connection getConnection () {
        String databaseName = "Diploma";
        String databaseUser = "root";
        String databasePassword = "Jks03211055__";
        String url = "jdbc:mysql://localhost/" + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbLink;
    }
}

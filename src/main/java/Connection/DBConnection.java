package Connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private  final  String URL = "jdbc:postgresql://localhost:5432/mini_dish_db";
    private final String USER = "mini_dish_db_manager";
    private final  String PASSWORD = "123456";

    public Connection getDBConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }
}

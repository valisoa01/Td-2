package Connection;


import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getDBConnection() {
        try {
            return DriverManager.getConnection(
                    System.getenv("JDBC_URL"),
                    System.getenv("USERNAME"),
                    System.getenv("PASSWORD")
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur de connexion DB", e);
        }
    }
}

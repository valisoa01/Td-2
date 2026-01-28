package Connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.io.InputStream;
import java.util.Properties;

public class DBConnection {

    public static Connection getDBConnection() {
        String url = System.getenv("JDBC_URL");
        String user = System.getenv("USERNAME");
        String pass = System.getenv("PASSWORD");

         if (url == null || user == null || pass == null) {
            try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    // first try generic jdbc.* keys
                    if (url == null) url = props.getProperty("jdbc.url");
                    if (user == null) user = props.getProperty("jdbc.username");
                    if (pass == null) pass = props.getProperty("jdbc.password");

                    // then try Spring Boot properties if still missing
                    if (url == null) url = props.getProperty("spring.datasource.url");
                    if (user == null) user = props.getProperty("spring.datasource.username");
                    if (pass == null) pass = props.getProperty("spring.datasource.password");
                }
            } catch (Exception ignored) {
            }
        }

        if (url == null || user == null || pass == null) {
            throw new RuntimeException("Missing DB credentials. Set JDBC_URL, USERNAME, PASSWORD or add jdbc.url/jdbc.username/jdbc.password to application.properties");
        }

        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de connexion DB", e);
        }
    }
}


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Update these values to match your MySQL server
private static final String URL = "jdbc:mysql://localhost:3306/ecomdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root"; // <-- replace with your MySQL username
    private static final String PASS = "aman2005"; // <-- replace with your MySQL password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // load driver
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found. Add connector JAR to classpath.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

import java.sql.Connection;
import java.sql.SQLException;

public class ECommerceApp {
    public static void main(String[] args) {
        System.out.println("=== E-Commerce System Starting ===");
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Database connected successfully!");
            } else {
                System.out.println("Database connection failed.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            return;
        }
        new ConsoleUI().start();
        System.out.println("E-Commerce System stopped.");
    }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutrition_db", "root", "A10254229sds@");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}

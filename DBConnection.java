
import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:orcl";
    private static final String USER = "colegperry";
    private static final String PASSWORD = "a7601";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

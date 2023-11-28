import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:orcl";
    private static final String USER = "hwp";
    private static final String PASSWORD = "a2301";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

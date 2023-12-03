
import java.sql.*;

/*
    Class: DBConnection
    Authors: Hayden Price, Cole Perry, Audrey Gagum
    External Packages: java.sql.*
    Inheritance: None

    Purpose:
    The DBConnection class provides a centralized means of establishing a connection to a specified Oracle database. 
    It encapsulates the details required to connect to the database, including the URL, user name, and password. 
    This class is utilized by other classes in the application for database operations.

    Public Class Constants and Variables:
    - URL: String - The URL of the Oracle database.
    - USER: String - The username for the database connection.
    - PASSWORD: String - The password for the database connection.

    Constructors:
    - None (default constructor is implicit)

    Implemented Methods:
    - getConnection(): Establishes and returns a Connection object for the Oracle database. Throws SQLException in case of connection failure.

    Exception Handling:
    - getConnection() method throws SQLException to indicate a failure in establishing a database connection.
*/
public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
    private static final String USER = "colegperry";
    private static final String PASSWORD = "a7601";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

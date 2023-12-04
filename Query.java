import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/*
    Class: Query
    Authors: Hayden Price, Cole Perry, Audrey Gagum
    External Packages: java.sql.*, java.util.Scanner
    Inheritance: None

    Purpose:
    The Query class is designed to execute predefined queries and a custom query on a fitness center's 
    database. It offers a menu-driven interface for executing queries such as listing members with a 
    negative balance, checking class schedules, and viewing trainers' working hours, in addition to 
    allowing custom queries based on user input.

    Public Class Constants and Variables: None

    Constructors:
    - None

    Implemented Methods:
    - executeQueries(): Main method to display query options and handle user selections.
    - Private utility methods to execute specific queries and display results.
*/
public class Query {

    /*
     * Method: executeQueries
     * Purpose: Provides a menu-driven interface for users to select and execute
     * specific queries or a custom query
     * on the database. Options include listing members with negative balances,
     * checking class schedules, and
     * viewing trainers' working hours.
     * Pre-conditions: None
     * Post-conditions: The selected query is executed, and its results are
     * displayed.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during query
     * execution.
     */
    public static void executeQueries() {
        try (Connection conn = DBConnection.getConnection()) {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Select a query to execute:");
                System.out.println("1: List all members with a negative balance");
                System.out.println("2: Check a member's class schedule for November");
                System.out.println("3: Check all trainers' working hours for December");
                System.out.println("4: Custom query (requires user input)");
                System.out.println("5: Exit");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        listMembersWithNegativeBalance(conn);
                        break;
                    case 2:
                        checkMemberClassScheduleForNovember(conn, scanner);
                        break;
                    case 3:
                        checkTrainersWorkingHoursForDecember(conn);
                        break;
                    case 4:
                        executeCustomQuery(conn, scanner);
                        break;
                    case 5:
                    	conn.close();
                        System.out.println("Exiting query interface...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Method: listMembersWithNegativeBalance
     * Purpose: Retrieves and displays a list of members who have a negative
     * balance.
     * Pre-conditions: None
     * Post-conditions: Members with negative balances are printed to the console.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void listMembersWithNegativeBalance(Connection conn) throws SQLException {
        String sql = "SELECT fname, lname, phonenum FROM member WHERE totalspent > totalPaid";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("fname") + " " + rs.getString("lname");
                String phone = rs.getString("phonenum");
                System.out.println("Member: " + name + ", Phone: " + phone);
            }
        }
    }

    /*
     * Method: checkMemberClassScheduleForNovember
     * Purpose: Retrieves and displays the class schedule for a given member for the
     * month of November.
     * Pre-conditions: Member ID must be valid and existing in the database.
     * Post-conditions: The class schedule for the specified member in November is
     * printed.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - scanner: Scanner (in) - Scanner object for reading user input.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void checkMemberClassScheduleForNovember(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter Member ID:");
        int memberId = scanner.nextInt();

        String sql = "SELECT c.courseName, c.sdate, c.edate " +
                "FROM colegperry.course c " +
                "JOIN colegperry.coursePackage cp ON c.courseID = cp.firstclassID OR c.courseID = cp.secondclassID " +
                "WHERE cp.packagenum IN (SELECT curpackageID FROM member WHERE memberID = ?) " +
                "AND TO_CHAR(c.sdate, 'YYMM') = '2311'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String courseName = rs.getString("courseName");
                Date startDate = rs.getDate("sdate");
                Date endDate = rs.getDate("edate");
                System.out.println("Course: " + courseName + ", Start Date: " + startDate + ", End Date: " + endDate);
            }
        }
    }

    /*
     * Method: checkTrainersWorkingHoursForDecember
     * Purpose: Retrieves and displays the working hours of trainers for the month
     * of December.
     * Pre-conditions: None
     * Post-conditions: Trainers' working hours in December are printed to the
     * console.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void checkTrainersWorkingHoursForDecember(Connection conn) throws SQLException {
        String sql = "SELECT t.trainername, COUNT(*) as totalClasses FROM colegperry.trainer t JOIN colegperry.trainClass tc ON t.trainerID = tc.trainerID JOIN colegperry.course c ON tc.classID = c.courseID WHERE EXTRACT(MONTH FROM c.sdate) = 12 AND EXTRACT(YEAR FROM c.sdate) = EXTRACT(YEAR FROM CURRENT_DATE) GROUP BY t.trainername";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String trainerName = rs.getString("trainername");
                int totalClasses = rs.getInt("totalClasses");
                System.out.println("Trainer: " + trainerName + ", Total Classes in December: " + totalClasses);
            }
        }
    }

    /*
     * Method: executeCustomQuery
     * Purpose: Allows the execution of a custom query based on user input.
     * Specifically, it retrieves the names
     * of members enrolled in courses taught by a specific trainer.
     * Pre-conditions: Trainer ID must be valid and existing in the database.
     * Post-conditions: Members enrolled in the specified trainer's courses are
     * printed.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - scanner: Scanner (in) - Scanner object for reading user input.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void executeCustomQuery(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter Trainer ID:");
        int trainerId = scanner.nextInt();

        String sql = "SELECT m.fname, m.lname FROM member m JOIN coursePackage cp ON m.curpackageID = cp.packagenum JOIN trainClass tc ON cp.firstclassID = tc.classID OR cp.secondclassID = tc.classID WHERE tc.trainerID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("fname") + " " + rs.getString("lname");
                System.out.println("Member: " + name);
            }
        }
    }

}

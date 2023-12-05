import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/*
    Class: Update
    Authors: Hayden Price, Cole Perry, Audrey Gagum
    External Packages: java.sql.*, java.util.Scanner
    Inheritance: None

    Purpose:
    The Update class is dedicated to updating records in the coursePackage table of a fitness center's 
    database. It provides functionality to modify existing course packages, including adding new courses, 
    updating existing courses, or removing courses from a package.

    Public Class Constants and Variables: None

    Constructors:
    - None

    Implemented Methods:
    - updateCoursePackage(): Provides an interface to select a course package and choose an update option.
    - Private utility methods to display course packages and perform specific update operations.
*/
public class Update {

	
	
	
	
	
    /*
     * Method: updateRecord
     * Purpose: Displays a menu for the user to select the type of record they wish
     * to update in the database.
     * The method then calls the appropriate method based on the user's choice.
     * Pre-conditions: None
     * Post-conditions: The selected record is updated in the database, or an
     * invalid choice message is displayed.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - None within this method, but methods called from it may handle exceptions,
     * particularly SQL exceptions.
     */
    public static void updateRecord() {
        // Checks what kind of insert the user would like to make
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select a table to insert record:");
        System.out.println("1: Update Course Package");
        System.out.println("2: Add funds to user account");

        int tableChoice = scanner.nextInt();

        switch (tableChoice) {
            case 1:
                updateCoursePackage();
                break;
            case 2:
                addFundsToMember();
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }
    
    /*
     * Method: addFundsToMember
     * Purpose: Asks the user for an amount, which is the amount the member
     * is going to 'pay off' for their account. This amount is added to the
     * user's "amountpaid" attribute.
     * Pre-conditions: None
     * Post-conditions: The selected record is updated in the database, or an
     * invalid choice message is displayed.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - None within this method, but methods called from it may handle exceptions,
     * particularly SQL exceptions.
     */
    public static void addFundsToMember() {
        try (Connection conn = DBConnection.getConnection()) {
            Scanner scanner = new Scanner(System.in);


            // Ask the admin to select a package for editing
            System.out.print("Enter the ID of the Member account to add funds to:");
            int userID = scanner.nextInt();
            
            System.out.println("Enter the amount of money (floating point value):");
            float amount = scanner.nextFloat();
            String sql = "UPDATE colegperry.member SET totalpaid = " + amount + "WHERE memberID = " + userID;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
            
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//    UPDATE table_name
//    SET column_name = new_value
//    WHERE condition;
    /*
     * Method: updateCoursePackage
     * Purpose: Provides a user interface to select a course package and then choose
     * to either update an existing
     * course in the package, add a new course to it, or remove a course from it.
     * Pre-conditions: None
     * Post-conditions: The selected course package is updated as per the user's
     * choice.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the update
     * operations.
     */
    public static void updateCoursePackage() {
        try (Connection conn = DBConnection.getConnection()) {
            Scanner scanner = new Scanner(System.in);

            // Display all course packages
            displayCoursePackages(conn);

            // Ask the admin to select a package for editing
            System.out.println("Enter the ID of the Course Package to edit:");
            int packageId = scanner.nextInt();

            // Show editing options
            System.out.println("Choose an option:");
            System.out.println("1: Update existing course in the package");
            System.out.println("2: Add a new course to the package");
            System.out.println("3: Remove a course from the package");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    updateExistingCourseInPackage(conn, packageId, scanner);
                    break;
                case 2:
                    addNewCourseToPackage(conn, packageId, scanner);
                    break;
                case 3:
                    removeCourseFromPackage(conn, packageId, scanner);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Method: displayCoursePackages
     * Purpose: Displays all available course packages from the database.
     * Pre-conditions: None
     * Post-conditions: Course package details are printed to the console.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void displayCoursePackages(Connection conn) throws SQLException {
        String sql = "SELECT * FROM coursePackage";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int packagenum = rs.getInt("packagenum");
                String packagename = rs.getString("packagename");
                System.out.println("Package ID: " + packagenum + ", Package Name: " + packagename);
            }
        }
    }

    /*
     * Method: updateExistingCourseInPackage
     * Purpose: Updates an existing course in a selected package with a new course
     * ID.
     * Pre-conditions: Valid package and course IDs must be provided.
     * Post-conditions: The course in the package is updated with the new course ID.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - packageId: int (in) - The ID of the course package to update.
     * - scanner: Scanner (in) - Scanner object for reading user input.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void updateExistingCourseInPackage(Connection conn, int packageId, Scanner scanner)
            throws SQLException {
        System.out.println("Enter the ID of the course to update:");
        int courseId = scanner.nextInt();

        System.out.println("Enter the new course ID:");
        int newCourseId = scanner.nextInt();

        String sql = "UPDATE coursePackage SET firstclassID = CASE WHEN firstclassID = ? THEN ? ELSE firstclassID END, secondclassID = CASE WHEN secondclassID = ? THEN ? ELSE secondclassID END WHERE packagenum = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, newCourseId);
            stmt.setInt(3, courseId);
            stmt.setInt(4, newCourseId);
            stmt.setInt(5, packageId);
            stmt.executeUpdate();
            System.out.println("Course updated successfully.");
        }
    }

    /*
     * Method: addNewCourseToPackage
     * Purpose: Adds a new course to a selected package, assuming the package can
     * have two courses.
     * Pre-conditions: Valid package ID and new course ID must be provided.
     * Post-conditions: The new course is added to the package if there's an
     * available slot.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - packageId: int (in) - The ID of the course package to update.
     * - scanner: Scanner (in) - Scanner object for reading user input.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void addNewCourseToPackage(Connection conn, int packageId, Scanner scanner) throws SQLException {
        System.out.println("Enter the ID of the new course to add:");
        int newCourseId = scanner.nextInt();

        // Assuming coursePackage can have two courses (firstclassID and secondclassID)
        String sql = "UPDATE coursePackage SET firstclassID = COALESCE(firstclassID, ?), secondclassID = CASE WHEN firstclassID IS NOT NULL THEN COALESCE(secondclassID, ?) ELSE secondclassID END WHERE packagenum = ? AND (firstclassID IS NULL OR secondclassID IS NULL)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newCourseId);
            stmt.setInt(2, newCourseId);
            stmt.setInt(3, packageId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No available slot to add a new course in the package.");
            } else {
                System.out.println("Course added successfully.");
            }
        }
    }

    /*
     * Method: removeCourseFromPackage
     * Purpose: Removes a specified course from a selected package.
     * Pre-conditions: Valid package ID and course ID must be provided.
     * Post-conditions: The specified course is removed from the package.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - packageId: int (in) - The ID of the course package to update.
     * - scanner: Scanner (in) - Scanner object for reading user input.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void removeCourseFromPackage(Connection conn, int packageId, Scanner scanner) throws SQLException {
        System.out.println("Enter the ID of the course to remove:");
        int courseId = scanner.nextInt();

        String sql = "UPDATE coursePackage SET firstclassID = CASE WHEN firstclassID = ? THEN NULL ELSE firstclassID END, secondclassID = CASE WHEN secondclassID = ? THEN NULL ELSE secondclassID END WHERE packagenum = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, courseId);
            stmt.setInt(3, packageId);
            stmt.executeUpdate();
            System.out.println("Course removed successfully.");
        }
    }
}

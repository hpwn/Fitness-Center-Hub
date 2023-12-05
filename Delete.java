import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/*
    Class: Delete
    Authors: Hayden Price, Cole Perry, Audrey Gagum
    External Packages: java.sql.*, java.util.Scanner
    Inheritance: None

    Purpose:
    The Delete class provides functionality to delete records from various tables in a fitness center's 
    database. It allows users to select a table (like Member, Course, CoursePackage) and delete specific 
    records based on input criteria such as Member ID or Course ID.

    Public Class Constants and Variables: None

    Constructors:
    - None

    Implemented Methods:
    - deleteRecord(): Offers a menu to select a table from which to delete a record and calls the appropriate method.
    - deleteMemberRecord(): Deletes a member record after performing various checks like unpaid balances.
    - deleteCourseRecord(): Deletes a course record, ensuring no active enrollments are impacted.
    - deleteCoursePackageRecord(): Deletes a course package record, checking if it's currently in use.
    - Other private utility methods for specific checks and deletions in the database.
*/
public class Delete {

    /*
     * Method: deleteRecord
     * Purpose: Provides a menu-driven interface for the user to select a table and
     * then calls the corresponding
     * method to handle the deletion of a record from that table.
     * Pre-conditions: None
     * Post-conditions: A record is deleted from the selected table, or an invalid
     * choice message is displayed.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - None within this method, but methods called from it may handle exceptions,
     * particularly SQL exceptions.
     */
    public static void deleteRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select a table to delete record from:");
        System.out.println("1: Member");
        System.out.println("2: Course");
        System.out.println("3: CoursePackage");
        int tableChoice = scanner.nextInt();

        switch (tableChoice) {
            case 1:
                deleteMemberRecord();
                break;
            case 2:
                deleteCourseRecord();
                break;
            case 3:
                deleteCoursePackageRecord();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /*
     * Method: deleteMemberRecord
     * Purpose: Handles the deletion of a member record from the database. It
     * performs checks for unreturned
     * equipment and unpaid balances, and handles active course participation before
     * deleting the member.
     * Pre-conditions: Member ID must be valid and existing in the database.
     * Post-conditions: If all checks pass, the member record is deleted from the
     * database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operations.
     */
    public static void deleteMemberRecord() {
        try (Connection conn = DBConnection.getConnection()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter Member ID to delete:");
            int memberId = scanner.nextInt();

            // Check for unreturned equipment
            checkAndMarkUnreturnedEquipment(conn, memberId);

            // Check for unpaid balances
            if (hasUnpaidBalances(conn, memberId)) {
                return; // Exit if there are unpaid balances
            }

            // Check and handle active course participation
            handleActiveCourseParticipation(conn, memberId);

            // Delete the member record
            deleteMember(conn, memberId);
            conn.close();
            System.out.println("Member record deleted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Method: checkAndMarkUnreturnedEquipment
     * Purpose: Checks for any unreturned equipment associated with the given member
     * ID and marks them as lost.
     * Pre-conditions: Member ID must be valid and existing in the database.
     * Post-conditions: All unreturned equipment for the member is marked as lost.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - memberId: int (in) - The ID of the member to check for unreturned
     * equipment.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void checkAndMarkUnreturnedEquipment(Connection conn, int memberId) throws SQLException {
        String sql = "UPDATE item SET lost = 1 WHERE memberID = ? AND checkout IS NOT NULL AND checkin IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
    }

    /*
     * Method: hasUnpaidBalances
     * Purpose: Checks if the specified member has any unpaid balances.
     * Pre-conditions: Member ID must be valid and existing in the database.
     * Post-conditions: Returns true if there are unpaid balances, false otherwise.
     * Return value: boolean - Indicates whether the member has unpaid balances.
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - memberId: int (in) - The ID of the member to check for unpaid balances.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static boolean hasUnpaidBalances(Connection conn, int memberId) throws SQLException {
        String sql = "SELECT SUM(totalPaid - totalspent) AS balance FROM colegperry.member WHERE memberID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getDouble("balance") < 0) {
                System.out.println("Member has unpaid balances. Cannot delete.");
                conn.close();
                return true;
            }
        }
        return false;
    }

    /*
     * Method: handleActiveCourseParticipation
     * Purpose: Deletes any active course participation records for the specified
     * member.
     * Pre-conditions: Member ID must be valid and existing in the database.
     * Post-conditions: All active course participation for the member is deleted.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - memberId: int (in) - The ID of the member whose course participation is to
     * be deleted.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void handleActiveCourseParticipation(Connection conn, int memberId) throws SQLException {
        String sql = "DELETE FROM colegperry.member WHERE memberID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
    }

    /*
     * Method: deleteMember
     * Purpose: Deletes the member record for the specified member ID from the
     * database.
     * Pre-conditions: Member ID must be valid and existing in the database.
     * Post-conditions: The member record is deleted from the database.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - memberId: int (in) - The ID of the member to be deleted.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void deleteMember(Connection conn, int memberId) throws SQLException {
        String sql = "DELETE FROM colegperry.member WHERE memberID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
    }

    /*
     * Method: deleteCourseRecord
     * Purpose: Deletes a course record from the database. It first checks if the
     * course has active enrollments
     * and notifies enrolled members before deletion.
     * Pre-conditions: Course ID must be valid and existing in the database.
     * Post-conditions: Course record, along with any corresponding enrollments, is
     * deleted from the database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operations.
     */
    public static void deleteCourseRecord() {
        try (Connection conn = DBConnection.getConnection()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter Course ID to delete:");
            int courseId = scanner.nextInt();

            if (courseHasActiveEnrollments(conn, courseId)) {
                printEnrolledMembers(conn, courseId);
                // Wait for confirmation to proceed after notifying members
                System.out.println("Press 'Y' to confirm deletion after notifying members:");
                String confirmation = scanner.next();
                if (!confirmation.equalsIgnoreCase("Y")) {
                	conn.close();
                    return; // Exit if not confirmed
                }
            }

            deleteCourse(conn, courseId);
            conn.close();
            System.out.println("Course and corresponding enrollments deleted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Method: courseHasActiveEnrollments
     * Purpose: Checks if the specified course has any active enrollments.
     * Pre-conditions: Course ID must be valid and existing in the database.
     * Post-conditions: Returns true if there are active enrollments, false
     * otherwise.
     * Return value: boolean - Indicates whether the course has active enrollments.
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - courseId: int (in) - The ID of the course to check for active enrollments.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static boolean courseHasActiveEnrollments(Connection conn, int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM coursePackage WHERE firstclassID = ? OR secondclassID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /*
     * Method: printEnrolledMembers
     * Purpose: Prints the details of members enrolled in the specified course.
     * Pre-conditions: Course ID must be valid and existing in the database.
     * Post-conditions: Member details for those enrolled in the course are printed.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - courseId: int (in) - The ID of the course whose enrolled members are to be
     * printed.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void printEnrolledMembers(Connection conn, int courseId) throws SQLException {
        String sql = "SELECT m.fname, m.lname, m.phonenum FROM member m JOIN coursePackage cp ON m.curpackageID = cp.packagenum WHERE cp.firstclassID = ? OR cp.secondclassID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("fname") + " " + rs.getString("lname");
                String phone = rs.getString("phonenum");
                System.out.println("Member: " + name + ", Phone: " + phone);
            }
        }
    }

    /*
     * Method: deleteCourse
     * Purpose: Deletes the course record for the specified course ID from the
     * database, along with any
     * associated references in the coursePackage table.
     * Pre-conditions: Course ID must be valid and existing in the database.
     * Post-conditions: The course record and associated references are deleted from
     * the database.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - courseId: int (in) - The ID of the course to be deleted.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void deleteCourse(Connection conn, int courseId) throws SQLException {
        // Update coursePackage table to remove references to the deleted course
        String sqlUpdatePackage = "UPDATE coursePackage SET firstclassID = NULL WHERE firstclassID = " + courseId;
        try (PreparedStatement stmt = conn.prepareStatement(sqlUpdatePackage)) {
            stmt.executeUpdate();
            sqlUpdatePackage = "UPDATE coursePackage SET secondclassID = NULL WHERE secondclassID = " + courseId;
            PreparedStatement stm = conn.prepareStatement(sqlUpdatePackage);
            stm.executeUpdate();
        }

        // Delete the course
        String sqlDeleteCourse = "DELETE FROM course WHERE courseID = " + courseId;
        try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteCourse)) {
            stmt.executeUpdate();
        }
        conn.close();
    }

    /*
     * Method: deleteCoursePackageRecord
     * Purpose: Deletes a course package record from the database after checking if
     * it's currently in use by members.
     * Pre-conditions: Package ID must be valid and existing in the database.
     * Post-conditions: Course package record is deleted from the database if not in
     * use.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operations.
     */
    public static void deleteCoursePackageRecord() {
        try (Connection conn = DBConnection.getConnection()) {
            Scanner scanner = new Scanner(System.in);

            // Display all course packages
            displayCoursePackages(conn);

            // Ask the admin to select a package for deletion
            System.out.println("Enter the ID of the Course Package to delete:");
            int packageId = scanner.nextInt();

            // Check if deleting the package will affect enrolled members
            if (isPackageInUse(conn, packageId)) {
                System.out.println("Cannot delete package as it is currently in use by members.");
                return; // Exit if the package is in use
            }

            // Proceed with deletion
            deleteCoursePackage(conn, packageId);
            System.out.println("Course package deleted successfully.");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Method: displayCoursePackages
     * Purpose: Displays all the course packages from the database.
     * Pre-conditions: None
     * Post-conditions: All course packages are displayed to the user.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void displayCoursePackages(Connection conn) throws SQLException {
        String sql = "SELECT * FROM colegperry.coursePackage";
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
     * Method: isPackageInUse
     * Purpose: Checks if the specified course package is currently in use by any
     * members.
     * Pre-conditions: Package ID must be valid and existing in the database.
     * Post-conditions: Returns true if the package is in use, false otherwise.
     * Return value: boolean - Indicates whether the package is in use.
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - packageId: int (in) - The ID of the course package to check.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static boolean isPackageInUse(Connection conn, int packageId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM member WHERE curpackageID = " + packageId;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /*
     * Method: deleteCoursePackage
     * Purpose: Deletes the course package record for the specified package ID from
     * the database.
     * Pre-conditions: Package ID must be valid and existing in the database.
     * Post-conditions: The course package record is deleted from the database.
     * Return value: None
     * Parameters:
     * - conn: Connection (in) - The database connection.
     * - packageId: int (in) - The ID of the course package to be deleted.
     * 
     * Exception Handling:
     * - Throws SQLException for any SQL-related errors encountered.
     */
    private static void deleteCoursePackage(Connection conn, int packageId) throws SQLException {
        String sql = "DELETE FROM colegperry.coursePackage WHERE packagenum = " + packageId;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}

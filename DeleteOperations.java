import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DeleteOperations {
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

            System.out.println("Member record deleted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void checkAndMarkUnreturnedEquipment(Connection conn, int memberId) throws SQLException {
        String sql = "UPDATE item SET lost = 1 WHERE memberID = ? AND checkout IS NOT NULL AND checkin IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
    }

    private static boolean hasUnpaidBalances(Connection conn, int memberId) throws SQLException {
        String sql = "SELECT SUM(totalPaid - totalspent) AS balance FROM member WHERE memberID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getDouble("balance") < 0) {
                System.out.println("Member has unpaid balances. Cannot delete.");
                return true;
            }
        }
        return false;
    }

    private static void handleActiveCourseParticipation(Connection conn, int memberId) throws SQLException {
        String sql = "DELETE FROM course WHERE memberID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
    }

    private static void deleteMember(Connection conn, int memberId) throws SQLException {
        String sql = "DELETE FROM member WHERE memberID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
    }

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
                    return; // Exit if not confirmed
                }
            }

            deleteCourse(conn, courseId);
            System.out.println("Course and corresponding enrollments deleted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean courseHasActiveEnrollments(Connection conn, int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM coursePackage WHERE firstclassID = ? OR secondclassID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

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

    private static void deleteCourse(Connection conn, int courseId) throws SQLException {
        // Update coursePackage table to remove references to the deleted course
        String sqlUpdatePackage = "UPDATE coursePackage SET firstclassID = NULL WHERE firstclassID = ?; UPDATE coursePackage SET secondclassID = NULL WHERE secondclassID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlUpdatePackage)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, courseId);
            stmt.executeUpdate();
        }

        // Delete the course
        String sqlDeleteCourse = "DELETE FROM course WHERE courseID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteCourse)) {
            stmt.setInt(1, courseId);
            stmt.executeUpdate();
        }
    }

    private static void deleteCoursePackageRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID number of the Course Package you would like to remove: ");
        String IDToRemove = scanner.nextLine();

        String deleteQuery = "DELETE FROM colegperry.coursePackage WHERE packageID = " + IDToRemove;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Course Package Removed, " + rowsAffected + " row(s) affected.");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

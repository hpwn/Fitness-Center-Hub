import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Delete {
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
    }

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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    private static boolean isPackageInUse(Connection conn, int packageId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM member WHERE curpackageID = " + packageId;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private static void deleteCoursePackage(Connection conn, int packageId) throws SQLException {
        String sql = "DELETE FROM colegperry.coursePackage WHERE packagenum = " + packageId;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}

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

    private static void deleteMemberRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID number of the Member you would like to remove: ");
        String memberID = scanner.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            // Check for Unreturned Equipment
            if (hasUnreturnedEquipment(conn, memberID)) {
                markEquipmentAsLost(conn, memberID);
                updateAvailableEquipmentQuantity(conn, memberID);
            }

            // Check for Unpaid Balances
            if (hasUnpaidBalances(conn, memberID)) {
                System.out.println("Member has unpaid balances. Cannot delete account.");
                return; // Prevent deletion
            }

            // Check for Active Course Participation and Delete
            // deleteCourseParticipations(conn, memberID);

            // Finally, Delete Member
            String deleteQuery = "DELETE FROM colegperry.member WHERE memberID = " + memberID;
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.executeUpdate();
                System.out.println("Member Removed");
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasUnreturnedEquipment(Connection conn, String memberID) throws SQLException {
        String query = "SELECT COUNT(*) FROM item WHERE memberID = ? AND checkout IS NOT NULL AND lost = 0";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, memberID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private static void markEquipmentAsLost(Connection conn, String memberID) throws SQLException {
        String update = "UPDATE item SET lost = 1 WHERE memberID = ? AND checkout IS NOT NULL";
        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, memberID);
            stmt.executeUpdate();
        }
    }

    private static void updateAvailableEquipmentQuantity(Connection conn, String memberID) throws SQLException {
        // Assuming 'qty' in 'item' table represents the total available quantity for
        // each item type
        String update = "UPDATE item SET qty = qty - 1 WHERE memberID = ? AND checkout IS NOT NULL AND lost = 1";
        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, memberID);
            stmt.executeUpdate();
        }
    }

    private static boolean hasUnpaidBalances(Connection conn, String memberID) throws SQLException {
        // Assuming there is a way to determine unpaid transactions.
        String query = "SELECT COUNT(*) FROM transaction,member WHERE memberID = ? AND totalspent-totalpaid > 0";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, memberID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private static void deleteCourseParticipations(Connection conn, String memberID) throws SQLException {
        // Assuming a hypothetical table 'course_enrollment'... what should we do here
        // we need a way to track who's enrolled in what right? because when I start to
        // create the delete course function,
        // I need to know who's enrolled in what course so I can delete them, etc, etc
        String delete = "DELETE FROM course_enrollment WHERE memberID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setString(1, memberID);
            stmt.executeUpdate();
        }
    }

    private static void deleteCourseRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID number of the Course you would like to remove: ");
        String IDToRemove = scanner.nextLine();

        String deleteQuery = "DELETE FROM colegperry.course WHERE courseID = " + IDToRemove;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Course Removed, " + rowsAffected + " row(s) affected.");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
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

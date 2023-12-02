import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Update {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
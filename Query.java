import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Query {

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

    private static void checkMemberClassScheduleForNovember(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter Member ID:");
        int memberId = scanner.nextInt();

        String sql = "SELECT c.courseName, c.sdate, c.edate FROM course c JOIN coursePackage cp ON c.courseID = cp.firstclassID OR c.courseID = cp.secondclassID WHERE cp.packagenum IN (SELECT curpackageID FROM member WHERE memberID = ?) AND MONTH(c.sdate) = 11 AND YEAR(c.sdate) = YEAR(CURRENT_DATE)";
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

    private static void checkTrainersWorkingHoursForDecember(Connection conn) throws SQLException {
        String sql = "SELECT t.trainername, COUNT(*) as totalClasses FROM trainer t JOIN trainClass tc ON t.trainerID = tc.trainerID JOIN course c ON tc.classID = c.courseID WHERE MONTH(c.sdate) = 12 AND YEAR(c.sdate) = YEAR(CURRENT_DATE) GROUP BY t.trainername";
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
     * This custom query retrieves the names of members who are enrolled in courses
     * taught by a specific trainer, identified by trainerId.
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
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class FitnessCenterApp {
    private static Scanner scanner = new Scanner(System.in);
    static int transactionID = 150;

    public static void main(String[] args) {
        try {
            // Gathering what the user would like to do
            while (true) {
                System.out.println("Choose an operation:");
                System.out.println("1: Insert Record");
                System.out.println("2: Delete Record");
                System.out.println("3: Update Record");
                System.out.println("4: Execute Query");
                System.out.println("5: Exit");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        insertRecord();
                        break;
                    case 2:
                        deleteRecord();
                        break;
                    case 3:
                        updateRecord();
                        break;
                    case 4:
                        executeQuery();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertRecord() {
        // Checks what kind of insert the user would like to make
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select a table to insert record:");
        System.out.println("1: Member");
        System.out.println("2: MemLevel");
        System.out.println("3: Course");
        System.out.println("4: Trainer");
        System.out.println("5: TrainClass");
        System.out.println("6: Item");
        System.out.println("7: Transaction");
        System.out.println("8: CoursePackage");
        int tableChoice = scanner.nextInt();

        switch (tableChoice) {
            case 1:
                insertMemberRecord();
                break;
            case 2:
                insertMemLevelRecord();
                break;
            case 3:
                insertCourseRecord();
                break;
            case 4:
                insertTrainerRecord();
                break;
            case 5:
                insertTrainClassRecord();
                break;
            case 6:
                insertItemRecord();
                break;
            case 7:
                insertTransactionRecord();
                break;
            case 8:
                insertCoursePackageRecord();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void insertMemberRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(
                "Enter member ID, first name, last name, phone number (comma-separated and no spaces): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(";
        // Building the statement string
        for (int i = 0; i < params.length; i++) {
            if (i == params.length - 1) {
                statementString += params[i] + "";
            } else if (i < 1 || i > 3) {
                statementString += params[i] + ",";
            } else {
                statementString += "'" + params[i] + "',";
            }

        }
        // Gathering the packages the user can select and adding them to the list along
        // with IDs
        String packagesQuery = "SELECT packagenum,packagename FROM colegperry.coursePackage";
        HashMap<String, Integer> packageInfo = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
                // Executing query and reading results
                Statement stmt = conn.createStatement()) {
            ResultSet answer = stmt.executeQuery(packagesQuery);

            System.out.println("Here are our available packages, which one would you like?\n");

            while (answer.next()) {
                packageInfo.put(answer.getString("packagename"), answer.getInt("packagenum"));
                System.out.println("Package: " + answer.getString("packagename"));
            }

            System.out.print("\nPlease enter a package name: ");
            String packageName = scanner.nextLine();

            // Adding the package ID to the user record
            statementString += "," + packageInfo.get(packageName) + ",NULL,";
            answer = stmt.executeQuery("SELECT packagecost FROM colegperry.coursePackage WHERE packagenum="
                    + packageInfo.get(packageName));
            BigDecimal packageCost = null;
            if (answer.next()) {
                System.out.println("\n");
                packageCost = answer.getBigDecimal("packagecost");
                statementString += packageCost + ")";
                // Use package cost as needed
            }
            // Executing the insert
            executeInsert("INSERT INTO colegperry.member VALUES " + statementString);

            // Finding which member level the member belongs to based on their purchase and
            // updating

            // If a member(s) is found containing the minSpent value higher than the package
            // cost value
            int theID = 0;
            BigDecimal maxAmount = null;
            while (answer.next()) {
                int curID = answer.getInt("levelID");
                BigDecimal curAmount = answer.getBigDecimal("minspent");
                if (maxAmount == null || maxAmount.compareTo(curAmount) < 0) {
                    maxAmount = answer.getBigDecimal("minspent");
                    theID = curID;
                }
            }
            // Executing the update
            String updateQuery = "UPDATE colegperry.member SET memlevelID = " + theID + " WHERE memberID =" + params[0];

            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            pstmt.executeUpdate();

            // Adding the transaction to the transaction table
            String transactionSQL = "INSERT INTO colegperry.transaction VALUES (" + transactionID++ + "," + maxAmount
                    + ", SYSDATE, "
                    + "'Course Package Purchase')";
            insertTransactionRecord(transactionSQL);

            // Closing connection to DB
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void insertMemLevelRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Level ID, Level Name, Discount, Minimum Spent (comma-separated and no spaces): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + ",'" + params[1] + "'," + params[2] + "," + params[3] + ")";

        // Constructing the statement string

        executeInsert("INSERT INTO colegperry.memlevel VALUES " + statementString);
    }

    private static void insertCourseRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter Course ID, Course Name, Start Time, Start Date (yyyy-mm-dd), End Date (yyyy-mm-dd) (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + ",'" + params[1] + "'," + params[2] + ",TO_DATE('" + params[3] +
                "','YYYY-MM-DD'),TO_DATE('" + params[4] + "','YYYY-MM-DD'))";

        executeInsert(
                "INSERT INTO colegperry.course VALUES " + statementString);
    }

    private static void insertTrainerRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Trainer ID, Trainer Name, Phone Number (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + ",'" + params[1] + "','" + params[2] + "')";

        executeInsert("INSERT INTO colegperry.trainer VALUES " + statementString);
    }

    private static void insertTrainClassRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Trainer ID, Class ID (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + "," + params[1] + ")";

        executeInsert("INSERT INTO colegperry.trainClass VALUES " + statementString);
    }

    private static void insertItemRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter Item ID, Member ID, Check-in Time, Check-out Time, Quantity, Lost Status (0 or 1) (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + "," + params[1] + "," + params[2] + "," + params[3] + "," + params[4]
                + "," + params[5] + ")";

        executeInsert("INSERT INTO colegperry.item VALUES " + statementString);
    }

    private static void insertTransactionRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter Transaction ID, Amount, Transaction Date (yyyy-mm-dd), Transaction Type (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + "," + params[1] + ", TO_DATE('" + params[2] + "','YYYY-MM-DD'),'"
                + params[3] + "')";

        executeInsert("INSERT INTO colegperry.transaction VALUES " + statementString);
    }

    private static void insertTransactionRecord(String sql) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertCoursePackageRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Package Number, Package Name, First Class ID, Second Class ID (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + ",'" + params[1] + "'," + params[2] + "," + params[3] + ")";

        executeInsert("INSERT INTO colegperry.coursePackage VALUES " + statementString);
    }

    private static void executeInsert(String sql) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteRecord() {
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
            deleteCourseParticipations(conn, memberID);

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
        String query = "SELECT COUNT(*) FROM transaction WHERE memberID = ? AND /* condition for unpaid balances */";
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

    private static void updateRecord() {
        // Implementation of record update
    }

    private static void executeQuery() {
        // Implementation of executing a query
    }
}

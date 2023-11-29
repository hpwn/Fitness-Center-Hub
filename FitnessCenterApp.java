
import java.sql.*;
import java.util.Scanner;

public class FitnessCenterApp {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
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

    // Method implementations for inserting into each table

    private static void insertMemberRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter member ID, first name, last name, phone number, membership level ID, total spent (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");

        executeInsert("INSERT INTO colegperry.member VALUES (?, ?, ?, ?, ?, ?)", params);
    }

    private static void insertMemLevelRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Level ID, Level Name, Discount, Minimum Spent (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");

        executeInsert("INSERT INTO colegperry.memlevel VALUES (?, ?, ?, ?)", params);
    }

    private static void insertCourseRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter Course ID, Course Name, Start Time, Start Date (yyyy-mm-dd), End Date (yyyy-mm-dd) (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");

        executeInsert(
                "INSERT INTO colegperry.course VALUES (?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'))",
                params);
    }

    private static void insertTrainerRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Trainer ID, Trainer Name, Phone Number (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");

        executeInsert("INSERT INTO colegperry.trainer VALUES (?, ?, ?)", params);
    }

    private static void insertTrainClassRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Trainer ID, Class ID (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");

        executeInsert("INSERT INTO colegperry.trainClass VALUES (?, ?)", params);
    }

    private static void insertItemRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter Item ID, Member ID, Check-in Time, Check-out Time, Quantity, Lost Status (0 or 1) (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");

        executeInsert("INSERT INTO colegperry.item VALUES (?, ?, ?, ?, ?, ?)", params);
    }

    private static void insertTransactionRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter Transaction ID, Amount, Transaction Date (yyyy-mm-dd), Transaction Type (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");

        executeInsert("INSERT INTO colegperry.transaction VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)", params);
    }

    private static void insertCoursePackageRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Package Number, Package Name, First Class ID, Second Class ID (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");

        executeInsert("INSERT INTO colegperry.coursePackage VALUES (?, ?, ?, ?)", params);
    }

    private static void executeInsert(String sql, String[] params) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteRecord() {
        // Implementation of record deletion
    }

    private static void updateRecord() {
        // Implementation of record update
    }

    private static void executeQuery() {
        // Implementation of executing a query
    }
}

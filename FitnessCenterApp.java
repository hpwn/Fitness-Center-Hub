
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
        System.out.println("Enter member ID: ");
        int memberId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Enter first name: ");
        String fname = scanner.nextLine();
        System.out.println("Enter last name: ");
        String lname = scanner.nextLine();
        System.out.println("Enter phone number: ");
        String phonenum = scanner.nextLine();
        System.out.println("Enter membership level ID: ");
        int memlevelId = scanner.nextInt();
        System.out.println("Enter total spent: ");
        double totalSpent = scanner.nextDouble();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn
                        .prepareStatement("INSERT INTO colegperry.member VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, memberId);
            stmt.setString(2, fname);
            stmt.setString(3, lname);
            stmt.setString(4, phonenum);
            stmt.setInt(5, memlevelId);
            stmt.setDouble(6, totalSpent);

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertMemLevelRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Level ID: ");
        int levelId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.println("Enter Level Name: ");
        String levelName = scanner.nextLine();

        System.out.println("Enter Discount: ");
        int discount = scanner.nextInt();

        System.out.println("Enter Minimum Spent: ");
        double minSpent = scanner.nextDouble();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO colegperry.memlevel VALUES (?, ?, ?, ?)")) {

            stmt.setInt(1, levelId);
            stmt.setString(2, levelName);
            stmt.setInt(3, discount);
            stmt.setDouble(4, minSpent);

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertCourseRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Course ID: ");
        int courseId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.println("Enter Course Name: ");
        String courseName = scanner.nextLine();

        System.out.println("Enter Start Time (as an integer): ");
        int time = scanner.nextInt();

        System.out.println("Enter Start Date (format yyyy-mm-dd): ");
        String startDate = scanner.next();

        System.out.println("Enter End Date (format yyyy-mm-dd): ");
        String endDate = scanner.next();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO colegperry.course VALUES (?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'))")) {

            stmt.setInt(1, courseId);
            stmt.setString(2, courseName);
            stmt.setInt(3, time);
            stmt.setString(4, startDate);
            stmt.setString(5, endDate);

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertTrainerRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Trainer ID: ");
        int trainerId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.println("Enter Trainer Name: ");
        String trainerName = scanner.nextLine();

        System.out.println("Enter Phone Number: ");
        int phoneNumber = scanner.nextInt();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO colegperry.trainer VALUES (?, ?, ?)")) {

            stmt.setInt(1, trainerId);
            stmt.setString(2, trainerName);
            stmt.setInt(3, phoneNumber);

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertTrainClassRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Trainer ID: ");
        int trainerId = scanner.nextInt();

        System.out.println("Enter Class ID: ");
        int classId = scanner.nextInt();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO colegperry.trainClass VALUES (?, ?)")) {

            stmt.setInt(1, trainerId);
            stmt.setInt(2, classId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertItemRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Item ID: ");
        int itemId = scanner.nextInt();

        System.out.println("Enter Member ID: ");
        int memberId = scanner.nextInt();

        System.out.println("Enter Check-in Time (as an integer): ");
        int checkin = scanner.nextInt();

        System.out.println("Enter Check-out Time (as an integer): ");
        int checkout = scanner.nextInt();

        System.out.println("Enter Quantity: ");
        int qty = scanner.nextInt();

        System.out.println("Enter Lost Status (0 for not lost, 1 for lost): ");
        int lost = scanner.nextInt();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn
                        .prepareStatement("INSERT INTO colegperry.item VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setInt(1, itemId);
            stmt.setInt(2, memberId);
            stmt.setInt(3, checkin);
            stmt.setInt(4, checkout);
            stmt.setInt(5, qty);
            stmt.setInt(6, lost);

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertTransactionRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Transaction ID: ");
        int transId = scanner.nextInt();

        System.out.println("Enter Amount: ");
        int amount = scanner.nextInt();

        System.out.println("Enter Transaction Date (format yyyy-mm-dd): ");
        String transDate = scanner.next();

        System.out.println("Enter Transaction Type: ");
        scanner.nextLine(); // consume newline
        String transType = scanner.nextLine();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO colegperry.transaction VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)")) {

            stmt.setInt(1, transId);
            stmt.setInt(2, amount);
            stmt.setString(3, transDate);
            stmt.setString(4, transType);

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertCoursePackageRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Package Number: ");
        int packageNum = scanner.nextInt();

        System.out.println("Enter Package Name: ");
        scanner.nextLine(); // consume newline
        String packageName = scanner.nextLine();

        System.out.println("Enter First Class ID: ");
        int firstClassId = scanner.nextInt();

        System.out.println("Enter Second Class ID: ");
        int secondClassId = scanner.nextInt();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn
                        .prepareStatement("INSERT INTO colegperry.coursePackage VALUES (?, ?, ?, ?)")) {

            stmt.setInt(1, packageNum);
            stmt.setString(2, packageName);
            stmt.setInt(3, firstClassId);
            stmt.setInt(4, secondClassId);

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

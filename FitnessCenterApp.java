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
        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("Enter table name to insert record:");
            String tableName = scanner.next();

            // Example for inserting into member table
            if ("member".equalsIgnoreCase(tableName)) {
                System.out.println("Enter member ID:");
                int memberId = scanner.nextInt();
                System.out.println("Enter first name:");
                String firstName = scanner.next();
                System.out.println("Enter last name:");
                String lastName = scanner.next();
                System.out.println("Enter phone number:");
                String phone = scanner.next();

                String sql = "INSERT INTO member (memberID, fname, lname, phonenum) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, memberId);
                    statement.setString(2, firstName);
                    statement.setString(3, lastName);
                    statement.setString(4, phone);
                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("A new member was inserted successfully!");
                    }
                }
            } else {
                // Handle other table insertions
            }
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

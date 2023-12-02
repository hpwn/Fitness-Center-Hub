import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;

/*
    Class: Insert
    Authors: Hayden Price, Cole Perry, Audrey Gagum
    External Packages: java.util.Scanner, java.sql.*, java.math.BigDecimal
    Inheritance: None

    Purpose:
    The Insert class is responsible for handling the insertion of various records into different tables 
    within a fitness center's database. It offers a menu-driven approach for users to select the type 
    of record they wish to insert, such as members, courses, trainers, etc., and then captures the necessary 
    details for each record type.

    Public Class Constants and Variables:
    - scanner: Scanner object for reading user input.
    - transactionID: static integer for tracking transaction IDs.

    Constructors:
    - None

    Implemented Methods:
    - insertRecord(): Displays a menu for selecting the type of record to insert and handles the user's choice.
    - insertMemberRecord(): Captures and inserts member details into the member table.
    - insertMemLevelRecord(): Captures and inserts membership level details into the memlevel table.
    - insertCourseRecord(): Captures and inserts course details into the course table.
    - insertTrainerRecord(): Captures and inserts trainer details into the trainer table.
    - insertTrainClassRecord(): Captures and inserts training class details into the trainClass table.
    - insertItemRecord(): Captures and inserts item details into the item table.
    - insertTransactionRecord(): Overloaded methods that capture and insert transaction details into the transaction table.
    - insertCoursePackageRecord(): Captures and inserts course package details into the coursePackage table.
    - executeInsert(String sql): Executes the provided SQL insert statement.
*/
public class Insert {

    private static Scanner scanner = new Scanner(System.in);
    static int transactionID = 1000;

    /*
     * Method: insertRecord
     * Purpose: Displays a menu for the user to select the type of record they wish
     * to insert into the database.
     * The method then calls the appropriate method based on the user's choice.
     * Pre-conditions: None
     * Post-conditions: The selected record is inserted into the database, or an
     * invalid choice message is displayed.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - None within this method, but methods called from it may handle exceptions,
     * particularly SQL exceptions.
     */
    public static void insertRecord() {
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

    /*
     * Method: insertMemberRecord
     * Purpose: Captures member details from the user and inserts them into the
     * member table of the database.
     * It also includes logic for selecting a membership package and updating member
     * levels based on transactions.
     * Pre-conditions: None
     * Post-conditions: A new member record is inserted into the database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during database
     * operations.
     */
    public static void insertMemberRecord() {
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
                statementString += packageCost + ",0)";
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

    /*
     * Method: insertMemLevelRecord
     * Purpose: Captures membership level details from the user and inserts them
     * into the memlevel table
     * of the database. This includes information such as level ID, name, discount,
     * and minimum spending.
     * Pre-conditions: None
     * Post-conditions: A new membership level record is inserted into the database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operation.
     */
    public static void insertMemLevelRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Level ID, Level Name, Discount, Minimum Spent (comma-separated and no spaces): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + ",'" + params[1] + "'," + params[2] + "," + params[3] + ")";

        // Constructing the statement string

        executeInsert("INSERT INTO colegperry.memlevel VALUES " + statementString);
    }

    /*
     * Method: insertCourseRecord
     * Purpose: Captures course details from the user, such as course ID, name,
     * start and end dates,
     * and inserts this information into the course table of the database.
     * Pre-conditions: Course dates should be in the format yyyy-mm-dd.
     * Post-conditions: A new course record is inserted into the database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operation.
     */
    public static void insertCourseRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter Course ID, Course Name, Start Time, Start Date (yyyy-mm-dd), End Date (yyyy-mm-dd) (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + ",'" + params[1] + "'," + params[2] + ",TO_DATE('" + params[3] +
                "','YYYY-MM-DD'),TO_DATE('" + params[4] + "','YYYY-MM-DD'),0,50)";

        executeInsert(
                "INSERT INTO colegperry.course VALUES " + statementString);
    }

    /*
     * Method: insertTrainerRecord
     * Purpose: Captures trainer details from the user, including trainer ID, name,
     * and phone number,
     * and inserts them into the trainer table of the database.
     * Pre-conditions: None
     * Post-conditions: A new trainer record is inserted into the database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operation.
     */
    public static void insertTrainerRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Trainer ID, Trainer Name, Phone Number (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + ",'" + params[1] + "','" + params[2] + "')";

        executeInsert("INSERT INTO colegperry.trainer VALUES " + statementString);
    }

    /*
     * Method: insertTrainClassRecord
     * Purpose: Captures training class details, such as trainer ID and class ID,
     * from the user and
     * inserts them into the trainClass table of the database.
     * Pre-conditions: None
     * Post-conditions: A new training class record is inserted into the database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operation.
     */
    public static void insertTrainClassRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Trainer ID, Class ID (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + "," + params[1] + ")";

        executeInsert("INSERT INTO colegperry.trainClass VALUES " + statementString);
    }

    /*
     * Method: insertItemRecord
     * Purpose: Captures item details including item ID, member ID, check-in and
     * check-out times,
     * quantity, and lost status from the user and inserts them into the item table
     * of the database.
     * Pre-conditions: Lost status should be represented as 0 or 1.
     * Post-conditions: A new item record is inserted into the database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operation.
     */
    public static void insertItemRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter Item ID, Member ID, Check-in Time, Check-out Time, Quantity, Lost Status (0 or 1) (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + "," + params[1] + "," + params[2] + "," + params[3] + "," + params[4]
                + "," + params[5] + ")";

        executeInsert("INSERT INTO colegperry.item VALUES " + statementString);
    }

    /*
     * Method: insertTransactionRecord (Overloaded)
     * Purpose: Captures transaction details from the user, including transaction
     * ID, amount, date,
     * and type, and inserts them into the transaction table of the database.
     * Pre-conditions: Transaction date should be in the format yyyy-mm-dd.
     * Post-conditions: A new transaction record is inserted into the database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operation.
     */
    public static void insertTransactionRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter Transaction ID, Amount, Transaction Date (yyyy-mm-dd), Transaction Type (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + "," + params[1] + ", TO_DATE('" + params[2] + "','YYYY-MM-DD'),'"
                + params[3] + "')";

        executeInsert("INSERT INTO colegperry.transaction VALUES " + statementString);
    }

    /*
     * Method: insertCoursePackageRecord
     * Purpose: Captures course package details from the user, such as package
     * number, name, cost,
     * and class IDs, and inserts this information into the coursePackage table of
     * the database.
     * Pre-conditions: Cost should be to the second decimal.
     * Post-conditions: A new course package record is inserted into the database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operation.
     */
    public static void insertTransactionRecord(String sql) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Method: insertCoursePackageRecord
     * Purpose: Captures course package details from the user, such as package
     * number, name, cost,
     * and class IDs, and inserts this information into the coursePackage table of
     * the database.
     * Pre-conditions: Cost should be to the second decimal.
     * Post-conditions: A new course package record is inserted into the database.
     * Return value: None
     * Parameters: None
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the database
     * operation.
     */
    public static void insertCoursePackageRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                "Enter Package Number, Package Name, Cost (To second decimal), First Class ID, Second Class ID (comma-separated): ");
        String input = scanner.nextLine();
        String[] params = input.split(",");
        String statementString = "(" + params[0] + ",'" + params[1] + "'," + params[2] + "," + params[3] + ","
                + params[4] + ")";

        executeInsert("INSERT INTO colegperry.coursePackage VALUES " + statementString);
    }

    /*
     * Method: executeInsert
     * Purpose: Executes the given SQL insert statement to add a record to the
     * database.
     * Pre-conditions: A valid SQL insert statement must be provided.
     * Post-conditions: The record is inserted into the database, and the number of
     * affected rows is printed.
     * Return value: None
     * Parameters:
     * - sql: String (in) - The SQL insert statement to be executed.
     * 
     * Exception Handling:
     * - Catches SQLException to handle any SQL-related errors during the execution
     * of the insert statement.
     */

    public static void executeInsert(String sql) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

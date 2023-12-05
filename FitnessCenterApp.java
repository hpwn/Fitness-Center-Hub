/*
    Authors: Hayden Price, Cole Perry, Audrey Gagum
    Course: CSc 460 Database Design Fall 2023
    Assignment: Program #4
    Instructor: Lester I. McCann, Ph.D.
    TAs: Zhenyu Qi, Danial Bazmandeh
    Due Date: 12/5/23

    This program is designed to manage records for a fitness center. It includes features to insert, delete,
    update, and query records in the system. Techniques such as object-oriented programming and exception
    handling are employed to manage the data effectively. 

    Operational Requirements:
    - Programming Language: Java
    - Special Compilation Information: None
    - Input Location: User input via console
    - Known Bugs: 👁️👄👁️

    The program aims to demonstrate the use of Java for building a simple record management system,
    focusing on basic CRUD (Create, Read, Update, Delete) operations.
*/

import java.util.Scanner;

/*
    Class: FitnessCenterApp
    Author: Hayden Price, Cole Perry, Audrey Gagum
    External Packages: java.util.Scanner
    Inheritance: None

    Purpose:
    This class serves as the main entry point for the Fitness Center record management application. 
    It provides a menu-driven interface for the user to interact with the system, allowing them 
    to choose operations like inserting, deleting, updating, and querying records.

    Public Class Constants and Variables:
    - scanner: Scanner object for reading user input

    Constructors:
    - None

    Implemented Methods:
    - main(String[] args): The main method which drives the application, presenting a menu to the user 
      and handling their choices for different operations.
*/
public class FitnessCenterApp {

    private static Scanner scanner = new Scanner(System.in);

    /*
     * Method: main
     * Purpose: Serves as the entry point of the FitnessCenterApp. It displays a
     * menu to the user, allowing them to choose various operations such as
     * inserting, deleting, updating, and querying records. The method handles
     * user input and directs the flow of the application accordingly.
     * Pre-conditions: None
     * Post-conditions: The application is terminated after completing the chosen
     * operation or when the user chooses to exit.
     * Return value: None
     * Parameters:
     * - args: String array (in from the JVM) - Command line arguments passed to the
     * application, not used in this method.
     * 
     * Exception Handling:
     * - Catches and prints any exceptions that may occur during the execution of
     * the program, particularly during user input handling.
     */

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
                        Insert.insertRecord();
                        break;
                    case 2:
                        Delete.deleteRecord();
                        break;
                    case 3:
                        Update.updateRecord();
                        break;
                    case 4:
                        Query.executeQueries();
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

}

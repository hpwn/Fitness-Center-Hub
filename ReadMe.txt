# Fitness Center Application

## Compilation and Execution Instructions

This document provides instructions to compile and execute the Fitness Center Application, developed in Java. It requires Java Runtime Environment (JRE) and Java Development Kit (JDK).

### Prerequisites

- Java Development Kit (JDK), version 8 or above
- Access to an Oracle SQL database with the necessary schema

### Database Configuration

1. Ensure the Oracle SQL database is running and accessible.
2. Update the `DBConnection` class with the database URL, username, and password:
   ```java
   private static final String URL = "your_database_url";
   private static final String USER = "your_username";
   private static final String PASSWORD = "your_password";
   ```

### Compilation

Navigate to the source directory where the Java files are located and compile using `javac`:

```bash
javac FitnessCenterApp.java Insert.java Delete.java Update.java Query.java DBConnection.java
```

### Execution

Run the application using `java`:

```bash
java FitnessCenterApp
```

Follow the on-screen instructions to interact with the application.

### Notes

- All Java files should be in the same directory or properly referenced if located in different packages.
- The application is designed for an Oracle SQL database; ensure it is set up and accessible.

## Authors and Work Distribution

### Hayden Price:

(a), (c)

### Cole Perry:

(a), (b), testing

### Audrey Gagum:

(b), testing

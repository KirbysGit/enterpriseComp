# Project 3 – A Two-Tier Client-Server Application
*CNT 4714 – Spring 2025*

## Project Overview
This project implements a two-tier client-server SQL application that provides a graphical user interface for interacting with a MySQL database. The application supports multiple user roles with different access levels and includes a comprehensive logging system for tracking database operations.

### Key Features
- Java-based GUI using Swing (JFrame, JTable)
- Secure MySQL database integration using PreparedStatement
- Role-based access control via properties files
- Real-time query execution and result display
- Operation logging system
- Support for parameterized queries
- Modern, user-friendly interface

## Project Structure
```
Project3/
│── Accountant-OperationsLogScreenshots/    # Screenshots for Accountant logs
│── Client1CommandScreenshots/              # Screenshots for Client 1 queries
│── Client2CommandScreenshots/              # Screenshots for Client 2 queries
│── RootCommandsScreenshots/                # Screenshots for Root user commands
│── sqlCommands/                           # SQL setup scripts
│   ├── project3client1script.sql          # SQL commands for Client 1
│   ├── project3client2script.sql          # SQL commands for Client 2
│   ├── project3rootscript.sql             # SQL commands for Root user
│── src/                                   # Source code and dependencies
│   ├── properties/                        # Database connection config files
│   ├── AccountantGUI.java                 # Accountant-specific GUI
│   ├── DatabaseGUI.java                   # Main database client application
│   ├── mysql-connector-j-9.2.0.jar        # MySQL JDBC Driver
```

## Key Components

### Source Files
- **DatabaseGUI.java**: Main application GUI for database interaction
- **AccountantGUI.java**: Specialized interface for accountant role (read-only access)
- **mysql-connector-j-9.2.0.jar**: JDBC driver for MySQL connectivity

### Configuration
- **properties/**: Contains connection configuration files for different user roles
  - Database URLs
  - User credentials
  - Access permissions

### SQL Scripts
- **project3client1script.sql**: Client 1 role queries and permissions
- **project3client2script.sql**: Client 2 role queries and permissions
- **project3rootscript.sql**: Root/Admin user setup and permissions

## User Roles and Permissions

| Role      | Permissions                               |
|-----------|------------------------------------------|
| Root      | Full access (SELECT, INSERT, UPDATE, DELETE) |
| Client 1  | Limited query execution                   |
| Client 2  | Limited query execution                   |
| Accountant| Read-only access to operation logs        |

## Setup and Installation

### Prerequisites
1. Java Development Kit (JDK) 8 or higher
2. MySQL Server 8.0 or higher
3. MySQL Connector/J 9.2.0

### Installation Steps
1. Clone or download the project repository
2. Set up MySQL database using provided SQL scripts
3. Configure properties files with appropriate database credentials
4. Add mysql-connector-j-9.2.0.jar to the classpath

### Running the Application
1. Compile the Java files:
```bash
javac -cp ".;mysql-connector-j-9.2.0.jar" src/DatabaseGUI.java
javac -cp ".;mysql-connector-j-9.2.0.jar" src/AccountantGUI.java
```

2. Run the application:
```bash
# For main application
java -cp ".;mysql-connector-j-9.2.0.jar" src.DatabaseGUI

# For accountant interface
java -cp ".;mysql-connector-j-9.2.0.jar" src.AccountantGUI
```

## Usage Guide

1. **Login**:
   - Select appropriate properties file
   - Enter username and password
   - Click "Connect to Database"

2. **Execute Queries**:
   - Enter SQL query in the command area
   - For parameterized queries, use ? placeholders
   - Click "Execute SQL Command" to run

3. **View Results**:
   - Results display in the table below
   - Use "Clear Results" to reset the view
   - Operation status shown in status bar

## Error Handling

- Comprehensive SQL error messages
- Connection status monitoring
- Invalid credential detection
- Query syntax validation
- Permission violation alerts

## Screenshots
Screenshots of various operations are available in their respective directories:
- Root user operations: `RootCommandsScreenshots/`
- Client 1 operations: `Client1CommandScreenshots/`
- Client 2 operations: `Client2CommandScreenshots/`
- Accountant operations: `Accountant-OperationsLogScreenshots/`

package dataaccess;

import exceptions.DataAccessException;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }








    /*
    * this is the log mysql gave me after it was set up
    *
    * Executing step: Writing configuration file

Saving my.ini configuration file...
Saved my.ini configuration file.
Completed execution of step: Writing configuration file

Executing step: Updating Windows Firewall rules

Adding a Windows Firewall rule for MySQL97 on port 3306.
Attempting to add a Windows Firewall rule with command: netsh.exe advfirewall firewall add rule name="Port 3306"
* protocol=TCP localport=3306 dir=in action=allow
Ok.


Successfully added the Windows Firewall rule.
Adding a Windows Firewall rule for MySQL97 on port 33060.
Attempting to add a Windows Firewall rule with command: netsh.exe advfirewall firewall add rule name="Port 33060"
* protocol=TCP localport=33060 dir=in action=allow
Ok.


Successfully added the Windows Firewall rule.
Completed execution of step: Updating Windows Firewall rules

Executing step: Adjusting Windows service

Attempting to grant the required filesystem permissions to the 'NT AUTHORITY\NetworkService' account.
Granted permissions to the data directory.
Granted permissions to the install directory.
Adding new service
New service added
Completed execution of step: Adjusting Windows service

Executing step: Initializing database (may take a long time)

Attempting to run MySQL Server with --initialize-insecure option...
Starting process for MySQL Server 9.7.1...
Starting process with command: C:\Program Files\MySQL\MySQL Server 9.7\bin\mysqld.exe --defaults-file="C:\ProgramData\
* MySQL\MySQL Server 9.7\my.ini" --console --initialize-insecure=on --lower-case-table-names=1...
MySQL Server Initialization - start.
C:\Program Files\MySQL\MySQL Server 9.7\bin\mysqld.exe (mysqld 9.7.1) initializing of server in progress as process 16972
InnoDB initialization has started.
InnoDB initialization has ended.
root@localhost is created with an empty password ! Please consider switching off the --initialize-insecure option.
MySQL Server Initialization - end.
Process for mysqld, with ID 16972, was run successfully and exited with code 0.
Successfully started process for MySQL Server 9.7.1.
MySQL Server 9.7.1 intialized the database successfully.
Completed execution of step: Initializing database (may take a long time)

Executing step: Updating permissions for the data folder and related server files

Attempting to update the permissions for the data folder and related server files...
Inherited permissions have been converted to explicit permissions.
Full control permissions granted to: Administrators.
Full control permissions granted to: CREATOR OWNER.
Full control permissions granted to: SYSTEM.
Full control permissions granted to: NETWORK SERVICE.
Access to the data directory is removed for the users group.
Permissions for the data folder and related server files are updated correctly.
Completed execution of step: Updating permissions for the data folder and related server files

Executing step: Starting the server

Attempting to start service MySQL97...
MySQL Server - start.
C:\Program Files\MySQL\MySQL Server 9.7\bin\mysqld.exe (mysqld 9.7.1) starting as process 26984
MySQL Server has access to 8 logical CPUs.
MySQL Server has access to 8353841152 bytes of physical memory.
InnoDB initialization has started.
InnoDB initialization has ended.
CA certificate ca.pem is self signed.
Channel mysql_main configured to support TLS. Encrypted connections are now supported for this channel.
X Plugin ready for connections. Bind-address: '::' port: 33060
C:\Program Files\MySQL\MySQL Server 9.7\bin\mysqld.exe: ready for connections. Version: '9.7.1'  socket: ''  port: 3306
*  MySQL Community Server - GPL.
Successfully started service MySQL97.
Waiting until a connection to MySQL Server 9.7.1 can be established (with a maximum of 10 attempts)...
Retry 1: Attempting to connect to Mysql@localhost:3306 with user root with no password...
Successfully connected to MySQL Server 9.7.1.
Completed execution of step: Starting the server

Executing step: Applying security settings

Attempting to update security settings.
Updated security settings.
Completed execution of step: Applying security settings

Executing step: Creating user accounts

Attempting to Add New MySQL Users
Created user 'emeline:localhost'.
Added new users to the MySQL database successfully.
Completed execution of step: Creating user accounts

Executing step: Updating the Start menu link

Attempting to verify command-line client shortcut.
Verified command-line client shortcut.
Verified command-line client shortcut.
Completed execution of step: Updating the Start menu link


    *
    *
    * */
}

package com.everything.game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * class MySqlManager
 * connect & keep session mysql
 */
public class MySQLManager {
    public static Connection conn;
    public static Statement stat;

    public static synchronized void create(String dbUrl, String user, String pass) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL connect: " + dbUrl);
            try {
                conn = DriverManager.getConnection(dbUrl, user, pass);
                stat = conn.createStatement();
                System.out.println("MySQL ready!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("driver mysql not found!");
        }
    }

    public static synchronized boolean close() {
        System.out.println("Close connection to database");
        try {
            if (stat != null)
                stat.close();
            if (conn != null)
                conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

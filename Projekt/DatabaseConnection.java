package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Ustawienia Twojej bazy (Docker)
    private static final String URL = "jdbc:db2://localhost:50000/projekt:sslConnection=false;securityMechanism=3;authentication=SERVER;";
    private static final String USER = "db2inst1";
    private static final String PASSWORD = "";

    public static Connection connect() {
        Connection conn = null;
        try {
            // Ładowanie sterownika
            Class.forName("com.ibm.db2.jcc.DB2Driver");

            // Próba połączenia
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ SUKCES: Połączono z bazą danych!");

        } catch (ClassNotFoundException e) {
            System.out.println("❌ BŁĄD: Nie znaleziono sterownika Db2 (sprawdź pom.xml).");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ BŁĄD SQL: Nie można połączyć z bazą.");
            e.printStackTrace();
        }
        return conn;
    }

    // Metoda main tylko do testu
    public static void main(String[] args) {
        connect();
    }
}
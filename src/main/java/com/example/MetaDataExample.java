package com.example;

import java.nio.channels.SelectableChannel;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import com.github.freva.asciitable.AsciiTable;

public class MetaDataExample {
    public static void main(String[] args) {
        MetaDataExample example = new MetaDataExample();

        try {
            example.listDataInTable("tb_book");
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void printMetaData() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            System.out.println("Database Name: " + metaData.getDatabaseProductName());
            System.out.println("Database version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver Name: " + metaData.getDriverName());
            System.out.println("Driver Version: " + metaData.getDriverVersion());
            System.out.println("URL: " + metaData.getURL());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void listAllDatabases() {
        String sql = "SELECT datname FROM pg_database WHERE datistemplate = false;";

        try (Connection connection = DatabaseConfig.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\nAll Databases");
            while (rs.next()) {
                System.out.println(rs.getString("datname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void listAllTalbes(String databaseName) {
        String url = String.format("jdbc:postgresql://localhost:5432/%s", databaseName);

        try (Connection connection = DriverManager.getConnection(url, "postgres", "1234")) {
            DatabaseMetaData metaData = connection.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = metaData.getTables(null, "public", "%", types);

            System.out.println("\nAll Tables in database " + databaseName);

            while (rs.next()) {
                System.out.println(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void listAllTableInCatalog() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = metaData.getTables(null, null, "%", types);

            while (rs.next()) {
                System.out.println(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error: " + e.getMessage());
        }
    }

    public void listAllColumnsInTable(String tableName) {
        try (Connection connection = DatabaseConfig.getConnection()) {
             DatabaseMetaData metaData = connection.getMetaData();
             ResultSet rs = metaData.getColumns(null, null, tableName, "%");

             System.out.println("\nAll Columns in table " + tableName);

             while (rs.next()) {
                System.out.printf(
                    "%-20s %-20s %-20s %-20s %-20s\n",
                    rs.getString("COLUMN_NAME"),
                    rs.getString("TYPE_NAME"),
                    rs.getInt("COLUMN_SIZE"),
                    rs.getInt("DECIMAL_DIGITS"),
                    rs.getInt("NULLABLE") == 1 ? "YES" : "NO"
                );
             }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error: " + e.getMessage());
        }
    }

    public void listAllColumnsInTableTextMode(String tableName) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, "%");

            List<String[]> rows = new ArrayList<>();

            String[] headers = {"Columm Name", "Type", "Size", "Decimal Digits", "Nullable"};

            while (rs.next()) {
                String[] row = new String[5];
                row[0] = rs.getString("COLUMN_NAME");
                row[1] = rs.getString("TYPE_NAME");
                row[2] = String.valueOf(rs.getInt("COLUMN_SIZE"));
                row[3] = String.valueOf(rs.getInt("DECIMAL_DIGITS"));
                row[4] = rs.getInt("NULLABLE") == 1 ? "YES" : "NO";

                rows.add(row);
            }

            AsciiTable at = new AsciiTable();
            System.out.println("\nAll Columns in table " + tableName);
            System.out.println(at.getTable(headers, rows.toArray(new String[0][])));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error: " + e.getMessage());
        }
    }

    public void listDataInTable(String tableName) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);

            List<String[]> rows = new ArrayList<>();
            String[] headers = {"id", "name", "price"};

            while (rs.next()) {
                String[] row = new String[3];
                row[0] = String.valueOf(rs.getInt("id"));
                row[1] = rs.getString("name");
                row[2] = String.valueOf(rs.getInt("price"));

                rows.add(row);
            }

            AsciiTable at = new AsciiTable();
            System.out.println("\nAll Data in table " + tableName);
            System.out.println(at.getTable(headers, rows.toArray(new String[0][])));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error: " + e.getMessage());
        }
    }
}


















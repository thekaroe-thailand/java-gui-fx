package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.CallableStatement;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.exportDataToCsv();
    }

    public void insertData() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = "INSERT INTO tb_book(name, price) VALUES(?, ?)";
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, "Book 1");
            p.setInt(2, 100);
            p.executeUpdate(); // INSERT, UPDATE, DELETE

            System.out.println("insert success");

            conn.close();
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void updateData() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = "UPDATE tb_book SET name = ?, price = ? WHERE id = ?";
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, "Book 2");
            p.setInt(2, 200);
            p.setInt(3, 15); // id = 15
            p.executeUpdate();

            System.out.println("update success");

            conn.close();
        } catch (SQLException e) {
            System.err.print("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void deleteData() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = "DELETE FROM tb_book WHERE id = ?";
            PreparedStatement p = conn.prepareStatement(sql);
            p.setInt(1, 15);
            p.executeUpdate();

            System.out.println("delete success");

            conn.close();
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void selectData() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = "SELECT * FROM tb_book";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int price = rs.getInt("price");

                System.out.printf("%d %s %d", id, name, price);
                System.out.println();
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("error: " + e.getMessage());
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void selectBetweenPrice() {
        try { 
            Connection conn = DatabaseConfig.getConnection();
            String sql = "SELECT * FROM tb_book WHERE price BETWEEN ? AND ?";
            PreparedStatement p = conn.prepareStatement(sql);
            p.setInt(1, 100);
            p.setInt(2, 200);

            ResultSet rs = p.executeQuery();

            System.out.println("id\tname\tprice");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int price = rs.getInt("price");

                System.out.printf("%d\t%s\t%d", id, name, price);
                System.out.println();
            }

            conn.close();
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void selectLikeName() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = "SELECT * FROM tb_book WHERE name LIKE ?";
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, "%java%");

            ResultSet rs = p.executeQuery();

            System.out.println("id\tname\tprice");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int price = rs.getInt("price");

                System.out.printf("%d\t%s\t%d", id, name, price);
                System.out.println();
            }

            conn.close();
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void sumPrice() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = "SELECT SUM(price) FROM tb_book";
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet rs = p.executeQuery();

            if (rs.next()) {
                int sum = rs.getInt(1);
                System.out.println("sum: " + sum);
            }

            conn.close();
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void maxMinAndAvgAndCountPrice() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = """
                    SELECT
                        MAX(price) AS maxPrice,
                        MIN(price) AS minPrice,
                        AVG(price) AS avgPrice,
                        COUNT(*) AS totalBook
                    FROM tb_book
                    """;
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet rs = p.executeQuery();

            if (rs.next()) {
                System.out.println("max: " + rs.getInt("maxPrice"));
                System.out.println("min: " + rs.getInt("minPrice"));
                System.out.println("avg:" + rs.getDouble("avgPrice"));
                System.out.println("total: " + rs.getInt("totalBook"));
            }

            conn.close();
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void batchQuery() {
        try { 
            Connection conn = DatabaseConfig.getConnection();
            String sql = "INSERT INTO tb_book(name, price) VALUES(?, ?)";
            PreparedStatement p = conn.prepareStatement(sql);

            for (int i = 0; i < 10; i++) {
                p.setString(1, "book " + i);
                p.setInt(2, i * 200);
                p.addBatch();
            }

            int[] result = p.executeBatch();

            if (result.length > 0) {
                System.out.println("insert success");
            }

            conn.close();
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void selectDataFromView() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = "SELECT * FROM view_book";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                String categoriesName = rs.getString("categories_name");
                String categoriesRemark = rs.getString("remark");

                System.out.printf("%d %s %d %s %s", id, name, price, categoriesName, categoriesRemark);
                System.out.println();
            }

            conn.close();
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void callProcedure() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = "CALL store_book(?, ?, ?)";
            CallableStatement cstmt = conn.prepareCall(sql);
            cstmt.setString(1, "Book data");
            cstmt.setInt(2, 100);
            cstmt.setInt(3, 1);
            cstmt.executeUpdate();

            System.out.println("insert success");

            conn.close();
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void insertDataFromCsv() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = "INSERT INTO tb_book(name, price, categories_id) VALUES(?, ?, ?)";
            PreparedStatement p = conn.prepareStatement(sql);

            String filePath = "src/main/java/com/example/book.csv";
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                int price = Integer.parseInt(data[1].trim());
                int categoriesId = Integer.parseInt(data[2].trim());

                p.setString(1, name);
                p.setInt(2, price);
                p.setInt(3, categoriesId);
                p.executeUpdate();
            }

            System.out.println("insert success");

            conn.close();
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }

    public void exportDataToCsv() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String sql = "SELECT * FROM tb_book";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            String filePath = "src/main/java/com/example/book_export.csv";
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);

            while (rs.next()) {
                String name = rs.getString("name");
                int price = rs.getInt("price");
                int categoriesId = rs.getInt("categories_id");

                bw.write(name + "," + price + "," + categoriesId);
                bw.newLine();
            }

            bw.close();
            fw.close();

            System.out.println("export success");

            conn.close();
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
    }
}


/*
try {
            
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
        }
 */















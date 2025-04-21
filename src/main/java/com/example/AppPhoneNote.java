package com.example;

import javafx.application.*;
import javafx.scene.*;      
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.sql.*;
import javafx.collections.*;
import javafx.geometry.Pos;
import javafx.scene.control.cell.*;


public class AppPhoneNote extends Application {
    // เพิ่ม connection string สำหรับ SQLite
    private static final String DB_URL = "jdbc:sqlite:phone_notes.db";
    private TextField txtName;
    private TextField txtPhone;

    public static void main(String[] args) {
        // สร้างฐานข้อมูลและตารางเมื่อเริ่มโปรแกรม
        initDatabase();
        launch(args);
    }

    private static void initDatabase() {
        // สร้างตารางถ้ายังไม่มี
        String sql = """
            CREATE TABLE IF NOT EXISTS contacts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                phone TEXT NOT NULL
            )
            """;
            
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new StackPane());        
        
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setStyle("-fx-padding: 10;");

        Label lblName = new Label("ชื่อเพื่อน");
        vbox.getChildren().add(lblName);

        txtName = new TextField();
        vbox.getChildren().add(txtName);

        Label lblPhone = new Label("เบอร์โทร");
        vbox.getChildren().add(lblPhone);

        txtPhone = new TextField();
        vbox.getChildren().add(txtPhone);

        TableView<Contact> tableView = new TableView<>();

        // สร้างปุ่มบันทึก
        Button btnSave = new Button("บันทึก");
        btnSave.setOnAction(e -> {
            saveContact(txtName.getText(), txtPhone.getText());
            txtName.clear();
            txtPhone.clear();

            loadContacts(tableView);
        });

        // สร้างปุ่มลบ
        Button btnDelete = new Button("ลบ");
        btnDelete.setOnAction(e -> {
            deleteContact(tableView);
            loadContacts(tableView);
        });

        // สร้างปุ่มแก้ไข
        Button btnEdit = new Button("แก้ไข");
        btnEdit.setOnAction(e -> {
            editContact(tableView);
            loadContacts(tableView);
        });

        // เรียงปุ่มให้อยู่แถวเดียวกัน
        HBox hbox = new HBox();
        hbox.getChildren().addAll(btnSave, btnDelete, btnEdit);
        hbox.setSpacing(5);
        vbox.getChildren().add(hbox);

        // เพิ่ม TableView สำหรับแสดงข้อมูล
        TableColumn<Contact, String> nameCol = new TableColumn<>("ชื่อ");
        TableColumn<Contact, String> phoneCol = new TableColumn<>("เบอร์โทร");
        
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        tableView.getColumns().clear();
        tableView.getColumns().addAll(nameCol, phoneCol);
        vbox.getChildren().add(tableView);

        scene.setRoot(vbox);
        primaryStage.setTitle("Phone Note");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // โหลดข้อมูลเมื่อเปิดโปรแกรม
        loadContacts(tableView);

        // เมื่อคลิกที่รายการในตาราง ให้แสดงข้อมูลใน TextField
        tableView.getSelectionModel().selectedItemProperty().addListener((
            obs, oldSelection, newSelection
        ) -> {
            if (newSelection != null) {
                txtName.setText(newSelection.getName());
                txtPhone.setText(newSelection.getPhone());
            }
        });
    }

    private void editContact(TableView<Contact> tableView) {
        if (txtName.getText() != null && txtPhone.getText() != null) {
            String sql = "UPDATE contacts SET name = ?, phone = ? WHERE phone = ?";

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtName.getText());
                pstmt.setString(2, txtPhone.getText());
                pstmt.setString(3, txtPhone.getText());
                pstmt.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("แก้ไขข้อมูลเรียบร้อยแล้ว");
                alert.showAndWait();

                loadContacts(tableView);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteContact(TableView<Contact> tableView) {
        Contact selectedContact = tableView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            String sql = "DELETE FROM contacts WHERE phone = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, selectedContact.getPhone());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveContact(String name, String phone) {
        String sql = "INSERT INTO contacts (name, phone) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.executeUpdate();
            
            // แสดง Alert เมื่อบันทึกสำเร็จ
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("บันทึกข้อมูลเรียบร้อยแล้ว");
            alert.showAndWait();
            
        } catch (SQLException e) {
            e.printStackTrace();
            // แสดง Alert เมื่อเกิดข้อผิดพลาด
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("เกิดข้อผิดพลาดในการบันทึกข้อมูล");
            alert.showAndWait();
        }
    }

    private void loadContacts(TableView<Contact> tableView) {
        String sql = "SELECT name, phone FROM contacts";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ObservableList<Contact> contacts = FXCollections.observableArrayList();
            
            while (rs.next()) {
                contacts.add(new Contact(
                    rs.getString("name"),
                    rs.getString("phone")
                ));
            }
            
            tableView.setItems(contacts);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

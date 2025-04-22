package com.example;

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.sql.*;

import javax.naming.ldap.ControlFactory;

import javafx.collections.*;
import javafx.geometry.Pos;
import javafx.scene.control.cell.*;

public class AppPhoneNote extends Application {
    private static final String DB_URL = "jdbc:sqlite:phone_notes.db";
    private TextField txtName;
    private TextField txtPhone;

    public static void main(String[] args) {
        initDatabase();
        launch(args);
    }

    private static void initDatabase() {
        String sql = """
                CREATE TABLE IF NOT EXISTS contacts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    phone TEXT NOT NULL
                )
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
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

        Button btnSave = new Button("บันทึก");
        btnSave.setOnAction(e -> {
            saveContact(txtName.getText(), txtPhone.getText());
            txtName.clear();
            txtPhone.clear();

            loadContacts(tableView);
        });

        Button btnDelete = new Button("ลบ");
        btnDelete.setOnAction(e -> {
            deleteContact(tableView);
            loadContacts(tableView);
        });

        Button btnEdit = new Button("แก้ไข");
        btnEdit.setOnAction(e -> {
            editContact(tableView);
            loadContacts(tableView);
        });

        HBox hbox = new HBox();
        hbox.getChildren().addAll(btnSave, btnDelete, btnEdit);
        hbox.setSpacing(5);

        vbox.getChildren().add(hbox);

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

        loadContacts(tableView);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtName.setText(newSelection.getName());
                txtPhone.setText(newSelection.getPhone());
            }
        });
    }

    private void editContact(TableView<Contact> tableView) {
        if (txtName.getText() != null && txtPhone.getText() != null) {
            String sql = "UPDATE contacts SET name = ?, phone = ? WHERE phone = ?";
            
            try (
                Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement p = conn.prepareStatement(sql);
            ) {
                p.setString(1, txtName.getText());
                p.setString(2, txtPhone.getText());
                p.setString(3, txtPhone.getText());
                p.executeUpdate();

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

            try (
                Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement p = conn.prepareStatement(sql);
            ) {
                p.setString(1, selectedContact.getPhone());
                p.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveContact(String name, String phone) {
        String sql = "INSERT INTO contacts (name, phone) VALUES(?, ?)";

        try (
            Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement p = conn.prepareStatement(sql);
        ) {
            p.setString(1, name);
            p.setString(2, phone);
            p.executeUpdate();
        } catch (SQLException e) {  
            e.printStackTrace();
        }
    }

    private void loadContacts(TableView<Contact> tableView) {
        String sql = "SELECT name, phone FROM contacts";

        try (
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        ) {
            ObservableList<Contact> contacts = FXCollections.observableArrayList();

            while (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                Contact c = new Contact(name, phone);
                contacts.add(c);
            }

            tableView.setItems(contacts);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

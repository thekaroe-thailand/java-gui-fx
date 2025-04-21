package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuBar;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.io.File;

public class GuiExample extends Application {
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10;");

        TextField textField = new TextField();
        textField.setOnKeyReleased(e -> {
            if (textField.getText().equals("admin")) {
                Stage popupStage = new Stage();
                VBox popupVBox = new VBox();
                popupVBox.setStyle("-fx-padding: 10;");

                Label popupLabel = new Label("ยินดีต้อนรับ Admin");
                Button closeButton = new Button("Close");
                closeButton.setOnAction(event -> popupStage.close());

                popupVBox.getChildren().addAll(popupLabel, closeButton);

                Scene popuScene = new Scene(popupVBox, 200, 100);
                popupStage.setScene(popuScene);
                popupStage.setTitle("Welcome");
                popupStage.show();
            }
        });
        root.getChildren().add(textField);

        Label label = new Label("Hello JavaFX!");
        root.getChildren().add(label);

        Button button = new Button("Show Text");
        button.setOnAction(e -> {
            String text = textField.getText();
            label.setText(text);
        });
        root.getChildren().add(button);

        Button popupButton = new Button("Show Popup");
        popupButton.setOnAction(e -> {
            Stage popupStage = new Stage();
            VBox popupVBox = new VBox(10);
            popupVBox.setStyle("-fx-padding: 10;");

            Label popupLabel = new Label("This is a popup windows!");
            Button closeButton = new Button("Close");
            closeButton.setOnAction(event -> popupStage.close());

            popupVBox.getChildren().addAll(popupLabel, closeButton);

            Scene popupScene = new Scene(popupVBox, 200, 100);
            popupStage.setScene(popupScene);
            popupStage.setTitle("Popup Window");
            popupStage.show();
        });
        root.getChildren().add(popupButton);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Option 1", "Option 2", "Option 3");
        root.getChildren().add(comboBox);

        Label dropdownLabel = new Label("Selected Option: ");

        comboBox.setOnAction(e -> {
            String selectedOption = comboBox.getValue();
            dropdownLabel.setText("Selected Option: " + selectedOption);
        });
        root.getChildren().add(dropdownLabel);

        TableView<Person> tableView = new TableView<>();

        TableColumn<Person, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Person, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getAge()));

        TableColumn<Person, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));

        tableView.getColumns().addAll(nameColumn, ageColumn, addressColumn);

        ObservableList<Person> data = FXCollections.observableArrayList();

        for (int i = 0; i < 10; i++) {
            data.add(new Person("Person " + i, 20 + i, "Address " + i));
        }

        tableView.setItems(data);
        tableView.setPrefHeight(200);

        root.getChildren().add(tableView);

        // menu
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");

        fileMenu.getItems().addAll(openItem, saveItem);
        menuBar.getMenus().add(fileMenu);

        Menu settingMenu = new Menu("Settings");
        MenuItem generalItem = new MenuItem("General");
        MenuItem accountItem = new MenuItem("Account");
        MenuItem securityItem = new MenuItem("Security");
        MenuItem languageItem = new MenuItem("Language");
        settingMenu.getItems().addAll(generalItem, accountItem, securityItem, languageItem);

        fileMenu.getItems().add(settingMenu);

        root.getChildren().add(menuBar);

        // image
        File file = new File("src/main/java/com/example/myimage.png");
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        root.getChildren().add(imageView);

        // button to open second windows
        Button openSecondWindowBtn = new Button("Open Second Windows");
        openSecondWindowBtn.setOnAction(e -> {
            String textToSend = textField.getText();
            scene2(textToSend);
        });
        root.getChildren().add(openSecondWindowBtn);

        // button to open login page
        Button openLoginPageBtn = new Button("Open Login Page");
        openLoginPageBtn.setOnAction(e -> {
            layoutLoginPage();
        });
        root.getChildren().add(openLoginPageBtn);

        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("JavaFX Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void scene2(String message) {
        Stage secondStage = new Stage();
        VBox root = new VBox();
        root.setStyle("-fx-padding: 10;");

        Label label = new Label("message = " + message);
        root.getChildren().addAll(label);

        Scene scene = new Scene(root, 300, 200);
        secondStage.setScene(scene);
        secondStage.setTitle("Second Window");
        secondStage.show();
    }

    public void layoutLoginPage() {
        Stage loginStage = new Stage();

        VBox root = new VBox();
        root.setStyle("-fx-padding: 10;");

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 10 0;");
        root.getChildren().add(titleLabel);

        VBox formBox = new VBox(10);
        formBox.setPrefWidth(200);

        HBox usernameBox = new HBox();
        Label usernameLabel = new Label("Username");
        usernameLabel.setPrefWidth(80);

        TextField usernameField = new TextField();
        usernameField.setPrefWidth(200);
        usernameField.setPromptText("Username");

        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        HBox passwordBox = new HBox();
        Label passwordLabel = new Label("Password");
        passwordLabel.setPrefWidth(80);

        TextField passwordField = new TextField();
        passwordField.setPrefWidth(200);
        passwordField.setPromptText("Password");

        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        HBox buttonBox = new HBox();
        buttonBox.setStyle("-fx-padding: 0 0 0 80;");
        Button loginButton = new Button("Login");
        buttonBox.getChildren().add(loginButton);

        formBox.getChildren().addAll(usernameBox, passwordBox, buttonBox);
        root.getChildren().add(formBox);

        Scene scene = new Scene(root, 300, 200);
        loginStage.setScene(scene);
        loginStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

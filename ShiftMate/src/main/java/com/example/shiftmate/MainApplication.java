package com.example.shiftmate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/main.fxml"));
        BorderPane root = loader.load();

        MainController mainController = loader.getController();

        // Load initial content and navbar
        mainController.loadPage("views/home.fxml");  // Default page

        Scene scene = new Scene(root, 1920, 1080);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/shiftMateText.png")));

        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Shift Mate");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package com.example.shiftmate.Controllers.Main;

import com.example.shiftmate.MainController;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class NavbarController implements Initializable {
    @FXML
    ImageView btnLogo;
    private MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnLogo.setOnMouseClicked(event -> {
            if (mainController != null) {
                mainController.loadPage("views/home.fxml"); // Load home page
                mainController.removeNavbar(); // Remove navbar after click
            }
        });


    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

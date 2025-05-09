package com.example.shiftmate.Controllers.Main;

import com.example.shiftmate.MainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;
public class HomeController implements Initializable {

    @FXML
    private Button btnStart;

    MainController mainController;
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnStart.setOnAction(e -> {
            if (mainController != null) {
                mainController.loadPage("views/generate_page.fxml");
            } else {
                System.err.println("MainController reference is null!");
            }
        });
        btnStart.setOnMouseEntered(event -> btnStart.setOpacity(0.8));
    }
}

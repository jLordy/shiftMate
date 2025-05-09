package com.example.shiftmate;

import com.example.shiftmate.Controllers.Main.GeneratePageController;
import com.example.shiftmate.Controllers.Main.HomeController;
import com.example.shiftmate.Controllers.Program.ProgramPageController;
import com.example.shiftmate.Controllers.Main.NavbarController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private BorderPane root;  // This is your main layout from main.fxml

    private void loadNavbar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/navbar.fxml"));
            Parent navbar = loader.load();
            root.setTop(navbar);

            // Pass MainController reference to NavbarController
            NavbarController navbarController = loader.getController();
            navbarController.setMainController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void removeNavbar() {
        root.setTop(null);  // Removes the navbar from the top of BorderPane
    }


    public void loadPage(String fxmlFile) {
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/example/shiftmate/" + fxmlFile));
            Parent page = loader.load();
            root.setCenter(page);

            // Get the controller of the loaded FXML
            Object controller = loader.getController();
            if (controller instanceof HomeController) {
                ((HomeController) controller).setMainController(this);
                return;
            }
            else if(controller instanceof GeneratePageController){
                ((GeneratePageController) controller).setMainController(this);

            }else if(controller instanceof ProgramPageController) {
                ((ProgramPageController) controller).setMainController(this);
            }
            loadNavbar();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Add this getter method
    public BorderPane getRoot() {
        return root;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPage("views/home.fxml");
    }


}




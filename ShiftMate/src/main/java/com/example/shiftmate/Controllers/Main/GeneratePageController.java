package com.example.shiftmate.Controllers.Main;

import com.example.shiftmate.Controllers.Employee.*;
import com.example.shiftmate.MainController;
import com.example.shiftmate.Models.EmployeeModel;
import com.example.shiftmate.Controllers.Program.ProgramPageController;

import com.example.shiftmate.Models.ProgramModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GeneratePageController implements Initializable {
    @FXML
    private VBox mainContainer;
    @FXML
    private HBox employeeContainer;
    @FXML
    private Pane btnAddEmployee;
    @FXML
    private ImageView btnGenerate;

    private MainController mainController;
    private int cardCount = 0;

    public List<HBox> hBoxList = new ArrayList<>();
    private List<EmployeeModel> employees = new ArrayList<>();
    private List<ProgramModel> programs = new ArrayList<>();

    public void addEmployeeCard() {
        try {
            // 1. Verify resource exists
            String fxmlPath = "/com/example/shiftmate/views/employees_form.fxml";
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                throw new RuntimeException("FXML file not found at: " + fxmlPath);
            }
            //2. Load the FXMLLoader
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            // 3. Get controller with debug
            EmployeeFormController controller = loader.getController();

            // 4. Setup dialog
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Add New Employee");
            dialog.setResizable(false);
            dialog.setScene(new Scene(root));

            //5. Callback
            controller.setOnAddEmployeeCallback(() -> {
                // The controller now handles validation internally
                EmployeeModel employee = new EmployeeModel(
                        controller.getName(),
                        new ArrayList<>(controller.getSubjects()) // Create a copy
                );
                employees.add(employee);
                fillEmployeeCard(employee);
                dialog.close();
            });

            // 6. Show the form
            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("COMPLETE ERROR TRACE:");
            e.printStackTrace();
        }
    }
    private HBox createNewHBox() {
        HBox newHBox = new HBox();
        newHBox.setSpacing(16);
        newHBox.setAlignment(Pos.CENTER_LEFT);

        if (btnAddEmployee.getParent() != null) {
            System.out.println("→ Removing btnAddEmployee from old parent");
            ((Pane) btnAddEmployee.getParent()).getChildren().remove(btnAddEmployee);
        }

        newHBox.getChildren().add(btnAddEmployee);
        mainContainer.getChildren().add(newHBox);
        hBoxList.add(newHBox);
        System.out.println("→ New HBox created and added. HBox count: " + hBoxList.size());

        return newHBox;
    }
    private void fillEmployeeCard(EmployeeModel employee) {
        try {
            cardCount++;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shiftmate/views/employee_card.fxml"));
            Parent card = loader.load();
            EmployeeCardController cardController = loader.getController();
            cardController.setEmployeeData(employee);
            cardController.setOnRemoveCallback(() -> {
                Platform.runLater(() -> {
                    removeEmployeeCard(card, employee);
                });
            });

            HBox currentHBox = hBoxList.getLast();
            currentHBox.getChildren().add(currentHBox.getChildren().size() - 1, card);

            if (currentHBox.getChildren().size() - 1 == 6) {
                currentHBox.getChildren().remove(btnAddEmployee);
                HBox newHBox = createNewHBox();  // make sure this only adds to list ONCE
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create employee card: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Unexpected Error", "Something went wrong: " + e.getMessage());
        }
    }
    private void removeEmployeeCard(Node card, EmployeeModel employee) {
        for (HBox hBox : hBoxList) {
            int count = hBox.getChildren().contains(btnAddEmployee)
                    ? hBox.getChildren().size() - 1
                    : hBox.getChildren().size();
        }

        cardCount--;
        // 1. Remove from data list
        employees.remove(employee);

        // 2. Remove from UI and find its position
        int hboxIndex = -1;
        int cardIndex = -1;

        for (int i = 0; i < hBoxList.size(); i++) {
            HBox hbox = hBoxList.get(i);
            if (hbox.getChildren().contains(card)) {
                cardIndex = hbox.getChildren().indexOf(card);
                hbox.getChildren().remove(card);
                hboxIndex = i;
                break;
            }
        }

        if (hboxIndex == -1) return; // Card not found

        // 3. Reorganize remaining cards
        reorganizeCardsAfterRemoval();

        // 4. Clean up empty HBoxes (keep at least one)
        cleanUpEmptyHBoxes();
    }
    private void reorganizeCardsAfterRemoval() {
        // Step 1: Gather all cards (excluding the button)
        List<Node> allCards = new ArrayList<>();
        for (HBox hbox : hBoxList) {
            for (Node node : hbox.getChildren()) {
                if (node != btnAddEmployee) {
                    allCards.add(node);
                }
            }
            hbox.getChildren().clear(); // Clear all
        }

        // Step 2: Redistribute cards with max 6 per row
        int index = 0;
        for (HBox hbox : hBoxList) {
            while (hbox.getChildren().size() < 6 && index < allCards.size()) {
                hbox.getChildren().add(allCards.get(index++));
            }
        }

        // Step 3: Remove empty HBoxes except the first
        for (int i = hBoxList.size() - 1; i >= 1; i--) {
            HBox hbox = hBoxList.get(i);
            if (hbox.getChildren().isEmpty()) {
                hBoxList.remove(i);
                mainContainer.getChildren().remove(hbox);
            }
        }

        // Step 4: Place the add button in the first HBox with space
        for (HBox hbox : hBoxList) {
            if (hbox.getChildren().size() < 6) {
                hbox.getChildren().add(btnAddEmployee);
                return;
            }
        }

        // Step 5: If no space, create new HBox
        HBox newHBox = new HBox(16);
        hBoxList.add(newHBox);
        mainContainer.getChildren().add(newHBox);
    }
    private void cleanUpEmptyHBoxes() {
        // Keep at least one HBox
        for (int i = hBoxList.size() - 1; i > 0; i--) {
            HBox hbox = hBoxList.get(i);
            // Check if only contains add button
            if (hbox.getChildren().size() == 1 &&
                    hbox.getChildren().getFirst() == btnAddEmployee) {
                mainContainer.getChildren().remove(hbox);
                hBoxList.remove(i);
            }
        }
        // Ensure add button is in the last HBox
        if (!hBoxList.isEmpty()) {
            HBox lastHBox = hBoxList.getLast();
            if (!lastHBox.getChildren().contains(btnAddEmployee)) {
                lastHBox.getChildren().add(btnAddEmployee);
            }
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnAddEmployee.setOnMouseClicked(e -> addEmployeeCard());

        // Ensure the first HBox has the Add button
        if (!employeeContainer.getChildren().contains(btnAddEmployee)) {
            employeeContainer.getChildren().add(btnAddEmployee);
        }
        hBoxList.add(employeeContainer);

        btnGenerate.setOnMouseClicked(event -> {
            if (!employees.isEmpty()) {
                // Load the page and get its controller
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/shiftmate/views/program_page.fxml"));
                Parent page = null;
                try {
                    page = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ProgramPageController programController = loader.getController();
                programController.setData(employees, programs);
                programController.setMainController(mainController);

                // Show the page
                mainController.getRoot().setCenter(page);
            } else {
                showAlert("Invalid Action", "Add employee first");
            }
        });


    }
    public void setMainController(MainController mainController) {this.mainController = mainController;}
    public void setData(List<EmployeeModel> employees, List<ProgramModel> programs) {
        this.employees = employees;
        this.programs = programs;
        // 2. clear out old UI
        mainContainer.getChildren().clear();
        hBoxList.clear();

        // 3. re-initialize the very first row with the Add button
        employeeContainer.getChildren().clear();
        employeeContainer.getChildren().add(btnAddEmployee);
        mainContainer.getChildren().add(employeeContainer);
        hBoxList.add(employeeContainer);

        // 4. re-draw one card per employee
        for (EmployeeModel emp : employees) {
            fillEmployeeCard(emp);
        }
    }
}